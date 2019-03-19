package MDPatternDetection

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

  val tdb = new TdbOperation()
  var numQueryRun = 0

  val duration = System.currentTimeMillis() - t1

  executeQueriesInFile(Declarations.constructQueriesFile2, "https://dbpedia.org/sparql")

  def executeQueriesInFile(filePath: String, endPoint: String) = {


    val queryExecutor = new QueryExecutor


    val constructQueriesList = Source.fromFile(filePath).getLines
    var nb_req = 0
    var nb_model_notnull = 0
    var nb_model_null = 0

    constructQueriesList.grouped(100000).foreach {
      groupOfLines => {




        val timeFor100000 = System.currentTimeMillis()

        //var nonValidQueries : ParSeq[Query] = ParSeq()


        val treatedGroupOfLines = groupOfLines.par.map {

          line => {
            try {
            nb_req += 1
            println("Requete\t" + nb_req)

            val query = QueryFactory.create(line)

            runQuery(endPoint, queryExecutor, query).map {
              case model => {
                nb_model_notnull += 1
                Right(model)
              }
              case null => {
                nb_model_null += 1
                Left(line)
              }

            }.recover { case e: Exception => Left(line) }
            } catch {
              case ex: Exception => Future.successful(Left(line))
            }

          }


        }.toVector

        println("--------------------- un group finished ---------------------------------- ")

          val seq = Await.result(Future.sequence(treatedGroupOfLines), Duration.Inf)
          val (correct, errors) = seq.partition(_.isRight)


        println("************ nombre model not null avant tdb : " + nb_model_notnull)
        println("************ nombre model  null avant tdb : " + nb_model_null)


            writeInTdb(correct.collect { case Right(x) => x })
            writeInLogFile(Declarations.executionLogFile, errors.collect { case Left(line) => line })

          val finish = System.currentTimeMillis() - timeFor100000
        println("time for 100 000 req is   " + finish)


      }

    }
  }

  def runQuery(endPoint: String, queryExecutor: QueryExecutor, query: Query): Future[Model] = future {
    numQueryRun += 1
    val model = queryExecutor.executeQueryConstruct(query, endPoint)
    println("run query n: " + numQueryRun)
    model
  }
  println(duration)

}
