package MDPatternDetection

import java.io.{File, PrintWriter}
import java.util

import MDfromLogQueries.Declarations.Declarations
import MDfromLogQueries.Declarations.Declarations.syntaxValidFile
import MDfromLogQueries.Util.{Constants, FileOperation}
import org.apache.jena.query.{Query, QueryFactory}

object Queries2GraphesParallel extends App {


  val t1 = System.currentTimeMillis()
  val duration = System.currentTimeMillis() - t1

  def writeFiles(destinationfilePath: String) = {

    val writer = new PrintWriter(new File(destinationfilePath))

    val queries = TransformQueriesInFile(destinationfilePath)

    queries.forEach(x => writer.write(x.toString().replaceAll("[\n\r]", "\t") + "\n"))

    writer.close()
  }

  writeFiles(syntaxValidFile)

  def TransformQueriesInFile(filePath: String): util.ArrayList[Query] = {
    new Constants(Declarations.dbPediaOntologyPath)
    val constructQueriesList = new util.ArrayList[Query]
    val constructQueriesListFinal = new util.ArrayList[Query]

    val lines = FileOperation.ReadFile(filePath).asInstanceOf[util.ArrayList[String]]

    var nb_line = 0 // for statistical matters

    try {

      /** Graph pattern extraction **/

      import scala.collection.JavaConversions._


      lines.par.map {
        line => {
          nb_line += 1
          System.out.println("*  " + nb_line)
          var query = QueryFactory.create(line)
          val queryUpdate = new QueryUpdate(query)
          query = queryUpdate.toConstruct(query)
          constructQueriesList.add(query)


          /* if (nb_line == 10000) {
              constructQueriesListFinal.addAll(constructQueriesList)
              FileOperation.WriteConstructQueriesInFile(constructQueriesFile, constructQueriesList)
              nb_line = 0
              constructQueriesList.clear()
            }*/
        }
      }

      constructQueriesListFinal
    }
  }

  println(duration)
}
