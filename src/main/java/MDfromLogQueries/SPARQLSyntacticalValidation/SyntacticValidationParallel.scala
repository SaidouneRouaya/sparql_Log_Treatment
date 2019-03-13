package MDfromLogQueries.SPARQLSyntacticalValidation

import java.io.{File, FileOutputStream, PrintWriter}

import MDfromLogQueries.Declarations.Declarations
import MDfromLogQueries.Declarations.Declarations._
import MDfromLogQueries.Util.Constants2

import scala.collection.parallel.ParSeq
import scala.io.Source

object SyntacticValidationParallel extends App {


  val t1 = System.currentTimeMillis()




  //: util.ArrayList[Query]
  def valideQueriesInFile(filePath: String) = {
    new Constants2(dbPediaOntologyPath)
    var queryList = Source.fromFile(filePath).getLines

    queryList.grouped(100000).foreach {
      groupOfLines => {
        var nb_req = 0
        val treatedGroupOfLines = groupOfLines.par.map {
          line => {
            nb_req = nb_req + 1
            //println("* " + nb_req)
            var verifiedQuery = ""
            try {
              verifiedQuery = Validate(line)
              if (verifiedQuery != null) println("not null")

              /* Some meaning if there is a result != null */
              Some(verifiedQuery)
            } catch {
              case unknown => {
                println("une erreur\n\n\n\n\n\n\n\n\n")
                writeInLogFile(Declarations.syntaxNonValidFile, verifiedQuery)
                None
              }
            }
          }
        }
        println("--------------------- un group finished ---------------------------------- ")
        writeInFile(syntaxValidFile2, treatedGroupOfLines.collect { case Some(x) => x })
      }
    }
  }

  /** Function that writes into destinationFilePath the list passed as parameter **/
  def writeInFile(destinationFilePath: String, queries: ParSeq[String]) = {


    val writer = new PrintWriter(new FileOutputStream(new File(destinationFilePath), true))

    queries.foreach(query => writer.write(query.replaceAll("[\n\r]", "\t") + "\n"))

    writer.close()
  }

  def writeInLogFile(destinationFilePath: String, query: String) = {

    val writer = new PrintWriter(new FileOutputStream(new File(destinationFilePath), true))

    writer.write(query.replaceAll("[\n\r]", "\t") + "\n")

    writer.close()
  }



  private def Validate(queryStr: String) = {
    val queryStr2 = QueryFixer.get.fix(queryStr)
    /*System.out.println(queryStr2);*/

    QueryFixer.toQuery(queryStr2).toString
  }

  valideQueriesInFile(writingDedupFilePath)
  val duration = System.currentTimeMillis() - t1
  println(duration)


}
