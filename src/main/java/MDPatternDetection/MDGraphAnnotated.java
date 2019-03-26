package MDPatternDetection;

import MDPatternDetection.AnnotationClasses.Annotations;
import MDfromLogQueries.Util.Constants;
import MDfromLogQueries.Util.Constants2;
import MDfromLogQueries.Util.ConstantsUtil;
import com.google.common.base.Stopwatch;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.vocabulary.RDF;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static MDfromLogQueries.Declarations.Declarations.tdbDirectory;
import static MDfromLogQueries.Util.FileOperation.readModelFromFile;
import static java.util.concurrent.TimeUnit.SECONDS;

public class MDGraphAnnotated {
    //private Model associatedModel;
    //private Model mdModel;
    //   private String modelSubject;

    public MDGraphAnnotated(Model model, String modelsSubject) {

    }


    public static HashMap<String, Model> constructMDGraphs(HashMap<String, Model> hashMapModels) {
        // HashMap<String , Model > results= new HashMap<>();

        Iterator it = hashMapModels.entrySet().iterator();
        int i = 0;
        while (it.hasNext()) {

            Map.Entry<String, Model> pair = (Map.Entry) it.next();
            System.out.println(i++ + "\n");
            construtMDGraph(pair.getKey(), pair.getValue());

        }

        return hashMapModels;
    }


    public static void construtMDGraph(String modelSubject, Model model) {
        Resource subject = null;
        String propertyType;
        Statement statement;
        Property property;
        ConstantsUtil constantsUtil = new ConstantsUtil();
        //Iterator<Resource> subjects = mdModel.listSubjects();

        subject = model.getResource(modelSubject);

        /*while (subjects.hasNext() && subject==null)
        {
            Resource loopSubject = subjects.next();
            if (loopSubject.hasURI(modelSubject))
                subject = loopSubject;
        }*/

        if (subject != null) {
            subject.addProperty(RDF.type, Annotations.FACT.toString());
            List<Statement> propertyIterator = subject.listProperties().toList();
            for (Statement stat : propertyIterator) {
                statement = stat;
                property = statement.getPredicate();
                if (!property.equals(RDF.type)) {
                    propertyType = constantsUtil.getPropertyType(property);
                    //  System.out.println(" predicat :"+property+ "type dialha : "+propertyType);
                    switch (propertyType) {
                        case ("datatypeProperty"): {
                            statement.getObject().asResource().addProperty(RDF.type, Annotations.FACTATTRIBUTE.toString());
                        }
                        break;
                        case ("objectProperty"): {

                            if (constantsUtil.isFunctionalProperty(property)) {
                                statement.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSION.toString());
                            } else {
                                statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString());

                            }
                            addDimensionLevels(statement.getObject().asResource(),constantsUtil);
                        }
                        break;
                        default: {
                            //TODO Ajouter ce cas là
                            if (Constants2.askDatatypePropEndpoint(property, "http://linkedgeodata.org/sparql"/*"https://dbpedia.org/sparql"*/) || statement.getObject().asNode().getURI().matches("http://www.w3.org/2000/01/rdf-schema#Literal")) {
                                statement.getObject().asResource().addProperty(RDF.type, Annotations.FACTATTRIBUTE.toString());
                            } else {
                                //TODO sinon il faut demander au endpoint si c fonctionnel
                                statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString());
                                addDimensionLevels(statement.getObject().asResource(),constantsUtil);
                            }
                        }
                        break;
                    }
                }
            }
        }

         //return model;
    }

    public static void addDimensionLevels(Resource dimension, ConstantsUtil constantsUtil) {
        //Statement statement;
        Property property;
        String propertyType;
        List<Statement> propertyIterator = dimension.listProperties().toList();
        for (Statement statement : propertyIterator) {
            //statement = (Statement) propertyIterator.next();
            property = statement.getPredicate();
            if (!property.equals(RDF.type)) {
                propertyType = constantsUtil.getPropertyType(property);
                switch (propertyType) {
                    case ("datatypeProperty"): {
                        statement.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSIONATTRIBUTE.toString());
                    }
                    break;
                    case ("objectProperty"): {

                        if (constantsUtil.isFunctionalProperty(property)) {
                            statement.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSIONLEVEL.toString());
                            statement.getObject().asResource().addProperty(new PropertyImpl(Annotations.PARENTLEVEL.toString()), dimension);
                        } else {
                            statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString());
                            statement.getObject().asResource().addProperty(new PropertyImpl(Annotations.PARENTLEVEL.toString()), dimension);
                        }
                        addDimensionLevels(statement.getObject().asResource(),constantsUtil);
                    }
                    break;
                    default: {
                        //TODO Ajouter ce cas là
                        if (Constants2.askDatatypePropEndpoint(property, "https://dbpedia.org/sparql") || statement.getObject().asNode().getURI().matches("http://www.w3.org/2000/01/rdf-schema#Literal")) {
                            statement.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSIONATTRIBUTE.toString());
                        } else {
                            //TODO sinon il faut demander au endpoint si c fonctionnel
                            statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSIONLEVEL.toString());
                            statement.getObject().asResource().addProperty(new PropertyImpl(Annotations.PARENTLEVEL.toString()), dimension);
                        }
                    }
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        //MDGraphAnnotated mdGraphAnnotated = new MDGraphAnnotated();
        //TODO Ajouter un exemple de test ou tester sur le résultats des étapes précédentes
        Dataset dataset = TDBFactory.createDataset(tdbDirectory);
        String sujet = "http://dbpedia.org/class/yago/WikicatPopulatedPlacesInGegharkunikProvince";
        //Model testModel = ModelFactory.createModelForGraph(dataset.getNamedModel(sujet).getGraph());
        Model testModel = readModelFromFile("test.ttl");
        //writeModelInFile("test",testModel);

        // mdGraphAnnotated.construtMDGraph();

        //System.out.println(mdGraphAnnotated.getMdModel());
        HashMap<String, Model> hashMap = new HashMap<String, Model>();
        hashMap.put(sujet, testModel);

        MDGraphAnnotated mdGraphAnnotated = new MDGraphAnnotated(testModel, sujet);
        // hashMap.put("new",mdGraphAnnotated.getMdModel());
        Consolidation.afficherListInformations(hashMap);

        stopwatch.stop();
        System.out.println("\n Time elapsed for the program is " + stopwatch.elapsed(SECONDS));

    }


}
