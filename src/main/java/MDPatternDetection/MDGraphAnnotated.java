package MDPatternDetection;

import MDPatternDetection.AnnotationClasses.Annotations;
import MDPatternDetection.AnnotationClasses.Dimension;
import MDPatternDetection.AnnotationClasses.Fact;
import MDfromLogQueries.Util.Constants;
import com.google.common.base.Stopwatch;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.vocabulary.RDF;

import java.util.Iterator;
import java.util.Set;

import static MDfromLogQueries.Declarations.Declarations.syntaxValidFileTest;
import static java.util.concurrent.TimeUnit.SECONDS;

public class MDGraphAnnotated {
    private Model associatedModel;
    private Model mdModel;


    public void construtMDGraph(Model model, String sujectModel)
    {
        Resource subject= null;
        String propertyType;
        Statement statement;
        Property property;
        associatedModel = model;
        mdModel = associatedModel;
        Iterator<Resource> subjects = mdModel.listSubjects();
        while (subjects.hasNext() && subject==null)
        {
            Resource loopSubject = subjects.next();
            if (loopSubject.hasURI(sujectModel))
                subject = loopSubject;
        }

        if (subject != null)
        {
            subject.addProperty(RDF.type, Annotations.FACT.toString());
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

                        if (Constants.isFunctionalProperty(property))
                        {
                            statement.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSION.toString());
                        }
                        else
                        {
                            statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString());

                        }
                        addDimensionLevels(statement.getObject().asResource());
                    }
                    break;
                    case ("otherProperty"): {
                        //TODO Ajouter ce cas là
                        if (statement.getObject().asNode().getURI().matches("http://www.w3.org/2000/01/rdf-schema#Literal"))
                        {
                            statement.getObject().asResource().addProperty(RDF.type, Annotations.FACTATTRIBUTE.toString());
                        }
                        else
                        {
                            //TODO sinon il faut demander au endpoint si c fonctionnel
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

    }

    public void addDimensionLevels(Resource dimension)
    {
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

                    if (Constants.isFunctionalProperty(property))
                    {
                        statement.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSIONLEVEL.toString());
                        statement.getObject().asResource().addProperty(new PropertyImpl(Annotations.PARENTLEVEL.toString()),dimension);
                    }
                    else
                    {
                        statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString());
                        statement.getObject().asResource().addProperty(new PropertyImpl(Annotations.PARENTLEVEL.toString()),dimension);
                    }
                    addDimensionLevels(statement.getObject().asResource());
                }
                break;
                case ("otherProperty"): {
                    //TODO Ajouter ce cas là
                    if (statement.getObject().asNode().getURI().matches("http://www.w3.org/2000/01/rdf-schema#Literal"))
                    {
                        statement.getObject().asResource().addProperty(RDF.type, Annotations.DIMENSIONATTRIBUTE.toString());
                    }
                    else
                    {
                        //TODO sinon il faut demander au endpoint si c fonctionnel
                        statement.getObject().asResource().addProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSIONLEVEL.toString());
                        statement.getObject().asResource().addProperty(new PropertyImpl(Annotations.PARENTLEVEL.toString()),dimension);
                    }
                }
                break;
                default:
                    break;
            }
        }
    }

    public static void main(String[] args)  {
        Stopwatch stopwatch = Stopwatch.createStarted();
        MDGraphAnnotated mdGraphAnnotated = new MDGraphAnnotated();
        //TODO Ajouter un exemple de test ou tester sur le résultats des étapes précédentes
        // mdGraphAnnotated.construtMDGraph();

        stopwatch.stop();
        System.out.println("\n Time elapsed for the program is "+ stopwatch.elapsed(SECONDS));

    }


}
