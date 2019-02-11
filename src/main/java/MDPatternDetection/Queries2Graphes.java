package MDPatternDetection;

import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.Constants;
import MDfromLogQueries.Util.FileOperation;
import com.google.common.base.Stopwatch;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import java.util.ArrayList;

import static MDfromLogQueries.Declarations.Declarations.syntaxValidFileTest;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Queries2Graphes {
    /**
     * This class transforms queries to CONSTRUCT to give back their graph for further manipulation
     **/

    public Queries2Graphes() {
        /* Change the path in the case of using another query logs */
        new Constants(Declarations.dbPediaOntologyPath); // init the Constants to use it next
    }

    /**
     * Transforms queries read from the file filePath, to CONSTRUCT queries
     * to get the graph corresponding to each query
     **/

    public static ArrayList<Query> TransformQueriesinFile(String filePath) {

        ArrayList<Query> constructQueriesList = new ArrayList<>();
        ArrayList<String> lines;

        int nb_line = 0; // for statistical matters

        try {
            /** Graph pattern extraction **/

            lines = (ArrayList<String>) FileOperation.ReadFile(filePath);


/*
            String queryString = "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
                    "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
                    "PREFIX dbo: <http://dbpedia.org/ontology/>" +
                    "SELECT ?title WHERE {" +
                    "     ?game a dbo:Game  ." +
                    "?game foaf:friend ?op ." +
                    "Filter (?game = \"gg\")" +
                    "    OPTIONAL { ?game foaf:name ?title }." +
                    "} ORDER by ?title limit 10";


            String queryString2 = "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
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

            lines = new ArrayList<>();
            lines.add(queryString);
            lines.add(queryString2);
*/
            for (String line : lines) {

                try {
                    nb_line++;

                    Query query = QueryFactory.create(line);

                    QueryUpdate queryUpdate = new QueryUpdate(query);

                    query = queryUpdate.toConstruct(query);

                    constructQueriesList.add(query);

                    // System.out.println("***********   "+query);


                } catch (Exception e) {

                }

            }
          
           /* System.out.println(" Queries number : "+nb_line);
            System.out.println("size of Pattern List  "+PatternList.size());*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        return constructQueriesList;
    }


    public static void main(String[] args)  {
        Stopwatch stopwatch = Stopwatch.createStarted();

        TransformQueriesinFile(syntaxValidFileTest);

        stopwatch.stop();
        System.out.println("\n Time elapsed for the program is "+ stopwatch.elapsed(SECONDS));

    }


}
