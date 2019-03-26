package MDPatternDetection;


import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.ResourceImpl;

import java.util.HashMap;
import java.util.Set;

public class MDGraphBySubject {

    public static void main(String... argv) {
        HashMap<String, Model> results = TdbOperation.unpersistModelsMap(TdbOperation.dataSetAnnotated);

        HashMap<String, Model> resultsbySubject = getModelsOfSubject("http://dbpedia.org/ontology/University",results);

        int i = 0;
        for (String key : resultsbySubject.keySet())
        {
            i++;
            System.out.println("************ Modèle "+i+" sujet "+key+" ************");
            ConsolidationTest.afficherModel(resultsbySubject.get(key));

        }

    }


    public static HashMap<String, Model> getModelsOfSubject(String subject, HashMap<String,Model> models)
    {
        HashMap<String, Model> resultingModels = new HashMap<>();
        Set<String> keys = models.keySet();
        RDFNode subjectNode = new ResourceImpl(subject);
        for(String key : keys)
        {
            if (models.get(key).containsResource(subjectNode))
            {
                resultingModels.put(key,models.get(key));
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