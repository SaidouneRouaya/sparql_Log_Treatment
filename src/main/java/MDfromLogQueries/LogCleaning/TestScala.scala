package MDfromLogQueries.LogCleaning

import java.io.{File, PrintWriter}
import java.util

import MDPatternDetection.QueryUpdate
import MDfromLogQueries.Declarations.Declarations
import MDfromLogQueries.Declarations.Declarations.{constructQueriesFile, syntaxValidFile}
import MDfromLogQueries.Util.{Constants, FileOperation}
import org.apache.jena.query.{Query, QueryFactory}


object TestScala extends App {


  println("je suis dans la transformation ")
  val t1 = System.currentTimeMillis()
  val duration = System.currentTimeMillis() - t1

  def writeFiles(destinationfilePath: String) = {

    println("je suis dans la fct d'ecriture")
    val writer = new PrintWriter(new File(destinationfilePath))

    val queries = TransformQueriesInFile(syntaxValidFile)

    queries.forEach(x => writer.write(x.toString().replaceAll("[\n\r]", "\t") + "\n"))

    writer.close()
  }

  writeFiles(constructQueriesFile)

  def TransformQueriesInFile(filePath: String): util.ArrayList[Query] = {
    println("je suis dans la fct de transf")
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

        }
      }

      constructQueriesListFinal
    }
  }

  println(duration)


}

