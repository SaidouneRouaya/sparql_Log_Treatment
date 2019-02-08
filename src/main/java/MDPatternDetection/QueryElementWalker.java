package MDPatternDetection;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.*;

import java.util.Iterator;

public class QueryElementWalker extends ElementVisitorBase {

    private BasicPattern basicPattern = new BasicPattern();
    private BasicPattern basicPatternOpt = new BasicPattern();
    private boolean where = true;

    public void walker(Element element, BasicPattern BP, BasicPattern BPopt) {
        basicPattern = BP;
        basicPatternOpt = BPopt;

        ElementWalker.walk(element, this);
    }

    public void walker(Element element, BasicPattern BP) {
        basicPattern = BP;
        ElementWalker.walk(element, this);
    }

    public void walkerOpt(Element element, BasicPattern BPopt) {
        where = false;
        basicPatternOpt = BPopt;

        ElementWalker.walk(element, this);
    }



    @Override
    public void visit(ElementPathBlock el) {

        Iterator<Triple> BP_iterator;         /** if this is the first block i.e. the first time to visit the elment pathBlock **/

        if (where) {
            BP_iterator = basicPattern.getList().iterator();

            while (BP_iterator.hasNext()) {
                el.getPattern().add(new TriplePath(BP_iterator.next()));
            }
            where = false;
        } else {
            BP_iterator = basicPatternOpt.getList().iterator();

            while (BP_iterator.hasNext()) {
                el.getPattern().add(new TriplePath(BP_iterator.next()));
            }
        }

    }

    @Override
    public void visit(ElementOptional el) {

        Iterator<Triple> BP_iterator = basicPattern.getList().iterator();
        /** if this is the first block i.e. the first time to visit the elment pathBlock **/
        //  walker(el, basicPattern);
        System.out.println("je suis dans visit element optional");

    }







}
