package MDPatternDetection;

import MDfromLogQueries.SPARQLSyntaxicValidation.Resources;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.InputStream;

public class GraphLoader {


    private InputStream DBPEDIA_ONTOLOGY = Resources.getResourceAsStream("dbpedia_2014.owl");


    /**
     * Load Ontology
     **/
    public Model readOntology() {

        Model model = ModelFactory.createDefaultModel();

        try {
            model.read(DBPEDIA_ONTOLOGY, "owl");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    /**
     * Load graph
     **/

    public Graph loadGraph() {
        try {
            Model m = readOntology();
            return m.getGraph();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
