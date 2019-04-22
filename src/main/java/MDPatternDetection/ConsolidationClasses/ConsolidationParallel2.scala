package MDPatternDetection.ConsolidationClasses

import java.util

import MDfromLogQueries.Util.TdbOperation
import org.apache.jena.query.Dataset
import org.apache.jena.rdf.model._
import org.apache.jena.tdb.TDB

import scala.collection.{JavaConverters, mutable}

object ConsolidationParallel2 extends App {

  val tdbOperation = new TdbOperation()

  val t1 = System.currentTimeMillis()

  //var modelsHashMaps: mutable.HashMap[String, Model] = convertToScalaMap(TdbOperation.unpersistModelsMap(TdbOperation.originalDataSet))


  toStringModelsHashmap2(unpersistModelsMap(TdbOperation.originalDataSet))

  //val modelHashMap2: mutable.HashMap[String, Model] = consolidate(TdbOperation.unpersistModelsMap(TdbOperation.originalDataSetConsolidated))

  //writeInTdb(modelHashMap2, TdbOperation.originalDataSetConsolidated)

  //TODO à déplacer vers une classe pour l'annotation
  /* val modelsConsolidated = TdbOperation.unpersistModelsMap(TdbOperation.originalDataSetConsolidated)
   val modelsAnnotated = MDGraphAnnotated.constructMDGraphs(modelsConsolidated)
   writeInTdb(convertToScalaMap(modelsAnnotated), TdbOperation.dataSetAnnotated)*/
  val duration = System.currentTimeMillis() - t1

  /** *************************************************** Functions ***********************************************************************/

  def consolidate(modelHashMap: util.HashMap[String, Model]): mutable.HashMap[String, Model] = {

    println(" consolidation ")
    if (modelHashMap == null) return null
    var nb = 0

    var modelsHashMap: mutable.HashMap[String, Model] = convertToScalaMap(modelHashMap)

    // var modelsHashMap: mutable.HashMap[String, Model] = toStringModelsHashmap2(modelsHashMaps)
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

  /** Unpersisting **/

  def getModelFromTDB(modelName: String, dataset: Dataset): Model = {
    val model = dataset.getNamedModel(modelName)
    model
  }

  def writeInTdb(models: mutable.HashMap[String, Model], dataset: Dataset) = {

    println(" nombres des models pour persisting " + models.size)

    models.foreach(m => {
      try {
        if (m != null) {
          if (dataset.containsNamedModel(m._1)) {
            val model = dataset.getNamedModel(m._1).union(m._2)
            dataset.replaceNamedModel(m._1, model)
          }
          else
            dataset.addNamedModel(m._1, m._2)
        }
      }
      catch {
        case ex: Exception => ex.printStackTrace()
      }
    })
  }

  def toStringModelsHashmap2(it: Iterator[String]) = {
    val modelHashMap = new mutable.HashMap[String, Model]
    var modelsFromOneModel = new mutable.HashMap[String, Model]
    var nb = 0
    it.grouped(10000).foreach {
      listOfKies => {
        println("je start the list********")
        listOfKies.foreach {
          key => {

            val model = getModelFromTDB(key, TdbOperation.originalDataSet)
            nb += 1
            System.out.println("model num " + nb)
            modelsFromOneModel = getModelsofModel(model)
            import scala.collection.JavaConversions._
            for (key2 <- modelsFromOneModel.keySet) {

              if (modelHashMap.containsKey(key2)) {
                modelHashMap.replace(key2, modelHashMap.get(key2).get.union(modelsFromOneModel.get(key2).get))
              }
              else modelHashMap.put(key2, modelsFromOneModel.get(key2).get)
            }
          }
        }

        println(s" ------------------------- finish with the group ------------------------------- ")
        writeInTdb(modelHashMap, TdbOperation.dataSetConsolidate)
        modelHashMap.clear()
      }
    }

  }

  def getModelsofModel(model: Model): mutable.HashMap[String, Model] = {
    val resourceList = model.listSubjects.toList
    val rdfNodeList = model.listObjects.toList
    val modelHashMap = new mutable.HashMap[String, Model]
    import scala.collection.JavaConversions._
    for (resource <- resourceList) {
      val resourceModel = ModelFactory.createDefaultModel
      if (!rdfNodeList.contains(resource)) {
        var visitedNodes = new mutable.HashSet[RDFNode]()
        visitedNodes.add(resource)
        modelHashMap.put(resource.getURI, getModelOfResource(resource, resourceModel, visitedNodes))
      }
    }
    modelHashMap
  }

  def getModelOfResource(resource: Resource, model: Model, visitedNodes: mutable.HashSet[RDFNode]): Model = {
    val stmtIterator = resource.listProperties
    var internModel = stmtIterator.toModel
    //System.out.println(" le modele louwel "+resource+" "+internModel);
    //System.out.println(" modeeelddd "+model);
    val list = resource.listProperties.toList
    import scala.collection.JavaConversions._
    for (statement <- list) { //System.out.println("je rentre ici");
      //System.out.println(" modeeel "+model);
      val contains = visitedNodes.contains(statement.getObject.asResource)
      //System.out.println("le contains "+contains);
      if (!contains) { //if (model.getResource(rdfNode.toString()))
        visitedNodes.add(statement.getObject)
        internModel.add(getModelOfResource(statement.getObject.asResource, internModel, visitedNodes))
      }
      else internModel = internModel.remove(statement)
      //internModel = internModel.union(getModelOfResource(statement.getObject().asResource(),internModel));
      //model.add();
    }
    internModel
  }


  def unpersistModelsMap(dataset: Dataset): Iterator[String] = {

    val results = new mutable.HashMap[String, Model]

    TDB.sync(dataset)

    JavaConverters.asScalaIterator(dataset.listNames())
  }

  println(s" La durée : $duration")


}
