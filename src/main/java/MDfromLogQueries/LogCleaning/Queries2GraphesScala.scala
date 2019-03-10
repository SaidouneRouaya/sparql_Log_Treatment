package MDfromLogQueries.LogCleaning

import java.io.{File, FileOutputStream, PrintWriter}
import java.util

import MDPatternDetection.{QueryConstruction, QueryUpdate}
import MDfromLogQueries.Declarations.Declarations
import MDfromLogQueries.Declarations.Declarations.{constructQueriesFile, syntaxValidFile}
import MDfromLogQueries.Util.{Constants2, FileOperation}
import org.apache.jena.query.{Query, QueryFactory}

import scala.collection.JavaConverters



object Main extends App {

  println("je suis dans la transformation ")
  val t1 = System.currentTimeMillis()
  val duration = System.currentTimeMillis() - t1

  //, queries: util.ArrayList[Query]
  def writeFiles(destinationfilePath: String) = {

    println("je suis dans la fct d'ecriture")
    val writer = new PrintWriter(new FileOutputStream(new File(destinationfilePath), true))


    val queries = TransformQueriesInFile(syntaxValidFile)

    queries.forEach(x => writer.write(x.toString().replaceAll("[\n\r]", "\t") + "\n"))

    writer.close()
  }

  //: util.ArrayList[Query]
  def TransformQueriesInFile(filePath: String) = {
    println("je suis dans la fct de transf")

    new Constants2(Declarations.dbPediaOntologyPath)
    val constructQueriesList = new util.ArrayList[Query]
    val constructQueriesListFinal = new util.ArrayList[Query]

    val lines = JavaConverters.collectionAsScalaIterable(FileOperation.ReadFile(syntaxValidFile))
    var queryUpdate = new QueryUpdate()
    //  val lines = FileOperation.ReadFile(filePath).asInstanceOf[util.ArrayList[String]]


    var nb_line = 0 // for statistical matters
    var query = QueryFactory.create()


    /** Graph pattern extraction **/

    lines.par.map {

      //lines.par.map
      //lines.par.foreach {
      //lines.foreach {
      line => {
        try {

          nb_line += 1
          System.out.println("*  " + nb_line)
          query = QueryFactory.create(line)
          queryUpdate = new QueryUpdate(query)
          query = queryUpdate.toConstruct(query)
        } catch {
          case e: Exception => e.printStackTrace()

          case unknown => println("-----" + unknown)
        }

        constructQueriesList.add(query)
        /*  if (nb_line == 10000) {
            nb_line = 0
            writeFiles(Declarations.constructQueriesFile, constructQueriesList)
            println(" ************** nouveau lot **************")
            // constructQueriesListFinal.addAll(constructQueriesList)

            constructQueriesList.clear()
          }*/
      }
    }

    println(" Le nombre de requetes not Found \t" + QueryConstruction.nb_prop + "\tsur \t" + QueryConstruction.nb_prop_total)

    constructQueriesList

  }

  //TransformQueriesInFile("")
  //TransformQueriesInFile(syntaxValidFile)

  writeFiles(constructQueriesFile)
  println(duration)

}

