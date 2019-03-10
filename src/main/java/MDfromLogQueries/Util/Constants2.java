package MDfromLogQueries.Util;

import MDPatternDetection.OntologyFactory;
import MDPatternDetection.QueryExecutor;
import MDfromLogQueries.Declarations.Declarations;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


public class Constants2 {

    /**
     * This class is for constants' declaration
     **/

    private static HashSet<OntProperty> datatypeProperties = new HashSet<>();
    private static HashSet<OntProperty> objectProperties = new HashSet<>();
    private static HashSet<OntProperty> otherProperties = new HashSet<>();
    private static OntModel addedPropertiesOntology = ModelFactory.createOntologyModel();
    private static String ontologyPath;
    private static String defaultOntologiesDirectory = Declarations.defaultOntologiesDir;

    //TODO a changer pour le test d'autres endpoints (mettre en entrée)
    private static String endpoint = "https://dbpedia.org/sparql";

    public Constants2(String path) {
        ontologyPath = path;
        initObjectProperties();
        initDatatypeProperties();
        initDefaultProperties();
        System.out.println("\n" + otherProperties.size() + "*****************************");
    }


    public static HashSet<OntProperty> getDatatypeProperties() {
        return datatypeProperties;
    }

    public static HashSet<OntProperty> getObjectProperties() {
        return objectProperties;
    }

    public static HashSet<OntProperty> getOtherProperties() {
        return otherProperties;
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

    /**
     * Initialize a list of Object properties
     **/
    private static void initObjectProperties() {
        OntModel ontologie = ModelFactory.createOntologyModel();
        OntologyFactory.readOntology(ontologyPath, ontologie);
        objectProperties.addAll(ontologie.listObjectProperties().toList());
    }

    public static void main(String[] args) {
        new Constants2(Declarations.dbPediaOntologyPath);
        initDefaultProperties();
        for (OntProperty ontProperty : otherProperties) {
            try {
                System.out.println("the range : " + ontProperty.getRange());
            } catch (Exception e) {
                System.out.println("erreur");
            }
        }

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

    private static void addPropertiesToList(OntModel ontology) {
        int datatypePropertiesSize = ontology.listDatatypeProperties().toList().size();
        int objectPropertiesSize = ontology.listObjectProperties().toList().size();
        otherProperties.addAll(ontology.listOntProperties().toList());
        if (datatypePropertiesSize > 0 || objectPropertiesSize > 0) {
            if (objectPropertiesSize > 0) {
                objectProperties.addAll(ontology.listObjectProperties().toList());
                otherProperties.removeAll(ontology.listObjectProperties().toList());
            }

            if (datatypePropertiesSize > 0) {
                datatypeProperties.addAll(ontology.listDatatypeProperties().toList());
                otherProperties.removeAll(ontology.listDatatypeProperties().toList());
            }
        }
    }


    /**
     * Return properties set
     **/

    public static boolean askDatatypePropEndpoint(Property property, String endpoint) {
        String queryStr = "Ask { <" + property.getURI() + "> a <http://www.w3.org/2002/07/owl#DatatypeProperty>}";
        QueryExecutor queryExecutor = new QueryExecutor();
        return queryExecutor.executeQueryAsk(queryStr, endpoint);
    }


    public static void persistModel() {
        //writeModelInFile(Declarations.propertiesOntology,addedPropertiesOntology);
    }


    /**
     * Execute a query onto an ontology
     **/
    private static HashSet<String> simpleExecution(Query query, OntModel ontologie) {
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

    public static OntModel getAddedPropertiesOntology() {
        return addedPropertiesOntology;
    }

    public static void addModeltoOntology(Model model) {
        addedPropertiesOntology.add(model);
    }

    public static void addDatatypesProperties(List<OntProperty> propertyList) {
        datatypeProperties.addAll(propertyList);
    }

    public static void addObjectProperties(List<OntProperty> propertyList) {
        objectProperties.addAll(propertyList);
    }

    public static void addOtherProperties(List<OntProperty> propertyList) {
        otherProperties.addAll(propertyList);
    }


}
