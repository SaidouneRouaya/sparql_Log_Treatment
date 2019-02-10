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

            for (String line : lines) {
                nb_line++;
                constructQueriesList.add(new QueryUpdate(QueryFactory.create(line)).getTheQuery());
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
