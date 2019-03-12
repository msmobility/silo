package de.tum.bgu.msm.data.accessibility;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.jet.math.tdouble.DoubleFunctions;
import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.simulator.AnnualUpdate;
import de.tum.bgu.msm.util.matrices.Matrices;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.utils.TravelTimeUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.util.Collection;

/**
 * Calculates and stores accessibilities
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 12 December 2012 in Santa Fe
 **/
public class Accessibility implements AnnualUpdate {

    private static final Logger logger = Logger.getLogger(Accessibility.class);

    private final Properties properties;

    private DoubleMatrix1D autoAccessibilities;
    private DoubleMatrix1D transitAccessibilities;
    private DoubleMatrix1D regionalAccessibilities;

    private final float autoOperatingCosts;
    private final float alphaAuto;
    private final float betaAuto;
    private final float alphaTransit;
    private final float betaTransit;

    private TravelTimes travelTimes;

    private float[] workTripLengthFrequencyDistribution;
    private SiloDataContainer dataContainer;

    public Accessibility(SiloDataContainer dataContainer,  Properties properties) {
        this.properties = properties;
        this.dataContainer = dataContainer;
        this.autoOperatingCosts = properties.accessibility.autoOperatingCosts;
        this.alphaAuto = properties.accessibility.alphaAuto;
        this.betaAuto = properties.accessibility.betaAuto;
        this.alphaTransit = properties.accessibility.alphaTransit;
        this.betaTransit = properties.accessibility.betaTransit;
    }

    @Override
    public void setup() {
        this.autoAccessibilities = Matrices.doubleMatrix1D(dataContainer.getGeoData().getZones().values());
        this.transitAccessibilities = Matrices.doubleMatrix1D(dataContainer.getGeoData().getZones().values());
        this.regionalAccessibilities = Matrices.doubleMatrix1D(dataContainer.getGeoData().getRegions().values());


        
        logger.info("Initializing trip length frequency distributions");
        readWorkTripLengthFrequencyDistribution();
    }

    @Override
    public void prepareYear(int year) {
        calculateHansenAccessibilities(year);
    }

    @Override
    public void finishYear(int year) {

    }



    public void calculateHansenAccessibilities(int year) {
        logger.info("  Calculating accessibilities for " + year);
        final DoubleMatrix1D population = SummarizeData.getPopulationByZone(dataContainer);

        logger.info("  Calculating zone zone accessibilities: auto");
        final DoubleMatrix2D peakTravelTimeMatrixCar =
                TravelTimeUtil.getPeakTravelTimeMatrix(TransportMode.car, travelTimes, dataContainer.getGeoData().getZones().values());
        final DoubleMatrix2D autoAccessZoneToZone =
                calculateZoneToZoneAccessibilities(population, peakTravelTimeMatrixCar, alphaAuto, betaAuto);
        logger.info("  Calculating zone zone accessibilities: transit");
        final DoubleMatrix2D peakTravelTimeMatrixTransit =
                TravelTimeUtil.getPeakTravelTimeMatrix(TransportMode.pt, travelTimes, dataContainer.getGeoData().getZones().values());
        final DoubleMatrix2D transitAccessZoneToZone =
                calculateZoneToZoneAccessibilities(population,
                        peakTravelTimeMatrixTransit, alphaTransit, betaTransit);

        logger.info("  Aggregating zone accessibilities");
        aggregateAccessibilities(autoAccessZoneToZone, transitAccessZoneToZone, autoAccessibilities, transitAccessibilities, dataContainer.getGeoData().getZones().keySet());

        logger.info("  Scaling zone accessibilities");
        scaleAccessibility(autoAccessibilities);
        scaleAccessibility(transitAccessibilities);

        logger.info("  Calculating regional accessibilities");
        regionalAccessibilities.assign(calculateRegionalAccessibility(dataContainer.getGeoData().getRegions().values(), autoAccessibilities));
    }

    /**
     * Calculates regional accessibilities for the given regions and zonal accessibilities and returns them in a vector
     *
     * @param regions             the regions to calculate the accessibility for
     * @param autoAccessibilities the accessibility vector containing values for each zone
     */
    static DoubleMatrix1D calculateRegionalAccessibility(Collection<Region> regions, DoubleMatrix1D autoAccessibilities) {
        final DoubleMatrix1D matrix = Matrices.doubleMatrix1D(regions);
        regions.parallelStream().forEach(r -> {
            double sum = r.getZones().stream().mapToDouble(z -> autoAccessibilities.getQuick(z.getZoneId())).sum() / r.getZones().size();
            matrix.setQuick(r.getId(), sum);
        });
        return matrix;
    }

    /**
     * Scales the accessibility vector such that the highest value equals to 100
     *
     * @param accessibility the accessibility vector containing agregated accessbilities for every zone
     */
    static void scaleAccessibility(DoubleMatrix1D accessibility) {
        final double sumScaleFactor = 100.0 / accessibility.getMaxLocation()[0];
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
    static void aggregateAccessibilities(DoubleMatrix2D autoAcessibilities, DoubleMatrix2D transitAccessibilities,
                                         DoubleMatrix1D aggregatedAuto, DoubleMatrix1D aggregatedTransit, Collection<Integer> keys) {
        keys.forEach(i -> {

            aggregatedAuto.setQuick(i, autoAcessibilities.viewRow(i).zSum());
            aggregatedTransit.setQuick(i, transitAccessibilities.viewRow(i).zSum());
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
    static DoubleMatrix2D calculateZoneToZoneAccessibilities(DoubleMatrix1D population, DoubleMatrix2D travelTimes, double alpha, double beta) {
        final int size = Math.toIntExact(population.size());
        final DoubleMatrix2D travelTimesCopy = travelTimes.viewPart(0, 0, size, size).copy();
        return travelTimesCopy.forEachNonZero((origin, destination, travelTime) ->
                travelTime > 0 ? Math.pow(population.getQuick(destination), alpha) * Math.exp(beta * travelTime) : 0);
    }

    private void readWorkTripLengthFrequencyDistribution() {
        String fileName = properties.main.baseDirectory + properties.accessibility.htsWorkTLFD;
        TableDataSet tlfd = SiloUtil.readCSVfile(fileName);
        workTripLengthFrequencyDistribution = new float[tlfd.getRowCount() + 1];
        for (int row = 1; row <= tlfd.getRowCount(); row++) {
            int tt = (int) tlfd.getValueAt(row, "TravelTime");
            if (tt > workTripLengthFrequencyDistribution.length) {
                logger.error("Inconsistent trip length frequency in " + properties.main.baseDirectory +
                        properties.accessibility.htsWorkTLFD + ": " + tt + ". Provide data in 1-min increments.");
            }
            workTripLengthFrequencyDistribution[tt] = tlfd.getValueAt(row, "utility");
        }
    }

    public float getCommutingTimeProbability(int minutes) {
        if (minutes < workTripLengthFrequencyDistribution.length) {
            return workTripLengthFrequencyDistribution[minutes];
        } else {
            return 0;
        }
    }

    public double getAutoAccessibilityForZone(int zone) {
    	// Can be combined with getTransitAccessibilityForZone into one method which get the mode
    	// as an argument, nk/dz, july'18
        return this.autoAccessibilities.getQuick(zone);
    }

    public double getTransitAccessibilityForZone(int zoneId) {
        return this.transitAccessibilities.getQuick(zoneId);
    }

    public double getRegionalAccessibility(int region) {
        return regionalAccessibilities.getQuick(region);
    }


//    public double getPeakTravelCosts(Location i, Location j) {
//        return (autoOperatingCosts / 100) * travelTimes.getTravelTime(i, j, TIME_OF_DAY, "car");
//        // Take costs provided by MATSim here? Should be possible
//        // without much alterations as they are part of NodeData, which is contained in MATSimTravelTimes, nk/dz, jan'18
//    }
}