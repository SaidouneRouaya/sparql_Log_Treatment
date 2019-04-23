package MDfromLogQueries.SPARQLSyntacticalValidation;

import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.FileOperation;

import java.util.ArrayList;

public class SyntacticalValidation {

    private static int queriesNumber = 0;


    public static void main(String[] args) {

        Validate("\tSELECT  ?s (count(*))\t" +
                "FROM <http://linkedgeodata.org>\t" +
                "WHERE\t  { ?s  rdfs:label  ?label\t    " +
                "FILTER regex(?label, \"Moscova\")\t    " +
                "OPTIONAL\t      { ?s  georss:point  ?point }\t  }\t" +
                "GROUP BY ?s\t");


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


        queriesNumber += validQueryList.size();
        /* System.out.println("Size of validQueryList : "+validQueryList.size());*/
        FileOperation.WriteInFile(destinationFilePath,validQueryList);
        FileOperation.WriteInFile(Declarations.syntaxNonValidFile,nonValidQueryList);
        System.out.println("\n\n nombre d'erreur \t" + nb);
    }

}
