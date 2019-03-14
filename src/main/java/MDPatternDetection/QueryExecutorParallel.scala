package MDPatternDetection

import java.util

import MDfromLogQueries.Declarations.Declarations
import MDfromLogQueries.Util.{Constants, FileOperation}
import org.apache.jena.query.{Query, QueryFactory}
import org.apache.jena.rdf.model.Model

import scala.collection.JavaConverters


//class  QueryExecutorParallel {
object QueryExecutorParallel extends App {

  def executeQueriesInFile(filePath: String, endPoint: String): util.ArrayList[Model] = {
    val t1 = System.currentTimeMillis()
    var nb = 0
    val queryExecutor = new QueryExecutor


    val constructQueriesList = Queries2Graphes.TransformQueriesInFile(filePath)


    val results = new util.ArrayList[Model]
    val iterable = JavaConverters.collectionAsScalaIterable(constructQueriesList)
    println("execution")
    iterable.par.map {
      query => {
        nb = nb + 1
        println("Requete\t" + nb)
        val model = queryExecutor.executeQueryConstruct(query, endPoint)
        if (model != null) results.add(model)
        // results.add(queryExecutor.executeQueryConstruct(query, endPoint))
      }

    }

    val duration = System.currentTimeMillis() - t1
    println(duration)

    return results
  }


  def TransformQueriesInFile(filePath: String): util.ArrayList[Query] = {
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
  }

  //executeQueriesInFile(Declarations.syntaxValidFileTest, "https://dbpedia.org/sparql")


}
