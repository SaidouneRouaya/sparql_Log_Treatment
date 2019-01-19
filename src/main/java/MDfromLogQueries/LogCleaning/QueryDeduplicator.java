package MDfromLogQueries.LogCleaning;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import MDfromLogQueries.Declarations.*;

public class QueryDeduplicator {

    public static void main(String[] args){

        try {
            Set<String> querySet = new HashSet<>();
            File logFile = new File(Declarations.cleanedQueriesFile);
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
