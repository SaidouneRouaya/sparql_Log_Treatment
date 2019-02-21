package MDPatternDetection;


import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.Iterator;

import static MDfromLogQueries.Declarations.Declarations.syntaxValidFileTest;
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



        String queryStr2 = "CONSTRUCT " +
                " {  ?sub1 <http://dbpedia.org/property/type> ?ob1 . }" +
                "WHERE { <http://dbpedia.org/resource/Vandalia%2C_Illinois> " +
                "<http://dbpedia.org/property/type>  ?value ; " +
                " a  ?sub1 . ?value  a  ?ob1 }";

        String queryStr = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
                " PREFIX dbo: <http://dbpedia.org/ontology/> " +
                "SELECT ?title WHERE { ?game a dbo:Game  . ?game foaf:name ?title .} " +
                "ORDER by ?title limit 10";

    }*/

    public static void main(String[] args) {


        String endPoint = "https://dbpedia.org/sparql";
        ArrayList<Model> results = QueryExecutor.executeQuiersInFile(syntaxValidFileTest, endPoint);
        System.out.println("------------------------------- AFFICHAGE DES RESULTATS ---------------------------------------");
        afficherModels(results);
    }


    //TODO a enlever après

    public static void afficherModels(ArrayList<Model> results) {


        for (Model m : results) {
            System.out.println("________________________ NEW MODEL ____________________________________\n");

            Iterator<Resource> listSubjects = m.listSubjects();

            while (listSubjects.hasNext()) {
                Resource sub = listSubjects.next();
                Iterator<Statement> listProp = sub.listProperties();

                while (listProp.hasNext()) {
                    System.out.println(" \t\t\t " + listProp.next().toString());
                }

            }
            System.out.println("_____________________________ END _______________________________\n");
        }
    }

    //TODO a enlever après


}






