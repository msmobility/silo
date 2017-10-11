package de.tum.bgu.msm.relocation;

/*
 * Implementation of the MovesModelI Interface for the Munich implementation
 * @author Rolf Moeckel
 * Date: 20 May 2017, near Greenland in an altitude of 35,000 feet
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

import javax.script.ScriptException;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ResourceBundle;

public class MovesModelMuc implements MovesModelI {
    private static Logger logger = Logger.getLogger(MovesModelMuc.class);
    static Logger traceLogger = Logger.getLogger("trace");
    private GeoData geoData;
    protected static final String PROPERTIES_MOVES_UEC_FILE                  = "HH.Moves.UEC.FileName";
    protected static final String PROPERTIES_MOVES_UEC_DATA_SHEET            = "HH.Moves.UEC.DataSheetNumber";
    protected static final String PROPERTIES_MOVES_UEC_MODEL_SHEET_DD_UTIL   = "HH.Moves.UEC.Dwelling.Utility";
    protected static final String PROPERTIES_MOVES_UEC_MODEL_SHEET_MOVEORNOT = "HH.Moves.UEC.ModelSheetNumber.moveOrNot";
    protected static final String PROPERTIES_LOG_UTILITY_CALCULATION_MOVES_D = "log.util.hhRelocation.dd";
    protected static final String PROPERTIES_LOG_UTILITY_CALCULATION_MOVES_R = "log.util.hhRelocation.rg";
    protected static final String PROPERTIES_MOVE_OR_NOT_BINOMIAL_LOG_MODEL  = "move.or.not.binomial.log.model.parameter";
    protected static final String PROPERTIES_MOVE_OR_NOT_BINOMIAL_LOG_SHIFT  = "move.or.not.binomial.log.shift.parameter";


    // properties
    private String uecFileName;
    private int dataSheetNumber;
    private boolean logCalculationDwelling;
    private ResourceBundle rb;
    private int numAltsMoveOrNot;
    private double[][][] utilityRegion;
    private double parameter_MoveOrNotSlope;
    private double parameter_MoveOrNotShift;
    int[] evalDwellingAvail;
    int numAltsEvalDwelling;
    private UtilityExpressionCalculator ddUtilityModel;
    private MovesDMU evaluateDwellingDmu;
    private double[] averageHousingSatisfaction;
    private float[] zonalShareForeigners;
    private float[] regionalShareForeigners;
    private int[] householdsByRegion;
    private SelectRegionJSCalculator regionCalculator;
    private SelectDwellingJSCalculator dwellingCalculator;


    public MovesModelMuc(ResourceBundle rb, GeoData geoData) {
        // constructor
        this.rb = rb;
        this.geoData = geoData;

        // read properties
        uecFileName     = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_MOVES_UEC_FILE);
        dataSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_MOVES_UEC_DATA_SHEET);
        logCalculationDwelling = ResourceUtil.getBooleanProperty(rb, PROPERTIES_LOG_UTILITY_CALCULATION_MOVES_D);
        evaluateDwellingDmu = new MovesDMU();

        setupEvaluateDwellings();
        setupMoveOrNotMove();
        setupSelectRegionModel();
        setupSelectDwellingModel();
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


    private void calculateShareOfForeignersByZoneAndRegion() {
        // Calculate share of foreigners by zone and region

        zonalShareForeigners = new float[geoData.getZones().length];
        regionalShareForeigners = new float[geoData.getRegionList().length];
        SiloUtil.setArrayToValue(zonalShareForeigners, 0f);
        for (Household hh: Household.getHouseholdArray()) {
            int region = geoData.getRegionOfZone(hh.getHomeZone());
            if (hh.getNationality() != Nationality.german) {
                zonalShareForeigners[geoData.getZoneIndex(hh.getHomeZone())]++;
                regionalShareForeigners[geoData.getRegionIndex(region)]++;
            }
        }
        int[] hhByZone = HouseholdDataManager.getNumberOfHouseholdsByZone(geoData);
        for (int zone: geoData.getZones()) {
            if (hhByZone[geoData.getZoneIndex(zone)] > 0) {
                zonalShareForeigners[geoData.getZoneIndex(zone)] =
                        zonalShareForeigners[geoData.getZoneIndex(zone)] / hhByZone[geoData.getZoneIndex(zone)];
            } else {
                zonalShareForeigners[geoData.getZoneIndex(zone)] = 0;  // should not be necessary, but implemented for safety
            }
        }
        int[] hhByRegion = HouseholdDataManager.getNumberOfHouseholdsByRegion(geoData);
        for (int region: geoData.getRegionList()) {
            if (hhByRegion[geoData.getRegionIndex(region)] > 0) {
                regionalShareForeigners[geoData.getRegionIndex(region)] =
                        regionalShareForeigners[geoData.getRegionIndex(region)] / hhByRegion[geoData.getRegionIndex(region)];
            } else {
                regionalShareForeigners[geoData.getRegionIndex(region)] = 0;  // should not be necessary, but implemented for safety
            }
        }
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
                double util = calculateUtility(hh.getHouseholdType(), dd, modelContainer);
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


    private double convertDistToWorkToUtil (Household hh, int homeZone) {
        // convert distance to work and school to utility
        double util = 1;
        for (Person p: hh.getPersons()) {
            if (p.getOccupation() == 1 && p.getWorkplace() != -2) {
                int workZone = Job.getJobFromId(p.getWorkplace()).getZone();
                int travelTime = (int) SiloUtil.rounder(Accessibility.getAutoTravelTime(homeZone, workZone),0);
                util = util * Accessibility.getWorkTLFD(travelTime);
            }
        }
        return util;
    }


    private double convertTravelCostsToUtility (Household hh, int homeZone) {
        // convert travel costs to utility
        double util = 1;
        float workTravelCostsGasoline = 0;
        for (Person p: hh.getPersons()) if (p.getOccupation() == 1 && p.getWorkplace() != -2) {
            int workZone = Job.getJobFromId(p.getWorkplace()).getZone();
            // yearly commute costs with 251 work days over 12 months, doubled to account for return trip
            workTravelCostsGasoline += Accessibility.getTravelCosts(homeZone, workZone) * 251f * 2f;
        }
        // todo: Create more plausible utilities
        // Assumptions: Transportation costs are 5.9-times higher than expenditures for gasoline (https://www.census.gov/compendia/statab/2012/tables/12s0688.xls)
        // Households spend 19% of their income on transportation, and 70% thereof is not
        // work-related (but HBS, HBO, NHB, etc. trips)
        float travelCosts = workTravelCostsGasoline * 5.9f + (hh.getHhIncome() * 0.19f * 0.7f);
        if (travelCosts > (hh.getHhIncome() * 0.19f)) util = 0.5;
        if (travelCosts > (hh.getHhIncome() * 0.25f)) util = 0.4;
        if (travelCosts > (hh.getHhIncome() * 0.40f)) util = 0.2;
        if (travelCosts > (hh.getHhIncome() * 0.50f)) util = 0.0;
        return util;
    }


    public double[] updateUtilitiesOfVacantDwelling (Dwelling dd, SiloModelContainer modelContainer) {
        // Calculate utility of this dwelling for each household type

        double[] utilByHhType = new double[HouseholdType.values().length];
        for (HouseholdType ht: HouseholdType.values()) {
            utilByHhType[ht.ordinal()] = calculateUtility(ht, dd, modelContainer);
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
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("SelectRegionCalc"));
        regionCalculator = new SelectRegionJSCalculator(reader, false);
    }


    public void calculateRegionalUtilities() {
        // everything is available

        int[] regions = geoData.getRegionList();
        calculateShareOfForeignersByZoneAndRegion();

        int highestRegion = SiloUtil.getHighestVal(regions);
        int[] regPrice = new int[highestRegion + 1];
        float[] regAcc = new float[highestRegion + 1];
        for (int region: regions) {
            regPrice[region] = calculateRegPrice(region);
            regAcc[region] = (float) convertAccessToUtility(Accessibility.getRegionalAccessibility(region));
        }

        utilityRegion = new double[SiloUtil.incBrackets.length + 1][Nationality.values().length][regions.length];
        for (int income = 1; income <= SiloUtil.incBrackets.length + 1; income++) {

            float[] priceUtil = new float[highestRegion + 1];

            for (int region: regions) {
                priceUtil[region] = (float) convertPriceToUtility(regPrice[region], income);
            }

            for (Nationality nationality: Nationality.values()) {
                for (int region: regions) {
                    regionCalculator.setIncomeGroup(income - 1);
                    regionCalculator.setNationality(nationality);
                    regionCalculator.setMedianPrice(priceUtil[region]);
                    regionCalculator.setForeignersShare(regionalShareForeigners[geoData.getRegionIndex(region)]);
                    regionCalculator.setAccessibility(regAcc[region]);
                    double utility = 0;
                    try {
                        utility = regionCalculator.calculate();
                        utilityRegion[income - 1][nationality.ordinal()][region-1] = utility;
                    } catch (ScriptException e) {
                        e.printStackTrace();
                    }
                }
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

        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("SelectDwellingCalc"));
        dwellingCalculator = new SelectDwellingJSCalculator(reader, false);
    }


    private double[] getRegionUtilities (HouseholdType ht, Race race, int[] workZones) {
        // return utility of regions based on household type and based on work location of workers in household

        int[] regions = geoData.getRegionList();
        double[] util = new double[regions.length];
        double[] workDistanceFactor = new double[regions.length];
        for (int i = 0; i < regions.length; i++) {
            workDistanceFactor[i] = 1;
            if (workZones != null) {  // for inmigrating household, work places are selected after household found a home
                for (int workZone : workZones) {
                    int smallestDistInMin = (int) Accessibility.getMinDistanceFromZoneToRegion(workZone, regions[i]);
                    workDistanceFactor[i] = workDistanceFactor[i] * Accessibility.getWorkTLFD(smallestDistInMin);
                }
            }
        }
        int incomeCat = HouseholdType.convertHouseholdTypeToIncomeCategory(ht);
        for (int i = 0; i < regions.length; i++) {
            util[i] = utilityRegion[incomeCat - 1][race.ordinal()][i] * workDistanceFactor[i];
        }
        return util;
    }


    public void chooseMove (int hhId, SiloModelContainer modelContainer, SiloDataContainer dataContainer) {
        // simulates (a) if this household moves and (b) where this household moves

        if (!EventRules.ruleHouseholdMove(Household.getHouseholdFromId(hhId))) return;  // Household does not exist anymore
        if (!moveOrNot(hhId)) return;                                         // Step 1: Consider relocation if household is not very satisfied or if household income exceed restriction for low-income dwelling
        Household hh = Household.getHouseholdFromId(hhId);
        int idNewDD = searchForNewDwelling(hh.getPersons(), modelContainer);  // Step 2: Choose new dwelling
        if (idNewDD > 0) {
            moveHousehold(hh, hh.getDwellingId(), idNewDD, dataContainer);    // Step 3: Move household
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


    public int searchForNewDwelling(Person[] persons, SiloModelContainer modelContainer) {
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
            if (pp.getRace() != householdRace) householdRace = Race.black; //changed this so race is a proxy of nationality
        }
        if (householdRace == Race.other){
            householdRace = Race.black;
        } else if (householdRace == Race.hispanic){
            householdRace = Race.black;
        }
        int incomeBracket = HouseholdDataManager.getIncomeCategoryForIncome(householdIncome);
        HouseholdType ht = HouseholdDataManager.defineHouseholdType(persons.length, incomeBracket);

        // Step 1: select region
        int[] regions = geoData.getRegionList();
        double[] regionUtilities = getRegionUtilities(ht, householdRace, workZones);
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
        double sumProbs = 0.;
        int maxNumberOfDwellings = Math.min(20, vacantDwellings.length);  // No household will evaluate more than 20 dwellings
        float factor = ((float) maxNumberOfDwellings / (float) vacantDwellings.length);
        for (int i = 0; i < vacantDwellings.length; i++) {
            if (SiloUtil.getRandomNumberAsFloat() > factor) continue;
            Dwelling dd = Dwelling.getDwellingFromId(vacantDwellings[i]);
            double util = calculateUtility(ht, dd, modelContainer);
            dwellingCalculator.setDwellingUtility(util);
            try {
                expProbs[i] = dwellingCalculator.calculate();
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            sumProbs =+ expProbs[i];
        }
        if (sumProbs == 0) return -1;    // could not find dwelling that fits restrictions
        int selected = SiloUtil.select(expProbs, sumProbs);
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


    private double calculateUtility (HouseholdType ht, Dwelling dd, SiloModelContainer modelContainer) {
        // calculate utility for household hh in dwelling dd

        evaluateDwellingDmu.setUtilityDwellingQuality(convertQualityToUtility(dd.getQuality()));
        evaluateDwellingDmu.setUtilityDwellingSize(convertAreaToUtility(dd.getBedrooms()));
        evaluateDwellingDmu.setUtilityDwellingAutoAccessibility(convertAccessToUtility(modelContainer.getAcc().getAutoAccessibility(dd.getZone())));
        evaluateDwellingDmu.setUtilityDwellingTransitAccessibility(convertAccessToUtility(modelContainer.getAcc().getTransitAccessibility(dd.getZone())));

        int price = dd.getPrice();
        evaluateDwellingDmu.setUtilityDwellingPrice(convertPriceToUtility(price, ht));
        evaluateDwellingDmu.setType(ht);
        double util[] = ddUtilityModel.solve(evaluateDwellingDmu.getDmuIndexValues(), evaluateDwellingDmu, evalDwellingAvail);
        // log UEC values for each household type
        if (logCalculationDwelling)
            ddUtilityModel.logAnswersArray(traceLogger, "Quality of dwelling " + dd.getId());
        return util[0];
    }

}
