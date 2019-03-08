package Statistics;

import MDPatternDetection.AnnotationClasses.Annotations;
import MDPatternDetection.TdbOperation;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.vocabulary.RDF;

import java.util.ArrayList;
import java.util.HashMap;

import static MDfromLogQueries.Declarations.Declarations.tdbDirectory;
import static java.lang.StrictMath.max;

public class Statistis1 {

    private static Dataset dataset = TDBFactory.createDataset(tdbDirectory);

    private int NC = 0; //Total number of classes of the star S
    private int NFC = 0;    //Number of fact classes of the Start S
    private int NDC = 0;    //Number of dimension classes of the star S
    private int NBC = 0;    //Number of base classes (dimension hierarchy levels ) of the star S


    private int NAFC = 0;    //Number of Fact Attributes attributes of the fact class of the star S
    private int NADC = 0; //	Number of Dimension and Dimension Attributes of the dimension classes of the star S
    private int NABC = 0;    //** Number of  Dimension and Dimension Attributes of the base classes of the star

    private int NMH = 0;//	** Number of multiple hierarchies in the schema
    private int NLDH = 0; //Number of levels in dimension hierarchies of the schema

    private int NH = 0;    //**Number of hierarchy relationships of the star S
    private int NA = 0;    //**Total number of Fact Attributes, Dimensions and Dimension attributes of the star S

    private int DHP = 0; // Maximum depth of the hierarchy relationships of the star S

    private int RBC = 0;    //Ratio of base classes. Number of base classes per dimension class of the star S
    private int RSA = 0;    //Ratio of attributes of the star S. (Number of attributes Fact Attributes) /( number of Dimension + Dimension attributes)

    //**
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


    public Statistis1() {
    }

    public static void main(String... argv) {

        Statistis1 statistis1 = new Statistis1();
        HashMap<String, Model> models = TdbOperation.unpersistModelsMap();

        ArrayList<Model> listModels = (ArrayList) models.values();
        ResIterator resIterator;

        //  Iterator it = models.entrySet().iterator();

        //while (it.hasNext()) {Map.Entry<String, Model> pair = (Map.Entry) it.next();

        for (Model m : listModels) {

            resIterator = m.listSubjects();

            int nbNodes = 0;
            int levelsDepth = 0;

            while (resIterator.hasNext()) {
                Resource node = resIterator.next();
                nbNodes++;


                if (node.hasProperty(RDF.type, Annotations.FACT.toString())) statistis1.setNFC(statistis1.getNFC() + 1);
                if (node.hasProperty(RDF.type, Annotations.DIMENSION.toString()))
                    statistis1.setNDC(statistis1.getNDC() + 1);
                if (node.hasProperty(RDF.type, Annotations.DIMENSIONLEVEL.toString())) {
                    statistis1.setNBC(statistis1.getNBC() + 1);
                    levelsDepth++;
                }


                if (node.hasProperty(RDF.type, Annotations.FACTATTRIBUTE.toString()))
                    statistis1.setNAFC(statistis1.getNAFC() + 1);
                if (node.hasProperty(RDF.type, Annotations.DIMENSIONATTRIBUTE.toString()))
                    statistis1.setNADC(statistis1.getNADC() + 1);


            }

            statistis1.setNC(statistis1.getNC() + nbNodes);// also : statistis1.setNC(statistis1.getNC()+statistis1.getNFC()+statistis1.getNBC()+statistis1.getNDC());
            statistis1.setNLDH(max(statistis1.getNLDH(), levelsDepth));

        }
        statistis1.setRBC((statistis1.getNBC() / statistis1.getNDC()) * 100);
        statistis1.setRSA((statistis1.getNAFC() / statistis1.getNADC()) * 100);

    }

    public static Dataset getDataset() {
        return dataset;
    }

    public static void setDataset(Dataset dataset) {
        Statistis1.dataset = dataset;
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

    public int getRBC() {
        return RBC;
    }

    public void setRBC(int RBC) {
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

    public int getRSA() {
        return RSA;
    }

    public void setRSA(int RSA) {
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
