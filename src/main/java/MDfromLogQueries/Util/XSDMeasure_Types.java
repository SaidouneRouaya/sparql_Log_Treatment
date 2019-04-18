package MDfromLogQueries.Util;


import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;

import java.util.HashSet;

public class XSDMeasure_Types {

    public static HashSet<Resource> types;

    public XSDMeasure_Types() {
        types.add(XSD.integer);
        types.add(XSD.xdouble);
        types.add(XSD.xlong);
        types.add(XSD.xfloat);
        types.add(XSD.xint);
        types.add(XSD.time);
        types.add(XSD.decimal);
        types.add(XSD.negativeInteger);
        types.add(XSD.xshort);
    }
}
