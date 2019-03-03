package MDPatternDetection.AnnotationClasses;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Resource;

import java.util.List;

public class Fact {
    Resource factNode;
    List<Triple> attributes;

    public void addAttribute(Triple triple)
    {
        attributes.add(triple);
    }

    public void setFactNode(Resource factNode) {
        this.factNode = factNode;
    }
}
