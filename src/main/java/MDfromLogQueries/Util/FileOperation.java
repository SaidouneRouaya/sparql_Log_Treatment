package MDfromLogQueries.Util;

import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;


public class FileOperation {

    /**
     * This class implements some recurrent file operations
     **/

    public static int nbTotalLines =0;

    public static Collection<String> ReadFile(String readingFilePath) {

        File file = new File(readingFilePath);
        String line;
        ArrayList<String> collection = new ArrayList<>();
        BufferedReader br = null;
        int linesNumbers = 0; // for statistical matters
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
        /*System.out.println("number of lines in the file  :   "+linesNumbers );*/

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

                bw.write(query.replaceAll("[\n\r]","\t")+"\n");

                bw.flush();
            }
        }        catch (IOException e) {
            System.out.println("Impossible file creation");
        }finally {

            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void WriteConstructQueriesInFile(String writingFilePath, ArrayList<Query> constructQueries) {
        File file = new File(writingFilePath);
        BufferedWriter bw = null;
        try {
            if (!file.isFile()) file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file, true));

            for (Query query : constructQueries) {

                bw.write(query.toString().replaceAll("[\n\r]", "\t") + "\n");

                bw.flush();
            }
        } catch (IOException e) {
            System.out.println("Impossible file creation");
        } finally {

            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void writeQueryInLog(String writingFilePath, String queryType, Query query) {

        File file = new File(writingFilePath);
        BufferedWriter bw = null;
        try {
            if (!file.isFile()) file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(queryType + query.toString().replaceAll("[\n\r]", "\t") + "\n");
            bw.flush();
        } catch (IOException e) {
            System.out.println("Impossible file creation");
        } finally {

            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    public static void writeModelsInFile(String writingFilePath, ArrayList<Model> models) {
        System.out.println("RAni sdakhel Write\n");

        File file = new File(writingFilePath);
        FileOutputStream outputFile = null;
        OutputStream out = null;

        Statement statement;

        try {
            if (!file.isFile()) file.createNewFile();

            outputFile = new FileOutputStream(file);


            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            out = new FileOutputStream(file);

            for (Model model : models) {

              /*  Iterator<Statement> list = model.listStatements();
                 while (list.hasNext()) {
                    statement = list.next();
                    System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+statement.toString());
                    bw.write(statement.toString().replaceAll("[\n\r]","\t"));
                    bw.flush();
                }
                bw.write("\n");
                bw.flush();

                //bw.write(model.toString().replaceAll("[\n\r]","\t")+"\n");
                */
                model.write(out, "RDF/XML");

                //bw.flush();
            }
            System.out.println("kamalt write\n");
        } catch (IOException e) {
            System.out.println("Impossible file creation");
        } finally {

            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void writeModelInFile(String writingFilePath, Model model) {
        System.out.println("RAni sdakhel Write\n");

        File file = new File(writingFilePath);
        FileOutputStream outputFile = null;
        OutputStream out = null;

        Statement statement;

        try {
            if (!file.isFile()) file.createNewFile();

            outputFile = new FileOutputStream(file);


            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            out = new FileOutputStream(file);

            model.write(out, "TURTLE");

            //bw.flush();
            System.out.println("kamalt write\n");
        } catch (IOException e) {
            System.out.println("Impossible file creation");
        } finally {

            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public static ArrayList<Model> readModelsFromFile(String filePath) {

        System.out.println("rani dakhel read\n");
        ArrayList<Model> models = new ArrayList<>();
        BufferedReader br = null;
        FileInputStream inputFile = null;
        InputStream in = null;
        File file = new File(filePath);
        int linesNumbers = 0;


        try {
            if (!file.isFile()) file.createNewFile();
            in = new FileInputStream(file);

            br = new BufferedReader(new FileReader(file));

            Model model = ModelFactory.createDefaultModel();

            //  model.read(br, null, "RDF/XML");
            // model.read(inputFile,filePath,  "TURTLE");

            //   model.read(inputFile ,  "RDF/XML");

            // model.read(in,  "TURTLE");
            model.read(in, "RDF/XML");


          /*  String line;
            while ( (line =br.readLine()) != null) {

                model.read(new StringReader(line), null, "TURTLE");

              //  model.read(new ByteArrayInputStream(line.getBytes()), null);

                models.add(model);
            }
*/
            System.out.println("*****\t" + linesNumbers);


            System.out.println("kamalt read\n");
        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return models;
    }

    public static Model readModelFromFile(String filePath) {
        BufferedReader br = null;
        InputStream in = null;
        File file = new File(filePath);
        int linesNumbers = 0;
        Model model = ModelFactory.createDefaultModel();

        try {
            if (!file.isFile()) file.createNewFile();
            in = new FileInputStream(file);

            br = new BufferedReader(new FileReader(file));

            model = ModelFactory.createDefaultModel();
            model.read(in, filePath, "TURTLE");

            System.out.println("*****\t" + linesNumbers);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return model;
    }


    public static  void WriteInFileParallel (String writingFilePath, CopyOnWriteArrayList synchronizedList)
    {
        File file =new File(writingFilePath);
        BufferedWriter bw = null;
        try {
            if (!file.isFile()) file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file, true));

            for ( int i=0; i<synchronizedList.size(); i++) {

                bw.write(synchronizedList.get(i) + "\n");

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
