package de.tum.bgu.msm.models.realEstate;

import com.pb.common.util.IndexSort;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Build new dwellings based on current demand. Model works in two steps. At the end of each simulation period,
 * the demand for new housing is calculated and stored. During the following simulation period, demand is realized
 * step by step. This helps simulating the time lag between demand for housing and actual completion of new dwellings.
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 4 December 2012 in Santa Fe
 **/

public class ConstructionModel extends AbstractModel {

    static Logger logger = Logger.getLogger(ConstructionModel.class);

    private final GeoData geoData;

    private final ConstructionLocationJSCalculator constructionLocationJSCalculator;
    private float betaForZoneChoice;
    private float priceIncreaseForNewDwelling;
    private ArrayList<Integer[]> plannedDwellings;
    public static int[] listOfPlannedConstructions;
    private boolean makeSomeNewDdAffordable;
    private float shareOfAffordableDd;
    private float restrictionForAffordableDd;

    private ConstructionDemandJSCalculator constructionDemandCalculator;


    public ConstructionModel(SiloDataContainer dataContainer) {
        super(dataContainer);
        this.geoData = dataContainer.getGeoData();
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("ConstructionLocationCalc"));
        constructionLocationJSCalculator = new ConstructionLocationJSCalculator(reader);
        setupConstructionModel();
        setupEvaluationOfZones();
    }

    private void setupConstructionModel() {
        Reader reader;
        if(Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("ConstructionDemandCalcMuc"));
        } else{
            reader = new InputStreamReader(this.getClass().getResourceAsStream("ConstructionDemandCalcMstm"));
        }
        constructionDemandCalculator = new ConstructionDemandJSCalculator(reader);

        makeSomeNewDdAffordable = Properties.get().realEstate.makeSOmeNewDdAffordable;
        if (makeSomeNewDdAffordable) {
            shareOfAffordableDd = Properties.get().realEstate.affordableDwellingsShare;
            restrictionForAffordableDd = Properties.get().realEstate.levelOfAffordability;
        }
    }


    private void setupEvaluationOfZones() {
        // set up model to evaluate zones for construction of new dwellings
        betaForZoneChoice = Properties.get().realEstate.constructionLogModelBeta;
        priceIncreaseForNewDwelling = Properties.get().realEstate.constructionLogModelInflator;
    }


    public void planNewDwellingsForThisComingYear(int year, SiloModelContainer modelContainer, SiloDataContainer dataContainer) {
        // plan new dwellings based on demand and available land (not immediately realized, as construction needs some time)

        dataContainer.getHouseholdData().calculateMedianHouseholdIncomeByMSA(dataContainer.getGeoData());  // needs to be calculate even if no dwellings are added this year: median income is needed in housing search in MovesModelMstm.searchForNewDwelling (int hhId)
        dataContainer.getRealEstateData().calculateRegionWidePriceAndVacancyByDwellingType();
        if (!EventRules.ruleBuildDwelling()) return;
        logger.info("  Planning dwellings to be constructed from " + year + " to " + (year + 1));

        // calculate demand by region
        double[][] vacancyByRegion = dataContainer.getRealEstateData().getVacancyRateByTypeAndRegion();
        double[][] demandByRegion = new double[DwellingType.values().length][SiloUtil.getHighestVal(geoData.getRegionIdsArray()) + 1];
        float[][] avePriceByTypeAndZone = calculateScaledAveragePriceByZone(100);
        float[][] avePriceByTypeAndRegion = calculateScaledAveragePriceByRegion(100);
        float[][] aveSizeByTypeAndRegion = calculateAverageSizeByTypeAndByRegion();
        for (DwellingType dt: DwellingType.values()) {
            int dto = dt.ordinal();
            for (int region: geoData.getRegionIdsArray()) {
                demandByRegion[dto][region] = constructionDemandCalculator.calculateConstructionDemand(vacancyByRegion[dto][region], dt);
            }
        }
        // try to satisfy demand, build more housing in zones with particularly low vacancy rates, if available land use permits
        int[][] existingDwellings = dataContainer.getRealEstateData().getDwellingCountByTypeAndRegion();
        DwellingType[] dtOrder = findOrderOfDwellingTypes(dataContainer);
        plannedDwellings = new ArrayList<>();
        for (DwellingType dt : dtOrder) {
            int dto = dt.ordinal();
            float acresNeededForOneDwelling = dataContainer.getRealEstateData().getAcresNeededForOneDwelling(dt);
            for (int region : geoData.getRegionIdsArray()) {
                int demand = (int) (existingDwellings[dto][region] * demandByRegion[dto][region] + 0.5);
                if (demand == 0) {
                    continue;
                }
                int[] zonesInThisRegion = geoData.getRegions().get(region).getZones().stream().mapToInt(Zone::getId).toArray();
                double[] util = new double[SiloUtil.getHighestVal(zonesInThisRegion) + 1];
                for (int zone : zonesInThisRegion) {
                    float avePrice = avePriceByTypeAndZone[dto][zone];
                    if (avePrice == 0) avePrice = avePriceByTypeAndRegion[dto][region];
                    if (avePrice == 0)
                        logger.error("Ave. price is 0. Replaced with region-wide average price for this dwelling type.");
                    // evaluate utility for building DwellingType dt where the average price of this dwelling type in this zone is avePrice
                    util[zone] = constructionLocationJSCalculator.calculateConstructionProbability(dt, avePrice, modelContainer.getAcc().getAutoAccessibilityForZone(zone));
                }
                double[] prob = new double[SiloUtil.getHighestVal(zonesInThisRegion) + 1];
                // walk through every dwelling to be built
                for (int i = 1; i <= demand; i++) {
                    double probSum = 0;
                    for (int zone : zonesInThisRegion) {
                        boolean useDwellingsAsCapacity = dataContainer.getRealEstateData().useDwellingCapacityForThisZone(zone);
                        double availableLand = dataContainer.getRealEstateData().getAvailableLandForConstruction(zone);
                        if ((useDwellingsAsCapacity && availableLand == 0) ||                              // capacity by dwellings is use
                                (!useDwellingsAsCapacity && availableLand < acresNeededForOneDwelling) ||  // not enough land available?
                                !geoData.isThisDwellingTypeAllowed(dt.toString(), zone)) {                 // construction of this dwelling type allowed in this zone?
                            prob[zone] = 0.;
                        } else {
                            prob[zone] = betaForZoneChoice * availableLand * util[zone];
                            probSum += prob[zone];
                        }
                    }
                    if (probSum == 0) continue;
                    for (int zone : zonesInThisRegion) {
                        prob[zone] = prob[zone] / probSum;
                    }
                    int zone = SiloUtil.select(prob);
                    Integer[] attributes = new Integer[6];
                    attributes[0] = zone;
                    attributes[1] = dto;
                    attributes[2] = (int) (aveSizeByTypeAndRegion[dto][region] + 0.5);
                    attributes[3] = Properties.get().main.qualityLevels;  // set all new dwellings to highest quality level
                    attributes[4] = 0;  // set restriction for new dwellings to unrestricted by default
                    if (makeSomeNewDdAffordable) {
                        if (SiloUtil.getRandomNumberAsFloat() <= shareOfAffordableDd)
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
                        int msa = geoData.getZones().get(zone).getMsa();
                        attributes[5] = (int) (Math.abs((attributes[4] / 100f)) * HouseholdDataManager.getMedianIncome(msa) / 12 * 0.18 + 0.5);
                    }

                    plannedDwellings.add(attributes);
                    dataContainer.getRealEstateData().convertLand(zone, acresNeededForOneDwelling);
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
        for (Dwelling dd : dataContainer.getRealEstateData().getDwellings()) {
            int dt = dd.getType().ordinal();
            int zone = geoData.getZones().get(dd.getZone()).getId();
            counter[dt][zone]++;
            avePrice[dt][zone] += dd.getPrice();
        }
        for (DwellingType dt : DwellingType.values()) {
            int dto = dt.ordinal();
            float[] avePriceThisType = new float[geoData.getHighestZonalId() + 1];
            for (int zone : geoData.getZoneIdsArray()) {
                if (counter[dto][zone] > 0) {
                    avePriceThisType[zone] = avePrice[dto][zone] / counter[dto][zone];
                } else {
                    avePriceThisType[zone] = 0;
                }
            }
            float[] scaledAvePriceThisDwellingType = SiloUtil.scaleArray(avePriceThisType, scaler);
            for (int zones : geoData.getZoneIdsArray()) {
                avePrice[dto][zones] = scaledAvePriceThisDwellingType[zones];
            }
        }
        return avePrice;
    }


    private float[][] calculateScaledAveragePriceByRegion(float scaler) {
        // calculate scaled average housing price by dwelling type and region

        float[][] avePrice = new float[DwellingType.values().length][SiloUtil.getHighestVal(geoData.getRegionIdsArray()) + 1];
        int[][] counter = new int[DwellingType.values().length][SiloUtil.getHighestVal(geoData.getRegionIdsArray()) + 1];
        for (Dwelling dd : dataContainer.getRealEstateData().getDwellings()) {
            int dt = dd.getType().ordinal();
            int region = geoData.getZones().get(dd.getZone()).getRegion().getId();
            counter[dt][region]++;
            avePrice[dt][region] += dd.getPrice();
        }
        for (DwellingType dt : DwellingType.values()) {
            int dto = dt.ordinal();
            float[] avePriceThisType = new float[SiloUtil.getHighestVal(geoData.getRegionIdsArray()) + 1];
            for (int region : geoData.getRegionIdsArray()) {
                if (counter[dto][region] > 0) {
                    avePriceThisType[region] = avePrice[dto][region] / counter[dto][region];
                } else {
                    avePriceThisType[region] = 0;
                }
            }
            float[] scaledAvePriceThisDwellingType = SiloUtil.scaleArray(avePriceThisType, scaler);
            for (int region : geoData.getRegionIdsArray()) {
                avePrice[dto][region] = scaledAvePriceThisDwellingType[region];
            }
        }
        return avePrice;
    }


    private float[][] calculateAverageSizeByTypeAndByRegion() {
        // calculate average housing size by dwelling type and region

        float[][] aveSize = new float[DwellingType.values().length][SiloUtil.getHighestVal(geoData.getRegionIdsArray()) + 1];
        int[][] counter = new int[DwellingType.values().length][SiloUtil.getHighestVal(geoData.getRegionIdsArray()) + 1];
        for (Dwelling dd : dataContainer.getRealEstateData().getDwellings()) {
            int dt = dd.getType().ordinal();
            int region = geoData.getZones().get(dd.getZone()).getRegion().getId();
            counter[dt][region]++;
            aveSize[dt][region] += dd.getBedrooms();
        }
        for (DwellingType dt : DwellingType.values()) {
            int dto = dt.ordinal();
            for (int region : geoData.getRegions().keySet()) {
                if (counter[dto][region] > 0) {
                    aveSize[dto][region] = aveSize[dto][region] / counter[dto][region];
                } else {
                    aveSize[dto][region] = 0;
                }
            }
        }
        // catch if one region should not have a given dwelling type (should almost never happen, but theoretically possible)
        float[] totalAveSizeByType = new float[DwellingType.values().length];
        for (DwellingType dt : DwellingType.values()) {
            int dto = dt.ordinal();
            int validRegions = 0;
            for (int region : geoData.getRegions().keySet()) {
                if (aveSize[dto][region] > 0) {
                    totalAveSizeByType[dto] += aveSize[dto][region];
                    validRegions++;
                }
            }
            totalAveSizeByType[dto] = totalAveSizeByType[dto] / validRegions;
        }
        for (DwellingType dt: DwellingType.values()) {
            int dto = dt.ordinal();
            for (int region: geoData.getRegions().keySet()) {
                if (aveSize[dto][region] == 0) aveSize[dto][region] = totalAveSizeByType[dto];
            }
        }
        return aveSize;
    }


    private DwellingType[] findOrderOfDwellingTypes (SiloDataContainer dataContainer) {
        // define order of dwelling types based on their average price. More expensive types are built first.

        double[] prices = dataContainer.getRealEstateData().getAveragePriceByDwellingType();
        int[] scaledPrices = new int[prices.length];
        for (int i = 0; i < prices.length; i++) {
            if (prices[i] * 10000 > Integer.MAX_VALUE)
                logger.error("Average housing price for " + DwellingType.values()[i] +
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


    public void buildDwelling(int id, int year, SiloModelContainer modelContainer, SiloDataContainer dataContainer) {
        // realize dwelling project id

        Integer[] attributes = plannedDwellings.get(id);
        int ddId = RealEstateDataManager.getNextDwellingId();
        int zoneId = attributes[0];
        int dto = attributes[1];
        int size = attributes[2];
        int quality = attributes[3];
        float restriction = attributes[4] / 100f;
        int price = attributes[5];

        Dwelling dd = dataContainer.getRealEstateData().createDwelling(ddId, zoneId, -1, DwellingType.values()[dto], size, quality, price, restriction, year);
        double utils[] = modelContainer.getMove().updateUtilitiesOfVacantDwelling(dd);
        dd.setUtilitiesOfVacantDwelling(utils);
        dataContainer.getRealEstateData().addDwellingToVacancyList(dd);
        EventManager.countEvent(EventTypes.DD_CONSTRUCTION);

        if (ddId == SiloUtil.trackDd) {
            SiloUtil.trackWriter.println("Dwelling " + ddId + " was constructed with these properties: ");
            SiloUtil.trackWriter.println(dd.toString());
        }
    }
}
