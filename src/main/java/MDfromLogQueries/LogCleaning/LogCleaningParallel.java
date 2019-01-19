package MDfromLogQueries.LogCleaning;
import MDfromLogQueries.Declarations.Declarations;
import com.google.common.base.Stopwatch;
import MDfromLogQueries.*;

import javax.lang.model.type.DeclaredType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.CopyOnWriteArrayList;

import static MDfromLogQueries.Declarations.Declarations.writingFilePath;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class LogCleaningParallel implements Runnable{

    private CopyOnWriteArrayList synchronizedList;
    private String logfile;

    public LogCleaningParallel (String filename, CopyOnWriteArrayList<String> list){
        this.logfile = filename;
        this.synchronizedList = list;
    }

    @Override
    public void run() {
        try {
            System.out.println("Vous avez saisi l'url: " + logfile);
            BufferedReader br = new BufferedReader(new FileReader(new File(logfile)));
            String line ="";
            int nb_line=0;
            int nb_rqst_notnull = 0;
            while ((line = br.readLine()) != null) {
                //  System.out.println("dans le fichier");
                nb_line++;
                String requestStr = new LogParser().queryFromLogLine(line);
                if (requestStr!=null)
                {nb_rqst_notnull++;
                    synchronizedList.add(requestStr);
                }
                   }
            System.out.println( "Nombre de lignes dans le fichiers \t"+nb_line);
           // list.forEach(e -> System.out.println(e));
            System.out.println( "Nombre de requetes non null dans le fichiers \t"+nb_rqst_notnull);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String art[]) throws IOException {

        Stopwatch stopwatch = Stopwatch.createStarted();

        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        String dir = Declarations.directoryPath;
        ExecutorService executor = Executors.newFixedThreadPool(8);
        crawlDirectoryAndProcessFiles(dir,executor,list);
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        }


        File fichier_log_Nettoye =new File(writingFilePath);
        BufferedWriter bw = null;
        try {
            if (!fichier_log_Nettoye.isFile()) fichier_log_Nettoye.createNewFile();
            bw = new BufferedWriter(new FileWriter(fichier_log_Nettoye, true));

            for (int i=0; i<list.size();i++ ) {

                bw.write(list.get(i) +"####");

                bw.flush();
            }
        }        catch (IOException e) {
            System.out.println("Impossible de creer le fichier");
        }finally {
            System.out.println("je suis dans le finally");
            bw.close();}
        stopwatch.stop();
        System.out.println("Time elapsed for the program is "+ stopwatch.elapsed(MILLISECONDS));


    }

    public static  void crawlDirectoryAndProcessFiles(String directory, Executor executor, CopyOnWriteArrayList<String> list ) {

        try {
            List<Path> filesInFolder = Files.walk(Paths.get(directory))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());

            /** for each log file in the specified directory **/
            for (Path p : filesInFolder) {
                executor.execute(new LogCleaningParallel(p.toString(),list));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





