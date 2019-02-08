package MDPatternDetection;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;

public class QueryExecutor {
    public ResultSet executeQuerySelect(String queryStr, String endpoint)
    {
        ResultSet results = null;
        try{
            Query query = QueryFactory.create(queryStr);
            QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
            results = qexec.execSelect();
            while (results.hasNext())
                System.out.println("Resultat "+ results.next());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return results;
    }
    public Model executeQueryConstruct(String queryStr, String endpoint)
    {
        Model results = null;
        try{
            Query query = QueryFactory.create(queryStr);
            QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
            results = qexec.execConstruct();
                System.out.println("Resultat "+ results.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return results;
    }
}
