package edu.umd.ncsg.realEstate;

import com.pb.common.util.IndexSort;
import edu.umd.ncsg.SiloModel;
import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.data.*;
import edu.umd.ncsg.events.EventManager;
import edu.umd.ncsg.events.EventRules;
import edu.umd.ncsg.events.EventTypes;
import edu.umd.ncsg.relocation.MovesModel;
import org.apache.log4j.Logger;
import com.pb.common.util.ResourceUtil;
import com.pb.common.calculator.UtilityExpressionCalculator;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.io.File;

/**
 * Build new dwellings based on current demand. Model works in two steps. At the end of each simulation period,
 * the demand for new housing is calculated and stored. During the following simulation period, demand is realized
 * step by step. This helps simulating the time lag between demand for housing and actual completion of new dwellings.
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 4 December 2012 in Santa Fe
 **/

public class ConstructionModel {

    static Logger logger = Logger.getLogger(ConstructionModel.class);
    static Logger traceLogger = Logger.getLogger("trace");

    protected static final String PROPERTIES_RealEstate_UEC_FILE                      = "RealEstate.UEC.FileName";
    protected static final String PROPERTIES_RealEstate_UEC_DATA_SHEET                = "RealEstate.UEC.DataSheetNumber";
    protected static final String PROPERTIES_RealEstate_UEC_MODEL_SHEET_CONSTDEMAND   = "RealEstate.UEC.ModelSheetNumber.ConsDemand";
    protected static final String PROPERTIES_RealEstate_UEC_MODEL_SHEET_CONSTLOCATION = "RealEstate.UEC.ModelSheetNumber.ConsLocatn";
    protected static final String PROPERTIES_LOG_UTILITY_CALCULATION_CONSTRUCTION     = "log.util.ddConstruction";
    protected static final String PROPERTIES_RealEstate_ZONE_UTILITY_BETA             = "construct.dwelling.mn.log.model.beta";
    protected static final String PROPERTIES_RealEstate_ZONE_UTILITY_INFLATOR         = "construct.dwelling.mn.log.model.inflator";
    protected static final String PROPERTIES_FLAG_TO_MAKE_NEW_DD_AFFORDABLE           = "make.new.dwellings.partly.affordable";
    protected static final String PROPERTIES_SHARE_OF_DD_TO_BE_MADE_AFFORDABLE        = "share.of.affordable.dwellings";
    protected static final String PROPERTIES_RESTRICTION_SETTING_FOR_AFFORDABLE_DD    = "level.of.affordability.setting";


    private ResourceBundle rb;
    private boolean logCalculation;
    private String uecFileName;
    private int dataSheetNumber;
    private ConstructionDMU evaluateZoneDmu;
    private UtilityExpressionCalculator zoneUtilityModel;
    private float[] sizeAdjustment;
    private float[] shapeAdjustment;
    private float betaForZoneChoice;
    private float priceIncreaseForNewDwelling;
    private ArrayList<Integer[]> plannedDwellings;
    public static int[] listOfPlannedConstructions;
    private boolean makeSomeNewDdAffordable;
    private float shareOfAffordableDd;
    private float restrictionForAffordableDd;


    public ConstructionModel (ResourceBundle rb) {

        this.rb = rb;
        // read properties
        uecFileName     = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_RealEstate_UEC_FILE);
        dataSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_RealEstate_UEC_DATA_SHEET);
        logCalculation  = ResourceUtil.getBooleanProperty(rb, PROPERTIES_LOG_UTILITY_CALCULATION_CONSTRUCTION);
        evaluateZoneDmu = new ConstructionDMU();
        setupConstructionModel();
        setupEvaluationOfZones();
    }


    private void setupConstructionModel() {

        // Construction Demand
        int constructionModelSheetNumberA = ResourceUtil.getIntegerProperty(rb, PROPERTIES_RealEstate_UEC_MODEL_SHEET_CONSTDEMAND);
        UtilityExpressionCalculator constructionModelDemand = new UtilityExpressionCalculator(new File(uecFileName),
                constructionModelSheetNumberA,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                ConstructionDMU.class);
        ConstructionDMU constructionDemandDmu = new ConstructionDMU();

        int[] availability = {1, 1};
        sizeAdjustment = new float[DwellingType.values().length];
        shapeAdjustment = new float[DwellingType.values().length];
        for (DwellingType dwellingTp: DwellingType.values()) {
            int dtType = dwellingTp.ordinal();
            constructionDemandDmu.setType(dwellingTp);
            constructionDemandDmu.setToken(1);
            double parOne[] = constructionModelDemand.solve(constructionDemandDmu.getDmuIndexValues(), constructionDemandDmu, availability);
            sizeAdjustment[dtType] = (float) parOne[0];
            constructionDemandDmu.setToken(2);
            double parTwo[] = constructionModelDemand.solve(constructionDemandDmu.getDmuIndexValues(), constructionDemandDmu, availability);
            shapeAdjustment[dtType] = (float) parTwo[0];
        }
        makeSomeNewDdAffordable = ResourceUtil.getBooleanProperty(rb, PROPERTIES_FLAG_TO_MAKE_NEW_DD_AFFORDABLE, false);
        if (makeSomeNewDdAffordable) {
            shareOfAffordableDd = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_SHARE_OF_DD_TO_BE_MADE_AFFORDABLE);
            restrictionForAffordableDd = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_RESTRICTION_SETTING_FOR_AFFORDABLE_DD);
        }
    }


    private void setupEvaluationOfZones() {
        // set up model to evaluate zones for construction of new dwellings

        int zoneUtilityModelSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_RealEstate_UEC_MODEL_SHEET_CONSTLOCATION);
        // initialize UEC
        zoneUtilityModel = new UtilityExpressionCalculator(new File(uecFileName),
                zoneUtilityModelSheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                ConstructionDMU.class);
        betaForZoneChoice = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_RealEstate_ZONE_UTILITY_BETA);
        priceIncreaseForNewDwelling = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_RealEstate_ZONE_UTILITY_INFLATOR);
    }


    public void planNewDwellingsForThisComingYear(int year, RealEstateDataManager realEstateData) {
        // plan new dwellings based on demand and available land (not immediately realized, as construction needs some time)

        HouseholdDataManager.calculateMedianHouseholdIncomeByMSA();  // needs to be calculate even if no dwellings are added this year: median income is needed in housing search in MovesModel.searchForNewDwelling (int hhId)
        realEstateData.calculateRegionWidePriceAndVacancyByDwellingType();
        if (!EventRules.ruleBuildDwelling()) return;
        logger.info("  Planning dwellings to be constructed from " + year + " to " + (year + 1));

        // calculate demand by region
        double[][] vacancyByRegion = realEstateData.getVacancyRateByTypeAndRegion();
        double[][] demandByRegion = new double[DwellingType.values().length][SiloUtil.getHighestVal(geoData.getRegionList()) + 1];
        float[][] avePriceByTypeAndZone = calculateScaledAveragePriceByZone(100);
        float[][] avePriceByTypeAndRegion = calculateScaledAveragePriceByRegion(100);
        float[][] aveSizeByTypeAndRegion = calculateAverageSizeByTypeAndByRegion();
        for (DwellingType dt: DwellingType.values()) {
            int dto = dt.ordinal();
            for (int region: geoData.getRegionList()) {
                demandByRegion[dto][region] = sizeAdjustment[dto] / Math.exp(shapeAdjustment[dto] * vacancyByRegion[dto][region]);
            }
        }

        // try to satisfy demand, build more housing in zones with particularly low vacancy rates, if available land use permits
        int[][] existingDwellings = realEstateData.getDwellingCountByTypeAndRegion();
        DwellingType[] dtOrder = findOrderOfDwellingTypes(realEstateData);
        plannedDwellings = new ArrayList<>();
        for (DwellingType dt: dtOrder) {
            int dto = dt.ordinal();
            float acresNeededForOneDwelling = realEstateData.getAcresNeededForOneDwelling(dt);
            for (int region: geoData.getRegionList()) {
                int demand = (int) (existingDwellings[dto][region] * demandByRegion[dto][region] + 0.5);
                if (demand == 0) continue;
                int[] zonesInThisRegion = geoData.getZonesInRegion(region);
                double[] util = new double[SiloUtil.getHighestVal(zonesInThisRegion) + 1];
                for (int zone: zonesInThisRegion) {
                    float avePrice = avePriceByTypeAndZone[dto][zone];
                    if (avePrice == 0) avePrice = avePriceByTypeAndRegion[dto][region];
                    if (avePrice == 0) logger.error("Ave. price is 0. Replaced with region-wide average price for this dwelling type.");
                    util[zone] = getUtilityOfDwellingTypeInZone(dt, avePrice, Accessibility.getAutoAccessibility(zone));
                }
                double[] prob = new double[SiloUtil.getHighestVal(zonesInThisRegion) + 1];
                // walk through every dwelling to be built
                for (int i = 1; i <= demand; i++) {
                    double probSum = 0;
                    for (int zone: zonesInThisRegion) {
                        boolean useDwellingsAsCapacity = realEstateData.useDwellingCapacityForThisZone(zone);
                        double availableLand = realEstateData.getAvailableLandForConstruction(zone);
                        if ((useDwellingsAsCapacity && availableLand == 0) ||                              // capacity by dwellings is use
                                (!useDwellingsAsCapacity && availableLand < acresNeededForOneDwelling) ||  // not enough land available?
                                !realEstateData.getWhetherConstructionIsPermitted(dt, zone)) {             // construction of this dwelling type allowed in this zone?
                            prob[zone] = 0.;
                        } else {
                            prob[zone] = betaForZoneChoice * availableLand * util[zone];
                            probSum += prob[zone];
                        }
                    }
                    if (probSum == 0) continue;
                    for (int zone: zonesInThisRegion) {
                        prob[zone] = prob[zone] / probSum;
                    }
                    int zone = SiloUtil.select(prob);
                    Integer[] attributes = new Integer[6];
                    attributes[0] = zone;
                    attributes[1] = dto;
                    attributes[2] = (int) (aveSizeByTypeAndRegion[dto][region] + 0.5);
                    attributes[3] = SiloUtil.numberOfQualityLevels;  // set all new dwellings to highest quality level
                    attributes[4] = 0;  // set restriction for new dwellings to unrestricted by default
                    if (makeSomeNewDdAffordable) {
                        if (SiloModel.rand.nextFloat() <= shareOfAffordableDd)
                             attributes[4] = (int) (restrictionForAffordableDd * 100);
                    }
                    if (attributes[4] == 0) {
                        // dwelling is unrestricted, generate free-market price
                        float avePrice = avePriceByTypeAndZone[dto][zone];
                        if (avePrice == 0) avePrice = avePriceByTypeAndRegion[dto][region];
                        if (avePrice == 0)
                            logger.error("Ave. price is 0. Replace with region-wide average price for this dwelling type.");
                        attributes[5] = (int) (priceIncreaseForNewDwelling * avePrice + 0.5);
                    } else {
                        // rent-controlled, multiply restriction (usually 0.3, 0.5 or 0.8) with median income with 30% housing budget
                        // correction: in the PUMS data set, households with the about-median income of 58,000 pay 18% of their income in rent...
                        int msa = geoData.getMSAOfZone(zone);
                        attributes[5] = (int) (Math.abs((attributes[4] / 100f)) * HouseholdDataManager.getMedianIncome(msa) / 12 * 0.18 + 0.5);
                    }

                    plannedDwellings.add(attributes);
                    realEstateData.convertLand(zone, acresNeededForOneDwelling);
                }
            }
        }
        listOfPlannedConstructions = new int[plannedDwellings.size()];
        for (int i = 0; i < listOfPlannedConstructions.length; i++) listOfPlannedConstructions[i] = i;
    }


    private float[][] calculateScaledAveragePriceByZone(float scaler) {
        // calculate scaled average housing price by dwelling type and zone

        float[][] avePrice = new float[DwellingType.values().length][geoData.getHighestZonalId() + 1];
        int[][] counter = new int[DwellingType.values().length][geoData.getHighestZonalId() + 1];
        for (Dwelling dd: Dwelling.getDwellingArray()) {
            int dt = dd.getType().ordinal();
            int zone = dd.getZone();
            counter[dt][zone]++;
            avePrice[dt][zone] += dd.getPrice();
        }
        for (DwellingType dt: DwellingType.values()) {
            int dto = dt.ordinal();
            float[] avePriceThisType = new float[geoData.getHighestZonalId() + 1];
            for (int zone: geoData.getZones()) {
                if (counter[dto][zone] > 0) {
                    avePriceThisType[zone] = avePrice[dto][zone] / counter[dto][zone];
                } else {
                    avePriceThisType[zone] = 0;
                }
            }
            float[] scaledAvePriceThisDwellingType = SiloUtil.scaleArray(avePriceThisType, scaler);
            for (int zones: geoData.getZones()) {
                avePrice[dto][zones] = scaledAvePriceThisDwellingType[zones];
            }
        }
        return avePrice;
    }


    private float[][] calculateScaledAveragePriceByRegion(float scaler) {
        // calculate scaled average housing price by dwelling type and region

        float[][] avePrice = new float[DwellingType.values().length][SiloUtil.getHighestVal(geoData.getRegionList()) + 1];
        int[][] counter = new int[DwellingType.values().length][SiloUtil.getHighestVal(geoData.getRegionList()) + 1];
        for (Dwelling dd: Dwelling.getDwellingArray()) {
            int dt = dd.getType().ordinal();
            int region = geoData.getRegionOfZone(dd.getZone());
            counter[dt][region]++;
            avePrice[dt][region] += dd.getPrice();
        }
        for (DwellingType dt: DwellingType.values()) {
            int dto = dt.ordinal();
            float[] avePriceThisType = new float[SiloUtil.getHighestVal(geoData.getRegionList()) + 1];
            for (int region: geoData.getRegionList()) {
                if (counter[dto][region] > 0) {
                    avePriceThisType[region] = avePrice[dto][region] / counter[dto][region];
                } else {
                    avePriceThisType[region] = 0;
                }
            }
            float[] scaledAvePriceThisDwellingType = SiloUtil.scaleArray(avePriceThisType, scaler);
            for (int region: geoData.getRegionList()) {
                avePrice[dto][region] = scaledAvePriceThisDwellingType[region];
            }
        }
        return avePrice;
    }


    private float[][] calculateAverageSizeByTypeAndByRegion() {
        // calculate average housing size by dwelling type and region

        float[][] aveSize = new float[DwellingType.values().length][SiloUtil.getHighestVal(geoData.getRegionList()) + 1];
        int[][] counter = new int[DwellingType.values().length][SiloUtil.getHighestVal(geoData.getRegionList()) + 1];
        for (Dwelling dd: Dwelling.getDwellingArray()) {
            int dt = dd.getType().ordinal();
            int region = geoData.getRegionOfZone(dd.getZone());
            counter[dt][region]++;
            aveSize[dt][region] += dd.getBedrooms();
        }
        for (DwellingType dt: DwellingType.values()) {
            int dto = dt.ordinal();
            for (int region: geoData.getRegionList()) {
                if (counter[dto][region] > 0) {
                    aveSize[dto][region] = aveSize[dto][region] / counter[dto][region];
                } else {
                    aveSize[dto][region] = 0;
                }
            }
        }
        // catch if one region should not have a given dwelling type (should almost never happen, but theoretically possible)
        float[] totalAveSizeByType = new float[DwellingType.values().length];
        for (DwellingType dt: DwellingType.values()) {
            int dto = dt.ordinal();
            int validRegions = 0;
            for (int region: geoData.getRegionList()) {
                if (aveSize[dto][region] > 0) {
                    totalAveSizeByType[dto] += aveSize[dto][region];
                    validRegions++;
                }
            }
            totalAveSizeByType[dto] = totalAveSizeByType[dto] / validRegions;
        }
        for (DwellingType dt: DwellingType.values()) {
            int dto = dt.ordinal();
            for (int region: geoData.getRegionList()) {
                if (aveSize[dto][region] == 0) aveSize[dto][region] = totalAveSizeByType[dto];
            }
        }
        return aveSize;
    }


    private DwellingType[] findOrderOfDwellingTypes (RealEstateDataManager realEstateData) {
        // define order of dwelling types based on their average price. More expensive types are built first.

        double[] prices = realEstateData.getAveragePriceByDwellingType();
        int[] scaledPrices = new int[prices.length];
        for (int i = 0; i < prices.length; i++) {
            if (prices[i] * 10000 > Integer.MAX_VALUE) logger.error("Average housing price for " + DwellingType.values()[i] +
                    " with " + prices[i] + " is too large to be sorted. Adjust code.");
            scaledPrices[i] = (int) prices[i] * 10000;
        }
        int[] sortedPrices = IndexSort.indexSort(scaledPrices);
        DwellingType[] sortedDwellingTypes = new DwellingType[prices.length];
        for (int i = 0; i < prices.length; i++) {
            sortedDwellingTypes[prices.length - i - 1] = DwellingType.values()[sortedPrices[i]];
        }
        return sortedDwellingTypes;
    }


    private double getUtilityOfDwellingTypeInZone(DwellingType dt, float avePrice, double accessibility) {
        // evaluate utility for building DwellingType dt where the average price of this dwelling type in this zone is avePrice

        int numAltsEvalZones = zoneUtilityModel.getNumberOfAlternatives();
        int[] evaluationAvail = new int[numAltsEvalZones +1];
        for (int i=1; i < evaluationAvail.length; i++) evaluationAvail[i] = 1;
        // set DMU attributes
        evaluateZoneDmu.setType(dt);
        evaluateZoneDmu.setAvePrice(avePrice);
        evaluateZoneDmu.setZonalAccessibility((float) accessibility);
        double util[] = zoneUtilityModel.solve(evaluateZoneDmu.getDmuIndexValues(), evaluateZoneDmu, evaluationAvail);
        // log UEC values
        if (logCalculation)
            zoneUtilityModel.logAnswersArray(traceLogger, "Quality of zone for construction of " + dt);
        return util[0];
    }


    public void buildDwelling (int id, MovesModel move, int year) {
        // realize dwelling project id

        Integer[] attributes = plannedDwellings.get(id);
        int ddId = RealEstateDataManager.getNextDwellingId();
        int zone = attributes[0];
        int dto = attributes[1];
        int size = attributes[2];
        int quality = attributes[3];
        float restriction = attributes[4] / 100f;
        int price = attributes[5];

        Dwelling dd = new Dwelling(ddId, zone, -1, DwellingType.values()[dto], size, quality, price, restriction, year);
        double utils[] = move.updateUtilitiesOfVacantDwelling(dd);
        dd.setUtilitiesOfVacantDwelling(utils);
        RealEstateDataManager.addDwellingToVacancyList(dd);
        EventManager.countEvent(EventTypes.ddConstruction);

        if (ddId == SiloUtil.trackDd) {
            SiloUtil.trackWriter.println("Dwelling " + ddId + " was constructed with these properties: ");
            dd.logAttributes(SiloUtil.trackWriter);
        }

    }
}
