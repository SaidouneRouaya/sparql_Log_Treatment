package MDfromLogQueries.SPARQLSyntaxicValidation;

import MDfromLogQueries.Util.FileOperation;
import com.google.common.base.Stopwatch;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import static MDfromLogQueries.Declarations.Declarations.cleanedQueriesFile;
import static MDfromLogQueries.Declarations.Declarations.syntaxValidFile;
import static MDfromLogQueries.Declarations.Declarations.writingDedupFilePath;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class SyntaxicValidation {

    private static String test2(String queryStr) {
        //String queryStr = "SELECT ?x ?y WHERE { ?x ?x }";
        String queryStr2 = QueryFixer.get().fix(queryStr);
        Query maybeQuery = null;
        try {
            maybeQuery = QueryFactory.create(queryStr2);
            //System.out.println(maybeQuery.toString());
            return maybeQuery.toString();
        }
        catch (Exception e)
        {
            System.out.println("C'est une erreur");
            //e.printStackTrace();
            return null;
        }
        //System.out.println(query.toString());


    }
    public static void main(String[] args) throws IOException{
        Stopwatch stopwatch = Stopwatch.createStarted();
        ArrayList<String> queryList = new ArrayList<>();
        ArrayList<String> validQueryList = new ArrayList<>();
        String query= null;
        try {
            File dedupFile = new File(writingDedupFilePath );
            BufferedReader br = new BufferedReader(new FileReader(dedupFile));
            //String line ="";
            int nb_line=0;
            queryList = (ArrayList<String>) FileOperation.ReadFile(writingDedupFilePath);

            for (String line : queryList){
                nb_line++;
                query = test2(line);
                if (query!=null)
                {
                    validQueryList.add(query);
                }
             System.out.println( "ligne \t"+nb_line);
        }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("taille de la validQueryList : "+validQueryList.size());
        FileOperation.WriteInFile(syntaxValidFile,validQueryList);
        stopwatch.stop();
        System.out.println("Time elapsed for the program is "+ stopwatch.elapsed(SECONDS));

    }
}
