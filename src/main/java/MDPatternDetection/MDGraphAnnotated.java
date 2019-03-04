package MDPatternDetection;

import MDPatternDetection.AnnotationClasses.Annotations;
import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.Constants;
import com.google.common.base.Stopwatch;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.vocabulary.RDF;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class MDGraphAnnotated {


    private static Model associatedModel;
    private static Model mdModel;

    public static void main(String[] args) {

        new Constants(Declarations.dbPediaOntologyPath);
        Stopwatch stopwatch_unpersist = Stopwatch.createStarted();
        HashMap<String, Model> modelHashMap = TestTDB.unpersistModelsMap();
        stopwatch_unpersist.stop();
        System.out.println("\nTime elapsed for unpersist program is \t" + stopwatch_unpersist.elapsed(MILLISECONDS) + "\n\n");

        Stopwatch stopwatch_annotation = Stopwatch.createStarted();
        HashMap<String, Model> modelHashMapAnnotated = new HashMap<>();
        Iterator it = modelHashMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Model> pair = (Map.Entry) it.next();

            modelHashMapAnnotated.put(pair.getKey(), MDGraphAnnotated.construtMDGraph(pair.getKey(), pair.getValue()));
        }

        afficher(modelHashMapAnnotated);

        stopwatch_annotation.stop();
        System.out.println("\n Time elapsed for annotation program is " + stopwatch_annotation.elapsed(MILLISECONDS));

    }


    public static Model construtMDGraph(String subjectModel, Model model) {

        Resource subject = null;
        String propertyType;
        Statement statement;
        Property property;

        associatedModel = model;
        mdModel = associatedModel;

        Iterator<Resource> subjects = mdModel.listSubjects();
        while (subjects.hasNext() && subject == null) {
            Resource loopSubject = subjects.next();
            if (loopSubject.hasURI(subjectModel))
                subject = loopSubject;
        }

        if (subject != null) {
            // mark the subject as Fact
            subject.addProperty(RDF.type, Annotations.FACT.toString());

            // annotation of the other nodes
            Iterator propertyIterator = subject.listProperties();

            while (propertyIterator.hasNext()) {
                statement = (Statement) propertyIterator.next();
                property = statement.getPredicate();
                propertyType = Constants.getPropertyType(property);

                switch (propertyType) {
                    case ("datatypeProperty"): {
                        statement.getObject().asResource().addProperty(RDF.type, Annotations.FACTATTRIBUTE.toString());
                    }
                    break;
                    case ("objectProperty"): {

                        if (Constants.isFunctionalProperty(property)) {
                            statement.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSION.toString());
                        } else {
                            statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString());

                        }
                        addDimensionLevels(statement.getObject().asResource());
                    }
                    break;
                    case ("otherProperty"): {
                        // TODO Ajouter ce cas là
                        if (statement.getObject().asNode().getURI().matches("http://www.w3.org/2000/01/rdf-schema#Literal")) {
                            statement.getObject().asResource().addProperty(RDF.type, Annotations.FACTATTRIBUTE.toString());
                        } else {
                            // TODO sinon il faut demander au endpoint si c fonctionnel
                            statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString());
                            addDimensionLevels(statement.getObject().asResource());
                        }
                    }
                    break;
                    default:
                        break;
                }
            }
        }

        return model;
    }

    public static void addDimensionLevels(Resource dimension) {
        Statement statement;
        Property property;
        String propertyType;
        Iterator propertyIterator = dimension.listProperties();

        while (propertyIterator.hasNext()) {

            statement = (Statement) propertyIterator.next();
            property = statement.getPredicate();
            propertyType = Constants.getPropertyType(property);

            switch (propertyType) {
                case ("datatypeProperty"): {
                    statement.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSIONATTRIBUTE.toString());
                }
                break;
                case ("objectProperty"): {

                    if (Constants.isFunctionalProperty(property)) {
                        statement.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSIONLEVEL.toString());
                        statement.getObject().asResource().addProperty(new PropertyImpl(Annotations.PARENTLEVEL.toString()), dimension);
                    } else {
                        statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString());
                        statement.getObject().asResource().addProperty(new PropertyImpl(Annotations.PARENTLEVEL.toString()), dimension);
                    }
                    addDimensionLevels(statement.getObject().asResource());
                }
                break;
                case ("otherProperty"): {
                    //TODO Ajouter ce cas là
                    if (statement.getObject().asNode().getURI().matches("http://www.w3.org/2000/01/rdf-schema#Literal")) {
                        statement.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSIONATTRIBUTE.toString());
                    } else {
                        //TODO sinon il faut demander au endpoint si c fonctionnel
                        statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSIONLEVEL.toString());
                        statement.getObject().asResource().addProperty(new PropertyImpl(Annotations.PARENTLEVEL.toString()), dimension);
                    }
                }
                break;
                default:
                    break;
            }
        }
    }


    // TODO à enlever apres
    public static void afficher(HashMap<String, Model> modelHashMap) {

        Iterator it = modelHashMap.entrySet().iterator();

        System.out.println(" Afichage des résultats \n");


        while (it.hasNext()) {

            Map.Entry<String, Model> pair = (Map.Entry) it.next();

            System.out.println(" Fact: \t\t " + pair.getKey() + "\n");

            System.out.println(" Dimensions: \t\n ");

            Iterator<Statement> listStatements = pair.getValue().listStatements();
            while (listStatements.hasNext()) {

                Statement statement = listStatements.next();

                System.out.println(statement.toString());

            }

            System.out.println("\n______________________________________________________________________\n");

        }
    }


}
