package MDPatternDetection;

import MDPatternDetection.AnnotationClasses.Dimension;
import MDPatternDetection.AnnotationClasses.Fact;
import MDfromLogQueries.Util.Constants;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MDGraph {
    Model associatedModel;
    Fact fact;
    Set<Dimension> dimensions;
    Set<Dimension> nonFunctionalDimensions;


    public void construtMDGraph(Model model, String sujectModel)
    {
        Resource subject= null;
        String propertyType;
        Statement statement;
        Property property;
        associatedModel = model;
        Iterator<Resource> subjects = associatedModel.listSubjects();
        while (subjects.hasNext() && subject==null)
        {
            Resource loopSubject = subjects.next();
            if (loopSubject.hasURI(sujectModel))
                subject = loopSubject;
        }
        fact.setFactNode(subject);
        if (subject != null)
        {
            Iterator propertyIterator = subject.listProperties();
            while (propertyIterator.hasNext()) {
                statement = (Statement) propertyIterator.next();
                property = statement.getPredicate();
                propertyType = Constants.getPropertyType(property);
                switch (propertyType) {
                    case ("datatypeProperty"): {
                        fact.addAttribute(statement.asTriple());
                    }
                    break;
                    case ("objectProperty"): {

                        if (Constants.isFunctionalProperty(property))
                        {
                            dimensions.add(new Dimension(statement.getObject()));
                        }
                        else
                        {
                            nonFunctionalDimensions.add(new Dimension(statement.getObject()));
                        }

                    }
                    break;
                    case ("otherProperty"): {
                        //TODO Ajouter ce cas là
                    }
                    break;
                    default:
                        break;
                }
            }
        }

    }

    public void addDimensionLevels()
    {

    }



}
