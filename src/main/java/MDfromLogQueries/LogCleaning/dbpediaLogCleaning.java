package MDfromLogQueries.LogCleaning;

import com.google.common.base.Stopwatch;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static MDfromLogQueries.Declarations.Declarations.LogDirectory;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class dbpediaLogCleaning {


    // private static final String TEST_LINE = "ip - - [28/Sep/2014 00:00:00 +0200] \"GET /sparql?query=SELECT+%3Fabstract+WHERE+%7B+%3Fs+rdfs%3Alabel+%27Completing%27%40en+.%0A%3Fs+dbpedia-owl%3Aabstract+%3Fabstract+.%0AFILTER+langMatches%28+lang%28%3Fabstract%29%2C+%27en%27%29%7D+LIMIT+1000&default-graph-uri=http://dbpedia.org&format=JSON HTTP/1.0\" 200 119 \"\" \"Java/1.6.0_51\" ";
    private static final Pattern PATTERN = Pattern.compile("[^\"]*\"(?:GET )?/sparql/?\\?([^\"\\s\\n]*)[^\"]*\".*");

    public static void main(String[] args) {

        Stopwatch stopwatch = Stopwatch.createStarted();



        try {
            /** Directory of logs parsing **/


              List<Path> filesInFolder =  Files.walk(Paths.get(LogDirectory))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());


            /** for each log file in the specified directory **/
            for (Path p : filesInFolder) {
                String chemin = p.toString();

                System.out.println("Vous avez saisi l'url: " + chemin);
            File logFile = new File(chemin);
            BufferedReader br = new BufferedReader(new FileReader(logFile));
            String line ="";
            int nb_line=0;
            int nb_rqst_notnull = 0;
            while ((line = br.readLine()) != null) {
                nb_line++;
                String requestStr = new LogParser().queryFromLogLine(line);

                System.out.println("\n"+line+"\n");
                System.out.println(requestStr);

                if (requestStr!=null)  nb_rqst_notnull++;
                    /** ecriture dans un fichier **/


                System.out.println( "Nombre de lignes dans le fichiers \t"+nb_line);
                System.out.println( "Nombre de requetes non null dans le fichiers \t"+nb_rqst_notnull);


            }}
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopwatch.stop();
        System.out.println("Time elapsed for the program is "+ stopwatch.elapsed(MILLISECONDS));

    }


    public  String queryFromLogLine(String line) {
        Matcher matcher = PATTERN.matcher(line);
        if (matcher.find()) {
            String requestStr = matcher.group(1);
            String queryStr = queryFromRequest(requestStr);
            return queryStr != null ? queryStr : requestStr;
        } else {
            return null;
        }
    }

    public String queryFromRequest(String requestStr) {
        List<NameValuePair> pairs = URLEncodedUtils.parse(requestStr, StandardCharsets.UTF_8);

        for (NameValuePair pair : pairs) {
            if ("query".equals(pair.getName())) {
                System.out.println("URL Unicode Utils   "+pair.getValue());
                return pair.getValue();
            }
        }
        return null;
    }



}


