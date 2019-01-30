package MDfromLogQueries.LogCleaning;

import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.FileOperation;
import com.google.common.base.Stopwatch;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import java.util.concurrent.CopyOnWriteArrayList;

import static MDfromLogQueries.Declarations.Declarations.directoryPath;
import static MDfromLogQueries.Declarations.Declarations.writingFilePath;
import static java.util.concurrent.TimeUnit.MILLISECONDS;




public class LogCleaningParallel2 {

 //   static ArrayList<String> linesList= new ArrayList<>();
    private CopyOnWriteArrayList synchronizedList;


      public LogCleaningParallel2(CopyOnWriteArrayList synchronizedList) {
        this.synchronizedList = synchronizedList;
    }

    public void queriesExtraction (ArrayList<String> linesList)
    {
        ExecutorService executor = Executors.newFixedThreadPool(12);

        for (final  String line : linesList) {executor.execute(() -> addQueryToSynchronizedListQueries(line));}

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        }

      /*  try {

            linesList.parallelStream().forEach((line)->addQueryToSynchronizedListQueries(line) );
        }
        catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void addQueryToSynchronizedListQueries(String line){

        String requestStr=LogParser.queryFromLogLine( line );

        if((requestStr)!=null)
        {
            synchronizedList.add(requestStr);

            FileOperation.nbQueriesTotalLines++;
        }

    }



    public static void main(String art[])  {

        Stopwatch stopwatch = Stopwatch.createStarted();

        LogCleaningParallel2 logClean = new LogCleaningParallel2(new CopyOnWriteArrayList() );
        ArrayList<String> list ;

        try {
            List<Path> filesInFolder = Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());

            /** for each log file in the specified directory **/
            for (Path p : filesInFolder) {
                list = (  ArrayList<String>) FileOperation.ReadFile(p.toString());
             logClean.queriesExtraction(list);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        FileOperation.WriteInFile(writingFilePath, logClean.synchronizedList);
        stopwatch.stop();
        System.out.println("Nombre total de lignes dans les fichiers :  "+FileOperation.nbTotalLines);
        System.out.println("Nombre total de requetes extraites :  "+FileOperation.nbQueriesTotalLines);
        System.out.println("\nTime elapsed for the program is "+ stopwatch.elapsed(MILLISECONDS));


    }

  /*  public static  void crawlDirectoryAndProcessFiles(String directory, Executor executor ) {

        try {
            List<Path> filesInFolder = Files.walk(Paths.get(directory))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());

                    ArrayList<String> list ;


            /** for each log file in the specified directory /
            for (Path p : filesInFolder) {

               list = (  ArrayList<String>) FileOperation.ReadFile(p.toString());

                executor.execute(new LogCleaningParallel(p.toString(),list));



            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}







