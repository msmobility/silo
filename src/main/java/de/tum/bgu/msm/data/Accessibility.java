package de.tum.bgu.msm.data;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.travelTimes.MatrixTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
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

    public Accessibility(GeoData geoData) {
        this.geoData = geoData;
        autoOperatingCosts = Properties.get().accessibility.autoOperatingCosts;
    }

	public void initialize() {
        readWorkTripLengthFrequencyDistribution();
        calculateDistanceToRegions();
	}

    public void readCarSkim(int year) {
        // Read hwySkim matrix for year
        logger.info("Reading skims for " + year);

        String hwyFileName = Properties.get().main.baseDirectory + "skims/" + Properties.get().accessibility.autoSkimFile(year);
        // Read highway hwySkim
        OmxFile hSkim = new OmxFile(hwyFileName);
        hSkim.openReadOnly();
        // Work-around to make sure that existing code does not break
        String matrixName = "HOVTime";
        if (Properties.get().accessibility.usingAutoPeakSkim) matrixName = Properties.get().accessibility.autoPeakSkim;
        OmxMatrix timeOmxSkimAutos = hSkim.getMatrix(matrixName);
        Matrix hwySkim = SiloUtil.convertOmxToMatrix(timeOmxSkimAutos);
        travelTimes.put(TransportMode.car, new MatrixTravelTimes(hwySkim));
//        TableDataSet hwySkimTbl = SiloUtil.readCSVfile(hwyFileName);
//        hwySkim = new Matrix(SiloUtil.getZones().length, SiloUtil.getZones().length);
//        hwySkim.setExternalNumbersZeroBased(SiloUtil.getZones());
//        for (int row = 1; row <= hwySkimTbl.getRowCount(); row++) {
//            int orig = (int) hwySkimTbl.getValueAt(row, "OTAZ");
//            int dest = (int) hwySkimTbl.getValueAt(row, "DTAZ");
//            if (orig > SiloUtil.getHighestZonalId() || dest > SiloUtil.getHighestZonalId()) continue;
//            hwySkim.setValueAt(orig, dest, hwySkimTbl.getValueAt(row, "time"));
//        }
        
        
        // Write out matrix as csv file for testing
//        MatrixWriter matrixWriter = MatrixWriter.createWriter(MatrixType.CSV, new File("./info/given_impedance_" + year + ".csv"));
//        new File(Properties.get().main.baseDirectory + "testing").mkdirs();
//        MatrixWriter matrixWriter = MatrixWriter.createWriter(MatrixType.CSV, new File(Properties.get().main.baseDirectory + "testing/given_impedance_" + year + ".csv"));
//        matrixWriter.writeMatrix(hwySkim);
//        Log.info("For testing: Written skim out as a csv file");
        
        
        // Read in matrix from csv
//        public static Matrix convertCSVToMatrix (String fileName) {
//        	
//        for (int i = 0; i < dimensions[0]; i++)
//            for (int j = 0; j < dimensions[1]; j++) {
//                mat.setValueAt(i + 1, j + 1, (float) dArray[i][j]);
////        		System.out.println("i = " + i + " ; j = " + j + " ; dArray[i][j] = " + dArray[i][j]);
//            }
//        return mat;
//        }        
    }
    
    public void readPtSkim(int year) {    
        // Read transit hwySkim
        String transitFileName = Properties.get().main.baseDirectory + "skims/" + Properties.get().accessibility.transitSkimFile(year);
        OmxFile tSkim = new OmxFile(transitFileName);
        tSkim.openReadOnly();
        // Work-around to make sure that existing code does not break
        String transitMatrixName = "CheapJrnyTime";
        if (Properties.get().accessibility.usingTransitPeakSkim) transitMatrixName = Properties.get().accessibility.transitPeakSkim;

        OmxMatrix timeOmxSkimTransit = tSkim.getMatrix(transitMatrixName);
        Matrix transitSkim = SiloUtil.convertOmxToMatrix(timeOmxSkimTransit);
        travelTimes.put(TransportMode.pt, new MatrixTravelTimes(transitSkim));
//        TableDataSet transitSkimTbl = SiloUtil.readCSVfile(transitFileName);
//        transitSkim = new Matrix(SiloUtil.getZones().length, SiloUtil.getZones().length);
//        transitSkim.setExternalNumbersZeroBased(SiloUtil.getZones());
//        for (int row = 1; row <= transitSkimTbl.getRowCount(); row++) {
//            int orig = (int) transitSkimTbl.getValueAt(row, "OTAZ");
//            int dest = (int) transitSkimTbl.getValueAt(row, "DTAZ");
//            if (orig > SiloUtil.getHighestZonalId() || dest > SiloUtil.getHighestZonalId()) continue;
//            transitSkim.setValueAt(orig, dest, transitSkimTbl.getValueAt(row, "time"));
//        }
//        for (int zn: SiloUtil.getZones()) transitSkim.setValueAt(zn, zn, 0);  // intrazonal distance not specified in this CUBE skim, set to 0
    }
    

    public float getAutoTravelTime(int i, int j) {
    	return (float) travelTimes.get(TransportMode.car).getTravelTimeFromTo(i, j);
    }

    public float getTransitTravelTime(int i, int j) {
    	return (float) travelTimes.get(TransportMode.pt).getTravelTimeFromTo(i, j);
    }

    public float getTravelCosts(int i, int j) {
        return (autoOperatingCosts / 100f) * getAutoTravelTime(i, j);
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
                double autoTravelTime = getAutoTravelTime(orig, dest);
                if (autoTravelTime == 0) {      // should never happen for auto
                    autoImpedance = 0;
                } else {
                    autoImpedance = Math.exp(betaAuto * autoTravelTime);
                }
                double transitImpedance;
                double transitTravelTime = getTransitTravelTime(orig, dest);
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
        
        
        // new -- write output
//      System.out.println("zone = " + orig + " has autoAccessibility = " + autoAccessibility[GeoDataMstm.getZoneIndex(orig)]);
//      System.out.println("zone = " + orig + " has zoneIndex = " + GeoDataMstm.getZoneIndex(orig))


/*  RM: Had to comment out this part because model fails when CSVFileWriter is called. Seems to be an issue within MATSim:
Exception in thread "main" java.lang.NoClassDefFoundError: org/matsim/core/utils/io/IOUtils
at de.tum.bgu.msm.transportModel.CSVFileWriter.<init>(CSVFileWriter.java:29)
at de.tum.bgu.msm.data.Accessibility.calculateAccessibilities(Accessibility.java:204)

        new File(Properties.get().main.baseDirectory + "testing").mkdirs();
        CSVFileWriter accessibilityFileWriter = new CSVFileWriter(Properties.get().main.baseDirectory + "testing/accessibility_" + year +".csv", ",");
		
		accessibilityFileWriter.writeField("zoneId");
		accessibilityFileWriter.writeField("autoAccessibility");
		accessibilityFileWriter.writeNewLine();

		for (int i = 0; i < zones.length; i++) {
				accessibilityFileWriter.writeField(zones[i]);
				accessibilityFileWriter.writeField(autoAccessibility[GeoDataMstm.getZoneIndex(i)]);
				accessibilityFileWriter.writeNewLine();    
		}
		
		accessibilityFileWriter.close();
		Log.info("For testing: Written accessibilities out as a csv file");
		// end new
*/
		
		
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


    private void calculateDistanceToRegions () {
        // calculate the minimal distance from each zone to every region
        travelTimeToRegion = new Matrix(geoData.getZones().length, geoData.getRegionList().length);
        travelTimeToRegion.setExternalNumbersZeroBased(geoData.getZones(), geoData.getRegionList());
        for (int iz: geoData.getZones()) {
            float[] minDist = new float[SiloUtil.getHighestVal(geoData.getRegionList())+1];
            for (int i = 0; i < minDist.length; i++) minDist[i] = Float.MAX_VALUE;
            for (int jz: geoData.getZones()) {
                int region = geoData.getRegionOfZone(jz);
                float travelTime = getAutoTravelTime(iz, jz);
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
        if (minutes < workTLFD.length) return workTLFD[minutes];
        else return 0;
    }


    public double getRegionalAccessibility (int region) {
        return regionalAccessibility[geoData.getRegionIndex(region)];
    }


    public float getMinDistanceFromZoneToRegion (int zone, int region) {
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
}