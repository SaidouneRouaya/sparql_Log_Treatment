package MDPatternDetection.ValidationClasses;


import MDfromLogQueries.Util.FileOperation;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.TDBFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/* j'ai fais une validation manuelle dans ce code
 * et j'ai regroupé les fact par thème (voir la longues liste de if dans la suite du code
 * */
public class Validation {
    public static int nbDim = 0;
    public static int nbDimLevel = 0;
    public static int nbParentLevel = 0;
    public static int nbDimAtt = 0;
    public static int nbNonFunctionalDim = 0;
    public static int nbNonFunctDimLevel = 0;
    public static int nbFact = 0;
    public static int nbFactAtt = 0;
    public static int nbGraph = 0;
    public static String endpoint = "https://dbpedia.org/sparql";
    public static Collection<String> col = new ArrayList<>();

    public static void main(String args[]) {
        Dataset dataSetAnnotated2 = TDBFactory.createDataset("C:\\Users\\HP\\Documents\\tdbDirectoryAnnotated\\tdbDirectoryAnnotated");
        TDB.sync(dataSetAnnotated2);
        List<String> it = new ArrayList<String>();
        Iterator<String> E = dataSetAnnotated2.listNames();
        try {
            while (E.hasNext()) {
                it.add(E.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (String name : it) {
                Model mi = dataSetAnnotated2.getNamedModel(name);
                NodeIterator it11 = mi.listObjects();
                List<Statement> it2 = mi.listStatements().toList();
                int nbFactx = 0;
                int nbFactAttx = 0;
                int nbDimx = 0;
                while (it11.hasNext()) {
                    String vall = it11.next().toString();
                    if (vall.contains("FACT") && vall.length() == 4) nbFactx++;
                    if (vall.contains("FACTATTRIBUTE")) nbFactAttx++;
                    if ((vall.contains("DIMENSION")) && !(vall.contains("DIMENSIONATTRIBUTE")) && !(vall.contains("DIMENSIONLEVEL")))
                        nbDimx++;
                }
                /****************Keep just facts with attributes & Dimensions***************************/
                if (nbFactx > 0 && nbFactAttx > 0 && nbDimx > 0) {
                    String phrase = "----------------------------------------Graph-----------------------------------------";
                    Collection<Statement> mm = new ArrayList<>();
                    Collection<String> test = new ArrayList<>();
                    for (Statement st : it2) {
                        /*
                        j'ai éliminé tt les triplets without annotation , je ne garde que les triplets de type MD
                         */
                        if ((st.getObject().toString().contains("NONFUNCTIONALDIMENSIONLEVEL")) || (st.getObject().toString().contains("NONFUNCTIONALDIMENSION")) ||
                                (st.getObject().toString().contains("DIMENSIONATTRIBUTE")) || (st.getObject().toString().contains("PARENTLEVEL")) ||
                                (st.getObject().toString().contains("DIMENSIONLEVEL")) || (st.getObject().toString().contains("DIMENSION")) ||
                                (st.getObject().toString().contains("FACTATTRIBUTE")) || (st.getObject().toString().contains("FACT") && st.getObject().toString().length() == 4
                        )) {
                            if (!((st.getSubject().toString().toLowerCase().contains("core#concept")) && (st.getObject().toString().contains("NONFUNCTIONALDIMENSION")))
                            ) {
                                if (!((st.getSubject().toString().toLowerCase().contains("owl#thing")) && (st.getObject().toString().contains("NONFUNCTIONALDIMENSION")))
                                ) {
                                    if (!((st.getSubject().toString().toLowerCase().contains("xmlschema#double")) && (st.getObject().toString().contains("NONFUNCTIONALDIMENSION")))
                                    ) {
                                        if (!((st.getSubject().toString().toLowerCase().contains("ns#langstring")) && (st.getObject().toString().contains("NONFUNCTIONALDIMENSION")))
                                        ) {
                                            if (!((st.getSubject().toString().toLowerCase().contains("xmlschema#nonnegativeinteger")) && (st.getObject().toString().contains("NONFUNCTIONALDIMENSION")))
                                            ) {
                                                if (!((st.getSubject().toString().toLowerCase().contains("xmlschema#string")) && (st.getObject().toString().contains("NONFUNCTIONALDIMENSION")))
                                                ) {
                                                    if (!((st.getSubject().toString().toLowerCase().contains("xmlschema#integer")) && (st.getObject().toString().contains("NONFUNCTIONALDIMENSION")))
                                                    ) {
                                                        if (!((st.getSubject().toString().toLowerCase().contains("owl#class")) && (st.getObject().toString().contains("NONFUNCTIONALDIMENSION")))
                                                        ) {
                                                            if (!((st.getSubject().toString().toLowerCase().contains("/image")) && (st.getObject().toString().contains("NONFUNCTIONALDIMENSION")))
                                                            ) {
                                                                if (!((st.getSubject().toString().toLowerCase().contains("#class")) && (st.getObject().toString().contains("NONFUNCTIONALDIMENSION")))
                                                                ) {

                                                                  /*
                                                                  ici je voulais renommer les fact/dim/level... en supprimant les chiffres de leurs noms.

                                                                   */
                                                                    String ob = st.getSubject().toString();
                                                                    int j = 0;
                                                                    for (int i = 0; i < 10; i++) {
                                                                        String a = "" + i + "";
                                                                        while (ob.contains(a)) {
                                                                            j = ob.lastIndexOf(a);
                                                                            ob = ob.substring(0, j) + ob.substring(j + 1);

                                                                        }
                                                                    }
                                                                    /* après je fais une déduplication (càd après élimination de chiffres, on tombe sur des dim/levels avec le mm nom. donc je supprime les duplications.
                                                                     * pour les attributs, j'ai pas dédupliqué*/
                                                                    if (!st.getObject().toString().contains("ATTRIBUTE") && !st.getObject().toString().contains("FACT")) {
                                                                        if (!test.contains(ob)) {
                                                                            mm.add(st);
                                                                            test.add(ob);
                                                                        }
                                                                    } else {
                                                                        mm.add(st);
                                                                        test.add(ob);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    int nbfacts2 = 0;
                    int nbfactsattrib2 = 0;
                    int nbdimension2 = 0;
                    /*
                    je ne garde pas les graph avec seulement deux triplets
                     */
                    if (mm.size() > 2) {
                        for (Statement r : mm) {
                            if (r.getObject().toString().contains("FACT") && r.getObject().toString().length() == 4)
                                nbfacts2++;
                            if (r.getObject().toString().contains("FACTATTRIBUTE")) nbfactsattrib2++;
                            if ((r.getObject().toString().contains("DIMENSION")) && !(r.getObject().toString().contains("DIMENSIONATTRIBUTE")))
                                nbdimension2++;
                        }
                        /*
                        je garde tt les graphs ayant au moins 1 fact attribute /et 1 dim
                         */
                        if (nbfacts2 > 0 && nbfactsattrib2 > 0 && nbdimension2 > 0) {
                            boolean resp = true;
                        /*
                        je renomme les facts like I done above . le mm principe (pour éliminer les fact dupliquée)
                         */
                            for (Statement r : mm) {
                                String s = r.getObject().toString();
                                if ((s.contains("FACT") && s.length() == 4)) {
                                    String ss = r.getSubject().toString();
                                    int j = 0;
                                    for (int i = 0; i < 10; i++) {
                                        String a = "" + i + "";
                                        while (ss.contains(a)) {
                                            j = ss.lastIndexOf(a);
                                            ss = ss.substring(0, j) + ss.substring(j + 1);
                                        }
                                    }
                                /*
                                C'est à ce niv que je fais le groupement des facts
                                 */
                                    if (ss.contains("ArtMuseumsAndGalleries")) ss = "ArtMuseumsAndGalleries";
                                    if (ss.contains("CitiesAndTownsIn")) ss = "CitiesAndTownsIn";
                                    if (ss.contains("WikicatAbbots")) ss = "Abbots";
                                    if (ss.contains("WikicatAcademicsOf")) ss = "AcademicsOf";
                                    if (ss.contains("WikicatAcademies")) ss = "Academies";
                                    if (ss.contains("WikicatAccidentsAndIncidents")) ss = "AccidentsAndIncidents";
                                    if (ss.contains("WikicatActors")) ss = "Actors";
                                    if (ss.contains("WikicatActresses")) ss = "Actors";
                                    if (ss.contains("WikicatAdaptationsOfWorksBy")) ss = "AdaptationsOfWorksBy";
                                    if (ss.contains("WikicatAdministrators")) ss = "Administrators";
                                    if (ss.contains("WikicatAirfieldsOfTheUnitedStatesArmyAirForces"))
                                        ss = "AirfieldsOfTheUnitedStatesArmyAirForces";
                                    if (ss.contains("WikicatAirlinerAccidentsAndIncidents"))
                                        ss = "AirlinerAccidentsAndIncidents";
                                    if (ss.contains("WikicatAirlinesOf")) ss = "AirlinesOf"; //
                                    if (ss.contains("WikicatAirports")) ss = "Airports";
                                    if (ss.contains("WikicatAlbumsArrangedBy")) ss = "AlbumsArrangedBy";
                                    if (ss.contains("WikicatAlbumsBy")) ss = "AlbumsBy";
                                    if (ss.contains("WikicatAlbumsConductedBy")) ss = "AlbumsConductedBy";
                                    if (ss.contains("WikicatAlbumsProducedBy")) ss = "AlbumsProducedBy";
                                    if (ss.contains("WikicatAlbumsRecorded")) ss = "AlbumsRecorded";
                                    if (ss.contains("WikicatAlbumsWithCoverArt")) ss = "AlbumsWithCoverArt";
                                    if (ss.contains("WikicatAluminiumCompanies")) ss = "AluminiumCompanies";
                                    if (ss.contains("WikicatAlumniOf")) ss = "universityAlumniOf";
                                    if (ss.contains("WikicatAmbassadors")) ss = "Ambassadors";
                                    if (ss.contains("WikicatApostolicNunciosTo")) ss = "ApostolicNunciosTo"; //
                                    if (ss.contains("WikicatArchaeologicalSites")) ss = "ArchaeologicalSites";
                                    if (ss.contains("WikicatArchbishops")) ss = "Archbishops";
                                    if (ss.contains("WikicatArchdeacons")) ss = "Archdeacons";
                                    if (ss.contains("WikicatArchitects")) ss = "Architects";
                                    if (ss.contains("WikicatArchitectureFirms")) ss = "ArchitectureFirms";
                                    if (ss.contains("WikicatArtists")) ss = "Artists";
                                    if (ss.contains("WikicatAssassinated")) ss = "Assassinated"; //
                                    if (ss.contains("WikicatAsteroidsNamedFrom")) ss = "AsteroidsNamedFrom";
                                    if (ss.contains("WikicatATeam")) ss = "ATeam";
                                    if (ss.contains("WikicatAthletesFrom")) ss = "AthletesFrom";
                                    if (ss.contains("WikicatAutomotiveCompaniesOf")) ss = "AutomotiveCompaniesOf";
                                    if (ss.contains("WikicatAuxiliaryShipsOf")) ss = "AuxiliaryShipsOf";
                                    if (ss.contains("WikicatAviationAccidentsAndIncidents"))
                                        ss = "AviationAccidentsAndIncidents";
                                    if (ss.contains("WikicatAviatorsFrom")) ss = "AviatorsFrom";
                                    if (ss.contains("WikicatAviatorsFrom")) ss = "WikicatAviatorsFrom";
                                    if (ss.contains("WikicatBanksBased")) ss = "BanksBased";
                                    if (ss.contains("WikicatBanksOf")) ss = "BanksOf";
                                    if (ss.contains("WikicatBaseballPlayersFrom")) ss = "BaseballPlayersFrom";
                                    if (ss.contains("WikicatBasketballPlayers")) ss = "BasketballPlayers";
                                    if (ss.contains("WikicatBasketballTeams")) ss = "BasketballTeams";
                                    if (ss.contains("WikicatBasketballVenues")) ss = "BasketballVenues";
                                    if (ss.contains("WikicatBattles")) ss = "Battles";
                                    if (ss.contains("WikicatBays")) ss = "Bays";
                                    if (ss.contains("WikicatBeachesOf")) ss = "Beaches";
                                    if (ss.contains("WikicatBeautyPageants")) ss = "BeautyPageants";
                                    if (ss.contains("WikicatBiographicalFilmsAbout")) ss = "BiographicalFilmsAbout";
                                    if (ss.contains("WikicatBirdsOf")) ss = "BirdsOf";
                                    if (ss.contains("WikicatBishops")) ss = "Bishops";
                                    if (ss.contains("WikicatBoardingSchoolsIn")) ss = "BoardingSchoolsIn";
                                    if (ss.contains("WikicatBodiesOfWater")) ss = "BodiesOfWater";
                                    if (ss.contains("WikicatBooksAbout")) ss = "BooksAbout";
                                    if (ss.contains("WikicatBooksBy")) ss = "BooksBy";
                                    if (ss.contains("WikicatBotanicalGardensIn")) ss = "WikicatBotanicalGardensIn";
                                    if (ss.contains("WikicatBoxersAt")) ss = "WikicatBoxersAt";
                                    if (ss.contains("WikicatBoxersFrom")) ss = "WikicatBoxersFrom";
                                    if (ss.contains("WikicatBoys'SchoolsIn")) ss = "Boys'SchoolsIn";
                                    if (ss.contains("WikicatBridgesIn")) ss = "BridgesIn";
                                    if (ss.contains("WikicatBuildingsAndStructuresIn")) ss = "BuildingsAndStructuresIn";
                                    if (ss.contains("WikicatBuildingsAndStructuresOnTheNationalRegisterOfHistoricPlacesIn"))
                                        ss = "BuildingsAndStructuresOnTheNationalRegisterOfHistoricPlacesIn";
                                    if (ss.contains("WikicatBuildingsAndStructuresUnderConstructionIn"))
                                        ss = "BuildingsAndStructuresUnderConstructionIn";
                                    if (ss.contains("WikicatBusinessSchools")) ss = "BusinessSchools";
                                    if (ss.contains("WikicatCemeteriesIn")) ss = "CemeteriesIn";
                                    if (ss.contains("WikicatCensus-designatedPlacesIn"))
                                        ss = "Census-designatedPlacesIn";
                                    if (ss.contains("WikicatCharactersCreatedBy")) ss = "CharactersCreatedBy";
                                    if (ss.contains("WikicatCharitiesBasedIn")) ss = "CharitiesBasedIn";
                                    if (ss.contains("WikicatCharterSchoolsIn")) ss = "CharterSchoolsIn"; //
                                    if (ss.contains("WikicatChiefJusticesOf")) ss = "ChiefJusticesOf";
                                    if (ss.contains("WikicatChiefMinistersOf")) ss = "ChiefMinistersOf";
                                    if (ss.contains("WikicatChiefsOf")) ss = "ChiefsOf";
                                    if (ss.contains("WikicatChristianMissionariesIn")) ss = "ChristianMissionariesIn";
                                    if (ss.contains("WikicatChurchesIn")) ss = "ChurchesIn";
                                    if (ss.contains("WikicatCitiesIn")) ss = "CitiesIn";
                                    if (ss.contains("WikicatCivilServantsIn")) ss = "CivilServantsIn";
                                    if (ss.contains("WikicatCoalMinesIn")) ss = "CoalMinesIn";
                                    if (ss.contains("WikicatCoalTownsIn")) ss = "CoalTownsIn";
                                    if (ss.contains("WikicatComicsBy")) ss = "WikicatComicsBy";
                                    if (ss.contains("WikicatCommandersOf")) ss = "CommandersOf";
                                    if (ss.contains("WikicatCommercialBuildingsOn")) ss = "CommercialBuildingsOn";
                                    if (ss.contains("WikicatCommissionersOf")) ss = "CommissionersOf"; //
                                    if (ss.contains("WikicatCommonwealthGamesCompetitorsFor"))
                                        ss = "CommonwealthGamesCompetitorsFor";
                                    if (ss.contains("WikicatCommonwealthGamesSilverMedallists"))
                                        ss = "CommonwealthGamesSilverMedallists";
                                    if (ss.contains("WikicatCommunesIn")) ss = "CommunesIn";
                                    if (ss.contains("WikicatCommunesOf")) ss = "CommunesOf";
                                    if (ss.contains("WikicatCommunistPartiesIn")) ss = "WikicatCommunistPartiesIn";
                                    if (ss.contains("WikicatCommunityCollegesIn")) ss = "WikicatCommunityCollegesIn";
                                    if (ss.contains("WikicatCommunitySchoolsIn")) ss = "CommunitySchoolsIn";
                                    if (ss.contains("WikicatCompaniesBasedIn")) ss = "CompaniesBasedIn";
                                    if (ss.contains("WikicatCompaniesListedOn")) ss = "CompaniesListedOn";
                                    if (ss.contains("WikicatCompaniesOf")) ss = "CompaniesOf";
                                    if (ss.contains("WikicatComprehensiveSchoolsIn")) ss = "ComprehensiveSchoolsIn";
                                    if (ss.contains("WikicatConservativePartiesIn"))
                                        ss = "WikicatConservativePartiesIn";
                                    if (ss.contains("WikicatContemporaryHitRadioStationsIn"))
                                        ss = "WikicatContemporaryHitRadioStationsIn";
                                    if (ss.contains("WikicatConventionCentersIn")) ss = "ConventionCentersIn";
                                    if (ss.contains("WikicatConvertsTo")) ss = "WikicatConvertsTo";
                                    if (ss.contains("WikicatCouncillorsIn")) ss = "WikicatCouncillorsIn";
                                    if (ss.contains("WikicatCountessesOf")) ss = "WikicatCountessesOf";
                                    if (ss.contains("WikicatCountiesOf")) ss = "WikicatCountiesOf";
                                    if (ss.contains("WikicatCountryHousesIn")) ss = "WikicatCountryHousesIn";
                                    if (ss.contains("WikicatCountsOf")) ss = "WikicatCountsOf";
                                    if (ss.contains("WikicatCountyCommissionersIn"))
                                        ss = "WikicatCountyCommissionersIn";
                                    if (ss.contains("WikicatCountyRoadsIn")) ss = "WikicatCountyRoadsIn";
                                    if (ss.contains("WikicatCountyRoutesIn")) ss = "CountyRoutesIns";
                                    if (ss.contains("WikicatCricketersFrom")) ss = "WikicatCricketersFrom";
                                    if (ss.contains("WikicatCricketGroundsIn")) ss = "WikicatCricketGroundsIn";
                                    if (ss.contains("WikicatCruisersOf")) ss = "WikicatCruisersOf";
                                    if (ss.contains("WikicatCycleRacesIn")) ss = "WikicatCycleRacesIn";
                                    if (ss.contains("WikicatDamsIn")) ss = "WikicatDamsIn";
                                    if (ss.contains("WikicatDefunctAirlinesOf")) ss = "WikicatDefunctAirlinesOf";
                                    if (ss.contains("WikicatDefunctCompaniesBasedIn"))
                                        ss = "WikicatDefunctCompaniesBasedIn";
                                    if (ss.contains("WikicatDefunctPoliticalPartiesIn"))
                                        ss = "WikicatDefunctPoliticalPartiesIn";
                                    if (ss.contains("WikicatDefunctPrisonsIn")) ss = "WikicatDefunctPrisonsIn";
                                    if (ss.contains("WikicatDefunctSchoolsIn")) ss = "WikicatDefunctSchoolsIn";
                                    if (ss.contains("WikicatDefunctSportsVenuesIn"))
                                        ss = "WikicatDefunctSportsVenuesIn";
                                    if (ss.contains("WikicatDelegatesToTheUnitedStatesHouseOfRepresentativesFrom"))
                                        ss = "WikicatDelegatesToTheUnitedStatesHouseOfRepresentativesFrom";
                                    if (ss.contains("WikicatDeputiesOf")) ss = "WikicatDeputiesOf";
                                    if (ss.contains("WikicatDeputyChiefsOf")) ss = "DeputyChiefsOf"; //
                                    if (ss.contains("WikicatDeputyLieutenantsOf")) ss = "DeputyLieutenantsOf";
                                    if (ss.contains("WikicatDeputyPremiersOf")) ss = "WikicatDeputyPremiersOf";
                                    if (ss.contains("WikicatDeputyPrimeMinistersOf"))
                                        ss = "WikicatDeputyPrimeMinistersOf";
                                    if (ss.contains("WikicatDirectorsOf")) ss = "WikicatDirectorsOf";
                                    if (ss.contains("WikicatDisastersIn")) ss = "WikicatDisastersIn";
                                    if (ss.contains("WikicatDiscographiesOf")) ss = "WikicatDiscographiesOf";
                                    if (ss.contains("WikicatDistrictAttorneysIn")) ss = "WikicatDistrictAttorneysIn";
                                    if (ss.contains("WikicatDistrictsOf")) ss = "WikicatDistrictsOf";
                                    if (ss.contains("WikicatDisusedRailwayStationsIn"))
                                        ss = "WikicatDisusedRailwayStationsIn";
                                    if (ss.contains("WikicatDocumentaryFilmsAbout"))
                                        ss = "WikicatDocumentaryFilmsAbout";
                                    if (ss.contains("WikicatDuchessesOf")) ss = "DuchessesOf";
                                    if (ss.contains("WikicatDukesOf")) ss = "WikicatDukesOf";
                                    if (ss.contains("WikicatDutchPeopleOf")) ss = "WikicatDutchPeopleOf";
                                    if (ss.contains("WikicatEarthquakesIn")) ss = "WikicatEarthquakesIn";
                                    if (ss.contains("WikicatEducationalInstitutionsIn"))
                                        ss = "WikicatEducationalInstitutionsIn";
                                    if (ss.contains("WikicatEducatorsFrom")) ss = "WikicatEducatorsFrom";
                                    if (ss.contains("WikicatElementarySchoolsIn")) ss = "WikicatElementarySchoolsIn";
                                    if (ss.contains("WikicatEngineeringUniversitiesAndCollegesIn"))
                                        ss = "WikicatEngineeringUniversitiesAndCollegesIn";
                                    if (ss.contains("WikicatEnglishPeopleOf")) ss = "WikicatEnglishPeopleOf";
                                    if (ss.contains("WikicatEnvironmentalOrganisationsBasedIn"))
                                        ss = "WikicatEnvironmentalOrganisationsBasedIn";
                                    if (ss.contains("WikicatExpatriateBasketballPeopleIn"))
                                        ss = "WikicatExpatriateBasketballPeopleIn";
                                    if (ss.contains("WikicatExpatriateFootballManagersIn"))
                                        ss = "WikicatExpatriateFootballManagersIn";
                                    if (ss.contains("WikicatExpresswaysIn")) ss = "WikicatExpresswaysIn";
                                    if (ss.contains("WikicatFast-foodChainsOf")) ss = "WikicatFast-foodChainsOf";
                                    if (ss.contains("WikicatFerryCompaniesOf")) ss = "WikicatFerryCompaniesOf";
                                    if (ss.contains("WikicatFestivalsIn")) ss = "WikicatFestivalsIn";
                                    if (ss.contains("WikicatFictionalCharactersWith"))
                                        ss = "WikicatFictionalCharactersWith";
                                    if (ss.contains("WikicatFictionalPopulatedPlacesIn"))
                                        ss = "WikicatFictionalPopulatedPlacesIn";
                                    if (ss.contains("WikicatFilmsAbout")) ss = "WikicatFilmsAbout";
                                    if (ss.contains("WikicatFilmsBasedOn")) ss = "WikicatFilmsBasedOn";
                                    if (ss.contains("WikicatFilmsDirectedBy")) ss = "WikicatFilmsDirectedBy";
                                    if (ss.contains("WikicatFinnishExpatriatesIn")) ss = "WikicatFinnishExpatriatesIn";
                                    if (ss.contains("WikicatFootballClubsIn")) ss = "WikicatFootballClubsIn";
                                    if (ss.contains("WikicatFootballVenuesIn")) ss = "WikicatFootballVenuesIn";
                                    if (ss.contains("WikicatForeignMinistersOf")) ss = "WikicatForeignMinistersOf";
                                    if (ss.contains("WikicatFormerCensus-designatedPlacesIn"))
                                        ss = "WikicatFormerCensus-designatedPlacesIn";
                                    if (ss.contains("WikicatFormerMunicipalitiesIn"))
                                        ss = "WikicatFormerMunicipalitiesIn";
                                    if (ss.contains("WikicatFormerPopulatedPlacesIn"))
                                        ss = "WikicatFormerPopulatedPlacesIn";
                                    if (ss.contains("WikicatGermanPeopleOf")) ss = "WikicatGermanPeopleOf";
                                    if (ss.contains("WikicatGhostTownsIn")) ss = "WikicatGhostTownsIn";
                                    if (ss.contains("WikicatGirls'SchoolsIn")) ss = "WikicatGirls'SchoolsIn";
                                    if (ss.contains("WikicatGolfClubsAndCoursesIn"))
                                        ss = "WikicatGolfClubsAndCoursesIn";
                                    if (ss.contains("WikicatGovernmentBuildingsIn"))
                                        ss = "WikicatGovernmentBuildingsIn";
                                    if (ss.contains("WikicatGovernmentMinistersOf"))
                                        ss = "WikicatGovernmentMinistersOf";
                                    if (ss.contains("WikicatGovernment-ownedCompaniesOf"))
                                        ss = "WikicatGovernment-ownedCompaniesOf";
                                    if (ss.contains("WikicatGovernorsOf")) ss = "WikicatGovernorsOf";
                                    if (ss.contains("WikicatGrammarSchoolsIn")) ss = "WikicatGrammarSchoolsIn";
                                    if (ss.contains("WikicatGrandMastersOf")) ss = "WikicatGrandMastersOf";
                                    if (ss.contains("WikicatGrandOfficersOf")) ss = "WikicatGrandOfficersOf";
                                    if (ss.contains("WikicatHighCommissionersOf")) ss = "WikicatHighCommissionersOf";
                                    if (ss.contains("WikicatHighSchoolsIn")) ss = "WikicatHighSchoolsIn";
                                    if (ss.contains("WikicatHospitalsIn")) ss = "WikicatHospitalsIn";
                                    if (ss.contains("WikicatHotelsI")) ss = "WikicatHotelsI";
                                    if (ss.contains("WikicatHousesIn")) ss = "WikicatHousesIn";
                                    if (ss.contains("WikicatHungarianExpatriatesIn"))
                                        ss = "WikicatHungarianExpatriatesIn";
                                    if (ss.contains("WikicatIceHockeyPeopleFrom")) ss = "WikicatIceHockeyPeopleFrom";
                                    if (ss.contains("WikicatIndependentSchoolsIn")) ss = "WikicatIndependentSchoolsIn";
                                    if (ss.contains("WikicatIndustrialBuildingsIn"))
                                        ss = "WikicatIndustrialBuildingsIn";
                                    if (ss.contains("WikicatInsectsOf")) ss = "WikicatInsectsOf";
                                    if (ss.contains("WikicatInsuranceCompaniesOf")) ss = "WikicatInsuranceCompaniesOf";
                                    if (ss.contains("WikicatInternationalBaccalaureateSchoolsIn"))
                                        ss = "WikicatInternationalBaccalaureateSchoolsIn";
                                    if (ss.contains("WikicatInternationalSchoolsIn"))
                                        ss = "WikicatInternationalSchoolsIn";
                                    if (ss.contains("WikicatInternationalSportsCompetitionsHostedBy"))
                                        ss = "WikicatInternationalSportsCompetitionsHostedBy";
                                    if (ss.contains("WikicatJudgesOfTheUnitedStatesDistrictCourtForTheDistrictOf"))
                                        ss = "WikicatJudgesOfTheUnitedStatesDistrictCourtForTheDistrictOf";
                                    if (ss.contains("WikicatKnightsOfTheOrderOf")) ss = "WikicatKnightsOfTheOrderOf";
                                    if (ss.contains("WikicatLakesOf")) ss = "WikicatLakesOf";
                                    if (ss.contains("WikicatLandmarksIn")) ss = "WikicatLandmarksIn";
                                    if (ss.contains("WikicatMilitaryFacilitiesOnTheNationalRegisterOfHistoricPlacesIn"))
                                        ss = "WikicatMilitaryFacilitiesOnTheNationalRegisterOfHistoricPlacesIn";
                                    if (ss.contains("WikicatMilitaryUnitsAndFormationsOf"))
                                        ss = "WikicatMilitaryUnitsAndFormationsOf";
                                    if (ss.contains("WikicatMinesIn")) ss = "WikicatMinesIn";
                                    if (ss.contains("WikicatMixedMartialArtistsFrom"))
                                        ss = "WikicatMixedMartialArtistsFrom";
                                    if (ss.contains("WikicatMobilePhoneCompaniesOf"))
                                        ss = "WikicatMobilePhoneCompaniesOf";
                                    if (ss.contains("WikicatMonumentsAndMemorialsIn"))
                                        ss = "WikicatMonumentsAndMemorialsIn";
                                    if (ss.contains("WikicatMountainsOf")) ss = "WikicatMountainsOf";
                                    if (ss.contains("WikicatMunicipalitiesOf")) ss = "WikicatMunicipalitiesOf";
                                    if (ss.contains("WikicatMuseumsIn")) ss = "WikicatMuseumsIn";
                                    if (ss.contains("WikicatMusicalGroupsFrom")) ss = "WikicatMusicalGroupsFrom";
                                    if (ss.contains("WikicatMusicFestivalsIn")) ss = "WikicatMusicFestivalsIn";
                                    if (ss.contains("WikicatMusiciansFrom")) ss = "WikicatMusiciansFrom";
                                    if (ss.contains("WikicatMusicVideosDirectedBy"))
                                        ss = "WikicatMusicVideosDirectedBy";
                                    if (ss.contains("WikicatNationalHistoricLandmarksIn"))
                                        ss = "WikicatNationalHistoricLandmarksIn";
                                    if (ss.contains("WikicatNaturalDisastersIn")) ss = "WikicatNaturalDisastersIn";
                                    if (ss.contains("WikicatNaturalizedCitizensOf"))
                                        ss = "WikicatNaturalizedCitizensOf";
                                    if (ss.contains("WikicatNeighborhoodsIn")) ss = "WikicatNeighborhoodsIn";
                                    if (ss.contains("WikicatNeighbourhoods")) ss = "Neighbourhoods";
                                    if (ss.contains("WikicatNewspapersPublishedIn"))
                                        ss = "WikicatNewspapersPublishedIn";
                                    if (ss.contains("WikicatNigerianExpatriatesIn"))
                                        ss = "WikicatNigerianExpatriatesIn";
                                    if (ss.contains("WikicatNondenominationalChristianSchoolsIn"))
                                        ss = "WikicatNondenominationalChristianSchoolsIn";
                                    if (ss.contains("WikicatNon-fictionBooksAbout"))
                                        ss = "WikicatNon-fictionBooksAbout";
                                    if (ss.contains("WikicatNon-governmentalOrganizationsBasedIn"))
                                        ss = "WikicatNon-governmentalOrganizationsBasedIn";
                                    if (ss.contains("WikicatNon-profitOrganisationsBasedIn"))
                                        ss = "WikicatNon-profitOrganisationsBasedIn";
                                    if (ss.contains("WikicatNovelsBy")) ss = "WikicatNovelsBy";
                                    if (ss.contains("WikicatNursingSchoolsIn")) ss = "WikicatNursingSchoolsIn";
                                    if (ss.contains("WikicatOfficeBuildingsIn")) ss = "WikicatOfficeBuildingsIn";
                                    if (ss.contains("WikicatOfficersOfTheOrderOf")) ss = "WikicatOfficersOfTheOrderOf";
                                    if (ss.contains("WikicatOlympicAthletesOf")) ss = "WikicatOlympicAthletesOf";
                                    if (ss.contains("WikicatOlympicBronzeMedalistsFor"))
                                        ss = "WikicatOlympicBronzeMedalistsFor";
                                    if (ss.contains("WikicatOlympicCanoeistsOf")) ss = "WikicatOlympicCanoeistsOf";
                                    if (ss.contains("WikicatOlympicEquestriansOf")) ss = "WikicatOlympicEquestriansOf";
                                    if (ss.contains("WikicatOlympicFencersOf")) ss = "WikicatOlympicFencersOf";
                                    if (ss.contains("WikicatOlympicFieldHockeyPlayersOf"))
                                        ss = "WikicatOlympicFieldHockeyPlayersOf";
                                    if (ss.contains("WikicatOlympicFigureSkatersOf"))
                                        ss = "WikicatOlympicFigureSkatersOf";
                                    if (ss.contains("WikicatOlympicFootballersOf")) ss = "WikicatOlympicFootballersOf";
                                    if (ss.contains("WikicatOlympicGoldMedalistsFor"))
                                        ss = "WikicatOlympicGoldMedalistsFor";
                                    if (ss.contains("WikicatOlympicGymnastsOf")) ss = "WikicatOlympicGymnastsOf";
                                    if (ss.contains("WikicatOlympicHandballPlayersOf"))
                                        ss = "WikicatOlympicHandballPlayersOf";
                                    if (ss.contains("WikicatOlympicIceHockeyPlayersOf"))
                                        ss = "WikicatOlympicIceHockeyPlayersOf";
                                    if (ss.contains("WikicatOlympicSailorsOf")) ss = "WikicatOlympicSailorsOf";
                                    if (ss.contains("WikicatOlympicShootersOf")) ss = "WikicatOlympicShootersOf";
                                    if (ss.contains("WikicatOlympicSilverMedalistsFor"))
                                        ss = "WikicatOlympicSilverMedalistsFor";
                                    if (ss.contains("WikicatOlympicSpeedSkatersOf"))
                                        ss = "WikicatOlympicSpeedSkatersOf";
                                    if (ss.contains("WikicatOlympicSwimmersOf")) ss = "WikicatOlympicSwimmersOf";
                                    if (ss.contains("WikicatOlympicTennisPlayersOf"))
                                        ss = "WikicatOlympicTennisPlayersOf";
                                    if (ss.contains("WikicatOlympicVolleyballPlayersOf"))
                                        ss = "WikicatOlympicVolleyballPlayersOf";
                                    if (ss.contains("WikicatOlympicWeightliftersOf"))
                                        ss = "WikicatOlympicWeightliftersOf";
                                    if (ss.contains("WikicatOrchidsOf")) ss = "WikicatOrchidsOf";
                                    if (ss.contains("WikicatOrganisationsBasedIn")) ss = "WikicatOrganisationsBasedIn";
                                    if (ss.contains("WikicatOrganizationsBasedIn")) ss = "WikicatOrganizationsBasedIn";
                                    if (ss.contains("WikicatPaintersFrom")) ss = "PaintersFrom";
                                    if (ss.contains("WikicatPaintings")) ss = "Paintings";
                                    if (ss.contains("WikicatPalacesIn")) ss = "WikicatPalacesIn";
                                    if (ss.contains("WikicatPanAmericanGamesCompetitorsFor"))
                                        ss = "WikicatPanAmericanGamesCompetitorsFor";
                                    if (ss.contains("WikicatParalympicGoldMedalistsFor"))
                                        ss = "WikicatParalympicGoldMedalistsFor";
                                    if (ss.contains("WikicatParalympicSilverMedalistsFor"))
                                        ss = "WikicatParalympicSilverMedalistsFor";
                                    if (ss.contains("WikicatParksIn")) ss = "WikicatParksIn";
                                    if (ss.contains("WikicatPassengerShipsOf")) ss = "WikicatPassengerShipsOf";
                                    if (ss.contains("WikicatPeopleAssociatedWithTheUniversity"))
                                        ss = "WikicatPeopleAssociatedWithTheUniversity";
                                    if (ss.contains("WikicatPeopleConvictedOf")) ss = "WikicatPeopleConvictedOf";
                                    if (ss.contains("WikicatPeopleDeportedFrom")) ss = "WikicatPeopleDeportedFrom";
                                    if (ss.contains("WikicatPeopleEducatedAt")) ss = "WikicatPeopleEducatedAt";
                                    if (ss.contains("WikicatPeopleExecutedBy")) ss = "WikicatPeopleExecutedBy";
                                    if (ss.contains("WikicatPeopleExtraditedTo")) ss = "WikicatPeopleExtraditedTo";
                                    if (ss.contains("WikicatPeopleFrom")) ss = "PeopleFrom";
                                    if (ss.contains("WikicatPeopleKilledBy")) ss = "WikicatPeopleKilledBy";
                                    if (ss.contains("WikicatPeopleMurderedIn")) ss = "WikicatPeopleMurderedIn";
                                    if (ss.contains("WikicatPeopleOf")) ss = "WikicatPeopleOf";
                                    if (ss.contains("WikicatPeopleWhoDiedIn")) ss = "WikicatPeopleWhoDiedIn";
                                    if (ss.contains("WikicatPermanentRepresentativesOf"))
                                        ss = "WikicatPermanentRepresentativesOf";
                                    if (ss.contains("WikicatPermanentSecretariesOf"))
                                        ss = "WikicatPermanentSecretariesOf";
                                    if (ss.contains("WikicatPhysiciansFrom")) ss = "WikicatPhysiciansFrom";
                                    if (ss.contains("WikicatPlacesOfWorshipIn")) ss = "WikicatPlacesOfWorshipIn";
                                    if (ss.contains("WikicatPlayersOf")) ss = "WikicatPlayersOf";
                                    if (ss.contains("WikicatPolishPeopleOf")) ss = "WikicatPolishPeopleOf";
                                    if (ss.contains("WikicatPoliticalPartiesIn")) ss = "WikicatPoliticalPartiesIn";
                                    if (ss.contains("WikicatPoliticiansOfTheRepublicOfChinaOnTaiwanFrom"))
                                        ss = "WikicatPoliticiansOfTheRepublicOfChinaOnTaiwanFrom";
                                    if (ss.contains("WikicatPopulatedPlacesIn'")) ss = "WikicatPopulatedPlacesIn'";
                                    if (ss.contains("WikicatPopulatedPlacesOn")) ss = "WikicatPopulatedPlacesOn";
                                    if (ss.contains("WikicatPortsAndHarboursOf")) ss = "WikicatPortsAndHarboursOf";
                                    if (ss.contains("WikicatPrefectsOf")) ss = "WikicatPrefectsOf";
                                    if (ss.contains("WikicatPreparatorySchoolsIn"))
                                        ss = "WikicatPreparatorySchoolsIn"; //
                                    if (ss.contains("WikicatPresidentsOf")) ss = "WikicatPresidentsOf";
                                    if (ss.contains("WikicatPresidentsOfTheUniversityOf"))
                                        ss = "WikicatPresidentsOfTheUniversityOf";
                                    if (ss.contains("WikicatPrimarySchoolsIn")) ss = "WikicatPrimarySchoolsIn";
                                    if (ss.contains("WikicatPrimeMinistersOf")) ss = "WikicatPrimeMinistersOf";
                                    if (ss.contains("WikicatPrincessesOf")) ss = "WikicatPrincessesOf";
                                    if (ss.contains("WikicatPrisonersAndDetaineesOf"))
                                        ss = "WikicatPrisonersAndDetaineesOf";
                                    if (ss.contains("WikicatPrisonersSentencedToDeathBy"))
                                        ss = "WikicatPrisonersSentencedToDeathBy";
                                    if (ss.contains("WikicatPrisonersSentencedToLifeImprisonmentBy"))
                                        ss = "WikicatPrisonersSentencedToLifeImprisonmentBy";
                                    if (ss.contains("WikicatPrisonersWhoDiedIn")) ss = "WikicatPrisonersWhoDiedIn";
                                    if (ss.contains("WikicatPrivateElementarySchoolsIn"))
                                        ss = "WikicatPrivateElementarySchoolsIn";
                                    if (ss.contains("WikicatPrivateHighSchoolsIn")) ss = "WikicatPrivateHighSchoolsIn";
                                    if (ss.contains("WikicatPrivatelyHeldCompaniesBasedIn"))
                                        ss = "WikicatPrivatelyHeldCompaniesBasedIn";
                                    if (ss.contains("WikicatPrivatelyHeldCompaniesOf"))
                                        ss = "WikicatPrivatelyHeldCompaniesOf";
                                    if (ss.contains("WikicatPrivateMiddleSchoolsIn"))
                                        ss = "WikicatPrivateMiddleSchoolsIn";
                                    if (ss.contains("WikicatPrivateSchoolsIn")) ss = "WikicatPrivateSchoolsIn";
                                    if (ss.contains("WikicatProfessionalWrestlersFrom"))
                                        ss = "WikicatProfessionalWrestlersFrom";
                                    if (ss.contains("WikicatProposedBuildingsAndStructuresIn"))
                                        ss = "WikicatProposedBuildingsAndStructuresIn";
                                    if (ss.contains("WikicatProtectedAreasOf")) ss = "WikicatProtectedAreasOf";
                                    if (ss.contains("WikicatPublicHighSchoolsIn")) ss = "WikicatPublicHighSchoolsIn";
                                    if (ss.contains("WikicatPublicMiddleSchoolsIn"))
                                        ss = "WikicatPublicMiddleSchoolsIn";
                                    if (ss.contains("WikicatPublicSchoolsIn")) ss = "WikicatPublicSchoolsIn";
                                    if (ss.contains("WikicatPupilsOf")) ss = "WikicatPupilsOf";
                                    if (ss.contains("WikicatRacingDriversFrom")) ss = "WikicatRacingDriversFrom"; //
                                    if (ss.contains("WikicatRadioStationsIn")) ss = "WikicatRadioStationsIn";
                                    if (ss.contains("WikicatRailwayAccidentsIn")) ss = "WikicatRailwayAccidentsIn";
                                    if (ss.contains("WikicatRailwayCompaniesOf")) ss = "WikicatRailwayCompaniesOf";
                                    if (ss.contains("WikicatRailwayLinesIn")) ss = "WikicatRailwayLinesIn";
                                    if (ss.contains("WikicatRailwayStationsIn")) ss = "WikicatRailwayStationsIn";
                                    if (ss.contains("WikicatRectorsOf")) ss = "WikicatRectorsOf";
                                    if (ss.contains("WikicatResearchInstitutesIn")) ss = "WikicatResearchInstitutesIn";
                                    if (ss.contains("WikicatRestaurantsIn")) ss = "WikicatRestaurantsIn";
                                    if (ss.contains("WikicatRiversOf")) ss = "WikicatRiversOf";
                                    if (ss.contains("WikicatRoadsIn")) ss = "WikicatRoadsIn";
                                    if (ss.contains("WikicatRollerCoastersManufacturedBy"))
                                        ss = "WikicatRollerCoastersManufacturedBy";
                                    if (ss.contains("WikicatRomanCatholicBishopsOf"))
                                        ss = "WikicatRomanCatholicBishopsOf";
                                    if (ss.contains("WikicatRomanCatholicSchoolsIn"))
                                        ss = "WikicatRomanCatholicSchoolsIn";
                                    if (ss.contains("WikicatRomanCatholicSecondarySchoolsIn"))
                                        ss = "WikicatRomanCatholicSecondarySchoolsIn";
                                    if (ss.contains("WikicatRomanCatholicUniversitiesAndCollegesIn"))
                                        ss = "WikicatRomanCatholicUniversitiesAndCollegesIn";
                                    if (ss.contains("WikicatSchoolsIn")) ss = "SchoolsIn";
                                    if (ss.contains("WikicatSchoolsOf")) ss = "SchoolsOf";
                                    if (ss.contains("WikicatScottishExpatriatesIn"))
                                        ss = "WikicatScottishExpatriatesIn";
                                    if (ss.contains("WikicatSecondarySchoolsIn")) ss = "WikicatSecondarySchoolsIn";
                                    if (ss.contains("WikicatSecretariesOfStateOf")) ss = "WikicatSecretariesOfStateOf";
                                    if (ss.contains("WikicatSerbianPeopleOf")) ss = "WikicatSerbianPeopleOf";
                                    if (ss.contains("WikicatShippingCompaniesOf")) ss = "WikicatShippingCompaniesOf";
                                    if (ss.contains("WikicatShipsOf")) ss = "WikicatShipsOf";
                                    if (ss.contains("WikicatShipwrecksIn")) ss = "WikicatShipwrecksIn";
                                    if (ss.contains("WikicatShoppingMallsIn")) ss = "WikicatShoppingMallsIn";
                                    if (ss.contains("WikicatShortStoriesBy")) ss = "WikicatShortStoriesBy";
                                    if (ss.contains("WikicatSkyscrapersIn")) ss = "WikicatSkyscrapersIn";
                                    if (ss.contains("WikicatSoftwareCompanies")) ss = "SoftwareCompanies";
                                    if (ss.contains("WikicatSongRecordingsProducedBy"))
                                        ss = "WikicatSongRecordingsProducedBy";
                                    if (ss.contains("WikicatSongsAbout")) ss = "WikicatSongsAbout";
                                    if (ss.contains("WikicatSongsWithLyricsBy")) ss = "WikicatSongsWithLyricsBy";
                                    if (ss.contains("WikicatSongsWithMusicBy")) ss = "WikicatSongsWithMusicBy";
                                    if (ss.contains("WikicatSpeakersOfTheNationalAssemblyOf"))
                                        ss = "WikicatSpeakersOfTheNationalAssemblyOf";
                                    if (ss.contains("WikicatSportsVenuesIn")) ss = "WikicatSportsVenuesIn";
                                    if (ss.contains("WikicatSquadronsOfTheRepublicOf"))
                                        ss = "WikicatSquadronsOfTheRepublicOf";
                                    if (ss.contains("WikicatStateParksOf")) ss = "WikicatStateParksOf";
                                    if (ss.contains("WikicatStateTreasurersOf")) ss = "WikicatStateTreasurersOf";
                                    if (ss.contains("WikicatSteamLocomotivesOf")) ss = "WikicatSteamLocomotivesOf";
                                    if (ss.contains("WikicatSteamshipsOf")) ss = "WikicatSteamshipsOf";
                                    if (ss.contains("WikicatSteelCompaniesOf")) ss = "WikicatSteelCompaniesOf";
                                    if (ss.contains("WikicatStudentNewspapersPublishedIn"))
                                        ss = "WikicatStudentNewspapersPublishedIn";
                                    if (ss.contains("WikicatSubmarineCommunicationsCablesIn"))
                                        ss = "WikicatSubmarineCommunicationsCablesIn";
                                    if (ss.contains("WikicatSubmarinesOf")) ss = "WikicatSubmarinesOf";
                                    if (ss.contains("WikicatSwedishPeopleOf")) ss = "WikicatSwedishPeopleOf";
                                    if (ss.contains("WikicatTelecommunicationsCompaniesOf"))
                                        ss = "WikicatTelecommunicationsCompaniesOf";
                                    if (ss.contains("WikicatTelevisionStationsIn")) ss = "WikicatTelevisionStationsIn";
                                    if (ss.contains("WikicatTownshipsIn")) ss = "WikicatTownshipsIn";
                                    if (ss.contains("WikicatTownsIn")) ss = "TownsIn";
                                    if (ss.contains("WikicatTramVehiclesOf")) ss = "TramVehiclesOf";
                                    if (ss.contains("WikicatTranslatorsOf")) ss = "WikicatTranslatorsOf";
                                    if (ss.contains("WikicatTransportCompaniesOf")) ss = "WikicatTransportCompaniesOf";
                                    if (ss.contains("WikicatTreatiesExtendedTo")) ss = "WikicatTreatiesExtendedTo";
                                    if (ss.contains("WikicatTreesOf")) ss = "WikicatTreesOf";
                                    if (ss.contains("WikicatTributariesOf")) ss = "WikicatTributariesOf";
                                    if (ss.contains("WikicatTropicalCyclonesIn")) ss = "WikicatTropicalCyclonesIn";
                                    if (ss.contains("WikicatUnincorporatedCommunitiesIn"))
                                        ss = "UnincorporatedCommunitiesIn";
                                    if (ss.contains("WikicatUnitedStatesAttorneysFor")) ss = "UnitedStatesAttorneysFor";
                                    if (ss.contains("WikicatUniversalMusic")) ss = "UniversalMusic";
                                    if (ss.contains("WikicatUniversities")) ss = "Universities";
                                    if (ss.contains("WikicatUniversitiesAndCollegesAffiliated"))
                                        ss = "UniversitiesAndCollegesAffiliated";
                                    if (ss.contains("WikicatValleysOf")) ss = "ValleysOf";
                                    if (ss.contains("WikicatVelodromesIn")) ss = "WikicatVelodromesIn";
                                    if (ss.contains("WikicatVenezuelanPeopleOf")) ss = "WikicatVenezuelanPeopleOf";
                                    if (ss.contains("WikicatVictimsOf")) ss = "WikicatVictimsOf";
                                    if (ss.contains("WikicatVictimsOfAviationAccidentsOrIncidentsIn"))
                                        ss = "VictimsOfAviationAccidentsOrIncidentsIn";
                                    if (ss.contains("WikicatVideoGameCompaniesOf")) ss = "WikicatVideoGameCompaniesOf";
                                    if (ss.contains("WikicatVillagesIn")) ss = "VillagesIn";
                                    if (ss.contains("WikicatViscountsOf")) ss = "WikicatViscountsOf";
                                    if (ss.contains("WikicatVisitorAttractionsIn")) ss = "WikicatVisitorAttractionsIn";
                                    if (ss.contains("WikicatVolcanoesOf")) ss = "VolcanoesOf";
                                    if (ss.contains("WikicatVolleyballPlayers")) ss = "VolleyballPlayers";
                                    if (ss.contains("WikicatWeatherEventsIn")) ss = "WikicatWeatherEventsIn";
                                    if (ss.contains("WikicatWomenOf")) ss = "WomenOf"; //
                                    if (ss.contains("WikicatWomenStateLegislatorsIn")) ss = "WomenStateLegislatorsIn";
                                    if (ss.contains("WikicatWorksAbout")) ss = "WorksAbout";
                                    if (ss.contains("WikicatWorksBy")) ss = "WorksBy";
                                    if (ss.contains("WikicatWorldWar")) ss = "WorldWar";
                                    if (ss.contains("WikicatWriters")) ss = "Writers";
                                    if (ss.contains("WikicatYearsOfThestCenturyIn")) ss = "YearsOfThestCenturyIn";
                                    if (ss.contains("WikicatYouthOrganisationsBasedIn"))
                                        ss = "YouthOrganisationsBasedIn";
                                    if (ss.contains("WikicatZoosIn")) ss = "ZoosIn";
                                    if (ss.contains("WikicatAircraftManufacturersOf"))
                                        ss = "WikicatAircraftManufacturersOf";
                                    if (ss.contains("WikicatAmericanExpatriateBasketballPeopleIn"))
                                        ss = "WikicatAmericanExpatriateBasketballPeopleIn";
                                    if (ss.contains("WikicatAmericanExpatriatesIn"))
                                        ss = "WikicatAmericanExpatriatesIn";
                                    if (ss.contains("WikicatAmericanMormonMissionariesIn"))
                                        ss = "WikicatAmericanMormonMissionariesIn";
                                    if (ss.contains("WikicatAmericanPeopleConvictedOf"))
                                        ss = "WikicatAmericanPeopleConvictedOf";
                                    if (ss.contains("WikicatAmericanPeopleExecutedBy"))
                                        ss = "WikicatAmericanPeopleExecutedBy";
                                    if (ss.contains("WikicatAmericanPeopleOf")) ss = "WikicatAmericanPeopleOf";
                                    if (ss.contains("WikicatAnglicanArchbishopsOf"))
                                        ss = "WikicatAnglicanArchbishopsOf";
                                    if (ss.contains("WikicatAnglicanArchdeaconsIn"))
                                        ss = "WikicatAnglicanArchdeaconsIn";
                                    if (ss.contains("WikicatAnglicanBishopsOf")) ss = "WikicatAnglicanBishopsOf";
                                    if (ss.contains("WikicatAnglicanSchoolsIn")) ss = "WikicatAnglicanSchoolsIn";
                                    if (ss.contains("WikicatAnglicanSuffraganBishopsInTheDioceseOf"))
                                        ss = "WikicatAnglicanSuffraganBishopsInTheDioceseOf";
                                    if (ss.contains("WikicatAsianGamesMedalistsIn"))
                                        ss = "WikicatAsianGamesMedalistsIn";
                                    if (ss.contains("WikicatAustralianPeopleOf")) ss = "WikicatAustralianPeopleOf";
                                    if (ss.contains("WikicatBelgianExpatriatesIn")) ss = "WikicatBelgianExpatriatesIn";
                                    if (ss.contains("WikicatBelgianPeopleOf")) ss = "WikicatBelgianPeopleOf";
                                    if (ss.contains("WikicatBenineseExpatriatesIn"))
                                        ss = "WikicatBenineseExpatriatesIn";
                                    if (ss.contains("WikicatBeverageCompaniesOf")) ss = "WikicatBeverageCompaniesOf";
                                    if (ss.contains("WikicatBiographicalMuseumsIn"))
                                        ss = "WikicatBiographicalMuseumsIn";
                                    if (ss.contains("WikicatBrazilianExpatriatesIn"))
                                        ss = "WikicatBrazilianExpatriatesIn";
                                    if (ss.contains("WikicatBrazilianPeopleOf")) ss = "WikicatBrazilianPeopleOf";
                                    if (ss.contains("WikicatCanadianExpatriateLacrossePeopleIn"))
                                        ss = "WikicatCanadianExpatriateLacrossePeopleIn";
                                    if (ss.contains("WikicatFinanceMinistersOf")) ss = "WikicatFinanceMinistersOf";
                                    if (ss.contains("WikicatFinancialServicesCompaniesOf"))
                                        ss = "WikicatFinancialServicesCompaniesOf";
                                    if (ss.contains("WikicatFinnishPeopleOf")) ss = "WikicatFinnishPeopleOf";
                                    if (ss.contains("WikicatFirstNationsGovernmentsIn"))
                                        ss = "WikicatFirstNationsGovernmentsIn";
                                    if (ss.contains("WikicatFlagsOf")) ss = "WikicatFlagsOf";
                                    if (ss.contains("WikicatFootballPlayersFrom")) ss = "WikicatFootballPlayersFrom";
                                    if (ss.contains("WikicatFormerChurchesIn")) ss = "WikicatFormerChurchesIn";
                                    if (ss.contains("WikicatFormerHighSchoolsIn")) ss = "WikicatFormerHighSchoolsIn";
                                    if (ss.contains("WikicatFormerStateHighwaysIn"))
                                        ss = "WikicatFormerStateHighwaysIn";
                                    if (ss.contains("WikicatFoundationSchoolsIn")) ss = "WikicatFoundationSchoolsIn";
                                    if (ss.contains("WikicatFrenchPeopleOf")) ss = "WikicatFrenchPeopleOf";
                                    if (ss.contains("WikicatFugitivesWantedBy")) ss = "WikicatFugitivesWantedBy";
                                    if (ss.contains("WikicatGaelicFootballClubsIn"))
                                        ss = "WikicatGaelicFootballClubsIn";
                                    if (ss.contains("WikicatGaeltachtPlacesIn")) ss = "WikicatGaeltachtPlacesIn";
                                    if (ss.contains("WikicatGovernmentMinistriesOf"))
                                        ss = "WikicatGovernmentMinistriesOf";
                                    if (ss.contains("WikicatGradeII*ListedBuildingsIn"))
                                        ss = "WikicatGradeII*ListedBuildingsIn";
                                    if (ss.contains("WikicatGrandCommandersOfTheOrderOf"))
                                        ss = "WikicatGrandCommandersOfTheOrderOf";
                                    if (ss.contains("WikicatGreekPeopleOf")) ss = "WikicatGreekPeopleOf";
                                    if (ss.contains("WikicatGulfsOf")) ss = "WikicatGulfsOf";
                                    if (ss.contains("WikicatHamletsIn")) ss = "WikicatHamletsIn";
                                    if (ss.contains("WikicatHeadlandsOf")) ss = "WikicatHeadlandsOf";
                                    if (ss.contains("WikicatHeavyMetalFestivalsIn"))
                                        ss = "WikicatHeavyMetalFestivalsIn";
                                    if (ss.contains("WikicatHighSheriffsOf")) ss = "WikicatHighSheriffsOf";
                                    if (ss.contains("WikicatHistoriansOf")) ss = "WikicatHistoriansOf";
                                    if (ss.contains("WikicatHistoricHouseMuseumsIn"))
                                        ss = "WikicatHistoricHouseMuseumsIn";
                                    if (ss.contains("WikicatHistoryMuseumsIn")) ss = "WikicatHistoryMuseumsIn";
                                    if (ss.contains("WikicatHongKongPeopleOf")) ss = "WikicatHongKongPeopleOf";
                                    if (ss.contains("WikicatHorseBreedsOriginatingIn"))
                                        ss = "WikicatHorseBreedsOriginatingIn";
                                    if (ss.contains("WikicatHospitalShipsOf")) ss = "WikicatHospitalShipsOf";
                                    if (ss.contains("WikicatHousesOnTheNationalRegisterOfHistoricPlacesIn"))
                                        ss = "WikicatHousesOnTheNationalRegisterOfHistoricPlacesIn";
                                    if (ss.contains("WikicatHungarianPeopleOf")) ss = "WikicatHungarianPeopleOf";
                                    if (ss.contains("WikicatHurricanesIn")) ss = "WikicatHurricanesIn";
                                    if (ss.contains("WikicatIceHockeyTeamsIn")) ss = "WikicatIceHockeyTeamsIn";
                                    if (ss.contains("WikicatImportantBirdAreas")) ss = "WikicatImportantBirdAreas";
                                    if (ss.contains("WikicatIncorporatedPlacesIn")) ss = "WikicatIncorporatedPlacesIn";
                                    if (ss.contains("WikicatIndiePopGroupsFrom")) ss = "WikicatIndiePopGroupsFrom";
                                    if (ss.contains("WikicatIndoorIceHockeyVenuesIn"))
                                        ss = "WikicatIndoorIceHockeyVenuesIn";
                                    if (ss.contains("WikicatInfantryDivisionsOf")) ss = "WikicatInfantryDivisionsOf";
                                    if (ss.contains("WikicatInternationalIceHockeyCompetitionsHostedBy"))
                                        ss = "WikicatInternationalIceHockeyCompetitionsHostedBy";
                                    if (ss.contains("WikicatInterstateHighwaysIn")) ss = "WikicatInterstateHighwaysIn";
                                    if (ss.contains("WikicatIrishPeopleOf")) ss = "WikicatIrishPeopleOf";
                                    if (ss.contains("WikicatIslandsOf")) ss = "WikicatIslandsOf";
                                    if (ss.contains("WikicatIsraeliEmigrantsTo")) ss = "WikicatIsraeliEmigrantsTo";
                                    if (ss.contains("WikicatIsraeliPeopleOf")) ss = "WikicatIsraeliPeopleOf";
                                    if (ss.contains("WikicatItalianPeopleOf")) ss = "WikicatItalianPeopleOf";
                                    if (ss.contains("WikicatJudgesOfTheUnitedStatesDistrictCourtFor"))
                                        ss = "WikicatJudgesOfTheUnitedStatesDistrictCourtFor";
                                    if (ss.contains("WikicatLawSchoolsIn")) ss = "WikicatLawSchoolsIn";
                                    if (ss.contains("WikicatLeadersOf")) ss = "WikicatLeadersOf";
                                    if (ss.contains("WikicatLGBTPeopleFrom")) ss = "WikicatLGBTPeopleFrom";
                                    if (ss.contains("WikicatLieutenantGovernorsOf"))
                                        ss = "WikicatLieutenantGovernorsOf";
                                    if (ss.contains("WikicatLighthouseMuseumsIn")) ss = "WikicatLighthouseMuseumsIn";
                                    if (ss.contains("WikicatLocalitiesIn")) ss = "WikicatLocalitiesIn";
                                    if (ss.contains("WikicatMagnetSchoolsIn")) ss = "WikicatMagnetSchoolsIn";
                                    if (ss.contains("WikicatManufacturingCompaniesOf"))
                                        ss = "WikicatManufacturingCompaniesOf";
                                    if (ss.contains("WikicatMargravesOf")) ss = "WikicatMargravesOf";
                                    if (ss.contains("WikicatMaritimeIncidentsIn")) ss = "WikicatMaritimeIncidentsIn";
                                    if (ss.contains("WikicatMarquessesOf")) ss = "WikicatMarquessesOf";
                                    if (ss.contains("WikicatMassacresIn")) ss = "WikicatMassacresIn";
                                    if (ss.contains("WikicatMastersOf")) ss = "WikicatMastersOf";
                                    if (ss.contains("WikicatMayorsOf")) ss = "WikicatMayorsOf";
                                    if (ss.contains("WikicatMayorsOf")) ss = "WikicatMayorsOf";
                                    if (ss.contains("WikicatMayorsOfPlacesIn")) ss = "WikicatMayorsOfPlacesIn";
                                    if (ss.contains("WikicatMediaCompaniesOf")) ss = "WikicatMediaCompaniesOf";
                                    if (ss.contains("WikicatMediaIn")) ss = "WikicatMediaIn";
                                    if (ss.contains("WikicatMerchantShipsOf")) ss = "WikicatMerchantShipsOf";
                                    if (ss.contains("WikicatMexicanPeopleOf")) ss = "WikicatMexicanPeopleOf";
                                    if (ss.contains("WikicatMiddleSchoolsIn")) ss = "WikicatMiddleSchoolsIn";
                                    if (ss.contains("WikicatMilitaryUnitsAndFormationsIn"))
                                        ss = "WikicatMilitaryUnitsAndFormationsIn";
                                    if (ss.contains("WikicatMiningCompaniesOf")) ss = "WikicatMiningCompaniesOf";
                                    if (ss.contains("WikicatMinistersOfDefenseOf")) ss = "WikicatMinistersOfDefenseOf";
                                    if (ss.contains("WikicatMinistersOfFinanceOf")) ss = "WikicatMinistersOfFinanceOf";
                                    if (ss.contains("WikicatMountainsAndHillsOf")) ss = "WikicatMountainsAndHillsOf";
                                    if (ss.contains("WikicatMunicipalPresidentsIn"))
                                        ss = "WikicatMunicipalPresidentsIn";
                                    if (ss.contains("WikicatMuseumShipsIn")) ss = "WikicatMuseumShipsIn";
                                    if (ss.contains("WikicatMusicSchoolsIn")) ss = "WikicatMusicSchoolsIn";
                                    if (ss.contains("WikicatNationalBasketballAssociationPlayersFrom"))
                                        ss = "WikicatNationalBasketballAssociationPlayersFrom";
                                    if (ss.contains("WikicatNationalHistoricSitesIn"))
                                        ss = "WikicatNationalHistoricSitesIn";
                                    if (ss.contains("WikicatNationalNaturalLandmarksIn"))
                                        ss = "WikicatNationalNaturalLandmarksIn";
                                    if (ss.contains("WikicatNationalSportsTeamsOf"))
                                        ss = "WikicatNationalSportsTeamsOf";
                                    if (ss.contains("WikicatNaturalHistoryMuseumsIn"))
                                        ss = "WikicatNaturalHistoryMuseumsIn";
                                    if (ss.contains("WikicatNaturalisedCitizensOf"))
                                        ss = "WikicatNaturalisedCitizensOf";
                                    if (ss.contains("WikicatNavalBattlesInvolving"))
                                        ss = "WikicatNavalBattlesInvolving";
                                    if (ss.contains("WikicatNovelsAbout")) ss = "WikicatNovelsAbout";
                                    if (ss.contains("WikicatOlympicAlpineSkiersOf"))
                                        ss = "WikicatOlympicAlpineSkiersOf";
                                    if (ss.contains("WikicatOlympicBasketballPlayersOf"))
                                        ss = "WikicatOlympicBasketballPlayersOf";
                                    if (ss.contains("WikicatPeopleAssociatedWith")) ss = "WikicatPeopleAssociatedWith";
                                    if (ss.contains("WikicatPeopleExtraditedFrom")) ss = "WikicatPeopleExtraditedFrom";
                                    if (ss.contains("WikicatPeopleKilledIn")) ss = "WikicatPeopleKilledIn";
                                    if (ss.contains("WikicatPopulatedPlacesIn")) ss = "WikicatPopulatedPlacesIn";

                                    String ab = "[" + ss + " , " + r.getObject().toString() + "]";
                                    if (!col.contains(ab) && ss.toLowerCase().contains("school")) resp = false;
                                }
                            }
                            if (resp == false) {
                                nbGraph++;
                                col.add(phrase);
                                for (Statement r2 : mm) {
                                    String s = r2.getObject().toString();

                                    if ((s.contains("FACT") && s.length() == 4)) {
                                        String ss = r2.getSubject().toString();
                                        int j = 0;
                                        for (int i = 0; i < 10; i++) {
                                            String a = "" + i + "";
                                            while (ss.contains(a)) {
                                                j = ss.lastIndexOf(a);
                                                ss = ss.substring(0, j) + ss.substring(j + 1);
                                            }
                                        }
                                        if (ss.contains("ArtMuseumsAndGalleries")) ss = "ArtMuseumsAndGalleries";
                                        if (ss.contains("CitiesAndTownsIn")) ss = "CitiesAndTownsIn";
                                        if (ss.contains("WikicatAbbots")) ss = "Abbots";
                                        if (ss.contains("WikicatAcademicsOf")) ss = "AcademicsOf";
                                        if (ss.contains("WikicatAcademies")) ss = "Academies";
                                        if (ss.contains("WikicatAccidentsAndIncidents")) ss = "AccidentsAndIncidents";
                                        if (ss.contains("WikicatActors")) ss = "Actors";
                                        if (ss.contains("WikicatActresses")) ss = "Actors";
                                        if (ss.contains("WikicatAdaptationsOfWorksBy")) ss = "AdaptationsOfWorksBy";
                                        if (ss.contains("WikicatAdministrators")) ss = "Administrators";
                                        if (ss.contains("WikicatAirfieldsOfTheUnitedStatesArmyAirForces"))
                                            ss = "AirfieldsOfTheUnitedStatesArmyAirForces";
                                        if (ss.contains("WikicatAirlinerAccidentsAndIncidents"))
                                            ss = "AirlinerAccidentsAndIncidents";
                                        if (ss.contains("WikicatAirlinesOf")) ss = "AirlinesOf"; //
                                        if (ss.contains("WikicatAirports")) ss = "Airports";
                                        if (ss.contains("WikicatAlbumsArrangedBy")) ss = "AlbumsArrangedBy";
                                        if (ss.contains("WikicatAlbumsBy")) ss = "AlbumsBy";
                                        if (ss.contains("WikicatAlbumsConductedBy")) ss = "AlbumsConductedBy";
                                        if (ss.contains("WikicatAlbumsProducedBy")) ss = "AlbumsProducedBy";
                                        if (ss.contains("WikicatAlbumsRecorded")) ss = "AlbumsRecorded";
                                        if (ss.contains("WikicatAlbumsWithCoverArt")) ss = "AlbumsWithCoverArt";
                                        if (ss.contains("WikicatAluminiumCompanies")) ss = "AluminiumCompanies";
                                        if (ss.contains("WikicatAlumniOf")) ss = "universityAlumniOf";
                                        if (ss.contains("WikicatAmbassadors")) ss = "Ambassadors";
                                        if (ss.contains("WikicatApostolicNunciosTo")) ss = "ApostolicNunciosTo"; //
                                        if (ss.contains("WikicatArchaeologicalSites")) ss = "ArchaeologicalSites";
                                        if (ss.contains("WikicatArchbishops")) ss = "Archbishops";
                                        if (ss.contains("WikicatArchdeacons")) ss = "Archdeacons";
                                        if (ss.contains("WikicatArchitects")) ss = "Architects";
                                        if (ss.contains("WikicatArchitectureFirms")) ss = "ArchitectureFirms";
                                        if (ss.contains("WikicatArtists")) ss = "Artists";
                                        if (ss.contains("WikicatAssassinated")) ss = "Assassinated"; //
                                        if (ss.contains("WikicatAsteroidsNamedFrom")) ss = "AsteroidsNamedFrom";
                                        if (ss.contains("WikicatATeam")) ss = "ATeam";
                                        if (ss.contains("WikicatAthletesFrom")) ss = "AthletesFrom";
                                        if (ss.contains("WikicatAutomotiveCompaniesOf")) ss = "AutomotiveCompaniesOf";
                                        if (ss.contains("WikicatAuxiliaryShipsOf")) ss = "AuxiliaryShipsOf";
                                        if (ss.contains("WikicatAviationAccidentsAndIncidents"))
                                            ss = "AviationAccidentsAndIncidents";
                                        if (ss.contains("WikicatAviatorsFrom")) ss = "AviatorsFrom";
                                        if (ss.contains("WikicatAviatorsFrom")) ss = "WikicatAviatorsFrom";
                                        if (ss.contains("WikicatBanksBased")) ss = "BanksBased";
                                        if (ss.contains("WikicatBanksOf")) ss = "BanksOf";
                                        if (ss.contains("WikicatBaseballPlayersFrom")) ss = "BaseballPlayersFrom";
                                        if (ss.contains("WikicatBasketballPlayers")) ss = "BasketballPlayers";
                                        if (ss.contains("WikicatBasketballTeams")) ss = "BasketballTeams";
                                        if (ss.contains("WikicatBasketballVenues")) ss = "BasketballVenues";
                                        if (ss.contains("WikicatBattles")) ss = "Battles";
                                        if (ss.contains("WikicatBays")) ss = "Bays";
                                        if (ss.contains("WikicatBeachesOf")) ss = "Beaches";
                                        if (ss.contains("WikicatBeautyPageants")) ss = "BeautyPageants";
                                        if (ss.contains("WikicatBiographicalFilmsAbout")) ss = "BiographicalFilmsAbout";
                                        if (ss.contains("WikicatBirdsOf")) ss = "BirdsOf";
                                        if (ss.contains("WikicatBishops")) ss = "Bishops";
                                        if (ss.contains("WikicatBoardingSchoolsIn")) ss = "BoardingSchoolsIn";
                                        if (ss.contains("WikicatBodiesOfWater")) ss = "BodiesOfWater";
                                        if (ss.contains("WikicatBooksAbout")) ss = "BooksAbout";
                                        if (ss.contains("WikicatBooksBy")) ss = "BooksBy";
                                        if (ss.contains("WikicatBotanicalGardensIn")) ss = "WikicatBotanicalGardensIn";
                                        if (ss.contains("WikicatBoxersAt")) ss = "WikicatBoxersAt";
                                        if (ss.contains("WikicatBoxersFrom")) ss = "WikicatBoxersFrom";
                                        if (ss.contains("WikicatBoys'SchoolsIn")) ss = "Boys'SchoolsIn";
                                        if (ss.contains("WikicatBridgesIn")) ss = "BridgesIn";
                                        if (ss.contains("WikicatBuildingsAndStructuresIn"))
                                            ss = "BuildingsAndStructuresIn";
                                        if (ss.contains("WikicatBuildingsAndStructuresOnTheNationalRegisterOfHistoricPlacesIn"))
                                            ss = "BuildingsAndStructuresOnTheNationalRegisterOfHistoricPlacesIn";
                                        if (ss.contains("WikicatBuildingsAndStructuresUnderConstructionIn"))
                                            ss = "BuildingsAndStructuresUnderConstructionIn";
                                        if (ss.contains("WikicatBusinessSchools")) ss = "BusinessSchools";
                                        if (ss.contains("WikicatCemeteriesIn")) ss = "CemeteriesIn";
                                        if (ss.contains("WikicatCensus-designatedPlacesIn"))
                                            ss = "Census-designatedPlacesIn";
                                        if (ss.contains("WikicatCharactersCreatedBy")) ss = "CharactersCreatedBy";
                                        if (ss.contains("WikicatCharitiesBasedIn")) ss = "CharitiesBasedIn";
                                        if (ss.contains("WikicatCharterSchoolsIn")) ss = "CharterSchoolsIn"; //
                                        if (ss.contains("WikicatChiefJusticesOf")) ss = "ChiefJusticesOf";
                                        if (ss.contains("WikicatChiefMinistersOf")) ss = "ChiefMinistersOf";
                                        if (ss.contains("WikicatChiefsOf")) ss = "ChiefsOf";
                                        if (ss.contains("WikicatChristianMissionariesIn"))
                                            ss = "ChristianMissionariesIn";
                                        if (ss.contains("WikicatChurchesIn")) ss = "ChurchesIn";
                                        if (ss.contains("WikicatCitiesIn")) ss = "CitiesIn";
                                        if (ss.contains("WikicatCivilServantsIn")) ss = "CivilServantsIn";
                                        if (ss.contains("WikicatCoalMinesIn")) ss = "CoalMinesIn";
                                        if (ss.contains("WikicatCoalTownsIn")) ss = "CoalTownsIn";
                                        if (ss.contains("WikicatComicsBy")) ss = "WikicatComicsBy";
                                        if (ss.contains("WikicatCommandersOf")) ss = "CommandersOf";
                                        if (ss.contains("WikicatCommercialBuildingsOn")) ss = "CommercialBuildingsOn";
                                        if (ss.contains("WikicatCommissionersOf")) ss = "CommissionersOf"; //
                                        if (ss.contains("WikicatCommonwealthGamesCompetitorsFor"))
                                            ss = "CommonwealthGamesCompetitorsFor";
                                        if (ss.contains("WikicatCommonwealthGamesSilverMedallists"))
                                            ss = "CommonwealthGamesSilverMedallists";
                                        if (ss.contains("WikicatCommunesIn")) ss = "CommunesIn";
                                        if (ss.contains("WikicatCommunesOf")) ss = "CommunesOf";
                                        if (ss.contains("WikicatCommunistPartiesIn")) ss = "WikicatCommunistPartiesIn";
                                        if (ss.contains("WikicatCommunityCollegesIn"))
                                            ss = "WikicatCommunityCollegesIn";
                                        if (ss.contains("WikicatCommunitySchoolsIn")) ss = "CommunitySchoolsIn";
                                        if (ss.contains("WikicatCompaniesBasedIn")) ss = "CompaniesBasedIn";
                                        if (ss.contains("WikicatCompaniesListedOn")) ss = "CompaniesListedOn";
                                        if (ss.contains("WikicatCompaniesOf")) ss = "CompaniesOf";
                                        if (ss.contains("WikicatComprehensiveSchoolsIn")) ss = "ComprehensiveSchoolsIn";
                                        if (ss.contains("WikicatConservativePartiesIn"))
                                            ss = "WikicatConservativePartiesIn";
                                        if (ss.contains("WikicatContemporaryHitRadioStationsIn"))
                                            ss = "WikicatContemporaryHitRadioStationsIn";
                                        if (ss.contains("WikicatConventionCentersIn")) ss = "ConventionCentersIn";
                                        if (ss.contains("WikicatConvertsTo")) ss = "WikicatConvertsTo";
                                        if (ss.contains("WikicatCouncillorsIn")) ss = "WikicatCouncillorsIn";
                                        if (ss.contains("WikicatCountessesOf")) ss = "WikicatCountessesOf";
                                        if (ss.contains("WikicatCountiesOf")) ss = "WikicatCountiesOf";
                                        if (ss.contains("WikicatCountryHousesIn")) ss = "WikicatCountryHousesIn";
                                        if (ss.contains("WikicatCountsOf")) ss = "WikicatCountsOf";
                                        if (ss.contains("WikicatCountyCommissionersIn"))
                                            ss = "WikicatCountyCommissionersIn";
                                        if (ss.contains("WikicatCountyRoadsIn")) ss = "WikicatCountyRoadsIn";
                                        if (ss.contains("WikicatCountyRoutesIn")) ss = "CountyRoutesIns";
                                        if (ss.contains("WikicatCricketersFrom")) ss = "WikicatCricketersFrom";
                                        if (ss.contains("WikicatCricketGroundsIn")) ss = "WikicatCricketGroundsIn";
                                        if (ss.contains("WikicatCruisersOf")) ss = "WikicatCruisersOf";
                                        if (ss.contains("WikicatCycleRacesIn")) ss = "WikicatCycleRacesIn";
                                        if (ss.contains("WikicatDamsIn")) ss = "WikicatDamsIn";
                                        if (ss.contains("WikicatDefunctAirlinesOf")) ss = "WikicatDefunctAirlinesOf";
                                        if (ss.contains("WikicatDefunctCompaniesBasedIn"))
                                            ss = "WikicatDefunctCompaniesBasedIn";
                                        if (ss.contains("WikicatDefunctPoliticalPartiesIn"))
                                            ss = "WikicatDefunctPoliticalPartiesIn";
                                        if (ss.contains("WikicatDefunctPrisonsIn")) ss = "WikicatDefunctPrisonsIn";
                                        if (ss.contains("WikicatDefunctSchoolsIn")) ss = "WikicatDefunctSchoolsIn";
                                        if (ss.contains("WikicatDefunctSportsVenuesIn"))
                                            ss = "WikicatDefunctSportsVenuesIn";
                                        if (ss.contains("WikicatDelegatesToTheUnitedStatesHouseOfRepresentativesFrom"))
                                            ss = "WikicatDelegatesToTheUnitedStatesHouseOfRepresentativesFrom";
                                        if (ss.contains("WikicatDeputiesOf")) ss = "WikicatDeputiesOf";
                                        if (ss.contains("WikicatDeputyChiefsOf")) ss = "DeputyChiefsOf"; //
                                        if (ss.contains("WikicatDeputyLieutenantsOf")) ss = "DeputyLieutenantsOf";
                                        if (ss.contains("WikicatDeputyPremiersOf")) ss = "WikicatDeputyPremiersOf";
                                        if (ss.contains("WikicatDeputyPrimeMinistersOf"))
                                            ss = "WikicatDeputyPrimeMinistersOf";
                                        if (ss.contains("WikicatDirectorsOf")) ss = "WikicatDirectorsOf";
                                        if (ss.contains("WikicatDisastersIn")) ss = "WikicatDisastersIn";
                                        if (ss.contains("WikicatDiscographiesOf")) ss = "WikicatDiscographiesOf";
                                        if (ss.contains("WikicatDistrictAttorneysIn"))
                                            ss = "WikicatDistrictAttorneysIn";
                                        if (ss.contains("WikicatDistrictsOf")) ss = "WikicatDistrictsOf";
                                        if (ss.contains("WikicatDisusedRailwayStationsIn"))
                                            ss = "WikicatDisusedRailwayStationsIn";
                                        if (ss.contains("WikicatDocumentaryFilmsAbout"))
                                            ss = "WikicatDocumentaryFilmsAbout";
                                        if (ss.contains("WikicatDuchessesOf")) ss = "DuchessesOf";
                                        if (ss.contains("WikicatDukesOf")) ss = "WikicatDukesOf";
                                        if (ss.contains("WikicatDutchPeopleOf")) ss = "WikicatDutchPeopleOf";
                                        if (ss.contains("WikicatEarthquakesIn")) ss = "WikicatEarthquakesIn";
                                        if (ss.contains("WikicatEducationalInstitutionsIn"))
                                            ss = "WikicatEducationalInstitutionsIn";
                                        if (ss.contains("WikicatEducatorsFrom")) ss = "WikicatEducatorsFrom";
                                        if (ss.contains("WikicatElementarySchoolsIn"))
                                            ss = "WikicatElementarySchoolsIn";
                                        if (ss.contains("WikicatEngineeringUniversitiesAndCollegesIn"))
                                            ss = "WikicatEngineeringUniversitiesAndCollegesIn";
                                        if (ss.contains("WikicatEnglishPeopleOf")) ss = "WikicatEnglishPeopleOf";
                                        if (ss.contains("WikicatEnvironmentalOrganisationsBasedIn"))
                                            ss = "WikicatEnvironmentalOrganisationsBasedIn";
                                        if (ss.contains("WikicatExpatriateBasketballPeopleIn"))
                                            ss = "WikicatExpatriateBasketballPeopleIn";
                                        if (ss.contains("WikicatExpatriateFootballManagersIn"))
                                            ss = "WikicatExpatriateFootballManagersIn";
                                        if (ss.contains("WikicatExpresswaysIn")) ss = "WikicatExpresswaysIn";
                                        if (ss.contains("WikicatFast-foodChainsOf")) ss = "WikicatFast-foodChainsOf";
                                        if (ss.contains("WikicatFerryCompaniesOf")) ss = "WikicatFerryCompaniesOf";
                                        if (ss.contains("WikicatFestivalsIn")) ss = "WikicatFestivalsIn";
                                        if (ss.contains("WikicatFictionalCharactersWith"))
                                            ss = "WikicatFictionalCharactersWith";
                                        if (ss.contains("WikicatFictionalPopulatedPlacesIn"))
                                            ss = "WikicatFictionalPopulatedPlacesIn";
                                        if (ss.contains("WikicatFilmsAbout")) ss = "WikicatFilmsAbout";
                                        if (ss.contains("WikicatFilmsBasedOn")) ss = "WikicatFilmsBasedOn";
                                        if (ss.contains("WikicatFilmsDirectedBy")) ss = "WikicatFilmsDirectedBy";
                                        if (ss.contains("WikicatFinnishExpatriatesIn"))
                                            ss = "WikicatFinnishExpatriatesIn";
                                        if (ss.contains("WikicatFootballClubsIn")) ss = "WikicatFootballClubsIn";
                                        if (ss.contains("WikicatFootballVenuesIn")) ss = "WikicatFootballVenuesIn";
                                        if (ss.contains("WikicatForeignMinistersOf")) ss = "WikicatForeignMinistersOf";
                                        if (ss.contains("WikicatFormerCensus-designatedPlacesIn"))
                                            ss = "WikicatFormerCensus-designatedPlacesIn";
                                        if (ss.contains("WikicatFormerMunicipalitiesIn"))
                                            ss = "WikicatFormerMunicipalitiesIn";
                                        if (ss.contains("WikicatFormerPopulatedPlacesIn"))
                                            ss = "WikicatFormerPopulatedPlacesIn";
                                        if (ss.contains("WikicatGermanPeopleOf")) ss = "WikicatGermanPeopleOf";
                                        if (ss.contains("WikicatGhostTownsIn")) ss = "WikicatGhostTownsIn";
                                        if (ss.contains("WikicatGirls'SchoolsIn")) ss = "WikicatGirls'SchoolsIn";
                                        if (ss.contains("WikicatGolfClubsAndCoursesIn"))
                                            ss = "WikicatGolfClubsAndCoursesIn";
                                        if (ss.contains("WikicatGovernmentBuildingsIn"))
                                            ss = "WikicatGovernmentBuildingsIn";
                                        if (ss.contains("WikicatGovernmentMinistersOf"))
                                            ss = "WikicatGovernmentMinistersOf";
                                        if (ss.contains("WikicatGovernment-ownedCompaniesOf"))
                                            ss = "WikicatGovernment-ownedCompaniesOf";
                                        if (ss.contains("WikicatGovernorsOf")) ss = "WikicatGovernorsOf";
                                        if (ss.contains("WikicatGrammarSchoolsIn")) ss = "WikicatGrammarSchoolsIn";
                                        if (ss.contains("WikicatGrandMastersOf")) ss = "WikicatGrandMastersOf";
                                        if (ss.contains("WikicatGrandOfficersOf")) ss = "WikicatGrandOfficersOf";
                                        if (ss.contains("WikicatHighCommissionersOf"))
                                            ss = "WikicatHighCommissionersOf";
                                        if (ss.contains("WikicatHighSchoolsIn")) ss = "WikicatHighSchoolsIn";
                                        if (ss.contains("WikicatHospitalsIn")) ss = "WikicatHospitalsIn";
                                        if (ss.contains("WikicatHotelsI")) ss = "WikicatHotelsI";
                                        if (ss.contains("WikicatHousesIn")) ss = "WikicatHousesIn";
                                        if (ss.contains("WikicatHungarianExpatriatesIn"))
                                            ss = "WikicatHungarianExpatriatesIn";
                                        if (ss.contains("WikicatIceHockeyPeopleFrom"))
                                            ss = "WikicatIceHockeyPeopleFrom";
                                        if (ss.contains("WikicatIndependentSchoolsIn"))
                                            ss = "WikicatIndependentSchoolsIn";
                                        if (ss.contains("WikicatIndustrialBuildingsIn"))
                                            ss = "WikicatIndustrialBuildingsIn";
                                        if (ss.contains("WikicatInsectsOf")) ss = "WikicatInsectsOf";
                                        if (ss.contains("WikicatInsuranceCompaniesOf"))
                                            ss = "WikicatInsuranceCompaniesOf";
                                        if (ss.contains("WikicatInternationalBaccalaureateSchoolsIn"))
                                            ss = "WikicatInternationalBaccalaureateSchoolsIn";
                                        if (ss.contains("WikicatInternationalSchoolsIn"))
                                            ss = "WikicatInternationalSchoolsIn";
                                        if (ss.contains("WikicatInternationalSportsCompetitionsHostedBy"))
                                            ss = "WikicatInternationalSportsCompetitionsHostedBy";
                                        if (ss.contains("WikicatJudgesOfTheUnitedStatesDistrictCourtForTheDistrictOf"))
                                            ss = "WikicatJudgesOfTheUnitedStatesDistrictCourtForTheDistrictOf";
                                        if (ss.contains("WikicatKnightsOfTheOrderOf"))
                                            ss = "WikicatKnightsOfTheOrderOf";
                                        if (ss.contains("WikicatLakesOf")) ss = "WikicatLakesOf";
                                        if (ss.contains("WikicatLandmarksIn")) ss = "WikicatLandmarksIn";
                                        if (ss.contains("WikicatMilitaryFacilitiesOnTheNationalRegisterOfHistoricPlacesIn"))
                                            ss = "WikicatMilitaryFacilitiesOnTheNationalRegisterOfHistoricPlacesIn";
                                        if (ss.contains("WikicatMilitaryUnitsAndFormationsOf"))
                                            ss = "WikicatMilitaryUnitsAndFormationsOf";
                                        if (ss.contains("WikicatMinesIn")) ss = "WikicatMinesIn";
                                        if (ss.contains("WikicatMixedMartialArtistsFrom"))
                                            ss = "WikicatMixedMartialArtistsFrom";
                                        if (ss.contains("WikicatMobilePhoneCompaniesOf"))
                                            ss = "WikicatMobilePhoneCompaniesOf";
                                        if (ss.contains("WikicatMonumentsAndMemorialsIn"))
                                            ss = "WikicatMonumentsAndMemorialsIn";
                                        if (ss.contains("WikicatMountainsOf")) ss = "WikicatMountainsOf";
                                        if (ss.contains("WikicatMunicipalitiesOf")) ss = "WikicatMunicipalitiesOf";
                                        if (ss.contains("WikicatMuseumsIn")) ss = "WikicatMuseumsIn";
                                        if (ss.contains("WikicatMusicalGroupsFrom")) ss = "WikicatMusicalGroupsFrom";
                                        if (ss.contains("WikicatMusicFestivalsIn")) ss = "WikicatMusicFestivalsIn";
                                        if (ss.contains("WikicatMusiciansFrom")) ss = "WikicatMusiciansFrom";
                                        if (ss.contains("WikicatMusicVideosDirectedBy"))
                                            ss = "WikicatMusicVideosDirectedBy";
                                        if (ss.contains("WikicatNationalHistoricLandmarksIn"))
                                            ss = "WikicatNationalHistoricLandmarksIn";
                                        if (ss.contains("WikicatNaturalDisastersIn")) ss = "WikicatNaturalDisastersIn";
                                        if (ss.contains("WikicatNaturalizedCitizensOf"))
                                            ss = "WikicatNaturalizedCitizensOf";
                                        if (ss.contains("WikicatNeighborhoodsIn")) ss = "WikicatNeighborhoodsIn";
                                        if (ss.contains("WikicatNeighbourhoods")) ss = "Neighbourhoods";
                                        if (ss.contains("WikicatNewspapersPublishedIn"))
                                            ss = "WikicatNewspapersPublishedIn";
                                        if (ss.contains("WikicatNigerianExpatriatesIn"))
                                            ss = "WikicatNigerianExpatriatesIn";
                                        if (ss.contains("WikicatNondenominationalChristianSchoolsIn"))
                                            ss = "WikicatNondenominationalChristianSchoolsIn";
                                        if (ss.contains("WikicatNon-fictionBooksAbout"))
                                            ss = "WikicatNon-fictionBooksAbout";
                                        if (ss.contains("WikicatNon-governmentalOrganizationsBasedIn"))
                                            ss = "WikicatNon-governmentalOrganizationsBasedIn";
                                        if (ss.contains("WikicatNon-profitOrganisationsBasedIn"))
                                            ss = "WikicatNon-profitOrganisationsBasedIn";
                                        if (ss.contains("WikicatNovelsBy")) ss = "WikicatNovelsBy";
                                        if (ss.contains("WikicatNursingSchoolsIn")) ss = "WikicatNursingSchoolsIn";
                                        if (ss.contains("WikicatOfficeBuildingsIn")) ss = "WikicatOfficeBuildingsIn";
                                        if (ss.contains("WikicatOfficersOfTheOrderOf"))
                                            ss = "WikicatOfficersOfTheOrderOf";
                                        if (ss.contains("WikicatOlympicAthletesOf")) ss = "WikicatOlympicAthletesOf";
                                        if (ss.contains("WikicatOlympicBronzeMedalistsFor"))
                                            ss = "WikicatOlympicBronzeMedalistsFor";
                                        if (ss.contains("WikicatOlympicCanoeistsOf")) ss = "WikicatOlympicCanoeistsOf";
                                        if (ss.contains("WikicatOlympicEquestriansOf"))
                                            ss = "WikicatOlympicEquestriansOf";
                                        if (ss.contains("WikicatOlympicFencersOf")) ss = "WikicatOlympicFencersOf";
                                        if (ss.contains("WikicatOlympicFieldHockeyPlayersOf"))
                                            ss = "WikicatOlympicFieldHockeyPlayersOf";
                                        if (ss.contains("WikicatOlympicFigureSkatersOf"))
                                            ss = "WikicatOlympicFigureSkatersOf";
                                        if (ss.contains("WikicatOlympicFootballersOf"))
                                            ss = "WikicatOlympicFootballersOf";
                                        if (ss.contains("WikicatOlympicGoldMedalistsFor"))
                                            ss = "WikicatOlympicGoldMedalistsFor";
                                        if (ss.contains("WikicatOlympicGymnastsOf")) ss = "WikicatOlympicGymnastsOf";
                                        if (ss.contains("WikicatOlympicHandballPlayersOf"))
                                            ss = "WikicatOlympicHandballPlayersOf";
                                        if (ss.contains("WikicatOlympicIceHockeyPlayersOf"))
                                            ss = "WikicatOlympicIceHockeyPlayersOf";
                                        if (ss.contains("WikicatOlympicSailorsOf")) ss = "WikicatOlympicSailorsOf";
                                        if (ss.contains("WikicatOlympicShootersOf")) ss = "WikicatOlympicShootersOf";
                                        if (ss.contains("WikicatOlympicSilverMedalistsFor"))
                                            ss = "WikicatOlympicSilverMedalistsFor";
                                        if (ss.contains("WikicatOlympicSpeedSkatersOf"))
                                            ss = "WikicatOlympicSpeedSkatersOf";
                                        if (ss.contains("WikicatOlympicSwimmersOf")) ss = "WikicatOlympicSwimmersOf";
                                        if (ss.contains("WikicatOlympicTennisPlayersOf"))
                                            ss = "WikicatOlympicTennisPlayersOf";
                                        if (ss.contains("WikicatOlympicVolleyballPlayersOf"))
                                            ss = "WikicatOlympicVolleyballPlayersOf";
                                        if (ss.contains("WikicatOlympicWeightliftersOf"))
                                            ss = "WikicatOlympicWeightliftersOf";
                                        if (ss.contains("WikicatOrchidsOf")) ss = "WikicatOrchidsOf";
                                        if (ss.contains("WikicatOrganisationsBasedIn"))
                                            ss = "WikicatOrganisationsBasedIn";
                                        if (ss.contains("WikicatOrganizationsBasedIn"))
                                            ss = "WikicatOrganizationsBasedIn";
                                        if (ss.contains("WikicatPaintersFrom")) ss = "PaintersFrom";
                                        if (ss.contains("WikicatPaintings")) ss = "Paintings";
                                        if (ss.contains("WikicatPalacesIn")) ss = "WikicatPalacesIn";
                                        if (ss.contains("WikicatPanAmericanGamesCompetitorsFor"))
                                            ss = "WikicatPanAmericanGamesCompetitorsFor";
                                        if (ss.contains("WikicatParalympicGoldMedalistsFor"))
                                            ss = "WikicatParalympicGoldMedalistsFor";
                                        if (ss.contains("WikicatParalympicSilverMedalistsFor"))
                                            ss = "WikicatParalympicSilverMedalistsFor";
                                        if (ss.contains("WikicatParksIn")) ss = "WikicatParksIn";
                                        if (ss.contains("WikicatPassengerShipsOf")) ss = "WikicatPassengerShipsOf";
                                        if (ss.contains("WikicatPeopleAssociatedWithTheUniversity"))
                                            ss = "WikicatPeopleAssociatedWithTheUniversity";
                                        if (ss.contains("WikicatPeopleConvictedOf")) ss = "WikicatPeopleConvictedOf";
                                        if (ss.contains("WikicatPeopleDeportedFrom")) ss = "WikicatPeopleDeportedFrom";
                                        if (ss.contains("WikicatPeopleEducatedAt")) ss = "WikicatPeopleEducatedAt";
                                        if (ss.contains("WikicatPeopleExecutedBy")) ss = "WikicatPeopleExecutedBy";
                                        if (ss.contains("WikicatPeopleExtraditedTo")) ss = "WikicatPeopleExtraditedTo";
                                        if (ss.contains("WikicatPeopleFrom")) ss = "PeopleFrom";
                                        if (ss.contains("WikicatPeopleKilledBy")) ss = "WikicatPeopleKilledBy";
                                        if (ss.contains("WikicatPeopleMurderedIn")) ss = "WikicatPeopleMurderedIn";
                                        if (ss.contains("WikicatPeopleOf")) ss = "WikicatPeopleOf";
                                        if (ss.contains("WikicatPeopleWhoDiedIn")) ss = "WikicatPeopleWhoDiedIn";
                                        if (ss.contains("WikicatPermanentRepresentativesOf"))
                                            ss = "WikicatPermanentRepresentativesOf";
                                        if (ss.contains("WikicatPermanentSecretariesOf"))
                                            ss = "WikicatPermanentSecretariesOf";
                                        if (ss.contains("WikicatPhysiciansFrom")) ss = "WikicatPhysiciansFrom";
                                        if (ss.contains("WikicatPlacesOfWorshipIn")) ss = "WikicatPlacesOfWorshipIn";
                                        if (ss.contains("WikicatPlayersOf")) ss = "WikicatPlayersOf";
                                        if (ss.contains("WikicatPolishPeopleOf")) ss = "WikicatPolishPeopleOf";
                                        if (ss.contains("WikicatPoliticalPartiesIn")) ss = "WikicatPoliticalPartiesIn";
                                        if (ss.contains("WikicatPoliticiansOfTheRepublicOfChinaOnTaiwanFrom"))
                                            ss = "WikicatPoliticiansOfTheRepublicOfChinaOnTaiwanFrom";
                                        if (ss.contains("WikicatPopulatedPlacesIn'")) ss = "WikicatPopulatedPlacesIn'";
                                        if (ss.contains("WikicatPopulatedPlacesOn")) ss = "WikicatPopulatedPlacesOn";
                                        if (ss.contains("WikicatPortsAndHarboursOf")) ss = "WikicatPortsAndHarboursOf";
                                        if (ss.contains("WikicatPrefectsOf")) ss = "WikicatPrefectsOf";
                                        if (ss.contains("WikicatPreparatorySchoolsIn"))
                                            ss = "WikicatPreparatorySchoolsIn"; //
                                        if (ss.contains("WikicatPresidentsOf")) ss = "WikicatPresidentsOf";
                                        if (ss.contains("WikicatPresidentsOfTheUniversityOf"))
                                            ss = "WikicatPresidentsOfTheUniversityOf";
                                        if (ss.contains("WikicatPrimarySchoolsIn")) ss = "WikicatPrimarySchoolsIn";
                                        if (ss.contains("WikicatPrimeMinistersOf")) ss = "WikicatPrimeMinistersOf";
                                        if (ss.contains("WikicatPrincessesOf")) ss = "WikicatPrincessesOf";
                                        if (ss.contains("WikicatPrisonersAndDetaineesOf"))
                                            ss = "WikicatPrisonersAndDetaineesOf";
                                        if (ss.contains("WikicatPrisonersSentencedToDeathBy"))
                                            ss = "WikicatPrisonersSentencedToDeathBy";
                                        if (ss.contains("WikicatPrisonersSentencedToLifeImprisonmentBy"))
                                            ss = "WikicatPrisonersSentencedToLifeImprisonmentBy";
                                        if (ss.contains("WikicatPrisonersWhoDiedIn")) ss = "WikicatPrisonersWhoDiedIn";
                                        if (ss.contains("WikicatPrivateElementarySchoolsIn"))
                                            ss = "WikicatPrivateElementarySchoolsIn";
                                        if (ss.contains("WikicatPrivateHighSchoolsIn"))
                                            ss = "WikicatPrivateHighSchoolsIn";
                                        if (ss.contains("WikicatPrivatelyHeldCompaniesBasedIn"))
                                            ss = "WikicatPrivatelyHeldCompaniesBasedIn";
                                        if (ss.contains("WikicatPrivatelyHeldCompaniesOf"))
                                            ss = "WikicatPrivatelyHeldCompaniesOf";
                                        if (ss.contains("WikicatPrivateMiddleSchoolsIn"))
                                            ss = "WikicatPrivateMiddleSchoolsIn";
                                        if (ss.contains("WikicatPrivateSchoolsIn")) ss = "WikicatPrivateSchoolsIn";
                                        if (ss.contains("WikicatProfessionalWrestlersFrom"))
                                            ss = "WikicatProfessionalWrestlersFrom";
                                        if (ss.contains("WikicatProposedBuildingsAndStructuresIn"))
                                            ss = "WikicatProposedBuildingsAndStructuresIn";
                                        if (ss.contains("WikicatProtectedAreasOf")) ss = "WikicatProtectedAreasOf";
                                        if (ss.contains("WikicatPublicHighSchoolsIn"))
                                            ss = "WikicatPublicHighSchoolsIn";
                                        if (ss.contains("WikicatPublicMiddleSchoolsIn"))
                                            ss = "WikicatPublicMiddleSchoolsIn";
                                        if (ss.contains("WikicatPublicSchoolsIn")) ss = "WikicatPublicSchoolsIn";
                                        if (ss.contains("WikicatPupilsOf")) ss = "WikicatPupilsOf";
                                        if (ss.contains("WikicatRacingDriversFrom")) ss = "WikicatRacingDriversFrom"; //
                                        if (ss.contains("WikicatRadioStationsIn")) ss = "WikicatRadioStationsIn";
                                        if (ss.contains("WikicatRailwayAccidentsIn")) ss = "WikicatRailwayAccidentsIn";
                                        if (ss.contains("WikicatRailwayCompaniesOf")) ss = "WikicatRailwayCompaniesOf";
                                        if (ss.contains("WikicatRailwayLinesIn")) ss = "WikicatRailwayLinesIn";
                                        if (ss.contains("WikicatRailwayStationsIn")) ss = "WikicatRailwayStationsIn";
                                        if (ss.contains("WikicatRectorsOf")) ss = "WikicatRectorsOf";
                                        if (ss.contains("WikicatResearchInstitutesIn"))
                                            ss = "WikicatResearchInstitutesIn";
                                        if (ss.contains("WikicatRestaurantsIn")) ss = "WikicatRestaurantsIn";
                                        if (ss.contains("WikicatRiversOf")) ss = "WikicatRiversOf";
                                        if (ss.contains("WikicatRoadsIn")) ss = "WikicatRoadsIn";
                                        if (ss.contains("WikicatRollerCoastersManufacturedBy"))
                                            ss = "WikicatRollerCoastersManufacturedBy";
                                        if (ss.contains("WikicatRomanCatholicBishopsOf"))
                                            ss = "WikicatRomanCatholicBishopsOf";
                                        if (ss.contains("WikicatRomanCatholicSchoolsIn"))
                                            ss = "WikicatRomanCatholicSchoolsIn";
                                        if (ss.contains("WikicatRomanCatholicSecondarySchoolsIn"))
                                            ss = "WikicatRomanCatholicSecondarySchoolsIn";
                                        if (ss.contains("WikicatRomanCatholicUniversitiesAndCollegesIn"))
                                            ss = "WikicatRomanCatholicUniversitiesAndCollegesIn";
                                        if (ss.contains("WikicatSchoolsIn")) ss = "SchoolsIn";
                                        if (ss.contains("WikicatSchoolsOf")) ss = "SchoolsOf";
                                        if (ss.contains("WikicatScottishExpatriatesIn"))
                                            ss = "WikicatScottishExpatriatesIn";
                                        if (ss.contains("WikicatSecondarySchoolsIn")) ss = "WikicatSecondarySchoolsIn";
                                        if (ss.contains("WikicatSecretariesOfStateOf"))
                                            ss = "WikicatSecretariesOfStateOf";
                                        if (ss.contains("WikicatSerbianPeopleOf")) ss = "WikicatSerbianPeopleOf";
                                        if (ss.contains("WikicatShippingCompaniesOf"))
                                            ss = "WikicatShippingCompaniesOf";
                                        if (ss.contains("WikicatShipsOf")) ss = "WikicatShipsOf";
                                        if (ss.contains("WikicatShipwrecksIn")) ss = "WikicatShipwrecksIn";
                                        if (ss.contains("WikicatShoppingMallsIn")) ss = "WikicatShoppingMallsIn";
                                        if (ss.contains("WikicatShortStoriesBy")) ss = "WikicatShortStoriesBy";
                                        if (ss.contains("WikicatSkyscrapersIn")) ss = "WikicatSkyscrapersIn";
                                        if (ss.contains("WikicatSoftwareCompanies")) ss = "SoftwareCompanies";
                                        if (ss.contains("WikicatSongRecordingsProducedBy"))
                                            ss = "WikicatSongRecordingsProducedBy";
                                        if (ss.contains("WikicatSongsAbout")) ss = "WikicatSongsAbout";
                                        if (ss.contains("WikicatSongsWithLyricsBy")) ss = "WikicatSongsWithLyricsBy";
                                        if (ss.contains("WikicatSongsWithMusicBy")) ss = "WikicatSongsWithMusicBy";
                                        if (ss.contains("WikicatSpeakersOfTheNationalAssemblyOf"))
                                            ss = "WikicatSpeakersOfTheNationalAssemblyOf";
                                        if (ss.contains("WikicatSportsVenuesIn")) ss = "WikicatSportsVenuesIn";
                                        if (ss.contains("WikicatSquadronsOfTheRepublicOf"))
                                            ss = "WikicatSquadronsOfTheRepublicOf";
                                        if (ss.contains("WikicatStateParksOf")) ss = "WikicatStateParksOf";
                                        if (ss.contains("WikicatStateTreasurersOf")) ss = "WikicatStateTreasurersOf";
                                        if (ss.contains("WikicatSteamLocomotivesOf")) ss = "WikicatSteamLocomotivesOf";
                                        if (ss.contains("WikicatSteamshipsOf")) ss = "WikicatSteamshipsOf";
                                        if (ss.contains("WikicatSteelCompaniesOf")) ss = "WikicatSteelCompaniesOf";
                                        if (ss.contains("WikicatStudentNewspapersPublishedIn"))
                                            ss = "WikicatStudentNewspapersPublishedIn";
                                        if (ss.contains("WikicatSubmarineCommunicationsCablesIn"))
                                            ss = "WikicatSubmarineCommunicationsCablesIn";
                                        if (ss.contains("WikicatSubmarinesOf")) ss = "WikicatSubmarinesOf";
                                        if (ss.contains("WikicatSwedishPeopleOf")) ss = "WikicatSwedishPeopleOf";
                                        if (ss.contains("WikicatTelecommunicationsCompaniesOf"))
                                            ss = "WikicatTelecommunicationsCompaniesOf";
                                        if (ss.contains("WikicatTelevisionStationsIn"))
                                            ss = "WikicatTelevisionStationsIn";
                                        if (ss.contains("WikicatTownshipsIn")) ss = "WikicatTownshipsIn";
                                        if (ss.contains("WikicatTownsIn")) ss = "TownsIn";
                                        if (ss.contains("WikicatTramVehiclesOf")) ss = "TramVehiclesOf";
                                        if (ss.contains("WikicatTranslatorsOf")) ss = "WikicatTranslatorsOf";
                                        if (ss.contains("WikicatTransportCompaniesOf"))
                                            ss = "WikicatTransportCompaniesOf";
                                        if (ss.contains("WikicatTreatiesExtendedTo")) ss = "WikicatTreatiesExtendedTo";
                                        if (ss.contains("WikicatTreesOf")) ss = "WikicatTreesOf";
                                        if (ss.contains("WikicatTributariesOf")) ss = "WikicatTributariesOf";
                                        if (ss.contains("WikicatTropicalCyclonesIn")) ss = "WikicatTropicalCyclonesIn";
                                        if (ss.contains("WikicatUnincorporatedCommunitiesIn"))
                                            ss = "UnincorporatedCommunitiesIn";
                                        if (ss.contains("WikicatUnitedStatesAttorneysFor"))
                                            ss = "UnitedStatesAttorneysFor";
                                        if (ss.contains("WikicatUniversalMusic")) ss = "UniversalMusic";
                                        if (ss.contains("WikicatUniversities")) ss = "Universities";
                                        if (ss.contains("WikicatUniversitiesAndCollegesAffiliated"))
                                            ss = "UniversitiesAndCollegesAffiliated";
                                        if (ss.contains("WikicatValleysOf")) ss = "ValleysOf";
                                        if (ss.contains("WikicatVelodromesIn")) ss = "WikicatVelodromesIn";
                                        if (ss.contains("WikicatVenezuelanPeopleOf")) ss = "WikicatVenezuelanPeopleOf";
                                        if (ss.contains("WikicatVictimsOf")) ss = "WikicatVictimsOf";
                                        if (ss.contains("WikicatVictimsOfAviationAccidentsOrIncidentsIn"))
                                            ss = "VictimsOfAviationAccidentsOrIncidentsIn";
                                        if (ss.contains("WikicatVideoGameCompaniesOf"))
                                            ss = "WikicatVideoGameCompaniesOf";
                                        if (ss.contains("WikicatVillagesIn")) ss = "VillagesIn";
                                        if (ss.contains("WikicatViscountsOf")) ss = "WikicatViscountsOf";
                                        if (ss.contains("WikicatVisitorAttractionsIn"))
                                            ss = "WikicatVisitorAttractionsIn";
                                        if (ss.contains("WikicatVolcanoesOf")) ss = "VolcanoesOf";
                                        if (ss.contains("WikicatVolleyballPlayers")) ss = "VolleyballPlayers";
                                        if (ss.contains("WikicatWeatherEventsIn")) ss = "WikicatWeatherEventsIn";
                                        if (ss.contains("WikicatWomenOf")) ss = "WomenOf"; //
                                        if (ss.contains("WikicatWomenStateLegislatorsIn"))
                                            ss = "WomenStateLegislatorsIn";
                                        if (ss.contains("WikicatWorksAbout")) ss = "WorksAbout";
                                        if (ss.contains("WikicatWorksBy")) ss = "WorksBy";
                                        if (ss.contains("WikicatWorldWar")) ss = "WorldWar";
                                        if (ss.contains("WikicatWriters")) ss = "Writers";
                                        if (ss.contains("WikicatYearsOfThestCenturyIn")) ss = "YearsOfThestCenturyIn";
                                        if (ss.contains("WikicatYouthOrganisationsBasedIn"))
                                            ss = "YouthOrganisationsBasedIn";
                                        if (ss.contains("WikicatZoosIn")) ss = "ZoosIn";
                                        if (ss.contains("WikicatAircraftManufacturersOf"))
                                            ss = "WikicatAircraftManufacturersOf";
                                        if (ss.contains("WikicatAmericanExpatriateBasketballPeopleIn"))
                                            ss = "WikicatAmericanExpatriateBasketballPeopleIn";
                                        if (ss.contains("WikicatAmericanExpatriatesIn"))
                                            ss = "WikicatAmericanExpatriatesIn";
                                        if (ss.contains("WikicatAmericanMormonMissionariesIn"))
                                            ss = "WikicatAmericanMormonMissionariesIn";
                                        if (ss.contains("WikicatAmericanPeopleConvictedOf"))
                                            ss = "WikicatAmericanPeopleConvictedOf";
                                        if (ss.contains("WikicatAmericanPeopleExecutedBy"))
                                            ss = "WikicatAmericanPeopleExecutedBy";
                                        if (ss.contains("WikicatAmericanPeopleOf")) ss = "WikicatAmericanPeopleOf";
                                        if (ss.contains("WikicatAnglicanArchbishopsOf"))
                                            ss = "WikicatAnglicanArchbishopsOf";
                                        if (ss.contains("WikicatAnglicanArchdeaconsIn"))
                                            ss = "WikicatAnglicanArchdeaconsIn";
                                        if (ss.contains("WikicatAnglicanBishopsOf")) ss = "WikicatAnglicanBishopsOf";
                                        if (ss.contains("WikicatAnglicanSchoolsIn")) ss = "WikicatAnglicanSchoolsIn";
                                        if (ss.contains("WikicatAnglicanSuffraganBishopsInTheDioceseOf"))
                                            ss = "WikicatAnglicanSuffraganBishopsInTheDioceseOf";
                                        if (ss.contains("WikicatAsianGamesMedalistsIn"))
                                            ss = "WikicatAsianGamesMedalistsIn";
                                        if (ss.contains("WikicatAustralianPeopleOf")) ss = "WikicatAustralianPeopleOf";
                                        if (ss.contains("WikicatBelgianExpatriatesIn"))
                                            ss = "WikicatBelgianExpatriatesIn";
                                        if (ss.contains("WikicatBelgianPeopleOf")) ss = "WikicatBelgianPeopleOf";
                                        if (ss.contains("WikicatBenineseExpatriatesIn"))
                                            ss = "WikicatBenineseExpatriatesIn";
                                        if (ss.contains("WikicatBeverageCompaniesOf"))
                                            ss = "WikicatBeverageCompaniesOf";
                                        if (ss.contains("WikicatBiographicalMuseumsIn"))
                                            ss = "WikicatBiographicalMuseumsIn";
                                        if (ss.contains("WikicatBrazilianExpatriatesIn"))
                                            ss = "WikicatBrazilianExpatriatesIn";
                                        if (ss.contains("WikicatBrazilianPeopleOf")) ss = "WikicatBrazilianPeopleOf";
                                        if (ss.contains("WikicatCanadianExpatriateLacrossePeopleIn"))
                                            ss = "WikicatCanadianExpatriateLacrossePeopleIn";
                                        if (ss.contains("WikicatFinanceMinistersOf")) ss = "WikicatFinanceMinistersOf";
                                        if (ss.contains("WikicatFinancialServicesCompaniesOf"))
                                            ss = "WikicatFinancialServicesCompaniesOf";
                                        if (ss.contains("WikicatFinnishPeopleOf")) ss = "WikicatFinnishPeopleOf";
                                        if (ss.contains("WikicatFirstNationsGovernmentsIn"))
                                            ss = "WikicatFirstNationsGovernmentsIn";
                                        if (ss.contains("WikicatFlagsOf")) ss = "WikicatFlagsOf";
                                        if (ss.contains("WikicatFootballPlayersFrom"))
                                            ss = "WikicatFootballPlayersFrom";
                                        if (ss.contains("WikicatFormerChurchesIn")) ss = "WikicatFormerChurchesIn";
                                        if (ss.contains("WikicatFormerHighSchoolsIn"))
                                            ss = "WikicatFormerHighSchoolsIn";
                                        if (ss.contains("WikicatFormerStateHighwaysIn"))
                                            ss = "WikicatFormerStateHighwaysIn";
                                        if (ss.contains("WikicatFoundationSchoolsIn"))
                                            ss = "WikicatFoundationSchoolsIn";
                                        if (ss.contains("WikicatFrenchPeopleOf")) ss = "WikicatFrenchPeopleOf";
                                        if (ss.contains("WikicatFugitivesWantedBy")) ss = "WikicatFugitivesWantedBy";
                                        if (ss.contains("WikicatGaelicFootballClubsIn"))
                                            ss = "WikicatGaelicFootballClubsIn";
                                        if (ss.contains("WikicatGaeltachtPlacesIn")) ss = "WikicatGaeltachtPlacesIn";
                                        if (ss.contains("WikicatGovernmentMinistriesOf"))
                                            ss = "WikicatGovernmentMinistriesOf";
                                        if (ss.contains("WikicatGradeII*ListedBuildingsIn"))
                                            ss = "WikicatGradeII*ListedBuildingsIn";
                                        if (ss.contains("WikicatGrandCommandersOfTheOrderOf"))
                                            ss = "WikicatGrandCommandersOfTheOrderOf";
                                        if (ss.contains("WikicatGreekPeopleOf")) ss = "WikicatGreekPeopleOf";
                                        if (ss.contains("WikicatGulfsOf")) ss = "WikicatGulfsOf";
                                        if (ss.contains("WikicatHamletsIn")) ss = "WikicatHamletsIn";
                                        if (ss.contains("WikicatHeadlandsOf")) ss = "WikicatHeadlandsOf";
                                        if (ss.contains("WikicatHeavyMetalFestivalsIn"))
                                            ss = "WikicatHeavyMetalFestivalsIn";
                                        if (ss.contains("WikicatHighSheriffsOf")) ss = "WikicatHighSheriffsOf";
                                        if (ss.contains("WikicatHistoriansOf")) ss = "WikicatHistoriansOf";
                                        if (ss.contains("WikicatHistoricHouseMuseumsIn"))
                                            ss = "WikicatHistoricHouseMuseumsIn";
                                        if (ss.contains("WikicatHistoryMuseumsIn")) ss = "WikicatHistoryMuseumsIn";
                                        if (ss.contains("WikicatHongKongPeopleOf")) ss = "WikicatHongKongPeopleOf";
                                        if (ss.contains("WikicatHorseBreedsOriginatingIn"))
                                            ss = "WikicatHorseBreedsOriginatingIn";
                                        if (ss.contains("WikicatHospitalShipsOf")) ss = "WikicatHospitalShipsOf";
                                        if (ss.contains("WikicatHousesOnTheNationalRegisterOfHistoricPlacesIn"))
                                            ss = "WikicatHousesOnTheNationalRegisterOfHistoricPlacesIn";
                                        if (ss.contains("WikicatHungarianPeopleOf")) ss = "WikicatHungarianPeopleOf";
                                        if (ss.contains("WikicatHurricanesIn")) ss = "WikicatHurricanesIn";
                                        if (ss.contains("WikicatIceHockeyTeamsIn")) ss = "WikicatIceHockeyTeamsIn";
                                        if (ss.contains("WikicatImportantBirdAreas")) ss = "WikicatImportantBirdAreas";
                                        if (ss.contains("WikicatIncorporatedPlacesIn"))
                                            ss = "WikicatIncorporatedPlacesIn";
                                        if (ss.contains("WikicatIndiePopGroupsFrom")) ss = "WikicatIndiePopGroupsFrom";
                                        if (ss.contains("WikicatIndoorIceHockeyVenuesIn"))
                                            ss = "WikicatIndoorIceHockeyVenuesIn";
                                        if (ss.contains("WikicatInfantryDivisionsOf"))
                                            ss = "WikicatInfantryDivisionsOf";
                                        if (ss.contains("WikicatInternationalIceHockeyCompetitionsHostedBy"))
                                            ss = "WikicatInternationalIceHockeyCompetitionsHostedBy";
                                        if (ss.contains("WikicatInterstateHighwaysIn"))
                                            ss = "WikicatInterstateHighwaysIn";
                                        if (ss.contains("WikicatIrishPeopleOf")) ss = "WikicatIrishPeopleOf";
                                        if (ss.contains("WikicatIslandsOf")) ss = "WikicatIslandsOf";
                                        if (ss.contains("WikicatIsraeliEmigrantsTo")) ss = "WikicatIsraeliEmigrantsTo";
                                        if (ss.contains("WikicatIsraeliPeopleOf")) ss = "WikicatIsraeliPeopleOf";
                                        if (ss.contains("WikicatItalianPeopleOf")) ss = "WikicatItalianPeopleOf";
                                        if (ss.contains("WikicatJudgesOfTheUnitedStatesDistrictCourtFor"))
                                            ss = "WikicatJudgesOfTheUnitedStatesDistrictCourtFor";
                                        if (ss.contains("WikicatLawSchoolsIn")) ss = "WikicatLawSchoolsIn";
                                        if (ss.contains("WikicatLeadersOf")) ss = "WikicatLeadersOf";
                                        if (ss.contains("WikicatLGBTPeopleFrom")) ss = "WikicatLGBTPeopleFrom";
                                        if (ss.contains("WikicatLieutenantGovernorsOf"))
                                            ss = "WikicatLieutenantGovernorsOf";
                                        if (ss.contains("WikicatLighthouseMuseumsIn"))
                                            ss = "WikicatLighthouseMuseumsIn";
                                        if (ss.contains("WikicatLocalitiesIn")) ss = "WikicatLocalitiesIn";
                                        if (ss.contains("WikicatMagnetSchoolsIn")) ss = "WikicatMagnetSchoolsIn";
                                        if (ss.contains("WikicatManufacturingCompaniesOf"))
                                            ss = "WikicatManufacturingCompaniesOf";
                                        if (ss.contains("WikicatMargravesOf")) ss = "WikicatMargravesOf";
                                        if (ss.contains("WikicatMaritimeIncidentsIn"))
                                            ss = "WikicatMaritimeIncidentsIn";
                                        if (ss.contains("WikicatMarquessesOf")) ss = "WikicatMarquessesOf";
                                        if (ss.contains("WikicatMassacresIn")) ss = "WikicatMassacresIn";
                                        if (ss.contains("WikicatMastersOf")) ss = "WikicatMastersOf";
                                        if (ss.contains("WikicatMayorsOf")) ss = "WikicatMayorsOf";
                                        if (ss.contains("WikicatMayorsOf")) ss = "WikicatMayorsOf";
                                        if (ss.contains("WikicatMayorsOfPlacesIn")) ss = "WikicatMayorsOfPlacesIn";
                                        if (ss.contains("WikicatMediaCompaniesOf")) ss = "WikicatMediaCompaniesOf";
                                        if (ss.contains("WikicatMediaIn")) ss = "WikicatMediaIn";
                                        if (ss.contains("WikicatMerchantShipsOf")) ss = "WikicatMerchantShipsOf";
                                        if (ss.contains("WikicatMexicanPeopleOf")) ss = "WikicatMexicanPeopleOf";
                                        if (ss.contains("WikicatMiddleSchoolsIn")) ss = "WikicatMiddleSchoolsIn";
                                        if (ss.contains("WikicatMilitaryUnitsAndFormationsIn"))
                                            ss = "WikicatMilitaryUnitsAndFormationsIn";
                                        if (ss.contains("WikicatMiningCompaniesOf")) ss = "WikicatMiningCompaniesOf";
                                        if (ss.contains("WikicatMinistersOfDefenseOf"))
                                            ss = "WikicatMinistersOfDefenseOf";
                                        if (ss.contains("WikicatMinistersOfFinanceOf"))
                                            ss = "WikicatMinistersOfFinanceOf";
                                        if (ss.contains("WikicatMountainsAndHillsOf"))
                                            ss = "WikicatMountainsAndHillsOf";
                                        if (ss.contains("WikicatMunicipalPresidentsIn"))
                                            ss = "WikicatMunicipalPresidentsIn";
                                        if (ss.contains("WikicatMuseumShipsIn")) ss = "WikicatMuseumShipsIn";
                                        if (ss.contains("WikicatMusicSchoolsIn")) ss = "WikicatMusicSchoolsIn";
                                        if (ss.contains("WikicatNationalBasketballAssociationPlayersFrom"))
                                            ss = "WikicatNationalBasketballAssociationPlayersFrom";
                                        if (ss.contains("WikicatNationalHistoricSitesIn"))
                                            ss = "WikicatNationalHistoricSitesIn";
                                        if (ss.contains("WikicatNationalNaturalLandmarksIn"))
                                            ss = "WikicatNationalNaturalLandmarksIn";
                                        if (ss.contains("WikicatNationalSportsTeamsOf"))
                                            ss = "WikicatNationalSportsTeamsOf";
                                        if (ss.contains("WikicatNaturalHistoryMuseumsIn"))
                                            ss = "WikicatNaturalHistoryMuseumsIn";
                                        if (ss.contains("WikicatNaturalisedCitizensOf"))
                                            ss = "WikicatNaturalisedCitizensOf";
                                        if (ss.contains("WikicatNavalBattlesInvolving"))
                                            ss = "WikicatNavalBattlesInvolving";
                                        if (ss.contains("WikicatNovelsAbout")) ss = "WikicatNovelsAbout";
                                        if (ss.contains("WikicatOlympicAlpineSkiersOf"))
                                            ss = "WikicatOlympicAlpineSkiersOf";
                                        if (ss.contains("WikicatOlympicBasketballPlayersOf"))
                                            ss = "WikicatOlympicBasketballPlayersOf";
                                        if (ss.contains("WikicatPeopleAssociatedWith"))
                                            ss = "WikicatPeopleAssociatedWith";
                                        if (ss.contains("WikicatPeopleExtraditedFrom"))
                                            ss = "WikicatPeopleExtraditedFrom";
                                        if (ss.contains("WikicatPeopleKilledIn")) ss = "WikicatPeopleKilledIn";
                                        if (ss.contains("WikicatPopulatedPlacesIn")) ss = "WikicatPopulatedPlacesIn";

                                        col.add("[" + ss + " , " + r2.getObject().toString() + "]");
                                        System.out.println(ss);
                                    } else {
                                        col.add("[" + r2.getSubject().toString() + " , " + r2.getObject().toString() + "]");

                                    }
                                    if (s.contains("FACT") && s.length() == 4) nbFact++;
                                    if (s.contains("FACTATTRIBUTE")) nbFactAtt++;
                                    if (s.contains("DIMENSIONATTRIBUTE")) nbDimAtt++;
                                    if (s.contains("NONFUNCTIONALDIMENSION") && s.length() == 22) nbNonFunctionalDim++;
                                    if (s.contains("NONFUNCTIONALDIMENSIONLEVEL")) nbNonFunctDimLevel++;


                                }
                            }

                        }


                    }
                }
            }
            System.out.println("NB valid Graphs: " + nbGraph);
            FileOperation.WriteInFile("D:\\Documents_Dihia\\ConstrunctedGraphs.txt", col);
            System.out.println("nbDimAtt: " + nbDimAtt);
            System.out.println("nbNonFunctionalDim: " + nbNonFunctionalDim);
            System.out.println("nbNonFunctDimLevel: " + nbNonFunctDimLevel);
            System.out.println("nbFact: " + nbFact);
            System.out.println("nbFactAtt: " + nbFactAtt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}