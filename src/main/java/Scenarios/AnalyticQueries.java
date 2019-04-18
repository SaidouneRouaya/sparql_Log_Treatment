package Scenarios;

import MDPatternDetection.ExecutionClasses.QueryExecutor;
import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.SPARQLSyntacticalValidation.QueryFixer;
import MDfromLogQueries.Util.FileOperation;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
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


    public static boolean isAnalytic(Query query)
    {
        List<ExprAggregator> exprAggregatorList;
        int i = 0;
        if (query.hasAggregators())
        {
            exprAggregatorList = query.getAggregators();
            for (ExprAggregator exprAggregator : exprAggregatorList)
            {
                System.out.println("la var :"+exprAggregator.getAggregator().getExprList());
                // the exprList is null in case it's a count(*)
                if (exprAggregator.getAggregator().getExprList() != null)
                {
                    i++;
                }
            }
        }
        return (i>0);
    }



    public static void main(String args[])
    {
        /*ArrayList<String> analyticQueriesList = new ArrayList<>();
        String queryStr;
        Query query = null;
        ArrayList<String> queryList;
        int nb = 0;
        AnalyticQueries analyticQueries = new AnalyticQueries();
        int nb_line = 0; //for statistical needs
        queryList = (ArrayList<String>) FileOperation.ReadFile(Declarations.syntaxValidFile2);
        int size = queryList.size();
        //for (String line : queryList){
        while (nb_line< size ){
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
        //System.out.println("Size of validQueryList : "+validQueryList.size());
        FileOperation.WriteInFile(Declarations.AnalyticQueriesFile,analyticQueriesList);*/

        /*ArrayList<String> analyticQueriesList = new ArrayList<>();
        String queryStr;
        Query query;
        ArrayList<String> queryList;
        int nb = 0;
        int nb_line = 0; //for statistical needs
        queryList = (ArrayList<String>) FileOperation.ReadFile(Declarations.AnalyticQueriesFile2);
        int size = queryList.size();
        //for (String line : queryList){
        while (nb_line< size ){
            try {
                nb_line++;
                //queryStr = line;
                queryStr = queryList.get(nb_line);
                try {
                    query = QueryFactory.create(queryStr);

                    QueryUpdate queryUpdate = new QueryUpdate(query);
                    query = queryUpdate.toConstruct(query);
                    analyticQueriesList.add(query.toString());
                } catch (Exception e) {
                    // e.printStackTrace();
                }
                System.out.println( "line \t"+nb_line);
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("erreur");
                nb++;
            }
        }
        //System.out.println("Size of validQueryList : "+validQueryList.size());
        FileOperation.WriteInFile(Declarations.AnalyticConstructQueriesFile,analyticQueriesList);

*/
        ArrayList<ResultSet> results = new ArrayList<>();

        try {
            Query query;
            QueryExecutor queryExecutor = new QueryExecutor();
            ArrayList<String> constructQueriesList = (ArrayList<String>) FileOperation.ReadFile(Declarations.AnalyticQueriesFile2);
            // Execution of each CONSTRUCT query
            for (String queryStr : constructQueriesList) {
                query = QueryFactory.create(queryStr);
                System.out.println("exeution req ");
                ResultSet resultSet;
                if ((resultSet = queryExecutor.executeQuerySelect(query, "https://dbpedia.org/sparql")) != null) results.add(resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        /*System.out.println("\n\n nombre d'erreur \t" + nb);
        String queryStr =/*"PREFIX dbpprop: <http://dbpedia.org/property/> "
                + " PREFIX dbpedia: <http://dbpedia.org/resource/> "
                + "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
                + "select ?similar (count(?p) as ?similarity) "
                + "where { values ?movie { <http://dbpedia.org/resource/Lagaan> }"
                + " ?similar ?p ?o ; a dbpedia-owl:Film . "
                + "?movie   ?p ?o .} group by "
                + "?similar ?movie having(count(?p) > 35) order by desc(?similarity)";
                "PREFIX  skos: <http://www.w3.org/2004/02/skos/core#> " +
                        "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                        "SELECT DISTINCT COUNT(?category) as ?ca ?jj  " +
                        "WHERE { " +
                        "?category rdf:type skos:Concept . " +
                        "} LIMIT 10 ";
        String queryStr2 = QueryFixer.get().fix(queryStr);
        System.out.println(queryStr2);
       /*String queryStr = "PREFIX  skos: <http://www.w3.org/2004/02/skos/core#>\tPREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\tPREFIX  bif:  <http://dbpedia.org/property/>" +
               "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
               "SELECT DISTINCT ?category ?label (SUM(?o) AS ?numOfInstances)  WHERE { ?category rdf:type skos:Concept . ?o skos:subject ?category . ?category rdfs:label ?label .  ?label bif:contains \"german and football\" .  FILTER (lang(?label) = \"en\") } ORDER BY DESC(?numOfInstances) LIMIT 30";
           Query query = QueryFactory.create(queryStr2, Syntax.syntaxARQ);
        if (query.hasAggregators()) {
            System.out.println("c'est analytique ");
        }*/



    }
}
