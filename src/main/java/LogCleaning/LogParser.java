package LogCleaning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import java.io.File;
import java.util.List;

    public class LogParser {

        // private static final String TEST_LINE = "ip - - [28/Sep/2014 00:00:00 +0200] \"GET /sparql?query=SELECT+%3Fabstract+WHERE+%7B+%3Fs+rdfs%3Alabel+%27Completing%27%40en+.%0A%3Fs+dbpedia-owl%3Aabstract+%3Fabstract+.%0AFILTER+langMatches%28+lang%28%3Fabstract%29%2C+%27en%27%29%7D+LIMIT+1000&default-graph-uri=http://dbpedia.org&format=JSON HTTP/1.0\" 200 119 \"\" \"Java/1.6.0_51\" ";
        private static final Pattern PATTERN = Pattern.compile("[^\"]*\"(?:GET )?/sparql/?\\?([^\"\\s]*)[^\"]*\".*");

        public static void main(String[] args) {
            try {
            /*System.out.println("Veuillez saisir le nom du file :");
            //System.in.read();
            Scanner sc = new Scanner(System.in);
            String chemin = sc.next();
            System.out.println("Vous avez saisi l'url: " + chemin);
            File file = new File(chemin);
            */
                String chemin = "C:\\Users\\pc\\Desktop\\PFE\\DataLog\\dbp351logs\\access.log-20100502";
                System.out.println("Vous avez saisi l'url: " + chemin);
                File file = new File(chemin);
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line ="";
                int nb_line=0;
                while ((line = br.readLine()) != null) {
                    nb_line++;
                    String requestStr = new LogParser().queryFromLogLine(line);
                    System.out.println(requestStr);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

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
                    return pair.getValue();
                }
            }
            return null;
        }




    }



