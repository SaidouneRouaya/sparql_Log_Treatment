package MDfromLogQueries.Util;

import MDfromLogQueries.Declarations.Declarations;
import Statistics.Statistics1;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
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


    public static ArrayList<ArrayList<String>> ReadFile4Transform(String readingFilePath) {

        File file = new File(readingFilePath);
        String line;


        ArrayList<ArrayList<String>> collections = new ArrayList<>();


        ArrayList<String> collection = new ArrayList<>();

        BufferedReader br = null;
        int linesNumbers = 0; // for statistical matters
        try {
            if (!file.isFile()) file.createNewFile();
            boolean finish = false;
            br = new BufferedReader(new FileReader(file));

            while (!finish) {


                while ((line = br.readLine()) != null && linesNumbers < 100000) {

                    collection.add(line);

                    linesNumbers++;
                }

                System.out.println("la taille avant le if " + collection.size());
                if (linesNumbers >= 100000) {
                    linesNumbers = 0;
                    collections.add(collection);

                    collection.clear();
                } else {
                    collections.add(collection);
                    finish = true;
                }
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


        //nbTotalLines+=linesNumbers;
        /*System.out.println("number of lines in the file  :   "+linesNumbers );*/

        return collections;
    }



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


    public static void main(String[] args) {

        ArrayList<ArrayList<String>> listt = ReadFile4Transform(Declarations.syntaxValidFile);


        for (ArrayList<String> a : listt) {

            System.out.println("taille : " + a.size());


        }

    }
     /* BufferedWriter bw = null;

        try {
        File file =new File("C:\\Users\\pc\\Desktop\\PFE\\Files\\Fichier_Syntaxe_Valide_Test.txt");


        if (!file.isFile()) file.createNewFile();

        for (int i=0; i<65; i++)
        {

            bw = new BufferedWriter(new FileWriter(file, true));



            bw.write(i+"\n");

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
}*/

    public static void writeStatisticsInFile2(Statistics1 statistics1, String typeStat, String writingFilePath) {
        File file = new File(writingFilePath);
        BufferedWriter bw = null;
        try {
            if (!file.isFile()) file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file, true));


            bw.write("\n****************************** " + typeStat + " *******************************************");
            bw.write("Total number of classes of the star S\t:\tNC(S) =\t" + statistics1.getNC() + "\n");
            bw.write("Number of fact classes of the start S\t:\tNFC(S) =\t" + statistics1.getNFC() + "\n");
            bw.write("Number of dimension classes of the star S \t:\tNDC(S) =\t" + statistics1.getNDC() + "\n");
            bw.write("Number of base classes of the star S\t:\t =\tNBC(S) =\t" + statistics1.getNBC() + "\n");
            bw.write("Ratio of base classes. Number of base classes per dimension class of the star S\t:\tRBC(S) =\t" + statistics1.getRBC() + "\n");
            bw.write("Number of Fact Attributes attributes of the fact class of the star S\t:\tNAFC(S) =\t" + statistics1.getNAFC() + "\n");
            bw.write("Number of Dimension and Dimension Attributes of the dimension classes of the star S\t:\tNADC(S) =\t" + statistics1.getNADC() + "\n");
            bw.write("Number of  Dimension and Dimension Attributes of the base classes of the star S\t:\tNABC(S) =\t" + statistics1.getNABC() + "\n");
            bw.write("Total number of Fact Attributes, Dimensions and Dimension attributes of the star S\t:\tNA(S) =\t" + statistics1.getNA() + "\n");
            bw.write("Number of hierarchy relationships of the star S\t:\tNH(S) =\t" + statistics1.getNH() + "\n");
            bw.write("Maximum depth of the hierarchy relationships of the star S\t:\tDHP(S)  =\t" + statistics1.getDHP() + "\n");
            bw.write("Ratio of attributes of the star S\t:\tRSA(S) =\t" + statistics1.getRSA() + "\n");
            bw.write("****************************** Fin de " + typeStat + " *******************************************\n");
            bw.flush();
        } catch (
                IOException e) {
            System.out.println("Impossible file creation");
        } finally {

            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void writeStatisticsListInFile(ArrayList<Statistics1> statistisArrayList, String writingFilePath) {
        File file = new File(writingFilePath);
        BufferedWriter bw = null;
        try {
            if (!file.isFile()) file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file, true));
            int i = 0;
            for (Statistics1 stat : statistisArrayList) {
                i++;
                bw.write("********************************* Graph number " + i + " ************************************************\n");

               /* StmtIterator stmtIterator = stat.getModel().listStatements();
                int j = 0;
                while (stmtIterator.hasNext())
                {
                    j++;
                    bw.write(j+". "+stmtIterator.nextStatement()+"\n");
                }*/

                bw.write("------------------------------------------------------------------\n");

                bw.write("Total number of classes of the star S\t:\tNC(S) =\t" + stat.getNC() + "\n");
                bw.write("Number of fact classes of the start S\t:\tNFC(S) =\t" + stat.getNFC() + "\n");
                bw.write("Number of dimension classes of the star S \t:\tNDC(S) =\t" + stat.getNDC() + "\n");
                bw.write("Number of base classes of the star S\t:\t =\tNBC(S) =\t" + stat.getNBC() + "\n");
                bw.write("Ratio of base classes. Number of base classes per dimension class of the star S\t:\tRBC(S) =\t" + stat.getRBC() + "\n");
                bw.write("Number of Fact Attributes attributes of the fact class of the star S\t:\tNAFC(S) =\t" + stat.getNAFC() + "\n");
                bw.write("Number of Dimension and Dimension Attributes of the dimension classes of the star S\t:\tNADC(S) =\t" + stat.getNADC() + "\n");
                bw.write("Number of  Dimension and Dimension Attributes of the base classes of the star S\t:\tNABC(S) =\t" + stat.getNABC() + "\n");
                bw.write("Total number of Fact Attributes, Dimensions and Dimension attributes of the star S\t:\tNA(S) =\t" + stat.getNA() + "\n");
                bw.write("Number of hierarchy relationships of the star S\t:\tNH(S) =\t" + stat.getNH() + "\n");
                bw.write("Maximum depth of the hierarchy relationships of the star S\t:\tDHP(S)  =\t" + stat.getDHP() + "\n");
                bw.write("Ratio of attributes of the star S\t:\tRSA(S) =\t" + stat.getRSA() + "\n");
                bw.write("*********************************************************************************\n");

            }
            bw.flush();
        } catch (
                IOException e) {
            System.out.println("Impossible file creation");
        } finally {

            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void writeStatisticsInFile(String writingFilePath, Statistics1 statistics1) {
        File file = new File(writingFilePath);
        BufferedWriter bw = null;
        try {
            if (!file.isFile()) file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file, true));


            bw.write("Total number of classes of the star S\t:\tNC(S) =\t" + statistics1.getNC() + "\n");
            bw.write("Number of fact classes of the start S\t:\tNFC(S) =\t" + statistics1.getNFC() + "\n");
            bw.write("Number of dimension classes of the star S \t:\tNDC(S) =\t" + statistics1.getNDC() + "\n");
            bw.write("Number of base classes of the star S\t:\t =\tNBC(S) =\t" + statistics1.getNBC() + "\n");
            bw.write("Ratio of base classes. Number of base classes per dimension class of the star S\t:\tRBC(S) =\t" + statistics1.getRBC() + "\n");
            bw.write("Number of Fact Attributes attributes of the fact class of the star S\t:\tNAFC(S) =\t" + statistics1.getNAFC() + "\n");
            bw.write("Number of Dimension and Dimension Attributes of the dimension classes of the star S\t:\tNADC(S) =\t" + statistics1.getNADC() + "\n");
            bw.write("Number of  Dimension and Dimension Attributes of the base classes of the star S\t:\tNABC(S) =\t" + statistics1.getNABC() + "\n");
            bw.write("Total number of Fact Attributes, Dimensions and Dimension attributes of the star S\t:\tNA(S) =\t" + statistics1.getNA() + "\n");
            bw.write("Number of hierarchy relationships of the star S\t:\tNH(S) =\t" + statistics1.getNH() + "\n");
            bw.write("Maximum depth of the hierarchy relationships of the star S\t:\tDHP(S)  =\t" + statistics1.getDHP() + "\n");
            bw.write("Ratio of attributes of the star S\t:\tRSA(S) =\t" + statistics1.getRSA() + "\n");
            bw.write("Number of multiple hierarchies in the schema\t:\tNMH =\t" + statistics1.getNMH() + "\n");
            bw.write("Number of levels in dimension hierarchies of the schema\t:\tNLDH =\t" + statistics1.getNLDH() + "\n");
            bw.write("Number of alternate paths in multiple hierarchies of the schema\t:\tNAPMH =\t" + statistics1.getNAPMH() + "\n");
            bw.write("Number of dimensions involved in shared hierarchies of the schema\t:\t NDSH =\t" + statistics1.getNDSH() + "\n");
            bw.write("Number of shared hierarchies of the schema\t:\tNSH =\t" + statistics1.getNSH() + "\n");
            bw.write("Number of Shared Levels Within Dimensions\t:\tNSLWD =\t" + statistics1.getNSLWD() + "\n");
            bw.write("Number of Shared Levels between Dimensions within a Fact Scheme\t:\tNSLBD =\t" + statistics1.getNSLBD() + "\n");
            bw.write("Number of Shared Levels between Dimensions across Different Fact Schemes\t:\tNSLAF =\t" + statistics1.getNSLAF() + "\n");
            bw.write("Number of Non-Strict Hierarchies\t:\tNNSH =\t" + statistics1.getNNSH() + "\n");
            bw.write("Number of the classes which have interaction between the classes and their attributes in the multidimensional  the conceptual model\t:\t CM1 =\t" + statistics1.getCM1() + "\n");
            bw.write("Number of The classes which are related by inheritance form a hierarchy called the generalization hierarchy\t:\tCM2\tI =\t" + statistics1.getCM2() + "\n");
            bw.write("multidimensional model complexity metric\t:\tMMCM =\t" + statistics1.getMMCM() + "\n");


            bw.flush();
        } catch (
                IOException e) {
            System.out.println("Impossible file creation");
        } finally {

            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public static void writeModelsInFile(HashMap<String, Model> models, String writingFilePath) {
        File file = new File(writingFilePath);
        BufferedWriter bw = null;
        try {
            if (!file.isFile()) file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file, true));
            int i =0;
            Set<String> keys = models.keySet();
            for (String key : keys) {
                i++;
                bw.write("*********************************Graph number "+i+" "+key +" ************************************************\n");

                StmtIterator stmtIterator = models.get(key).listStatements();
                int j = 0;
                while (stmtIterator.hasNext())
                {
                    j++;
                    bw.write(j+". "+stmtIterator.nextStatement()+"\n");
                }

                bw.write("*********************************************************************************\n");

            }
            bw.flush();
        } catch (
                IOException e) {
            System.out.println("Impossible file creation");
        } finally {

            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}








