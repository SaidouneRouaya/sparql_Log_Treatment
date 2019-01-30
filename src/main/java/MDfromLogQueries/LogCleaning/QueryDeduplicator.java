package MDfromLogQueries.LogCleaning;

import MDfromLogQueries.Util.FileOperation;
import com.google.common.base.Stopwatch;


import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static MDfromLogQueries.Declarations.Declarations.cleanedQueriesFile;
import static MDfromLogQueries.Declarations.Declarations.writingDedupFilePath;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class QueryDeduplicator {

    public static void main(String[] args) throws IOException{
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            Set<String> querySet = new HashSet<>();
            File logFile = new File(cleanedQueriesFile);
            BufferedReader br = new BufferedReader(new FileReader(logFile));
            String line ="";
            int nb_line=0;
            while ((line = br.readLine()) != null) {
                nb_line++;
                querySet.add(line);
                System.out.println( "ligne \t"+nb_line);
        }
            System.out.println("nombre de ligne dans le set :"+querySet.size()+" "+nb_line);
            FileOperation.WriteInFile(writingDedupFilePath,querySet);
            stopwatch.stop();
            System.out.println("Time elapsed for the program is "+ stopwatch.elapsed(SECONDS));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
