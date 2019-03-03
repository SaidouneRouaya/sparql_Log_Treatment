package MDPatternDetection;


import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static MDfromLogQueries.Declarations.Declarations.syntaxValidFileTest;
//TODO delete this class

public class App {
   /* public static void main(String[] args) {
       /* String queryStr =
                "PREFIX foaf:  <http://xmlns.com/foaf/0.1/>" +
                        "SELECT DISTINCT ?name ?nick" +
                        "{?x foaf:mbox <mailt:person@server> ." +
                        "?x foaf:name ?name " +
                        "OPTIONAL { ?x foaf:nick ?nick }}";
         String queryStr = "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
                "PREFIX dc: <http://purl.org/dc/elements/1.1/>" +
                "PREFIX : <http://dbpedia.org/resource/>" +
                "PREFIX dbo: <http://dbpedia.org/ontology/>" +
                "PREFIX dbpedia2: <http://dbpedia.org/property/>" +
                "PREFIX dbpedia: <http://dbpedia.org/>" +
                "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" +
                "SELECT ?title WHERE {" +
                "     ?game a dbo:Game  ." +
                "     ?game a dbo:Game  ." +
                "     ?game a dbo:Game  ." +
                "     ?game foaf:name ?title ." +
                "} ORDER by ?title limit 10";

        Query query = QueryFactory.create(queryStr);
        System.out.println("*************" + query.toString());
        Op op = Algebra.compile(query);
        System.out.println(op.toString());
        //System.out.println(movb.executeQuery(queryStr).getResourceModel().size());
        movb.myOpVisitorWalker(op);
        movb.contructGraph();



        String queryStr2 = "CONSTRUCT " +
                " {  ?sub1 <http://dbpedia.org/property/type> ?ob1 . }" +
                "WHERE { <http://dbpedia.org/resource/Vandalia%2C_Illinois> " +
                "<http://dbpedia.org/property/type>  ?value ; " +
                " a  ?sub1 . ?value  a  ?ob1 }";

        String queryStr = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
                " PREFIX dbo: <http://dbpedia.org/ontology/> " +
                "SELECT ?title WHERE { ?game a dbo:Game  . ?game foaf:name ?title .} " +
                "ORDER by ?title limit 10";

    }*/

    public static void main(String[] args) {

        String endPoint = "https://dbpedia.org/sparql";


        try {

            Queries2Graphes q2g = new Queries2Graphes();
            QueryExecutor queryExecutor = new QueryExecutor();
            ArrayList<Query> constructQueriesList = Queries2Graphes.TransformQueriesinFile(syntaxValidFileTest);

            ArrayList<Model> results = new ArrayList<>();

            // Execution de chaque requete Construct
            for (Query query : constructQueriesList) {
                query.setLimit(10);
                //   System.out.println("*************   " + query.toString());
                results.add(queryExecutor.executeQueryConstruct(query, endPoint));
            }

            // affichage
            System.out.println("------------------------------- AFFICHAGE DES RESULTATS ---------------------------------------");
            afficherModels(results);


            Statement statement;
            HashMap<String, EachNodeSInformation> listInfoNodes = new HashMap<>();


            // For every model in results
            for (Model m : results) {
                Iterator<Statement> list = m.listStatements();
                // For every Statement (triple) in model
                while (list.hasNext()) {

                    statement = list.next();
                    String subject = statement.getSubject().toString();

                    // if the subject is not in listInfoNodes as a subject
                    if (!listInfoNodes.containsKey(subject)) {
                        // if it doesn't exist , create a new instance with numberAsSubject = 1
                        listInfoNodes.put(subject, new EachNodeSInformation(statement.getSubject(), 1, 0, statement, null));

                    } else {// if the subject exists in listInfoNodes

                        // increment number of subjects and add the statement too the list
                        listInfoNodes.get(subject).setNumberAsSubject();
                        listInfoNodes.get(subject).getListAsSubject().add(statement);


                        // test if the object doesn't exists in listInfoNodes
                        String object = statement.getObject().toString();
                        if (!listInfoNodes.containsKey(object)) {
                            // if yes, add a new instance where the object is a subject with numberAsObject = 1
                            listInfoNodes.put(object, new EachNodeSInformation(statement.getObject().asResource(), 0, 1, null, statement));
                        } else { // if the object exists in listInfoNodes
                            // increment number of object and add the statement too the list
                            listInfoNodes.get(object).setNumberAsObject();
                            listInfoNodes.get(object).getListAsObject().add(statement);

                        }

                    }

                }
            }


            afficherListInformations2(listInfoNodes);

            // Consolidation


            // loop on listInfoNodes, for every node which appears as subject more then object
            // Create it's new model that's composed of it's sub models contained in Node.listAsSubject

         /*  Iterator it = listInfoNodes.entrySet().iterator();
            Map.Entry<String, EachNodeSInformation> pair;

            ArrayList<Model> resultsS = new ArrayList<>();
            ArrayList<Model> resultsO = new ArrayList<>();
            while (it.hasNext()) {

               pair = (Map.Entry) it.next();

               ArrayList<Statement> listAsObject =pair.getValue().getListAsObject();
               ArrayList<Statement> listAsSubject = pair.getValue().getListAsSubject();

               if (pair.getValue().getNumberAsSubject()>pair.getValue().getNumberAsObject()) {

                   Model model = ModelFactory.createDefaultModel();


                  // consolidate all the statements and build a model with a unique subject

                    for (Statement st:listAsSubject)
                    {
                        model.add(st);
                        // delete the statement from the list
                    }

                    // add the resulting model to results
                    resultsS.add(model);

                   Model modell = ModelFactory.createDefaultModel();

                   // create a model from the remaining statements
                if (pair.getValue().getNumberAsObject()!=0)
                {
                    for (Statement st:listAsObject)
                    {
                        modell.add(st);
                        // delete the statement from the list
                    }
                    resultsO.add(modell);
                }

               }
               else

                it.remove(); // avoids a ConcurrentModificationException
            }




            System.out.println("########## Affichage apres consolidation number As subject ##########");
          afficherModels(resultsS);
            System.out.println("\n\n\n\n ########## Affichage apres consolidation du reste des statements ##########");
          afficherModels(resultsO);

/*
            Model finalModel = ModelFactory.createDefaultModel();

            for (Model m : results) {
                finalModel.add(m);
            }


            // affichage apres consolidation

            Iterator<Statement> list = finalModel.listStatements();

            while (list.hasNext()) {
                System.out.println(list.next().toString() + "\n");

            }

            System.out.println("number of subjects" + finalModel.listSubjects().toList().size());
            for (Resource res : finalModel.listSubjects().toList()) {
                System.out.println(" subject = " + res.toString() + "\n");
            }
            */

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    //TODO a enlever après

    public static void afficherModels(ArrayList<Model> results) {

        Statement statement;
        for (Model m : results) {
            System.out.println("________________________ NEW MODEL ____________________________________\n");

            Iterator<Resource> listSubjects = m.listSubjects();

            while (listSubjects.hasNext()) {
                Resource sub = listSubjects.next();
                Iterator<Statement> listProp = sub.listProperties();

                while (listProp.hasNext()) {
                    System.out.println(" \t\t\t " + listProp.next().toString());
                }

            }


            /*Iterator<Statement> list = m.listStatements();

            while (list.hasNext()) {
                statement = list.next();
                System.out.println("Subject \t" + statement.getSubject().toString() + "\n");
                System.out.println("\t\tStatement \t" + statement.toString() + "\n");

            }*/
            System.out.println("_____________________________ END _______________________________\n");

        }


    }

    //TODO a enlever après
    public static void afficherListInformations(HashMap<String, EachNodeSInformation> listInfoNodes) {

        Iterator it = listInfoNodes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, EachNodeSInformation> pair = (Map.Entry) it.next();

            System.out.println(pair.getKey() + " : \n -------------- List as Subject --------------");

            for (Statement st : pair.getValue().getListAsSubject()) {
                System.out.println(st.toString() + "\n");
            }

            System.out.println("\n --------------------------------- List as Object --------------");

            for (Statement st : pair.getValue().getListAsObject()) {
                System.out.println(st.toString() + "\n");
            }
            System.out.println("\n###########################################################################\n\n\n");

            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public static void afficherListInformations2(HashMap<String, EachNodeSInformation> listInfoNodes) {

        Iterator it = listInfoNodes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, EachNodeSInformation> pair = (Map.Entry) it.next();

            System.out.println(pair.getKey() + " : \n -------------- Number as Subject : \t " + pair.getValue().getNumberAsSubject());

            System.out.println("\t -------------- Number as Object : " + pair.getValue().getNumberAsObject() + "\n");

            System.out.println("\n______________________________________________________________________\n\n\n");

            it.remove(); // avoids a ConcurrentModificationException
        }
    }

}






