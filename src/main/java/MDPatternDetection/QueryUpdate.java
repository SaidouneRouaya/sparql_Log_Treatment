package MDPatternDetection;


import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.BasicPattern;

public class QueryUpdate {

    public static void main(String[] args) {

        final String queryString = "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
                "PREFIX dbo: <http://dbpedia.org/ontology/>" +
                "SELECT ?title WHERE {" +
                "     ?game a dbo:Game  ." +
                "     ?game a dbo:Game  ." +
                "     ?game a dbo:Game  ." +
                "    OPTIONAL { ?game foaf:name ?title }." +
                " OPTIONAL { ?game foaf:name ?title } " +
                "} ORDER by ?title limit 10";


        final String queryString3 = "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
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
                "    OPTIONAL { ?game foaf:name ?title }." +
                " OPTIONAL { ?game foaf:name ?title } " +
                "} ORDER by ?title limit 10";


        final String queryString2 = "" +
                "SELECT * WHERE {\n" +
                " ?a ?b ?c1 ;\n" +
                "    ?b ?c2 .\n" +
                " ?d ?e ?f .\n" +
                " ?g ?h ?i .\n" +
                "OPTIONAL { ?p ?q ?r .\n" +
                "  ?d ?e2 ?f2 . }\n" +
                "}";


        final Query query = QueryFactory.create(queryString);
        System.out.println("== before ==\n" + query);

        BasicPattern bp = new BasicPattern();
        Triple triple = new Triple(new Node_Variable("sub"), new Node_Variable("pred"), new Node_Variable("obj"));
        Triple triple2 = new Triple(new Node_Variable("sub2"), new Node_Variable("pred2"), new Node_Variable("obj2"));
        Triple triple3 = new Triple(new Node_Variable("sub3"), new Node_Variable("pred3"), new Node_Variable("obj3"));

        bp.add(triple);
        bp.add(triple2);
        bp.add(triple3);
        addGP2Query(query, bp);

          /*  ElementWalker.walk( query.getQueryPattern(),
                    new ElementVisitorBase() {
                        @Override
                        public void visit(ElementPathBlock el) {
                            ListIterator<TriplePath> it = el.getPattern().iterator();
                            while ( it.hasNext() ) {
                                final TriplePath tp = it.next();
                                System.out.println("Triple Pattern"+tp.toString());
                                final Var d = Var.alloc( "d" );
                                if ( tp.getSubject().equals( d )) {
                                    it.add( new TriplePath( new Triple( d, d, d )));
                                }
                            }
                        }
                    });*/
        System.out.println("\n\n\n== after ==\n" + query);
    }

    public static Query addGP2Query(Query query, final BasicPattern BP) {

        QueryElementWalker qew = new QueryElementWalker();
        BasicPattern basicPattern = new BasicPattern();
        basicPattern.add(new Triple(new Node_Variable("SubjOptional"), new Node_Variable("PredOptional"), new Node_Variable("ObjOptional")));

        //    qew.walker(query.getQueryPattern(), BP, basicPattern);
        //qew.walker(query.getQueryPattern(), BP);
        qew.walkerOpt(query.getQueryPattern(), basicPattern);



      /*MyOpVisitorBase opV = new MyOpVisitorBase();
        opV.myOpVisitorWalker(Algebra.compile(query), BP);


            query.setQueryPattern();

        /**Test avec model **/
        //  Model model = ModelFactory.createDefaultModel();
        // .asTriple()

        //Statement st =null;


        //model.add(st);

        return query;
    }


}


