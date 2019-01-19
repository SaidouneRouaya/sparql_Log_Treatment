package MDfromLogQueries.SPARQLSyntaxicValidation;

import com.google.common.base.Stopwatch;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import static MDfromLogQueries.Declarations.Declarations.syntaxValidFile;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class SyntaxicValidation {

    private static String test2(String queryStr) {
        //String queryStr = "SELECT ?x ?y WHERE { ?x ?x }";
        String queryStr2 = QueryFixer.get().fix(queryStr);
        Query maybeQuery = null;
        try {
            maybeQuery = QueryFactory.create(queryStr2);
            System.out.println(maybeQuery.toString());
            return maybeQuery.toString();
        }
        catch (Exception e)
        {
            System.out.println("C'est une erreur");
            return null;
            //e.printStackTrace();
        }
       //System.out.println(query.toString());


    }
    public static void main(String[] args) throws IOException{
        Stopwatch stopwatch = Stopwatch.createStarted();
        ArrayList<String> validQueryList = new ArrayList<>();
        String query= null;
        try {

            File logFile = new File("C:\\Users\\KamilaB\\Desktop\\3CS\\Prototypage\\Step 1\\Fichier_log_Nettoye.txt");
            Scanner scanner = new Scanner(logFile);
            scanner.useDelimiter("####");
           // BufferedReader br = new BufferedReader(new FileReader(logFile));
            String line;
            while (scanner.hasNext()) {
                line = scanner.next();
                //System.out.println(line);
                query = test2(line);
                if (query!=null)
                {
                    validQueryList.add(query);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        File log_file_valid_syntax =new File(syntaxValidFile);
        BufferedWriter bw = null;
        try {
            if (!log_file_valid_syntax.isFile()) log_file_valid_syntax.createNewFile();
            bw = new BufferedWriter(new FileWriter(log_file_valid_syntax, true));

            for (int i=0; i<validQueryList.size();i++ ) {

                bw.write(validQueryList.get(i) +"####");

                bw.flush();
            }
        }        catch (IOException e) {
            System.out.println("Impossible de creer le fichier");
        }finally {
            System.out.println("je suis dans le finally");
            bw.close();}
        stopwatch.stop();
        System.out.println("Time elapsed for the program is "+ stopwatch.elapsed(MILLISECONDS));

    }
}

