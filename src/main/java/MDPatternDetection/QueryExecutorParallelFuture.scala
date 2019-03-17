package MDPatternDetection

import java.util

import MDPatternDetection.QueryExecutorParallel.{writeInLogFile, writeInTdb}
import MDfromLogQueries.Declarations.Declarations
import org.apache.jena.query.{Query, QueryFactory}
import org.apache.jena.rdf.model.Model

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, future}
import scala.io.Source

object QueryExecutionParallelFuture extends App {

  val t1 = System.currentTimeMillis()


  val duration = System.currentTimeMillis() - t1

  executeQueriesInFile(Declarations.constructQueriesFile2, "https://dbpedia.org/sparql")

  def runQuery(endPoint: String, queryExecutor: QueryExecutor, query: Query): Future[Model] = future {
    val model = queryExecutor.executeQueryConstruct(query, endPoint)
    model
  }

  def executeQueriesInFile(filePath: String, endPoint: String) = {

    var nb = 0
    val queryExecutor = new QueryExecutor


    val constructQueriesList = Source.fromFile(filePath).getLines
    val results = new util.ArrayList[Model]
    var nb_req = 0

    constructQueriesList.grouped(10000).foreach {
      groupOfLines => {

        if (nb_req < 1) {


        val timeFor100000 = System.currentTimeMillis()

        //var nonValidQueries : ParSeq[Query] = ParSeq()


        val treatedGroupOfLines = groupOfLines.par.map {

          line => {
            try {
            nb_req += 1
            println("Requete\t" + nb_req)

            val query = QueryFactory.create(line)

            runQuery(endPoint, queryExecutor, query).map {
              case model => Right(model)
              case null => Left(line)

            }.recover { case e: Exception => Left(line) }
            } catch {
              case ex: Exception => Future.successful(Left(line))
            }

          }


        }.toVector

        println("--------------------- un group finished ---------------------------------- ")

          val seq = Await.result(Future.sequence(treatedGroupOfLines), Duration.Inf)
          val (correct, errors) = seq.partition(_.isRight)

            writeInTdb(correct.collect { case Right(x) => x })
            writeInLogFile(Declarations.executionLogFile, errors.collect { case Left(line) => line })

          val finish = System.currentTimeMillis() - timeFor100000
        println("time for 100 000 req is   " + finish)

      }
      }

    }
  }
  println(duration)

}
