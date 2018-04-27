package de.tum.bgu.msm.models.relocation;

import com.pb.common.calculator.UtilityExpressionCalculator;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractDefaultMovesModel extends AbstractModel implements MovesModelI {

    protected final static Logger LOGGER = Logger.getLogger(AbstractDefaultMovesModel.class);
    protected final static Logger traceLogger = Logger.getLogger("trace");

    protected final GeoData geoData;
    protected final Accessibility accessibility;

    protected String uecFileName;
    protected int dataSheetNumber;
    protected int numAltsMoveOrNot;
//    protected int[] evalDwellingAvail;
//    protected MovesDMU evaluateDwellingDmu;
    protected double[][][] utilityRegion;

    protected boolean logCalculationDwelling;
    protected boolean logCalculationRegion;

    private int numAltsEvalDwelling;
    private double parameter_MoveOrNotSlope;
    private double parameter_MoveOrNotShift;
    private double[] averageHousingSatisfaction;

//    protected UtilityExpressionCalculator ddUtilityModel;
    protected DwellingUtilityJSCalculator dwellingUtilityJSCalculator;

    public AbstractDefaultMovesModel(SiloDataContainer dataContainer, Accessibility accessibility) {
        super(dataContainer);
        this.geoData = dataContainer.getGeoData();
        this.accessibility = accessibility;
        uecFileName     = Properties.get().main.baseDirectory + Properties.get().moves.uecFileName;
        dataSheetNumber = Properties.get().moves.dataSheet;
//        logCalculationDwelling = Properties.get().moves.logHhRelocation;
        logCalculationRegion = Properties.get().moves.logHhRelocationRegion;
        //evaluateDwellingDmu = new MovesDMU();
        setupMoveOrNotMove();
        setupEvaluateDwellings();
        setupSelectRegionModel();
        setupSelectDwellingModel();
    }

    protected abstract void setupSelectRegionModel();

    protected abstract void setupSelectDwellingModel();

    protected abstract double calculateDwellingUtilityOfHousehold(HouseholdType hhType, int income, Dwelling dwelling);

    @Override
    public double[] updateUtilitiesOfVacantDwelling (Dwelling dd) {
        // Calculate utility of this dwelling for each household type

        double[] utilByHhType = new double[HouseholdType.values().length];
        for (HouseholdType ht: HouseholdType.values()) {
            utilByHhType[ht.ordinal()] = calculateDwellingUtilityOfHousehold(ht, -1, dd);
//            if (logCalculationDwelling) {
//                ddUtilityModel.logAnswersArray(traceLogger, "Quality of dwelling " + dd.getId());
//            }
        }
        return utilByHhType;
    }

    @Override
    public void chooseMove (int hhId) {
        // simulates (a) if this household moves and (b) where this household moves

        Household household = dataContainer.getHouseholdData().getHouseholdFromId(hhId);
        if (!EventRules.ruleHouseholdMove(household)) {
            return;  // Household does not exist anymore
        }
        if (!moveOrNot(household)) {
            return;                                                             // Step 1: Consider relocation if household is not very satisfied or if household income exceed restriction for low-income dwelling
        }

        int idNewDD = searchForNewDwelling(household.getPersons());  // Step 2: Choose new dwelling
        if (idNewDD > 0) {
            moveHousehold(household, household.getDwellingId(), idNewDD);    // Step 3: Move household
            EventManager.countEvent(EventTypes.HOUSEHOLD_MOVE);
            dataContainer.getHouseholdData().addHouseholdThatMoved(household);
            if (hhId == SiloUtil.trackHh) SiloUtil.trackWriter.println("Household " + hhId + " has moved to dwelling " +
                    household.getDwellingId());
        } else {
            if (hhId == SiloUtil.trackHh) SiloUtil.trackWriter.println("Household " + hhId + " intended to move but " +
                    "could not find an adequate dwelling.");
        }
    }


    private void setupEvaluateDwellings() {
//        int ddUtilityModelSheetNumber = Properties.get().moves.dwellingUtilSheet;
//        ddUtilityModel = new UtilityExpressionCalculator(new File(uecFileName),
//                ddUtilityModelSheetNumber,
//                dataSheetNumber,
//                SiloUtil.getRbHashMap(),
//                MovesDMU.class);
        //configure the JS calculator here here
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("DwellingUtilityCalc"));
        dwellingUtilityJSCalculator = new DwellingUtilityJSCalculator(reader);
    }

    private void setupMoveOrNotMove() {
        int moveOrNotModelSheetNumber = Properties.get().moves.moveOrNotSheet;
        UtilityExpressionCalculator moveOrNotModel = new UtilityExpressionCalculator(new File(uecFileName),
                moveOrNotModelSheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                MovesDMU.class);
        MovesDMU moveOrNotDmu = new MovesDMU();
        // everything is available
        numAltsMoveOrNot = moveOrNotModel.getNumberOfAlternatives();
        int[] moveOrNotAvail = new int[numAltsMoveOrNot + 1];
        for (int i = 1; i < moveOrNotAvail.length; i++) moveOrNotAvail[i] = 1;
        // set DMU attributes
        moveOrNotModel.solve(moveOrNotDmu.getDmuIndexValues(), moveOrNotDmu, moveOrNotAvail);
        // todo: looks wrong, parameter should be read from UEC file, not from properties file
        parameter_MoveOrNotSlope = Properties.get().moves.moveOrNotSlope;
        parameter_MoveOrNotShift = Properties.get().moves.moveOrNotShift;
        if (logCalculationDwelling) {
            moveOrNotModel.logAnswersArray(traceLogger, "Move-Or-Not Model");
        }
    }

    private void evaluateAllDwellingUtilities() {
        // walk through each dwelling and evaluate utility of current resident
        // also, calculate utility of vacant dwellings for all household types

        LOGGER.info("  Evaluating utility of dwellings for current residents and utility of vacant dwellings for all " +
                "household types");
        // everything is available
        //obtain the number of alternatives
//        numAltsEvalDwelling = ddUtilityModel.getNumberOfAlternatives();
//        evalDwellingAvail = new int[numAltsEvalDwelling + 1];
//        for (int i = 1; i < evalDwellingAvail.length; i++) {
//            evalDwellingAvail[i] = 1;
//        }
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        for (Dwelling dd : dataContainer.getRealEstateData().getDwellings()) {
            if (dd.getResidentId() == -1) {
                // dwelling is vacant, evaluate for all household types
                double utils[] = updateUtilitiesOfVacantDwelling(dd);
                dd.setUtilitiesOfVacantDwelling(utils);
            } else {
                // dwelling is occupied, evaluate for the current household
                Household hh = householdData.getHouseholdFromId(dd.getResidentId());
                double util = calculateDwellingUtilityOfHousehold(hh.getHouseholdType(), hh.getHhIncome(), dd);
                dd.setUtilOfResident(util);
                // log UEC values for each household
//                if (logCalculationDwelling)
//                    //return the alternatives
//                    ddUtilityModel.logAnswersArray(traceLogger, "Quality of dwelling " + dd.getId());
            }
        }
    }

    protected double convertPriceToUtility(int price, HouseholdType ht) {

        int incCategory = HouseholdType.convertHouseholdTypeToIncomeCategory(ht);
        float[] shares = RealEstateDataManager.getRentPaymentsForIncomeGroup(incCategory);
        int priceCategory = (int) (price / 200f + 0.5);   // 25 rent categories are defined as <rent/200>, see RealEstateDataManager
        priceCategory = Math.min(priceCategory, RealEstateDataManager.rentCategories);
        double util = 0;
        for (int i = 0; i <= priceCategory; i++) util += shares[i];
        return (1f - util);   // invert utility, as lower price has higher utility
    }

    protected double convertPriceToUtility(int price, int incCategory) {

        float[] shares = RealEstateDataManager.getRentPaymentsForIncomeGroup(incCategory);
        int priceCategory = (int) (price / 200f);   // 25 rent categories are defined as <rent/200>, see RealEstateDataManager
        priceCategory = Math.min(priceCategory, RealEstateDataManager.rentCategories);
        double util = 0;
        for (int i = 0; i <= priceCategory; i++) util += shares[i];
        return (1f - util);   // invert utility, as lower price has higher utility
    }

    protected double convertQualityToUtility(int quality) {
        return (float) quality / (float) Properties.get().main.qualityLevels;
    }

    protected double convertAreaToUtility(int area) {
        return (float) area / (float) RealEstateDataManager.largestNoBedrooms;
    }


    protected int calculateRegPrice(int region) {
        // calculate the average price across all dwelling types

        int priceSum = 0;
        int counter = 0;
        for (Dwelling d : dataContainer.getRealEstateData().getDwellings()) {

            if (geoData.getZones().get(d.getZone()).getRegion().getId() == region) {
                priceSum += d.getPrice();
                counter++;
            }
        }
        return (int) ((priceSum * 1f) / (counter * 1f) + 0.5f);
    }


    protected double convertAccessToUtility(double accessibility) {
        return accessibility / 100f;
    }

    protected Map<Integer, Double> calculateRegionalPrices() {
        final Map<Integer, Zone> zones = geoData.getZones();
        final Map<Integer, List<Dwelling>> dwellingsByRegion =
                dataContainer.getRealEstateData().getDwellings().parallelStream().collect(Collectors.groupingByConcurrent(d ->
                        zones.get(d.getZone()).getRegion().getId()));
        final Map<Integer, Double> rentsByRegion = dwellingsByRegion.entrySet().parallelStream().collect(Collectors.toMap(e ->
                e.getKey(), e-> e.getValue().stream().mapToDouble(d -> d.getPrice()).average().getAsDouble()));
        return rentsByRegion;
    }

    protected boolean moveOrNot(Household household) {

        HouseholdType hhType = household.getHouseholdType();
        Dwelling dd = dataContainer.getRealEstateData().getDwelling(household.getDwellingId());
        if (!isHouseholdEligibleToLiveHere(household, dd)) {
            return true;
        }
        double currentUtil = dd.getUtilOfResident();

        double[] prop = new double[2];
        prop[0] = 1. - 1. / (1. + parameter_MoveOrNotShift *
                Math.exp(parameter_MoveOrNotSlope * (averageHousingSatisfaction[hhType.ordinal()] - currentUtil)));
        prop[1] = 1. - prop[0];
        return SiloUtil.select(prop) == 0;
    }

    private boolean isHouseholdEligibleToLiveHere(Household hh, Dwelling dd) {
        // Check if dwelling is restricted, if so check if household is still eligible to live in this dwelling (household income could exceed eligibility criterion)
        if (dd.getRestriction() <= 0) {
            return true;   // Dwelling is not income restricted
        }
        int msa = geoData.getZones().get(dd.getZone()).getMsa();
        return hh.getHhIncome() <= (HouseholdDataManager.getMedianIncome(msa) * dd.getRestriction());
    }

    @Override
    public void calculateAverageHousingSatisfaction() {
        evaluateAllDwellingUtilities();
        averageHousingSatisfaction = new double[HouseholdType.values().length];
        int[] hhCountyByType = new int[HouseholdType.values().length];
        for (Household hh : dataContainer.getHouseholdData().getHouseholds()) {
            double util = dataContainer.getRealEstateData().getDwelling(hh.getDwellingId()).getUtilOfResident();
            int count = hh.getHouseholdType().ordinal();
            averageHousingSatisfaction[count] += util;
            hhCountyByType[count]++;
        }
        for (int hhType = 0; hhType < HouseholdType.values().length; hhType++) {
            averageHousingSatisfaction[hhType] = averageHousingSatisfaction[hhType] / (1. * hhCountyByType[hhType]);
        }
    }

    @Override
    public void moveHousehold(Household hh, int idOldDD, int idNewDD) {
        // if this household had a dwelling in this study area before, vacate old dwelling
        if (idOldDD > 0) {
            Dwelling dd = dataContainer.getRealEstateData().getDwelling(idOldDD);
            dd.setResidentID(-1);
            dataContainer.getRealEstateData().addDwellingToVacancyList(dd);
        }
        dataContainer.getRealEstateData().removeDwellingFromVacancyList(idNewDD);
        hh.setDwelling(idNewDD);
        dataContainer.getRealEstateData().getDwelling(idNewDD).setResidentID(hh.getId());
        if (hh.getId() == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("Household " +
                    hh.getId() + " moved from dwelling " + idOldDD + " to dwelling " + idNewDD + ".");
        }
    }
}
