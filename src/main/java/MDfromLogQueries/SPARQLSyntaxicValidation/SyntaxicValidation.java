package MDfromLogQueries.SPARQLSyntaxicValidation;

import com.google.common.base.Stopwatch;

import java.util.ArrayList;

import static java.util.concurrent.TimeUnit.SECONDS;

public class SyntaxicValidation {

    private static String test2(String queryStr) {
        //String queryStr = "SELECT ?x ?y WHERE { ?x ?x }";
        String queryStr2 = QueryFixer.get().fix(queryStr);
        System.out.println(queryStr2);
        return QueryFixer.toQuery(queryStr2).toString();
    }

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ArrayList<String> validQueryList = new ArrayList<>();
        test2("PREFIX a: <http://a/>\n" +
                "\n" +
                "# 12:34\n" +
                "SELECT ?x ?x ?x ?y WHERE {\n" +
                "?x a:a ?y.\n" +
                "?x b:b ?y.\n" +
                "?x b:b ?y.\n" +
                "?x b:b ?y.\n" +
                "?x c-x:c-x ?y.\n" +
                "}");
        /*try {
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
        */
        stopwatch.stop();
        System.out.println("Time elapsed for the program is "+ stopwatch.elapsed(SECONDS));

    }
}
