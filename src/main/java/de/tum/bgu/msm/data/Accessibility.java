package de.tum.bgu.msm.data;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.travelTimes.MatrixTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.transportModel.matsim.MatsimPTDistances;
import omx.OmxFile;
import omx.OmxMatrix;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.util.Collections;
import java.util.HashMap;
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
    private float[] workTLFD;
    private float autoOperatingCosts;
    private Table<Integer, Integer, Double> travelTimeToRegion;
	private final Map<String, TravelTimes> travelTimes = new LinkedHashMap<>();
	private PTDistances ptDistances;
	private static final double TIME_OF_DAY = 8*60.*60.;

	private final Map<Integer, Double> autoAccessibilityByZone = new HashMap<>();
	private final Map<Integer, Double> transitAccessibilityByZone = new HashMap<>();
	private final Map<Integer, Double> accessibilityByRegion = new HashMap<>();

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
//        hwySkim = new Matrix(SiloUtil.getZoneIdsArray().length, SiloUtil.getZoneIdsArray().length);
//        hwySkim.setExternalNumbersZeroBased(SiloUtil.getZoneIdsArray());
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
//        transitSkim = new Matrix(SiloUtil.getZoneIdsArray().length, SiloUtil.getZoneIdsArray().length);
//        transitSkim.setExternalNumbersZeroBased(SiloUtil.getZoneIdsArray());
//        for (int row = 1; row <= transitSkimTbl.getRowCount(); row++) {
//            int orig = (int) transitSkimTbl.getValueAt(row, "OTAZ");
//            int dest = (int) transitSkimTbl.getValueAt(row, "DTAZ");
//            if (orig > SiloUtil.getHighestZonalId() || dest > SiloUtil.getHighestZonalId()) continue;
//            transitSkim.setValueAt(orig, dest, transitSkimTbl.getValueAt(row, "time"));
//        }
//        for (int zn: SiloUtil.getZoneIdsArray()) transitSkim.setValueAt(zn, zn, 0);  // intrazonal distance not specified in this CUBE skim, set to 0
    }

    public double getAutoAccessibilityForZone(int zoneId) {
        return this.autoAccessibilityByZone.get(zoneId);
    }

    public double getTransitAccessibilityForZone(int zoneId) {
        return this.transitAccessibilityByZone.get(zoneId);
    }


    public float getPeakAutoTravelTime(int i, int j) {
    	return (float) travelTimes.get(TransportMode.car).getTravelTime(i, j, TIME_OF_DAY);
    }

    public float getPeakTransitTravelTime(int i, int j) {
    	return (float) travelTimes.get(TransportMode.pt).getTravelTime(i, j, TIME_OF_DAY);
    }

    public float getPeakTravelCosts(int i, int j) {
        return (autoOperatingCosts / 100f) * getPeakAutoTravelTime(i, j); // Take costs provided by MATSim here? Should be possible
        // without much alterations as they are part of NodeData, which is contained in MATSimTravelTimes, nk/dz, jan'18
    }

    public void calculateAccessibilities (int year) {
        // Calculate Hansen TripGenAccessibility (recalculated every year)

        logger.info("  Calculating accessibilities for " + year);
        final float alphaAuto = Properties.get().accessibility.alphaAuto;
        final float betaAuto = Properties.get().accessibility.betaAuto;
        final float alphaTransit = Properties.get().accessibility.alphaTransit;
        final float betaTransit = Properties.get().accessibility.betaTransit;

        int counter = 0;
        for (Zone orig: geoData.getZones().values()) {

            if(Math.log10(counter) / Math.log10(2.) % 1 == 0) {
                logger.info(counter + " accessibilities calculated");
            }
            double autoAccessibility = 0;
            double transitAccessibility = 0;

            for (Zone dest: geoData.getZones().values()) {

                double autoImpedance;
                double autoTravelTime = getPeakAutoTravelTime(orig.getId(), dest.getId());

                if (autoTravelTime == 0) {
                    autoImpedance = 0;
                    logger.debug("Auto travel time of 0 between zones " + orig.getId() + " and "
                            + dest.getId() + "! Should never happen for auto!");
                } else {
                    autoImpedance = Math.exp(betaAuto * autoTravelTime);
                }

                double transitImpedance;
                double transitTravelTime = getPeakTransitTravelTime(orig.getId(), dest.getId());

                if (transitTravelTime == 0) {
                    logger.debug("Zone " + orig.getId() + " and " + dest.getId() + ": transit travel time is zero. Assuming zones are not connected by walk-to-transit");
                    transitImpedance = 0;
                } else {
                    transitImpedance = Math.exp(betaTransit * transitTravelTime);
                }
                int population = dest.getPopulation();
                autoAccessibility += Math.pow(population, alphaAuto) * autoImpedance;
                transitAccessibility += Math.pow(population, alphaTransit) * transitImpedance;
            }

            autoAccessibilityByZone.put(orig.getId(), autoAccessibility);
            transitAccessibilityByZone.put(orig.getId(), transitAccessibility);
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
		
		
        SiloUtil.scaleMap(autoAccessibilityByZone, 100);
        SiloUtil.scaleMap(transitAccessibilityByZone, 100);

        for (Region region: geoData.getRegions().values()) {
            double regionalAccessibility = 0;
            for (Zone zone: region.getZones()) {
                regionalAccessibility += autoAccessibilityByZone.get(zone.getId());
            }
            regionalAccessibility /= region.getZones().size();
            accessibilityByRegion.put(region.getId(), regionalAccessibility);
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
        travelTimeToRegion = ArrayTable.create(geoData.getZones().keySet(), geoData.getRegions().keySet());

        for (Zone zone: geoData.getZones().values()) {
            for(Region region: geoData.getRegions().values()) {
                double minDist = Double.MAX_VALUE;
                for(Zone zoneInRegion: region.getZones()) {
                    float travelTime = getPeakAutoTravelTime(zone.getId(), zoneInRegion.getId());
                    minDist = Math.min(minDist, travelTime);
                }
                travelTimeToRegion.put(zone.getId(), region.getId(), minDist);
            }
        }
    }

    public float getWorkTLFD (int minutes) {
        // return probability to commute 'minutes'
        if (minutes < workTLFD.length) return workTLFD[minutes];
        else return 0;
    }

    public double getRegionalAccessibility (int region) {
        return accessibilityByRegion.get(region);
    }


    public float getMinTravelTimeFromZoneToRegion(int zone, int region) {
        return travelTimeToRegion.get(zone, region).floatValue();
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

	
	public void setPTDistances(MatsimPTDistances ptDistances) {
		this.ptDistances = ptDistances;
	}
}