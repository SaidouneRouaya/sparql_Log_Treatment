package MDPatternDetection;


import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.Constants2;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.syntax.Template;

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
        new Constants2(Declarations.dbPediaOntologyPath); // init the constants tu use it next
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

    private Query addGP2Query(Query query) {
        QueryModifyElementVisitor qmev = new QueryModifyElementVisitor();
        qmev.walker(query.getQueryPattern(),queryConstruction);
        return query;
    }


}


