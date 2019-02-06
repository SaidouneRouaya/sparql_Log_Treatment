package MDPatternDetection;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.*;

import java.util.Iterator;

public class QueryElementWalker extends ElementVisitorBase {
    private BasicPattern basicPattern = new BasicPattern();


    public void walker(Element element, BasicPattern BP) {

        if (!(element instanceof ElementOptional)) {

            basicPattern = BP;

        } else {
            basicPattern = new BasicPattern();
        }
        ElementWalker.walk(element, this);
    }


    @Override
    public void visit(ElementPathBlock el) {


        Iterator<Triple> BP_iterator = basicPattern.getList().iterator();
        // pour chaque

        // while (BP_iterator.hasNext()) el.getPattern().add(new TriplePath(BP_iterator.next()));
        while (BP_iterator.hasNext()) {


            el.addTriplePath(new TriplePath(BP_iterator.next()));


            System.out.println("je passe dans le while");

        }
        System.out.println("je passe dans le EL Path Block");

    }


}
