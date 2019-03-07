package MDPatternDetection;

import MDfromLogQueries.Util.Constants;
import org.apache.jena.graph.*;
import org.apache.jena.graph.impl.CollectionGraph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.vocabulary.RDF;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class QueryConstruction {

    /**
     * This class completes the Basic pattern with missing rdf:type triples
     * and create a Construct basic pattern
     **/

    private BasicPattern bpModified; // QueryPattern after modification to build a construct query
    private BasicPattern bpWhere = new BasicPattern(); //Query pattern of the WHERE clause after adding rdf:type triples
    private BasicPattern bpConstruct = new BasicPattern();//Query pattern of the CONSTRUCT clause to generate the graph automaticly
    private Property rdfTypeProp = RDF.type;//new PropertyImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"); // Variable containing rdfType property
    private Set<Triple> existingTriples = new HashSet<>();
    private int i = 1; //Number of subject variables
    private int j = 1; // Number of predicate variables

    public BasicPattern getBpConstruct() {
        return bpConstruct;
    }

    public BasicPattern getBpWhere() {
        return bpWhere;
    }



    /** Takes e_bpwhere the basic pattern of the query before modification and returns bpwhere the basic pattern of the query after modification
     * Same for optional
     **/
    public void completePattern(BasicPattern e_bpwhere)
    {
        existingTriples.addAll(e_bpwhere.getList());
        this.bpWhere=  modifyBasicPattern(e_bpwhere);
        //System.out.println(bpWhere.toString());
        //afficher();
    }

    /** Takes a basic pattern and returns the basic pattern + every variable rdf:type ?type **/
    private BasicPattern modifyBasicPattern(BasicPattern bpat) {
        List<Triple> triples = bpat.getList();
        bpModified= new BasicPattern();
        //bpModified = bpat;
        Resource subject;
        Graph graph = constructGraph(triples);
        Model queryModel = new ModelCom(graph); // We use a model to parse the graph by its subject -> properties -> objects
        Iterator nodeIterator = queryModel.listSubjects();
        while (nodeIterator.hasNext()) { // for every subject we verify whether it has an rdf:type property in the origin basic pattern
            Node subjectRDFTypeValue;
            subject = (Resource) nodeIterator.next();
            subjectRDFTypeValue = verifyRDFTypeProperty(subject, i, rdfTypeProp, "sub"); //verifies whether the subject had an rdf:type triple
            i++;
            propertyIterate(subject, subjectRDFTypeValue); // parses the properties of the subject
        }
        return bpModified;
    }

    private Graph constructGraph(List<Triple> triples) {
        Graph graph = new CollectionGraph();
        for (Triple t : triples) {
            graph.add(t);
        }
        return graph;
    }

    /** Verifies whether the triple already  exists in the Where clause**/
    private Triple tripleExists(Triple theTriple)
    {
        Iterator<Triple> iterator = existingTriples.iterator();
        boolean exist = false;
        Triple  triple= null;
        while (iterator.hasNext() && !exist)
        {
            triple = iterator.next();
            if (triple.getSubject().matches(theTriple.getSubject()) && triple.getPredicate().matches(theTriple.getPredicate()))
                 exist =true;
        }
        if (exist)
        return triple;
        else
            return null;
    }

    /** Verifies for every Node of type Resource whether it has a rdf:type triple in the basic pattern **/
    private Node verifyRDFTypeProperty(Resource subject, int i, Property rdfTypeProp, String subobj) {
        Node subjectRDFTypeValue;
        Triple newTriple;
        Triple exists;
        if (!subject.hasProperty(rdfTypeProp)) {
            subjectRDFTypeValue = new Node_Variable(subobj + i);
            newTriple = new Triple(subject.asNode(), rdfTypeProp.asNode(), subjectRDFTypeValue);
            exists = tripleExists(newTriple);
            if (exists==null) {
                bpModified.add(newTriple);
                existingTriples.add(newTriple);
            }
            else
            {
                subjectRDFTypeValue = exists.getObject();
            }
        } else {
            subjectRDFTypeValue = subject.getProperty(rdfTypeProp).getObject().asNode();
        }
        return subjectRDFTypeValue;
    }

    /** Parses every property of a subject **/
    private void propertyIterate(Resource subject, Node subjectRDFTypeValue) {
        Property property;
        Iterator propertyIterator = subject.listProperties();
        Triple newTriple;
        String propertyType;
        while (propertyIterator.hasNext()) {
            Node objectRDFTypeValue = NodeFactory.createBlankNode();
            property = ((Statement) propertyIterator.next()).getPredicate();
            if (property.asNode().isVariable() || !property.getNameSpace().matches(rdfTypeProp.getNameSpace())) {
                propertyType = Constants.getPropertyType(property);
                switch (propertyType)
                {
                    case ("variable"):
                    {
                        if (subject.getProperty(property).getObject().isLiteral())
                        {
                            objectRDFTypeValue = NodeFactory.createURI("http://www.w3.org/2000/01/rdf-schema#Literal");
                        }
                        else {
                            objectRDFTypeValue = verifyRDFTypeProperty(subject.getProperty(property).getObject().asResource(), j, rdfTypeProp, "ob");
                            j++;
                        }// if it's an object property, it treats it as a subject
                    }
                    break;
                    case ("datatypeProperty"):
                    {
                        // if it's a datatype property it searches for its range (type of object) and sets
                        // the triple of the construct with it
                        objectRDFTypeValue = Constants.getRangeofProperty(property);
                        if (objectRDFTypeValue==null)
                        {
                            objectRDFTypeValue = NodeFactory.createURI("http://www.w3.org/2000/01/rdf-schema#Literal");
                        }
                    }
                    break;
                    case ("objectProperty"):
                    {
                        objectRDFTypeValue = verifyRDFTypeProperty(subject.getProperty(property).getObject().asResource(), j, rdfTypeProp, "ob");
                         // if it's an object property, it treats it as a subject
                        j++;
                    }
                    break;
                    case ("otherProperty"):
                    {
                        objectRDFTypeValue = Constants.getRangeofProperty(property);
                        if (objectRDFTypeValue==null)
                        {
                            if(subject.getProperty(property).getObject().isLiteral())
                                objectRDFTypeValue = NodeFactory.createURI("http://www.w3.org/2000/01/rdf-schema#Literal");
                            else {
                                objectRDFTypeValue = verifyRDFTypeProperty(subject.getProperty(property).getObject().asResource(), j, rdfTypeProp, "ob");
                                j++;
                            }

                        }
                        //TODO voir si ça ne pose pas de problème par rapport aux literaux typés : xsd:String, xsd:int
                        else if (!objectRDFTypeValue.getURI().matches("http://www.w3.org/2000/01/rdf-schema#Literal"))
                        {
                            objectRDFTypeValue = verifyRDFTypeProperty(subject.getProperty(property).getObject().asResource(), j, rdfTypeProp, "ob");
                            j++;
                        }
                    }
                    break;
                    case ("notFound"):
                    {
                        if (subject.getProperty(property).getObject().isLiteral())
                        {
                            objectRDFTypeValue = NodeFactory.createURI("http://www.w3.org/2000/01/rdf-schema#Literal");
                        }
                        else
                        {
                            objectRDFTypeValue = verifyRDFTypeProperty(subject.getProperty(property).getObject().asResource(), j, rdfTypeProp, "ob");
                            j++;
                        }

                    }
                    break;
                }
                newTriple = new Triple(subjectRDFTypeValue, property.asNode(), objectRDFTypeValue);
                bpConstruct.add(newTriple);
            }
        }
    }

    //TODO à enlever ki nefriwha
    private void afficher() {
        List<Triple> triplebbcp = this.bpWhere.getList();
        System.out.println("BP WHERE : ");
        for (Triple t : triplebbcp) {
            System.out.println(t.toString());
        }
        System.out.println("Fin BP WHERE : ");
        List<Triple> triplebbgcp = this.bpConstruct.getList();
        System.out.println("BP Construct : ");
        for (Triple t : triplebbgcp) {
            System.out.println(t.toString());
        }
        System.out.println("Fin BP Construct");
    }
}
