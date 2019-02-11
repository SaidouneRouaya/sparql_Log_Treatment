package MDPatternDetection;


import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.Constants;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.syntax.Template;

public class QueryUpdate {

    /**
     * This class update queries with rdf:type triples
     **/

    private QueryConstruction queryConstruction = new QueryConstruction();

    public QueryUpdate(Query query)
    {
        addGP2Query(query);

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


        final String queryString2 = "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
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

    private Query addGP2Query(Query query) {
        QueryModifyElementVisitor qmev = new QueryModifyElementVisitor();
        qmev.walker(query.getQueryPattern(),queryConstruction);
        return query;
    }


}


