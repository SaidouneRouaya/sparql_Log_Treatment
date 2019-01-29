package MDPatternDetection;

import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.FileOperation;
import com.google.common.base.Stopwatch;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.engine.optimizer.Pattern;
import org.apache.jena.sparql.syntax.Element;


import javax.sound.midi.Soundbank;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import static MDfromLogQueries.Declarations.Declarations.syntaxValidFile;
import static MDfromLogQueries.Declarations.Declarations.writingDedupFilePath;
import static java.util.concurrent.TimeUnit.SECONDS;

public class QueryPatternExtraction {

    public QueryPatternExtraction() {
    }

    public Element extractGP (Query query){
        Element element=null;
        try {
            element = query.getQueryPattern();

        }
        catch (Exception e)
        {
            System.out.println("C'est une erreur");
            e.printStackTrace();
        }
        return element ;

    }


    public static void main(String[] args)  {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<String> validQueryList = new ArrayList<>();
        Query query = null;
        try {


           /* File syntaxValidFile = new File(Declarations.syntaxValidFile);
            BufferedReader br = new BufferedReader(new FileReader(syntaxValidFile));
            //String line ="";
            */
           /** Graph pattern extraction **/
           int nb_line=0;
           int nb_GP=0;
           int nb_nullGP=0;
            lines = (ArrayList<String>) FileOperation.ReadFile(syntaxValidFile);
            QueryPatternExtraction QPE= new QueryPatternExtraction();
            for (String line : lines){
                nb_line++;
                query = QueryFactory.create(line);

              //  System.out.println( "ligne \t"+nb_line);

                try {
                    System.out.println( QPE.extractGP(query).toString()+"\n");
                    nb_GP++;
                } catch (Exception e){


                    nb_nullGP++;
                    e.printStackTrace();
                }

            }

            System.out.println(" nombre de requetes : "+nb_line+"\t nombre de GP : "+nb_GP+"\t nombre de null GP "+nb_nullGP);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        stopwatch.stop();
        System.out.println("\n Time elapsed for the program is "+ stopwatch.elapsed(SECONDS));

    }


}
