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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Calculates and stores accessibilities
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 12 December 2012 in Santa Fe
 **/

public class Accessibility {

    private final static Logger LOGGER = Logger.getLogger(Accessibility.class);

    private GeoData geoData;
    private float[] workTLFD;
    private Table<Integer, Integer, Double> travelTimeToRegion;
    private static final double TIME_OF_DAY = 8*60.*60.;

    private final Map<String, TravelTimes> travelTimes = new LinkedHashMap<>();

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


    public Accessibility(GeoData geoData) {
        this.geoData = geoData;

        autoOperatingCosts = Properties.get().accessibility.autoOperatingCosts;

        alphaAuto = Properties.get().accessibility.alphaAuto;
        betaAuto = Properties.get().accessibility.betaAuto;
        alphaTransit = Properties.get().accessibility.alphaTransit;
        betaTransit = Properties.get().accessibility.betaTransit;

        autoAccessibilities = Matrices.doubleMatrix1D(geoData.getZones().values());
        transitAccessibilities = Matrices.doubleMatrix1D(geoData.getZones().values());
        regionalAccessibilities = Matrices.doubleMatrix1D(geoData.getRegions().values());

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

	public void initialize() {
        readWorkTripLengthFrequencyDistribution();
        calculateTravelTimesToRegions();
	}

    public void readCarSkim(int year) {
        LOGGER.info("Reading car skims for " + year);
        String hwyFileName = Properties.get().main.baseDirectory + "skims/" + Properties.get().accessibility.autoSkimFile(year);
        SkimTravelTimes SkimTravelTimes = readSkim(hwyFileName, carMatrixName);
        travelTimes.put(TransportMode.car, SkimTravelTimes);
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

    OmxMatrix timeOmxSkimTransit = omx.getMatrix(matrixName) ;
    	return new SkimTravelTimes(Matrices.convertOmxToDoubleMatrix2D(timeOmxSkimTransit, omx.getLookup("lookup1")));
    }

    public void calculateAccessibilities (int year) {
        // Calculate Hansen TripGenAccessibility (recalculated every year)

        LOGGER.info("  Calculating accessibilities for " + year);

        final DoubleMatrix2D carTravelTimesCopy = getPeakAutoTravelTimeMatrix().copy();
        final int[] population = SummarizeData.getPopulationByZone(geoData);
        carTravelTimesCopy.forEachNonZero((origin, destination, autoTravelTime) -> {
            if(!geoData.getZones().containsKey(origin) || !geoData.getZones().containsKey(destination)) {
                return 0;
            }
            double imp = Math.exp(betaAuto * autoTravelTime);
            return Math.pow(population[destination], alphaAuto) * imp;
        });

        final DoubleMatrix2D transitTravelTimesCopy = getPeakTransitTravelTimeMatrix().copy();
        transitTravelTimesCopy.forEachNonZero((origin, destination, autoTravelTime) -> {
            if(!geoData.getZones().containsKey(origin) || !geoData.getZones().containsKey(destination)) {
                return 0;
            }
            return Math.pow(population[destination], alphaTransit) * Math.exp(betaTransit * autoTravelTime);
        });

        for(int i: geoData.getZones().keySet()) {
            autoAccessibilities.setQuick(i,carTravelTimesCopy.viewRow(i).zSum());
            transitAccessibilities.setQuick(i,carTravelTimesCopy.viewRow(i).zSum());
        }
        double sumScaleFactor = autoAccessibilities.zSum();
        sumScaleFactor = 1.0 / sumScaleFactor;
        autoAccessibilities.assign(DoubleFunctions.mult(sumScaleFactor));


        geoData.getRegions().values().parallelStream().forEach(r -> {
            double sum = r.getZones().stream().mapToDouble(z -> autoAccessibilities.getQuick(z.getId())).sum();
            regionalAccessibilities.setQuick(r.getId(), sum);
        });
    }


    private void readWorkTripLengthFrequencyDistribution () {
        // read HTS trip length frequency distribution for work trips

        String fileName = Properties.get().main.baseDirectory + Properties.get().accessibility.htsWorkTLFD;
        TableDataSet tlfd = SiloUtil.readCSVfile(fileName);
        workTLFD = new float[tlfd.getRowCount() + 1];
        for (int row = 1; row <= tlfd.getRowCount(); row++) {
            int tt = (int) tlfd.getValueAt(row, "TravelTime");
            if (tt > workTLFD.length) LOGGER.error("Inconsistent trip length frequency in " + Properties.get().main.baseDirectory +
                    Properties.get().accessibility.htsWorkTLFD + ": " + tt + ". Provide data in 1-min increments.");
            workTLFD[tt] = tlfd.getValueAt(row, "utility");
        }
    }


    private void calculateTravelTimesToRegions() {
        travelTimeToRegion = ArrayTable.create(geoData.getZones().keySet(), geoData.getRegions().keySet());

        for (Zone zone: geoData.getZones().values()) {
            for(Region region: geoData.getRegions().values()) {
                double minDist = Double.MAX_VALUE;
                for(Zone zoneInRegion: region.getZones()) {
                    double travelTime = getPeakAutoTravelTime(zone.getId(), zoneInRegion.getId());
                    minDist = Math.min(minDist, travelTime);
                }
                travelTimeToRegion.put(zone.getId(), region.getId(), minDist);
            }
        }
    }

    public float getWorkTLFD (int minutes) {
        // return probability to commute 'minutes'
        if (minutes < workTLFD.length) {
            return workTLFD[minutes];
        } else {
            return 0;
        }
    }

    public double getRegionalAccessibility (int region) {
        return 0.;//accessibilityByRegion.get(region);
    }


    public float getMinTravelTimeFromZoneToRegion(int zone, int region) {
        return travelTimeToRegion.get(zone, region).floatValue();
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

    public double getAutoAccessibilityForZone(int zoneId) {
        return this.autoAccessibilities.getQuick(zoneId);
    }

    public double getTransitAccessibilityForZone(int zoneId) {
        return this.transitAccessibilities.getQuick(zoneId);
    }

    public double getPeakAutoTravelTime(int i, int j) {
        return travelTimes.get(TransportMode.car).getTravelTime(i, j, TIME_OF_DAY);
    }

    public double getPeakTransitTravelTime(int i, int j) {
        return travelTimes.get(TransportMode.pt).getTravelTime(i, j, TIME_OF_DAY);
    }

    public DoubleMatrix2D getPeakAutoTravelTimeMatrix() {
        TravelTimes tt = travelTimes.get(TransportMode.car);
        if(tt instanceof SkimTravelTimes) {
            return ((SkimTravelTimes) tt).getPeakTravelTimeMatrix();
        } else {
            final DoubleMatrix2D matrix = Matrices.doubleMatrix2D(geoData.getZones().values(), geoData.getZones().values());
            for(int origin: geoData.getZones().keySet()) {
                for(int destination: geoData.getZones().keySet()) {
                    matrix.setQuick(origin, destination, tt.getTravelTime(origin, destination, TIME_OF_DAY));
                }
            }
            return matrix;
        }
    }

    public DoubleMatrix2D getPeakTransitTravelTimeMatrix() {
        TravelTimes tt = travelTimes.get(TransportMode.pt);
        if(tt instanceof SkimTravelTimes) {
            return ((SkimTravelTimes) tt).getPeakTravelTimeMatrix();
        } else {
            final DoubleMatrix2D matrix = Matrices.doubleMatrix2D(geoData.getZones().values(), geoData.getZones().values());
            for(int origin: geoData.getZones().keySet()) {
                for(int destination: geoData.getZones().keySet()) {
                    matrix.setQuick(origin, destination, tt.getTravelTime(origin, destination, TIME_OF_DAY));
                }
            }
            return matrix;
        }
    }


    public double getPeakTravelCosts(int i, int j) {
        return (autoOperatingCosts / 100) * getPeakAutoTravelTime(i, j);
        // Take costs provided by MATSim here? Should be possible
        // without much alterations as they are part of NodeData, which is contained in MATSimTravelTimes, nk/dz, jan'18
    }
}