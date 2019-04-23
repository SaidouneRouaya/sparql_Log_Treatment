package Scenarios

import java.util

import MDPatternDetection.AnnotationClasses.MDGraphAnnotated
import MDPatternDetection.ConsolidationClasses.ConsolidationParallel
import MDPatternDetection.ConsolidationClasses.ConsolidationParallel._
import MDPatternDetection.ExecutionClasses.QueryExecutor
import MDPatternDetection.ExecutionClasses.QueryExecutor.executeQuiersInFile
import MDfromLogQueries.Declarations.Declarations._
import MDfromLogQueries.LogCleaning.LogCleaning.writeFiles
import MDfromLogQueries.LogCleaning.QueriesDeduplicator.DeduplicateQueriesInFile
import MDfromLogQueries.LogCleaning.{LogCleaning, QueriesDeduplicator}
import MDfromLogQueries.SPARQLSyntacticalValidation.SyntacticValidationParallel
import MDfromLogQueries.SPARQLSyntacticalValidation.SyntacticValidationParallel.valideQueriesInFile
import MDfromLogQueries.Util.{FileOperation, TdbOperation}
import Statistics.MDGraphBySubjectScala.{statisticsBySubjectList, subjects}
import org.apache.jena.rdf.model.Model


object Scenario_LogOnly extends App {

  /** 1. Nettoyage du log **/
  var t_cleaning: Long = System.currentTimeMillis()
  writeFiles(directoryPath, cleanedQueriesFile)
  FileOperation.writeTimesInFile(timesFilePath, "Log Cleaning", System.currentTimeMillis() - t_cleaning)
  FileOperation.writeQueriesNumberInFile(queriesNumberFilePath, "Log Cleaning", LogCleaning.queriesNumber)

  val endpoint = "https://dbpedia.org/sparql"
  DeduplicateQueriesInFile(cleanedQueriesFile)
  FileOperation.writeTimesInFile(timesFilePath, "Deduplication ", System.currentTimeMillis() - t_dedup)
  FileOperation.writeQueriesNumberInFile(queriesNumberFilePath, "Deduplication ", QueriesDeduplicator.queriesNumber)
  val modelsConsolidated: util.HashMap[String, Model] = TdbOperation.unpersistModelsMap(TdbOperation.dataSetConsolidate)
  valideQueriesInFile(writingDedupFilePath)
  FileOperation.writeTimesInFile(timesFilePath, "Syntactical Validation ", System.currentTimeMillis() - t_syntacticValidation)
  FileOperation.writeQueriesNumberInFile(queriesNumberFilePath, "Syntactical Validation ", SyntacticValidationParallel.queriesNumber)
  val modelsAnnotated: util.HashMap[String, Model] = MDGraphAnnotated.constructMDGraphs(modelsConsolidated)
  /** 2. Deduplication **/
  var t_dedup: Long = System.currentTimeMillis()
  executeQuiersInFile(constructQueriesFile2, endpoint)
  FileOperation.writeTimesInFile(timesFilePath, "Execution ", System.currentTimeMillis() - t_execution)
  FileOperation.writeQueriesNumberInFile(queriesNumberFilePath, "Execution : nb queries executed ", QueryExecutor.queriesNumber)
  FileOperation.writeQueriesNumberInFile(queriesNumberFilePath, "Execution : nb queries non executed ", QueryExecutor.queriesLogNumber)
  /** 3. Validaion syntaxique **/
  var t_syntacticValidation: Long = System.currentTimeMillis()
  writeInTdb(consolidate(TdbOperation.unpersistModelsMap(TdbOperation.originalDataSet)), TdbOperation.dataSetConsolidate)
  FileOperation.writeTimesInFile(timesFilePath, "Consolidation ", System.currentTimeMillis() - t_consolidation)
  FileOperation.writeQueriesNumberInFile(queriesNumberFilePath, "Consolidation : nb of models  ", ConsolidationParallel.modelsNumber)
  /** 4. Execution **/
  var t_execution: Long = System.currentTimeMillis()
  /** 5. Consolidation **/
  var t_consolidation: Long = System.currentTimeMillis()
  /** 6. Annotation **/
  var t_annotation: Long = System.currentTimeMillis()
  writeInTdb(convertToScalaMap(modelsAnnotated), TdbOperation.dataSetAnnotated)
  FileOperation.writeTimesInFile(timesFilePath, "Annotation ", System.currentTimeMillis() - t_annotation)
  /** 7. Statistique **/
  var t_statistics: Long = System.currentTimeMillis()
  statisticsBySubjectList(subjects)
  FileOperation.writeTimesInFile(timesFilePath, "Statistics ", System.currentTimeMillis() - t_statistics)
  //TODO ecrire dans un fichier les stat concernant nombre de req ..Etc
}
