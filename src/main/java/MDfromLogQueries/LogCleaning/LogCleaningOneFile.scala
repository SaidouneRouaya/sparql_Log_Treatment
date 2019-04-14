package MDfromLogQueries.LogCleaning

import java.io.{File, FileOutputStream, PrintWriter}
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

import MDfromLogQueries.Declarations.Declarations
import MDfromLogQueries.Util.FileOperation
import org.apache.http.client.utils.URLEncodedUtils

import scala.collection.JavaConverters
import scala.collection.parallel.ParSeq
import scala.io.Source

object LogCleaningOneFile extends App{
  /** This class reads the log files and extract queries **/

  val t1 = System.currentTimeMillis()
  print("je suis dans log cleaning")

  /* Directory that coontains the log files 's Path */
  val directoryPath = Declarations.directoryPath

  /* Result (cleaned queries)'s file path */
  val destinationfilePath = Declarations.cleanedQueriesFileCopie
  val duration = System.currentTimeMillis() - t1
  /* Regex on wich is based the algorithm to extract the queries */
  private val PATTERN = Pattern.compile("[^\"]*\"(?:GET )?/sparql/?\\?([^\"\\s\\n]*)[^\"]*\".*")
  //private val PATTERN = Pattern.compile("(sparql)(.*)")
  /* Statistical variables*/
  var nb_queries = 0
  /** Write the cleaned queries in the destination file path **/
  def writeFiles(filePath: String, destinationfilePath: String) = {
    var queryList = Source.fromFile(filePath).getLines

    queryList.grouped(100000).foreach {
      groupOfLines => {
        var nb_req = 0
        val treatedGroupOfLines = groupOfLines.par.map {
          line => {
            try {
              val extractedQuery = queryFromLogLine(line)
              if (extractedQuery != null) {
                nb_req += 1
                println("* " + nb_req)
                Right(Some(extractedQuery))
              } else Left(line)

            } catch {
              case e : Exception => {
                println("une erreur\n\n\n\n\n\n\n\n\n")
                Left(line)
              }
            }
          }
        }

        println("--------------------- un group finished ---------------------------------- ")
        nb_queries = nb_queries+nb_req
        val (correct, errors) = treatedGroupOfLines.partition(_.isRight)
        writeInFile(destinationfilePath, correct.collect { case Right(Some(x)) => x })
        writeInFile(Declarations.notCleanedQueries, errors.collect { case Left(line) => line })
      }
      }
    println("nombre de requêtes dans le log :"+ nb_queries)
    null
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

  /** match the line passed as parameter with the Regex to extract the query and return the query **/
  def queryFromLogLine(line: String) = {
    val matcher = PATTERN.matcher(line)

    if (matcher.find) {
      val requestStr = matcher.group(1) //celui de dbpedia
      //val requestStr = matcher.group(2) //Celui de british museum
      val queryStr = queryFromRequest(requestStr)
      if (queryStr != null) queryStr
      else requestStr
    }
    else null
  }
  def writeInFile(destinationFilePath: String, queries: ParSeq[String]) = {


    val writer = new PrintWriter(new FileOutputStream(new File(destinationFilePath), true))

    queries.foreach(query => writer.write(query.replaceAll("[\n\r]", "\t") + "\n"))

    writer.close()
  }
  val dir = new File(directoryPath)
  dir.listFiles().toList.foreach(filePath => writeFiles(filePath.toString,destinationfilePath))
  //writeFiles(dirPath, destinationfilePath)

  def queryFromRequest(requestStr: String): String = {
    val pairs = URLEncodedUtils.parse(requestStr, StandardCharsets.UTF_8)
    pairs.forEach {
      pair => {
        if (pair.getName == "query") {
          return pair.getValue
        }
      }
    }

    null

  }
  println(duration)
}
