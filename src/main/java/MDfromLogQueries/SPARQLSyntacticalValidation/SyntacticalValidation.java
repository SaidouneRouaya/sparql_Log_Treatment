package MDfromLogQueries.SPARQLSyntacticalValidation;

import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.FileOperation;
import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static MDfromLogQueries.Declarations.Declarations.*;
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
        /*Stopwatch stopwatch = Stopwatch.createStarted();
        String sourceFile = syntaxValidFile2;
       // String writingFile = syntaxValidFile;
        String writingFile = "C:\\Users\\KamilaB\\Desktop\\3CS\\Prototypage\\Step_1\\endpoints\\LinkedGeoData\\Support_Files\\Resulting_Files\\syntaxValidReglage";

        // Validate (Test_query);

        ValidateFile(sourceFile, writingFile);

        stopwatch.stop();
        System.out.println("Time elapsed for the program is " + stopwatch.elapsed(SECONDS));*/
      /*  Validate("PREFIX dc: <http://purl.org/dc/elements/1.1/>" +
                " PREFIX lgdp: <http://linkedgeodata.org/property/> " +
                "PREFIX lgdo: <http://linkedgeodata.org/ontology/>\t" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\t" +
                "PREFIX gho: <http://ghodata/>" +
                "\tPREFIX dbpedia-owl: <http://dbpedia.org/ontology/>" +
                "\tPREFIX umbel-sc: <http://umbel.org/umbel/sc/>" +
                "\tPREFIX linkedct: <http://data.linkedct.org/resource/linkedct/>" +
                "\tPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\t" +
                "SELECT count distinct ?a WHERE {?a a dbpedia-owl:LightHouse} GROUP BY  ?a\n");    */
        Validate("\tSELECT  ?s (count(*))\tFROM <http://linkedgeodata.org>\tWHERE\t  { ?s  rdfs:label  ?label\t    FILTER regex(?label, \"Moscova\")\t    OPTIONAL\t      { ?s  georss:point  ?point }\t  }\tGROUP BY ?s\t");


    }

    /**
     * Validates query with Query Fixer
     **/
    private static String Validate(String queryStr) {
        String queryStr2 = QueryFixer.get().fix(queryStr);
        System.out.println(queryStr2);
        return QueryFixer.toQuery(queryStr2).toString();
    }

    /**
     * Validates the queries contained in the file in filepath
     **/

    public static void ValidateFile(String filePath, String destinationFilePath) {

        ArrayList<String> validQueryList = new ArrayList<>();
        ArrayList<String> nonValidQueryList = new ArrayList<>();
        String query;
        ArrayList<String> queryList;
        int nb = 0;

            int nb_line = 0; //for statistical needs
            queryList = (ArrayList<String>) FileOperation.ReadFile(filePath);
            for (String line : queryList){
                try {
                nb_line++;
                query = Validate(line);
                if (query!=null) {
                    validQueryList.add(query);
                }
                 System.out.println( "line \t"+nb_line);
                } catch (Exception e) {
                    // e.printStackTrace();
                    System.out.println("erreur 1");
                    nonValidQueryList.add(line);
                    nb++;
                }
            }


        /* System.out.println("Size of validQueryList : "+validQueryList.size());*/
        FileOperation.WriteInFile(destinationFilePath,validQueryList);
        FileOperation.WriteInFile(Declarations.syntaxNonValidFile,nonValidQueryList);
        System.out.println("\n\n nombre d'erreur \t" + nb);
    }

}
