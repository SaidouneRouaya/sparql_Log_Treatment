package MDPatternDetection;

import MDfromLogQueries.Util.Constants;
import org.apache.jena.graph.*;
import org.apache.jena.graph.impl.CollectionGraph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.sparql.core.BasicPattern;

import java.util.Iterator;
import java.util.List;

public class QueryConstruction {
    private BasicPattern bpModified; // QueryPattern after modification to build a construct query
    private BasicPattern bpWhere = new BasicPattern(); //Query pattern of the WHERE clause after adding rdf:type triples
    private BasicPattern bpWhereOptional = new BasicPattern();//Query pattern of the WHERE OPTIONAL clause after adding rdf:type triples
    private BasicPattern bpConstruct = new BasicPattern();//Query pattern of the CONSTRUCT clause to generate the graph automaticly
    private Property rdfTypeProp = new PropertyImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"); // Variable containing rdfType property
    private int i = 1; //Number of subject variables
    private int j = 1; // Number of predicate variables

    public BasicPattern getBpConstruct() {
        return bpConstruct;
    }

    public BasicPattern getBpWhere() {
        return bpWhere;
    }

    public BasicPattern getBpWhereOptional() {
        return bpWhereOptional;
    }

    /**
     * Fix the basic graph pattern to create an ontology to test with the dataset ontology
     **/

    /** Takes e_bpwhere the basic pattern of the query before modification and returns bpwhere the basic pattern of the query after modification
     * Same for optional
     **/
    public void completePatterns(BasicPattern e_bpwhere, BasicPattern e_bpWhereOptional)
    {
        this.bpWhere=  modifyBasicPattern(e_bpwhere);
        System.out.println(bpWhere.toString());
        this.bpWhereOptional = modifyBasicPattern(e_bpWhereOptional);
        afficher();
    }

    /** Takes a basic pattern and returns the basic pattern + every variable rdf:type ?type **/
    public BasicPattern modifyBasicPattern(BasicPattern bpat) {
        List<Triple> triples = bpat.getList();
        bpModified= new BasicPattern();
        bpModified = bpat;
        Resource subject;
        Graph graph = constructGraph(triples);
        Model queryModel = new ModelCom(graph); // We use a model to parse the graph by its subject -> properties -> objects
        Iterator nodeIterator = queryModel.listSubjects();
        while (nodeIterator.hasNext()) { // for every subject we verify wether it has an rdf:type property in the origin basic pattern
            Node subjectRDFTypeValue;
            //TODO voir si on ne doit pas le déplacer dans le haut de la classe
            subject = (Resource) nodeIterator.next();
            subjectRDFTypeValue = verifyRDFTypeProperty(subject, i, rdfTypeProp, "sub"); //verifies wether the subject had an rdf:type triple
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
    private boolean tripleExists(Triple theTriple)
    {
        List<Triple> triples = bpWhere.getList();
        int i =0;
        boolean exist = false;
        while (i<triples.size() && !exist)
        {
            if (triples.get(i).getSubject().matches(theTriple.getSubject()) && triples.get(i).getPredicate().matches(theTriple.getPredicate()))
                 exist =true;
            i++;
        }
        return exist;

    }

    /** Verifies for every Node of type Resource whether it has a rdf:type triple in the basic pattern **/
    public Node verifyRDFTypeProperty(Resource subject, int i, Property rdfTypeProp, String subobj) {
        Node subjectRDFTypeValue;
        Triple newTriple;
        if (!subject.hasProperty(rdfTypeProp)) {
            subjectRDFTypeValue = new Node_Variable(subobj + i);
            newTriple = new Triple(subject.asNode(), rdfTypeProp.asNode(), subjectRDFTypeValue);
            if (!tripleExists(newTriple))
            bpModified.add(newTriple);
        } else {
            subjectRDFTypeValue = subject.getProperty(rdfTypeProp).getObject().asNode();
        }
        return subjectRDFTypeValue;
    }


    private boolean isDatatypeProperty(Property property) {
        if(!property.asNode().isVariable())
            return Constants.getDatatypeProperties().contains(property.getNameSpace());
        else
            return false;
    }

    private boolean isObjectProperty(Property property) {
        return Constants.getObjectProperties().contains(property.getNameSpace());
    }

    /** Parses every property of a subject **/
    public void propertyIterate(Resource subject, Node subjectRDFTypeValue) {
        Property property;
        Iterator propertyIterator = subject.listProperties();
        Triple newTriple;
        while (propertyIterator.hasNext()) {
            Node objectRDFTypeValue;
            property = ((Statement) propertyIterator.next()).getPredicate();
            if (isDatatypeProperty(property)) {
                Iterator rangeIterator = Constants.getRangeofProperty(property).iterator();
                while (rangeIterator.hasNext()) {
                    objectRDFTypeValue = (Node) rangeIterator.next();
                    newTriple = new Triple(subjectRDFTypeValue, property.asNode(), objectRDFTypeValue);
                    bpConstruct.add(newTriple); // if it's a datatype property it searches for its range (type of object) and sets
                    // the triple of the construct with it
                }
            } else {
                objectRDFTypeValue = verifyRDFTypeProperty(subject.getProperty(property).getObject().asResource(), j, rdfTypeProp, "ob");
                newTriple = new Triple(subjectRDFTypeValue, property.asNode(), objectRDFTypeValue);
                bpConstruct.add(newTriple); // if it's an object property, it treats it as a subject
                j++;
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
        List<Triple> triplebfbcp = this.bpWhereOptional.getList();
        System.out.println("BP WHERE Optional : ");
        for (Triple t : triplebfbcp) {
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
