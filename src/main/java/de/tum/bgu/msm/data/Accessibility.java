package de.tum.bgu.msm.data;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.jet.math.tdouble.DoubleFunctions;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.Matrices;
import omx.OmxFile;
import omx.OmxMatrix;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Calculates and stores accessibilities
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 12 December 2012 in Santa Fe
 **/

public class Accessibility {

    private static final Logger LOGGER = Logger.getLogger(Accessibility.class);
    private static final double TIME_OF_DAY = 8 * 60. * 60.;

    private final GeoData geoData;

    private final Map<String, TravelTimes> travelTimes = new LinkedHashMap<>();

    private final Table<Integer, Integer, Double> travelTimeToRegion;

    private final DoubleMatrix1D autoAccessibilities;
    private final DoubleMatrix1D transitAccessibilities;
    private final DoubleMatrix1D regionalAccessibilities;

    private final float autoOperatingCosts;
    private final float alphaAuto;
    private final float betaAuto;
    private final float alphaTransit;
    private final float betaTransit;

    private final String transitMatrixName;
    private final String carMatrixName;

    private float[] workTLFD;

    public Accessibility(GeoData geoData) {
        this.geoData = geoData;
        this.travelTimeToRegion = ArrayTable.create(geoData.getZones().keySet(), geoData.getRegions().keySet());

        this.autoOperatingCosts = Properties.get().accessibility.autoOperatingCosts;
        this.alphaAuto = Properties.get().accessibility.alphaAuto;
        this.betaAuto = Properties.get().accessibility.betaAuto;
        this.alphaTransit = Properties.get().accessibility.alphaTransit;
        this.betaTransit = Properties.get().accessibility.betaTransit;

        this.autoAccessibilities = Matrices.doubleMatrix1D(geoData.getZones().values());
        this.transitAccessibilities = Matrices.doubleMatrix1D(geoData.getZones().values());
        this.regionalAccessibilities = Matrices.doubleMatrix1D(geoData.getRegions().values());

        // Work-around to make sure that existing code does not break
        if (Properties.get().accessibility.usingTransitPeakSkim) {
            transitMatrixName = Properties.get().accessibility.transitPeakSkim;
        } else {
            transitMatrixName = "CheapJrnyTime";
        }

        // Work-around to make sure that existing code does not break
        if (Properties.get().accessibility.usingAutoPeakSkim) {
            carMatrixName = Properties.get().accessibility.autoPeakSkim;
        } else {
            carMatrixName = "HOVTime";
        }
    }

    /**
     * Initializes the accessibility object by reading trip length distributions
     * and zone to region travel times. Travel times should therefore
     * be read/updated _BEFORE_ this method is called.
     */
    public void initialize() {
        LOGGER.info("Initializing trip length frequency distributions");
        readWorkTripLengthFrequencyDistribution();
        LOGGER.info("Initializing travel times to regions");
        calculateTravelTimesToRegions();
    }

    public void readCarSkim(int year) {
        LOGGER.info("Reading car skims for " + year);
        String hwyFileName = Properties.get().main.baseDirectory + "skims/" + Properties.get().accessibility.autoSkimFile(year);
        SkimTravelTimes skimTravelTimes = readSkim(hwyFileName, carMatrixName);
        travelTimes.put(TransportMode.car, skimTravelTimes);
    }

    public void readPtSkim(int year) {
        LOGGER.info("Reading transit skims for " + year);
        String transitFileName = Properties.get().main.baseDirectory + "skims/" + Properties.get().accessibility.transitSkimFile(year);
        SkimTravelTimes SkimTravelTimes = readSkim(transitFileName, transitMatrixName);
        travelTimes.put(TransportMode.pt, SkimTravelTimes);
    }

    private SkimTravelTimes readSkim(String fileName, String matrixName) {
        OmxFile omx = new OmxFile(fileName);
        omx.openReadOnly();
        OmxMatrix timeOmxSkimTransit = omx.getMatrix(matrixName);
        return new SkimTravelTimes(Matrices.convertOmxToDoubleMatrix2D(timeOmxSkimTransit));
    }

    public void calculateHansenAccessibilities(int year) {

        LOGGER.info("  Calculating accessibilities for " + year);
        final DoubleMatrix1D population = SummarizeData.getPopulationByZone(geoData);

        LOGGER.info("  Calculating zone zone accessibilities: auto");
        final DoubleMatrix2D autoAccessZoneToZone =
                calculateZoneToZoneAccessibilities(population, getPeakTravelTimeMatrix(TransportMode.car), alphaAuto, betaAuto);
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
            double sum = r.getZones().stream().mapToDouble(z -> autoAccessibilities.getQuick(z.getId())).sum() / r.getZones().size();
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
                Math.pow(population.getQuick(destination), alpha) * Math.exp(beta * travelTime));
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

    private void calculateTravelTimesToRegions() {
        geoData.getZones().values(). forEach( z -> {
            geoData.getRegions().values().stream().forEach( r -> {
                double min = r.getZones().stream().mapToDouble(zoneInRegion ->
                        getPeakAutoTravelTime(z.getId(), zoneInRegion.getId())).min().getAsDouble();
                travelTimeToRegion.put(z.getId(), r.getId(), min);
            });
        });
    }

    public float getCommutingTimeProbability(int minutes) {
        if (minutes < workTLFD.length) {
            return workTLFD[minutes];
        } else {
            return 0;
        }
    }

    public Map<String, TravelTimes> getTravelTimes() {
        return Collections.unmodifiableMap(travelTimes);
    }

    public void addTravelTimeForMode(String mode, TravelTimes travelTimes) {
        if (mode == null) {
            LOGGER.fatal("Mode is null. Aborting...", new RuntimeException());
        }
        if (travelTimes == null) {
            LOGGER.fatal("TravelTimes object is null. Aborting...", new RuntimeException());
        }
        if (this.travelTimes.containsKey(mode)) {
            LOGGER.info("Replace travel time for " + mode + " mode.");
        }
        this.travelTimes.put(mode, travelTimes);
    }

    private DoubleMatrix2D getPeakTravelTimeMatrix(String forMode) {
        TravelTimes tt = travelTimes.get(forMode);
        if (tt instanceof SkimTravelTimes) {
            return ((SkimTravelTimes) tt).getPeakTravelTimeMatrix();
        } else {
            final DoubleMatrix2D matrix = Matrices.doubleMatrix2D(geoData.getZones().values(), geoData.getZones().values());
            for (int origin : geoData.getZones().keySet()) {
                for (int destination : geoData.getZones().keySet()) {
                    matrix.setQuick(origin, destination, tt.getTravelTime(origin, destination, TIME_OF_DAY));
                }
            }
            return matrix;
        }
    }

    public double getAutoAccessibilityForZone(int zoneId) {
        return this.autoAccessibilities.getQuick(zoneId);
    }

    public double getTransitAccessibilityForZone(int zoneId) {
        return this.transitAccessibilities.getQuick(zoneId);
    }

    public double getRegionalAccessibility(int region) {
        return regionalAccessibilities.getQuick(region);
    }

    public double getPeakAutoTravelTime(int i, int j) {
        return travelTimes.get(TransportMode.car).getTravelTime(i, j, TIME_OF_DAY);
    }

    public float getMinTravelTimeFromZoneToRegion(int zone, int region) {
        return travelTimeToRegion.get(zone, region).floatValue();
    }

    public double getPeakTransitTravelTime(int i, int j) {
        return travelTimes.get(TransportMode.pt).getTravelTime(i, j, TIME_OF_DAY);
    }

    public double getPeakTravelCosts(int i, int j) {
        return (autoOperatingCosts / 100) * getPeakAutoTravelTime(i, j);
        // Take costs provided by MATSim here? Should be possible
        // without much alterations as they are part of NodeData, which is contained in MATSimTravelTimes, nk/dz, jan'18
    }
}