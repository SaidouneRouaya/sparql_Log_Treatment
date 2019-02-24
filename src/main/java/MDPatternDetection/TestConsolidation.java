package MDPatternDetection;

import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.Iterator;

import static MDfromLogQueries.Declarations.Declarations.syntaxValidFileTest;

public class TestConsolidation {


    public static void main(String[] args) {

        String endPoint = "https://dbpedia.org/sparql";


        try {

            QueryExecutor queryExecutor = new QueryExecutor();
            ArrayList<Query> constructQueriesList = Queries2Graphes.TransformQueriesinFile(syntaxValidFileTest);

            ArrayList<Model> results = new ArrayList<>();

            // Execution de chaque requete Construct
            for (Query query : constructQueriesList) {
                query.setLimit(10);
                results.add(queryExecutor.executeQueryConstruct(query, endPoint));
            }
            System.out.println("------------------------------- AFFICHAGE DES RESULTATS ---------------------------------------");

            int i = 0;
            int numSubjects = 0;
            Resource subject = null;
            Iterator<Statement> list = null;
            for (Model m : results) {
                i++;
                System.out.println("List of subjects of the model number " + i + "\n");


                if (i == 1) {
                    subject = m.listSubjects().toList().get(0);

                    System.out.println(" \tsubject = " + subject.toString() + "\n");
                    list = subject.listProperties();
                } else if (m.listSubjects().toList().contains(subject)) {
                    list = subject.listProperties();
                }


                while (list.hasNext()) {
                    System.out.println("\t\t\t " + list.next().toString());
                }


            }

            Model finalModel = ModelFactory.createDefaultModel();
            for (Model m : results) {
                finalModel.add(m);
            }


            int num = 0;
            System.out.println("List of subjects of final Model ");

            ArrayList<Resource> listt = (ArrayList) finalModel.listSubjects().toList();

            for (Resource res : listt) {
                System.out.println(" \tsubject after consolidation = " + res.toString() + "\n");

                Iterator<Statement> it = res.listProperties();

                while (it.hasNext()) {
                    System.out.println("\t\t\t " + it.next().toString());
                }


            }


        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}