package MDPatternDetection

import java.io.{File, FileOutputStream, PrintWriter}
import java.util

import MDfromLogQueries.Declarations.Declarations
import org.apache.jena.query.QueryFactory
import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.tdb.TDB

import scala.collection.parallel.ParSeq
import scala.io.Source


object QueryExecutorParallel extends App {

  var nb_model = 0
  val t1 = System.currentTimeMillis()
  val duration = System.currentTimeMillis() - t1

  def executeQueriesInFile(filePath: String, endPoint: String) = {
    val t1 = System.currentTimeMillis()
    var nb = 0
    val queryExecutor = new QueryExecutor


    val constructQueriesList = Source.fromFile(filePath).getLines
    val results = new util.ArrayList[Model]
    constructQueriesList.grouped(100000).foreach {
      groupOfLines => {
        var nb_req = 0
        //var nonValidQueries : ParSeq[Query] = ParSeq()
        val treatedGroupOfLines = groupOfLines.par.map {
          line => {
            nb_req += 1
            println("Requete\t" + nb_req)
            var model = ModelFactory.createDefaultModel()
            try {
              val query = QueryFactory.create(line)
              model = queryExecutor.executeQueryConstruct(query, endPoint)
              if (model != null) {
                Right(Some(model))
              } else Right(None)

            }
            catch {
              case exp: Exception => {
                println("une erreur\n\n\n\n\n\n\n\n\n")
                //nonValidQueries.+:(constructedQuery)
                Left(line)
              }
            }
          }
        }

        println("--------------------- un group finished ---------------------------------- ")
        val (correct, errors) = treatedGroupOfLines.partition(_.isRight)
        writeInTdb(correct.collect { case Right(Some(x)) => x })

        writeInLogFile(Declarations.executionLogFile, errors.collect { case Left(line) => line })

      }

    }
  }

  def writeInLogFile(destinationFilePath: String, queries: ParSeq[String]) = {
    val writer = new PrintWriter(new FileOutputStream(new File(destinationFilePath), true))

    queries.foreach(query => writer.write(query.replaceAll("[\n\r]", "\t") + "\n"))

    writer.close()
  }


  def writeInLogFile(destinationFilePath: String, queries: Vector[String]) = {

    val writer = new PrintWriter(new FileOutputStream(new File(destinationFilePath), true))

    queries.foreach(query => writer.write(query.replaceAll("[\n\r]", "\t") + "\n"))

    writer.close()
  }

  executeQueriesInFile(Declarations.constructQueriesFile2, "https://dbpedia.org/sparql")

  def writeInTdb(models: ParSeq[Model]) = {
    val tdb = new TdbOperation()
    var nb_model = 0



    // je suis un commentaire
    models.foreach(m => {
      nb_model += 1
      TdbOperation.originalDataSet.addNamedModel("model_" + nb_model, m)
    })


  }

  def writeInTdb(models: Vector[Model]) = {

    models.foreach(m => {

      if (m != null) {
        nb_model += 1
        println("write "+nb_model)
        TdbOperation
          .originalDataSet
          .addNamedModel("model_" + nb_model,
            m)
      }
    })
    TDB.sync(TdbOperation.originalDataSet)

  }

  println(duration)
}

