package MDPatternDetection

import java.io.{File, FileOutputStream, PrintWriter}
import java.util

import MDfromLogQueries.Declarations.Declarations
import com.google.common.base.Stopwatch
import org.apache.jena.query.{Query, QueryFactory}
import org.apache.jena.rdf.model.Model

import scala.collection.parallel.ParSeq
import scala.io.Source


object QueryExecutorParallel extends App {


  val t1 = System.currentTimeMillis()

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
            println("Requete\t" + nb_req)

            try {
              val query =QueryFactory.create(line)
              val model = queryExecutor.executeQueryConstruct(query, endPoint)
              if (model != null) {
                Some(model)
                results.add(model)
              }
            }
            catch {
              case  exp : Exception =>
                {
                  println("une erreur\n\n\n\n\n\n\n\n\n")
                  //nonValidQueries.+:(constructedQuery)
                  writeInLogFile(Declarations.ExecutionLogFile, line)
                  None
                }
            }
          }
        }

        println("--------------------- un group finished ---------------------------------- ")
        var stopwatch_consolidation = Stopwatch.createUnstarted
        var stopwatch_persist1 = Stopwatch.createUnstarted
        var stopwatch_persist2 = Stopwatch.createUnstarted
        var stopwatch_annotate = Stopwatch.createUnstarted
        System.out.println("\nLa consolidation \n")
        if (!results.isEmpty) {
          stopwatch_consolidation = Stopwatch.createStarted
          val modelHashMap = Consolidation.consolidate(results)
          stopwatch_consolidation.stop
          // persist before annotate
          System.out.println("\n le persisting 1  \n")
          stopwatch_persist1 = Stopwatch.createStarted
          TdbOperation.persistNonAnnotated(modelHashMap)
          stopwatch_persist1.stop
        }
        //results.addAll(treatedGroupOfLines.collect { case Some(x) => x }).toList)
      }
    }

    val duration = System.currentTimeMillis() - t1
    println(duration)

    return results
  }

  def writeInLogFile(destinationFilePath: String, query: String) = {

    val writer = new PrintWriter(new FileOutputStream(new File(destinationFilePath), true))

    writer.write(query.replaceAll("[\n\r]", "\t") + "\n")

    writer.close()
  }

  def writeInTdb(destinationFilePath: String, queries: ParSeq[Model]) = {


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
  val duration = System.currentTimeMillis() - t1
  println(duration)
}
