package MDPatternDetection;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public class QueryExecutor {

    /**
     * This class executes the queries
     **/

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
            e.printStackTrace();
        }
        return results;
    }


}
