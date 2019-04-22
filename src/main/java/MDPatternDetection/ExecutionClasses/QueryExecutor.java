package MDPatternDetection.ExecutionClasses;

import MDPatternDetection.AnnotationClasses.MDGraphAnnotated;
import MDPatternDetection.ConsolidationClasses.Consolidation;
import MDPatternDetection.GraphConstructionClasses.Queries2Graphes;
import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.FileOperation;
import com.google.common.base.Stopwatch;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static MDfromLogQueries.Declarations.Declarations.executionLogFile;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class QueryExecutor {

    /**
     * This class executes the queries
     **/

    public static ArrayList<Model> executeQuiersInFile(String filePath, String endPoint) {
        ArrayList<Model> results = new ArrayList<>();

        try {
            QueryExecutor queryExecutor = new QueryExecutor();
            ArrayList<Query> constructQueriesList = Queries2Graphes.TransformQueriesInFile(filePath);
            // Execution of each CONSTRUCT query
            for (Query query : constructQueriesList) {
                System.out.println("exeution req ");
                Model model;
                if ((model = queryExecutor.executeQueryConstruct(query, endPoint)) != null) results.add(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }
    public static void main(String[] args) {
        String endPoint = "https://dbpedia.org/sparql";
        QueryExecutor.executeQuiersInFile2(Declarations.constructQueriesFile, endPoint);
    }
    public static void executeQuiersInFile2(String filePath, String endPoint) {
        ArrayList<Model> results = new ArrayList<>();

        ArrayList<String> allLines = (ArrayList<String>) FileOperation.ReadFile(filePath);
        int size = allLines.size();
        //int size=40;
        List<String> lines;
        Stopwatch stopwatch_consolidation = Stopwatch.createUnstarted();
        Stopwatch stopwatch_persist1 = Stopwatch.createUnstarted();
        Stopwatch stopwatch_persist2 = Stopwatch.createUnstarted();
        Stopwatch stopwatch_annotate = Stopwatch.createUnstarted();


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

                int num = 0;
                Query query;
                System.out.println("\nL'execution des requetes \n");

                for (String queryStr : allLines) {
                    num++;
                    query=QueryFactory.create(queryStr);
                    System.out.println("exeution req " + num + "\n");
                    Model model;
                    if ((model = queryExecutor.executeQueryConstruct(query, endPoint)) != null) results.add(model);
                    if (model != null) {
                        System.out.println("Le model :");
                        Iterator<Statement> listStatements = model.listStatements();
                        while (listStatements.hasNext()) {
                            System.out.println(listStatements.next().toString());

                        }
                    System.out.println("Le sujet : "+model.listObjects().next().toString());}

                    if (num ==10)
                        return;
                }

                System.out.println("\nLa consolidation \n");
                if (!results.isEmpty()) {
                    stopwatch_consolidation = Stopwatch.createStarted();
                    HashMap<String, Model> modelHashMap = Consolidation.consolidate(results);
                    stopwatch_consolidation.stop();

                    // persist before annotate
                    System.out.println("\n le persisting 1  \n");
                    stopwatch_persist1 = Stopwatch.createStarted();
                    // TdbOperation.persistNonAnnotated(modelHashMap);
                    stopwatch_persist1.stop();

                    // annotation

                    System.out.println("\n L'annotation \n");
                    stopwatch_annotate = Stopwatch.createStarted();
                    MDGraphAnnotated.constructMDGraphs(modelHashMap);
                    stopwatch_annotate.stop();

                    // persisting
                    System.out.println("\n le persisting 2 \n");
                    stopwatch_persist2 = Stopwatch.createStarted();
                    // TdbOperation.persistAnnotatedHashMap(modelHashMap);
                    stopwatch_persist2.stop();
                }
                lines = allLines.subList(0, cpt);
                allLines.removeAll(lines);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\nTime elapsed for consolidation program is \t" + stopwatch_consolidation.elapsed(MILLISECONDS));
        System.out.println("\nTime elapsed for annotation program is \t" + stopwatch_annotate.elapsed(MILLISECONDS));
        System.out.println("\nTime elapsed for persist program is \t" + stopwatch_persist1.elapsed(MILLISECONDS));
        System.out.println("\nTime elapsed for persist program is \t" + stopwatch_persist2.elapsed(MILLISECONDS));

    }

    public boolean executeQueryAsk(String queryStr, String endpoint) {
        boolean results = false;

        Query query = null;
        try {
            query = QueryFactory.create(queryStr);
            QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
            results = qexec.execAsk();
            /*  System.out.println("Result " + results.next());*/
        } catch (Exception e) {
            e.printStackTrace();
            FileOperation.writeQueryInLog(executionLogFile, "Ask", query);
        }
        return results;
    }

    public QueryExecutor() {
    }

    public ResultSet executeQuerySelect(Query query, String endpoint)
    {
        ResultSet results = null;
        try{
            //  query = QueryFactory.create(queryStr);
            QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
            results = qexec.execSelect();
            /*  System.out.println("Result " + results.next());*/
        }
        catch (Exception e){
            System.out.println("the query " + query + "\n********************");
            e.printStackTrace();
            FileOperation.writeQueryInLog(executionLogFile, "Select", query);
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
            FileOperation.writeQueryInLog(executionLogFile, "Construct", query);
        }
        return results;
    }


}
