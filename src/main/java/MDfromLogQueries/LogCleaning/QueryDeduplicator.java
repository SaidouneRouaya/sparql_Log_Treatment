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

public class QueryDeduplicator {

    public static void main(String[] args) throws IOException{
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            Set<String> querySet = new HashSet<>();
            File logFile = new File(cleanedQueriesFile);
            Scanner scanner = new Scanner(logFile);
            scanner.useDelimiter("####");
            int nb_line=0;
            // BufferedReader br = new BufferedReader(new FileReader(logFile));
            String line;
            while (scanner.hasNext()) {
                line = scanner.next();
                //System.out.println(line);

                if (!querySet.contains(line))
                {
                    querySet.add(line);
                }
                nb_line++;
            }
            System.out.println("nombre de ligne dans le set :"+querySet.size()+" "+nb_line);
            FileOperation.WriteInFile(writingDedupFilePath,querySet);
            }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
