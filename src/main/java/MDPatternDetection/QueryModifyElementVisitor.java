package MDPatternDetection;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.PathBlock;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;

import java.util.Iterator;
import java.util.List;

public class QueryModifyElementVisitor extends ElementVisitorBase {

    private BasicPattern basicPattern = new BasicPattern();
    private QueryConstruction queryConstruction;


    public void walker(Element element, QueryConstruction e_queryConstruction) {
        queryConstruction = e_queryConstruction;
        ElementWalker.walk(element, this);
    }



    @Override
    public void visit(ElementPathBlock el) {

        basicPattern = toBasicPattern(el.getPattern().getList());
        queryConstruction.completePattern(basicPattern);
        basicPattern = queryConstruction.getBpWhere();
        List<Triple> newTriples = basicPattern.getList();
        for(Triple triple : newTriples)
        {
            el.addTriple(triple);
        }
    }

    private BasicPattern toBasicPattern(List<TriplePath> triples)
    {
        BasicPattern basicPattern = new BasicPattern();
        for(TriplePath triplePath : triples)
        {
            basicPattern.add(triplePath.asTriple());
        }
        return basicPattern;
    }


}
