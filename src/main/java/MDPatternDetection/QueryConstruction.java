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
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.sparql.core.BasicPattern;

import java.util.Iterator;
import java.util.List;

public class QueryConstruction {
    Constants constants = new Constants();
    private BasicPattern bpModified; // QueryPattern after modification to build a construct query
    private BasicPattern bpConstruct = new BasicPattern();
    private Property rdfTypeProp = new PropertyImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

    /**
     * Fix the basic graph pattern to create an ontology to test with the dataset ontology
     **/
    public void modifyBasicPattern(BasicPattern bpat) {
        List<Triple> triples = bpat.getList();
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
        afficher();
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
        return constants.getDatatypeProperties().contains(property.getNameSpace());
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
            property = (Property) propertyIterator.next();

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
        List<Triple> triplebbcp = bpModified.getList();
        for (Triple t : triplebbcp) {
            System.out.println(t.toString());
        }
        List<Triple> triplebbgcp = bpConstruct.getList();
        for (Triple t : triplebbgcp) {
            System.out.println(t.toString());
        }
    }
}
