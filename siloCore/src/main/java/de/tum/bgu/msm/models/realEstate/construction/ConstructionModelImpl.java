package de.tum.bgu.msm.models.realEstate.construction;

import com.pb.common.util.IndexSort;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.development.Development;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.events.impls.realEstate.ConstructionEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Build new dwellings based on current demand. Model works in two steps. At the end of each simulation period,
 * the demand for new housing is calculated and stored. During the following simulation period, demand is realized
 * step by step. This helps simulating the time lag between demand for housing and actual completion of new dwellings.
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 4 December 2012 in Santa Fe
 **/

public class ConstructionModelImpl extends AbstractModel implements ConstructionModel {

    private final static Logger LOGGER = Logger.getLogger(ConstructionModelImpl.class);

    private final GeoData geoData;
    private final DwellingFactory factory;
    private final ConstructionLocationStrategy locationStrategy;
    private final ConstructionDemandStrategy demandStrategy;
    private final Accessibility accessibility;

    private float betaForZoneChoice;
    private float priceIncreaseForNewDwelling;
    private boolean makeSomeNewDdAffordable;
    private float shareOfAffordableDd;
    private float restrictionForAffordableDd;

    private int currentYear = -1;

    public ConstructionModelImpl(DataContainer dataContainer, DwellingFactory factory,
                                 Properties properties, ConstructionLocationStrategy locationStrategy,
                                 ConstructionDemandStrategy demandStrategy) {
        super(dataContainer, properties);
        this.geoData = dataContainer.getGeoData();
        this.accessibility = dataContainer.getAccessibility();
        this.factory = factory;
        this.locationStrategy = locationStrategy;
        this.demandStrategy = demandStrategy;
    }

    @Override
    public void setup() {
//        Reader locationCalcReader = new InputStreamReader(this.getClass().getResourceAsStream("ConstructionLocationCalc"));
//        final Reader constructionDemandReader;
//        switch (properties.main.implementation) {
//            case MUNICH:
//                constructionDemandReader = new InputStreamReader(this.getClass().getResourceAsStream("ConstructionDemandCalc"));
//                break;
//            case MARYLAND:
//                constructionDemandReader = new InputStreamReader(this.getClass().getResourceAsStream("ConstructionDemandCalcMstm"));
//                break;
//            case PERTH:
//                constructionDemandReader = new InputStreamReader(this.getClass().getResourceAsStream("ConstructionDemandCalc"));
//                break;
//            case KAGAWA:
//            case CAPE_TOWN:
//            default:
//                throw new RuntimeException("ConstructionModel not applicable for " + properties.main.implementation);
//        }


        makeSomeNewDdAffordable = properties.realEstate.makeSomeNewDdAffordable;
        if (makeSomeNewDdAffordable) {
            shareOfAffordableDd = properties.realEstate.affordableDwellingsShare;
            restrictionForAffordableDd = properties.realEstate.levelOfAffordability;
        }

        // set up model to evaluate zones for construction of new dwellings
        betaForZoneChoice = properties.realEstate.constructionLogModelBeta;
        priceIncreaseForNewDwelling = properties.realEstate.constructionLogModelInflator;
    }

    @Override
    public void prepareYear(int year) {}

    @Override
    public Collection<ConstructionEvent> getEventsForCurrentYear(int year) {
        currentYear = year;
        List<ConstructionEvent> events = new ArrayList<>();

        // plan new dwellings based on demand and available land (not immediately realized, as construction needs some time)
        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        LOGGER.info("  Planning dwellings to be constructed from " + year + " to " + (year + 1));

        // calculate demand by region
        double[][] vacancyByRegion = realEstate.getVacancyRateByTypeAndRegion();

        List<DwellingType> dwellingTypes = realEstate.getDwellingTypes();
        double[][] demandByRegion = new double[dwellingTypes.size()][geoData.getRegions().keySet().stream().max(Comparator.naturalOrder()).get() + 1];
        float[][] avePriceByTypeAndZone = calculateScaledAveragePriceByZone(100);
        float[][] avePriceByTypeAndRegion = calculateScaledAveragePriceByRegion(100);
        float[][] aveSizeByTypeAndRegion = calculateAverageSizeByTypeAndByRegion();
        for (DwellingType dt : dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            for (int region : geoData.getRegions().keySet()) {
                demandByRegion[dto][region] = demandStrategy.calculateConstructionDemand(vacancyByRegion[dto][region], dt);
            }
        }
        // try to satisfy demand, build more housing in zones with particularly low vacancy rates, if available land use permits
        int[][] existingDwellings = realEstate.getDwellingCountByTypeAndRegion();
        DwellingType[] sortedDwellingTypes = findOrderOfDwellingTypes(dataContainer);
        for (DwellingType dt : sortedDwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            for (int region : geoData.getRegions().keySet()) {
                int demand = (int) (existingDwellings[dto][region] * demandByRegion[dto][region] + 0.5);
                if (demand == 0) {
                    continue;
                }
                int[] zonesInThisRegion = geoData.getRegions().get(region).getZones().stream().mapToInt(Zone::getZoneId).toArray();
                double[] util = new double[SiloUtil.getHighestVal(zonesInThisRegion) + 1];
                for (int zone : zonesInThisRegion) {
                    float avePrice = avePriceByTypeAndZone[dto][zone];
                    if (avePrice == 0) avePrice = avePriceByTypeAndRegion[dto][region];
                    if (avePrice == 0)
                        LOGGER.error("Ave. price is 0. Replaced with region-wide average price for this dwelling type.");
                    // evaluate utility for building DwellingType dt where the average price of this dwelling type in this zone is avePrice
                    util[zone] = locationStrategy.calculateConstructionProbability(dt, avePrice, accessibility.getAutoAccessibilityForZone(zone));
                }
                double[] prob = new double[SiloUtil.getHighestVal(zonesInThisRegion) + 1];
                // walk through every dwelling to be built
                for (int i = 1; i <= demand; i++) {
                    double probSum = 0;
                    for (int zone : zonesInThisRegion) {
                        Development development = dataContainer.getGeoData().getZones().get(zone).getDevelopment();
                        boolean useDwellingsAsCapacity = development.isUseDwellingCapacity();
                        double availableLand = realEstate.getAvailableCapacityForConstruction(zone);
                        if ((useDwellingsAsCapacity && availableLand == 0) ||                              // capacity by dwellings is use
                                (!useDwellingsAsCapacity && availableLand < dt.getAreaPerDwelling()) ||  // not enough land available?
                                !development.isThisDwellingTypeAllowed(dt)) {                 // construction of this dwelling type allowed in this zone?
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
                    int size = (int) (aveSizeByTypeAndRegion[dto][region] + 0.5);
                    int quality = properties.main.qualityLevels;  // set all new dwellings to highest quality level

                    // set restriction for new dwellings to unrestricted by default
                    int restriction = 0;

                    int price;

                    if (makeSomeNewDdAffordable) {
                        if (SiloUtil.getRandomNumberAsFloat() <= shareOfAffordableDd) {
                            restriction = (int) (restrictionForAffordableDd * 100);
                        }
                    }
                    if (restriction == 0) {
                        // dwelling is unrestricted, generate free-market price
                        float avePrice = avePriceByTypeAndZone[dto][zone];
                        if (avePrice == 0) avePrice = avePriceByTypeAndRegion[dto][region];
                        if (avePrice == 0)
                            LOGGER.error("Ave. price is 0. Replace with region-wide average price for this dwelling type.");
                        price = (int) (priceIncreaseForNewDwelling * avePrice + 0.5);
                    } else {
                        // rent-controlled, multiply restriction (usually 0.3, 0.5 or 0.8) with median income with 30% housing budget
                        // correction: in the PUMS data set, households with the about-median income of 58,000 pay 18% of their income in rent...
                        int msa = geoData.getZones().get(zone).getMsa();
                        price = (int) (Math.abs((restriction / 100f)) * dataContainer.getHouseholdDataManager().getMedianIncome(msa) / 12 * 0.18 + 0.5);
                    }

                    restriction /= 100f;

                    int ddId = realEstate.getNextDwellingId();
                    Dwelling plannedDwelling = factory.createDwelling(ddId, zone, null, -1,
                            dt, size, quality, price, restriction, currentYear);
                    // Dwelling is created and added to events list, but dwelling it not added to realEstateDataManager yet
                    events.add(new ConstructionEvent(plannedDwelling));
                    realEstate.convertLand(zone, dt.getAreaPerDwelling());
                }
            }
        }
        return events;
    }

    @Override
    public boolean handleEvent(ConstructionEvent event) {

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        Dwelling dd = event.getDwelling();
        realEstate.addDwelling(dd);

        Coordinate coordinate = dataContainer.getGeoData().getZones().get(dd.getZoneId()).getRandomCoordinate();
        dd.setCoordinate(coordinate);

        realEstate.addDwellingToVacancyList(dd);

        if (dd.getId() == SiloUtil.trackDd) {
            SiloUtil.trackWriter.println("Constructed dwelling: " + dd);
        }
        return true;
    }

    @Override
    public void endYear(int year) {
    }

    @Override
    public void endSimulation() {

    }


    private float[][] calculateScaledAveragePriceByZone(float scaler) {
        // calculate scaled average housing price by dwelling type and zone

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        List<DwellingType> dwellingTypes = realEstate.getDwellingTypes();

        final int highestZoneId = geoData.getZones().keySet().stream().max(Comparator.naturalOrder()).get();
        float[][] avePrice = new float[dwellingTypes.size()][highestZoneId + 1];
        int[][] counter = new int[dwellingTypes.size()][highestZoneId + 1];
        for (Dwelling dd : realEstate.getDwellings()) {
            int dt = dwellingTypes.indexOf(dd.getType());
            int zone = geoData.getZones().get(dd.getZoneId()).getZoneId();
            counter[dt][zone]++;
            avePrice[dt][zone] += dd.getPrice();
        }
        for (DwellingType dt : dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            float[] avePriceThisType = new float[highestZoneId + 1];
            for (int zone : geoData.getZones().keySet()) {
                if (counter[dto][zone] > 0) {
                    avePriceThisType[zone] = avePrice[dto][zone] / counter[dto][zone];
                } else {
                    avePriceThisType[zone] = 0;
                }
            }
            float[] scaledAvePriceThisDwellingType = SiloUtil.scaleArray(avePriceThisType, scaler);
            for (int zones : geoData.getZones().keySet()) {
                avePrice[dto][zones] = scaledAvePriceThisDwellingType[zones];
            }
        }
        return avePrice;
    }


    private float[][] calculateScaledAveragePriceByRegion(float scaler) {

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        List<DwellingType> dwellingTypes = realEstate.getDwellingTypes();
        final int highestRegionId = geoData.getRegions().keySet().stream().max(Comparator.naturalOrder()).get();
        float[][] avePrice = new float[dwellingTypes.size()][highestRegionId + 1];
        int[][] counter = new int[dwellingTypes.size()][highestRegionId + 1];
        for (Dwelling dd : realEstate.getDwellings()) {
            int dt = dwellingTypes.indexOf(dd.getType());
            int region = geoData.getZones().get(dd.getZoneId()).getRegion().getId();
            counter[dt][region]++;
            avePrice[dt][region] += dd.getPrice();
        }
        for (DwellingType dt : dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            float[] avePriceThisType = new float[highestRegionId + 1];
            for (int region : geoData.getRegions().keySet()) {
                if (counter[dto][region] > 0) {
                    avePriceThisType[region] = avePrice[dto][region] / counter[dto][region];
                } else {
                    avePriceThisType[region] = 0;
                }
            }
            float[] scaledAvePriceThisDwellingType = SiloUtil.scaleArray(avePriceThisType, scaler);
            for (int region : geoData.getRegions().keySet()) {
                avePrice[dto][region] = scaledAvePriceThisDwellingType[region];
            }
        }
        return avePrice;
    }


    private float[][] calculateAverageSizeByTypeAndByRegion() {
        // calculate average housing size by dwelling type and region
        final int highestRegionId = geoData.getRegions().keySet().stream().max(Comparator.naturalOrder()).get();
        List<DwellingType> dwellingTypes = dataContainer.getRealEstateDataManager().getDwellingTypes();
        float[][] aveSize = new float[dwellingTypes.size()][highestRegionId + 1];
        int[][] counter = new int[dwellingTypes.size()][highestRegionId + 1];
        for (Dwelling dd : dataContainer.getRealEstateDataManager().getDwellings()) {
            int dt = dwellingTypes.indexOf(dd.getType());
            int region = geoData.getZones().get(dd.getZoneId()).getRegion().getId();
            counter[dt][region]++;
            aveSize[dt][region] += dd.getBedrooms();
        }
        for (DwellingType dt : dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            for (int region : geoData.getRegions().keySet()) {
                if (counter[dto][region] > 0) {
                    aveSize[dto][region] = aveSize[dto][region] / counter[dto][region];
                } else {
                    aveSize[dto][region] = 0;
                }
            }
        }
        // catch if one region should not have a given dwelling type (should almost never happen, but theoretically possible)
        float[] totalAveSizeByType = new float[dwellingTypes.size()];
        for (DwellingType dt : dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            int validRegions = 0;
            for (int region : geoData.getRegions().keySet()) {
                if (aveSize[dto][region] > 0) {
                    totalAveSizeByType[dto] += aveSize[dto][region];
                    validRegions++;
                }
            }
            totalAveSizeByType[dto] = totalAveSizeByType[dto] / validRegions;
        }
        for (DwellingType dt : dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            for (int region : geoData.getRegions().keySet()) {
                if (aveSize[dto][region] == 0) aveSize[dto][region] = totalAveSizeByType[dto];
            }
        }
        return aveSize;
    }


    private DwellingType[] findOrderOfDwellingTypes(DataContainer dataContainer) {
        // define order of dwelling types based on their average price. More expensive types are built first.

        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
        double[] prices = realEstateDataManager.getAveragePriceByDwellingType();
        List<DwellingType> dwellingTypes = realEstateDataManager.getDwellingTypes();
        int[] scaledPrices = new int[prices.length];
        for (int i = 0; i < prices.length; i++) {
            if (prices[i] * 10000 > Integer.MAX_VALUE) {
                LOGGER.error("Average housing price for " + dwellingTypes.get(i) +
                        " with " + prices[i] + " is too large to be sorted. Adjust code.");
            }
            scaledPrices[i] = (int) prices[i] * 10000;
        }
        int[] sortedPrices = IndexSort.indexSort(scaledPrices);
        DwellingType[] sortedDwellingTypes = new DwellingType[prices.length];
        for (int i = 0; i < prices.length; i++) {
            sortedDwellingTypes[prices.length - i - 1] = dwellingTypes.get(sortedPrices[i]);
        }
        return sortedDwellingTypes;
    }
}
