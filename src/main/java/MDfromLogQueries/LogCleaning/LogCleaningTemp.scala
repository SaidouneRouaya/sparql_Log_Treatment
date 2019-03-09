package MDfromLogQueries.LogCleaning

import java.io.{File, PrintWriter}
import java.nio.charset.StandardCharsets

import MDfromLogQueries.Declarations.Declarations
import MDfromLogQueries.Declarations.Declarations.syntaxValidFile
import MDfromLogQueries.LogCleaning.TestScala.TransformQueriesInFile
import MDfromLogQueries.Util.FileOperation
import org.apache.http.client.utils.URLEncodedUtils

import scala.collection.JavaConverters

class LogCleaningTemp {


  /** This class reads the log files and extract queries **/

  val t1 = System.currentTimeMillis()


  /* Result (construct queries)'s file path */
  val filePath = Declarations.constructQueriesFile
  val duration = System.currentTimeMillis() - t1
  /* Statistical variables*/
  var nb_queries = 0

  /** Write the cleaned queries in the destination file path **/
  def writeFiles(destinationfilePath: String) = {


    val queries = TransformQueriesInFile(syntaxValidFile)

    val writer = new PrintWriter(new File(destinationfilePath))

    queries.forEach(x => if (x != null) writer.write(x.toString().replaceAll("[\n\r]", "\t") + "\n"))

    writer.close()
  }

  /** Read lines of log file passed as parameter **/

  def extractQueries(file: File) = {

    val iterable = JavaConverters.collectionAsScalaIterable(FileOperation.ReadFile(file.toString))
    iterable.par.map {
      line => {
        nb_queries += 1
        //queryFromLogLine(line)
      }
    }
  }

  /** match the line passed as parameter with the Regex to extract the query and return the query **/

  writeFiles(filePath)

  def queryFromRequest(requestStr: String): String = {
    val pairs = URLEncodedUtils.parse(requestStr, StandardCharsets.UTF_8)

    /*for (pair <- pairs) {
   /*   if ("query" == pair.getName) {
      return pair.getValue
     }*/
    }*/

    null

  }

  println(duration)
}
