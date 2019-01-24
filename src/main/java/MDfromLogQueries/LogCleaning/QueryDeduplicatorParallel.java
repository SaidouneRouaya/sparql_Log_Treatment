package MDfromLogQueries.LogCleaning;

import MDfromLogQueries.Util.FileOperation;
import com.google.common.base.Stopwatch;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.*;


import static MDfromLogQueries.Declarations.Declarations.cleanedQueriesFile;
import static MDfromLogQueries.Declarations.Declarations.writingDedupFilePath;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class QueryDeduplicatorParallel implements Runnable{
    private CopyOnWriteArraySet querySet;
    private String line;
    private int nb_line;

    public QueryDeduplicatorParallel(CopyOnWriteArraySet<String> set, String line){
        this.querySet = set;
        this.line = line;
    }
    @Override
    public void run() {
        if (!querySet.contains(line))
        {
            querySet.add(line);
            nb_line++;
            System.out.println("ligne numero "+nb_line);
        }
    }
    public static void main(String[] args) throws IOException{

        Stopwatch stopwatch = Stopwatch.createStarted();

        CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<>();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        crawlFileNProcessLines(executor,set);
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        }
        System.out.println("nombre de ligne dans le set :"+set.size());

        FileOperation.WriteInFile(writingDedupFilePath, set);
        stopwatch.stop();
        System.out.println("Time elapsed for the program is "+ stopwatch.elapsed(MILLISECONDS));
    }
    public static  void crawlFileNProcessLines(Executor executor, CopyOnWriteArraySet<String> set) {

        try {
            File logFile = new File(cleanedQueriesFile);
            Scanner scanner = new Scanner(logFile);
            scanner.useDelimiter("####");
            /** for each line in the file **/
            while (scanner.hasNext()) {
                //System.out.println(line);
                executor.execute(new QueryDeduplicatorParallel(set,scanner.next()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

