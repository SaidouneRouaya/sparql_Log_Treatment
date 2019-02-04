package MDPatternDetection;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.CollectionGraph;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitorBase;
import org.apache.jena.sparql.algebra.OpWalker;
import org.apache.jena.sparql.algebra.op.OpBGP;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyOpVisitorBase extends OpVisitorBase {
    private Node subject;
    private Node predicate;
    private Node object;
    private List<Triple> triples = new ArrayList<>();
    private Graph graph;
    private Model model;

    public void myOpVisitorWalker(Op op) {
        OpWalker.walk(op, this);
    }

    @Override
    public void visit(final OpBGP opBGP) {
        final List<Triple> triples = opBGP.getPattern().getList();

        for (final Triple triple : triples) {
            System.out.println("Triple: " + triple.toString());
            subject = triple.getSubject();

            predicate = triple.getPredicate();
            object = triple.getObject();
            this.triples.add(triple);
        }
    }

    public void contructGraph() {
        graph = new CollectionGraph();
        for (Triple t : triples) {
            graph.add(t);
        }

        model = new ModelCom(graph);

      /*  Iterator nodeIterator = model.listSubjects();
        while (nodeIterator.hasNext())
        {
            System.out.println("Noeud : "+nodeIterator.next());
        }
        System.out.println("taille : "+graph.size());
        */

        getClasses();
    }

    public void getClasses() {
        Iterator graphIterator = model.listSubjects();
        Resource subject;
        while (graphIterator.hasNext()) {
            subject = ((ResIterator) graphIterator).next();

            if (subject.hasProperty(new PropertyImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))) {
                RDFNode object = model.listObjectsOfProperty(subject, new PropertyImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")).next();
                if (object.isResource()) {
                    System.out.println(object.toString());
                }
            }
        }
    }

    public ResultSet executeQuerySelect(String queryStr) {
        ResultSet results = null;
        try {
            Query query = QueryFactory.create(queryStr);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            results = qexec.execSelect();

            while (results.hasNext())
                System.out.println("Resultat " + results.next());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}