package MDfromLogQueries.LogCleaning;
import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.FileOperation;
import com.google.common.base.Stopwatch;


import javax.lang.model.type.DeclaredType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
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
            Collection<String> list = FileOperation.ReadFile(logfile);
          String requestStr="";
          int numberOfQueriesFile=0;


             for (String line : list)
            {
                requestStr = new LogParser().queryFromLogLine( line );
                if (requestStr!=null) {
                    synchronizedList.add(requestStr);
                numberOfQueriesFile++;
                }

            }
            FileOperation.nbQueriesTotalLines+= numberOfQueriesFile;
        }
         catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void main(String art[])  {

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

        FileOperation.WriteInFile(writingFilePath, list);
        stopwatch.stop();
        System.out.println("Nombre total de lignes dans les fichiers :  "+FileOperation.nbTotalLines);
        System.out.println("Nombre total de requetes extraites :  "+FileOperation.nbQueriesTotalLines);
        System.out.println("\nTime elapsed for the program is "+ stopwatch.elapsed(MILLISECONDS));


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





