package Scenarios;

import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.FileOperation;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.expr.ExprAggregator;

import java.util.ArrayList;
import java.util.List;

public class AnalyticQueries {

    public boolean containsAggregator(Query query)
    {
        List<ExprAggregator> exprAggregatorList = new ArrayList<>();
        if (!query.hasAggregators())
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    public static void main(String args[])
    {
       /* ArrayList<String> analyticQueriesList = new ArrayList<>();
        String queryStr;
        Query query = null;
        ArrayList<String> queryList;
        int nb = 0;
        AnalyticQueries analyticQueries = new AnalyticQueries();
        int nb_line = 0; //for statistical needs
        queryList = (ArrayList<String>) FileOperation.ReadFile(Declarations.syntaxValidFile);
        int size = queryList.size();
        //for (String line : queryList){
        while (nb_line< size && nb_line<=10000){
            try {
                nb_line++;
                //queryStr = line;
                queryStr = queryList.get(nb_line);
                try {
                    query = QueryFactory.create(queryStr);
                } catch (Exception e) {
                    // e.printStackTrace();
                }
                if (query.hasAggregators()) {
                    analyticQueriesList.add(query.toString());
                }
                System.out.println( "line \t"+nb_line);
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("erreur");
                nb++;
            }
        }
        /* System.out.println("Size of validQueryList : "+validQueryList.size())
        FileOperation.WriteInFile(Declarations.AnalyticQueriesFile,analyticQueriesList);

        System.out.println("\n\n nombre d'erreur \t" + nb);;*/
       String queryStr = "SELECT DISTINCT ?category ?label (SUM(?o) AS ?numOfInstances)  WHERE { ?category rdf:type skos:Concept . ?o skos:subject ?category . ?category rdfs:label ?label .  ?label bif:contains \"german and football\" .  FILTER (lang(?label) = \"en\") } ORDER BY DESC(?numOfInstances) LIMIT 30";
            Query query = QueryFactory.create(queryStr);
        if (query.hasAggregators()) {
            System.out.println("c'est analytique ");
        }

    }
}
