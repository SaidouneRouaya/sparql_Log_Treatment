package MDPatternDetection;

import Statistics.Statistics1;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.TDBFactory;

import javax.xml.crypto.Data;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static MDfromLogQueries.Declarations.Declarations.*;


public class TdbOperation {
    private static Dataset Dirdataset = TDBFactory.createDataset(tdbDirectory);
    public static Dataset originalDataSet = TDBFactory.createDataset(originalTdbDirectory);
    public static Dataset originalDataSetConsolidated = TDBFactory.createDataset(originalTdbDirectoryConsolidated);
    public static Dataset dataSetAnnotated = TDBFactory.createDataset(originalTdbDirectoryAnnotated);
    public static Dataset dataSetAnalytic = TDBFactory.createDataset(tdbAnalytic);

    public static void main(String... argv) {

    }

    public TdbOperation() {
    }

    public static boolean exists(String name, Dataset dt) {

        boolean exists = false;
        // if exists a model with subject.toString == name
        if (dt.containsNamedModel(name)) exists = true;
        else {
            // Verify if it exists as a node inside some model in the tdb
            Iterator<String> it = dt.listNames();
            String subject;

            while (it.hasNext()) {
                subject = it.next();
                Model model = dt.getNamedModel(subject);

                if (model.containsResource(ResourceFactory.createResource(name))) exists = true;

            }
        }
        return exists;
    }


    public static void persistNonAnnotated(HashMap<String, Model> modelHashMap) {
        persistWithVerif(modelHashMap,originalDataSetConsolidated);
    }

    public static void persistAnnotatedHashMap(HashMap<String, Model> modelHashMap) {
        persistWithVerif(modelHashMap,dataSetAnnotated);
    }

    private static void persistWithVerif(HashMap<String,Model> modelHashMap, Dataset dataset)
    {
        try {

            //;
            //Dataset dataset = DatasetFactory.create(model);
            //TDB.sync(dataset);
            int n = 0;
            Iterator it = modelHashMap.entrySet().iterator();

            while (it.hasNext()) {
                n++;
                System.out.println("je persiste "+n);

                Map.Entry<String, Model> pair = (Map.Entry) it.next();

                //Verify if the model exists already in the tdb

                if (exists(pair.getKey(), dataset)) {
                    dataset.getNamedModel(pair.getKey()).add(pair.getValue());
                } else {
                    dataset.addNamedModel(pair.getKey(), pair.getValue());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void persist(ArrayList<Model> models) {


        try {

            for (Model m : models) {

                originalDataSet.addNamedModel(m.listSubjects().next().toString(), m);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getDatasetNames(Dataset tdbDataset)
    {
        TDB.sync(tdbDataset);
        ArrayList<String> namesList = new ArrayList<>();
        // Model modell = dataset . getDefaultModel ();
        Iterator<String> it = tdbDataset.listNames();
        while (it.hasNext())
        {
            namesList.add(it.next());
        }

        return namesList;
    }

    public static HashMap<String, Model> unpersistModelsMap(Dataset dataset) {
        HashMap<String, Model> results = new HashMap<>();
        //Dataset dataset = TDBFactory.createDataset(tdbDirectory);
        TDB.sync(dataset);
        // Model modell = dataset . getDefaultModel ();
        Iterator<String> it = dataset.listNames();
        String name;
        try {
            while (it.hasNext()) {
                name = it.next();
                // Model model = dataset.getNamedModel(name);
                Model model = dataset.getNamedModel(name);
                //ConsolidationTest.afficherModel(model);
                /*StmtIterator stmtIterator = model.listStatements();
                while (stmtIterator.hasNext()) {
                    System.out.println(stmtIterator.next() + "\n*\n");
                }*/
                results.put(name, model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }




}
