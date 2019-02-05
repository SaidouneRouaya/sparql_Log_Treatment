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

import java.util.Iterator;
import java.util.List;

public class QueryConstruction {
    Constants constants = new Constants();
    private BasicPattern bpModified; // QueryPattern after modification to build a construct query
    private BasicPattern bpWhere = new BasicPattern();
    private BasicPattern bpWhereOptional = new BasicPattern();
    private BasicPattern bpConstruct = new BasicPattern();
    private Property rdfTypeProp = new PropertyImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

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

    public void completePatterns(BasicPattern e_bpwhere, BasicPattern e_bpWhereOptional)
    {
       this.bpWhere=  modifyBasicPattern(e_bpwhere);
       this.bpWhereOptional = modifyBasicPattern(e_bpWhereOptional);
        afficher();
    }
    public BasicPattern modifyBasicPattern(BasicPattern bpat) {
        List<Triple> triples = bpat.getList();
        bpModified= new BasicPattern();
        // Represents the where Basic Pattern is the construct query after adding new triples
        bpModified = bpat;
        Resource subject;
        Graph graph = constructGraph(triples);
        Model queryModel = new ModelCom(graph);
        Iterator nodeIterator = queryModel.listSubjects();
        while (nodeIterator.hasNext()) {
            Node subjectRDFTypeValue = null;
            int i = 1;
            subject = (Resource) nodeIterator.next();
            subjectRDFTypeValue = verifyRDFTypeProperty(subject, i, rdfTypeProp, "sub");
            // La ligne en dessous risque de duplication des graph pattern mais c'est le seul moyen pour la réutilisation de la fonction
            propertyIterate(subject, subjectRDFTypeValue);
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

    public Node verifyRDFTypeProperty(Resource subject, int i, Property rdfTypeProp, String subobj) {
        Node subjectRDFTypeValue;
        if (!subject.hasProperty(rdfTypeProp)) {
            subjectRDFTypeValue = new Node_Variable(subobj + i);
            bpModified.add(new Triple(subject.asNode(), rdfTypeProp.asNode(), subjectRDFTypeValue));
        } else {
            subjectRDFTypeValue = subject.getProperty(rdfTypeProp).getObject().asNode();
        }
        return subjectRDFTypeValue;
    }

    private boolean isDatatypeProperty(Property property) {
        if(!property.asNode().isVariable())
        return constants.getDatatypeProperties().contains(property.getNameSpace());
        else
            return false;
    }

    private boolean isObjectProperty(Property property) {
        return constants.getObjectProperties().contains(property.getNameSpace());
    }

    public void propertyIterate(Resource subject, Node subjectRDFTypeValue) {
        Property property;
        Iterator propertyIterator = subject.listProperties();
        int j = 1;
        Triple newTriple;
        while (propertyIterator.hasNext()) {
            Node objectRDFTypeValue;
            property = ((Statement) propertyIterator.next()).getPredicate();
            if (isDatatypeProperty(property)) {
                Iterator rangeIterator = constants.getRangeofProperty(property).iterator();
                while (rangeIterator.hasNext()) {
                    objectRDFTypeValue = (Node) rangeIterator.next();
                    newTriple = new Triple(subjectRDFTypeValue, property.asNode(), objectRDFTypeValue);
                    bpConstruct.add(newTriple);
                }
            } else {
                objectRDFTypeValue = verifyRDFTypeProperty(subject.getProperty(property).getObject().asResource(), j, rdfTypeProp, "ob");
                newTriple = new Triple(subjectRDFTypeValue, property.asNode(), objectRDFTypeValue);
                bpConstruct.add(newTriple);
                j++;
            }
        }
    }

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
