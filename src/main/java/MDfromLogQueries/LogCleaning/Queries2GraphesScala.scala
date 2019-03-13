package MDfromLogQueries.LogCleaning

import java.io._

import MDPatternDetection.QueryUpdate
import MDfromLogQueries.Declarations.Declarations
import MDfromLogQueries.Declarations.Declarations.syntaxValidFile
import MDfromLogQueries.Util.Constants2
import org.apache.jena.query.QueryFactory

import scala.io.Source



object Main extends App {

  println("je suis dans la transformation ")
  val t1 = System.currentTimeMillis()
  val duration = System.currentTimeMillis() - t1

  //, queries: util.ArrayList[Query]
  def writeFiles(destinationfilePath: String) = {

    println("je suis dans la fct d'ecriture")
    val writer = new PrintWriter(new FileOutputStream(new File(destinationfilePath), true))


    val queries = TransformQueriesInFile(syntaxValidFile)

    // queries. forEach(x => writer.write(x.toString().replaceAll("[\n\r]", "\t") + "\n"))

    writer.close()
  }

  //: util.ArrayList[Query]
  def TransformQueriesInFile(filePath: String) = {


    new Constants2(Declarations.dbPediaOntologyPath)

    //fichier de lecture ici

    val lines = Source.fromFile(filePath).getLines
    //  val pw = new PrintWriter(new File(Declarations.constructQueriesFile))
    val pw = new PrintWriter(new FileOutputStream(new File(Declarations.constructQueriesFile), true))

    //  println("Taille de lines "+lines.size)

    //lines.foreach {
    lines.grouped(100000).foreach {

      groupOfLines => {

        var nb_req = 0

        println("taille du groupe " + groupOfLines.size)

        val treatedGroupOfLines = groupOfLines.par.map {
          line => {
            nb_req = nb_req + 1
            println("* " + nb_req)

            try {
              val query = QueryFactory.create(line)
              val queryUpdate = new QueryUpdate()
              val constructedQuery = queryUpdate.toConstruct(query)
              Some(constructedQuery)

            } catch {
              case unknown => {
                println("une erreur")
                None
              }
            }

        }

        }

        //fichier d'ecriture ici

        println("--------------------- un group finished ---------------------------------- ")

        treatedGroupOfLines.collect { case Some(x) => x }.foreach(
          line => {

            pw.write(line.toString().replaceAll("[\n\r]", "\t") + "\n")
            pw.write("\n")
          })

      }
    }

    pw.close()
  }


  TransformQueriesInFile(syntaxValidFile)


  println(duration)


}

