package MDPatternDetection;


import MDfromLogQueries.Util.FileOperation;
import com.google.common.base.Stopwatch;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.CollectionGraph;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitorBase;
import org.apache.jena.sparql.algebra.OpWalker;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.expr.ExprList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static MDfromLogQueries.Declarations.Declarations.syntaxValidFileTest;
import static java.util.concurrent.TimeUnit.SECONDS;

public class QueryPatternExtraction {

    public QueryPatternExtraction() {
    }

    public static void main(String[] args)  {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ArrayList<String> lines;
        ArrayList<BasicPattern> PatternList = new ArrayList<>();
        Query query;
        try {

           /** Graph pattern extraction **/
           int nb_line=0;
           int nb_GP=0;
           int nb_nullGP=0;
           BasicPattern bp;
            lines = (ArrayList<String>) FileOperation.ReadFile(syntaxValidFileTest);
            QueryPatternExtraction QPE= new QueryPatternExtraction();
            for (String line : lines){
                nb_line++;
                query = QueryFactory.create(line);
                //System.out.println( "ligne \t"+query);

                try {
                    bp =QPE.extractGP(query);
                    System.out.println( bp.toString()+"\n"+nb_GP);
                    PatternList.add(bp);
                    nb_GP++;
                } catch (Exception e){
                    nb_nullGP++;
                    e.printStackTrace();
                }

            }

            System.out.println(" nombre de requetes : "+nb_line+"\t nombre de GP : "+nb_GP+"\t nombre de null GP "+nb_nullGP);
            System.out.println("taille liste "+PatternList.size());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        stopwatch.stop();
        System.out.println("\n Time elapsed for the program is "+ stopwatch.elapsed(SECONDS));

    }

    public BasicPattern extractGP(Query query) {

        OpBGPVisitor opBGPVisitor = new OpBGPVisitor();
        try {
            opBGPVisitor.OpBGPVisitorWalker(Algebra.compile(query));

            return opBGPVisitor.getBgp();

        } catch (Exception e) {
            System.out.println("C'est une erreur");
            e.printStackTrace();
            return null;
        }
    }

    private class OpBGPVisitor extends OpVisitorBase {
        BasicPattern bgp;
        ExprList expList;

        public void OpBGPVisitorWalker(Op op) {
            OpWalker.walk(op, this);
            System.out.println("Fait");
        }

        @Override
        public void visit(final OpBGP opBGP) {
            this.bgp = opBGP.getPattern();

        }

        @Override
        public void visit(OpFilter opFilter) {
            expList = opFilter.getExprs();
        }

        public BasicPattern getBgp() {
            return bgp;
        }
    }

        /** Fix the basic graph pattern to create an ontology to test with the dataset ontology **/
        private BasicPattern modifyBasicPattern(BasicPattern bpat)
        {
            List<Triple> triples = bpat.getList();
            // Represents the where Basic Pattern is the construct query after adding new triples
            BasicPattern bpModified = bpat ;
            BasicPattern bpConstruct = new BasicPattern();
            Property rdfTypeProp = new PropertyImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
            Resource subject = null;
            Node object =  null;
            Property predicate = null;
            /*Triple newTriple = null;
            for (Triple triple : triples)
            {
                subject = triple.getSubject();
                object = triple.getObject();
                predicate = (Property) triple.getPredicate();

            }*/
            Graph graph = constructGraph(triples);
            Model queryModel = new ModelCom(graph);
            Iterator nodeIterator = queryModel.listSubjects();
            while (nodeIterator.hasNext())
            {
                //  model.getRDFNode(nodeIterator.next().getSubject().asNode());
                Node subjecRDFTypeValue = null;
                int i = 1;
                subject = (Resource) nodeIterator.next();
                if (!subject.hasProperty(rdfTypeProp))
                {
                    subjecRDFTypeValue = new Node_Variable("sub"+i);
                    bpModified.add(new Triple(subject.asNode(),rdfTypeProp.asNode(),subjecRDFTypeValue));
                    i++;
                }
                else{
                    subjecRDFTypeValue = subject.getProperty(rdfTypeProp).getObject().asNode();
                }
                //TODO Faire une fonction pour parcourir les propriétés et les objets
                Iterator propertyIterator = subject.listProperties();
                while (propertyIterator.hasNext())
                {

                }
//TODO Ajouter le subjectRDFTypeValue dans la clause de construct

            }
            return  bpModified;
        }
        public Graph constructGraph(List<Triple> triples)
        {
            Graph graph = new CollectionGraph();
            for (Triple t: triples ) {
                graph.add(t);
            }
            return graph;
        }


}
