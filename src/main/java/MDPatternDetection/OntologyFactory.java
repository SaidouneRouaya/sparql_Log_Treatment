package MDPatternDetection;

import org.apache.commons.io.FilenameUtils;
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
            String extension = FilenameUtils.getExtension(file);
            switch (extension)
            {
                case ("rdf") :
                    extension = "RDF/XML";
                    break;
                case ("ttl"):
                    extension = "TURTLE";
                    break;
                case ("nt"):
                    extension = "N-TRIPLES";
                    break;
                    default:
                        extension = "RDF/XML";
                        break;
            }
            model.read(in, file, extension);
            in.close();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }
}
