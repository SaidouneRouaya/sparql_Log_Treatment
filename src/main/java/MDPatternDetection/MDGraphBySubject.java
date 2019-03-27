package MDPatternDetection;

import Statistics.Statistics1;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.ResourceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static MDfromLogQueries.Util.FileOperation.writeStatisticsInFile2;
import static MDfromLogQueries.Util.FileOperation.writeStatisticsListInFile;
import static Statistics.Statistics1.*;

public class MDGraphBySubject {

    public static void main(String... argv) {
        HashMap<String, Model> results = TdbOperation.unpersistModelsMap(TdbOperation.dataSetAnnotated);
        Statistics1 statistics = new Statistics1();

        HashMap<String, Model> publication = getModelsOfSubject("http://dbpedia.org/ontology/University", results);
        HashMap<String, Model> software = getModelsOfSubject("http://dbpedia.org/ontology/University", results);
        HashMap<String, Model> game = getModelsOfSubject("http://dbpedia.org/ontology/University", results);
        HashMap<String, Model> album = getModelsOfSubject("http://dbpedia.org/ontology/University", results);
        HashMap<String, Model> hotel = getModelsOfSubject("http://dbpedia.org/ontology/University", results);
        HashMap<String, Model> movie = getModelsOfSubject("http://dbpedia.org/ontology/University", results);
        HashMap<String, Model> book = getModelsOfSubject("http://dbpedia.org/ontology/University", results);
        HashMap<String, Model> media = getModelsOfSubject("http://dbpedia.org/ontology/University", results);
        HashMap<String, Model> airport = getModelsOfSubject("http://dbpedia.=/ontology/University", results);


        /** university **/
        HashMap<String, Model> university = getModelsOfSubject("http://dbpedia.org/ontology/University", results);
        writeAllStats(statistics.stat2(university), "university");


    }


    public static void writeAllStats(ArrayList<Statistics1> statistics1ArrayList, String subj) {

        String path = "C:\\Users\\pc\\Desktop\\PFE\\Files\\Statistics\\";
        writeStatisticsListInFile(statistics1ArrayList, path + subj + ".txt");
        writeStatisticsInFile2(avgStatistics(statistics1ArrayList), "Average", path + subj + ".txt");
        writeStatisticsInFile2(minStatistics(statistics1ArrayList), "Minimum", path + subj + ".txt");
        writeStatisticsInFile2(maxStatistics(statistics1ArrayList), "Maximum", path + subj + ".txt");
        writeStatisticsInFile2(totalStatistics(statistics1ArrayList), "Total", path + subj + ".txt");


    }


    public static HashMap<String, Model> getModelsOfSubject(String subject, HashMap<String, Model> models) {
        HashMap<String, Model> resultingModels = new HashMap<>();
        Set<String> keys = models.keySet();
        RDFNode subjectNode = new ResourceImpl(subject);
        for (String key : keys) {
            if (models.get(key).containsResource(subjectNode)) {
                resultingModels.put(key, models.get(key));
            }
        }
        return resultingModels;
    }


}


/* the books
"http://dbpedia.org/ontology/Book"
"http://schema.org/Book"
"http://purl.org/ontology/bibo/Book"
 */

/* Movies
"http://dbpedia.org/ontology/movie"
 */

/* University
"http://schema.org/CollegeOrUniversity"
 */