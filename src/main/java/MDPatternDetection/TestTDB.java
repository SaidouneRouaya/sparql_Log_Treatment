package MDPatternDetection;

import MDfromLogQueries.Declarations.Declarations;
import com.google.common.base.Stopwatch;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.TDBFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static MDfromLogQueries.Declarations.Declarations.tdbDirectory;
import static java.util.concurrent.TimeUnit.MILLISECONDS;



public class TestTDB {


    public static void main(String... argv) {

        Stopwatch stopwatch_total = Stopwatch.createStarted();
        Stopwatch stopwatch_exec = Stopwatch.createStarted();

        String endPoint = "https://dbpedia.org/sparql";
        //ArrayList<Model> results = QueryExecutor.executeQuiersInFile(syntaxValidFileTest, endPoint);
        // ArrayList<Model> results =  QueryExecutorParallel.executeQueriesInFile(Declarations.syntaxValidFile, "https://dbpedia.org/sparql");
       //QueryExecutor.executeQuiersInFile2(Declarations.syntaxValidFile, "https://dbpedia.org/sparql");
        stopwatch_exec.stop();

  /*      if (results==null) return;

        //  App.afficherModels(results);

        Stopwatch stopwatch_consolidation = Stopwatch.createStarted();
        HashMap<String, Model> modelHashMap = TestConsolidation2.consolidate(results);
        stopwatch_consolidation.stop();

       // TestConsolidation2.afficherListInformations(modelHashMap);

        Stopwatch stopwatch_persist = Stopwatch.createStarted();
        persistModelsMap(modelHashMap);
        stopwatch_persist.stop();
*/
        HashMap<String, Model> modelHashMap;

        Stopwatch stopwatch_unpersist = Stopwatch.createStarted();
        modelHashMap = unpersistModelsMap();
        stopwatch_unpersist.stop();

        //TestConsolidation2.afficherListInformations(modelHashMap);
        //HashMap<String,Model> newMap = TestConsolidation2.consolidate(modelHashMap);
try {
    TestConsolidation2.afficherListInformations(modelHashMap);
}
catch (Exception e)
{

}
          HashMap<String,Model> annotatedHashMap = new HashMap<>();
        MDGraphAnnotated mdGraphAnnotated;

        Iterator it = modelHashMap.entrySet().iterator();
        int cpt = 0;

        while (it.hasNext() && cpt<100)
        {
            try {
                Map.Entry<String, Model> pair = (Map.Entry) it.next();
                mdGraphAnnotated = new MDGraphAnnotated(pair.getValue(), pair.getKey());
                annotatedHashMap.put(pair.getKey(), mdGraphAnnotated.getMdModel());
                System.out.println(cpt);
                cpt++;
            }
            catch (Exception e)
            {

            }
        }

        TestConsolidation2.afficherListInformations(annotatedHashMap);

        System.out.println("\nsize after unpersisting  " + modelHashMap.size());

        stopwatch_total.stop();
        System.out.println("\nTime elapsed for execution program is \t" + stopwatch_exec.elapsed(MILLISECONDS));
        System.out.println("\nTime elapsed for unpersist program is \t" + stopwatch_unpersist.elapsed(MILLISECONDS));
        System.out.println("\nTime elapsed for the whole program is \t" + stopwatch_total.elapsed(MILLISECONDS));

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
       // TDB.sync(dataset);
        Iterator<String> it = dataset.listNames();
        String name;
        it.next();it.next();it.next();
        while (it.hasNext()) {
            name = it.next();
           // System.out.println("la clé "+ name);

            Model model = dataset.getNamedModel(name);
           // System.out.println(model);

            results.put(name, model);
        }

        return results;
    }

}
