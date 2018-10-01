package de.tum.bgu.msm.data;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.jet.math.tdouble.DoubleFunctions;
import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.util.matrices.Matrices;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.util.Collection;

/**
 * Calculates and stores accessibilities
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 12 December 2012 in Santa Fe
 **/
public class Accessibility {

    private static final Logger LOGGER = Logger.getLogger(Accessibility.class);
    private static final double TIME_OF_DAY = 8 * 60. * 60.;

    private final GeoData geoData;

    private DoubleMatrix1D autoAccessibilities;
    private DoubleMatrix1D transitAccessibilities;
    private DoubleMatrix1D regionalAccessibilities;

    private final float autoOperatingCosts;
    private final float alphaAuto;
    private final float betaAuto;
    private final float alphaTransit;
    private final float betaTransit;

    private final SiloDataContainer dataContainer;
    private final TravelTimes travelTimes;

    private float[] workTLFD;

    public Accessibility(SiloDataContainer dataContainer) {
        this.geoData = dataContainer.getGeoData();
        this.dataContainer = dataContainer;

        this.autoAccessibilities = Matrices.doubleMatrix1D(geoData.getZones().values());
        this.transitAccessibilities = Matrices.doubleMatrix1D(geoData.getZones().values());
        this.regionalAccessibilities = Matrices.doubleMatrix1D(geoData.getRegions().values());

        this.autoOperatingCosts = Properties.get().accessibility.autoOperatingCosts;
        this.alphaAuto = Properties.get().accessibility.alphaAuto;
        this.betaAuto = Properties.get().accessibility.betaAuto;
        this.alphaTransit = Properties.get().accessibility.alphaTransit;
        this.betaTransit = Properties.get().accessibility.betaTransit;

        this.travelTimes = dataContainer.getTravelTimes();
    }

    /**
     * Initializes the accessibility object by reading trip length distributions
     * and zone to region travel times. Travel times should therefore
     * be read/updated _BEFORE_ this method is called.
     */
    public void initialize() {
        LOGGER.info("Initializing trip length frequency distributions");
        readWorkTripLengthFrequencyDistribution();
    }

    public void calculateHansenAccessibilities(int year) {
        LOGGER.info("  Calculating accessibilities for " + year);
        final DoubleMatrix1D population = SummarizeData.getPopulationByZone(dataContainer);

        LOGGER.info("  Calculating zone zone accessibilities: auto");
        final DoubleMatrix2D peakTravelTimeMatrix = getPeakTravelTimeMatrix(TransportMode.car);
        final DoubleMatrix2D autoAccessZoneToZone =
                calculateZoneToZoneAccessibilities(population, peakTravelTimeMatrix, alphaAuto, betaAuto);
        LOGGER.info("  Calculating zone zone accessibilities: transit");
        final DoubleMatrix2D transitAccessZoneToZone =
                calculateZoneToZoneAccessibilities(population, getPeakTravelTimeMatrix(TransportMode.pt), alphaTransit, betaTransit);

        LOGGER.info("  Aggregating zone accessibilities");
        aggregateAccessibilities(autoAccessZoneToZone, transitAccessZoneToZone, autoAccessibilities, transitAccessibilities, geoData.getZones().keySet());

        LOGGER.info("  Scaling zone accessibilities");
        scaleAccessibility(autoAccessibilities);
        scaleAccessibility(transitAccessibilities);

        LOGGER.info("  Calculating regional accessibilities");
        regionalAccessibilities.assign(calculateRegionalAccessibility(geoData.getRegions().values(), autoAccessibilities));
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
        String fileName = Properties.get().main.baseDirectory + Properties.get().accessibility.htsWorkTLFD;
        TableDataSet tlfd = SiloUtil.readCSVfile(fileName);
        workTLFD = new float[tlfd.getRowCount() + 1];
        for (int row = 1; row <= tlfd.getRowCount(); row++) {
            int tt = (int) tlfd.getValueAt(row, "TravelTime");
            if (tt > workTLFD.length) {
                LOGGER.error("Inconsistent trip length frequency in " + Properties.get().main.baseDirectory +
                        Properties.get().accessibility.htsWorkTLFD + ": " + tt + ". Provide data in 1-min increments.");
            }
            workTLFD[tt] = tlfd.getValueAt(row, "utility");
        }
    }

    public float getCommutingTimeProbability(int minutes) {
        if (minutes < workTLFD.length) {
            return workTLFD[minutes];
        } else {
            return 0;
        }
    }


    private DoubleMatrix2D getPeakTravelTimeMatrix(String mode) {
    	if (travelTimes instanceof SkimTravelTimes) {
    		return ((SkimTravelTimes) travelTimes).getMatrixForMode(mode);
    	}
    	
    	// The following lines can go once the skim-based case (above) remains the only in this method
    	// MATSim-based accessibilities will be provided "directly", nk/dz, july'18
        final DoubleMatrix2D matrix = Matrices.doubleMatrix2D(geoData.getZones().values(), geoData.getZones().values());
        for (Zone origin : geoData.getZones().values()) {
            for (Zone destination : geoData.getZones().values()) {
                matrix.setQuick(origin.getZoneId(), destination.getZoneId(), travelTimes.getTravelTime(origin, destination, TIME_OF_DAY, mode));
            }
        }
        return matrix;
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
    
    public double getPeakTravelCosts(Location i, Location j) {
        return (autoOperatingCosts / 100) * travelTimes.getTravelTime(i, j, TIME_OF_DAY, "car");
        // Take costs provided by MATSim here? Should be possible
        // without much alterations as they are part of NodeData, which is contained in MATSimTravelTimes, nk/dz, jan'18
    }
}