package MDPatternDetection;


import MDfromLogQueries.Util.FileOperation;
import com.google.common.base.Stopwatch;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitorBase;
import org.apache.jena.sparql.algebra.OpWalker;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.syntax.Element;

import java.util.ArrayList;
import java.util.List;

import static MDfromLogQueries.Declarations.Declarations.syntaxValidFile;
import static java.util.concurrent.TimeUnit.SECONDS;

public class QueryPatternExtraction {

    public QueryPatternExtraction() {
    }

    public BasicPattern extractGP (Query query){
        BasicPattern graphPattern =null;
        OpBGPVisitor opBGPVisitor = new OpBGPVisitor();
        try {
            opBGPVisitor.OpBGPVisitorWalker(Algebra.compile(query));
            graphPattern = opBGPVisitor.getBgp();

        }
        catch (Exception e)
        {
            System.out.println("C'est une erreur");
            e.printStackTrace();
        }
        return graphPattern ;

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
            lines = (ArrayList<String>) FileOperation.ReadFile(/*syntaxValidFile*/"C:\\Users\\KamilaB\\Desktop\\3CS\\Prototypage\\Step_1\\Fichiers_Resultat\\Fichier_Syntaxe_Valide_test.txt");
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

        /** Fix the basic graph pattern to create an ontology to test with the dataset ontology **/
        private BasicPattern modifyBasicPattern(BasicPattern bpat)
        {
            List<Triple> triples = bpat.getList();
            BasicPattern bpModifie = new BasicPattern();
            Node subject = null;
            Node object =  null;
            Property predicate = null;
            for (Triple triple : triples)
            {
                subject = triple.getSubject();
                object = triple.getObject();
                predicate = (Property) triple.getPredicate();
                if (!predicate.getNameSpace().matches("rdf|rdfs|owl"))
                {
                    if (!object.isLiteral()) {
                        bpModifie.add(triple);
                    }
                    else if(predicate instanceof DatatypeProperty)
                    {

                    }
                }

            }
            return  bpModifie;
        }
    }

}
