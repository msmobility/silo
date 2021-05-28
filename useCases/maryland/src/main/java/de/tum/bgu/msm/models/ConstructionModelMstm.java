package de.tum.bgu.msm.models;

import de.tum.bgu.msm.common.util.IndexSort;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.HouseholdDataManagerMstm;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.development.Development;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.geo.MstmZone;
import de.tum.bgu.msm.events.impls.realEstate.ConstructionEvent;
import de.tum.bgu.msm.models.realEstate.construction.ConstructionDemandStrategy;
import de.tum.bgu.msm.models.realEstate.construction.ConstructionLocationStrategy;
import de.tum.bgu.msm.models.realEstate.construction.ConstructionModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Build new dwellings based on current demand. Model works in two steps. At the end of each simulation period,
 * the demand for new housing is calculated and stored. During the following simulation period, demand is realized
 * step by step. This helps simulating the time lag between demand for housing and actual completion of new dwellings.
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 4 December 2012 in Santa Fe
 **/
public class ConstructionModelMstm extends AbstractModel implements ConstructionModel {


    private final static Logger logger = Logger.getLogger(ConstructionModelMstm.class);

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
    private Map<Integer, List<Dwelling>> dwellingsByRegion;

    public ConstructionModelMstm(DataContainer dataContainer, DwellingFactory factory,
                                 Properties properties, ConstructionLocationStrategy locationStrategy,
                                 ConstructionDemandStrategy demandStrategy, Random rnd) {
        super(dataContainer, properties, rnd);
        this.geoData = dataContainer.getGeoData();
        this.accessibility = dataContainer.getAccessibility();
        this.factory = factory;
        this.locationStrategy = locationStrategy;
        this.demandStrategy = demandStrategy;
    }

    @Override
    public void setup() {

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
    public void prepareYear(int year) {
        dwellingsByRegion = dataContainer.getRealEstateDataManager().getDwellings()
                .stream().collect(Collectors.groupingBy(d -> geoData.getZones().get(d.getZoneId()).getRegion().getId()
                ));
    }

    @Override
    public Collection<ConstructionEvent> getEventsForCurrentYear(int year) {
        currentYear = year;
        List<ConstructionEvent> events = new ArrayList<>();

        // plan new dwellings based on demand and available land (not immediately realized, as construction needs some time)
        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        logger.info("  Planning dwellings to be constructed from " + year + " to " + (year + 1));

        // calculate demand by region
        double[][] vacancyByRegion = realEstate.getVacancyRateByTypeAndRegion();

        List<DwellingType> dwellingTypes = realEstate.getDwellingTypes().getTypes();
        double[][] demandByRegion = new double[dwellingTypes.size()][geoData.getRegions().keySet().stream().max(Comparator.naturalOrder()).get() + 1];
        double[][] avePriceByTypeAndZone = calculateScaledAveragePriceByZone(100);
        double[][] avePriceByTypeAndRegion = calculateScaledAveragePriceByRegion(100);
        float[][] aveSizeByTypeAndRegion = calculateAverageSizeByTypeAndByRegion();
        for (DwellingType dt : dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            for (int region : geoData.getRegions().keySet()) {
                final int size = dwellingsByRegion.getOrDefault(region, Collections.emptyList()).size();
                final double v = demandStrategy.calculateConstructionDemand(vacancyByRegion[dto][region], dt, size);
                demandByRegion[dto][region] = v;
            }
        }
        // try to satisfy demand, build more housing in zones with particularly low vacancy rates, if available land use permits
        int[][] existingDwellings = realEstate.getDwellingCountByTypeAndRegion();


        final int highestZoneId = geoData.getZones().keySet().stream().max(Comparator.naturalOrder()).get();
        double utilitiesByDwellingTypeByZone[][] = new double[dwellingTypes.size()][highestZoneId + 1];
        for (DwellingType dt : dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            for (Zone zone : geoData.getZones().values()) {
                double avePrice = avePriceByTypeAndZone[dto][zone.getId()];
                if (avePrice == 0) {
                    avePrice = avePriceByTypeAndRegion[dto][zone.getRegion().getId()];
                    if(avePrice == 0) {
                        avePrice = Arrays.stream(avePriceByTypeAndRegion[dto]).average().orElse(Double.NaN);
                    }
                }
                // evaluate utility for building DwellingType dt where the average price of this dwelling type in this zone is avePrice
                utilitiesByDwellingTypeByZone[dto][zone.getId()] =
                        locationStrategy.calculateConstructionProbability(dt, avePrice, accessibility.getAutoAccessibilityForZone(geoData.getZones().get(zone.getId())));
            }
        }

        DwellingType[] sortedDwellingTypes = findOrderOfDwellingTypes(dataContainer);
        for (DwellingType dt : sortedDwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            for (int region : geoData.getRegions().keySet()) {
                int unrealizedDwellings = 0;
                int demand = (int) (existingDwellings[dto][region] * demandByRegion[dto][region] + 0.5);
                if (demand == 0) {
                    continue;
                }
                int[] zonesInThisRegion = geoData.getRegions().get(region).getZones().stream().mapToInt(Zone::getZoneId).toArray();
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
                            prob[zone] = betaForZoneChoice * availableLand * utilitiesByDwellingTypeByZone[dto][zone];
                            probSum += prob[zone];
                        }
                    }
                    if (probSum == 0) {
                        unrealizedDwellings++;
                        continue;
                    }
                    for (int zone : zonesInThisRegion) {
                        prob[zone] = prob[zone] / probSum;
                    }
                    int zone = SiloUtil.select(prob, random);
                    events.add(createNewDwelling(realEstate, aveSizeByTypeAndRegion, avePriceByTypeAndZone,
                            avePriceByTypeAndRegion, dt, dto, region, zone));
                }
                for (int i = 1; i <= unrealizedDwellings; i++) {
                    int zone = allocateUnrealizedDemandInDifferentRegion(realEstate, dt, dto,
                            avePriceByTypeAndZone, avePriceByTypeAndRegion, utilitiesByDwellingTypeByZone);
                    if(zone > -1) {
                        events.add(createNewDwelling(realEstate, aveSizeByTypeAndRegion, avePriceByTypeAndZone,
                                avePriceByTypeAndRegion, dt, dto, region, zone));
                    }
                }
            }
        }
        return events;
    }


    private ConstructionEvent createNewDwelling (RealEstateDataManager realEstate, float[][] aveSizeByTypeAndRegion,
                                                 double[][] avePriceByTypeAndZone, double[][] avePriceByTypeAndRegion,
                                                 DwellingType dt, int dto, int region, int zone) {
        // create construction event that is added to event list

        int size = (int) (aveSizeByTypeAndRegion[dto][region] + 0.5);
        int quality = properties.main.qualityLevels;  // set all new dwellings to highest quality level

        // set restriction for new dwellings to unrestricted by default
        int restriction = 0;

        int price;

        if (makeSomeNewDdAffordable) {
            if (random.nextDouble() <= shareOfAffordableDd) {
                restriction = (int) (restrictionForAffordableDd * 100);
            }
        }
        if (restriction == 0) {
            // dwelling is unrestricted, generate free-market price
            double avePrice = avePriceByTypeAndZone[dto][zone];
            if (avePrice == 0) {
                avePrice = avePriceByTypeAndRegion[dto][region];
            }
            if (avePrice == 0) {
                logger.error("Ave. price is 0. Replace with region-wide average price for this dwelling type.");
            }
            price = (int) (priceIncreaseForNewDwelling * avePrice + 0.5);
        } else {
            // rent-controlled, multiply restriction (usually 0.3, 0.5 or 0.8) with median income with 30% housing budget
            // correction: in the PUMS data set, households with the about-median income of 58,000 pay 18% of their income in rent...
            int msa = ((MstmZone) geoData.getZones().get(zone)).getMsa();
            price = (int) (Math.abs((restriction / 100f)) * ((HouseholdDataManagerMstm)dataContainer.getHouseholdDataManager()).getMedianIncome(msa) / 12 * 0.18 + 0.5);
        }

        restriction /= 100f;

        int ddId = realEstate.getNextDwellingId();
        DwellingMstm plannedDwelling = (DwellingMstm) factory.createDwelling(ddId, zone, null, -1,
                dt, size, quality, price, currentYear);
        plannedDwelling.setRestriction(restriction);
        // Dwelling is created and added to events list, but dwelling it not added to realEstateDataManager yet
        realEstate.convertLand(zone, dt.getAreaPerDwelling());
        return (new ConstructionEvent(plannedDwelling));
    }


    private int allocateUnrealizedDemandInDifferentRegion(RealEstateDataManager realEstate, DwellingType dt, int dto,
                                                          double[][] avePriceByTypeAndZone, double[][] avePriceByTypeAndRegion, double[][] utilitiesByDwellingTypeByZone) {
        // Due to limited available land or zoning, not all demand can be realized in all zones. Find an alternative
        // region where demand can be built
        int selectedZone = 1;
        double probSum = 0;
        double[] prob = new double[geoData.getZones().keySet().stream().max(Comparator.naturalOrder()).get()+1];
        for (Map.Entry<Integer, Zone> zone : geoData.getZones().entrySet()) {
            double availableLand = realEstate.getAvailableCapacityForConstruction(zone.getValue().getId());
            if (availableLand < dt.getAreaPerDwelling()) {
                continue;
            }
            Development development = zone.getValue().getDevelopment();
            boolean useDwellingsAsCapacity = development.isUseDwellingCapacity();
            if ((useDwellingsAsCapacity && availableLand == 0) ||                              // capacity by dwellings is use
                    (!useDwellingsAsCapacity && availableLand < dt.getAreaPerDwelling()) ||  // not enough land available?
                    !development.isThisDwellingTypeAllowed(dt)) {                 // construction of this dwelling type allowed in this zone?
                prob[zone.getValue().getId()] = 0.;
            } else {
                prob[zone.getValue().getId()] = betaForZoneChoice * availableLand * utilitiesByDwellingTypeByZone[dto][zone.getValue().getId()];
                probSum += prob[zone.getValue().getId()];
            }
        }
        if (probSum == 0) {
            return -1;
        }
        for (Map.Entry<Integer, Zone> zone : geoData.getZones().entrySet()) {
            prob[zone.getValue().getId()] = prob[zone.getValue().getId()] / probSum;
        }
        return SiloUtil.select(prob, random);
    }

    @Override
    public boolean handleEvent(ConstructionEvent event) {

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        Dwelling dd = event.getDwelling();
        realEstate.addDwelling(dd);

        Coordinate coordinate = dataContainer.getGeoData().getZones().get(dd.getZoneId()).getRandomCoordinate(random);
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

    private double[][] calculateScaledAveragePriceByZone(float scaler) {
        // calculate scaled average housing price by dwelling type and zone

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        List<DwellingType> dwellingTypes = realEstate.getDwellingTypes().getTypes();

        final int highestZoneId = geoData.getZones().keySet().stream().max(Comparator.naturalOrder()).get();
        double[][] avePrice = new double[dwellingTypes.size()][highestZoneId + 1];
        int[][] counter = new int[dwellingTypes.size()][highestZoneId + 1];
        for (Dwelling dd : realEstate.getDwellings()) {
            int dt = dwellingTypes.indexOf(dd.getType());
            int zone = geoData.getZones().get(dd.getZoneId()).getZoneId();
            counter[dt][zone]++;
            avePrice[dt][zone] += dd.getPrice();
        }
        for (DwellingType dt : dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            double[] avePriceThisType = new double[highestZoneId + 1];
            for (int zone : geoData.getZones().keySet()) {
                if (counter[dto][zone] > 0) {
                    avePriceThisType[zone] = avePrice[dto][zone] / counter[dto][zone];
                } else {
                    avePriceThisType[zone] = 0;
                }
            }
            double[] scaledAvePriceThisDwellingType = SiloUtil.scaleArray(avePriceThisType, scaler);
            for (int zones : geoData.getZones().keySet()) {
                avePrice[dto][zones] = scaledAvePriceThisDwellingType[zones];
            }
        }
        return avePrice;
    }


    private double[][] calculateScaledAveragePriceByRegion(float scaler) {

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        List<DwellingType> dwellingTypes = realEstate.getDwellingTypes().getTypes();
        final int highestRegionId = geoData.getRegions().keySet().stream().max(Comparator.naturalOrder()).get();
        double[][] avePrice = new double[dwellingTypes.size()][highestRegionId + 1];
        int[][] counter = new int[dwellingTypes.size()][highestRegionId + 1];
        for (Dwelling dd : realEstate.getDwellings()) {
            int dt = dwellingTypes.indexOf(dd.getType());
            int region = geoData.getZones().get(dd.getZoneId()).getRegion().getId();
            counter[dt][region]++;
            avePrice[dt][region] += dd.getPrice();
        }
        for (DwellingType dt : dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            double[] avePriceThisType = new double[highestRegionId + 1];
            for (int region : geoData.getRegions().keySet()) {
                if (counter[dto][region] > 0) {
                    avePriceThisType[region] = avePrice[dto][region] / counter[dto][region];
                } else {
                    avePriceThisType[region] = 0;
                }
            }
            double[] scaledAvePriceThisDwellingType = SiloUtil.scaleArray(avePriceThisType, scaler);
            for (int region : geoData.getRegions().keySet()) {
                avePrice[dto][region] = scaledAvePriceThisDwellingType[region];
            }
        }
        return avePrice;
    }


    private float[][] calculateAverageSizeByTypeAndByRegion() {
        // calculate average housing size by dwelling type and region
        final int highestRegionId = geoData.getRegions().keySet().stream().max(Comparator.naturalOrder()).get();
        List<DwellingType> dwellingTypes = dataContainer.getRealEstateDataManager().getDwellingTypes().getTypes();
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
        List<DwellingType> dwellingTypes = realEstateDataManager.getDwellingTypes().getTypes();
        int[] scaledPrices = new int[prices.length];
        for (int i = 0; i < prices.length; i++) {
            if (prices[i] * 10000 > Integer.MAX_VALUE) {
                logger.error("Average housing price for " + dwellingTypes.get(i) +
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