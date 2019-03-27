package MDPatternDetection;


import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.FileOperation;
import Statistics.Statistics1;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.ResourceImpl;

import java.util.HashMap;
import java.util.Set;

public class MDGraphBySubject {

    public static void main(String... argv) {
        HashMap<String, Model> results = TdbOperation.unpersistModelsMap(TdbOperation.dataSetAnnotated);

        HashMap<String, Model> resultsbySubject = getModelsOfSubject("http://dbpedia.org/ontology/Airport",results);
        resultsbySubject.putAll(getModelsOfSubject("http://schema.org/Airport",results));
        //
        // resultsbySubject.putAll(getModelsOfSubject("http://wikidata.dbpedia.org/resource/Q482994",results));

        int i = 0;
        /*for (String key : resultsbySubject.keySet())
        {
            i++;
            System.out.println("************ Modèle "+i+" sujet "+key+" ************");
            //ConsolidationTest.afficherModel(resultsbySubject.get(key));

        }*/
        /*Statistics1 statistis = new Statistics1();
        statistis.stat2(resultsbySubject);*/
        FileOperation.writeModelsInFile(resultsbySubject, Declarations.root+"//Resulting_Files//airport_models.txt");

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

/* Media
"http://dbpedia.org/ontology/Media"
 */

/* software
"http://dbpedia.org/ontology/Software"
 */

/*
<owl:Class rdf:about="http://dbpedia.org/ontology/Album">
<owl:equivalentClass rdf:resource="http://schema.org/MusicAlbum"/>
    <owl:equivalentClass rdf:resource="http://wikidata.dbpedia.org/resource/Q482994"/>
    <prov:wasDerivedFrom rdf:resource="http://mappings.dbpedia.org/index.php/OntologyClass:Album"/>
 */

/*
<owl:Class rdf:about="http://dbpedia.org/ontology/Hotel">
<owl:equivalentClass rdf:resource="http://schema.org/Hotel"/>
    <prov:wasDerivedFrom rdf:resource="http://mappings.dbpedia.org/index.php/OntologyClass:Hotel"/>
 */

/*
<owl:Class rdf:about="http://dbpedia.org/ontology/Airport">
 <owl:equivalentClass rdf:resource="http://schema.org/Airport"/>
    <prov:wasDerivedFrom rdf:resource="http://mappings.dbpedia.org/index.php/OntologyClass:Airport"/>
 */

/*
 <owl:Class rdf:about="http://dbpedia.org/ontology/Game">
 <prov:wasDerivedFrom rdf:resource="http://mappings.dbpedia.org/index.php/OntologyClass:Game"/>
 */