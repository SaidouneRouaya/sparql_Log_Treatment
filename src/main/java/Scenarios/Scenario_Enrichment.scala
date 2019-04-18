package Scenarios

import java.util

import MDPatternDetection.EnrichmentClasses.EnrichParallel.enrichMDScehma
import MDfromLogQueries.Declarations.Declarations.timesFilePath
import MDfromLogQueries.Util.{FileOperation, TdbOperation}
import org.apache.jena.rdf.model.Model

class Scenario_Enrichment {

  val modelsAnnotated: util.HashMap[String, Model] = TdbOperation.unpersistModelsMap(TdbOperation.dataSetAnnotated)
  /** 1. Unpersisting of annotated models **/
  var t_unpersisting: Long = System.currentTimeMillis()
  FileOperation.writeTimesInFile(timesFilePath, "Unpersisting for enrichment ", System.currentTimeMillis() - t_unpersisting)

  /** 2. Enrichment of annotated models **/
  var t_enrichment: Long = System.currentTimeMillis()
  enrichMDScehma(modelsAnnotated)
  FileOperation.writeTimesInFile(timesFilePath, "Enrichment ", System.currentTimeMillis() - t_enrichment)

}
