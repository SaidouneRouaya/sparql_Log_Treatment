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

    public Constants() {
        initObjectProperties();
        initDatatypeProperties();
    }

    private static void initDatatypeProperties() {
        OntModel ontologie = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology("C:\\Users\\KamilaB\\Desktop\\3CS\\Prototypage\\Step_1\\dbpedia_2014.owl\\dbpedia_2014.owl", ontologie);
        HashSet<String> datatypePropertySet = new HashSet<>();

        String datatypeQuery = "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>" +
                "select ?prop where { ?prop rdf:type owl:DatatypeProperty}";
        Query query = QueryFactory.create(datatypeQuery);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, ontologie)) {
            ResultSet results = qexec.execSelect();
            int i = 0;
            while (results.hasNext()) {
                datatypePropertySet.add(results.next().toString());
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        datatypeProperties = datatypePropertySet;
    }

    private static void initObjectProperties() {
        OntModel ontologie = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology("C:\\Users\\KamilaB\\Desktop\\3CS\\Prototypage\\Step_1\\dbpedia_2014.owl\\dbpedia_2014.owl", ontologie);
        HashSet<String> objectPropertySet = new HashSet<>();

        String datatypeQuery = "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>" +
                "select ?prop where { ?prop rdf:type owl:ObjectProperty}";
        Query query = QueryFactory.create(datatypeQuery);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, ontologie)) {
            ResultSet results = qexec.execSelect();
            int i = 0;
            while (results.hasNext()) {
                objectPropertySet.add(results.next().toString());
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        objectProperties = objectPropertySet;
    }

    public static HashSet<String> getDatatypeProperties() {
        if (datatypeProperties.size() < 1) {
            initDatatypeProperties();
        }
        return datatypeProperties;
    }

    public HashSet<String> getObjectProperties() {
        if (objectProperties.size() < 1) {
            initObjectProperties();
        }
        return objectProperties;
    }

    //à changer probablement en créant un nouveau type contenant la datatypeProperty et son ou ses range
    public HashSet<Node> getRangeofProperty(Property property) {
        OntModel ontologie = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology("C:\\Users\\KamilaB\\Desktop\\3CS\\Prototypage\\Step_1\\dbpedia_2014.owl\\dbpedia_2014.owl", ontologie);
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
