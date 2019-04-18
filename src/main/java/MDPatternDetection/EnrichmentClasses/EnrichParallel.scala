package MDPatternDetection.EnrichmentClasses

import java.util

import MDPatternDetection.ConsolidationClasses.ConsolidationParallel.convertToScalaMap
import MDfromLogQueries.Declarations.Declarations
import MDfromLogQueries.Util.FileOperation.writeStatisticsListInFile2
import MDfromLogQueries.Util.{Constants2, ConstantsUtil, TdbOperation}
import org.apache.jena.rdf.model.Model

import scala.collection.mutable

object EnrichParallel extends App {

  val modelsAnnotated: util.HashMap[String, Model] = TdbOperation.unpersistModelsMap(TdbOperation.dataSetAnnotated)
  var endpoint = "https://dbpedia.org/sparql"
  var constantsUtil = new ConstantsUtil
  var constants2 = new Constants2(Declarations.dbPediaOntologyPath)

  enrichMDScehma(modelsAnnotated)

  def enrichMDScehma(models: util.HashMap[String, Model]): Unit = {


    val modelsAnnotatedScala: mutable.HashMap[String, Model] = convertToScalaMap(modelsAnnotated)
    val itModels = modelsAnnotatedScala.values
    var numModel = 0

    itModels.grouped(20000).foreach {
      groupOfmodels => {
        val time = System.currentTimeMillis()
        groupOfmodels.par.foreach {
          model => {
            numModel += 1
            println(s"le model num: $numModel")
            Enrich.enrichModel(model)

          }
        }
        writeStatisticsListInFile2(Enrich.statisticsAnalytics4Fact, Declarations.statisticsAnalyticFactFile)
        writeStatisticsListInFile2(Enrich.statisticsAnalytics4Dimension, Declarations.statisticsAnalyticDimFile)

      }

    }

  }


}
