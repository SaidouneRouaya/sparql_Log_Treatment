package MDPatternDetection;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class OntologyFactory {

    public static void readOntology( String file, OntModel model )
    {
        InputStream in = null;
        try
        {
            in = new FileInputStream( file );
            model.read(in, "RDF/XML");
            in.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        // create OntModel
        OntModel ontologie = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology("C:\\Users\\KamilaB\\Desktop\\3CS\\Prototypage\\Step_1\\dbpedia_2014.owl\\dbpedia_2014.owl",ontologie);

        // find all owl:Class entities and filter these which do not have a parent
        /*String getRootsQuery =
                "SELECT DISTINCT ?s WHERE "
                        + "{"
                        + "  ?s <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://www.w3.org/2002/07/owl#Thing> . "
                        + "  FILTER ( ?s != <http://www.w3.org/2002/07/owl#Thing> && ?s != <http://www.w3.org/2002/07/owl#Nothing> ) . "
                        + "  OPTIONAL { ?s <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?super . "
                        + "  FILTER ( ?super != <http://www.w3.org/2002/07/owl#Thing> && ?super != ?s ) } . "
                        + "}";
                        "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "SELECT ?var ?type WHERE {?var rdf:type rdf:Property. ?var rdfs:range ?type} LIMIT 10";
*/
        String getRootsQuery =         "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "SELECT ?prop ?range WHERE {?prop rdf:type rdf:Property. " +
                "?prop rdfs:range ?range. " +
                " FILTER (?prop = <http://dbpedia.org/ontology/playerStatus>) }  ";
        Query query = QueryFactory.create( getRootsQuery );

        try ( QueryExecution qexec = QueryExecutionFactory.create( query, ontologie ) )
        {
            ResultSet results = qexec.execSelect();
            int i = 0;
            while( results.hasNext() )
            {
                //QuerySolution soln = results.nextSolution();
                i++;
                System.out.println(results.next() +" " + i);
            }
        }

    }
}
