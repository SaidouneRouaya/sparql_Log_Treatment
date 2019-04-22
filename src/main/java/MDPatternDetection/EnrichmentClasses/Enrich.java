package MDPatternDetection.EnrichmentClasses;


import MDPatternDetection.AnnotationClasses.Annotations;
import MDPatternDetection.ExecutionClasses.QueryExecutor;
import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.Util.Constants2;
import MDfromLogQueries.Util.ConstantsUtil;
import MDfromLogQueries.Util.TdbOperation;
import Statistics.StatisticsAnalytic;
import com.google.common.base.Stopwatch;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.util.ArrayList;
import java.util.HashMap;

import static MDfromLogQueries.Util.FileOperation.writeStatisticsListInFile2;

public class Enrich {

    public static String endpoint = "https://dbpedia.org/sparql";
    public static ConstantsUtil constantsUtil = new ConstantsUtil();
    public static Constants2 constants2 = new Constants2();
    public static ArrayList<StatisticsAnalytic> statisticsAnalytics4Fact = new ArrayList<>();
    public static ArrayList<StatisticsAnalytic> statisticsAnalytics4Dimension = new ArrayList<>();
    private static int nb_attribute = 0;
    private static int nb_objectProperty = 0;

    public static void enrichMDSchema(ArrayList<Model> models) {

        int numModel = 0;

        try {
            for (Model model : models) {
                numModel++;
                System.out.println("model num: " + numModel);
                enrichModel(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void enrichModel(Model model) {

        ResIterator resIterator = model.listSubjects();
        int numNoeud = 0;

        while (resIterator.hasNext()) {
            Resource node = resIterator.next();
            nb_attribute = 0;
            nb_objectProperty = 0;
            numNoeud++;
            // System.out.println("le noeud num: "+numNoeud);
            enrichNode(node);
        }
    }


    public static void enrichNode(Resource node) {

        if (node.hasProperty(RDF.type, Annotations.FACT.toString())) {
            countOtherProperties(node, false);
            StatisticsAnalytic statisticsAnalytic = new StatisticsAnalytic();
            statisticsAnalytic.type = "Fact";
            statisticsAnalytic.URI = node.toString();
            statisticsAnalytic.nbDim += nb_objectProperty;
            statisticsAnalytic.nbFactAtt += nb_attribute;
            statisticsAnalytics4Fact.add(statisticsAnalytic);
        } else {
            if (node.hasProperty(RDF.type, Annotations.NONFUNCTIONALDIMENSION.toString())) {
                countOtherProperties(node, false);
                StatisticsAnalytic statisticsAnalytic = new StatisticsAnalytic();
                statisticsAnalytic.type = "Dimension";
                statisticsAnalytic.URI = node.toString();
                statisticsAnalytic.nbLevel += nb_objectProperty;
                statisticsAnalytic.nbDimAtt += nb_attribute;
                statisticsAnalytics4Dimension.add(statisticsAnalytic);
            }
        }
    }

    public static void countOtherProperties(Resource node, boolean finish) {

        String queryStr = "SELECT DISTINCT ?p ?o WHERE { <" + node.getURI() + "> ?p ?o.}";

        QueryExecutor queryExecutor = new QueryExecutor();
        Query query = QueryFactory.create(queryStr);

        ResultSet results = queryExecutor.executeQuerySelect(query, endpoint);
        QuerySolution querySolution;
        // System.out.println("\ncount properties");
        try {
            while (results.hasNext()) {
                querySolution = results.nextSolution();

                //System.out.print("*\t");

                if (querySolution.get("?o").isLiteral() || querySolution.get("?p").equals(RDFS.label)) {
                    nb_attribute++;
                } else {
                    if (querySolution.get("?p").equals(RDFS.subClassOf) && !finish) {
                        countOtherProperties(querySolution.get("?o").asResource(), true);
                    } else {
                        //   System.out.println(querySolution.get("?p").toString());
                        String propertyType;

                        try {
                            propertyType = constantsUtil.getPropertyType(new PropertyImpl(querySolution.get("?p").asResource().toString()));
                        } catch (Exception e) {

                            propertyType = constantsUtil.findPropertyEndpoint(querySolution.get("?p").asResource().toString(), endpoint);
                        }

                        switch (propertyType) {
                            case ("datatypeProperty"): {
                                nb_attribute++;
                            }
                            case ("objectProperty"): {
                                nb_objectProperty++;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void main(String args[]) {


        HashMap<String, Model> modelsAnnotated = TdbOperation.unpersistModelsMap(TdbOperation.dataSetAnnotated);
        ArrayList<Model> models = new ArrayList<>(modelsAnnotated.values());
        ArrayList<Model> modelss = new ArrayList<>(models.subList(1, 20));
        Stopwatch stopwatch = Stopwatch.createUnstarted();
        stopwatch.start();


        enrichMDSchema(modelss);

        writeStatisticsListInFile2(statisticsAnalytics4Fact, Declarations.statisticsAnalyticFactFile);
        writeStatisticsListInFile2(statisticsAnalytics4Dimension, Declarations.statisticsAnalyticDimFile);
        stopwatch.stop();
        System.out.println(" le temps ecoulé : " + stopwatch);

    }
}
