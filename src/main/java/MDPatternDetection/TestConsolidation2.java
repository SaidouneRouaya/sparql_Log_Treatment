package MDPatternDetection;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.StatementImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TestConsolidation2 {
    public static void main(String[] args) {

        String endPoint = "https://dbpedia.org/sparql";
        ArrayList<Model> results = new ArrayList<>();

        try {

          /*  Queries2Graphes q2g = new Queries2Graphes();
            QueryExecutor queryExecutor = new QueryExecutor();
            ArrayList<Query> constructQueriesList = Queries2Graphes.TransformQueriesinFile(syntaxValidFileTest);



            // Execution de chaque requete Construct
            for (Query query : constructQueriesList) {
                //System.out.println(query.toString()+"\n");
                results.add(queryExecutor.executeQueryConstruct(query, endPoint));
            }

           */

            // for test issues

            for (int j = 0; j < 3; j++) {
                Model mod = ModelFactory.createDefaultModel();
                for (int i = 0; i < 4; i++) {

                    Statement s = new StatementImpl(ResourceFactory.createResource("Subj" + i), ResourceFactory.createProperty("predecate1" + j + i), ResourceFactory.createResource("obj1" + j + i));
                    Statement s1 = new StatementImpl(ResourceFactory.createResource("Subj" + i), ResourceFactory.createProperty("predecate2" + j + i), ResourceFactory.createResource("obj2" + j + i));
                    Statement s2 = new StatementImpl(ResourceFactory.createResource("Subj" + i), ResourceFactory.createProperty("predecate3" + j + i), ResourceFactory.createResource("obj3" + j + i));

                    Statement s3 = new StatementImpl(ResourceFactory.createResource("obj30" + i), ResourceFactory.createProperty("predecate1" + j + i), ResourceFactory.createResource("obj1" + j + i));
                    Statement s4 = new StatementImpl(ResourceFactory.createResource("obj30" + i), ResourceFactory.createProperty("predecate2" + j + i), ResourceFactory.createResource("obj2" + j + i));
                    Statement s5 = new StatementImpl(ResourceFactory.createResource("obj30" + i), ResourceFactory.createProperty("predecate3" + j + i), ResourceFactory.createResource("obj3" + j + i));

                    Statement s6 = new StatementImpl(ResourceFactory.createResource("obj21" + i), ResourceFactory.createProperty("predecate1" + j + i), ResourceFactory.createResource("obj1" + j + i));
                    Statement s7 = new StatementImpl(ResourceFactory.createResource("obj21" + i), ResourceFactory.createProperty("predecate2" + j + i), ResourceFactory.createResource("obj2" + j + i));
                    Statement s8 = new StatementImpl(ResourceFactory.createResource("obj21" + i), ResourceFactory.createProperty("predecate3" + j + i), ResourceFactory.createResource("obj3" + j + i));

                    if (j == 0) {
                        mod.add(s);
                        mod.add(s1);
                        mod.add(s2);
                    }
                    if (j == 1) {
                        mod.add(s3);
                        mod.add(s4);
                        mod.add(s5);
                    }
                    if (j == 2) {
                        mod.add(s6);
                        mod.add(s7);
                        mod.add(s8);
                    }
                }
                results.add(mod);
            }

            // App.afficherModels(results);

            System.out.println("------------------------------- AFFICHAGE DES RESULTATS ---------------------------------------");


            /* L'idée: stocker le sujet et son model */

            HashMap<String, Model> listInfoNodes = consolidate(results);


            //  afficherListInformations(listInfoNodes);
            System.out.println("\n\n/////////////////////////////////////////////////////////////////////////////////////////////\n\n");

            HashMap<String, Model> listInfoNodesFinal = consolidate(listInfoNodes);
            HashMap<String, Model> listInfoNodesFinal2;
            listInfoNodesFinal = consolidate(listInfoNodesFinal);
            // listInfoNodesFinal = consolidate(listInfoNodesFinal);
            // listInfoNodesFinal2 = consolidate(listInfoNodesFinal);


            //if (listInfoNodesFinal.equals(listInfoNodesFinal2)) System.out.println(" je suis vrai \n");
            //listInfoNodesFinal = consolidate(listInfoNodesFinal);
            afficherListInformations(listInfoNodesFinal);
            //afficherListInformations(listInfoNodesFinal2);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    public static void afficherListInformations(HashMap<String, Model> listInfoNodes) {

        Iterator it = listInfoNodes.entrySet().iterator();

        System.out.println(" Afichage des résultats \n");


        while (it.hasNext()) {
            Map.Entry<String, Model> pair = (Map.Entry) it.next();

            System.out.println(" Subject: \t\t " + pair.getKey() + "\n");
            Iterator<Statement> listStatements = pair.getValue().listStatements();
            while (listStatements.hasNext()) {
                System.out.println(listStatements.next().toString());

            }

            System.out.println("\n______________________________________________________________________\n");

            //   it.remove(); // avoids a ConcurrentModificationException
        }
    }


    public static HashMap<String, Model> consolidate(ArrayList<Model> results) {
        Statement statement;
        HashMap<String, Model> listInfoNodes = new HashMap<>();
        int numbersModels = 0;

        // For every model in results


        for (Model m : results) {
            Iterator<Statement> list = m.listStatements();
            // For every Statement (triple) in model
            while (list.hasNext()) {

                statement = list.next();
                String subject = statement.getSubject().toString();


                if (!listInfoNodes.containsKey(subject)) {
                    // if it doesn't exist , create a new instance
                    listInfoNodes.put(subject, ModelFactory.createDefaultModel());
                    numbersModels++;
                    listInfoNodes.get(subject).add(statement);

                } else {
                    // if the subject exists in listInfoNodes

                    // add the statement to the model
                    listInfoNodes.get(subject).add(statement);
                    //System.out.println("je suis la 1");
                    // test if the object doesn't exists in listInfoNodes
                }

             /*   String object = statement.getObject().toString();


                if (!listInfoNodes.containsKey(object)) {
                    // if yes, add a new instance where the object is a subject with numberAsObject = 1

                    listInfoNodes.put(object,  ModelFactory.createDefaultModel());
                    listInfoNodes.get(object).add(statement);
                    //  System.out.println("je suis la 2");

                } else { // if the object exists in listInfoNodes

                    // add the statement to the model
                    listInfoNodes.get(object).add(statement);
                    //System.out.println("je suis la 3");
                }
                //   m.remove(statement);
            */
            }
        }

        System.out.println("\n--------------- number of models :  " + numbersModels + "\n\n");

        return listInfoNodes;
    }

    public static HashMap<String, Model> consolidate(HashMap<String, Model> results) {
        Statement statement;
        HashMap<String, Model> newResults = new HashMap<>(results);
        //  HashMap<String, Model> newResults= new HashMap<>();

        boolean finish = false;
        Iterator it = results.entrySet().iterator();
        Map.Entry<String, Model> pair;

        while (!finish) {

            // while (!results.equals(newResults)) {
            while (it.hasNext()) {

                pair = (Map.Entry) it.next();

                Model m = pair.getValue();

                Iterator<Statement> list = m.listStatements();

                // For every Statement (triple) in the model


                while (list.hasNext()) {

                    statement = list.next();
                    String object = statement.getObject().toString();
                    String subject = statement.getSubject().toString();

                    if (results.containsKey(object)) {
                        finish = false;
                        results.get(subject).add(results.get(object));
                        newResults.get(subject).add(results.get(object));

                        newResults.values().remove(results.get(object));

                    } else finish = true;

                }
                System.out.println(finish + "\n");
                results = newResults;
            }

        }

        System.out.println("\n\n ----------------- number of models 2  " + newResults.size());

        return newResults;
    }


}

