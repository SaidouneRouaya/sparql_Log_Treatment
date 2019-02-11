package MDPatternDetection;

import MDfromLogQueries.Util.Constants;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.CollectionGraph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.sparql.core.BasicPattern;

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
    private Property rdfTypeProp = new PropertyImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"); // Variable containing rdfType property
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
        System.out.println(bpWhere.toString());
        //afficher();
    }

    /** Takes a basic pattern and returns the basic pattern + every variable rdf:type ?type **/
    public BasicPattern modifyBasicPattern(BasicPattern bpat) {
        List<Triple> triples = bpat.getList();
        bpModified= new BasicPattern();
        //bpModified = bpat;
        Resource subject;
        Graph graph = constructGraph(triples);
        Model queryModel = new ModelCom(graph); // We use a model to parse the graph by its subject -> properties -> objects
        Iterator nodeIterator = queryModel.listSubjects();
        while (nodeIterator.hasNext()) { // for every subject we verify wether it has an rdf:type property in the origin basic pattern
            Node subjectRDFTypeValue;
            subject = (Resource) nodeIterator.next();
            subjectRDFTypeValue = verifyRDFTypeProperty(subject, i, rdfTypeProp, "sub"); //verifies wether the subject had an rdf:type triple
            i++;
            propertyIterate(subject, subjectRDFTypeValue); // parses the properties of the subject
        }
        return bpModified;
    }

    public Graph constructGraph(List<Triple> triples) {
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
    public Node verifyRDFTypeProperty(Resource subject, int i, Property rdfTypeProp, String subobj) {
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


    private boolean isDatatypeProperty(Property property) {

        return Constants.isDatatypeProperty(property);
    }

    private boolean isObjectProperty(Property property) {
        //return Constants.getObjectProperties().contains(property.getNameSpace());
        return true;
    }

    /** Parses every property of a subject **/
    public void propertyIterate(Resource subject, Node subjectRDFTypeValue) {
        Property property;
        Iterator propertyIterator = subject.listProperties();
        Triple newTriple;
        while (propertyIterator.hasNext()) {
            Node objectRDFTypeValue;
            property = ((Statement) propertyIterator.next()).getPredicate();
            if (property.asNode().isVariable() || !property.getNameSpace().matches(rdfTypeProp.getNameSpace())) {
                if (!property.asNode().isVariable() && isDatatypeProperty(property)) {
                    //Iterator rangeIterator = Constants.getRangeofProperty(property).iterator();
                    objectRDFTypeValue = Constants.getTemporareRange();//Constants.getRangeofProperty(property);
                   /* while (rangeIterator.hasNext()) {
                        objectRDFTypeValue = (Node) rangeIterator.next();*/
                        newTriple = new Triple(subjectRDFTypeValue, property.asNode(), objectRDFTypeValue);
                        bpConstruct.add(newTriple); // if it's a datatype property it searches for its range (type of object) and sets
                        // the triple of the construct with it
                    //}
                } else {
                    objectRDFTypeValue = verifyRDFTypeProperty(subject.getProperty(property).getObject().asResource(), j, rdfTypeProp, "ob");
                    newTriple = new Triple(subjectRDFTypeValue, property.asNode(), objectRDFTypeValue);
                    bpConstruct.add(newTriple); // if it's an object property, it treats it as a subject
                    j++;
                }
            }
            /*else
            {
                newTriple = new Triple(subjectRDFTypeValue, property.asNode(), subject.getProperty(property).getObject().asNode());
                bpConstruct.add(newTriple);
            }*/
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
