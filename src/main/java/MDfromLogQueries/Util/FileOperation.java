package MDfromLogQueries.Util;

import MDfromLogQueries.LogCleaning.LogParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;


public class FileOperation {


    public static int nbTotalLines =0;
    public static int nbQueriesTotalLines =0;
    public static Collection<String> ReadFile(String readingFilePath) {

        File file = new File(readingFilePath);
        String line = "";
       ArrayList<String> collection = new ArrayList<>();
        BufferedReader br = null;
        int linesNumbers =0;
        try {
            if (!file.isFile()) file.createNewFile();

            br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                collection.add(line);
                linesNumbers++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                br.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        nbTotalLines+=linesNumbers;
        System.out.println("nombre de ligne dans le fichier est :   "+linesNumbers );
        return collection;
    }
   /* public static CopyOnWriteArrayList ReadFileParallel (String readingFilePath) {

        File file = new File(readingFilePath);
        String line = "";
        Collection<Object> collection = null;
        BufferedReader br = null;
        try {
            if (!file.isFile()) file.createNewFile();

            br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                collection.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return collection;
    }
*/


    public static  void WriteInFile (String writingFilePath, Collection<String> collection)
    {
        File file =new File(writingFilePath);
        BufferedWriter bw = null;
        try {
            if (!file.isFile()) file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file, true));

            for (String query : collection) {

                bw.write(query+"####");

                bw.flush();
            }
        }        catch (IOException e) {
            System.out.println("Imposible file creation");
        }finally {

            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public static  void WriteInFileParallel (String writingFilePath, CopyOnWriteArrayList synchronizedList)
    {
        File file =new File(writingFilePath);
        BufferedWriter bw = null;
        try {
            if (!file.isFile()) file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file, true));

            for ( int i=0; i<synchronizedList.size(); i++) {

                bw.write(synchronizedList.get(i)+"####");

                bw.flush();
            }
        }        catch (IOException e) {
            System.out.println("Imposible file creation");
        }finally {

            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }




}
