package MDPatternDetection

import java.util

import Statistics.Statistics1
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.impl.ResourceImpl

import scala.collection.mutable

object MDGraphBySubjectScala extends App {


  val t1 = System.currentTimeMillis()

  val results: util.HashMap[String, Model] = TdbOperation.unpersistModelsMap(TdbOperation.dataSetAnnotated)
  val resultsScala = ConsolidationParallel.convertToScalaMap(results)


  val statistics: Statistics1 = new Statistics1
  val duration = System.currentTimeMillis() - t1
  var models = new mutable.HashMap[String, Model]
  var subjects = Vector[String]()

  /** Book **/
  println(" ---------------- book ")
  subjects = Vector("http://dbpedia.org/ontology/Book", "http://schema.org/Book", "http://purl.org/ontology/bibo/Book")
  models.clear()
  models = getModelsOfSubjectList(subjects, resultsScala)
  stat = statistics.stat2(convertToJavaMap(models))
  MDGraphBySubject.writeAllStats(stat, "book")

  /** university **/
  println(" ---------------- book ")
  models.clear()
  models = getModelsOfSubject("http://dbpedia.org/ontology/University", resultsScala)
  stat = statistics.stat2(convertToJavaMap(models))
  MDGraphBySubject.writeAllStats(stat, "university")

  /** Publication **/

  /** Media **/
  println(" ---------------- book ")
  models.clear()
  models = getModelsOfSubject("http://dbpedia.org/ontology/Media", resultsScala)
  stat = statistics.stat2(convertToJavaMap(models))
  MDGraphBySubject.writeAllStats(stat, "media")

  /** Software **/
  println(" ---------------- software ")
  models.clear()
  models = getModelsOfSubject("http://dbpedia.org/ontology/Software", resultsScala)
  if (models.nonEmpty) {
    stat = statistics.stat2(convertToJavaMap(models))
    MDGraphBySubject.writeAllStats(stat, "software")

  }

  /** Album **/
  println(" ---------------- album ")
  subjects = Vector("http://dbpedia.org/ontology/Album", "http://schema.org/MusicAlbum", "http://wikidata.dbpedia.org/resource/Q482994")
  models.clear()
  models = getModelsOfSubjectList(subjects, resultsScala)
  if (models.nonEmpty) {
    stat = statistics.stat2(convertToJavaMap(models))
    MDGraphBySubject.writeAllStats(stat, "album")
  }

  /** Movie **/
  println(" ---------------- movie ")
  models.clear()
  models = getModelsOfSubject("http://dbpedia.org/ontology/movie", resultsScala)
  if (models.nonEmpty) {
    stat = statistics.stat2(convertToJavaMap(models))
    MDGraphBySubject.writeAllStats(stat, "movie")
  }
  /** Game **/
  println(" ---------------- game ")
  models.clear()
  models = getModelsOfSubject("http://dbpedia.org/ontology/Game", resultsScala)
  if (models.nonEmpty) {
    stat = statistics.stat2(convertToJavaMap(models))
    MDGraphBySubject.writeAllStats(stat, "game")
  }
  /** Hotel **/
  println(" ---------------- hotel ")
  subjects = Vector("http://dbpedia.org/ontology/Hotel", "http://schema.org/Hotel")
  models.clear()
  models = getModelsOfSubjectList(subjects, resultsScala)
  if (models.nonEmpty) {
    stat = statistics.stat2(convertToJavaMap(models))
    MDGraphBySubject.writeAllStats(stat, "hotel")
  }
  /** Airport **/
  println(" ---------------- airport ")
  subjects = Vector("http://dbpedia.org/ontology/Airport", "http://schema.org/Airport")
  models.clear()
  models = getModelsOfSubjectList(subjects, resultsScala)
  if (models.nonEmpty) {
    stat = statistics.stat2(convertToJavaMap(models))
    MDGraphBySubject.writeAllStats(stat, "airport")
  }
  var stat = new util.ArrayList[Statistics1]()

  def getModelsOfSubjectList(subjectList: Vector[String], models: mutable.HashMap[String, Model]): mutable.HashMap[String, Model] = {

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

  def convertToJavaMap(modelHashMap: mutable.HashMap[String, Model]): util.HashMap[String, Model] = {

    val result: util.HashMap[String, Model] = new util.HashMap[String, Model]()

    modelHashMap.par.foreach {
      pair => {
        result.put(pair._1, pair._2)
      }
    }
    result
  }

  println(duration)


}
