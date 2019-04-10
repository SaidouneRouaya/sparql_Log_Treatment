package MDfromLogQueries.SPARQLSyntacticalValidation;


import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.Syntax;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class fix every query Syntactically
 **/
public class QueryFixer {


    private static final QueryFixer INSTANCE = new QueryFixer();
    private static final Pattern PREFIX_PATTERN = Pattern.compile("(PREFIX\\s+)?([A-Za-z][\\w-]*):[^/]");
    private Map<String, String> predefinedMap;

    public QueryFixer(Map<String, String> predefinedMap) {
        this.predefinedMap = predefinedMap;
    }

    public QueryFixer() {
        this(loadMap());
    }

    public static QueryFixer get() {
        return INSTANCE;
    }
//TODO Faire en sorte de ne garder que les requêtes SELECT et convertir les construct en select

    private final static String TEST_QUERY_STR1 = "PREFIX a: <http://a/>\n" +
            "\n" +
            "# 12:34\n" +
            "SELECT ?x ?x ?x ?y WHERE {\n" +
            "?x a:a ?y.\n" +
            "?x b:b ?y.\n" +
            "?x b:b ?y.\n" +
            "?x b:b ?y.\n" +
            "?x c-x:c-x ?y.\n" +
            "OPTIONAL { ?x d:d ?y }" +
            "}";

    public static void main(String[] args) {
        String fixedQueryStr = new QueryFixer().fix(TEST_QUERY_STR1);
        /* System.out.println(fixedQueryStr.toString()); */
    }


    /**
     * Load the file containing different name spaces  to use them to verify the query's namespace
     **/
    private static Map<String, String> loadMap() {
        try (InputStream inputStream = Resources.getResourceAsStream("ns_map.yaml")) {
            Yaml yaml = new Yaml();
            Object object = yaml.load(inputStream);

            if (object instanceof Map) {
                /*System.out.println(((Map) object).get("rdf").toString());*/
                return (Map<String, String>) object;
            }
        } catch (IOException ignored) {
        }
        return new HashMap<>();
    }

    /** Eliminates commas if present in the query **/

    private static String fixSelectWithCommas(String queryStr) {
        Pattern ps = Pattern.compile("(?i:SELECT(?: DISTINCT)? (.*)(WHERE|FROM|\\{))");
        System.out.println("je rentre ici");
        Matcher ms = ps.matcher(queryStr);
        if (!ms.find()) {
            System.out.println("je sors par la ");
            return queryStr;
        }
        String select = ms.group(1);
        System.out.println(" the select : "+select);
        Pattern pv = Pattern.compile("(\\?[\\w_-]+)[\\s]*,\\s?");
        Matcher mv = pv.matcher(select);
        if (!mv.find()) {
            return queryStr;
        }
        String repl = mv.replaceAll("$1 ");
        return queryStr.replace(select, repl);
    }

    private static String fixAggregators(String queryStr)
    {
        Pattern ps = Pattern.compile("(?i:SELECT(?: DISTINCT)? (.*\n*)(WHERE|FROM|\\{))");
        Matcher ms = ps.matcher(queryStr);
        if (!ms.find()) {
            return queryStr;
        }
        String select = ms.group(1);
 //       Pattern aggregPattern = Pattern.compile("(( |[(])(count|SUM|AVG|MIN|MAX)( distinct)?( |[(]))",Pattern.CASE_INSENSITIVE);
        Pattern aggregPattern = Pattern.compile("( ([(]*)( )*(count|SUM|AVG|MIN|MAX)( distinct)?( |\\())",Pattern.CASE_INSENSITIVE);
        Matcher mv = aggregPattern.matcher(queryStr);
        if (mv.find()) {
            /* Put the aggregator ex : COUNT(?var) as ?var2 between brackets to respect Jena Syntax */
            System.out.println("groupe 2 "+mv.group(2));
            if (mv.group(2).isEmpty())
                queryStr = aggregatorBetweenBrackets(queryStr);
            if (mv.group(4) != null)
            {
                queryStr = aggregatorDistinctBetweenBrackets(queryStr);
            }
            /* Adds all variables that are in the select clause to the group by clause */
            Pattern variablesPattern = Pattern.compile("(\\?[\\w_-]+)");
            Matcher matcherVariables = variablesPattern.matcher(select);
            String concatStr="";
            if (matcherVariables.find())
            {
                /*finds the variable associated to the aggregation */
                ArrayList<String> stringArrayList = asVariables(select);
                 concatStr = " GROUP BY ";
                concatStr = concatStr+" "+matcherVariables.group(0);
                while (matcherVariables.find())
                {
                    if (!stringArrayList.contains(matcherVariables.group(0)))
                        concatStr = concatStr+" "+matcherVariables.group(0)+" ";
                }
                //queryStr= queryStr+concatStr;
            }
            Pattern endOfRequestPattern = Pattern.compile("((.*})(([^{}]*)$))");
            Matcher matcherEndOfRequest = endOfRequestPattern.matcher(queryStr);
            if (matcherEndOfRequest.find())
            {
                String endofRequestStr = matcherEndOfRequest.group(4);
                Pattern groupByPattern = Pattern.compile("(GROUP BY )");
                Matcher matcherGroupBy = groupByPattern.matcher(endofRequestStr);
                if(matcherGroupBy.find())
                {
                    endofRequestStr = endofRequestStr.replace(matcherGroupBy.group(0),concatStr);
                }
                else {
                    endofRequestStr = concatStr+endofRequestStr;
                }
                endofRequestStr = endofRequestStr.replace(".","");
                queryStr =  matcherEndOfRequest.group(2)+endofRequestStr;
            }
        }
        return queryStr;
    }

    private static String aggregatorBetweenBrackets(String queryStr)
    {
        Pattern aggregatorPattern = Pattern.compile("(COUNT|SUM|AVG|MIN|MAX)([ ]*[\\(])([* \\?\\w_-]*)(\\))( as )+(\\?[\\w_-]+)",Pattern.CASE_INSENSITIVE);
        Matcher mv = aggregatorPattern.matcher(queryStr);
        while (mv.find())
        {
            String str = mv.group(0);
            queryStr = queryStr.replace(str,"("+str+")");
        }
        return queryStr;
    }

    private static String aggregatorDistinctBetweenBrackets(String queryStr)
    {
        Pattern aggregatorPattern = Pattern.compile("(( |[(])(count|SUM|AVG|MIN|MAX)( )*(distinct (\\?[\\w_-]+)))",Pattern.CASE_INSENSITIVE);
        Matcher mv = aggregatorPattern.matcher(queryStr);
        while (mv.find())
        {
            String str = mv.group(5);
            queryStr = queryStr.replace(str,"("+str+")");
        }
        return queryStr;
    }

    private static ArrayList<String> asVariables(String select)
    {
        ArrayList<String> stringList = new ArrayList<>();
        Pattern asPattern = Pattern.compile("((as )(\\?[\\w_-]+))",Pattern.CASE_INSENSITIVE);
        Matcher matcherAs = asPattern.matcher(select);
        while (matcherAs.find())
        {
            stringList.add(matcherAs.group(3));
        }
        return stringList;
    }
    /** Finds the undeclared prefixes **/
    private static Set<String> findUndeclared(String queryStr) {
        Matcher m = PREFIX_PATTERN.matcher(queryStr);
        Set<String> declared = new LinkedHashSet<>();
        Set<String> usedOrUndeclared = new LinkedHashSet<>();
        while (m.find()) {
            String keyword = m.group(1);
            String prefix = m.group(2);
            if (keyword != null) {
                declared.add(prefix);
            } else {
                usedOrUndeclared.add(prefix);
            }
        }
        usedOrUndeclared.removeAll(declared);
        return usedOrUndeclared;
    }

    /** Transform a query string to object Query **/
    public static Query toQuery(String queryStr) {
        Query maybeQuery = null;
        try {
            maybeQuery = QueryFactory.create(queryStr, Syntax.syntaxARQ);
        }catch (QueryParseException queryParseException)
        {
           System.out.println("erreur 2");
            System.out.println("*****+-+-+-+-*****"+queryStr);
            System.out.println("+++++-*+++++*-+"+queryParseException.getMessage());
        }
        catch (Exception e) {
            System.out.println("*****+-+-+-+-*****"+queryStr);
            System.out.println("*****+-+-+-+-*****");
            e.printStackTrace();
        }
        return maybeQuery;
    }

    /**
     * Fix the undeclared nameSpaces and prefixes (declare them)
     **/

    private String fixUndeclared(String queryStr, Set<String> undeclared) {
        StringBuilder sb = new StringBuilder();
        for (String prefix : undeclared) {
            String val = predefinedMap.get(prefix);
            sb.append("PREFIX ");
            sb.append(prefix);
            sb.append(": <");
            if (val != null) {
                sb.append(val);
                sb.append(">  # PREDEFINED\n");
            } else {
                sb.append("http://0.0.0.0/");
                sb.append(prefix);
                sb.append("/>  # GENERATED\n");
            }
        }
        if (sb.length() == 0) {
            return queryStr;
        } else {
            sb.append(queryStr);
            return sb.toString();
        }
    }

    /**
     * Returns the final valid string of the query
     **/
    public String fix(String queryStr) {
        queryStr = fixSelectWithCommas(queryStr);
        queryStr = fixAggregators(queryStr);
        Set<String> undeclared = findUndeclared(queryStr);
        if (undeclared.isEmpty()) {
            return queryStr;
        } else {
            return fixUndeclared(queryStr, undeclared);
        }
    }
}