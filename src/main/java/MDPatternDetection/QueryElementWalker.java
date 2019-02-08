package MDPatternDetection;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;

import java.util.Iterator;

public class QueryElementWalker extends ElementVisitorBase {

    private BasicPattern basicPattern = new BasicPattern();
    private BasicPattern basicPatternOpt = new BasicPattern();
    private boolean where;
    private boolean optional;

    public void walker(Element element, BasicPattern BP, BasicPattern BPopt) {
        where = true;
        optional = true;

        basicPattern = BP;
        basicPatternOpt = BPopt;

        ElementWalker.walk(element, this);
    }

    public void walker(Element element, BasicPattern BP) {
        where = true;
        optional = false;
        basicPattern = BP;
        ElementWalker.walk(element, this);
    }

    public void walkerOpt(Element element, BasicPattern BPopt) {
        where = false;
        optional = true;
        basicPatternOpt = BPopt;

        ElementWalker.walk(element, this);
    }



    @Override
    public void visit(ElementPathBlock el) {

        Iterator<Triple> BP_iterator;
        /** if this is the first block i.e. the first time to visit the elment pathBlock **/

        System.out.println("**************************************************");
        if (where) {
            BP_iterator = basicPattern.getList().iterator();

            System.out.println("Where && ! optional" + where + !optional);
            while (BP_iterator.hasNext()) {
                el.getPattern().add(new TriplePath(BP_iterator.next()));
            }

        } else {

        }
        if (!where && optional) {
            BP_iterator = basicPatternOpt.getList().iterator();
            System.out.println("! Where && optional" + !where + optional);
            while (BP_iterator.hasNext()) {
                el.getPattern().add(new TriplePath(BP_iterator.next()));
            }
        }

    }

 /*   @Override
    public void visit(ElementOptional el) {

      //  Iterator<Triple> BP_iterator = basicPattern.getList().iterator();

        //  walker(el, basicPattern);
        optional=true;
        where= false;
        System.out.println("je suis dans visit element optional");

    }
*/






}
