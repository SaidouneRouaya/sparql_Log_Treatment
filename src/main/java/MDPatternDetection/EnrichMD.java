package MDPatternDetection;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.util.ModelUtils;
import org.apache.jena.tdb.TDB;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnrichMD {
    public static int nbDim = 0;
    public static int nbDimLevel = 0;
    public static int nbParentLevel = 0;
    public static int nbDimAtt = 0;
    public static int nbNonFunctionalDim = 0;
    public static int nbNonFunctDimLevel = 0;
    public static int nbFact = 0;
    public static int nbFactAtt = 0;
    public static String endpoint = "http://linkedgeodata.org/sparql";

    public static void addNode(List<Statement> it3, Resource R, String S) {
        Node n = NodeFactory.createLiteral(S);
        Model model = ModelFactory.createDefaultModel();
        RDFNode n1 = ModelUtils.convertGraphNodeToRDFNode(n, model);
        Statement statement = ResourceFactory.createStatement(R, RDF.type, n1);
        it3.add(statement);
    }


    public static void main(String args[]) {

        TdbOperation tdb = new TdbOperation();
        // Dataset dataSetAnnotated2 = TDBFactory.createDataset("C:\\Users\\HP\\Desktop\\Linked Geo Data\\tdbDirectoryAnnotated");
        Dataset dataSetAnnotated2 = TdbOperation.dataSetAnnotated;
        TDB.sync(dataSetAnnotated2);
        List<String> it = new ArrayList<String>();
        Iterator<String> E = dataSetAnnotated2.listNames();

        try {
            while (E.hasNext()) {
                it.add(E.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (String name : it) {
                Model mi = dataSetAnnotated2.getNamedModel(name);
                NodeIterator it1 = mi.listObjects();
                NodeIterator it11 = mi.listObjects();
                List<Statement> it2 = mi.listStatements().toList();
                List<Statement> it3 = mi.listStatements().toList();

                int nbFactx = 0;
                int nbFactAttx = 0;

                while (it11.hasNext()) {
                    String vall = it11.next().toString();
                    if (vall.contains("FACT") && vall.length() == 4) nbFactx++;
                    if (vall.contains("FACTATTRIBUTE")) nbFactAttx++;
                }

                if (nbFactx > 0 & nbFactAttx > 0) {
                    while (it1.hasNext()) {
                        String val = it1.next().toString();
                        if (val.contains("FACT") && val.length() == 4) nbFact++;
                        if (val.contains("FACTATTRIBUTE")) nbFactAtt++;
                        if (val.contains("DIMENSION") && val.length() == 9) nbDim++;
                        if (val.contains("DIMENSIONLEVEL") && val.length() == 14) nbDimLevel++;
                        if (val.contains("PARENTLEVEL")) nbParentLevel++;
                        if (val.contains("DIMENSIONATTRIBUTE")) nbDimAtt++;
                        if (val.contains("NONFUNCTIONALDIMENSION") && val.length() == 22) nbNonFunctionalDim++;
                        if (val.contains("NONFUNCTIONALDIMENSIONLEVEL")) nbNonFunctDimLevel++;
                    }
                    for (Statement st : it2) {

                        String val2 = st.getObject().toString();
                        String val3 = st.getSubject().toString();
                        val3 = (URLEncodedUtils.parse(val3, Charset.forName("UTF-8"))).toString();

                        if (val3.contains(" ")) val3 = val3.replace(" ", "");
                        if (val3.contains("Ã²quia")) val3 = val3.replace("\"Ã²quia\"", "");
                        if (val3.contains("|")) val3 = val3.replace("|", "");

                        if (val2.contains("FACT") & val2.length() == 4) {
                            System.out.println("ORIGINAL FACT");
                            String query = "SELECT DISTINCT ?p ?o WHERE {<" + val3.substring(1, val3.lastIndexOf("]")) + "> ?p ?o.}";
                            QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
                            ResultSet results = qexec.execSelect();
                            QuerySolution r;
                            while (results.hasNext()) {
                                r = results.next();
                                Resource pp = (Resource) r.get("?p");
                                Resource oo;
                                if (r.get("?o").isLiteral()) oo = RDFS.Literal;
                                else oo = (Resource) r.get("?o");
                                if (pp.equals(RDFS.label)) { //
                                    System.out.println("\t\t\tFACTATTRIBUTE");
                                    //addNode(it3, RDFS.label, "FACTATTRIBUTE");
                                    nbFactAtt++;
                                } else if (pp.equals(RDFS.subClassOf)) {
                                    String query2 = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>  SELECT DISTINCT ?o WHERE {<" + val3.substring(1, val3.lastIndexOf("]")) + "> rdfs:subClassOf ?o.}";
                                    QueryExecution qexec2 = QueryExecutionFactory.sparqlService(endpoint, query2);
                                    ResultSet results2 = qexec2.execSelect();

                                    QuerySolution r2;
                                    while (results2.hasNext()) {
                                        r2 = results2.next();
                                        String val4 = r2.get("?o").toString();
                                        String query3 = "SELECT DISTINCT ?p ?o WHERE {<" + val4 + "> ?p ?o.}";
                                        QueryExecution qexec3 = QueryExecutionFactory.sparqlService(endpoint, query3);
                                        ResultSet results3 = qexec3.execSelect();
                                        QuerySolution r3;
                                        while (results3.hasNext()) {
                                            r3 = results3.next();
                                            Resource ppSC = (Resource) r3.get("?p");
                                            Resource ooSC;
                                            if (r3.get("?o").isLiteral()) ooSC = RDFS.Literal;
                                            else ooSC = (Resource) r3.get("?o");

                                            if (ppSC.equals(RDFS.label)) {
                                                //addNode(it3, RDFS.label, "FACTATTRIBUTE");
                                                System.out.println("\t\t\tFACTATTRIBUTE");
                                                nbFactAtt++;
                                            } else {
                                                String query5 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT DISTINCT ?o WHERE {<" + ppSC + "> rdf:type ?o.}";
                                                QueryExecution qexec4 = QueryExecutionFactory.sparqlService(endpoint, query5);
                                                ResultSet results5 = qexec4.execSelect();
                                                QuerySolution r5;
                                                while (results5.hasNext()) {
                                                    r5 = results5.next();
                                                    String val5 = r5.get("?o").toString();
                                                    if (val5.equals(OWL.ObjectProperty)) {
                                                        //addNode(it3, ooSC, "DIMENSION");
                                                        System.out.println("\t\t\tDIMENSION");
                                                        nbDim++;
                                                    } else if (val5.equals(OWL.DatatypeProperty)) {
                                                        //addNode(it3, ooSC, "FACTATTRIBUTE");
                                                        System.out.println("\t\t\tFACTATTRIBUTE");
                                                        nbFactAtt++;
                                                    } else if (ooSC.isLiteral()) {
                                                        //addNode(it3, ooSC, "FACTATTRIBUTE");
                                                        System.out.println("\t\t\tFACTATTRIBUTE");
                                                        nbFactAtt++;
                                                    }

                                                }
                                                qexec4.close();

                                            }
                                        }
                                        qexec3.close();
                                    }
                                    qexec2.close();
                                } else {
                                    String query2 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  SELECT DISTINCT ?o WHERE {<" + pp + "> rdf:type ?o.}";
                                    QueryExecution qexec2 = QueryExecutionFactory.sparqlService(endpoint, query2);
                                    ResultSet results2 = qexec2.execSelect();
                                    QuerySolution r2;
                                    while (results2.hasNext()) {
                                        r2 = results2.next();
                                        String val4 = r2.get("?o").toString();
                                        if (val4.equals(OWL.ObjectProperty)) {
                                            //addNode(it3, oo, "DIMENSION");
                                            System.out.println("\t\t\tDIMENSION");
                                            nbDim++;
                                        } else if (val4.equals(OWL.DatatypeProperty)) {
                                            // addNode(it3, oo, "FACTATTRIBUTE");
                                            System.out.println("\t\t\tFACTATTRIBUTE");
                                            nbFactAtt++;
                                        } else if (oo.isLiteral()) {
                                            // addNode(it3, oo, "FACTATTRIBUTE");
                                            System.out.println("\t\t\tFACTATTRIBUTE");
                                            nbFactAtt++;
                                        }

                                    }
                                    qexec2.close();
                                }
                            }
                            qexec.close();
                        }
/** ***********************************************************************************************************************************/
                        else if (val2.contains("DIMENSION") && (!val2.contains("DIMENSIONATTRIBUTE"))) {
                            System.out.println("ORIGINAL DIMENSION");
                            String query = "SELECT DISTINCT ?p ?o WHERE {<" + val3.substring(1, val3.lastIndexOf("]")) + "> ?p ?o.}";
                            QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
                            ResultSet results = qexec.execSelect();
                            QuerySolution r;
                            while (results.hasNext()) {
                                r = results.next();
                                Resource pp = (Resource) r.get("?p");
                                Resource oo;
                                if (r.get("?o").isLiteral()) oo = RDFS.Literal;
                                else oo = (Resource) r.get("?o");
                                if (pp.equals(RDFS.label)) {
                                    //addNode(it3, RDFS.label, "DIMENSIONATTRIBUTE");
                                    System.out.println("\t\t\tDIMENSIONATTRIBUTE");
                                    nbDimAtt++;
                                } else if (pp.equals(RDFS.subClassOf)) {
                                    String query2 = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>  SELECT DISTINCT ?o WHERE {<" + val3.substring(1, val3.lastIndexOf("]")) + "> rdfs:subClassOf ?o.}";
                                    QueryExecution qexec2 = QueryExecutionFactory.sparqlService(endpoint, query2);
                                    ResultSet results2 = qexec2.execSelect();

                                    QuerySolution r2;
                                    while (results2.hasNext()) {
                                        r2 = results2.next();
                                        String val4 = r2.get("?o").toString();
                                        String query3 = "SELECT DISTINCT ?p ?o WHERE {<" + val4 + "> ?p ?o.}";
                                        QueryExecution qexec3 = QueryExecutionFactory.sparqlService(endpoint, query3);
                                        ResultSet results3 = qexec3.execSelect();
                                        QuerySolution r3;
                                        while (results3.hasNext()) {
                                            r3 = results3.next();
                                            Resource ppSC = (Resource) r3.get("?p");
                                            Resource ooSC;
                                            if (r3.get("?o").isLiteral()) ooSC = RDFS.Literal;
                                            else ooSC = (Resource) r3.get("?o");
                                            if (ppSC.equals(RDFS.label)) {
                                                //addNode(it3, RDFS.label, "DIMENSIONATTRIBUTE");
                                                System.out.println("\t\t\tDIMENSIONATTRIBUTE");
                                                nbDimAtt++;
                                            } else {
                                                String query5 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  SELECT DISTINCT ?o WHERE {<" + ppSC + "> rdf:type ?o.}";
                                                QueryExecution qexec5 = QueryExecutionFactory.sparqlService(endpoint, query5);
                                                ResultSet results5 = qexec5.execSelect();
                                                QuerySolution r5;
                                                while (results5.hasNext()) {
                                                    r5 = results5.next();
                                                    String val5 = r5.get("?o").toString();
                                                    if (val5.equals(OWL.ObjectProperty)) {
                                                        //addNode(it3, ooSC, "DIMENSIONLEVEL");
                                                        System.out.println("\t\t\tDIMENSIONLEVEL");
                                                        nbDimLevel++;
                                                    } else if (val5.equals(OWL.DatatypeProperty)) {
                                                        // addNode(it3, ooSC, "DIMENSIONATTRIBUTE");
                                                        System.out.println("\t\t\tDIMENSIONATTRIBUTE");
                                                        nbDimAtt++;
                                                    } else if (ooSC.isLiteral()) {
                                                        //addNode(it3, ooSC, "DIMENSIONATTRIBUTE");
                                                        System.out.println("\t\t\tDIMENSIONATTRIBUTE");
                                                        nbDimAtt++;
                                                    }

                                                }
                                                qexec5.close();
                                            }
                                        }
                                        qexec3.close();
                                    }
                                    qexec2.close();
                                } else {
                                    String query2 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  SELECT DISTINCT ?o WHERE {<" + pp + "> rdf:type ?o.}";
                                    QueryExecution qexec2 = QueryExecutionFactory.sparqlService(endpoint, query2);
                                    ResultSet results2 = qexec2.execSelect();

                                    QuerySolution r2;
                                    while (results2.hasNext()) {
                                        r2 = results2.next();
                                        String val4 = r2.get("?o").toString();
                                        if (val4.equals(OWL.ObjectProperty)) {
                                            //(it3, oo, "DIMENSIONLEVEL");
                                            System.out.println("\t\t\tDIMENSIONLEVEL");
                                            nbDimLevel++;
                                        } else if (val4.equals(OWL.DatatypeProperty)) {
                                            //addNode(it3, oo, "DIMENSIONATTRIBUTE");
                                            System.out.println("\t\t\tDIMENSIONATTRIBUTE");
                                            nbDimAtt++;
                                        } else if (oo.isLiteral()) {
                                            //addNode(it3, oo, "DIMENSIONATTRIBUTE");
                                            System.out.println("\t\t\tDIMENSIONATTRIBUTE");
                                            nbDimAtt++;
                                        }
                                    }
                                    qexec2.close();
                                }

                            }
                            qexec.close();
                        }
                    }
                }
            }
            System.out.println("nbDim: " + nbDim);
            System.out.println("nbDimLevel: " + nbDimLevel);
            System.out.println("nbParentLevel: " + nbParentLevel);
            System.out.println("nbDimAtt: " + nbDimAtt);
            System.out.println("nbNonFunctionalDim: " + nbNonFunctionalDim);
            System.out.println("nbNonFunctDimLevel: " + nbNonFunctDimLevel);
            System.out.println("nbFact: " + nbFact);
            System.out.println("nbFactAtt: " + nbFactAtt);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
