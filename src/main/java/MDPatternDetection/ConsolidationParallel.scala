package MDPatternDetection

import java.util

import org.apache.jena.query.Dataset
import org.apache.jena.rdf.model.{Model, ModelFactory, NodeIterator, RDFNode}
import org.apache.jena.tdb.TDB

import scala.collection.mutable

object ConsolidationParallel extends App {

  val tdbOperation = new TdbOperation()

  val t1 = System.currentTimeMillis()


  val modelHashMap: mutable.HashMap[String, Model] = consolidate(TdbOperation.unpersistModelsMap(TdbOperation.originalDataSet))

  writeInTdb(modelHashMap, TdbOperation.dataSetConsolidate)

  //TODO à déplacer vers une classe pour l'annotation
  val modelsConsolidated = TdbOperation.unpersistModelsMap(TdbOperation.dataSetConsolidate)
  val modelsAnnotated = MDGraphAnnotated.constructMDGraphs(modelsConsolidated)
  writeInTdb(convertToScalaMap(modelsAnnotated), TdbOperation.dataSetAnnotated)

  val duration = System.currentTimeMillis() - t1

  /** *************************************************** Functions ***********************************************************************/

  def consolidate(modelHashMap: util.HashMap[String, Model]): mutable.HashMap[String, Model] = {

    println(" consolidation ")
    if (modelHashMap == null) return null
    var nb = 0

    var modelsHashMap: mutable.HashMap[String, Model] = convertToScalaMap(modelHashMap)
    //var modelsHashMap = modelHashMap
    //if (modelsHashMap==null) return null
    var sizeOfResults: Int = modelsHashMap.size
    var sizeOfNewResults: Int = 0 // to compare it with the old one and exit the loop

    var nodeIterator: NodeIterator = null

    // loop until there is no consolidation possible i.e. the size of the map doesn't change
    while (sizeOfResults != sizeOfNewResults) {
      nb += 1
      println(s"la consolidation numero : $nb")
      val kies = modelsHashMap.keys
      sizeOfResults = sizeOfNewResults
      var nb_model = 0
      kies.foreach {
        key => {
          nb_model += 1
          println(s" model n°  $nb_model ")
          nodeIterator = modelsHashMap(key).listObjects

          // for all nodes in modelsHashMap
          while (nodeIterator.hasNext) {

            println(" je suis dans le while ")
            val node: RDFNode = nodeIterator.next
            // if node already exists as key (subject) in the map, and its model is not empty

            if (modelsHashMap.contains(node.toString) && !modelsHashMap(node.toString).isEmpty) {

              // then consolidate it with the model in question
              modelsHashMap(key).add(modelsHashMap(node.toString))
              modelsHashMap.put(node.toString, ModelFactory.createDefaultModel)
            }
          }
        }
      }

      // clean the map from the empty models
      modelsHashMap = cleanMap(modelsHashMap)
      sizeOfNewResults = modelsHashMap.size
    }
    println(" taille de la hashmap apres consolidation : " + modelsHashMap.size)
    modelsHashMap
  }

  def convertToScalaMap(modelHashMap: util.HashMap[String, Model]): mutable.HashMap[String, Model] = {

    val kies = modelHashMap.keySet()
    val result: mutable.HashMap[String, Model] = new mutable.HashMap[String, Model]()

    kies.forEach(
      key => {
        try {
          if (key != null && modelHashMap.get(key) != null) {
            result.put(key, modelHashMap.get(key))
          }
        }
        catch {
          case e: Exception =>
        }
      }

    )
    result
  }

  def cleanMap(map: mutable.HashMap[String, Model]): mutable.HashMap[String, Model] = {

    val newResults = new mutable.HashMap[String, Model]


    map.foreach {
      pair => {
        if (!pair._2.isEmpty) {
          newResults.put(pair._1, pair._2)
        }
      }

    }
    newResults
  }

  def toStringModelHashMap(it: Iterator[String]): Unit = {
    val iterator = it
    var num = 0
    var nb_grp = 0
    val modelHashMap: mutable.HashMap[String, Model] = mutable.HashMap()

    iterator.grouped(50000).foreach {

      listOfModelNames =>
        listOfModelNames.foreach {
          nb_grp += 1
          modelName => {
            val model = getModelFromTDB(modelName, TdbOperation.originalDataSet)

            num += 1
            System.out.println(" model num : " + num)

            val list = model.listStatements

            // For every Statement in the model
            while (list.hasNext) {
              val statement = list.next
              val subject = statement.getSubject.toString
              // if the pair doesn't exist in the map create a new instance
              if (!modelHashMap.contains(subject)) {
                modelHashMap.put(subject, ModelFactory.createDefaultModel)
                modelHashMap(subject).add(statement)
              }
              else { // add the statement to the corresponding model
                modelHashMap(subject).add(statement)
              }
            }

          }
        }

        writeInTdb(modelHashMap, TdbOperation.dataSetConsolidate)
        modelHashMap.clear()
        println(s" ------------------------- finish with the group number: $nb_grp -------------------------------- ")
    }

  }

  def writeInTdb(models: mutable.HashMap[String, Model], dataset: Dataset) = {

    println(" nombres des models pour persisting " + models.size)

    models.foreach(m => {

      if (m != null) {
        dataset.addNamedModel(m._1, m._2)
      }
    })
  }

  /** Unpersisting **/

  def getModelFromTDB(modelName: String, dataset: Dataset): Model = {
    val model = dataset.getNamedModel(modelName)
    model
  }

  def unpersistModelsMap(dataset: Dataset): mutable.HashMap[String, Model] = {

    val results = new mutable.HashMap[String, Model]

    //Dataset dataset = TDBFactory.createDataset(tdbDirectory);
    TDB.sync(dataset)

    if (dataset == null) return null

    System.out.println("taille de la liste  " + results.size)
    results
  }

  println(s" La durée : $duration")


}
