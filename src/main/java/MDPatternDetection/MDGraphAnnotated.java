package MDPatternDetection;

import MDPatternDetection.AnnotationClasses.Annotations;
import MDfromLogQueries.Util.Constants;
import com.google.common.base.Stopwatch;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb2.TDB2;
import org.apache.jena.vocabulary.RDF;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static MDfromLogQueries.Declarations.Declarations.tdbDirectory;
import static MDfromLogQueries.Util.FileOperation.readModelFromFile;
import static MDfromLogQueries.Util.FileOperation.readModelsFromFile;
import static MDfromLogQueries.Util.FileOperation.writeModelInFile;
import static java.util.concurrent.TimeUnit.SECONDS;

public class MDGraphAnnotated {
    private Model associatedModel;
    private Model mdModel;
    private String modelSubject;

    public MDGraphAnnotated(Model model, String modelsSubject)
    {
        associatedModel = model;
        mdModel = associatedModel;
        modelSubject = modelsSubject;
        construtMDGraph();
    }

    public Model getMdModel() {
        return mdModel;
    }

    public void construtMDGraph()
    {
        Resource subject= null;
        String propertyType;
        Statement statement;
        Property property;
        //Iterator<Resource> subjects = mdModel.listSubjects();
        subject = mdModel.getResource(modelSubject);
        /*while (subjects.hasNext() && subject==null)
        {
            Resource loopSubject = subjects.next();
            if (loopSubject.hasURI(modelSubject))
                subject = loopSubject;
        }*/

        if (subject != null)
        {
            subject.addProperty(RDF.type, Annotations.FACT.toString());
            List<Statement> propertyIterator = subject.listProperties().toList();
            for (Statement stat : propertyIterator) {
                statement = stat;
                property = statement.getPredicate();
                if (!property.equals(RDF.type)) {
                    propertyType = Constants.getPropertyType(property);
                    System.out.println(" predicat :"+property+ "type dialha : "+propertyType);
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
                        default: {
                            //TODO Ajouter ce cas là
                            if (Constants.askDatatypePropEndpoint(property, "https://dbpedia.org/sparql") || statement.getObject().asNode().getURI().matches("http://www.w3.org/2000/01/rdf-schema#Literal")) {
                                statement.getObject().asResource().addProperty(RDF.type, Annotations.FACTATTRIBUTE.toString());
                            } else {
                                //TODO sinon il faut demander au endpoint si c fonctionnel
                                statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString());
                                addDimensionLevels(statement.getObject().asResource());
                            }
                        }
                        break;
                    }
                }
            }
        }

    }

    public void addDimensionLevels(Resource dimension)
    {
        //Statement statement;
        Property property;
        String propertyType;
        List<Statement> propertyIterator = dimension.listProperties().toList();
        for (Statement statement : propertyIterator) {
            //statement = (Statement) propertyIterator.next();
            property = statement.getPredicate();
            if (!property.equals(RDF.type)) {
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
                    default : {
                        //TODO Ajouter ce cas là
                        if (Constants.askDatatypePropEndpoint(property, "https://dbpedia.org/sparql") || statement.getObject().asNode().getURI().matches("http://www.w3.org/2000/01/rdf-schema#Literal")) {
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

    public static void main(String[] args)  {
        Stopwatch stopwatch = Stopwatch.createStarted();
        //MDGraphAnnotated mdGraphAnnotated = new MDGraphAnnotated();
        //TODO Ajouter un exemple de test ou tester sur le résultats des étapes précédentes
        Dataset dataset = TDBFactory.createDataset(tdbDirectory);
        String sujet= "http://dbpedia.org/class/yago/WikicatPopulatedPlacesInGegharkunikProvince";
        //Model testModel = ModelFactory.createModelForGraph(dataset.getNamedModel(sujet).getGraph());
        Model testModel = readModelFromFile("test.ttl");
        //writeModelInFile("test",testModel);

        // mdGraphAnnotated.construtMDGraph();

        //System.out.println(mdGraphAnnotated.getMdModel());
        HashMap<String,Model> hashMap = new HashMap<String, Model>();
        hashMap.put(sujet,testModel);

        MDGraphAnnotated mdGraphAnnotated = new MDGraphAnnotated(testModel,sujet);
        hashMap.put("new",mdGraphAnnotated.getMdModel());
        TestConsolidation2.afficherListInformations(hashMap);

        stopwatch.stop();
        System.out.println("\n Time elapsed for the program is "+ stopwatch.elapsed(SECONDS));

    }


}
