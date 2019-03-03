package MDPatternDetection.AnnotationClasses;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.RDFNode;

import java.util.List;

public class Dimension {
    RDFNode dimensionNode;
    List<RDFNode> dimensionLevels;
    List<Triple> dimensionAttributes;

    public Dimension(RDFNode e_dimensionNode)
    {
        dimensionNode = e_dimensionNode;
    }
}
