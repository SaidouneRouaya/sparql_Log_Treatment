package MDPatternDetection;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static MDfromLogQueries.Declarations.Declarations.syntaxValidFileTest;
import static MDfromLogQueries.Declarations.Declarations.tdbDirectory;

public class TestTDB {


    public static void main(String... argv) {

        String endPoint = "https://dbpedia.org/sparql";
        ArrayList<Model> results = QueryExecutor.executeQuiersInFile(syntaxValidFileTest, endPoint);
        //  App.afficherModels(results);
        HashMap<String, Model> modelHashMap = TestConsolidation2.consolidate(results);

        TestConsolidation2.afficherListInformations(modelHashMap);
        System.out.println("Persisting");
        persistModelsMap(modelHashMap);
        System.out.println("unpersisting");
        HashMap<String, Model> modelHashMap2 = unpersistModelsMap();
        TestConsolidation2.afficherListInformations(modelHashMap);

        System.out.println("\nEquality ? : " + modelHashMap.equals(modelHashMap2));
        System.out.println("\n sizes: " + modelHashMap.size() + ",\t" + modelHashMap2.size());


    }


    public static void persistModelsMap(HashMap<String, Model> modelHashMap) {

        Dataset dataset = TDBFactory.createDataset(tdbDirectory);

        try {

            /*File file = new File(tdbDirectory);

            if (!file.isFile()) file.createNewFile();
            */

            Iterator it = modelHashMap.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, Model> pair = (Map.Entry) it.next();
                dataset.addNamedModel(pair.getKey(), pair.getValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataset.close();
        }
    }

    public static HashMap<String, Model> unpersistModelsMap() {
        HashMap<String, Model> results = new HashMap<>();

        Dataset dataset = TDBFactory.createDataset(tdbDirectory);
        Iterator<String> it = dataset.listNames();
        String name;
        while (it.hasNext()) {
            name = it.next();

            Model model = dataset.getNamedModel(name);

            results.put(name, model);
        }

        return results;
    }

}
