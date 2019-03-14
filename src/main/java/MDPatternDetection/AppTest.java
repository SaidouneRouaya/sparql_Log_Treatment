package MDPatternDetection;


import MDfromLogQueries.Declarations.Declarations;
import Statistics.Statistis1;
import com.google.common.base.Stopwatch;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
//TODO delete this class

public class AppTest {


    public static void main(String[] args) {


        Stopwatch stopwatch_total = Stopwatch.createStarted();
        Stopwatch stopwatch_exec = Stopwatch.createStarted();
        String endPoint = "https://dbpedia.org/sparql";

        // execution + annotation + persisting
        QueryExecutor.executeQuiersInFile2(Declarations.syntaxValidFile, endPoint);
        stopwatch_exec.stop();


        HashMap<String, Model> modelHashMap;
        Stopwatch stopwatch_unpersist = Stopwatch.createStarted();
        modelHashMap = TdbOperation.unpersistModelsMap();
        stopwatch_unpersist.stop();

        stopwatch_total.stop();

        Stopwatch stopwatch_stat = Stopwatch.createStarted();
        Statistis1 statistis1 = new Statistis1();
        statistis1.stat(modelHashMap);
        stopwatch_stat.stop();

        System.out.println("\nTime elapsed for execution program is \t" + stopwatch_exec.elapsed(MILLISECONDS));
        System.out.println("\nTime elapsed for unpersist program is \t" + stopwatch_unpersist.elapsed(MILLISECONDS));
        System.out.println("\nTime elapsed for the statistics program is \t" + stopwatch_stat.elapsed(MILLISECONDS));
        System.out.println("\nTime elapsed for the whole program is \t" + stopwatch_total.elapsed(MILLISECONDS));



    }


    //TODO a enlever après

    public static void afficherModels(ArrayList<Model> results) {


        for (Model m : results) {
            System.out.println("________________________ NEW MODEL ____________________________________\n");

            Iterator<Resource> listSubjects = m.listSubjects();

            while (listSubjects.hasNext()) {
                Resource sub = listSubjects.next();
                Iterator<Statement> listProp = sub.listProperties();

                while (listProp.hasNext()) {
                    System.out.println(" \t\t\t " + listProp.next().toString());
                }

            }
            System.out.println("_____________________________ END _______________________________\n");
        }
    }

    //TODO a enlever après


}






