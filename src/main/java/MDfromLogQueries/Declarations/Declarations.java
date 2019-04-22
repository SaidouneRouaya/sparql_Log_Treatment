package MDfromLogQueries.Declarations;

public class Declarations {
    public static String endpoint = "dbPedia";
    public static String root = "C:\\Users\\KamilaB\\Desktop\\3CS\\Prototypage\\Step_1\\endpoints\\"+endpoint+"\\Support_Files\\";
    public static String root2 = "C:\\Users\\KamilaB\\Desktop\\3CS\\Prototypage\\Step_1\\endpoints\\"+endpoint+"\\TdbDirectories\\";
    //public static String root = "C:\\Users\\pc\\Desktop\\PFE\\Files\\";
    //public static String root2 = "E:\\TdbDirectories\\";
    public static String rootScenarioAnalytic = "Analytic\\";
    public static String statististicsRoot = "Statistics\\";


    /** Queries log Path **/
    public static String directoryPath = root+"Data Log\\logs";

    /** Step 1 : Log Cleaning **/
    public static String cleanedQueriesFile = root + "ProgramOutput\\Fichier_log_Nettoye_Complet_Parallel.txt";
    public static String cleanedQueriesFileCopie = root+"ProgramOutput\\Fichier_log_Nettoye_Complet_Parallel_Scala_Copie.txt";
    public static String notCleanedQueries = root+"ProgramOutput\\not_Cleaned_Queries_File.txt";


    /** Step 2 : Queries deduplication **/
    public static String writingDedupFilePath = root+"ProgramOutput\\Fichier_log_dedup_Nettoye_java.txt";


    /** Step 3 : Syntactical Validation **/
    public static String syntaxValidFile = root+"ProgramOutput\\Fichier_Syntaxe_Valide.txt";
    public static String syntaxValidFile2 = root+"ProgramOutput\\Fichier_Syntaxe_Valide_scala.txt";
    public static String syntaxNonValidFile = root+"ProgramOutput\\Fichier_Syntaxe_Non_Valide.txt";
    public static String syntaxNonValidFile2 = root+"ProgramOutput\\Fichier_Syntaxe_Non_Valide_scala.txt";


    /** Step 4 : Add rdf:type variables and transform queries to Construct **/
    public static String logFile = root+"ProgramOutput\\Fichier_log.txt";
    public static String constructQueriesFile= root+"ProgramOutput\\Fichier_Construct_Queries.txt";
    public static String constructQueriesFile2= root+"ProgramOutput\\Fichier_Construct_Queries_scala.txt";
    public static String constructLogFileParallel = root+"ProgramOutput\\Fichier_log_Constuct_scala.txt";
    public static String constructLogFile = root + "ProgramOutput\\Fichier_log_construct_java.txt";

    /** Step 5 : Execution of queries **/
    public static String executionLogFile = root+"ProgramOutput\\Fichier_not_Executed_queries.txt";


    /** Resulting models statistics **/
    public static String statisticsFile = root+statististicsRoot+"Fichier_statistique.txt";
    public static String minstatisticsFile = root+statististicsRoot+"Fichier_statistique_min.txt";
    public static String maxstatisticsFile = root+statististicsRoot+"Fichier_statistique_max.txt";
    public static String avgstatisticsFile = root+statististicsRoot+"Fichier_statistique_avg.txt";
    public static String totalstatisticsFile = root+statististicsRoot+"Fichier_statistique_total.txt";
    public static String statisticsAnalyticFactFile = root + statististicsRoot+"Fichier_statistique_Analytique_Fact.txt";
    public static String statisticsAnalyticDimFile = root + statististicsRoot+"Fichier_statistique_Analytique_Dimension.txt";

    /** Default ontologies directory **/
    public static String defaultOntologiesDir = root+"Ontologies\\ontologies_namespaces";

    /** Triple databases directories **/
    public static String tdbDirectory = root2+"tdbDirectory";
    public static String originalTdbDirectory = root2+"originalTdbDirectory";
    public static String dataSetConsolidated = root2+"originalTdbDirConsolidated";
    public static String dataSetAnnotated = root2+"tdbDirectoryAnnotated";
    public static String _toString = root2+"_toString";
    public static String dataSetAnalytic = root2+rootScenarioAnalytic+"tdbDirectoryAnalytic";

    public static String propertiesOntology = root+"ontologies_namespaces\\addedOntology.ttl";
    public static String directoryPathCopie = "C:\\Users\\pc\\Desktop\\PFE\\Files\\DataLog\\test - Copie";
    public static String writingFilePath = root+"ProgramOutput\\Fichier_log_Nettoye_Complet_Parallel_test.txt";
    public static String writingFilePathCopie = root+"ProgramOutput\\Fichier_log_Nettoye_Complet_Parallel_Java_Copie.txt";
    public static String modelsFilePath = root+"ProgramOutput\\Fichier_models.rdf";
    public static String syntaxValidFileTest = root+"ProgramOutput\\Fichier_Syntaxe_Valide_Test.txt";


    /** Scénario 2 : Analytic Queries **/
    public static String AggregQueriesFile = root+rootScenarioAnalytic+"ProgramOutput\\Fichier_Analytic_Queries.txt";
    public static String AnalyticQueriesFile = root+rootScenarioAnalytic+"ProgramOutput\\Fichier_Analytic_Queries2.txt";
    public static String AnalyticSelectQueriesFile = root+rootScenarioAnalytic+"ProgramOutput\\Fichier_Analytic_Select_Queries.txt";
    public static String SelectQueriesAndResult = root+rootScenarioAnalytic+"ProgramOutput\\Fichier_Analytic_Select_Queries_Results.txt";

    /** Execution time of programs **/
    public static String timesFilePath = root + "ProgramOutput\\Fichier_Temps.txt";


}
