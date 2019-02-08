package MDPatternDetection;


import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitorBase;
import org.apache.jena.sparql.algebra.OpWalker;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.expr.ExprList;

import java.util.List;

public class QueryPatternExtraction {
    private BasicPattern graphPattern = null;
    private BasicPattern graphOptionalPattern = null;

    public QueryPatternExtraction() {
    }

    //TODO à enlever et mettre une classe plus globale cella est valable pour une seule query (ajouter l'initialisation des Constants dans la classe globale)


    public BasicPattern getGraphOptionalPattern() {
        return graphOptionalPattern;
    }

    public BasicPattern getGraphPattern() {
        return graphPattern;
    }

    /**
     * Extracts the graph pattern of a query (where graph pattern, and optional graph pattern)
     **/
    public void extractGP(Query query) {
        OpBPVisitor opBPVisitor = new OpBPVisitor();
        try {
            opBPVisitor.OpBPVisitorWalker(Algebra.compile(query));
            if (opBPVisitor.getBgpopt() != null) {
                graphPattern = opBPVisitor.getBgp();

                graphOptionalPattern = opBPVisitor.getBgpopt();
            } else {
                graphPattern = opBPVisitor.getTemp();
                graphOptionalPattern = new BasicPattern();
            }
            //TODO to delete when everything works fine
            System.out.println("*******" + graphPattern);
            System.out.println("*********" + opBPVisitor.getBgpopt());

        } catch (Exception e) {
            System.out.println("C'est une erreur");
            e.printStackTrace();
        }

    }

    /**
     * Visitor of the op (which is the query en morceaux)
     **/
    private class OpBPVisitor extends OpVisitorBase {
        BasicPattern bgp;
        BasicPattern temp;
        BasicPattern bgpopt;
        ExprList expList;

        public BasicPattern getTemp() {
            return temp;
        }


        public void OpBPVisitorWalker(Op op) {
            OpWalker.walk(op, this);
            System.out.println("Fait");
        }

        /* Visits the basic graph pattern */
        @Override
        public void visit(final OpBGP opBGP) {
            this.temp = opBGP.getPattern();
            List<Triple> triples = this.temp.getList();
            for (Triple tr : triples) {
                System.out.println("Triplle :" + tr);
            }

        }

        /* Visits the optional basic graph pattern */
        @Override
        public void visit(final OpLeftJoin opLeftJoin) {
            OpBPVisitorWalker(opLeftJoin.getLeft()); // Not Optional pattern
            this.bgp = temp;
            OpBPVisitorWalker(opLeftJoin.getRight()); // optional pattern
            this.bgpopt = temp;
        }

        /* Visits the filter clause (not yet used) */
        @Override
        public void visit(OpFilter opFilter) {
            expList = opFilter.getExprs();
        }

        public BasicPattern getBgp() {
            return bgp;
        }

        public BasicPattern getBgpopt() {
            return bgpopt;
        }
    }



}
