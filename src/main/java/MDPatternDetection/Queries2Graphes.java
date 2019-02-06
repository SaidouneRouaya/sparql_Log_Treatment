package MDPatternDetection;

import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.Constants;
import com.google.common.base.Stopwatch;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.Template;

import java.util.ArrayList;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Queries2Graphes {


    public Queries2Graphes() {
        //TODO change the path in case of using another query logs
        new Constants(Declarations.dbPediaOntologyPath); // init the constants tu use it next
    }

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<BasicPattern> PatternList = new ArrayList<>();
        Query query = null;
        try {
            /** Graph pattern extraction **/
            int nb_line = 0;
            int nb_GP = 0;
            int nb_nullGP = 0;
            BasicPattern bp;
            //lines = (ArrayList<String>) FileOperation.ReadFile(/*syntaxValidFile*/"C:\\Users\\KamilaB\\Desktop\\3CS\\Prototypage\\Step_1\\Fichiers_Resultat\\Fichier_Syntaxe_Valide_test.txt");
            QueryPatternExtraction QPE = new QueryPatternExtraction();
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
            query = QueryFactory.create("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT ?s where { ?s ?p ?o }");
            //System.out.println( "ligne \t"+query);

            try {
                QPE.extractGP(query);
                QueryConstruction qc = new QueryConstruction();
                qc.completePatterns(QPE.getGraphPattern(), QPE.getGraphOptionalPattern());
                query.setQueryConstructType();
                query.setConstructTemplate(new Template(qc.getBpConstruct()));
                query.setQueryPattern(new ElementTriplesBlock(QPE.getGraphPattern()));
                //  query.;
                System.out.println(QPE.getGraphPattern().toString() + "\n" + nb_GP);
                PatternList.add(QPE.getGraphPattern());
                nb_GP++;
            } catch (Exception e) {
                nb_nullGP++;
                e.printStackTrace();
            }

            System.out.println(" nombre de requetes : " + nb_line + "\t nombre de GP : " + nb_GP + "\t nombre de null GP " + nb_nullGP);
            System.out.println("taille liste " + PatternList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
        stopwatch.stop();
        System.out.println("\n Time elapsed for the program is " + stopwatch.elapsed(SECONDS));

    }
}
