package LogCleaning;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class QueryDeduplicator {

    public static void main(String[] args){
        final String cleanedQueriesFile = "C:\\Users\\pc\\Desktop\\PFE\\Fichier_log_Nettoye_Complet_Parallel.txt";
       // final String cleanedQueriesFile = "C:\\Users\\KamilaB\\Desktop\\3CS\\Prototypage\\Step 1\\Fichier_log_Nettoye.txt";

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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
