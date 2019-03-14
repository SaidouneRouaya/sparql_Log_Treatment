package MDPatternDetection

import java.io.{File, FileOutputStream, PrintWriter}
import java.util

import MDfromLogQueries.Declarations.Declarations
import MDfromLogQueries.Declarations.Declarations.syntaxValidFile2
import MDfromLogQueries.SPARQLSyntacticalValidation.SyntacticValidationParallel.{writeInFile, writeInLogFile}
import MDfromLogQueries.Util.{Constants, FileOperation}
import org.apache.jena.query.{Query, QueryFactory}
import org.apache.jena.rdf.model.Model

import scala.collection.parallel.ParSeq
import scala.io.Source


//class  QueryExecutorParallel {
object QueryExecutorParallel extends App {

  def executeQueriesInFile(filePath: String, endPoint: String): util.ArrayList[Model] = {
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
            nb_req+=1
            println("Requete\t" + nb)
            val query =QueryFactory.create(line)
            try {
              val model = queryExecutor.executeQueryConstruct(query, endPoint)
              if (model != null) {
                Some(model)
                //results.add(model)
              }
            }
            catch {
              case  exp : Exception =>
                {
                  println("une erreur\n\n\n\n\n\n\n\n\n")
                  //nonValidQueries.+:(constructedQuery)
                  writeInLogFile(ExecutionLogFile, line)
                  None
                }
            }
          }
        }

        println("--------------------- un group finished ---------------------------------- ")

        writeInFile(constructQueriesFile, treatedGroupOfLines.collect { case Some(x) => x })
      }
    }

    val duration = System.currentTimeMillis() - t1
    println(duration)

    return results
  }

  def writeInLogFile(destinationFilePath: String, query: Query) = {

    val writer = new PrintWriter(new FileOutputStream(new File(destinationFilePath), true))

    writer.write(query.toString().replaceAll("[\n\r]", "\t") + "\n")

    writer.close()
  }

  def writeInTdb(destinationFilePath: String, queries: ParSeq[Query]) = {


    val writer = new PrintWriter(new FileOutputStream(new File(destinationFilePath), true))

    queries.foreach(query => writer.write(query.toString().replaceAll("[\n\r]", "\t") + "\n"))

    writer.close()
  }
  /*def TransformQueriesInFile(filePath: String): util.ArrayList[Query] = {
    new Constants(Declarations.dbPediaOntologyPath)
    val constructQueriesList = new util.ArrayList[Query]

    var lines = FileOperation.ReadFile(filePath).asInstanceOf[util.ArrayList[String]]


    var nb_line = 0 // for statistical matters

    try {

      /** Graph pattern extraction **/

      import scala.collection.JavaConversions._

      for (line <- lines) {
        try { // String line = lines.get(nb_line);
          nb_line += 1
          var query = QueryFactory.create(line)
          val queryUpdate = new QueryUpdate(query)
          query = queryUpdate.toConstruct(query)
          constructQueriesList.add(query)
          // System.out.println("*  "+nb_line);
        } catch {
          case e: Exception =>

          //e.printStackTrace();
          //Todo do something (++ nb for statistics)
        }
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
    constructQueriesList
  }*/

  executeQueriesInFile(Declarations.constructQueriesFile, "https://dbpedia.org/sparql")


}
