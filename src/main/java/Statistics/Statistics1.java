package Statistics;

import MDPatternDetection.AnnotationClasses.Annotations;
import MDPatternDetection.TdbOperation;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.vocabulary.RDF;

import java.util.*;

import static MDfromLogQueries.Declarations.Declarations.*;
import static MDfromLogQueries.Util.FileOperation.writeStatisticsInFile;
import static MDfromLogQueries.Util.FileOperation.writeStatisticsInFile2;
import static MDfromLogQueries.Util.FileOperation.writeStatisticsListInFile;
import static java.lang.Math.min;
import static java.lang.StrictMath.max;

public class Statistics1 {

    private static Dataset dataset = TDBFactory.createDataset(tdbDirectory);

    private Model model = ModelFactory.createDefaultModel();

    private int NC = 0; //Total number of classes of the star S
    private int NFC = 0;    //Number of fact classes of the Start S
    private int NDC = 0;    //Number of dimension classes of the star S
    private int NBC = 0;    //Number of base classes (dimension hierarchy levels ) of the star S


    private int NAFC = 0;    //Number of Fact Attributes attributes of the fact class of the star S
    private int NADC = 0; //	Number of Dimension and Dimension Attributes of the dimension classes of the star S
    private int NABC = 0;    //** Number of  Dimension and Dimension Attributes of the base classes of the star

    private int NH = 0;    //**Number of hierarchy relationships of the star S
    private int NA = 0;    //**Total number of Fact Attributes, Dimensions and Dimension attributes of the star S

    private int DHP = 0; // Maximum depth of the hierarchy relationships of the star S

    private double RBC = 0;    //Ratio of base classes. Number of base classes per dimension class of the star S
    private double RSA = 0;    //Ratio of attributes of the star S. (Number of attributes Fact Attributes) /( number of Dimension + Dimension attributes)

    //**
    private int NMH = 0;//	** Number of multiple hierarchies in the schema
    private int NLDH = 0; //Number of levels in dimension hierarchies of the schema
    private int NAPMH = 0;    //Number of alternate paths in multiple hierarchies of the schema
    private int NDSH = 0;    //Number of dimensions involved in shared hierarchies of the schema
    private int NSH = 0;    //Number of shared hierarchies of the schema
    private int NSLWD = 0;    //Number of Shared Levels Within Dimensions
    private int NSLBD = 0;    //Number of Shared Levels between Dimensions within a Fact Scheme

    private int NSLAF = 0;    //Number of Shared Levels between Dimensions across Different Fact Schemes
    private int NNSH = 0;       //Number of Non-Strict Hierarchies
    private int CM1 = 0;   //This metric appraises the coupling that occurs due to the interaction between the classes and their attributes in the multidimensional  the conceptual model.
    private int CM2 = 0;   //It quantifies the coupling due to inheritance among the conceptual model classes. The classes which are related by inheritance form a hierarchy called the generalization hierarchy.
    private int MMCM = 0;    //multidimensional model complexity metric


    public Statistics1() {
    }

    public static void main(String... argv) {
        HashMap<String, Model> results = TdbOperation.unpersistModelsMap(TdbOperation.dataSetAnnotated);
        System.out.println(results.size());
        Statistics1 statistis = new Statistics1();
        statistis.stat2(results);


    }

    public void stat(HashMap<String, Model> models) {

        // = TdbOperation.unpersistModelsMap();

        ArrayList<Model> listModels = (ArrayList) models.values();
        ResIterator resIterator;


        for (Model m : listModels) {

            resIterator = m.listSubjects();

            int nbNodes = 0;
            int levelsDepth = 0;

            while (resIterator.hasNext()) {
                Resource node = resIterator.next();
                nbNodes++;


                if (node.hasProperty(RDF.type, Annotations.FACT.toString())) this.setNFC(this.getNFC() + 1);
                if (node.hasProperty(RDF.type, Annotations.DIMENSION.toString()))
                    this.setNDC(this.getNDC() + 1);
                if (node.hasProperty(RDF.type, Annotations.DIMENSIONLEVEL.toString())) {
                    this.setNBC(this.getNBC() + 1);
                    levelsDepth++;
                }


                if (node.hasProperty(RDF.type, Annotations.FACTATTRIBUTE.toString()))
                    this.setNAFC(this.getNAFC() + 1);
                if (node.hasProperty(RDF.type, Annotations.DIMENSIONATTRIBUTE.toString()))
                    this.setNADC(this.getNADC() + 1);


            }

            this.setNC(this.getNC() + nbNodes);// also : statistis1.setNC(statistis1.getNC()+statistis1.getNFC()+statistis1.getNBC()+statistis1.getNDC());
            this.setNLDH(max(this.getNLDH(), levelsDepth));

        }
        this.setRBC((this.getNBC() / this.getNDC()) * 100);
        this.setRSA((this.getNAFC() / this.getNADC()) * 100);
        writeStatisticsInFile(statisticsFile, this);

    }

    public void stat2(HashMap<String, Model> models) {

        // = TdbOperation.unpersistModelsMap();

        Set<String> listModels = models.keySet();
        Resource subject;
        RDFNode object;

        Model m;
        int i =0;
        ArrayList<Statistics1> statistics1ArrayList = new ArrayList<>();

        for (String key : listModels) {
            i++;
            System.out.println("model n° "+i);
            m = models.get(key);
            Statistics1 statistics1 = new Statistics1();
            subject = m.getResource(key);
            ArrayList<RDFNode> visitedNodes = new ArrayList<>();
            int nbHierarchies=0;
/*
                System.out.println(" la clé :"+key);
                ConsolidationTest.afficherModel(m);

*/
            if (subject != null) {
                if (subject.hasProperty(RDF.type, Annotations.FACT.toString())) statistics1.setNFC(statistics1.getNFC()+1);
                visitedNodes.add(subject);
                List<Statement> propertyIterator = subject.listProperties().toList();
                for (Statement stat : propertyIterator) {
                    if (!stat.getPredicate().equals(RDF.type) && !visitedNodes.contains(stat.getObject())) {
                        object = stat.getObject();
                        if (object.asResource().hasProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString())) {
                            visitedNodes.add(object);
                            statistics1.setNDC(statistics1.getNDC() + 1);
                            statistics1.setNH(0);
                            statistics1 = countLevels(object.asResource(), statistics1, 1, visitedNodes);
                            if (statistics1.getNH() > 0) {
                                nbHierarchies += statistics1.getNH();
                            }
                            statistics1.setDHP(max(statistics1.getDHP(), 1));
                        }
                        if (object.asResource().hasProperty(RDF.type, Annotations.FACTATTRIBUTE.toString()))
                            statistics1.setNAFC(statistics1.getNAFC() + 1);
                    }
                }
            }
            statistics1.setNH(nbHierarchies);
            statistics1.setNC(statistics1.getNDC()+ statistics1.getNFC()+ statistics1.getNBC());
            if (statistics1.getNDC() >0 )
                statistics1.setRBC(statistics1.getNBC()/ statistics1.getNDC());
            statistics1.setNA(statistics1.getNAFC()+ statistics1.getNADC());
            if (statistics1.getNADC() >0 )
            statistics1.setRSA(statistics1.getNAFC()/(statistics1.getNADC()+ statistics1.getNABC()));
            statistics1.setModel(m);
            statistics1ArrayList.add(statistics1);
            //showStatistics(statistics1);
            /*try {
                System.in.read();
            }
            catch (IOException e)
            {

            }*/
        }
        writeStatisticsListInFile(statistics1ArrayList,statisticsFile);
        writeStatisticsInFile2(avgstatisticsFile,avgStatistics(statistics1ArrayList));
        writeStatisticsInFile2(minstatisticsFile,minStatistics(statistics1ArrayList));
        writeStatisticsInFile2(maxstatisticsFile,maxStatistics(statistics1ArrayList));
        writeStatisticsInFile2(totalstatisticsFile,totalStatistics(statistics1ArrayList));


    }

    public Statistics1 countLevels(Resource resource, Statistics1 statistics1, int nbLevels, ArrayList<RDFNode> visitedNodes)
    {
        RDFNode object;
        List<Statement> propertyList = resource.listProperties().toList();
        int nbH=0;
        nbLevels++;
        for (Statement statement : propertyList)
        {
            if (!statement.getPredicate().equals(RDF.type) && !visitedNodes.contains(statement.getObject())) {
                object = statement.getObject();
                System.out.println(statement);
                if (object.asResource().hasProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSIONLEVEL.toString())) {
                    visitedNodes.add(object);
                    nbH++;
                    statistics1.setNBC(statistics1.getNBC() + 1);
                    statistics1.setDHP(max(statistics1.getDHP(), nbLevels));
                    statistics1.setNH(nbH);
                    statistics1 = countLevels(object.asResource(), statistics1, nbLevels,visitedNodes);

                }
                if (object.asResource().hasProperty(RDF.type, Annotations.DIMENSIONATTRIBUTE.toString())) {
                    if (resource.hasProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSIONLEVEL.toString()))
                        statistics1.setNABC(statistics1.getNABC() + 1);
                    else if (resource.hasProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString()))
                        statistics1.setNADC(statistics1.getNADC() + 1);

                }
            }

        }
        statistics1.setNH(nbH);
        return statistics1;
    }




    public Statistics1 avgStatistics(ArrayList<Statistics1> statistics1ArrayList)
    {
        int size = statistics1ArrayList.size();
        Statistics1 statistics1 = totalStatistics(statistics1ArrayList);
        statistics1.setNFC(statistics1.getNFC()/size);
        statistics1.setNDC(statistics1.getNDC()/size);
        statistics1.setNBC(statistics1.getNBC()/size);
        statistics1.setNC(statistics1.getNC()/size);
        statistics1.setRBC(statistics1.getRBC()/size);
        statistics1.setNAFC(statistics1.getNAFC()/size);
        statistics1.setNADC(statistics1.getNADC()/size);
        statistics1.setNA(statistics1.getNA()/size);
        statistics1.setNH(statistics1.getNH()/size);
        statistics1.setDHP(statistics1.getDHP()/size);
        statistics1.setRSA(statistics1.getRSA()/size);

        return statistics1;

    }
    public Statistics1 totalStatistics(ArrayList<Statistics1> statistics1ArrayList)
    {
        int size = statistics1ArrayList.size();
        Statistics1 statistics1 = new Statistics1();
        for (Statistics1 stat : statistics1ArrayList)
        {
            statistics1.setNFC(stat.getNFC()+ statistics1.getNFC());
            statistics1.setNDC(stat.getNDC()+ statistics1.getNDC());
            statistics1.setNBC(stat.getNBC()+ statistics1.getNBC());
            statistics1.setNC(stat.getNC()+ statistics1.getNC());
            statistics1.setRBC(stat.getRBC()+ statistics1.getRBC());
            statistics1.setNAFC(stat.getNAFC()+ statistics1.getNAFC());
            statistics1.setNADC(stat.getNADC()+ statistics1.getNADC());
            statistics1.setNA(stat.getNA()+ statistics1.getNA());
            statistics1.setNH(stat.getNH()+ statistics1.getNH());
            statistics1.setDHP(stat.getDHP()+ statistics1.getDHP());
            statistics1.setRSA(stat.getRSA()+ statistics1.getRSA());
        }

        return statistics1;

    }

    public Statistics1 minStatistics(ArrayList<Statistics1> statistics1ArrayList)
    {
        int size = statistics1ArrayList.size();
        Statistics1 statistics1 = new Statistics1();
        for (Statistics1 stat : statistics1ArrayList)
        {
            statistics1.setNFC(min(stat.getNFC(), statistics1.getNFC()));
            statistics1.setNDC(min(stat.getNDC(), statistics1.getNDC()));
            statistics1.setNBC(min(stat.getNBC(), statistics1.getNBC()));
            statistics1.setNC(min(stat.getNC(), statistics1.getNC()));
            statistics1.setRBC(min(stat.getRBC(), statistics1.getRBC()));
            statistics1.setNAFC(min(stat.getNAFC(), statistics1.getNAFC()));
            statistics1.setNADC(min(stat.getNADC(), statistics1.getNADC()));
            statistics1.setNA(min(stat.getNA(), statistics1.getNA()));
            statistics1.setNH(min(stat.getNH(), statistics1.getNH()));
            statistics1.setDHP(min(stat.getDHP(), statistics1.getDHP()));
            statistics1.setRSA(min(stat.getRSA(), statistics1.getRSA()));
        }
        return statistics1;

    }
    public Statistics1 maxStatistics(ArrayList<Statistics1> statistics1ArrayList)
    {
        int size = statistics1ArrayList.size();
        Statistics1 statistics1 = new Statistics1();
        for (Statistics1 stat : statistics1ArrayList)
        {
            statistics1.setNFC(max(stat.getNFC(), statistics1.getNFC()));
            statistics1.setNDC(max(stat.getNDC(), statistics1.getNDC()));
            statistics1.setNBC(max(stat.getNBC(), statistics1.getNBC()));
            statistics1.setNC(max(stat.getNC(), statistics1.getNC()));
            statistics1.setRBC(max(stat.getRBC(), statistics1.getRBC()));
            statistics1.setNAFC(max(stat.getNAFC(), statistics1.getNAFC()));
            statistics1.setNADC(max(stat.getNADC(), statistics1.getNADC()));
            statistics1.setNA(max(stat.getNA(), statistics1.getNA()));
            statistics1.setNH(max(stat.getNH(), statistics1.getNH()));
            statistics1.setDHP(max(stat.getDHP(), statistics1.getDHP()));
            statistics1.setRSA(max(stat.getRSA(), statistics1.getRSA()));
        }
        return statistics1;
    }





    public void showStatistics(Statistics1 statistics1) {


            System.out.println("Total number of classes of the star S\t:\tNC(S) =\t" + statistics1.getNC() + "\n");
            System.out.println("Number of fact classes of the start S\t:\tNFC(S) =\t" + statistics1.getNFC() + "\n");
            System.out.println("Number of dimension classes of the star S \t:\tNDC(S) =\t" + statistics1.getNDC() + "\n");
            System.out.println("Number of base classes of the star S\t:\t =\tNBC(S) =\t" + statistics1.getNBC() + "\n");
            System.out.println("Ratio of base classes. Number of base classes per dimension class of the star S\t:\tRBC(S) =\t" + statistics1.getRBC() + "\n");
            System.out.println("Number of Fact Attributes attributes of the fact class of the star S\t:\tNAFC(S) =\t" + statistics1.getNAFC() + "\n");
            System.out.println("Number of Dimension and Dimension Attributes of the dimension classes of the star S\t:\tNADC(S) =\t" + statistics1.getNADC() + "\n");
            System.out.println("Number of  Dimension and Dimension Attributes of the base classes of the star S\t:\tNABC(S) =\t" + statistics1.getNABC() + "\n");
            System.out.println("Total number of Fact Attributes, Dimensions and Dimension attributes of the star S\t:\tNA(S) =\t" + statistics1.getNA() + "\n");
            System.out.println("Number of hierarchy relationships of the star S\t:\tNH(S) =\t" + statistics1.getNH() + "\n");
            System.out.println("Maximum depth of the hierarchy relationships of the star S\t:\tDHP(S)  =\t" + statistics1.getDHP() + "\n");
            System.out.println("Ratio of attributes of the star S\t:\tRSA(S) =\t" + statistics1.getRSA() + "\n");

    }


    public void setModel(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public static Dataset getDataset() {
        return dataset;
    }

    public static void setDataset(Dataset dataset) {
        Statistics1.dataset = dataset;
    }

    public int getNFC() {
        return NFC;
    }

    public void setNFC(int NFC) {
        this.NFC = NFC;
    }

    public int getNDC() {
        return NDC;
    }

    public void setNDC(int NDC) {
        this.NDC = NDC;
    }

    public int getNBC() {
        return NBC;
    }

    public void setNBC(int NBC) {
        this.NBC = NBC;
    }

    public int getNC() {
        return NC;
    }

    public void setNC(int NC) {
        this.NC = NC;
    }

    public double getRBC() {
        return RBC;
    }

    public void setRBC(double RBC) {
        this.RBC = RBC;
    }

    public int getNAFC() {
        return NAFC;
    }

    public void setNAFC(int NAFC) {
        this.NAFC = NAFC;
    }

    public int getNADC() {
        return NADC;
    }

    public void setNADC(int NADC) {
        this.NADC = NADC;
    }

    public int getNABC() {
        return NABC;
    }

    public void setNABC(int NABC) {
        this.NABC = NABC;
    }

    public int getNA() {
        return NA;
    }

    public void setNA(int NA) {
        this.NA = NA;
    }

    public int getNH() {
        return NH;
    }

    public void setNH(int NH) {
        this.NH = NH;
    }

    public int getDHP() {
        return DHP;
    }

    public void setDHP(int DHP) {
        this.DHP = DHP;
    }

    public double getRSA() {
        return RSA;
    }

    public void setRSA(double RSA) {
        this.RSA = RSA;
    }

    public int getNMH() {
        return NMH;
    }

    public void setNMH(int NMH) {
        this.NMH = NMH;
    }

    public int getNLDH() {
        return NLDH;
    }

    public void setNLDH(int NLDH) {
        this.NLDH = NLDH;
    }

    public int getNAPMH() {
        return NAPMH;
    }

    public void setNAPMH(int NAPMH) {
        this.NAPMH = NAPMH;
    }

    public int getNDSH() {
        return NDSH;
    }

    public void setNDSH(int NDSH) {
        this.NDSH = NDSH;
    }

    public int getNSH() {
        return NSH;
    }

    public void setNSH(int NSH) {
        this.NSH = NSH;
    }

    public int getNSLWD() {
        return NSLWD;
    }

    public void setNSLWD(int NSLWD) {
        this.NSLWD = NSLWD;
    }

    public int getNSLBD() {
        return NSLBD;
    }

    public void setNSLBD(int NSLBD) {
        this.NSLBD = NSLBD;
    }

    public int getNSLAF() {
        return NSLAF;
    }

    public void setNSLAF(int NSLAF) {
        this.NSLAF = NSLAF;
    }

    public int getNNSH() {
        return NNSH;
    }

    public void setNNSH(int NNSH) {
        this.NNSH = NNSH;
    }

    public int getCM1() {
        return CM1;
    }

    public void setCM1(int CM1) {
        this.CM1 = CM1;
    }

    public int getCM2() {
        return CM2;
    }

    public void setCM2(int CM2) {
        this.CM2 = CM2;
    }

    public int getMMCM() {
        return MMCM;
    }

    public void setMMCM(int MMCM) {
        this.MMCM = MMCM;
    }
}
