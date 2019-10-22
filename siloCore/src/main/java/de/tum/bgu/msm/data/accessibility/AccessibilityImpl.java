package de.tum.bgu.msm.data.accessibility;

import cern.jet.math.tdouble.DoubleFunctions;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.DwellingData;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.HouseholdData;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobData;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix1D;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import de.tum.bgu.msm.util.matrices.Matrices;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Calculates and stores accessibilities
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 12 December 2012 in Santa Fe
 **/
public class AccessibilityImpl implements Accessibility {

    private static final Logger logger = Logger.getLogger(AccessibilityImpl.class);

    private final GeoData geoData;
    private final TravelTimes travelTimes;
    private final JobData jobData;

    private IndexedDoubleMatrix1D autoAccessibilities;
    private IndexedDoubleMatrix1D transitAccessibilities;
    private IndexedDoubleMatrix1D regionalAccessibilities;

    private final float alphaAuto;
    private final float betaAuto;
    private final float alphaTransit;
    private final float betaTransit;

    public AccessibilityImpl(GeoData geoData, TravelTimes travelTimes, Properties properties,
                             DwellingData dwellingData, JobData jobData) {
        this.geoData = geoData;
        this.travelTimes = travelTimes;
        this.alphaAuto = properties.accessibility.alphaAuto;
        this.betaAuto = properties.accessibility.betaAuto;
        this.alphaTransit = properties.accessibility.alphaTransit;
        this.betaTransit = properties.accessibility.betaTransit;
        this.jobData = jobData;
    }

    @Override
    public void setup() {
        this.autoAccessibilities = new IndexedDoubleMatrix1D(geoData.getZones().values());
        this.transitAccessibilities = new IndexedDoubleMatrix1D(geoData.getZones().values());
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

    @Override
    public void calculateHansenAccessibilities(int year) {

        logger.info("  Calculating accessibilities for " + year);
        final Map<Integer, List<Job>> jobsByZone = jobData.getJobs().stream().collect(Collectors.groupingBy(Location::getZoneId));
        IndexedDoubleMatrix1D employment = new IndexedDoubleMatrix1D(geoData.getZones().values());
        for(Map.Entry<Integer, List<Job>> entry: jobsByZone.entrySet()) {
            employment.setIndexed(entry.getKey(), entry.getValue().size());
        }

        logger.info("  Calculating zone zone accessibilities: auto");
        final IndexedDoubleMatrix2D peakTravelTimeMatrixCar =
                travelTimes.getPeakSkim(TransportMode.car);
        final IndexedDoubleMatrix2D autoAccessZoneToZone =
                calculateZoneToZoneAccessibilities(employment, peakTravelTimeMatrixCar, alphaAuto, betaAuto);
        logger.info("  Calculating zone zone accessibilities: transit");
        final IndexedDoubleMatrix2D peakTravelTimeMatrixTransit =
                travelTimes.getPeakSkim(TransportMode.pt);
        final IndexedDoubleMatrix2D transitAccessZoneToZone =
                calculateZoneToZoneAccessibilities(employment,
                        peakTravelTimeMatrixTransit, alphaTransit, betaTransit);

        logger.info("  Aggregating zone accessibilities");
        aggregateAccessibilities(autoAccessZoneToZone, transitAccessZoneToZone,
                autoAccessibilities, transitAccessibilities, geoData.getZones().keySet());

        logger.info("  Scaling zone accessibilities");
        scaleAccessibility(autoAccessibilities);
        scaleAccessibility(transitAccessibilities);

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
     * @param transitAccessibilities zone to zone accessibility matrix for transit
     * @param aggregatedAuto         vector to which the the aggregated auto accessibilities  will be written to
     * @param aggregatedTransit      vector to which the the aggregated transit accessibilities will be written to
     * @param keys                   zone ids that will be considered for aggregation
     */
    static void aggregateAccessibilities(IndexedDoubleMatrix2D autoAcessibilities, IndexedDoubleMatrix2D transitAccessibilities,
                                         IndexedDoubleMatrix1D aggregatedAuto, IndexedDoubleMatrix1D aggregatedTransit, Collection<Integer> keys) {
        keys.forEach(i -> {
            aggregatedAuto.setIndexed(i, autoAcessibilities.viewRow(i).zSum());
            aggregatedTransit.setIndexed(i, transitAccessibilities.viewRow(i).zSum());
        });
    }

    /**
     * Aggregates the zone to zone Hansen accessibilities into the given vectors, only considering the given keys.
     * Formula for origin i to destinations j:
     * accessibility_i = population_j^alpha * e^(beta * traveltime_ij)
     *
     * @param employment  a vector containing the employment by zone
     * @param travelTimes zone to zone travel time matrix
     * @param alpha       alpha parameter used for the hansen calculation
     * @param beta        beta parameter used for the hansen calculation
     */
    static IndexedDoubleMatrix2D calculateZoneToZoneAccessibilities(IndexedDoubleMatrix1D employment, IndexedDoubleMatrix2D travelTimes, double alpha, double beta) {
        final IndexedDoubleMatrix2D travelTimesCopy = travelTimes.copy();
        return travelTimesCopy.forEachNonZero((origin, destination, travelTime) ->
                travelTime > 0 ? Math.pow(employment.getIndexed(travelTimesCopy.getIdForInternalColumnIndex(destination)), alpha) * Math.exp(beta * travelTime) : 0);
    }

    @Override
    public double getAutoAccessibilityForZone(Zone zone) {
    	// Can be combined with getTransitAccessibilityForZone into one method which get the mode
    	// as an argument, nk/dz, july'18
        return this.autoAccessibilities.getIndexed(zone.getId());
    }

    @Override
    public double getTransitAccessibilityForZone(Zone zone) {
        return this.transitAccessibilities.getIndexed(zone.getId());
    }

    @Override
    public double getRegionalAccessibility(Region region) {
        return regionalAccessibilities.getIndexed(region.getId());
    }
}