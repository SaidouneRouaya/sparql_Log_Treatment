package MDPatternDetection;


import com.google.common.base.Stopwatch;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitorBase;
import org.apache.jena.sparql.algebra.OpWalker;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.Template;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

public class QueryPatternExtraction {


    public QueryPatternExtraction() {
    }

    public static void main(String[] args)  {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<BasicPattern> PatternList = new ArrayList<>();
        Query query = null;
        try {

           /** Graph pattern extraction **/
           int nb_line=0;
           int nb_GP=0;
           int nb_nullGP=0;
           BasicPattern bp;
            //lines = (ArrayList<String>) FileOperation.ReadFile(/*syntaxValidFile*/"C:\\Users\\KamilaB\\Desktop\\3CS\\Prototypage\\Step_1\\Fichiers_Resultat\\Fichier_Syntaxe_Valide_test.txt");
            QueryPatternExtraction QPE= new QueryPatternExtraction();
           /* for (String line : lines){
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

            }*/
            query = QueryFactory.create("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT ?s where { ?s ?p ?o Optional { ?s rdf:type ?o}}");
            //System.out.println( "ligne \t"+query);

            try {
                bp = QPE.extractGP(query);
                QueryConstruction qc = new QueryConstruction();
                qc.modifyBasicPattern(bp);
                query.setQueryConstructType();
                query.setConstructTemplate(new Template(qc.getBpConstruct()));
                query.setQueryPattern(new ElementTriplesBlock(bp));
              //  query.;
                System.out.println(bp.toString() + "\n" + nb_GP);
                PatternList.add(bp);
                nb_GP++;
            } catch (Exception e) {
                nb_nullGP++;
                e.printStackTrace();
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
        BasicPattern graphPattern = null;
        OpBGPVisitor opBGPVisitor = new OpBGPVisitor();
        try {
            opBGPVisitor.OpBGPVisitorWalker(Algebra.compile(query));
            graphPattern = opBGPVisitor.getBgp();
            System.out.println("*******"+graphPattern);
            System.out.println("*********"+opBGPVisitor.getBgpopt());

        } catch (Exception e) {
            System.out.println("C'est une erreur");
            e.printStackTrace();
        }
        return graphPattern;

    }

    private class OpBGPVisitor extends OpVisitorBase {
        BasicPattern bgp;
        BasicPattern temp;
        BasicPattern bgpopt;
        ExprList expList;

        public void OpBGPVisitorWalker(Op op) {
            OpWalker.walk(op, this);
            System.out.println("Fait");
        }

        @Override
        public void visit(final OpBGP opBGP) {
            this.temp = opBGP.getPattern();
            List<Triple> triples =this.temp.getList();
            for (Triple tr : triples)
            {
                System.out.println("Triplle :" + tr);
            }

        }

        @Override
        public void visit(final OpLeftJoin opLeftJoin) {
            OpBGPVisitorWalker(opLeftJoin.getLeft());
            this.bgp = temp;
            OpBGPVisitorWalker(opLeftJoin.getRight());
            this.bgpopt = temp;
        }

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
