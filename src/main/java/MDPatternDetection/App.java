package MDPatternDetection;


import com.google.common.base.Stopwatch;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
//TODO delete this class

public class App {


    public static void main(String[] args) {

        Stopwatch stopwatch_unpersist = Stopwatch.createStarted();
        HashMap<String, Model> modelHashMap = TdbOperation.unpersistModelsMap();
        stopwatch_unpersist.stop();
        System.out.println("\nTime elapsed for unpersist program is \t" + stopwatch_unpersist.elapsed(MILLISECONDS) + "\n\n");
        //Consolidation.afficherListInformations(modelHashMap);
        System.out.println("taille du tdb " + modelHashMap.size());
        System.out.println("\ntaille du tdb " + modelHashMap.keySet().size());

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






