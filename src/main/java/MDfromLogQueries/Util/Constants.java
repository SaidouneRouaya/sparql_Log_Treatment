package MDfromLogQueries.Util;

import MDPatternDetection.OntologyFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

import java.util.HashSet;
import java.util.Iterator;


public class Constants {

    /**
     * This class is for constants' declaration
     **/

    private static HashSet<OntProperty> datatypeProperties = new HashSet<>();
    private static HashSet<OntProperty> objectProperties = new HashSet<>();
    private static String ontologyPath;
    private static HashSet<OntModel> ontologies = new HashSet<>();
    private static Node temporarRange;

    public Constants(String path) {
        ontologyPath = path;
        initObjectProperties();
        initDatatypeProperties();
    }

    public static Node getTemporareRange() {
        return temporarRange;
    }

    /**
     * Initialize the DataType properties into a list
     **/
    private static void initDatatypeProperties() {
        OntModel ontologie = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology(ontologyPath, ontologie);
        ontologie.add(ontologie);
       /* String datatypeQuery = "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>" +
                "select ?prop where { ?prop rdf:type owl:DatatypeProperty}";
        Query query = QueryFactory.create(datatypeQuery);*/

        datatypeProperties.addAll(ontologie.listDatatypeProperties().toList());

        /* System.out.println(" Size of datatypeProperties "+datatypeProperties.size()); */
    }

    /** Initialize a list of Object properties **/
    private static void initObjectProperties() {
        OntModel ontologie = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology(ontologyPath, ontologie);

        /*String datatypeQuery = "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>" +
                "select ?prop where { ?prop rdf:type owl:ObjectProperty}";
        Query query = QueryFactory.create(datatypeQuery);*/

        objectProperties.addAll(ontologie.listObjectProperties().toList());
    }


    /** Verify if the property given as parameter is already in the Properties list **/
    private static boolean contains(Property property) {
        boolean returnValue = false;
        Node node;
        node = getRangeofProperty(property);
        if (node != null) {
            returnValue = true;
            temporarRange = node;
        }
        return returnValue;
    }


    /**
     * Return properties set
     **/
    //à changer probablement en créant un nouveau type contenant la datatypeProperty et son ou ses range
    public static Node getRangeofProperty(Property property) {

        OntProperty datatypeProperty;
        Node range = null;
        Iterator<OntProperty> iterator = datatypeProperties.iterator();
        while (iterator.hasNext()) {
            datatypeProperty = iterator.next();
            if (datatypeProperty.getURI().matches(property.getURI())) {
                range = datatypeProperty.getRange().asNode();
            }
        }
        return range;
    }

    /** Verify if the property is a dataType property **/
    public static boolean isDatatypeProperty(Property property) {
        boolean returnValue;
        if (contains(property)) returnValue = true;
        else {

            OntModel ontoModel = ModelFactory.createOntologyModel();
            OntologyFactory.readOntology(property.getNameSpace(), ontoModel);
            ontologies.add(ontoModel);
            /* System.out.println("Size of datatypeProperties" + ontoModel.listOntProperties().toList().size());*/
            datatypeProperties.addAll(ontoModel.listOntProperties().toList());
            objectProperties.addAll(ontoModel.listOntProperties().toList());

            /*for (OntProperty propertyy : ontoModel.listOntProperties().toList())
                System.out.println(" propriété : " + propertyy);*/

            returnValue = contains(property);
        }
        return returnValue;

    }


    public static HashSet<OntProperty> getDatatypeProperties() {
        if (datatypeProperties.size() < 1) {
            initDatatypeProperties();
        }
        return datatypeProperties;
    }

    public static HashSet<OntProperty> getObjectProperties() {
        if (objectProperties.size() < 1) {
            initObjectProperties();
        }
        return objectProperties;
    }

    /** Execute a query onto an ontology **/
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


}
