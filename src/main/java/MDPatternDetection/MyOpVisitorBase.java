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
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementPathBlock;

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
    private BasicPattern bp = new BasicPattern();
    private BasicPattern basicPattern = new BasicPattern();

    private ElementPathBlock e;

    public Element myOpVisitorWalker(Op op, BasicPattern BP) {

        bp = BP;
        OpWalker.walk(op, this);


        return e;

    }
    public void myOpVisitorWalker(Op op) {

        OpWalker.walk(op, this);

    }

    @Override
    public void visit(final OpBGP opBGP) {
        Iterator<Triple> BP_iterator = basicPattern.getList().iterator();
        while (BP_iterator.hasNext()) {
            Triple t = BP_iterator.next();

            opBGP.getPattern().add(t);
            e.addTriplePath(new TriplePath(t));
        }
    }


    @Override
    public void visit(final OpLeftJoin opLeftJoin) {
        basicPattern = bp;
        System.out.println("je sus dans left join");
        myOpVisitorWalker(opLeftJoin.getLeft()); // Not Optional pattern
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