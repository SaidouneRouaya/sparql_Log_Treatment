package MDfromLogQueries.LogCleaning;

import MDfromLogQueries.Util.FileOperation;
import com.google.common.base.Stopwatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import static MDfromLogQueries.Declarations.Declarations.cleanedQueriesFileCopie;
import static MDfromLogQueries.Declarations.Declarations.writingDedupFilePath;
import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * This class eliminates the duplicated queries
 **/


public class QueriesDeduplicator {

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        DeduplicateQueriesInFile(cleanedQueriesFileCopie);
        stopwatch.stop();
        System.out.println("Time elapsed for the program is " + stopwatch.elapsed(SECONDS));
    }

    /**
     * Reads file that contains all the queries from the log files and eliminate duplicated ones
     **/
    public static void DeduplicateQueriesInFile(String filePath) {

        try {
            File logFile = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(logFile));
            String line = "";
            // Using a HashSet because it doesn't accept duplications
            Set<String> querySet = new HashSet<>();
            // nb_line for statistical matters
            int nb_line = 0;

            while ((line = br.readLine()) != null) {
                nb_line++;
                querySet.add(line);
                /* System.out.println( "ligne \t"+nb_line);*/
            }
            System.out.println("nombre de ligne dans le set :" + querySet.size() + " " + nb_line);
            FileOperation.WriteInFile(writingDedupFilePath, querySet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
