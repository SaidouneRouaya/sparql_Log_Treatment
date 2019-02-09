package MDPatternDetection;


import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.Constants;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.syntax.Template;

public class QueryUpdate {
    private static QueryConstruction queryConstruction = new QueryConstruction();
    private Query theQuery;
    public QueryUpdate(Query query)
    {
        //theQuery = query;
        addGP2Query(query);
        theQuery = toConstruct(query,new Template(queryConstruction.getBpConstruct()));
    }

    public Query getTheQuery() {
        return theQuery;
    }

    public static void main(String[] args) {
        new Constants(Declarations.dbPediaOntologyPath); // init the constants tu use it next
        final String queryString = "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
                "PREFIX dbo: <http://dbpedia.org/ontology/>" +
                "SELECT ?title WHERE {" +
                "     ?game a dbo:Game  ." +
                "?game foaf:friend ?op ." +
                "Filter (?game = \"gg\")" +
                "    OPTIONAL { ?game foaf:name ?title }." +
                "} ORDER by ?title limit 10";


        final String queryString3 = "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
                "PREFIX dc: <http://purl.org/dc/elements/1.1/>" +
                "PREFIX : <http://dbpedia.org/resource/>" +
                "PREFIX dbo: <http://dbpedia.org/ontology/>" +
                "PREFIX dbpedia2: <http://dbpedia.org/property/>" +
                "PREFIX dbpedia: <http://dbpedia.org/>" +
                "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" +
                "SELECT ?title WHERE {" +
                "     ?game a dbo:Game  ." +
                " OPTIONAL { ?game foaf:name ?title } " +
                "} ORDER by ?title limit 10";


        final String queryString2 = "" +
                "SELECT * WHERE {\n" +
                " ?a ?b ?c1 ;\n" +
                "    ?b ?c2 .\n" +
                " ?d ?e ?f .\n" +
                " ?g ?h ?i .\n" +
                "OPTIONAL { ?p ?q ?r .\n" +
                "  ?d ?e2 ?f2 . }\n" +
                "}";


         Query query = QueryFactory.create(queryString);
        System.out.println("== before ==\n" + query);

        BasicPattern bp = new BasicPattern();
        Triple triple = new Triple(new Node_Variable("sub"), new Node_Variable("pred"), new Node_Variable("obj"));
        Triple triple2 = new Triple(new Node_Variable("sub2"), new Node_Variable("pred2"), new Node_Variable("obj2"));
        Triple triple3 = new Triple(new Node_Variable("sub3"), new Node_Variable("pred3"), new Node_Variable("obj3"));

        bp.add(triple);
        bp.add(triple2);
        bp.add(triple3);
        addGP2Query(query);

        System.out.println("\n\n\n== after ==\n" + query);
        System.out.println(" query construct : "+ queryConstruction.getBpConstruct().toString());
        query = toConstruct(query,new Template(queryConstruction.getBpConstruct()));
        System.out.println("nouvelle query : "+query);
    }

    private static Query toConstruct(Query query, Template constructTemplate)
    {
        query.setQueryConstructType();
        query.setConstructTemplate(constructTemplate);
        return query;
    }
    public static Query addGP2Query(Query query) {
        QueryModifyElementVisitor qmev = new QueryModifyElementVisitor();
        qmev.walker(query.getQueryPattern(),queryConstruction);
        return query;
    }


}


