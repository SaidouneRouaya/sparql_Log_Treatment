package MDfromLogQueries.Util;

import MDfromLogQueries.Declarations.Declarations;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.TDBFactory;

import java.util.*;

import static MDfromLogQueries.Declarations.Declarations.originalTdbDirectory;
import static MDfromLogQueries.Declarations.Declarations.tdbDirectory;


public class TdbOperation {
    private static Dataset dataset = TDBFactory.createDataset(tdbDirectory);
    public static Dataset originalDataSet = TDBFactory.createDataset(originalTdbDirectory);
    public static Dataset _toString = TDBFactory.createDataset(Declarations._toString);
    public static Dataset dataSetConsolidate = TDBFactory.createDataset(Declarations.dataSetConsolidated);
    public static Dataset dataSetAnnotated = TDBFactory.createDataset(Declarations.dataSetAnnotated);
    public static Dataset dataSetAnalytic = TDBFactory.createDataset(Declarations.dataSetAnalytic);



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


    public static void persistNonAnnotated(HashMap<String, Model> modelHashMap, Dataset originalDataSetStringModel) {
        try {
            //Dataset dataset = DatasetFactory.create(model);

            Iterator it = modelHashMap.entrySet().iterator();
            int nb = 0;
            while (it.hasNext()) {
                nb++;
                System.out.println(" next model " + nb);
                Map.Entry<String, Model> pair = (Map.Entry) it.next();

                if (exists(pair.getKey(), originalDataSetStringModel)) {
                    originalDataSetStringModel.getNamedModel(pair.getKey()).add(pair.getValue());

                } else {
                    originalDataSetStringModel.addNamedModel(pair.getKey(), pair.getValue());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void persistNonNamedModels(Collection<Model> modelsCollection, Dataset originalDataSetStringModel) {
        try {
            //Dataset dataset = DatasetFactory.create(model);
            Iterator it = modelsCollection.iterator();
            int nb = 0;
            for (Model model : modelsCollection) {
                nb++;
                System.out.println(" next model " + nb);
                originalDataSetStringModel.addNamedModel("model " + nb, model);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void persistAnnotatedHashMap(HashMap<String, Model> modelHashMap, Dataset dataset) {


        try {


            Iterator it = modelHashMap.entrySet().iterator();
            int nb = 0;
            while (it.hasNext()) {
                nb++;

                Map.Entry<String, Model> pair = (Map.Entry) it.next();
                System.out.println("persist next model " + nb);

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


    public static HashMap<String, Model> unpersistModelsMap(Dataset dataset) {
        HashMap<String, Model> results = new HashMap<>();

        //Dataset dataset = TDBFactory.createDataset(tdbDirectory);


        TDB.sync(dataset);
        if (dataset == null) return null;

        Iterator<String> it = dataset.listNames();

        String name;

        try {

            while (it.hasNext()) {
                name = it.next();

                while (name == null) {
                    name = it.next();
                }

                Model model = dataset.getNamedModel(name);

                if (name != null && model != null) results.put(name, model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("taille de la liste  " + results.size());
        return results;
    }


    public static ArrayList<String> unpersistListOfNames(Dataset dataset) {

        // dataset=originalDataSet;
        TDB.sync(dataset);

        if (dataset == null) return null;

        ArrayList<String> results = new ArrayList<>();

        Iterator<String> it = dataset.listNames();

        try {
            while (it.hasNext()) {
                String name = it.next();
                while (name == null) {
                    name = it.next();
                }

                results.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("taille de la liste  " + results.size());
        return results;
    }

}
