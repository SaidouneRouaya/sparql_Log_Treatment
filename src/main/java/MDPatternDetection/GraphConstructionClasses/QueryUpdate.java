package MDPatternDetection.GraphConstructionClasses;


import MDfromLogQueries.Util.Constants2;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.syntax.Template;

import java.util.ArrayList;

public class QueryUpdate {

    /**
     * This class update queries with rdf:type triples
     **/

    private QueryConstruction queryConstruction = new QueryConstruction();


    public QueryUpdate() {
    }

    public QueryUpdate(Query query)
    {
        addGP2Query(query);

    }

    public static void main(String[] args) {
        new Constants2(); // init the constants tu use it next
        final String queryString = "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
                "PREFIX dbo: <http://dbpedia.org/ontology/>" +
                "SELECT ?title WHERE {" +
                "     ?game a dbo:Game  ." +
                "?game foaf:friend ?op ." +
                "Filter (?game = \"gg\")" +
                "    OPTIONAL { ?game foaf:name ?title }." +
                "} ORDER by ?title limit 10";



        Query query = QueryFactory.create(queryString);
        System.out.println("== before ==\n" + query);

        //    query = toConstruct(query,new Template(queryConstruction.getBpConstruct()));

        System.out.println("\n\n\n== after ==\n" + query);

    }

    public QueryConstruction getQueryConstruction() {
        return queryConstruction;
    }

    public Query toConstruct(Query query) {
        Template constructTemplate = new Template(queryConstruction.getBpConstruct());
        query.setQueryConstructType();
        query.setConstructTemplate(constructTemplate);
        return query;
    }

    public Query addAddedVariablesToResultVars(Query query) {
        ArrayList<Node> nodeArrayList = queryConstruction.getAddedVariables();
        for (Node n : nodeArrayList) {
            try {
                query.addResultVar(n);
                query.addGroupBy(n);
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("not a variable");
            }
        }
        return query;
    }

    private Query addGP2Query(Query query) {
        QueryModifyElementVisitor qmev = new QueryModifyElementVisitor();
        qmev.walker(query.getQueryPattern(),queryConstruction);
        return query;
    }


}


