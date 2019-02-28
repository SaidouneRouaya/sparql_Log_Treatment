package MDPatternDetection;

import MDfromLogQueries.Util.FileOperation;
import com.google.common.base.Stopwatch;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class QueryExecutor {

    /**
     * This class executes the queries
     **/

    public static ArrayList<Model> executeQuiersInFile(String filePath, String endPoint) {
        ArrayList<Model> results = new ArrayList<>();

        try {
            QueryExecutor queryExecutor = new QueryExecutor();
            ArrayList<Query> constructQueriesList = Queries2Graphes.TransformQueriesinFile(filePath);
            // Execution of each CONSTRUCT query
            for (Query query : constructQueriesList) {
                System.out.println("exeution d'une requete");
                Model model;
                if ((model = queryExecutor.executeQueryConstruct(query, endPoint)) != null) results.add(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    public static void executeQuiersInFile2(String filePath, String endPoint) {
        ArrayList<Model> results = new ArrayList<>();

        ArrayList<String> allLines = (ArrayList<String>) FileOperation.ReadFile(filePath);
        int size = allLines.size();
        List<String> lines;
        Stopwatch stopwatch_consolidation = Stopwatch.createUnstarted();
        Stopwatch stopwatch_persist = Stopwatch.createUnstarted();


        try {

            while (size != 0) {

                int cpt;
                if (size >= 10000) {
                    cpt = 10000;
                    size -= 10000;

                } else {
                    cpt = size;
                    size = 0;
                }


                QueryExecutor queryExecutor = new QueryExecutor();

                System.out.println("je suis avant la transformation ");
                ArrayList<Query> constructQueriesList = Queries2Graphes.TransformQueriesinFile2(allLines.subList(0, cpt));
                // Execution of each CONSTRUCT query

                for (Query query : constructQueriesList) {
                    System.out.println("exeution d'une requete ");
                    Model model;
                    if ((model = queryExecutor.executeQueryConstruct(query, endPoint)) != null) results.add(model);
                }

                if (!results.isEmpty()) {
                    stopwatch_consolidation = Stopwatch.createStarted();
                    HashMap<String, Model> modelHashMap = TestConsolidation2.consolidate(results);
                    stopwatch_consolidation.stop();


                    // persisting
                    stopwatch_persist = Stopwatch.createStarted();
                    TestTDB.persistModelsMap(modelHashMap);
                    stopwatch_persist.stop();
                }
                lines = allLines.subList(0, cpt);
                allLines.removeAll(lines);
            }
            System.out.println("je suis apres l'exec ");
            // consolidation


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\nTime elapsed for consolidation program is \t" + stopwatch_consolidation.elapsed(MILLISECONDS));
        System.out.println("\nTime elapsed for persist program is \t" + stopwatch_persist.elapsed(MILLISECONDS));

    }

    public QueryExecutor() {
    }

    public ResultSet executeQuerySelect(String queryStr, String endpoint)
    {
        ResultSet results = null;
        try{
            Query query = QueryFactory.create(queryStr);
            QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
            results = qexec.execSelect();
            /*  System.out.println("Result " + results.next());*/
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return results;
    }


    public Model executeQueryConstruct(Query query, String endpoint)
    {
        Model results = null;
        try{

            QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest(endpoint, query);
            results = qexec.execConstruct();
            /* System.out.println("Result "+ results.toString());*/
        }
        catch (Exception e){
            // e.printStackTrace();
            System.out.println("failed error 400");
        }
        return results;
    }


}
