package MDfromLogQueries.Util;

import MDPatternDetection.OntologyFactory;
import org.apache.jena.atlas.iterator.Iter;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Blank;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.ontology.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.impl.PropertyImpl;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

// TODO Cette classe est à initialiser au niveau de l'appel de toutes les requêtes et pas seulement d'une seule
public class Constants {
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

    public static Node getTemporarRange() {
        return temporarRange;
    }

    private static void initDatatypeProperties() {
        OntModel ontologie = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology(ontologyPath, ontologie);
        ontologie.add(ontologie);
        String datatypeQuery = "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>" +
                "select ?prop where { ?prop rdf:type owl:DatatypeProperty}";
        Query query = QueryFactory.create(datatypeQuery);
        datatypeProperties.addAll(ontologie.listDatatypeProperties().toList());
        System.out.println("*** taille de datatypeProperties ****** "+datatypeProperties.size());
    }

    private static void initObjectProperties() {
        OntModel ontologie = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology(ontologyPath, ontologie);
        String datatypeQuery = "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>" +
                "select ?prop where { ?prop rdf:type owl:ObjectProperty}";
        Query query = QueryFactory.create(datatypeQuery);
        objectProperties.addAll(ontologie.listObjectProperties().toList());
    }

    private static boolean contains(Property property)
    {
        boolean returnValue = false;
        Node  node ;
        node = getRangeofProperty(property);
        if (node != null)
        {
            returnValue = true;
            temporarRange = node;
        }
        return returnValue;

    }

    public static boolean isDatatypeProperty(Property property)
    {
        boolean returnValue = false;
        if (contains(property))
        {
            System.out.println("je suis laaa ds le if");
            returnValue = true;
        }
        else
        {
            OntModel ontoModel = ModelFactory.createOntologyModel();
            OntologyFactory.readOntology(property.getNameSpace(),ontoModel);
            System.out.println("je suis laaa ds le ekseeee");
            ontologies.add(ontoModel);
            System.out.println("taille du datatypeProperties"+ ontoModel.listOntProperties().toList().size());
            datatypeProperties.addAll(ontoModel.listOntProperties().toList());
            objectProperties.addAll(ontoModel.listOntProperties().toList());
            for (OntProperty property1 : ontoModel.listOntProperties().toList())
            System.out.println(" propriété : "+ property1);
            returnValue =  contains(property);

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
    public static Node getRangeofProperty(Property property) {
        /*OntModel ontologie = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology(ontologyPath, ontologie);*/
        OntProperty datatypeProperty;
        Node range = null;
        Iterator<OntProperty>  iterator = datatypeProperties.iterator();
        while(iterator.hasNext())
        {
            datatypeProperty = iterator.next();
            if (datatypeProperty.getURI().matches(property.getURI()))
            {
                range = datatypeProperty.getRange().asNode();
            }
        }
        return range;
    }
}
