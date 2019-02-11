package MDfromLogQueries.Util;

import MDPatternDetection.OntologyFactory;
import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.SPARQLSyntacticalValidation.Resources;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.impl.OntPropertyImpl;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.impl.PropertyImpl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static MDfromLogQueries.Declarations.Declarations.LogDirectory;


public class Constants {

    /**
     * This class is for constants' declaration
     **/

    private static HashSet<OntProperty> datatypeProperties = new HashSet<>();
    private static HashSet<OntProperty> objectProperties = new HashSet<>();
    private static HashSet<OntProperty> otherProperties = new HashSet<>();
    private static String ontologyPath;
    private static String defaultOntologiesDirectory = Declarations.defaultOntologiesDir;
    private static Node temporarRange;
    private static OntProperty currentProperty= null;

    public Constants(String path) {
        ontologyPath = path;
        initObjectProperties();
        initDatatypeProperties();
        initDefaultProperties();
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
    public static void main(String[] args) {
        initDefaultProperties();
        System.out.println(isDatatypeProperty(new PropertyImpl("http://purl.org/dc/terms/alternative")));
    }

    private static void initDefaultProperties() {
                List<Path> filesInFolder = new ArrayList<>();
        try {
            filesInFolder = Files.walk(Paths.get(defaultOntologiesDirectory))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        /** for each file in the specified directory **/
        if (filesInFolder != null) {
            for (Path p : filesInFolder) {
                OntModel ontology = ModelFactory.createOntologyModel();
                String chemin = p.toString();
                System.out.println("Vous avez saisi l'url: " + chemin);
                OntologyFactory.readOntology(chemin, ontology);
                addPropertiesToList(ontology);
            }

        }
    }

    private static void addPropertiesToList(OntModel ontology)
    {
        int datatypePropertiesSize =ontology.listDatatypeProperties().toList().size();
        int objectPropertiesSize = ontology.listObjectProperties().toList().size();

        System.out.println("datatype "+datatypePropertiesSize+"\n object "+objectPropertiesSize+" ohtter "+ontology.listOntProperties().toList().size());
        otherProperties.addAll(ontology.listOntProperties().toList());
        if ( datatypePropertiesSize > 0 || objectPropertiesSize >0 )
        {
            if (objectPropertiesSize >0 ) {
                objectProperties.addAll(ontology.listObjectProperties().toList());
                otherProperties.removeAll(ontology.listObjectProperties().toList());
            }

            if (datatypePropertiesSize> 0) {
                datatypeProperties.addAll(ontology.listDatatypeProperties().toList());
                otherProperties.removeAll(ontology.listDatatypeProperties().toList());
            }
        }
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

    private static boolean setContains(Property property, HashSet<OntProperty> set)
    {
        for (OntProperty prop : set) {
            if (prop.getURI().matches(property.getURI())) {
                currentProperty = prop;
                return true;
            }
        }
        return false;
    }

    /** Verify if the property is a dataType property **/
    public static boolean isDatatypeProperty(Property property) {
        boolean returnValue= false;
        if(setContains(property,datatypeProperties))
        {
            returnValue = true;
        }
        return returnValue;

    }

    /** Verify if the property is an object property **/
    public static boolean isObjectProperty(Property property) {
        boolean returnValue= false;
        if(setContains(property,objectProperties))
        {
            returnValue = true;
        }
        return returnValue;

    }





    /** Execute a query onto an ontology **/
    private static HashSet<String> simpleExecution(Query query, OntModel ontologie)
    {
        HashSet<String> propertySet = new HashSet<>();
        try (QueryExecution qexec = QueryExecutionFactory.create(query, ontologie)) {
            ResultSet results = qexec.execSelect();
            int i = 0;
            while (results.hasNext()) {
                propertySet.add(results.next().getResource("prop").getURI());

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return propertySet;
    }


}
