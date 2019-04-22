package Scenarios;

import MDPatternDetection.ConsolidationClasses.Consolidation;
import MDPatternDetection.ConsolidationTest;
import MDPatternDetection.ExecutionClasses.QueryExecutor;
import MDPatternDetection.GraphConstructionClasses.QueryUpdate;
import MDfromLogQueries.Declarations.Declarations;
import MDfromLogQueries.SPARQLSyntacticalValidation.QueryFixer;
import MDfromLogQueries.Util.Constants2;
import MDfromLogQueries.Util.FileOperation;
import MDfromLogQueries.Util.TdbOperation;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.vocabulary.RDF;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AnalyticQueries {

    public boolean containsAggregator(Query query) {
        List<ExprAggregator> exprAggregatorList = new ArrayList<>();
        if (!query.hasAggregators()) {
            return false;
        } else {
            return true;
        }
    }


    public static boolean isAnalytic(Query query) {
        List<ExprAggregator> exprAggregatorList;
        int i = 0;
        if (query.hasAggregators()) {
            exprAggregatorList = query.getAggregators();
            for (ExprAggregator exprAggregator : exprAggregatorList) {
                System.out.println("la var :" + exprAggregator.getAggregator().getExprList());
                // the exprList is null in case it's a count(*)
                if (exprAggregator.getAggregator().getExprList() != null) {
                    i++;
                }
            }
        }
        return (i > 0);
    }


    public static ArrayList<String> getAnalyticQueries(ArrayList<String> queryList) {
        ArrayList<String> analyticQueriesList = new ArrayList<>();
        int nb_line = 0; //for statistical needs
        int nb = 0;
        String queryStr;
        Query query = QueryFactory.create();
        int size = queryList.size();
        while (nb_line < size) {
            try {
                nb_line++;
                queryStr = queryList.get(nb_line);
                query = QueryFactory.create(queryStr);
                if (query.hasAggregators() && isAnalytic(query)) {
                    analyticQueriesList.add(query.toString());
                }
                System.out.println("line \t" + nb_line);
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("erreur");
                nb++;
            }
        }
        return analyticQueriesList;
    }


    public static HashSet<Model> executeAnalyticQueriesList(ArrayList<String> queryList) {
        int nb_line = 0; //for statistical needs
        int nb = 0;
        String queryStr;
        HashSet<Model> modelHashSet = new HashSet<>();
        Query query;
        int size = queryList.size();
        QueryExecutor queryExecutor = new QueryExecutor();
        AnalyticQuery analyticQuery = new AnalyticQuery();
        while (nb_line < size) {
            try {
                nb_line++;
                //queryStr = line;
                System.out.println("requete num : "+nb_line);
                queryStr = queryList.get(nb_line);
                query = QueryFactory.create(queryStr);
                QueryUpdate queryUpdate = new QueryUpdate(query);// Adding missing rdf:type statements to the query
                query = queryUpdate.addAddedVariablesToResultVars(query); // Adding new variables to the resulting vars
                BasicPattern bpConstruct = queryUpdate.getQueryConstruction().getBpConstruct(); // Getting construct BasicPattern to use it to construct the Graph Pattern
                List<Triple> bpWhereTriples = queryUpdate.getQueryConstruction().getBpWhere().getList();

                analyticQuery.selectQuery = query;
                analyticQuery.selectQuery.addResultVar(analyticQuery.getAggregVariable());


                ResultSet resultSet;
                resultSet = queryExecutor.executeQuerySelect(query, "https://dbpedia.org/sparql");

                modelHashSet.addAll(constructModels(resultSet, bpConstruct, analyticQuery,bpWhereTriples));

                System.out.println("line \t" + nb_line);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("erreur");
                nb++;
            }
        }
        return modelHashSet;
    }

    public static HashSet<Model> constructModels(ResultSet resultSet, BasicPattern bpConstruct, AnalyticQuery analyticQuery
    ,List<Triple> bpWhereTriples) {
        List<Triple> tripleList;
        HashSet<Model> modelHashSet = new HashSet<>();
        Model model;
        Node node;
        QuerySolution querySolution;
        Statement statement;
        Resource subject;
        Property property;
        RDFNode object;
        while (resultSet.hasNext()) {
            model = ModelFactory.createDefaultModel();
            querySolution = resultSet.next();
            tripleList = bpConstruct.getList();
            for (Triple triple : tripleList) {
                node = triple.getSubject();
                if (node.isVariable()) {
                    subject = querySolution.get(node.getName()).asResource();
                } else {
                    subject = new ResourceImpl(node.getURI());

                }
                node = triple.getPredicate();
                if (node.isVariable()) {
                    property = new PropertyImpl(querySolution.get(node.getName()).asNode().getURI());
                } else {
                    property = new PropertyImpl(node.getURI());

                }
                node = triple.getObject();
                if (node.isVariable()) {
                    object = new ResourceImpl(querySolution.get(node.getName()).asNode().getURI());
                } else {
                    object = new ResourceImpl(node.getURI());

                }
                statement = new StatementImpl(subject, property, object);
                if (!model.contains(statement))
                    model.add(statement);
            }
            if (querySolution.get(analyticQuery.aggregVariables).isResource()) {
                Node rdfNode = getRdfTypeVariable(bpWhereTriples,analyticQuery.aggregVariables);
                if(!rdfNode.isBlank())
                    subject = new ResourceImpl(rdfNode.getName());
                else
                    subject = querySolution.get(analyticQuery.aggregVariables).asResource();

                property = new PropertyImpl("http://0.0.0.0/lodlinc/countMeasure");
                object = new ResourceImpl("http://www.w3.org/2001/XMLSchema#integer");
                statement = new StatementImpl(subject, property, object);
                model.add(statement);
            }
            modelHashSet.add(model);
        }
        return modelHashSet;
    }

    private static Node getRdfTypeVariable(List<Triple> bpWhereTriples, String variable)
    {
        Node rdfTypeVar = NodeFactory.createBlankNode();

        for (Triple triple : bpWhereTriples)
        {
            if (triple.subjectMatches(new Node_Variable(variable)) && triple.predicateMatches(RDF.type.asNode()))
            {
                rdfTypeVar = triple.getObject();
                return rdfTypeVar;
            }
        }
        return rdfTypeVar;
    }

    public static void main(String args[]) {
        ArrayList<String> analyticQueriesList = new ArrayList<>();
        Query query = null;
        ArrayList<String> queryList;
        AnalyticQueries analyticQueries = new AnalyticQueries();
        queryList = (ArrayList<String>) FileOperation.ReadFile(Declarations.syntaxValidFile2);
        analyticQueriesList = getAnalyticQueries(queryList);
        //System.out.println("Size of validQueryList : "+validQueryList.size());
        FileOperation.WriteInFile(Declarations.AnalyticQueriesFile,analyticQueriesList);
         new Constants2();
        new TdbOperation();
       // ArrayList<String> queryList;
        //queryList = (ArrayList<String>) FileOperation.ReadFile(Declarations.AnalyticSelectQueriesFile);
        HashSet<Model> modelHashSet = executeAnalyticQueriesList(queryList);
        TdbOperation.persistNonNamedModels(modelHashSet,TdbOperation.dataSetAnalytic);

        HashMap<String,Model> modelHashMap = TdbOperation.unpersistModelsMap(TdbOperation.dataSetAnalytic);

        Set<String> keys = modelHashMap.keySet();
        Model model ;
        for(String key : keys)
        {
            model = modelHashMap.get(key);
            System.out.println("*************************"+key+"*********************");
            ConsolidationTest.afficherModel(model);
        }

    }

    public static void writeresultsInFile(HashMap<String, ResultSet> resultSetHashMap, String writingFilePath) {
        File file = new File(writingFilePath);
        BufferedWriter bw = null;
        try {
            if (!file.isFile()) file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file, true));
            Set<String> keyset = resultSetHashMap.keySet();
            ResultSet resultSet;
            int i = 0;
            for (String key : keyset) {
                i++;
                bw.write("\n******************************************* Requete " + i + " *******************************************\n");
                bw.write(key);
                resultSet = resultSetHashMap.get(key);
                while (resultSet.hasNext()) {
                    bw.write(resultSet.next().toString() + "\n");
                }
                bw.write("\n****************************** Fin de la requete *******************************************\n");
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

class AnalyticQuery {
    protected Query selectQuery;
    protected Query constructQuery;
    protected String aggregVariables;

    public String getAggregVariable() {

        aggregVariables = selectQuery.getAggregators().get(0).getAggregator().getExprList().get(0).getVarName();
        return aggregVariables;
    }

}