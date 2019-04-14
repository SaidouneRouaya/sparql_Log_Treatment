package MDPatternDetection;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.TDBFactory;

import java.nio.charset.Charset;
import java.util.Iterator;

public class Enrich {

    public static void main(String args[]) {
        Dataset dataSetAnnotated2 = TDBFactory.createDataset("C:\\Users\\HP\\Desktop\\Linked Geo Data\\tdbDirectoryNew");
        //Dataset originalDataSet = TDBFactory.createDataset("C:\\Users\\HP\\Desktop\\Linked Geo Data\\tdbDirectoryNew");
        int i = 0;
        TDB.sync(dataSetAnnotated2);
        Iterator<String> it = dataSetAnnotated2.listNames();
        String name;
        try {
            while (it.hasNext()) {
                name = it.next();
                Model mi = dataSetAnnotated2.getNamedModel(name);
                StmtIterator it1 = mi.listStatements();
                i++;
                while (it1.hasNext()) {
                    Statement st = it1.next();
                    String val2 = st.getObject().toString();
                    String val3 = st.getSubject().toString();
                    val3 = (URLEncodedUtils.parse(val3, Charset.forName("UTF-8"))).toString();

                    if (val2.contains("FACT") & val2.length() == 4) {

                        String query = "SELECT DISTINCT ?p WHERE {<" + val3.substring(1, val3.lastIndexOf("]")) + "> ?p ?o.}";
                        System.out.println(query);
                        Query q = QueryFactory.create(query);
                        String endpoint = "http://linkedgeodata.org/sparql";
                        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
                        ResultSet results = qexec.execSelect();
                        qexec.close();
                        System.out.println("Gotten");

                    }
                }
            }
            System.out.println(i);
        } catch (Exception e) {
            System.out.println(i);
            e.printStackTrace();
        }


    }
}
