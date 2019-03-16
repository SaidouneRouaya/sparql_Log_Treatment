package MDPatternDetection

import java.util

import MDPatternDetection.QueryExecutorParallel.{writeInLogFile, writeInTdb}
import MDfromLogQueries.Declarations.Declarations
import org.apache.jena.query.{Query, QueryFactory}
import org.apache.jena.rdf.model.Model

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, future}
import scala.io.Source

object QueryExecutionParallelFuture extends App {

  val t1 = System.currentTimeMillis()
  val duration = System.currentTimeMillis() - t1

  def executeQueriesInFile(filePath: String, endPoint: String) = {

    var nb = 0
    val queryExecutor = new QueryExecutor


    val constructQueriesList = Source.fromFile(filePath).getLines
    val results = new util.ArrayList[Model]


    constructQueriesList.grouped(100000).foreach {
      groupOfLines => {
        var nb_req = 0
        val timeFor100000 = System.currentTimeMillis()

        //var nonValidQueries : ParSeq[Query] = ParSeq()

        val treatedGroupOfLines = groupOfLines.par.map {

          line => {
            nb_req += 1
            println("Requete\t" + nb_req)

            val query = QueryFactory.create(line)

            runQuery(endPoint, queryExecutor, query).map {
              case model => Right(model)
              case null => Left(line)

            }.recover { case e: Exception => Left(line) }


          }


        }.toVector

        println("--------------------- un group finished ---------------------------------- ")

        val seq = Future.sequence(treatedGroupOfLines)
        seq.foreach(
          listOfModels => {
            val (correct, errors) = listOfModels.partition(_.isRight)

            writeInTdb(correct.collect { case Right(x) => x })
            writeInLogFile(Declarations.executionLogFile, errors.collect { case Left(line) => line })
          }
        )
        val finish = System.currentTimeMillis() - timeFor100000
        println("time for 100 000 req is   " + finish)

      }

    }
  }

  executeQueriesInFile()

  def runQuery(endPoint: String, queryExecutor: QueryExecutor, query: Query): Future[Model] = future {
    val model = queryExecutor.executeQueryConstruct(query, endPoint)
    model
  }

  println(duration)

}
