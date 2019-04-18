package Statistics

import java.util

import MDPatternDetection.ConsolidationClasses.ConsolidationParallel
import MDfromLogQueries.Util.TdbOperation
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.impl.ResourceImpl

import scala.collection.mutable

object MDGraphBySubjectScala extends App {


  val t1 = System.currentTimeMillis()

  val results: util.HashMap[String, Model] = TdbOperation.unpersistModelsMap(TdbOperation.dataSetAnnotated)
  val resultsScala = ConsolidationParallel.convertToScalaMap(results)
  val duration = System.currentTimeMillis() - t1
  var subjects = Vector(
    /** Book **/
    "http://dbpedia.org/ontology/Book", "http://schema.org/Book", "http://purl.org/ontology/bibo/Book",

    /** University **/
    "http://dbpedia.org/ontology/University",

    /** Media **/
    "http://dbpedia.org/ontology/Media",

    /** Software **/
    "http://dbpedia.org/ontology/Software",

    /** Album **/
    "http://dbpedia.org/ontology/Album", "http://schema.org/MusicAlbum", "http://wikidata.dbpedia.org/resource/Q482994",

    /** Movie **/
    "http://dbpedia.org/ontology/movie",

    /** Game **/
    "http://dbpedia.org/ontology/Game",

    /** Hotel **/
    "http://dbpedia.org/ontology/Hotel", "http://schema.org/Hotel",

    /** Airport **/
    "http://dbpedia.org/ontology/Airport", "http://schema.org/Airport"
  )

  def statisticsBySubjectList(subjectsList: Vector[String]) {

    val statistics: Statistics1 = new Statistics1
    var stat = new util.ArrayList[Statistics1]()
    var models = new mutable.HashMap[String, Model]
    subjectsList.foreach(
      subjects => {
        models.clear()
        models = getModelsOfSubject(subjects, resultsScala)
        if (models.nonEmpty) {
          stat = statistics.stat2(convertToJavaMap(models))
          MDGraphBySubject.writeAllStats(stat, subjects.toString)
        }

      }
    )
  }

  def convertToJavaMap(modelHashMap: mutable.HashMap[String, Model]): util.HashMap[String, Model] = {

    val result: util.HashMap[String, Model] = new util.HashMap[String, Model]()

    modelHashMap.par.foreach {
      pair => {
        result.put(pair._1, pair._2)
      }
    }
    result
  }

  def getModelsOfSubject(subjectList: Vector[String], models: mutable.HashMap[String, Model]): mutable.HashMap[String, Model] = {

    val resultingModels = new mutable.HashMap[String, Model]
    var hashmap = new mutable.HashMap[String, Model]
    println(" im in get models of subject list ")

    subjectList.par.map {
      subject => {
        hashmap = {
          getModelsOfSubject(subject, models)
        }

      }
        resultingModels ++= hashmap
    }

    resultingModels
  }


  statisticsBySubjectList(subjects)

  def getModelsOfSubject(subject: String, models: mutable.HashMap[String, Model]): mutable.HashMap[String, Model] = {
    println(" im in get models of subject ")
    val resultingModels = new mutable.HashMap[String, Model]
    val keys = models.keySet
    val subjectNode = new ResourceImpl(subject)

    models.foreach {
      pair => {
        if (pair._2.containsResource(subjectNode)) resultingModels.put(pair._1, pair._2)
      }
    }

    resultingModels
  }

  println(duration)


}
