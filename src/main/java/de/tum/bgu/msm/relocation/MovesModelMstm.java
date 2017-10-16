package de.tum.bgu.msm.relocation;

/*
* @author Rolf Moeckel (PB Albuquerque)
* Created on Apr 4, 2011 in Albuquerque, NM
* Revised on Apr 24, 2014 in College Park, MD
*/

import com.pb.common.calculator.UtilityExpressionCalculator;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.events.EventTypes;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ResourceBundle;

public class MovesModelMstm implements MovesModelI {
    private static Logger logger = Logger.getLogger(MovesModelMstm.class);
    static Logger traceLogger = Logger.getLogger("trace");
    private GeoData geoData;
    protected static final String PROPERTIES_MOVES_UEC_FILE                  = "HH.Moves.UEC.FileName";
    protected static final String PROPERTIES_MOVES_UEC_DATA_SHEET            = "HH.Moves.UEC.DataSheetNumber";
    protected static final String PROPERTIES_MOVES_UEC_MODEL_SHEET_DD_UTIL   = "HH.Moves.UEC.Dwelling.Utility";
    protected static final String PROPERTIES_MOVES_UEC_MODEL_SHEET_MOVEORNOT = "HH.Moves.UEC.ModelSheetNumber.moveOrNot";
    protected static final String PROPERTIES_MOVES_UEC_MODEL_SHEET_REGION    = "HH.Moves.UEC.ModelSheetNumber.selectRegion";
    protected static final String PROPERTIES_MOVES_UEC_MODEL_SHEET_DWELLING  = "HH.Moves.UEC.ModelSheetNumber.selDwelling";
    protected static final String PROPERTIES_LOG_UTILITY_CALCULATION_MOVES_D = "log.util.hhRelocation.dd";
    protected static final String PROPERTIES_LOG_UTILITY_CALCULATION_MOVES_R = "log.util.hhRelocation.rg";
    protected static final String PROPERTIES_MOVE_OR_NOT_BINOMIAL_LOG_MODEL  = "move.or.not.binomial.log.model.parameter";
    protected static final String PROPERTIES_MOVE_OR_NOT_BINOMIAL_LOG_SHIFT  = "move.or.not.binomial.log.shift.parameter";
    protected static final String PROPERTIES_SELECT_DWELLING_MN_LOG_MODEL    = "select.dwelling.mn.log.model.parameter";
    protected static final String PROPERTIES_SELECT_DWELLING_RACE_FACTOR     = "relevance.of.race.in.zone.of.dwelling";
    protected static final String PROPERTIES_RUN_SCENARIO_HOUSING_SUBSIDY    = "provide.housing.subsidy.to.low.inc";

    // properties
    private String uecFileName;
    private int dataSheetNumber;
    private boolean logCalculationDwelling;
    private boolean logCalculationRegion;
    private ResourceBundle rb;
    private int numAltsMoveOrNot;
    private int numAltsSelReg;
    private double[][][] utilityRegion;
    private double parameter_MoveOrNotSlope;
    private double parameter_MoveOrNotShift;
    private double parameter_SelectDD;
    int[] evalDwellingAvail;
    int numAltsEvalDwelling;
    private UtilityExpressionCalculator ddUtilityModel;
    private UtilityExpressionCalculator selectRegionModel;
    private MovesDMU evaluateDwellingDmu;
    private MovesDMU selectRegionDmu;
    private double[] averageHousingSatisfaction;
    private float[][] zonalRacialComposition;
    private float[][] regionalRacialComposition;
    private double selectDwellingRaceRelevance;
    private boolean provideRentSubsidyToLowIncomeHh;
    private int[] householdsByRegion;


    public MovesModelMstm(ResourceBundle rb, GeoData geoData) {
        // constructor
        this.rb = rb;
        this.geoData = geoData;

        // read properties
        uecFileName     = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_MOVES_UEC_FILE);
        dataSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_MOVES_UEC_DATA_SHEET);
        logCalculationDwelling = ResourceUtil.getBooleanProperty(rb, PROPERTIES_LOG_UTILITY_CALCULATION_MOVES_D);
        logCalculationRegion = ResourceUtil.getBooleanProperty(rb, PROPERTIES_LOG_UTILITY_CALCULATION_MOVES_R);
        selectDwellingRaceRelevance = ResourceUtil.getDoubleProperty(rb, PROPERTIES_SELECT_DWELLING_RACE_FACTOR);
        evaluateDwellingDmu = new MovesDMU();

        setupEvaluateDwellings();
        setupMoveOrNotMove();
        setupSelectRegionModel();
        setupSelectDwellingModel();
        provideRentSubsidyToLowIncomeHh = ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SCENARIO_HOUSING_SUBSIDY, false);
        if (provideRentSubsidyToLowIncomeHh) RealEstateDataManager.calculateMedianRentByMSA();
    }


    private void setupEvaluateDwellings() {
        // set up model to evaluate dwellings

        int ddUtilityModelSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_MOVES_UEC_MODEL_SHEET_DD_UTIL);
        // initialize UEC
        ddUtilityModel = new UtilityExpressionCalculator(new File(uecFileName),
                ddUtilityModelSheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                MovesDMU.class);
    }


    public void calculateAverageHousingSatisfaction (SiloModelContainer modelContainer) {
        // calculate average satisfaction with dwelling (utility) for every household type

        evaluateAllDwellingUtilities(modelContainer);
        averageHousingSatisfaction = new double[HouseholdType.values().length];
        int[] hhCountyByType = new int[HouseholdType.values().length];
        for (Household hh: Household.getHouseholdArray()) {
            double util = Dwelling.getDwellingFromId(hh.getDwellingId()).getUtilOfResident();
            int count = hh.getHouseholdType().ordinal();
            averageHousingSatisfaction[count] += util;
            hhCountyByType[count]++;
        }
        for (int hhType = 0; hhType < HouseholdType.values().length; hhType++)
            averageHousingSatisfaction[hhType] = averageHousingSatisfaction[hhType] / (1. * hhCountyByType[hhType]);
    }


    private void calculateRacialCompositionByZoneAndRegion() {
        // Calculate share of races by zone and region

        zonalRacialComposition = new float[geoData.getZones().length][4];
        regionalRacialComposition = new float[geoData.getRegionList().length][4];
        SiloUtil.setArrayToValue(zonalRacialComposition, 0f);
        for (Household hh: Household.getHouseholdArray()) {
            zonalRacialComposition[geoData.getZoneIndex(hh.getHomeZone())][hh.getRace().ordinal()]++;
            int region = geoData.getRegionOfZone(hh.getHomeZone());
            regionalRacialComposition[geoData.getRegionIndex(region)][hh.getRace().ordinal()]++;
        }
        for (int zone: geoData.getZones()) {
            int zonalSum = 0;
            for (int raceType = 0; raceType < zonalRacialComposition[0].length; raceType++) {
                zonalSum += zonalRacialComposition[geoData.getZoneIndex(zone)][raceType];
            }
            if (zonalSum > 0) {
                for (int raceType = 0; raceType < zonalRacialComposition[0].length; raceType++) {
                    zonalRacialComposition[geoData.getZoneIndex(zone)][raceType] /= zonalSum;
                }
            }
        }
        for (int region: geoData.getRegionList()) {
            int regSum = 0;
            for (int raceType = 0; raceType < regionalRacialComposition[0].length; raceType++) {
                regSum += regionalRacialComposition[geoData.getRegionIndex(region)][raceType];
            }
            if (regSum > 0) {
                for (int raceType = 0; raceType < zonalRacialComposition[0].length; raceType++) {
                    regionalRacialComposition[geoData.getRegionIndex(region)][raceType] /= regSum;
                }
            }
        }
    }


    public float getZonalRacialShare(int zone, Race race) {
        return zonalRacialComposition[geoData.getZoneIndex(zone)][race.ordinal()];
    }


    private void evaluateAllDwellingUtilities (SiloModelContainer modelContainer) {
        // walk through each dwelling and evaluate utility of current resident
        // also, calculate utility of vacant dwellings for all household types

        logger.info("  Evaluating utility of dwellings for current residents and utility of vacant dwellings for all " +
                "household types");
        // everything is available
        numAltsEvalDwelling = ddUtilityModel.getNumberOfAlternatives();
        evalDwellingAvail = new int[numAltsEvalDwelling + 1];
        for (int i = 1; i < evalDwellingAvail.length; i++) evalDwellingAvail[i] = 1;
        for (Dwelling dd: Dwelling.getDwellingArray()) {
            if (dd.getResidentId() == -1) {
                // dwelling is vacant, evaluate for all household types
                double utils[] = updateUtilitiesOfVacantDwelling(dd, modelContainer);
                dd.setUtilitiesOfVacantDwelling(utils);
            } else {
                // dwelling is occupied, evaluate for the current household
                Household hh = Household.getHouseholdFromId(dd.getResidentId());
                double util = calculateUtility(hh.getHouseholdType(), hh.getHhIncome(), dd, modelContainer);
                dd.setUtilOfResident(util);
                // log UEC values for each household
                if (logCalculationDwelling)
                    ddUtilityModel.logAnswersArray(traceLogger, "Quality of dwelling " + dd.getId());
            }
        }
    }


    private double convertPriceToUtility (int price, HouseholdType ht) {
        // convert price into utility

        int incCategory = HouseholdType.convertHouseholdTypeToIncomeCategory(ht);
        float[] shares = RealEstateDataManager.getRentPaymentsForIncomeGroup(incCategory);
        int priceCategory = (int) (price / 200f + 0.5);   // 25 rent categories are defined as <rent/200>, see RealEstateDataManager
        priceCategory = Math.min(priceCategory, RealEstateDataManager.rentCategories);
        double util = 0;
        for (int i = 0; i <= priceCategory; i++) util += shares[i];
        return (1f - util);   // invert utility, as lower price has higher utility
    }


    private double convertPriceToUtility (int price, int incCategory) {
        // convert price into utility

        float[] shares = RealEstateDataManager.getRentPaymentsForIncomeGroup(incCategory);
        int priceCategory = (int) (price / 200f);   // 25 rent categories are defined as <rent/200>, see RealEstateDataManager
        priceCategory = Math.min(priceCategory, RealEstateDataManager.rentCategories);
        double util = 0;
        for (int i = 0; i <= priceCategory; i++) util += shares[i];
        return (1f - util);   // invert utility, as lower price has higher utility
    }


    private double convertQualityToUtility (int quality) {
        // convert quality levels 1 through 4 into utility
        return (float) quality / (float) SiloUtil.numberOfQualityLevels;
    }


    private double convertAreaToUtility (int area) {
        // convert area into utility
        return (float) area / (float) RealEstateDataManager.largestNoBedrooms;
    }


    private double convertAccessToUtility (double accessibility) {
        // convert accessibility into utility
        return accessibility / 100f;
    }


//    private double convertDistToWorkToUtil (Household hh, int homeZone) {
//        // convert distance to work and school to utility
//        double util = 1;
//        for (Person p: hh.getPersons()) {
//            if (p.getOccupation() == 1 && p.getWorkplace() != -2) {
//                int workZone = Job.getJobFromId(p.getWorkplace()).getZone();
//                int travelTime = (int) SiloUtil.rounder(siloModelContainer.getAcc().getAutoTravelTime(homeZone, workZone),0);
//                util = util * siloModelContainer.getAcc().getWorkTLFD(travelTime);
//            }
//        }
//        return util;
//    }


//    private double convertTravelCostsToUtility (Household hh, int homeZone) {
//        // convert travel costs to utility
//        double util = 1;
//        float workTravelCostsGasoline = 0;
//        for (Person p: hh.getPersons()) if (p.getOccupation() == 1 && p.getWorkplace() != -2) {
//            int workZone = Job.getJobFromId(p.getWorkplace()).getZone();
//            // yearly commute costs with 251 work days over 12 months, doubled to account for return trip
//            workTravelCostsGasoline += siloModelContainer.getAcc().getTravelCosts(homeZone, workZone) * 251f * 2f;
//        }
//        // todo: Create more plausible utilities
//        // Assumptions: Transportation costs are 5.9-times higher than expenditures for gasoline (https://www.census.gov/compendia/statab/2012/tables/12s0688.xls)
//        // Households spend 19% of their income on transportation, and 70% thereof is not
//        // work-related (but HBS, HBO, NHB, etc. trips)
//        float travelCosts = workTravelCostsGasoline * 5.9f + (hh.getHhIncome() * 0.19f * 0.7f);
//        if (travelCosts > (hh.getHhIncome() * 0.19f)) util = 0.5;
//        if (travelCosts > (hh.getHhIncome() * 0.25f)) util = 0.4;
//        if (travelCosts > (hh.getHhIncome() * 0.40f)) util = 0.2;
//        if (travelCosts > (hh.getHhIncome() * 0.50f)) util = 0.0;
//        return util;
//    }


    public double[] updateUtilitiesOfVacantDwelling (Dwelling dd, SiloModelContainer modelContainer) {
        // Calculate utility of this dwelling for each household type

        double[] utilByHhType = new double[HouseholdType.values().length];
        for (HouseholdType ht: HouseholdType.values()) {
            utilByHhType[ht.ordinal()] = calculateUtility(ht, -1, dd, modelContainer);
            // log UEC values for each household type
            if (logCalculationDwelling) ddUtilityModel.logAnswersArray(traceLogger, "Quality of dwelling " + dd.getId());
        }
        return utilByHhType;
    }


    private void setupMoveOrNotMove() {
        // set up model for choice move or stay

        int moveOrNotModelSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_MOVES_UEC_MODEL_SHEET_MOVEORNOT);
        // initialize UEC
        UtilityExpressionCalculator moveOrNotModel = new UtilityExpressionCalculator(new File(uecFileName),
                moveOrNotModelSheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                MovesDMU.class);
        MovesDMU moveOrNotDmu = new MovesDMU();
        // everything is available
        numAltsMoveOrNot = moveOrNotModel.getNumberOfAlternatives();
        int[] moveOrNotAvail = new int[numAltsMoveOrNot +1];
        for (int i=1; i < moveOrNotAvail.length; i++) moveOrNotAvail[i] = 1;
        // set DMU attributes
        moveOrNotModel.solve(moveOrNotDmu.getDmuIndexValues(), moveOrNotDmu, moveOrNotAvail);
        // todo: looks wrong, parameter should be read from UEC file, not from properties file
        parameter_MoveOrNotSlope = ResourceUtil.getDoubleProperty(rb, PROPERTIES_MOVE_OR_NOT_BINOMIAL_LOG_MODEL);
        parameter_MoveOrNotShift = ResourceUtil.getDoubleProperty(rb, PROPERTIES_MOVE_OR_NOT_BINOMIAL_LOG_SHIFT);
        if (logCalculationDwelling) {
            // log UEC values for each household type
            moveOrNotModel.logAnswersArray(traceLogger, "Move-Or-Not Model");
        }
    }


    private void setupSelectRegionModel() {
        // set up model for selection of region

        int selRegModelSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_MOVES_UEC_MODEL_SHEET_REGION);
        // initialize UEC
        selectRegionModel = new UtilityExpressionCalculator(new File(uecFileName),
                selRegModelSheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                MovesDMU.class);
        selectRegionDmu = new MovesDMU();
        numAltsSelReg = selectRegionModel.getNumberOfAlternatives();
    }


    public void calculateRegionalUtilities(SiloModelContainer siloModelContainer) {
        // everything is available

        calculateRacialCompositionByZoneAndRegion();
        int[] selRegAvail = new int[numAltsSelReg + 1];
        for (int i = 1; i < selRegAvail.length; i++) selRegAvail[i] = 1;

        int[] regions = geoData.getRegionList();
        int highestRegion = SiloUtil.getHighestVal(regions);
        int[] regPrice = new int[highestRegion + 1];
        float[] regAcc = new float[highestRegion + 1];
        float[] regSchQu = new float[highestRegion + 1];
        float[] regCrime = new float[highestRegion + 1];
        for (int region: regions) {
            regPrice[region] = calculateRegPrice(region);
            regAcc[region] = (float) convertAccessToUtility(siloModelContainer.getAcc().getRegionalAccessibility(region));
            regSchQu[region] = geoDataMstm.getRegionalSchoolQuality(region);
            regCrime[region] = 1f - geoDataMstm.getRegionalCrimeRate(region);  // invert utility, as lower crime rate has higher utility
        }
        selectRegionDmu.setRegionalAccessibility(regAcc);
        selectRegionDmu.setRegionalSchoolQuality(regSchQu);
        selectRegionDmu.setRegionalCrimeRate(regCrime);
        for (Race race: Race.values()) {
            float[] regionalRacialShare = new float[highestRegion + 1];
            for (int region: regions) regionalRacialShare[region] = regionalRacialComposition[geoData.getRegionIndex(region)][race.ordinal()];
            selectRegionDmu.setRegionalRace(race, regionalRacialShare);
        }
        utilityRegion = new double[SiloUtil.incBrackets.length + 1][Race.values().length][numAltsSelReg];
        for (int income = 1; income <= SiloUtil.incBrackets.length + 1; income++) {
            // set DMU attributes
            float[] priceUtil = new float[highestRegion + 1];
            for (int region: regions) priceUtil[region] = (float) convertPriceToUtility(regPrice[region], income);
            selectRegionDmu.setMedianRegionPrice(priceUtil);
            selectRegionDmu.setIncomeGroup(income - 1);
            for (Race race: Race.values()) {
                selectRegionDmu.setRace(race);
                double util[] = selectRegionModel.solve(selectRegionDmu.getDmuIndexValues(), selectRegionDmu, selRegAvail);
                for (int alternative = 0; alternative < numAltsSelReg; alternative++)
                    utilityRegion[income - 1][race.ordinal()][alternative] = util[alternative];
                // log UEC values for each household type
                if (logCalculationRegion)
                    selectRegionModel.logAnswersArray(traceLogger, "Select-Region Model for HH of income group " +
                            income + " with race " + race);
            }
        }
        householdsByRegion = HouseholdDataManager.getNumberOfHouseholdsByRegion(geoData);
    }


    private int calculateRegPrice(int region) {
        // calculate the average price across all dwelling types

        int priceSum = 0;
        int counter = 0;
        for (Dwelling d: Dwelling.getDwellingArray()) {
            int zone = d.getZone();
            if (geoData.getRegionOfZone(zone) == region) {
                priceSum += d.getPrice();
                counter++;
            }
        }
        return (int) ((priceSum * 1f) / (counter * 1f) + 0.5f);
    }


    private void setupSelectDwellingModel() {
        // set up model for choice of dwelling

        int selectDwellingSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_MOVES_UEC_MODEL_SHEET_DWELLING);
        // initialize UEC
        UtilityExpressionCalculator selectDwellingModel = new UtilityExpressionCalculator(new File(uecFileName),
                selectDwellingSheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                MovesDMU.class);
        MovesDMU selectDwellingDmu = new MovesDMU();
        // everything is available
        numAltsMoveOrNot = selectDwellingModel.getNumberOfAlternatives();
        int[] selectDwellingAvail = new int[numAltsMoveOrNot + 1];
        for (int i = 1; i < selectDwellingAvail.length; i++) selectDwellingAvail[i] = 1;
        // set DMU attributes
        selectDwellingModel.solve(selectDwellingDmu.getDmuIndexValues(), selectDwellingDmu, selectDwellingAvail);
        // todo: looks wrong, parameter should be read from UEC file, not from properties file
        parameter_SelectDD = ResourceUtil.getDoubleProperty(rb, PROPERTIES_SELECT_DWELLING_MN_LOG_MODEL);
        if (logCalculationDwelling) {
            // log UEC values for each household type
            selectDwellingModel.logAnswersArray(traceLogger, "Select-Dwelling Model");
        }
    }


    private double[] getRegionUtilities (HouseholdType ht, Race race, int[] workZones, SiloModelContainer siloModelContainer) {
        // return utility of regions based on household type and based on work location of workers in household

        int[] regions = geoData.getRegionList();
        double[] util = new double[numAltsSelReg];
        double[] workDistanceFactor = new double[numAltsSelReg];
        for (int i = 0; i < numAltsSelReg; i++) {
            workDistanceFactor[i] = 1;
            if (workZones != null) {  // for inmigrating household, work places are selected after household found a home
                for (int workZone : workZones) {
                    int smallestDistInMin = (int) siloModelContainer.getAcc().getMinDistanceFromZoneToRegion(workZone, regions[i]);
                    workDistanceFactor[i] = workDistanceFactor[i] * siloModelContainer.getAcc().getWorkTLFD(smallestDistInMin);
                }
            }
        }
        int incomeCat = HouseholdType.convertHouseholdTypeToIncomeCategory(ht);
        for (int i = 0; i < numAltsSelReg; i++) {
            util[i] = utilityRegion[incomeCat - 1][race.ordinal()][i] * workDistanceFactor[i];
        }
        return util;
    }


    public void chooseMove (int hhId, SiloModelContainer modelContainer, SiloDataContainer dataContainer) {
        // simulates (a) if this household moves and (b) where this household moves

        if (!EventRules.ruleHouseholdMove(Household.getHouseholdFromId(hhId))) return;  // Household does not exist anymore
        if (!moveOrNot(hhId)) return;                            // Step 1: Consider relocation if household is not very satisfied or if household income exceed restriction for low-income dwelling
        Household hh = Household.getHouseholdFromId(hhId);
        int idNewDD = searchForNewDwelling(hh.getPersons(), modelContainer);     // Step 2: Choose new dwelling
        if (idNewDD > 0) {
            moveHousehold(hh, hh.getDwellingId(), idNewDD, dataContainer);      // Step 3: Move household
            EventManager.countEvent(EventTypes.householdMove);
            if (hhId == SiloUtil.trackHh) SiloUtil.trackWriter.println("Household " + hhId + " has moved to dwelling " +
                    Household.getHouseholdFromId(hhId).getDwellingId());
        } else {
            if (hhId == SiloUtil.trackHh) SiloUtil.trackWriter.println("Household " + hhId + " intended to move but " +
                    "could not find an adequate dwelling.");
        }
    }


    private boolean moveOrNot (int hhId) {
        // select whether this household considers relocating or not

        Household hh = Household.getHouseholdFromId(hhId);
        HouseholdType hhType = hh.getHouseholdType();
        Dwelling dd = Dwelling.getDwellingFromId(hh.getDwellingId());
        if (!isHouseholdEligibleToLiveHere(hh, dd)) return true;
        double currentUtil = dd.getUtilOfResident();

        double[] prop = new double[2];
        prop[0] = 1. - 1. / (1. + parameter_MoveOrNotShift *
                Math.exp(parameter_MoveOrNotSlope * (averageHousingSatisfaction[hhType.ordinal()] - currentUtil)));
        prop[1] = 1. - prop[0];
        return SiloUtil.select(prop) == 0;
    }


    private boolean isHouseholdEligibleToLiveHere(Household hh, Dwelling dd) {
        // Check if dwelling is restricted, if so check if household is still eligible to live in this dwelling (household income could exceed eligibility criterion)
        if (dd.getRestriction() <= 0) return true;   // Dwelling is not income restricted
        int msa = geoDataMstm.getMSAOfZone(dd.getZone());
        return hh.getHhIncome() <= (HouseholdDataManager.getMedianIncome(msa) * dd.getRestriction());
    }


    public int searchForNewDwelling(Person[] persons, SiloModelContainer siloModelContainer) {
        // search alternative dwellings

        // data preparation
        int wrkCount = 0;
        for (Person pp: persons) if (pp.getOccupation() == 1 && pp.getWorkplace() != -2) wrkCount++;
        int pos = 0;
        int householdIncome = 0;
        int[] workZones = new int[wrkCount];
        Race householdRace = persons[0].getRace();
        for (Person pp: persons) if (pp.getOccupation() == 1 && pp.getWorkplace() != -2) {
            workZones[pos] = Job.getJobFromId(pp.getWorkplace()).getZone();
            pos++;
            householdIncome += pp.getIncome();
            if (pp.getRace() != householdRace) householdRace = Race.other;
        }
        int incomeBracket = HouseholdDataManager.getIncomeCategoryForIncome(householdIncome);
        HouseholdType ht = HouseholdDataManager.defineHouseholdType(persons.length, incomeBracket);

        // Step 1: select region
        int[] regions = geoData.getRegionList();
        double[] regionUtilities = getRegionUtilities(ht, householdRace, workZones, siloModelContainer);
        // todo: adjust probabilities to make that households tend to move shorter distances (dist to work is already represented)
        String normalizer = "population";
        int totalVacantDd = 0;
        for (int region: geoData.getRegionList()) totalVacantDd += RealEstateDataManager.getNumberOfVacantDDinRegion(region);
        for (int i = 0; i < regionUtilities.length; i++) {
            switch (normalizer) {
                case ("vacDd"): {
                    // Multiply utility of every region by number of vacant dwellings to steer households towards available dwellings
                    // use number of vacant dwellings to calculate attractivity of region
                    regionUtilities[i] = regionUtilities[i] * (float) RealEstateDataManager.getNumberOfVacantDDinRegion(regions[i]);
                } case ("shareVacDd"): {
                    // use share of empty dwellings to calculate attractivity of region
                    regionUtilities[i] = regionUtilities[i] * ((float) RealEstateDataManager.getNumberOfVacantDDinRegion(regions[i]) / (float) totalVacantDd);
                } case ("dampenedVacRate"): {
                    double x = (double) RealEstateDataManager.getNumberOfVacantDDinRegion(regions[i]) /
                            (double) RealEstateDataManager.getNumberOfDDinRegion(regions[i]) * 100d;  // % vacancy
                    double y = 1.4186E-03 * Math.pow(x, 3) - 6.7846E-02 * Math.pow(x, 2) + 1.0292 * x + 4.5485E-03;
                    y = Math.min(5d, y);                                                // % vacancy assumed to be ready to move in
                    regionUtilities[i] = regionUtilities[i] * (y / 100d * RealEstateDataManager.getNumberOfDDinRegion(regions[i]));
                    if (RealEstateDataManager.getNumberOfVacantDDinRegion(regions[i]) < 1) regionUtilities[i] = 0d;
                } case ("population"): {
                    regionUtilities[i] = regionUtilities[i] * householdsByRegion[i];
                } case ("noNormalization"): {
                    // do nothing
                }
            }
        }
        if (SiloUtil.getSum(regionUtilities) == 0) return -1;
        int selectedRegion = SiloUtil.select(regionUtilities);

        // Step 2: select vacant dwelling in selected region
        int[] vacantDwellings = RealEstateDataManager.getListOfVacantDwellingsInRegion(regions[selectedRegion]);
        double[] expProbs = SiloUtil.createArrayWithValue(vacantDwellings.length, 0d);
        int maxNumberOfDwellings = Math.min(20, vacantDwellings.length);  // No household will evaluate more than 20 dwellings
        float factor = ((float) maxNumberOfDwellings / (float) vacantDwellings.length);
        for (int i = 0; i < vacantDwellings.length; i++) {
            if (SiloUtil.getRandomNumberAsFloat() > factor) continue;
            Dwelling dd = Dwelling.getDwellingFromId(vacantDwellings[i]);
            int msa = geoDataMstm.getMSAOfZone(dd.getZone());
            if (dd.getRestriction() > 0 &&    // dwelling is restricted to households with certain income
                    householdIncome > (HouseholdDataManager.getMedianIncome(msa) * dd.getRestriction())) continue;
            float racialShare = 1;
            if (householdRace != Race.other) {
                racialShare = getZonalRacialShare(dd.getZone(), householdRace);
            }
            // multiply by racial share to make zones with higher own racial share more attractive
            double adjProb;
            if (householdQualifiesForSubsidy(householdIncome, dd.getZone(), dd.getPrice())) {
                adjProb = Math.pow(calculateUtility(ht, householdIncome, dd, siloModelContainer), (1 - selectDwellingRaceRelevance)) *
                        Math.pow(racialShare, selectDwellingRaceRelevance);
            } else {
                adjProb = Math.pow(dd.getUtilByHhType()[ht.ordinal()], (1 - selectDwellingRaceRelevance)) *
                        Math.pow(racialShare, selectDwellingRaceRelevance);
            }
            expProbs[i] = Math.exp(parameter_SelectDD * adjProb);
        }
        if (SiloUtil.getSum(expProbs) == 0) return -1;    // could not find dwelling that fits restrictions
        int selected = SiloUtil.select(expProbs);
        return vacantDwellings[selected];
    }


    public void moveHousehold(Household hh, int idOldDD, int idNewDD, SiloDataContainer dataContainer) {
        // Move household hh from oldDD to newDD

        // if this household had a dwelling in this study area before, vacate old dwelling
        if (idOldDD > 0) {
            Dwelling dd = Dwelling.getDwellingFromId(idOldDD);
            dd.setResidentID(-1);
            dataContainer.getRealEstateData().addDwellingToVacancyList(dd);
        }
        dataContainer.getRealEstateData().removeDwellingFromVacancyList(idNewDD);
        hh.setDwelling(idNewDD);
        Dwelling.getDwellingFromId(idNewDD).setResidentID(hh.getId());
        if (hh.getId() == SiloUtil.trackHh) SiloUtil.trackWriter.println("Household " +
                hh.getId() + " moved from dwelling " + idOldDD + " to dwelling " + idNewDD + ".");

    }


    private double calculateUtility (HouseholdType ht, int income, Dwelling dd, SiloModelContainer modelContainer) {
        // calculate utility for household hh in dwelling dd

        evaluateDwellingDmu.setUtilityDwellingQuality(convertQualityToUtility(dd.getQuality()));
        evaluateDwellingDmu.setUtilityDwellingSize(convertAreaToUtility(dd.getBedrooms()));
        evaluateDwellingDmu.setUtilityDwellingAutoAccessibility(convertAccessToUtility(modelContainer.getAcc().getAutoAccessibility(dd.getZone())));
        evaluateDwellingDmu.setUtilityDwellingTransitAccessibility(convertAccessToUtility(modelContainer.getAcc().getTransitAccessibility(dd.getZone())));
        evaluateDwellingDmu.setUtilityDwellingSchoolQuality(geoDataMstm.getZonalSchoolQuality(dd.getZone()));
        evaluateDwellingDmu.setUtilityDwellingCrimeRate(geoDataMstm.getCountyCrimeRate(geoDataMstm.getCountyOfZone(dd.getZone())));

        int price = dd.getPrice();
        if (provideRentSubsidyToLowIncomeHh && income > 0) {     // income equals -1 if dwelling is vacant right now
            // housing subsidy program in place
            int msa = geoDataMstm.getMSAOfZone(dd.getZone());
            if (income < (0.5f * HouseholdDataManager.getMedianIncome(msa)) && price < (0.4f * income / 12f)) {
                float housingBudget = (income / 12f * 0.18f);  // technically, the housing budget is 30%, but in PUMS data households pay 18% on the average
                float subsidy = RealEstateDataManager.getMedianRent(msa) - housingBudget;
                price = Math.max(0, price - (int) (subsidy + 0.5));
            }
        }

        evaluateDwellingDmu.setUtilityDwellingPrice(convertPriceToUtility(price, ht));
        evaluateDwellingDmu.setType(ht);
        double util[] = ddUtilityModel.solve(evaluateDwellingDmu.getDmuIndexValues(), evaluateDwellingDmu, evalDwellingAvail);
        // log UEC values for each household type
        if (logCalculationDwelling)
            ddUtilityModel.logAnswersArray(traceLogger, "Quality of dwelling " + dd.getId());
        return util[0];
    }


    private boolean householdQualifiesForSubsidy(int income, int zone, int price) {
        // check if households qualifies for subsidy
        int assumedIncome = Math.max(income, 15000);  // households with less than that must receive some welfare
        return provideRentSubsidyToLowIncomeHh &&
                income <= (0.5f * HouseholdDataManager.getMedianIncome(geoDataMstm.getMSAOfZone(zone))) &&
                price <= (0.4f * assumedIncome);
    }
}
