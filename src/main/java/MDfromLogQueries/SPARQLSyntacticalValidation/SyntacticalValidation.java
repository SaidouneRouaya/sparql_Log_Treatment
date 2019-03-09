package MDfromLogQueries.SPARQLSyntacticalValidation;

import MDfromLogQueries.Util.FileOperation;
import com.google.common.base.Stopwatch;

import java.util.ArrayList;

import static MDfromLogQueries.Declarations.Declarations.syntaxValidFile;
import static MDfromLogQueries.Declarations.Declarations.writingDedupFilePath;
import static java.util.concurrent.TimeUnit.SECONDS;

public class SyntacticalValidation {

    public static String Test_query = "PREFIX a: <http://a/>\n" +
            "\n" +
            "# 12:34\n" +
            "SELECT ?x ?x ?x ?y WHERE {\n" +
            "?x a:a ?y.\n" +
            "?x b:b ?y.\n" +
            "?x b:b ?y.\n" +
            "?x b:b ?y.\n" +
            "?x c-x:c-x ?y.\n" +
            "}";

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String sourceFile = writingDedupFilePath;
        String writingFile = syntaxValidFile;

        // Validate (Test_query);

        ValidateFile(sourceFile, writingFile);

        stopwatch.stop();
        System.out.println("Time elapsed for the program is " + stopwatch.elapsed(SECONDS));

    }

    /**
     * Validates query with Query Fixer
     **/
    private static String Validate(String queryStr) {
        String queryStr2 = QueryFixer.get().fix(queryStr);
        /*System.out.println(queryStr2);*/
        return QueryFixer.toQuery(queryStr2).toString();
    }

    /**
     * Validates the queries contained in the file in filepath
     **/

    public static void ValidateFile(String filePath, String destinationFilePath) {

        ArrayList<String> validQueryList = new ArrayList<>();
        String query;
        ArrayList<String> queryList;
        int nb = 0;
        try {

            int nb_line = 0; //for statistical needs
            queryList = (ArrayList<String>) FileOperation.ReadFile(filePath);
            for (String line : queryList){
                nb_line++;
                query = Validate(line);
                if (query!=null) {
                    validQueryList.add(query);
                }
                /* System.out.println( "line \t"+nb_line);*/
            }
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("erreur");
            nb++;
        }

        /* System.out.println("Size of validQueryList : "+validQueryList.size());*/
        FileOperation.WriteInFile(destinationFilePath,validQueryList);

        System.out.println("\n\n nombre d'erruer \t" + nb);
    }

}
