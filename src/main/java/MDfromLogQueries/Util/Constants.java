package MDfromLogQueries.Util;

import MDPatternDetection.OntologyFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

import java.util.HashSet;

// Cette classe est à initialiser au niveau de l'appel de toutes les requêtes et pas seulement d'une seule
public class Constants {
    private static HashSet<String> datatypeProperties;
    private static HashSet<String> objectProperties;
    private static String ontologyPath;

    public Constants(String ontologyPath) {
        this.ontologyPath = ontologyPath;
        initObjectProperties();
        initDatatypeProperties();
    }

    private static void initDatatypeProperties() {
        OntModel ontologie = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology(ontologyPath, ontologie);
        String datatypeQuery = "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>" +
                "select ?prop where { ?prop rdf:type owl:DatatypeProperty}";
        Query query = QueryFactory.create(datatypeQuery);
        datatypeProperties = simpleExecution(query,ontologie);
    }

    private static void initObjectProperties() {
        OntModel ontologie = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology(ontologyPath, ontologie);
        String datatypeQuery = "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>" +
                "select ?prop where { ?prop rdf:type owl:ObjectProperty}";
        Query query = QueryFactory.create(datatypeQuery);
        objectProperties = simpleExecution(query,ontologie);
    }

    public static HashSet<String> getDatatypeProperties() {
        if (datatypeProperties.size() < 1) {
            initDatatypeProperties();
        }
        return datatypeProperties;
    }

    public static HashSet<String> getObjectProperties() {
        if (objectProperties.size() < 1) {
            initObjectProperties();
        }
        return objectProperties;
    }

    /* Execute a query into an ontology*/
    private static HashSet<String> simpleExecution(Query query, OntModel ontologie)
    {
        HashSet<String> propertySet = new HashSet<>();
        try (QueryExecution qexec = QueryExecutionFactory.create(query, ontologie)) {
            ResultSet results = qexec.execSelect();
            int i = 0;
            while (results.hasNext()) {
                propertySet.add(results.next().toString());
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return propertySet;
    }

    //à changer probablement en créant un nouveau type contenant la datatypeProperty et son ou ses range
    public static HashSet<Node> getRangeofProperty(Property property) {
        OntModel ontologie = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology(ontologyPath, ontologie);
        HashSet<Node> rangeSet = new HashSet<>();

        String datatypeQuery = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "SELECT ?prop ?range WHERE {?prop rdf:type rdf:Property. " +
                "?prop rdfs:range ?type. " +
                " FILTER (?prop = <http://dbpedia.org/ontology/playerStatus>) }  ";
        Query query = QueryFactory.create(datatypeQuery);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, ontologie)) {
            ResultSet results = qexec.execSelect();
            int i = 0;
            while (results.hasNext()) {
                rangeSet.add(results.next().get("range").asNode());
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rangeSet;
    }
}
