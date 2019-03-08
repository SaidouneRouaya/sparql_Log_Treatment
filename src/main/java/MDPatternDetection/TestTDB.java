package MDPatternDetection;

import MDfromLogQueries.Declarations.Declarations;
import com.google.common.base.Stopwatch;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.TDBFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static MDfromLogQueries.Declarations.Declarations.tdbDirectory;
import static java.util.concurrent.TimeUnit.MILLISECONDS;



public class TestTDB {
    private static Dataset dataset = TDBFactory.createDataset(tdbDirectory);

    public static void main(String... argv) {

        Stopwatch stopwatch_total = Stopwatch.createStarted();
        Stopwatch stopwatch_exec = Stopwatch.createStarted();

        String endPoint = "https://dbpedia.org/sparql";
        //ArrayList<Model> results = QueryExecutor.executeQuiersInFile(syntaxValidFileTest, endPoint);
        //  ArrayList<Model> results =  QueryExecutorParallel.executeQueriesInFile(Declarations.syntaxValidFile, "https://dbpedia.org/sparql");
        QueryExecutor.executeQuiersInFile2(Declarations.syntaxValidFile, "https://dbpedia.org/sparql");
        stopwatch_exec.stop();
/*
      if (results==null) return;

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

        // TestConsolidation2.afficherListInformations(modelHashMap);

        System.out.println("\nsize after unpersisting  " + modelHashMap.size());

        stopwatch_total.stop();
        System.out.println("\nTime elapsed for execution program is \t" + stopwatch_exec.elapsed(MILLISECONDS));
        System.out.println("\nTime elapsed for unpersist program is \t" + stopwatch_unpersist.elapsed(MILLISECONDS));
        System.out.println("\nTime elapsed for the whole program is \t" + stopwatch_total.elapsed(MILLISECONDS));


        // MDGraphAnnotated. afficher(modelHashMap);
    }


    public static boolean exists(String name) {

        boolean exists = false;
        // if exists a model with subject.toString == name
        if (dataset.containsNamedModel(name)) exists = true;
        else {
            // Verify if it exists as a node inside some model in the tdb
            Iterator<String> it = dataset.listNames();
            String subject;

            while (it.hasNext()) {
                subject = it.next();
                Model model = dataset.getNamedModel(subject);

                if (model.containsResource(ResourceFactory.createResource(name))) exists = true;

            }
        }
        return exists;
    }


    public static void persistModelsMap(HashMap<String, Model> modelHashMap) {

        try {

            //Dataset dataset = DatasetFactory.create(model);

            Iterator it = modelHashMap.entrySet().iterator();

            while (it.hasNext()) {

                Map.Entry<String, Model> pair = (Map.Entry) it.next();

                //Verify if the model exists already in the tdb

                if (exists(pair.getKey())) {
                    dataset.getNamedModel(pair.getKey()).add(pair.getValue());
                } else {
                    dataset.addNamedModel(pair.getKey(), pair.getValue());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


/*
    public Iterator<Triple> find (Node s, Node p, Node o)
    {
        Iterator<Tuple<NodeId>> iter = table.findAsNodeIds(s, p, o) ;
        if ( iter == null )
            return new NullIterator<>() ;
        Iterator<Triple> iter2 = TupleLib.convertToTriples(table.getNodeTable(), iter) ;
        return iter2 ;
    }
  */

    public static HashMap<String, Model> unpersistModelsMap() {
        HashMap<String, Model> results = new HashMap<>();

        //Dataset dataset = TDBFactory.createDataset(tdbDirectory);


        TDB.sync(dataset);


        // Model modell = dataset . getDefaultModel ();

        Iterator<String> it = dataset.listNames();
        String name;
        // it.next();

        try {
            while (it.hasNext()) {
                name = it.next();
                // Model model = dataset.getNamedModel(name);
                Model model = dataset.getNamedModel(name);

                StmtIterator stmtIterator = model.listStatements();

                while (stmtIterator.hasNext()) {
                    System.out.println(stmtIterator.next() + "\n*\n");
                }

                results.put(name, model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

}
