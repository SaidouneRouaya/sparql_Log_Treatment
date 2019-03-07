package MDfromLogQueries.Util;

import MDPatternDetection.OntologyFactory;
import MDfromLogQueries.Declarations.Declarations;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.impl.PropertyImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class Constants {

    /**
     * This class is for constants' declaration
     **/

    private static HashSet<OntProperty> datatypeProperties = new HashSet<>();
    private static HashSet<OntProperty> objectProperties = new HashSet<>();
    private static HashSet<OntProperty> otherProperties = new HashSet<>();
    private static String ontologyPath;
    private static String defaultOntologiesDirectory = Declarations.defaultOntologiesDir;
    private static OntProperty currentProperty= null;

    public Constants(String path) {
        ontologyPath = path;
        initObjectProperties();
        initDatatypeProperties();
        initDefaultProperties();
    }

    /**
     * Initialize the DataType properties into a list
     **/
    private static void initDatatypeProperties() {
        OntModel ontologie = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology(ontologyPath, ontologie);
        ontologie.add(ontologie);
        datatypeProperties.addAll(ontologie.listDatatypeProperties().toList());
    }

    /** Initialize a list of Object properties **/
    private static void initObjectProperties() {
        OntModel ontologie = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology(ontologyPath, ontologie);
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
                //System.out.println("Vous avez saisi l'url: " + chemin);
                OntologyFactory.readOntology(chemin, ontology);
                addPropertiesToList(ontology);
            }

        }
    }

    private static void addPropertiesToList(OntModel ontology)
    {
        int datatypePropertiesSize =ontology.listDatatypeProperties().toList().size();
        int objectPropertiesSize = ontology.listObjectProperties().toList().size();
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


    /**
     * Return properties set
     **/
    //à changer probablement en créant un nouveau type contenant la datatypeProperty et son ou ses range
    public static Node getRangeofProperty(Property property) {
        if (currentProperty.getURI().matches(property.getURI())) {
            if (currentProperty.getRange() != null)
                return currentProperty.getRange().asNode();
        }
        else {
            Set<OntProperty> verificationSet= datatypeProperties;
            verificationSet.addAll(otherProperties);
            Node range;
            for (OntProperty ontProperty : verificationSet)
            {
                if (ontProperty.getURI().matches(property.getURI())) {
                    range = ontProperty.getRange().asNode();
                    return range;
                }
            }

        }
        return null;
    }

    public static Boolean isFunctionalProperty(Property property) {
        if (currentProperty.getURI().matches(property.getURI()))
            return currentProperty.isFunctionalProperty();
        else {
            Set<OntProperty> verificationSet = objectProperties;
            verificationSet.addAll(otherProperties);
            for (OntProperty ontProperty : verificationSet) {
                if (ontProperty.getURI().matches(property.getURI())) {
                    return ontProperty.isFunctionalProperty();
                }
            }
        }
        return false;
    }


    public static String getPropertyType(Property property)
    {
        /* If property is a variable */
        if (property.asNode().isVariable())
        {
            return "variable";
        }

        /* If property is a DatatypeProperty */
        else if (!property.asNode().isVariable() && isDatatypeProperty(property)) {
            return "datatypeProperty";
        }

        /* If property is an object property */
        else if (isObjectProperty(property)) {
            return "objectProperty";
        }

        /* if property is another property without type identified */
        else if(isOtherProperty(property) || findProperty(property))
        {
            return "otherProperty";

        }
        else
            return "notFound";
    }

    private static boolean findProperty(Property property)
    {
        OntModel ontoModel = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology(property.getNameSpace(), ontoModel);
        /* System.out.println("Size of datatypeProperties" + ontoModel.listOntProperties().toList().size());*/
        OntProperty ontProperty = ontoModel.getOntProperty(property.getURI());
        if (ontProperty != null)
        {
            currentProperty = ontProperty;
            return true;
        }
        else {
            return false;
        }
    }

    /** Verify if the property is contained in other properties **/
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
    private static boolean isDatatypeProperty(Property property) {
        boolean returnValue= false;
        if(setContains(property,datatypeProperties))
        {
            returnValue = true;
        }
        return returnValue;

    }

    /** Verify if the property is an object property **/
    private static boolean isObjectProperty(Property property) {
        boolean returnValue= false;
        if(setContains(property,objectProperties))
        {
            returnValue = true;
        }
        return returnValue;

    }

    /** Verify if the property is on other property **/
    private static boolean isOtherProperty(Property property) {
        boolean returnValue= false;
        if(setContains(property,otherProperties))
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
