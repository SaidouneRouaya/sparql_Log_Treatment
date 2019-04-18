package MDfromLogQueries.Util;

import MDPatternDetection.ExecutionClasses.QueryExecutor;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.OWL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConstantsUtil {
    private OntProperty currentProperty= null;


    //à changer probablement en créant un nouveau type contenant la datatypeProperty et son ou ses range
    public Node getRangeofProperty(Property property) {
        if (currentProperty.
                getURI()
                .matches(property
                        .getURI())) {
            OntResource range = currentProperty.getRange();
           // System.out.println("the range :"+range);
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
        OntModel ontoModel = ModelFactory.createOntologyModel(property.getNameSpace());
        // OntologyFactory.readOntology(, ontoModel);
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

    public String findPropertyEndpoint(String propertyStr, String endpoint) {
        String prop = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT DISTINCT ?o WHERE {<" + propertyStr + "> rdf:type ?o.}";
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, prop);
        ResultSet results = qexec.execSelect();
        QuerySolution querySolution;
        OntModel ont = ModelFactory.createOntologyModel();

        while (results.hasNext()) {
            querySolution = results.next();
            if (querySolution.get("?o").equals(OWL.ObjectProperty)) {
                Constants2.addObjectProperty(ont.createOntProperty(querySolution.get("?o").toString()));
                return "objectProperty";

            } else {
                if (querySolution.get("?o").equals(OWL.DatatypeProperty)) {
                    Constants2.addDatatypesProperty(ont.createOntProperty(querySolution.get("?o").toString()));
                    return "datatypeProperty";

                }
            }
        }
        return "";
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
