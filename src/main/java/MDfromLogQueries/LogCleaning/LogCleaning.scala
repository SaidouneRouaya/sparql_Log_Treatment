package MDfromLogQueries.LogCleaning

import java.io.{File, PrintWriter}
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

import MDfromLogQueries.Declarations.Declarations
import MDfromLogQueries.Util.FileOperation
import org.apache.http.client.utils.URLEncodedUtils

import scala.collection.JavaConverters


object Main extends App {

  /** This class reads the log files and extract queries **/

  val t1 = System.currentTimeMillis()
  print("je suis dans log cleaning")

  /* Directory that coontains the log files 's Path */
  val dirPath = Declarations.directoryPath

  /* Result (cleaned queries)'s file path */
  val filePath = Declarations.cleanedQueriesFileCopie

  /* Regex on wich is based the algorithm to extract the queries */
  private val PATTERN = Pattern.compile("[^\"]*\"(?:GET )?/sparql/?\\?([^\"\\s\\n]*)[^\"]*\".*")

  /* Statistical variables*/
  var nb_queries = 0


  /** Write the cleaned queries in the destination file path **/
  def writeFiles(directoryPath: String, destinationfilePath: String) = {
    val dir = new File(directoryPath)
    val logs = dir.listFiles().toList.par.flatMap(x => extractQueries(x))

    val writer = new PrintWriter(new File(destinationfilePath))
    logs.foreach(x => if(x != null) writer.write(x.replaceAll("[\n\r]","\t")+"\n"))
    writer.close()
  }

  /** Read lines of log file passed as parameter **/

  def extractQueries(file: File) = {

    val iterable = JavaConverters.collectionAsScalaIterable(FileOperation.ReadFile(file.toString))
    iterable.par.map {
      line => {
        nb_queries += 1
        queryFromLogLine(line)
      }
    }
  }


  val duration = System.currentTimeMillis() - t1

  /** match the line passed as parameter with the Regex to extract the query and return the query **/
  def queryFromLogLine(line: String) = {
    val matcher = PATTERN.matcher(line)

    if (matcher.find) {
      val requestStr = matcher.group(1)
      val queryStr = queryFromRequest(requestStr)
      if (queryStr != null) queryStr
      else requestStr
    }
    else null
  }

  writeFiles(dirPath, filePath)

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

