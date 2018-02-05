package de.tum.bgu.msm.data;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.travelTimes.MatrixTravelTimes;
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

    static Logger logger = Logger.getLogger(Accessibility.class);
    private GeoData geoData;
    private double[] autoAccessibility;
    private double[] transitAccessibility;
    private double[] regionalAccessibility;
    private float[] workTLFD;
    private float autoOperatingCosts;
    private Matrix travelTimeToRegion;
	private final Map<String, TravelTimes> travelTimes = new LinkedHashMap<>();
	private PTDistances ptDistances;
	private static final double TIME_OF_DAY = 8*60.*60.;

    public Accessibility(GeoData geoData) {
        this.geoData = geoData;
        autoOperatingCosts = Properties.get().accessibility.autoOperatingCosts;
    }

	public void initialize() {
        readWorkTripLengthFrequencyDistribution();
        calculateTravelTimesToRegions();
	}

    public void readCarSkim(int year) {
        logger.info("Reading skims for " + year);
        String hwyFileName = Properties.get().main.baseDirectory + "skims/" + Properties.get().accessibility.autoSkimFile(year);

        // Work-around to make sure that existing code does not break
        String matrixName = "HOVTime";
        if (Properties.get().accessibility.usingAutoPeakSkim) {
            matrixName = Properties.get().accessibility.autoPeakSkim;
        }
        MatrixTravelTimes matrixTravelTimes = readSkim(hwyFileName, matrixName);
        travelTimes.put(TransportMode.car, matrixTravelTimes);
    }
    
    public void readPtSkim(int year) {    
        String transitFileName = Properties.get().main.baseDirectory + "skims/" + Properties.get().accessibility.transitSkimFile(year);
        // Work-around to make sure that existing code does not break
        String transitMatrixName = "CheapJrnyTime";
        if (Properties.get().accessibility.usingTransitPeakSkim) {
            transitMatrixName = Properties.get().accessibility.transitPeakSkim;
        }
        MatrixTravelTimes matrixTravelTimes = readSkim(transitFileName, transitMatrixName);
        travelTimes.put(TransportMode.pt, matrixTravelTimes);
    }

    private MatrixTravelTimes readSkim(String fileName, String matrixName) {
        OmxFile omx = new OmxFile(fileName);
        omx.openReadOnly();
        OmxMatrix timeOmxSkimTransit = omx.getMatrix(matrixName);
        return new MatrixTravelTimes(Matrices.convertOmxToFloatMatrix2D(timeOmxSkimTransit, omx.getLookup("lookup1")));
    }
    

    public float getPeakAutoTravelTime(int i, int j) {
    	return (float) travelTimes.get(TransportMode.car).getTravelTime(i, j, TIME_OF_DAY);
    }

    public float getPeakTransitTravelTime(int i, int j) {
    	return (float) travelTimes.get(TransportMode.pt).getTravelTime(i, j, TIME_OF_DAY);
    }

    public float getPeakTravelCosts(int i, int j) {
        return (autoOperatingCosts / 100f) * getPeakAutoTravelTime(i, j);
        // Take costs provided by MATSim here? Should be possible
        // without much alterations as they are part of NodeData, which is contained in MATSimTravelTimes, nk/dz, jan'18
    }

    public void calculateAccessibilities (int year) {
        // Calculate Hansen TripGenAccessibility (recalculated every year)

        logger.info("  Calculating accessibilities for " + year);
        float alphaAuto = Properties.get().accessibility.alphaAuto;
        float betaAuto = Properties.get().accessibility.betaAuto;
        float alphaTransit = Properties.get().accessibility.alphaTransit;
        float betaTransit = Properties.get().accessibility.betaTransit;

        int[] zones = geoData.getZones();
        int[] pop = SummarizeData.getPopulationByZone(geoData);
        autoAccessibility = new double[zones.length];
        transitAccessibility = new double[zones.length];
        int counter = 0;
        for (int orig: zones) {
            if(Math.log10(counter) / Math.log10(2.) % 1 == 0) {
                logger.info(counter + " accessibilities calculated");
            }
            autoAccessibility[geoData.getZoneIndex(orig)] = 0;
            transitAccessibility[geoData.getZoneIndex(orig)] = 0;
            for (int dest: zones) {
                double autoImpedance;
                double autoTravelTime = getPeakAutoTravelTime(orig, dest);
                if (autoTravelTime == 0) {      // should never happen for auto
                    autoImpedance = 0;
                } else {
                    autoImpedance = Math.exp(betaAuto * autoTravelTime);
                }
                double transitImpedance;
                double transitTravelTime = getPeakTransitTravelTime(orig, dest);
                if (transitTravelTime == 0) {   // zone is not connected by walk-to-transit
                    transitImpedance = 0;
                } else {
                    transitImpedance = Math.exp(betaTransit * transitTravelTime);
                }
                // dz: zone "orig" and its zoneIndex "GeoDataMstm.getZoneIndex(orig)" are different!!
                // "orig" is the ID of the zone and zoneIndex is its location in the array
                // zoneIndex is "indexArray for array" zones
                autoAccessibility[geoData.getZoneIndex(orig)] += Math.pow(pop[dest], alphaAuto) * autoImpedance;
                transitAccessibility[geoData.getZoneIndex(orig)] += Math.pow(pop[dest], alphaTransit) * transitImpedance;
            }
            counter++;
        }
		
        autoAccessibility = SiloUtil.scaleArray(autoAccessibility, 100);
        transitAccessibility = SiloUtil.scaleArray(transitAccessibility, 100);

        regionalAccessibility = new double[geoData.getRegionList().length];
        for (int region: geoData.getRegionList()) {
            int[] zonesInThisRegion = geoData.getZonesInRegion(region);
            double sm = 0;
            for (int zone: zonesInThisRegion) sm += autoAccessibility[geoData.getZoneIndex(zone)];
             regionalAccessibility[geoData.getRegionIndex(region)] = sm / zonesInThisRegion.length;
        }
    }


    private void readWorkTripLengthFrequencyDistribution () {
        // read HTS trip length frequency distribution for work trips

        String fileName = Properties.get().main.baseDirectory + Properties.get().accessibility.htsWorkTLFD;
        TableDataSet tlfd = SiloUtil.readCSVfile(fileName);
        workTLFD = new float[tlfd.getRowCount() + 1];
        for (int row = 1; row <= tlfd.getRowCount(); row++) {
            int tt = (int) tlfd.getValueAt(row, "TravelTime");
            if (tt > workTLFD.length) logger.error("Inconsistent trip length frequency in " + Properties.get().main.baseDirectory +
                    Properties.get().accessibility.htsWorkTLFD + ": " + tt + ". Provide data in 1-min increments.");
            workTLFD[tt] = tlfd.getValueAt(row, "utility");
        }
    }


    private void calculateTravelTimesToRegions() {
        travelTimeToRegion = new Matrix(geoData.getZones().length, geoData.getRegionList().length);
        travelTimeToRegion.setExternalNumbersZeroBased(geoData.getZones(), geoData.getRegionList());
        for (int iz: geoData.getZones()) {
            float[] minDist = new float[SiloUtil.getHighestVal(geoData.getRegionList())+1];
            for (int i = 0; i < minDist.length; i++) minDist[i] = Float.MAX_VALUE;
            for (int jz: geoData.getZones()) {
                int region = geoData.getRegionOfZone(jz);
                float travelTime = getPeakAutoTravelTime(iz, jz);
                minDist[region] = Math.min(minDist[region], travelTime);
            }
            for (int region: geoData.getRegionList()) travelTimeToRegion.setValueAt(iz, region, minDist[region]);
        }
    }


    public double getAutoAccessibility(int zone) {
        return autoAccessibility[geoData.getZoneIndex(zone)];
    }

    public double getTransitAccessibility(int zone) {
        return transitAccessibility[geoData.getZoneIndex(zone)];
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
        return regionalAccessibility[geoData.getRegionIndex(region)];
    }


    public float getMinTravelTimeFromZoneToRegion(int zone, int region) {
        return travelTimeToRegion.getValueAt(zone, region);
    }


	public Map<String, TravelTimes> getTravelTimes() {
		return Collections.unmodifiableMap(travelTimes);
	}
	
	
	public void addTravelTimeForMode(String mode, TravelTimes travelTimes) {
		if (mode == null) {
			logger.fatal("Mode is null. Aborting...", new RuntimeException());
		}
		if (travelTimes == null) {
			logger.fatal("TravelTimes object is null. Aborting...", new RuntimeException());
		}
		if (this.travelTimes.containsKey(mode)) {
			logger.info("Replace travel time for " + mode + " mode.");
		}
		this.travelTimes.put(mode, travelTimes);
	}

	
	public void setPTDistances(PTDistances ptDistances) {
		this.ptDistances = ptDistances;
	}

	public PTDistances getPtDistances() {
        return this.ptDistances;
    }
}