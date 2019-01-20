package MDfromLogQueries.LogCleaning;

import com.google.common.base.Stopwatch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
            File fichier_log_Nettoye =new File(writingDedupFilePath);
            BufferedWriter bw = null;
            try {
                if (!fichier_log_Nettoye.isFile()) fichier_log_Nettoye.createNewFile();
                bw = new BufferedWriter(new FileWriter(fichier_log_Nettoye, true));
                for (String s : querySet)
                {
                    bw.write(s+"####");
                    bw.flush();
                }
            }
            catch (IOException e) {
                System.out.println("Impossible de creer le fichier");
            }finally {
                System.out.println("je suis dans le finally");
                bw.close();}
            stopwatch.stop();
            System.out.println("Time elapsed for the program is "+ stopwatch.elapsed(MILLISECONDS));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
