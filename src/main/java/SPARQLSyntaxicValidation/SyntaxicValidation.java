package SPARQLSyntaxicValidation;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.SQLOutput;
import java.util.Scanner;

public class SyntaxicValidation {
    private static void test2(String queryStr) {
        //String queryStr = "SELECT ?x ?y WHERE { ?x ?x }";
        String queryStr2 = QueryFixer.get().fix(queryStr);
        Query maybeQuery = null;
        try {
            maybeQuery = QueryFactory.create(queryStr2);
            System.out.println(maybeQuery.toString());
        }
        catch (Exception e)
        {
            System.out.println("C'est une erreur");
            //e.printStackTrace();
        }
       //System.out.println(query.toString());


    }
    public static void main(String[] args){

        try {

            File logFile = new File("C:\\Users\\KamilaB\\Desktop\\3CS\\Prototypage\\Step 1\\Fichier_log_Nettoye.txt");
            Scanner scanner = new Scanner(logFile);
            scanner.useDelimiter("####");
           // BufferedReader br = new BufferedReader(new FileReader(logFile));
            String line;
            while ((line = scanner.next()) != null) {
                System.out.println(line);
                test2(line);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

