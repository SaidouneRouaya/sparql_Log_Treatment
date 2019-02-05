package MDPatternDetection;


import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
       /* String queryStr =
                "PREFIX foaf:  <http://xmlns.com/foaf/0.1/>" +
                        "SELECT DISTINCT ?name ?nick" +
                        "{?x foaf:mbox <mailt:person@server> ." +
                        "?x foaf:name ?name " +
                        "OPTIONAL { ?x foaf:nick ?nick }}";*/
        MyOpVisitorBase movb = new MyOpVisitorBase();
        String queryStr = "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
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
                "     ?game a dbo:Game  ." +
                "     ?game a dbo:Game  ." +
                "     ?game foaf:name ?title ." +
                "} ORDER by ?title limit 10";

        Query query = QueryFactory.create(queryStr);
        System.out.println("*************" + query.toString());
        Op op = Algebra.compile(query);
        System.out.println(op.toString());
        //System.out.println(movb.executeQuery(queryStr).getResourceModel().size());
        movb.myOpVisitorWalker(op);
        movb.contructGraph();
    }
}

