package MDPatternDetection;

import org.apache.jena.ontology.OntModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class OntologyFactory {

    /**
     * This class loads an ontology
     **/

    public static void readOntology(String file, OntModel model ) {
        InputStream in;
        try {
            in = new FileInputStream( file );
            model.read(in, "RDF/XML");
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
