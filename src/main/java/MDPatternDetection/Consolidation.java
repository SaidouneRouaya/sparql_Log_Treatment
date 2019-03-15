package MDPatternDetection;

import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.FileOperation;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;

import java.util.*;


public class Consolidation {


    /*public static void main(String[] args) {

        String endPoint = "https://dbpedia.org/sparql";
        ArrayList<Model> results = QueryExecutor.executeQuiersInFile(syntaxValidFileTest, endPoint);

        AppTest.afficherModels(results);

        System.out.println("\n\n ------------------ consolidation Results -------------------\n\n");
        HashMap<String, Model> map = consolidate(results);
        afficherListInformations(map);

    }*/
    public static void main(String[] args) {

        String endPoint = "https://dbpedia.org/sparql";
        ArrayList<String> allLines = (ArrayList<String>) FileOperation.ReadFile(Declarations.constructQueriesFile);
        int size = allLines.size();
        int count = 0;
        String queryStr;
        QueryExecutor queryExecutor = new QueryExecutor();
        ArrayList<Model> results = new ArrayList<>();
        while (count <20)
        {
            count++;
            queryStr = allLines.get(count);
            Query query = QueryFactory.create(queryStr);
            Model model;
            System.out.println("req : "+count);
            if ((model = queryExecutor.executeQueryConstruct(query, endPoint)) != null) results.add(model);
            if (!model.isEmpty())
            {
                HashMap<String,Model>  modelHashMap = Consolidation.getModelsofModel(model);
                Set<String> stringSet = modelHashMap.keySet();
                for (String key : stringSet)
                {
                    System.out.println("Le model :");
                    Iterator<Statement> listStatements = modelHashMap.get(key).listStatements();
                    while (listStatements.hasNext()) {
                        System.out.println(listStatements.next().toString());

                    }
                }
                System.out.println("****************************************");
                }

        }/*
        Model model = ModelFactory.createDefaultModel();
        Resource resourceCommune = new ResourceImpl("o1");
        model.add(new ResourceImpl("s1"),new PropertyImpl("p1"), resourceCommune);
        model.add(new ResourceImpl("s1"),new PropertyImpl("p2"),new ResourceImpl("o2"));
        model.add(new ResourceImpl("s1"),new PropertyImpl("p3"),new ResourceImpl("o3"));
        model.add(resourceCommune,new PropertyImpl("p1"),new ResourceImpl("o4"));
        model.add(new ResourceImpl("s2"),new PropertyImpl("p2"),new ResourceImpl("o5"));
        model.add(new ResourceImpl("s2"),new PropertyImpl("p3"),new ResourceImpl("o3"));
        //model.add(new ResourceImpl("o4"),new PropertyImpl("p3"),new ResourceImpl("s1"));

        HashMap<String,Model>  modelHashMap = Consolidation.getModelsofModel(model);
        Set<String> stringSet = modelHashMap.keySet();
        for (String key : stringSet)
        {
            System.out.println("Le model :");
            Iterator<Statement> listStatements = modelHashMap.get(key).listStatements();
            while (listStatements.hasNext()) {
                System.out.println(listStatements.next().toString());

            }
        }*/



    }

    //TODO enlever cella
    public static void afficherListInformations(HashMap<String, Model> listInfoNodes) {

        Iterator it = listInfoNodes.entrySet().iterator();

        System.out.println(" Afichage des résultats \n");


        while (it.hasNext()) {
            Map.Entry<String, Model> pair = (Map.Entry) it.next();

            System.out.println(" Subject: \t\t " + pair.getKey() + "\n");
            Iterator<Statement> listStatements = pair.getValue().listStatements();
            while (listStatements.hasNext()) {
                System.out.println(listStatements.next().toString());

            }

            System.out.println("\n______________________________________________________________________\n");

        }
    }


    public static HashMap<String, Model> consolidate(ArrayList<Model> modelArrayList) {

        if (modelArrayList.size() == 0) {
            System.out.println("\nresults vide\n");
            return null;
        }
        return consolidate(toStringModelHashMap(modelArrayList));
    }

    /** Transforms an Array list of models to a hashmap
     * where the key is a subject and the value is the corresponding model
     */

    public static HashMap<String, Model> toStringModelHashMap(ArrayList<Model> modelArrayList) {
        Statement statement;
        String subject;
        HashMap<String, Model> modelHashMap = new HashMap<>();
        // create a pair <String, Model> where the key (String à is the subject of the statements that compose the model (value)

        // For every model in modelArrayList
        for (Model m : modelArrayList) {

            Iterator<Statement> list = m.listStatements();
            // For every Statement in the model
            while (list.hasNext()) {
                statement = list.next();
                subject = statement.getSubject().toString();

                // if the pair doesn't exist in the map create a new instance
                if (!modelHashMap.containsKey(subject)) {
                    modelHashMap.put(subject, ModelFactory.createDefaultModel());
                    modelHashMap.get(subject).add(statement);
                } else {
                    // add the statement to the corresponding model
                    modelHashMap.get(subject).add(statement);
                }
            }

        }
        return modelHashMap;
    }

    public static HashMap<String,Model> toStringModelsHashmap2(ArrayList<Model> modelArrayList)
    {
        HashMap<String,Model> modelHashMap = new HashMap<>();
        for (Model m : modelArrayList)
        {
            modelHashMap.putAll(getModelsofModel(m));
        }
        return modelHashMap;
    }

    public static HashMap<String,Model> getModelsofModel(Model model)
    {
        List<Resource> resourceList = model.listSubjects().toList();
        List<RDFNode> rdfNodeList = model.listObjects().toList();
        HashMap<String,Model> modelHashMap = new HashMap<>();
        for (Resource resource : resourceList )
        {
            Model resourceModel = ModelFactory.createDefaultModel();
            if (!rdfNodeList.contains(resource))
            {
                modelHashMap.put(resource.getURI(),getModelOfResource(resource,resourceModel));
            }
        }
        return modelHashMap;
    }

    public static Model getModelOfResource(Resource resource, Model model)
    {
        StmtIterator stmtIterator = resource.listProperties();
        Model internModel = stmtIterator.toModel();
        System.out.println(" le modele louwel "+resource+" "+internModel);
        System.out.println(" modeeelddd "+model);
        List<Statement> list =resource.listProperties().toList();
        for(Statement statement : list)
        {
            //System.out.println("je rentre ici");
            //System.out.println(" modeeel "+model);
            if (!model.listSubjects().toList().contains(statement.getObject().asResource()))
            //if (model.getResource(rdfNode.toString()))
                internModel.add(getModelOfResource(statement.getObject().asResource(),internModel));
                //internModel = internModel.union(getModelOfResource(statement.getObject().asResource(),internModel));
            //model.add();
        }
        return internModel;
    }

    /**
     * Consolidates the given model map so that all models (values) are mutually independent
     * (two model are independent if there is no node shared between them)
     */

    public static HashMap<String, Model> consolidate(HashMap<String, Model> modelsHashMap) {

        int sizeOfResults = modelsHashMap.size();
        int newSizeOfResults = 0; // to compare it with the old one and exit the loop
        NodeIterator nodeIterator;

        // loop until there is no connsolidation possible i.e. the size of the map doesn't change
        while (sizeOfResults != newSizeOfResults) {
            Set<String> kies = modelsHashMap.keySet();
            sizeOfResults = newSizeOfResults;

            for (String key : kies) {

                nodeIterator = modelsHashMap.get(key).listObjects();
                // for all nodes in modelsHashMap
                while (nodeIterator.hasNext()) {
                    RDFNode node = nodeIterator.next();

                    // if node already exists as key (subject) in the map, and its model is not empty
                    if (modelsHashMap.containsKey(node.toString()) && !modelsHashMap.get(node.toString()).isEmpty()) {

                        // then consolidate it with the model in question
                        modelsHashMap.get(key).add(modelsHashMap.get(node.toString()));
                        modelsHashMap.put(node.toString(), ModelFactory.createDefaultModel());
                    }
                }
            }

            // clean the map from the empty models
            modelsHashMap = cleanMap(modelsHashMap);
            newSizeOfResults = modelsHashMap.size();
        }
        return modelsHashMap;
    }

    /**
     * Cleans the given map from paris with empty values
     */
    public static HashMap<String, Model> cleanMap(HashMap<String, Model> map) {

        HashMap<String, Model> newResults = new HashMap<>();

        Iterator it = map.entrySet().iterator();
        Map.Entry<String, Model> pair;

        while (it.hasNext()) {

            pair = (Map.Entry) it.next();
            if (!pair.getValue().isEmpty()) {
                newResults.put(pair.getKey(),pair.getValue());
            }
        }
        return newResults;
    }



}

