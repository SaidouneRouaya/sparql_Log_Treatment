/*package MDPatternDetection;

import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.util.*;

import org.apache.jena.sparql.core.BasicPattern;
import MDFromQueryLogs.SyntacticalValidation.QueryFixer;
import java.io.*;
import java.util.regex.Pattern;
import java.util.Collection;

import Utils.SharedFunctions;
import MDFromQueryLogs.Deduplicate;

public class AnalyticQueries {
    private OntProperty currentProperty= null;
    private static String endpoint = "https://dbpedia.org/sparql";
    private static Property rdfTypeProp = RDF.type;

    private static Triple tripleExists(Triple theTriple, BasicPattern existingTriples)
    {
        Iterator<Triple> iterator = existingTriples.iterator();
        boolean exist = false;
        Triple  triple= null;
        while (iterator.hasNext() && !exist)
        {
            triple = iterator.next();
            if (triple.getSubject().matches(theTriple.getSubject()) && triple.getPredicate().matches(theTriple.getPredicate()))
                exist =true;
        }
        if (exist)
            return triple;
        else
            return null;
    }

    public static void main(String args[]) {
        int ap = 0;
        int i = 0;
        int j = 0;
        int r = 0;
        String linee = "";
        String SelectQ = "";
        String Query = "";
        String S="";
        String ReadingFilePath = "C:\\Users\\HP\\Desktop\\LOG GEO\\";
        File rep = new File(ReadingFilePath);
        File[] fichiersJava = rep.listFiles();
        Collection<String> CTest = new ArrayList<String>();
        Pattern PATTERN = Pattern.compile("[^\"]*\"(?:GET )?/sparql/?\\?([^\"\\s\\n]*)[^\"]*\".*");

       for (File var : fichiersJava) {
            String path = ReadingFilePath + var.getName();
            Matcher matcher = PATTERN.matcher("");
            System.out.println(path);
            Collection<String> C = SharedFunctions.ReadFile(path);
            for (String line : C) {

                i++;
                r = line.lastIndexOf("SELECT");
                if (r != -1){

                    j = line.lastIndexOf("&query=");
                    if (j!=-1) Query = line.substring(j + 7).trim();
                    j = Query.lastIndexOf("format");
                    if (j != -1) Query = Query.substring(0, j);
                    j = Query.indexOf("?query=");
                    if (j != -1) Query = Query.substring(j + 7).trim();
                    }
                    SelectQ = (URLEncodedUtils.parse(Query, Charset.forName("UTF-8"))).toString();
                    j = SelectQ.lastIndexOf("HTTP/1.1");
                    if (j!=-1) SelectQ = SelectQ.substring(0, j);
                    j = SelectQ.lastIndexOf(", debug=on");
                    if (j!=-1) SelectQ = SelectQ.substring(0, j);
                    j = SelectQ.lastIndexOf("]");
                    if (j!=-1) SelectQ = SelectQ.substring(0, j);


                    if (SelectQ != "") {
                        SelectQ = SelectQ.substring(1).trim();
                        SelectQ = SelectQ.replace(",", " ");

                        if(SelectQ.contains("COUNT(")||SelectQ.contains("COUNT (")||SelectQ.contains("count(")||SelectQ.contains("count (")||SelectQ.contains("Count(")||SelectQ.contains("Count (")
                           ||SelectQ.contains("SUM(")||SelectQ.contains("SUM (")||SelectQ.contains("sum(")||SelectQ.contains("sum (")||SelectQ.contains("Sum(")||SelectQ.contains("Sum (")
                                ||SelectQ.contains("AVG(")||SelectQ.contains("AVG (")||SelectQ.contains("avg(")||SelectQ.contains("avg (")||SelectQ.contains("Avg(")||SelectQ.contains("Avg (")
                        ) {
                             //ap++;
                             SelectQ = QueryFixer.get().fix(SelectQ);

                             try {
                                 Query q = QueryFixer.toQuery(SelectQ);
                                 //if (q.hasAggregators()) {
                                  SelectQ = q.toString();
                                   CTest.add(SelectQ);
                                // }
                             } catch (Exception e) {}
                         //}
                    }
                }
            }

        //SharedFunctions.WriteInFile("C:\\Users\\HP\\Desktop\\Files\\ProgramOutput\\MytestsGEO.txt",CTest);
        //Deduplicate.Dedup("C:\\Users\\HP\\Desktop\\Files\\ProgramOutput\\MytestsGEO.txt", "C:\\Users\\HP\\Desktop\\Files\\ProgramOutput\\NewMytestsGEO.txt");
        /*String paths="C:\\Users\\HP\\Desktop\\Files\\ProgramOutput\\NewMytestsTreated2.txt";
        Collection<String> DeduplicateQueries = SharedFunctions.ReadFile(paths);
        ArrayList<String> lines= new ArrayList(DeduplicateQueries);
        int size=0;
        ArrayList<Query> constructQueriesList = new ArrayList();

        for (String line : lines) {
            try {
                Query query = QueryFixer.toQuery(line);
                //String SS= query.getQueryPattern().toString();
                String SS = query.toString();

                //if ((!SS.contains("count(*)") || !SS.contains("Count(*)")) && query.getProjectVars().size()==1) {
                    size++;
                    System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
                    //System.out.println("Query before union treatement:\n "+query);
                    BasicPattern bp = new BasicPattern();
                   /* if (SS.contains("UNION") || SS.contains("union") || SS.contains("Union")) {
                        String[] AR1 = SS.split("UNION");
                        AR1[0] = AR1[0].replace("{", " ");
                        AR1[0] = AR1[0].replace("WHERE", "WHERE {");
                        AR1[0] = QueryFixer.get().fix(AR1[0]);
                        query = QueryFixer.toQuery(AR1[0]);
                        //System.out.println("AR1: "+query);
                    }*/

//String rqte=query.toString().replace("{"," ").replace("WHERE","WHERE{").replace("}")
//System.out.println("Query after union treatement:\n"+rqte);
//System.out.println("Query after union treatement:\n"+query);
//System.out.println("BP:\n"+query.getQueryPattern());

                    /*Element ee = query.getQueryPattern();
                    if (ee instanceof ElementGroup) {
                        Element e = ((ElementGroup) ee).getElements().get(0);
                        if (e instanceof ElementPathBlock) {
                            ElementPathBlock e1 = (ElementPathBlock) e;
                            PathBlock pBlk = e1.getPattern();
                            Resource subject;
                            Graph graph= new CollectionGraph();
                            for (TriplePath tp : pBlk) {
                                //System.out.println("Subject: "+tp.getSubject()+"; Predicate: "+tp.getPredicate()+"; Object: "+tp.getObject());
                                Triple t = Triple.create(tp.getSubject(), tp.getPredicate(), tp.getObject());
                                bp.add(t);
                                graph.add(t);
                                //if(tp.getPredicate().isVariable() && (tp.getSubject().isVariable()||tp.getObject().isVariable())) System.out.println("I'm predicate var not legal"+tp.getPredicate());
                                //else System.out.println("I'm not predicate var: "+tp.getPredicate());
                                //System.out.println("Subject: "+tp.getSubject()+". Classe subject: "+tp.getSubject().getClass());
                           }

                            Model queryModel = new ModelCom(graph); // We use a model to parse the graph by its subject -> properties -> objects
                            Iterator nodeIterator = queryModel.listSubjects();
                            int n=0;
                            BasicPattern bpModified=bp;

                            //System.out.println("Before: "+bp);
                            QueryConstruction qqq=new QueryConstruction(bpModified, bp);
                            while (nodeIterator.hasNext()) { // for every subject we verify whether it has an rdf:type property in the origin basic pattern
                                Node subjectRDFTypeValue;

                                qqq=new QueryConstruction(bpModified, bp);
                                subject = (Resource) nodeIterator.next();
                                subjectRDFTypeValue = qqq.verifyRDFTypeProperty(subject, n, rdfTypeProp, "sub" ); //
                                bp=qqq.getExistingTriples();
                                n++;
                                qqq.propertyIterate(subject, subjectRDFTypeValue);
                                bp=qqq.getExistingTriples();
                            }
                            //System.out.println("New pattern construct: "+qqq.getBpConstruct());
                            Query qr=QueryFactory.create(query);
                            qr.setQueryConstructType();
                            Template constructTemplate = new Template(bp);
                            qr.setConstructTemplate(constructTemplate);
                            //System.out.println("New pattern: \n"+qr);
                            constructQueriesList.add(qr);
                        } else if (e instanceof ElementTriplesBlock) {}
                    }
                }
            catch(Exception E){}
        }
        String endPoint = "https://dbpedia.org/sparql";
        ArrayList<Model> aray=new ArrayList<>();
        QueryExecutor queryExecution=new QueryExecutor();
        queryExecution.executeQuiersInFile2(endPoint,constructQueriesList);
        Collection<String> col = new ArrayList<>();

        for (Query m:constructQueriesList){
            col.add(m.toString());
        }

        /*
         * Passer les requtes Count comme entrées: 9requetes/26 du fichier consutuct
         */
        /*String Req1="PREFIX  skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX  category: <http://0.0.0.0/category/>\n" +
                "CONSTRUCT {\n" +
                "?ob1 skos:broader category:Paintings_by_nationality .\n" +
                "?sub0 skos:broader ?ob1.\n" +
                "?sub2 skos:subject ?y .\n" +
                "}\n" +
                "WHERE{\n" +
                "?x skos:broader category:Paintings_by_nationality .\n" +
                "?y skos:broader ?x .\n" +
                "?a skos:subject ?y .\n" +
                "?y rdf:type ?sub0 .\n" +
                "?x rdf:type ?ob1 .\n" +
                "category:Paintings_by_nationality rdf:type ?ob1 .\n" +
                "?a rdf:type ?sub2 .\n" +
                "}\n" +
                "LIMIT   50";
        String Req2="PREFIX  :     <http://dbpedia.org/resource/>\n" +
                "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX  skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX  dbpedia: <http://dbpedia.org/>\n" +
                "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX  category: <http://0.0.0.0/category/>\n" +
                "PREFIX  dbpedia2: <http://dbpedia.org/property/>\n" +
                "PREFIX  foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "PREFIX  db:   <http://dbpedia.org/ontology/>\n" +
                "PREFIX  dc:   <http://purl.org/dc/elements/1.1/>\n" +
                "CONSTRUCT{\n" +
                "?sub0 skos:broader ?ob1 .\n" +
                "?sub1 skos:subject ?sub0 .\n" +
                "}\n" +
                "WHERE{ \n" +
                "?x skos:broader category:Paintings_by_artist .\n" +
                "?a skos:subject ?x .\n" +
                "?x rdf:type ?sub0 .\n" +
                "category:Paintings_by_artist rdf:type ?ob1 .\n" +
                "?a rdf:type ?sub1 .\n" +
                "}LIMIT   50";

        String Req3="PREFIX  :     <http://dbpedia.org/resource/>\n" +
                "PREFIX  dbo:  <http://dbpedia.org/ontology/>\n" +
                "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX  skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX  dbpedia: <http://dbpedia.org/>\n" +
                "PREFIX  dbpedia2: <http://dbpedia.org/property/>\n" +
                "PREFIX  foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "PREFIX  dc:   <http://purl.org/dc/elements/1.1/>\n" +
                "CONSTRUCT{\n" +
                "?sub0  <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?ob1 .\n" +
                "?sub0  <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?ob2 .\n" +
                "?sub0  rdfs:label ?label .\n" +
                "?sub0  skos:subject ?ob3 .\n" +
                " }\n" +
                "WHERE{ \n" +
                "?element <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat .\n" +
                "?element <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?long .\n" +
                "?element rdfs:label ?label .\n" +
                "?element skos:subject ?subject .\n" +
                "?element rdf:type ?sub0 .\n" +
                "?lat rdf:type ?ob1 .\n" +
                "?long rdf:type ?ob2 .\n" +
                "?subject rdf:type ?ob3 .\n" +
                "FILTER ( lang(?label) = \"en\" )}";


        SharedFunctions.WriteInFile("C:\\Users\\HP\\Desktop\\Files\\ProgramOutput\\ConstructTreatedData.txt",col);
        System.out.println("Total NB reuqests of log: "+i);
        System.out.println("Total NB analytical requtes of log: "+ap);
        System.out.println("Total NB Valid analytical requtes of log: "+CTest.size());
        System.out.println("Deduplicated valid analytical queries: "+DeduplicateQueries.size());
        System.out.println("Analytic queries: "+size);
        System.out.println("construct queries: "+constructQueriesList.size());
        System.out.println("Aray excecuted Construct Size: "+aray.size());

        String triples =
                "<http://dbpedia.org/resource/53debf646ad3465872522651> <http://dbpedia.org/resource/end> <http://dbpedia.org/resource/1407106906391>." +
                        "\n<http://dbpedia.org/resource/53debf676ad3465872522655> <http://dbpedia.org/resource/foi> <http://dbpedia.org/resource/SpatialThing>.";


        Model model = null;
        try {
            model = ModelFactory.createDefaultModel()
                    .read(IOUtils.toInputStream(triples, "UTF-8"), null, "N-TRIPLES");
            int n=0;
            //System.out.println(model.listStatements());



        } catch (IOException e) {
            System.out.println("Error");
        }

    }


    }
*/