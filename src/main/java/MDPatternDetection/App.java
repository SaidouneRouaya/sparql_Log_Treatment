package MDPatternDetection;


import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import java.util.Iterator;
//TODO delete this class

public class App {
   /* public static void main(String[] args) {
       /* String queryStr =
                "PREFIX foaf:  <http://xmlns.com/foaf/0.1/>" +
                        "SELECT DISTINCT ?name ?nick" +
                        "{?x foaf:mbox <mailt:person@server> ." +
                        "?x foaf:name ?name " +
                        "OPTIONAL { ?x foaf:nick ?nick }}";
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
    }*/

    public static void main(String[] args) {

        String endPoint = "https://dbpedia.org/sparql";


        String queryStr2 = "CONSTRUCT " +
                " {  ?sub1 <http://dbpedia.org/property/type> ?ob1 . }" +
                "WHERE { <http://dbpedia.org/resource/Vandalia%2C_Illinois> " +
                "<http://dbpedia.org/property/type>  ?value ; " +
                " a  ?sub1 . ?value  a  ?ob1 }";

        String queryStr = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
                " PREFIX dbo: <http://dbpedia.org/ontology/> " +
                "SELECT ?title WHERE { ?game a dbo:Game  . ?game foaf:name ?title .} " +
                "ORDER by ?title limit 10";

        Query query = QueryFactory.create(queryStr2);
        System.out.println("*************   " + query.toString());

      /*  try () {
            System.out.println("***********************************");
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                System.out.println(results.next().toString()+"\n");
                System.out.println("___________________________________________");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    /*   ResultSet result= new QueryExecutor().executeQuerySelect (queryStr, endPoint);
        while (result.hasNext()) {
            System.out.println(result.next().toString()+"\n");
            System.out.println("___________________________________________");
        }*/

        Model result = new QueryExecutor().executeQueryConstruct(queryStr2, endPoint);


        Iterator<Statement> list = result.listStatements();
        while (list.hasNext()) {
            System.out.println(list.next().toString() + "\n");
            System.out.println("___________________________________________");
        }


    }
}

