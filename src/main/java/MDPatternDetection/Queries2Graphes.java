package MDPatternDetection;

import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.Constants;
import MDfromLogQueries.Util.FileOperation;
import com.google.common.base.Stopwatch;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static MDfromLogQueries.Declarations.Declarations.constructQueriesFile;
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
        new Constants(Declarations.dbPediaOntologyPath);
        //  ArrayList<Query> constructQueriesList = new ArrayList<>();
        ArrayList<Query> constructQueriesList = new ArrayList<>();
        ArrayList<Query> constructQueriesListFinal = new ArrayList<>();
        ArrayList<String> lines;

        int nb_line = 0; // for statistical matters

        try {
            /** Graph pattern extraction **/

            lines = (ArrayList<String>) FileOperation.ReadFile(filePath);


            //while (nb_line<1000){
            for (String line : lines) {

                try {


                    // String line = lines.get(nb_line);
                    nb_line++;

                    Query query = QueryFactory.create(line);

                    QueryUpdate queryUpdate = new QueryUpdate(query);

                    query = queryUpdate.toConstruct(query);

                    constructQueriesList.add(query);

                    System.out.println("*  " + nb_line);

                    if (nb_line == 10000) {
                        constructQueriesListFinal.addAll(constructQueriesList);
                        FileOperation.WriteConstructQueriesInFile(constructQueriesFile, constructQueriesList);
                        nb_line = 0;
                        constructQueriesList.clear();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //Todo do something (++ nb for statistics)
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return constructQueriesListFinal;
    }

    public static ArrayList<Query> TransformQueriesinFile2(List<String> lines) {

        new Constants(Declarations.dbPediaOntologyPath);

        //ArrayList<Query> constructQueriesList = new ArrayList<>();
        ArrayList<Query> constructQueriesList = new ArrayList<>();

        int nb_line = 0; // for statistical matters

        try {
            /** Graph pattern extraction **/

            Iterator<String> it = lines.iterator();

            while (it.hasNext()) {

                try {

                    String line = it.next();
                    nb_line++;
                    Query query = QueryFactory.create(line);

                    QueryUpdate queryUpdate = new QueryUpdate(query);

                    query = queryUpdate.toConstruct(query);

                    constructQueriesList.add(query);
                    it.remove();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Todo do something (++ nb for statistics)
                }
            }
            // save results into file
            FileOperation.WriteConstructQueriesInFile(constructQueriesFile, constructQueriesList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return constructQueriesList;
    }

    public static void main(String[] args)  {
        Stopwatch stopwatch = Stopwatch.createStarted();

        // Queries2GraphesParallel.TransformQueriesInFile(syntaxValidFile);

        stopwatch.stop();

        System.out.println("\n Time elapsed for the program is "+ stopwatch.elapsed(SECONDS));

    }


}
