package MDPatternDetection;


import MDfromLogQueries.LogCleaning.Queries2GraphesParallel;
import com.google.common.base.Stopwatch;

public class test {

    public static void main(String args[]) {

        System.out.println(" avec SELECT ");
        Stopwatch stopwatchSelect = Stopwatch.createStarted();
        //Queries2GraphesParallel.TransformQueriesInFile("C:\\Users\\pc\\Desktop\\PFE\\Files\\Fichier_Syntaxe_Valide_Test.txt");
        Queries2GraphesParallel.TransformQueriesInFile("C:\\Users\\pc\\Desktop\\PFE\\Files\\Fichier_Syntaxe_Valide_Test.txt");
        stopwatchSelect.stop();

        Stopwatch stopwatchexec = Stopwatch.createStarted();
        //QueryExecutorParallelFuture.executeQueriesInFile(Declarations.constructQueriesFileTest, "https://dbpedia.org/sparql");
        //QueryExecutorParallelFuture.executeQueriesInFile(Declarations.constructQueriesFileTest, "https://dbpedia.org/sparql");
        stopwatchexec.stop();


        System.out.println(" Temps de transformation " + stopwatchSelect);
        System.out.println(" Temps de l'exec " + stopwatchexec);


    }


}
