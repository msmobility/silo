package run;

import cern.jet.math.tdouble.DoubleFunctions;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.dwelling.DwellingData;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.HouseholdData;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix1D;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.util.Collection;

/**
 * Calculates and stores accessibilities
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 12 December 2012 in Santa Fe
 **/
public class AccessibilityPerth implements Accessibility {

    private static final Logger logger = Logger.getLogger(AccessibilityPerth.class);

    private final GeoData geoData;
    private final TravelTimes travelTimes;
    private final DwellingData dwellingData;
    private final HouseholdData householdData;

    private final Properties properties;

    private IndexedDoubleMatrix1D autoAccessibilities;
    private IndexedDoubleMatrix1D regionalAccessibilities;

    private final float autoOperatingCosts;
    private final float alphaAuto;
    private final float betaAuto;

    private float[] workTripLengthFrequencyDistribution;

    public AccessibilityPerth(GeoData geoData, TravelTimes travelTimes, Properties properties, DwellingData dwellingData, HouseholdData householdData) {
        this.geoData = geoData;
        this.travelTimes = travelTimes;
        this.properties = properties;
        this.autoOperatingCosts = properties.accessibility.autoOperatingCosts;
        this.alphaAuto = properties.accessibility.alphaAuto;
        this.betaAuto = properties.accessibility.betaAuto;
        this.dwellingData = dwellingData;
        this.householdData = householdData;
    }

    @Override
    public void setup() {
        this.autoAccessibilities = new IndexedDoubleMatrix1D(geoData.getZones().values());
        this.regionalAccessibilities = new IndexedDoubleMatrix1D(geoData.getRegions().values());
    }

    @Override
    public void prepareYear(int year) {
        calculateHansenAccessibilities(year);
    }

    @Override
    public void endYear(int year) {

    }

    @Override
    public void endSimulation() {

    }


    public void calculateHansenAccessibilities(int year) {
        logger.info("  Calculating accessibilities for " + year);
        final IndexedDoubleMatrix1D population = SummarizeData.getPopulationByZone(householdData, geoData, dwellingData);

        logger.info("  Calculating zone zone accessibilities: auto");
        final IndexedDoubleMatrix2D peakTravelTimeMatrixCar =
                travelTimes.getPeakSkim(TransportMode.car);
        final IndexedDoubleMatrix2D autoAccessZoneToZone =
                calculateZoneToZoneAccessibilities(population, peakTravelTimeMatrixCar, alphaAuto, betaAuto);


        logger.info("  Aggregating zone accessibilities");
        aggregateAccessibilities(autoAccessZoneToZone,
                autoAccessibilities, geoData.getZones().keySet());

        logger.info("  Scaling zone accessibilities");
        scaleAccessibility(autoAccessibilities);

        logger.info("  Calculating regional accessibilities");
        regionalAccessibilities.assign(calculateRegionalAccessibility(geoData.getRegions().values(), autoAccessibilities));
    }

    /**
     * Calculates regional accessibilities for the given regions and zonal accessibilities and returns them in a vector
     *
     * @param regions             the regions to calculate the accessibility for
     * @param autoAccessibilities the accessibility vector containing values for each zone
     */
    static IndexedDoubleMatrix1D calculateRegionalAccessibility(Collection<Region> regions, IndexedDoubleMatrix1D autoAccessibilities) {
        final IndexedDoubleMatrix1D matrix = new IndexedDoubleMatrix1D(regions);
        regions.parallelStream().forEach(r -> {
            double sum = r.getZones().stream().mapToDouble(z -> autoAccessibilities.getIndexed(z.getZoneId())).sum() / r.getZones().size();
            matrix.setIndexed(r.getId(), sum);
        });
        return matrix;
    }

    /**
     * Scales the accessibility vector such that the highest value equals to 100
     *
     * @param accessibility the accessibility vector containing agregated accessbilities for every zone
     */
    static void scaleAccessibility(IndexedDoubleMatrix1D accessibility) {
        final double sumScaleFactor = 100.0 / accessibility.getMaxValAndInternalIndex()[0];
        accessibility.assign(DoubleFunctions.mult(sumScaleFactor));
    }

    /**
     * Aggregates the zone to zone accessibilities into the given vectors, only considering the given keys.
     *
     * @param autoAcessibilities     zone to zone accessibility matrix for auto
     * @param aggregatedAuto         vector to which the the aggregated auto accessibilities  will be written to
     * @param keys                   zone ids that will be considered for aggregation
     */
    static void aggregateAccessibilities(IndexedDoubleMatrix2D autoAcessibilities, IndexedDoubleMatrix1D aggregatedAuto,
                                         Collection<Integer> keys) {
        keys.forEach(i -> {
            aggregatedAuto.setIndexed(i, autoAcessibilities.viewRow(i).zSum());
        });
    }

    /**
     * Aggregates the zone to zone Hansen accessibilities into the given vectors, only considering the given keys.
     * Formula for origin i to destinations j:
     * accessibility_i = population_j^alpha * e^(beta * traveltime_ij)
     *
     * @param population  a vector containing the population by zone
     * @param travelTimes zone to zone travel time matrix
     * @param alpha       alpha parameter used for the hansen calculation
     * @param beta        beta parameter used for the hansen calculation
     */
    static IndexedDoubleMatrix2D calculateZoneToZoneAccessibilities(IndexedDoubleMatrix1D population, IndexedDoubleMatrix2D travelTimes, double alpha, double beta) {
        final int size = Math.toIntExact(population.size());
        final IndexedDoubleMatrix2D travelTimesCopy = travelTimes.viewPart(0, 0, size, size).copy();
        return travelTimesCopy.forEachNonZero((origin, destination, travelTime) ->
                travelTime > 0 ? Math.pow(population.getIndexed(travelTimesCopy.getIdForInternalColumnIndex(destination)), alpha) * Math.exp(beta * travelTime) : 0);
    }

    @Override
    public double getAutoAccessibilityForZone(Zone zone) {
        // Can be combined with getTransitAccessibilityForZone into one method which get the mode
        // as an argument, nk/dz, july'18
        return this.autoAccessibilities.getIndexed(zone.getId());
    }

    @Override
    public double getTransitAccessibilityForZone(Zone zone) {
        return 0;
    }

    @Override
    public double getRegionalAccessibility(Region region) {
        return regionalAccessibilities.getIndexed(region.getId());
    }


//    public double getPeakTravelCosts(Location i, Location j) {
//        return (autoOperatingCosts / 100) * travelTimes.getTravelTime(i, j, TIME_OF_DAY, "car");
//        // Take costs provided by MATSim here? Should be possible
//        // without much alterations as they are part of NodeData, which is contained in MATSimTravelTimes, nk/dz, jan'18
//    }
}