package Scenarios

import java.util

import MDPatternDetection.EnrichmentClasses.EnrichParallel.enrichMDScehma
import MDfromLogQueries.Declarations.Declarations._
import MDfromLogQueries.Util.{FileOperation, TdbOperation}
import org.apache.jena.rdf.model.Model

object Scenario_Enrichment extends App {



  /** 1. Unpersisting of annotated models **/
  var t_unpersisting: Long = System.currentTimeMillis()
  val modelsAnnotated: util.HashMap[String, Model] = TdbOperation.unpersistModelsMap(TdbOperation.dataSetAnnotated)
  FileOperation.writeTimesInFile(timesFilePath, "Unpersisting for enrichment ", System.currentTimeMillis() - t_unpersisting)
  FileOperation.writeQueriesNumberInFile(queriesNumberFilePath, "Enrichment: unpersisting ", modelsAnnotated.size())


  /** 2. Enrichment of annotated models **/
  var t_enrichment: Long = System.currentTimeMillis()
  enrichMDScehma(modelsAnnotated)
  FileOperation.writeTimesInFile(timesFilePath, "Enrichment ", System.currentTimeMillis() - t_enrichment)


}
