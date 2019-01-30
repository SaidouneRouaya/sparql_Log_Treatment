package MDfromLogQueries.LogCleaning

import java.io.{File, PrintWriter}
import java.util.regex.Pattern

import MDfromLogQueries.Declarations.Declarations
import org.apache.http.client.utils.URLEncodedUtils

import scala.io.Source

//class LogCleaning{
object Main extends App {


  val t1 = System.currentTimeMillis()

println("je suis dans log cleaner")

  // directory contenant les ficheir a lire
  val dirPath = Declarations.directoryPath
  //fichier ou ecrire
  val filePath = Declarations.cleanedQueriesFile
  //taille buffer pour paralleliser
  val chunkSize = 128 * 1024
  private val PATTERN = Pattern.compile("[^\"]*\"(?:GET )?/sparql/?\\?([^\"\\s\\n]*)[^\"]*\".*")
var nb_queries=0


  def writeFiles(directoryPath : String, filePath : String)  = {
    val dir = new File(directoryPath)
    val logs = dir.listFiles().toList.par.flatMap(x => extractLog(x))

    val writer = new PrintWriter(new File(filePath))
    logs.foreach(x => if(x != null) writer.write(x.replaceAll("[\n\r]","\t")+"\n"))
    writer.close()
  }




  def extractLog(file : File)  = {
    println (file.toString)
    val iterator = Source.fromFile(file).getLines.grouped(chunkSize)
    iterator.flatMap { lines =>
      lines.par.map { line =>
        {
          nb_queries+= 1
          queryFromLogLine(line)
        } }
    }

  }


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

  import java.nio.charset.StandardCharsets

  def queryFromRequest(requestStr: String): String = {
    val pairs = URLEncodedUtils.parse(requestStr, StandardCharsets.UTF_8)
    import scala.collection.JavaConversions._
    for (pair <- pairs) {
      if ("query" == pair.getName) {
        return pair.getValue
      }
    }
    null
  }

  writeFiles(dirPath, filePath)
  println(s"nb queries $nb_queries")
  val duration = (System.currentTimeMillis() - t1)
  println(duration)
}
//}
