package MDPatternDetection;


import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Triple;

import java.util.List;

public class BasicGraphPattern {


    public Graph matchBGP(List<Triple> triples, Graph graph) {

        Graph g = null;
        for (Triple t : triples) {
            if (graph.contains(t)) {

                g.add(t);
            }

        }


        return g;
    }

}


