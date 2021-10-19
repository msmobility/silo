package de.tum.bgu.msm.models.realEstate.construction;

import de.tum.bgu.msm.common.util.IndexSort;
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

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Build new dwellings based on current demand. Model works in two steps. At the end of each simulation period,
 * the demand for new housing is calculated and stored. During the following simulation period, demand is realized
 * step by step. This helps simulating the time lag between demand for housing and actual completion of new dwellings.
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 4 December 2012 in Santa Fe
 **/
public class ConstructionModelImpl extends AbstractModel implements ConstructionModel {

    private final static Logger logger = Logger.getLogger(ConstructionModelImpl.class);

    private final GeoData geoData;
    private final DwellingFactory factory;
    private final ConstructionLocationStrategy locationStrategy;
    private final ConstructionDemandStrategy demandStrategy;
    private final Accessibility accessibility;

    private int currentYear = -1;

    private float betaForZoneChoice;
    private float priceIncreaseForNewDwelling;
    private Map<Integer, List<Dwelling>> dwellingsByRegion;
    PrintWriter pwd;


    public ConstructionModelImpl(DataContainer dataContainer, DwellingFactory factory,
                                 Properties properties, ConstructionLocationStrategy locationStrategy,
                                 ConstructionDemandStrategy demandStrategy, Random random) {
        super(dataContainer, properties, random);
        this.geoData = dataContainer.getGeoData();
        this.accessibility = dataContainer.getAccessibility();
        this.factory = factory;
        this.locationStrategy = locationStrategy;
        this.demandStrategy = demandStrategy;
    }

    @Override
    public void setup() {
        // set up model to evaluate zones for construction of new dwellings
        betaForZoneChoice = properties.realEstate.constructionLogModelBeta;
        priceIncreaseForNewDwelling = properties.realEstate.constructionLogModelInflator;

        pwd = SiloUtil.openFileForSequentialWriting(properties.main.baseDirectory +
                "/scenOutput/" +
                properties.main.scenarioName +
                "/constructionModel.csv", false);
        pwd.print("year,region,dd_type,dd,dd_demand,dd_price,dd_abs_price,dd_size,vacancy");
        pwd.println();
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
        double[][] aveAbsolutePriceByTypeAndZone = calculateAbsoluteAveragePriceByZone();
        double[][] avePriceByTypeAndRegion = calculateScaledAveragePriceByRegion(100);
        double[][] aveAbsolutePriceByTypeAndRegion = calculateAbosluteAveragePriceByRegion();
        float[][] aveSizeByTypeAndRegion = calculateAverageSizeByTypeAndByRegion();


        for (DwellingType dt : dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
            for (int region : geoData.getRegions().keySet()) {
                if (dwellingsByRegion.containsKey(region)){
                    demandByRegion[dto][region] = demandStrategy.calculateConstructionDemand(vacancyByRegion[dto][region], dt, dwellingsByRegion.get(region).size());
                } else {
                    //regions that, after scaling down the population, do not have any dwelling, thus are not in the map dwellingsByRegion
                    demandByRegion[dto][region] = 0;
                }

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
                    if (avePrice == 0) {
                        avePrice = Arrays.stream(avePriceByTypeAndRegion[dto]).average().orElse(Double.NaN);
                    }
                }
                // evaluate utility for building DwellingType dt where the average price of this dwelling type in this zone is avePrice
                utilitiesByDwellingTypeByZone[dto][zone.getId()] =
                        locationStrategy.calculateConstructionProbability(dt, avePrice, accessibility.getAutoAccessibilityForZone(geoData.getZones().get(zone.getId())));
            }
        }

        DwellingType[] sortedDwellingTypes = findOrderOfDwellingTypes(dataContainer);
        int unrealizedDemandCounter = 0;

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
                    events.add(createNewDwelling(realEstate, aveSizeByTypeAndRegion, aveAbsolutePriceByTypeAndZone,
                            aveAbsolutePriceByTypeAndRegion, dt, dto, region, zone));
                }
                for (int i = 1; i <= unrealizedDwellings; i++) {
                    int zone = allocateUnrealizedDemandInDifferentRegion(realEstate, dt, dto,
                            avePriceByTypeAndZone, avePriceByTypeAndRegion, utilitiesByDwellingTypeByZone);

                    if (zone > -1) {
                        events.add(createNewDwelling(realEstate, aveSizeByTypeAndRegion, aveAbsolutePriceByTypeAndZone,
                                aveAbsolutePriceByTypeAndRegion, dt, dto, region, zone));
                    } else {
                        unrealizedDemandCounter++;
                    }
                }
            }
        }

        for(int region : geoData.getRegions().keySet()){
            for (DwellingType dt : dwellingTypes){
                int ddTypeIndex = dwellingTypes.indexOf(dt);
                double dd = existingDwellings[ddTypeIndex][region];
                double demand = demandByRegion[ddTypeIndex][region];
                double price = avePriceByTypeAndRegion[ddTypeIndex][region];
                double absPrice = aveAbsolutePriceByTypeAndRegion[ddTypeIndex][region];
                double size = aveSizeByTypeAndRegion[ddTypeIndex][region];
                double vacancy = vacancyByRegion[ddTypeIndex][region];

                pwd.println(year + "," +
                        region +  "," +
                        dt.toString() + "," +
                        dd + "," +
                        demand + "," +
                        price +  "," +
                        absPrice + "," +
                        size + "," +
                        vacancy);


            }
        }

        logger.info("Planning of construction done. Planned " + events.size() + " dwellings.");
        if(unrealizedDemandCounter > 0) {
            logger.warn("There have been " + unrealizedDemandCounter + " dwellings that could not be built " +
                    "due to lack of developable land.");
        }
        return events;
    }

    @Override
    public boolean handleEvent(ConstructionEvent event) {

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        Dwelling dd = event.getDwelling();
        realEstate.addDwelling(dd);

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
        pwd.close();
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

    private double[][] calculateAbsoluteAveragePriceByZone() {
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
            double[] scaledAvePriceThisDwellingType = avePriceThisType;
            for (int zones : geoData.getZones().keySet()) {
                avePrice[dto][zones] = scaledAvePriceThisDwellingType[zones];
            }
        }
        return avePrice;
    }



    private ConstructionEvent createNewDwelling(RealEstateDataManager realEstate, float[][] aveSizeByTypeAndRegion,
                                                double[][] avePriceByTypeAndZone, double[][] avePriceByTypeAndRegion,
                                                DwellingType dt, int dto, int region, int zone) {
        // create construction event that is added to event list

        int size = (int) (aveSizeByTypeAndRegion[dto][region] + 0.5);
        int quality = properties.main.qualityLevels;  // set all new dwellings to highest quality level

        // dwelling is unrestricted, generate free-market price
        double avePrice = avePriceByTypeAndZone[dto][zone];
        if (avePrice == 0) {
            avePrice = avePriceByTypeAndRegion[dto][region];
        }
        if (avePrice == 0) {
            logger.error("Ave. price is 0. Replace with region-wide average price for this dwelling type.");
        }

        int price = (int) (priceIncreaseForNewDwelling * avePrice + 0.5);

        int ddId = realEstate.getNextDwellingId();
        Coordinate coordinate = dataContainer.getGeoData().getZones().get(zone).getRandomCoordinate(this.random);
        Dwelling plannedDwelling = factory.createDwelling(ddId, zone, coordinate, -1,
                dt, size, quality, price, currentYear);
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
        double[] prob = new double[geoData.getZones().keySet().stream().max(Comparator.naturalOrder()).get() + 1];
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

    private double[][] calculateAbosluteAveragePriceByRegion() {

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
            double[] scaledAvePriceThisDwellingType = avePriceThisType;
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
                if (aveSize[dto][region] == 0) {
                    aveSize[dto][region] = totalAveSizeByType[dto];
                }
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
