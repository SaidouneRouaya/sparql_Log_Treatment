package MDfromLogQueries.Util;

import MDPatternDetection.OntologyFactory;
import MDPatternDetection.QueryExecutor;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConstantsUtil {
    private OntProperty currentProperty= null;
    private static String endpoint = "https://dbpedia.org/sparql";

    //à changer probablement en créant un nouveau type contenant la datatypeProperty et son ou ses range
    public Node getRangeofProperty(Property property) {
        if (currentProperty.
                getURI()
                .matches(property
                        .getURI())) {
            OntResource range = currentProperty.getRange();
            System.out.println("the range :"+range);
            if (range != null) {
                return range.asNode();
            }
        }
        else {
            Set<OntProperty> verificationSet= Constants2.getDatatypeProperties();
            verificationSet.addAll(Constants2.getOtherProperties());
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

    public Boolean isFunctionalProperty(Property property) {
        if (currentProperty.getURI().matches(property.getURI()))
            return currentProperty.isFunctionalProperty();
        else {
            Set<OntProperty> verificationSet = Constants2.getObjectProperties();
            verificationSet.addAll(Constants2.getOtherProperties());
            for (OntProperty ontProperty : verificationSet) {
                if (ontProperty.getURI().matches(property.getURI())) {
                    return ontProperty.isFunctionalProperty();
                }
            }
        }
        return false;
    }


    public String getPropertyType(Property property)
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
            return "notFound";//selectPropertyFromEnpoint(property,endpoint);
    }

    private boolean findProperty(Property property)
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
    public String selectPropertyFromEnpoint(Property property, String endpoint)
    {
        String queryStr = "Construct{ <"+property.getURI()+"> ?predicate ?object } " +
                "WHERE {<"+property.getURI()+"> ?predicate ?object  }";
        Query query = QueryFactory.create(queryStr);
        QueryExecutor queryExecutor = new QueryExecutor();
        try{
            Model model = queryExecutor.executeQueryConstruct(query,endpoint);
            Constants2.addModeltoOntology(model);
            currentProperty = Constants2.getAddedPropertiesOntology().getOntProperty(property.getURI());
            List<OntProperty> propertyList = new ArrayList<>();
            propertyList.add(currentProperty);
            if (currentProperty.isDatatypeProperty())
            {
                Constants2.addDatatypesProperties(propertyList);
                return "datatypeProperty";
            }
            else if (currentProperty.isObjectProperty())
            {
                Constants2.addObjectProperties(propertyList);
                return "objectProperty";
            }
            else
            {
                Constants2.addOtherProperties(propertyList);
                return "otherProperty";
            }
        }
        catch (Exception e)
        {
            return "notFound";
        }

    }
    /** Verify if the property is contained in other properties **/
    private boolean setContains(Property property, HashSet<OntProperty> set)
    {
        if (set.contains(property)) {
            for (OntProperty prop : set) {
                if (prop.getURI()
                        .matches(property
                                .getURI())) {
                    currentProperty = prop;
                    return true;
                }
            }
        }
        else
        {
            return false;
        }
        return false;
    }

    /** Verify if the property is a dataType property **/
    private boolean isDatatypeProperty(Property property) {
        boolean returnValue= false;
        if(setContains(property,Constants2.getDatatypeProperties()))
        {
            returnValue = true;
        }
        return returnValue;

    }

    /** Verify if the property is an object property **/
    private boolean isObjectProperty(Property property) {
        boolean returnValue= false;
        if(setContains(property,Constants2.getObjectProperties()))
        {
            returnValue = true;
        }
        return returnValue;

    }

    /** Verify if the property is on other property **/
    private boolean isOtherProperty(Property property) {
        boolean returnValue= false;
        if(setContains(property,Constants2.getOtherProperties()))
        {
            returnValue = true;
        }
        return returnValue;

    }

}
