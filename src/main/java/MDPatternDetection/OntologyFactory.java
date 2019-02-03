package MDPatternDetection;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.impl.OntModelImpl;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        List<String> roots = new ArrayList<String>();

        // find all owl:Class entities and filter these which do not have a parent
        /*String getRootsQuery =
                "SELECT DISTINCT ?s WHERE "
                        + "{"
                        + "  ?s <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://www.w3.org/2002/07/owl#Thing> . "
                        + "  FILTER ( ?s != <http://www.w3.org/2002/07/owl#Thing> && ?s != <http://www.w3.org/2002/07/owl#Nothing> ) . "
                        + "  OPTIONAL { ?s <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?super . "
                        + "  FILTER ( ?super != <http://www.w3.org/2002/07/owl#Thing> && ?super != ?s ) } . "
                        + "}";
*/
        String getRootsQuery = "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "SELECT ?var WHERE {?var rdf:type rdf:Property} LIMIT 10";
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
                //RDFNode sub = soln.get("s");

                //if( !sub.isURIResource() ) continue;

                //roots.add( sub.toString() );
            }
        }

    }
}
