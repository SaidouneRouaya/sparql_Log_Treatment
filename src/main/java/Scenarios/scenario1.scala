package Scenarios

import MDPatternDetection.ConsolidationParallel.{consolidate, convertToScalaMap, modelsAnnotated, writeInTdb}
import MDPatternDetection.QueryExecutorParallelFuture.executeQueriesInFile
import MDPatternDetection.TdbOperation
import MDfromLogQueries.Declarations.Declarations
import MDfromLogQueries.Declarations.Declarations._
import MDfromLogQueries.LogCleaning.Main.writeFiles
import MDfromLogQueries.SPARQLSyntacticalValidation.SyntacticValidationParallel.valideQueriesInFile
import MDfromLogQueries.Util.FileOperation
import Statistics.MDGraphBySubjectScala.{statisticsBySubjectList, subjects}

class scenario1 {

  /** 1. Nettoyage du log **/
  var t_cleaning: Long = System.currentTimeMillis()
  writeFiles(directoryPath, cleanedQueriesFile)
  FileOperation.writeTimesInFile(timesFilePath, "Log Cleaning", System.currentTimeMillis() - t_cleaning)

  /** 2. Validaion syntaxique **/
  var t_syntacticValidation: Long = System.currentTimeMillis()
  valideQueriesInFile(writingDedupFilePath)
  FileOperation.writeTimesInFile(timesFilePath, "Syntactical Validation ", System.currentTimeMillis() - t_syntacticValidation)

  /** 3. Execution **/
  var t_execution: Long = System.currentTimeMillis()
  executeQueriesInFile(Declarations.constructQueriesFile2, "https://dbpedia.org/sparql")
  FileOperation.writeTimesInFile(timesFilePath, "Execution ", System.currentTimeMillis() - t_execution)

  /** 4. Consolidation **/
  var t_consolidation: Long = System.currentTimeMillis()
  writeInTdb(consolidate(TdbOperation.unpersistModelsMap(TdbOperation.originalDataSet)), TdbOperation.dataSetConsolidate)
  FileOperation.writeTimesInFile(timesFilePath, "Consolidation ", System.currentTimeMillis() - t_consolidation)

  /** 5. Annotation **/
  var t_annotation: Long = System.currentTimeMillis()
  writeInTdb(convertToScalaMap(modelsAnnotated), TdbOperation.dataSetAnnotated)
  FileOperation.writeTimesInFile(timesFilePath, "Annotation ", System.currentTimeMillis() - t_annotation)

  /** 6. Statistique **/
  var t_statistics: Long = System.currentTimeMillis()
  statisticsBySubjectList(subjects)
  FileOperation.writeTimesInFile(timesFilePath, "Statistics ", System.currentTimeMillis() - t_statistics)


}
