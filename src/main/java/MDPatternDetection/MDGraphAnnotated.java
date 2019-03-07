package MDPatternDetection;

import MDPatternDetection.AnnotationClasses.Annotations;
import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.Constants;
import com.google.common.base.Stopwatch;
import org.apache.jena.rdf.model.*;
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

            modelHashMapAnnotated.put(pair.getKey(), MDGraphAnnotated.construtMDGraph2(pair.getKey(), pair.getValue()));
        }

        stopwatch_annotation.stop();
        System.out.println("\n Time elapsed for annotation program is " + stopwatch_annotation.elapsed(MILLISECONDS));


//        afficher(modelHashMap);

    }

    /*
        public static Model construtMDGraph(String subjectModel, Model model) {

            Resource subject = null;
            String propertyType;
            Statement statement;
            Property property;

           try {

            associatedModel = model;
            mdModel = associatedModel;

    //        Iterator<Resource> subjects = mdModel.listSubjects();

          subject=  mdModel.getResource(subjectModel);
           /* while (subjects.hasNext() && subject == null) {
                Resource loopSubject = subjects.next();
                if (loopSubject.hasURI(subjectModel))
                    subject = loopSubject;
            }*


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
                            System.out.println("dakhel datatype prop");
                            statement.getObject().asResource().addProperty(RDF.type, Annotations.FACTATTRIBUTE.toString());
                        }
                        break;
                        case ("objectProperty"): {
                            System.out.println("dakhel object prop");
                            if (Constants.isFunctionalProperty(property)) {
                                statement.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSION.toString());
                            } else {
                                statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString());

                            }

                           // addDimensionLevels(statement.getObject().asResource());
                            addDimensionLevels(statement);
                        }
                        break;
                        case ("otherProperty"): {
                            // TODO Ajouter ce cas là
                            System.out.println("dakhel other prop");
                            if (statement.getObject().asNode().getURI().matches("http://www.w3.org/2000/01/rdf-schema#Literal")) {
                                statement.getObject().asResource().addProperty(RDF.type, Annotations.FACTATTRIBUTE.toString());
                            } else {
                                // TODO sinon il faut demander au endpoint si c fonctionnel
                                statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString());
                               // addDimensionLevels(statement.getObject().asResource());
                                addDimensionLevels(statement);
                            }
                        }
                        break;
                        default:
                            break;
                    }
                }
            }
           }
           catch (Exception e){
               e.printStackTrace();
           }

            return mdModel;
        }
    */
    public static void addDimensionLevels(Resource dimension) {

    }
/*
    public static void addDimensionLevels(Statement state) {
        Statement statement;
        Property property;
        String propertyType;
        Resource dimension= state.getObject().asResource();
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
*/

    public static Model construtMDGraph2(String subjectModel, Model model) {

        Resource subject = null;
        String propertyType;
        Statement statement;
        Property property;

        try {

            //associatedModel = model;
            //mdModel = associatedModel;


            subject = model.getResource(subjectModel);


            if (subject != null) {
                // mark the subject as Fact
                subject.addProperty(RDF.type, Annotations.FACT.toString());
            }

            ResIterator resIterator = model.listSubjects();


            while (resIterator.hasNext()) {
                Resource node = resIterator.next();
                // for the fact node

                if (node.hasProperty(RDF.type, Annotations.FACT.toString())) {

                    // annotation of the other nodes
                    Iterator propertyIterator = node.listProperties();

                    while (propertyIterator.hasNext()) {

                        statement = (Statement) propertyIterator.next();
                        property = statement.getPredicate();
                        propertyType = Constants.getPropertyType(property);

                        switch (propertyType) {
                            case ("datatypeProperty"): {
                                System.out.println("dakhel datatype prop");
                                statement.getObject().asResource().addProperty(RDF.type, Annotations.FACTATTRIBUTE.toString());
                            }
                            break;
                            case ("objectProperty"): {
                                System.out.println("dakhel object prop");
                                if (Constants.isFunctionalProperty(property)) {
                                    statement.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSION.toString());
                                } else {
                                    statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString());

                                }

                            }
                            break;
                            case ("otherProperty"): {
                                // TODO Ajouter ce cas là
                                System.out.println("dakhel other prop");
                                if (statement.getObject().asNode().getURI().matches("http://www.w3.org/2000/01/rdf-schema#Literal")) {
                                    statement.getObject().asResource().addProperty(RDF.type, Annotations.FACTATTRIBUTE.toString());
                                } else {
                                    // TODO sinon il faut demander au endpoint si c fonctionnel
                                    statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString());

                                }
                            }
                            break;
                            default:
                                break;
                        }

                    }

                }// for the other nodes
                else {
                    if (node.hasProperty(RDF.type, Annotations.DIMENSION.toString()) || node.hasProperty(RDF.type, Annotations.DIMENSIONLEVEL.toString())) {

                        Statement state;
                        Property prop;
                        String propType;
                        Iterator propertyIterator = node.listProperties();

                        while (propertyIterator.hasNext()) {

                            state = (Statement) propertyIterator.next();
                            prop = state.getPredicate();
                            propType = Constants.getPropertyType(prop);

                            switch (propType) {
                                case ("datatypeProperty"): {
                                    state.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSIONATTRIBUTE.toString());
                                }
                                break;
                                case ("objectProperty"): {

                                    if (Constants.isFunctionalProperty(prop)) {
                                        state.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSIONLEVEL.toString());
                                        state.getObject().asResource().addProperty(new PropertyImpl(Annotations.PARENTLEVEL.toString()), node);
                                    } else {
                                        state.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString());
                                        state.getObject().asResource().addProperty(new PropertyImpl(Annotations.PARENTLEVEL.toString()), node);
                                    }
                                    addDimensionLevels(state.getObject().asResource());
                                }
                                break;
                                case ("otherProperty"): {
                                    //TODO Ajouter ce cas là
                                    if (state.getObject().asNode().getURI().matches("http://www.w3.org/2000/01/rdf-schema#Literal")) {
                                        state.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSIONATTRIBUTE.toString());
                                    } else {
                                        //TODO sinon il faut demander au endpoint si c fonctionnel
                                        state.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSIONLEVEL.toString());
                                        state.getObject().asResource().addProperty(new PropertyImpl(Annotations.PARENTLEVEL.toString()), node);
                                    }
                                }
                                break;
                                default:
                                    break;
                            }
                        }

                    }

                }


            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return mdModel;
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
