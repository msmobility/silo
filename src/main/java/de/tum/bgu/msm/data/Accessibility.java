package de.tum.bgu.msm.data;

import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.matsim.core.utils.collections.Tuple;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import com.pb.common.util.ResourceUtil;

import de.tum.bgu.msm.SiloUtil;
import omx.OmxFile;
import omx.OmxMatrix;

/**
 * Calculates and stores accessibilities
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 12 December 2012 in Santa Fe
 **/

public class Accessibility {

    protected static final String PROPERTIES_AUTO_PEAK_SKIM                  = "auto.peak.sov.skim.";
    protected static final String PROPERTIES_AUTO_PEAK_SKIM_MATRIX_NAME      = "auto.peak.sov.skim.matrix.name";
    protected static final String PROPERTIES_TRANSIT_PEAK_SKIM               = "transit.peak.time.";
    protected static final String PROPERTIES_TRANSIT_PEAK_SKIM_MATRIX_NAME   = "transit.peak.time.matrix.name";
    protected static final String PROPERTIES_AUTO_ACCESSIBILITY_ALPHA        = "auto.accessibility.alpha";
    protected static final String PROPERTIES_AUTO_ACCESSIBILITY_BETA         = "auto.accessibility.beta";
    protected static final String PROPERTIES_TRANSIT_ACCESSIBILITY_ALPHA     = "transit.accessibility.a";
    protected static final String PROPERTIES_TRANSIT_ACCESSIBILITY_BETA      = "transit.accessibility.b";
    protected static final String PROPERTIES_HTS_WORK_TLFD                   = "hts.work.tlfd";
    protected static final String PROPERTIES_AUTO_OPERATING_COSTS            = "auto.operating.costs";

    static Logger logger = Logger.getLogger(Accessibility.class);
    private ResourceBundle rb;
    private GeoData geoData;
    private static Matrix hwySkim;
    private static Matrix transitSkim;
    private static double[] autoAccessibility;
    private static double[] transitAccessibility;
    private static double[] regionalAccessibility;
    private static float[] workTLFD;
    private static float autoOperatingCosts;
    private static Matrix travelTimeToRegion;

    public Accessibility(ResourceBundle rb, int year, GeoData geoData) {
        this.rb = rb;
        this.geoData = geoData;
        readSkim(year);
        calculateAccessibilities(year);
        readWorkTripLengthFrequencyDistribution();
        calculateDistanceToRegions();
        autoOperatingCosts = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_AUTO_OPERATING_COSTS);
    }


    public void readSkim(int year) {
        // Read hwySkim matrix for year
        logger.info("Reading skims for " + year);

        String hwyFileName = SiloUtil.baseDirectory + "skims/" + rb.getString(PROPERTIES_AUTO_PEAK_SKIM + year);
        // Read highway hwySkim
        OmxFile hSkim = new OmxFile(hwyFileName);
        hSkim.openReadOnly();
        // Work-around to make sure that existing code does not break
        String matrixName = "HOVTime";
        if (rb.containsKey(PROPERTIES_AUTO_PEAK_SKIM_MATRIX_NAME)) matrixName = rb.getString(PROPERTIES_AUTO_PEAK_SKIM_MATRIX_NAME);
        OmxMatrix timeOmxSkimAutos = hSkim.getMatrix(matrixName);
        hwySkim = SiloUtil.convertOmxToMatrix(timeOmxSkimAutos);
//        TableDataSet hwySkimTbl = SiloUtil.readCSVfile(hwyFileName);
//        hwySkim = new Matrix(SiloUtil.getZones().length, SiloUtil.getZones().length);
//        hwySkim.setExternalNumbersZeroBased(SiloUtil.getZones());
//        for (int row = 1; row <= hwySkimTbl.getRowCount(); row++) {
//            int orig = (int) hwySkimTbl.getValueAt(row, "OTAZ");
//            int dest = (int) hwySkimTbl.getValueAt(row, "DTAZ");
//            if (orig > SiloUtil.getHighestZonalId() || dest > SiloUtil.getHighestZonalId()) continue;
//            hwySkim.setValueAt(orig, dest, hwySkimTbl.getValueAt(row, "time"));
//        }
        
        
        // new -- write matrix as csv file for testing
//        MatrixWriter matrixWriter = MatrixWriter.createWriter(MatrixType.CSV, new File("./info/given_impedance_" + year + ".csv"));
//        new File(SiloUtil.baseDirectory + "testing").mkdirs();
//        MatrixWriter matrixWriter = MatrixWriter.createWriter(MatrixType.CSV, new File(SiloUtil.baseDirectory + "testing/given_impedance_" + year + ".csv"));
//        matrixWriter.writeMatrix(hwySkim);
//        Log.info("For testing: Written skim out as a csv file");
        // end new
        
        
        // new -- read in matrix from csv
//        public static Matrix convertCSVToMatrix (String fileName) {
//        	
//        for (int i = 0; i < dimensions[0]; i++)
//            for (int j = 0; j < dimensions[1]; j++) {
//                mat.setValueAt(i + 1, j + 1, (float) dArray[i][j]);
////        		System.out.println("i = " + i + " ; j = " + j + " ; dArray[i][j] = " + dArray[i][j]);
//            }
//        return mat;
//        }
        // end new
        
        
        
        
        // Read transit hwySkim
        String transitFileName = SiloUtil.baseDirectory + "skims/" + rb.getString(PROPERTIES_TRANSIT_PEAK_SKIM + year);
        OmxFile tSkim = new OmxFile(transitFileName);
        tSkim.openReadOnly();
        // Work-around to make sure that existing code does not break
        String transitMatrixName = "CheapJrnyTime";
        if (rb.containsKey(PROPERTIES_TRANSIT_PEAK_SKIM_MATRIX_NAME)) transitMatrixName = rb.getString(PROPERTIES_TRANSIT_PEAK_SKIM_MATRIX_NAME);

        OmxMatrix timeOmxSkimTransit = tSkim.getMatrix(transitMatrixName);
        transitSkim = SiloUtil.convertOmxToMatrix(timeOmxSkimTransit);
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
    
    
    // new Matsim
    public void readSkimBasedOnMatsim(int year, Map<Tuple<Integer, Integer>, Float> travelTimesMap) {
        logger.info("Reading skims based on MATSim travel times for " + year);
        
        // new matrix needs to have same dimension as previous matrix
        int rowCount = hwySkim.getRowCount();
        int columnCount = hwySkim.getColumnCount();
        
//        hwySkim = SiloMatsimUtils.convertTravelTimesToImpedanceMatrix(travelTimesMap, rowCount, columnCount, year);
//
//        MatrixWriter matrixWriter = MatrixWriter.createWriter(MatrixType.CSV, new File("./info/matsim_impedance_" + year + ".csv"));
//        matrixWriter.writeMatrix(hwySkim);
        

        // Read transit hwySkim ... unchanged... see above
        // comment out ... as would also not be called in no-matsim version!!
//        String transitFileName = SiloUtil.baseDirectory + "skims/" + rb.getString(PROPERTIES_TRANSIT_PEAK_SKIM + year);
//        OmxFile tSkim = new OmxFile(transitFileName);
//        tSkim.openReadOnly();
//        OmxMatrix timeOmxSkimTransit = tSkim.getMatrix("CheapJrnyTime");
//        transitSkim = SiloUtil.convertOmxToMatrix(timeOmxSkimTransit);
    }
    // end new Matsim
    

    public static float getAutoTravelTime(int i, int j) {
        return hwySkim.getValueAt(i, j);
    }

    public static float getTransitTravelTime(int i, int j) {
        return transitSkim.getValueAt(i, j);
    }

    public static float getTravelCosts(int i, int j) {
        return (autoOperatingCosts / 100f) * hwySkim.getValueAt(i, j);
    }

    public void calculateAccessibilities (int year) {
        // Calculate Hansen TripGenAccessibility (recalculated every year)

        logger.info("  Calculating accessibilities for " + year);
        float alphaAuto = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_AUTO_ACCESSIBILITY_ALPHA);
        float betaAuto = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_AUTO_ACCESSIBILITY_BETA);
        float alphaTransit = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_TRANSIT_ACCESSIBILITY_ALPHA);
        float betaTransit = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_TRANSIT_ACCESSIBILITY_BETA);

        int[] zones = geoData.getZones();
        int[] pop = summarizeData.getPopulationByZone(geoData);
        autoAccessibility = new double[zones.length];
        transitAccessibility = new double[zones.length];
        for (int orig: zones) {
            autoAccessibility[geoData.getZoneIndex(orig)] = 0;
            transitAccessibility[geoData.getZoneIndex(orig)] = 0;
            for (int dest: zones) {
                double autoImpedance;
                if (getAutoTravelTime(orig, dest) == 0) {      // should never happen for auto
                    autoImpedance = 0;
                } else {
                    autoImpedance = Math.exp(betaAuto * getAutoTravelTime(orig, dest));
                }
                double transitImpedance;
                if (getTransitTravelTime(orig, dest) == 0) {   // zone is not connected by walk-to-transit
                    transitImpedance = 0;
                } else {
                    transitImpedance = Math.exp(betaTransit * getTransitTravelTime(orig, dest));
                }
                // dz: zone "orig" and its zoneIndex "geoDataMstm.getZoneIndex(orig)" are different!!
                // "orig" is the ID of the zone and zoneIndex is its location in the array
                // zoneIndex is "indexArray for array" zones
                autoAccessibility[geoData.getZoneIndex(orig)] += Math.pow(pop[dest], alphaAuto) * autoImpedance;
                transitAccessibility[geoData.getZoneIndex(orig)] += Math.pow(pop[dest], alphaTransit) * transitImpedance;
            }
        }
        
        
        // new -- write output
//      System.out.println("zone = " + orig + " has autoAccessibility = " + autoAccessibility[geoDataMstm.getZoneIndex(orig)]);
//      System.out.println("zone = " + orig + " has zoneIndex = " + geoDataMstm.getZoneIndex(orig))


/*  RM: Had to comment out this part because model fails when CSVFileWriter is called. Seems to be an issue within MATSim:
Exception in thread "main" java.lang.NoClassDefFoundError: org/matsim/core/utils/io/IOUtils
at de.tum.bgu.msm.transportModel.CSVFileWriter.<init>(CSVFileWriter.java:29)
at de.tum.bgu.msm.data.Accessibility.calculateAccessibilities(Accessibility.java:204)

        new File(SiloUtil.baseDirectory + "testing").mkdirs();
        CSVFileWriter accessibilityFileWriter = new CSVFileWriter(SiloUtil.baseDirectory + "testing/accessibility_" + year +".csv", ",");
		
		accessibilityFileWriter.writeField("zoneId");
		accessibilityFileWriter.writeField("autoAccessibility");
		accessibilityFileWriter.writeNewLine();

		for (int i = 0; i < zones.length; i++) {
				accessibilityFileWriter.writeField(zones[i]);
				accessibilityFileWriter.writeField(autoAccessibility[geoDataMstm.getZoneIndex(i)]);
				accessibilityFileWriter.writeNewLine();    
		}
		
		accessibilityFileWriter.close();
		Log.info("For testing: Written accessibilities out as a csv file");
		// end new
*/
		
		
        autoAccessibility = SiloUtil.scaleArray(autoAccessibility, 100);
        transitAccessibility = SiloUtil.scaleArray(transitAccessibility, 100);

        regionalAccessibility = new double[SiloUtil.getHighestVal(geoData.getRegionList()) + 1];
        for (int region: geoData.getRegionList()) {
            int[] zonesInThisRegion = geoData.getZonesInRegion(region);
            double sm = 0;
            for (int zone: zonesInThisRegion) sm += autoAccessibility[geoData.getZoneIndex(zone)];
             regionalAccessibility[region] = sm / zonesInThisRegion.length;
        }
    }


    private void readWorkTripLengthFrequencyDistribution () {
        // read HTS trip length frequency distribution for work trips

        String fileName = SiloUtil.baseDirectory + rb.getString(PROPERTIES_HTS_WORK_TLFD);
        TableDataSet tlfd = SiloUtil.readCSVfile(fileName);
        workTLFD = new float[tlfd.getRowCount() + 1];
        for (int row = 1; row <= tlfd.getRowCount(); row++) {
            int tt = (int) tlfd.getValueAt(row, "TravelTime");
            if (tt > workTLFD.length) logger.error("Inconsistent trip length frequency in " + SiloUtil.baseDirectory +
                    rb.getString(PROPERTIES_HTS_WORK_TLFD) + ": " + tt + ". Provide data in 1-min increments.");
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


    public static float getWorkTLFD (int minutes) {
        // return probability to commute 'minutes'
        if (minutes < workTLFD.length) return workTLFD[minutes];
        else return 0;
    }


    public static double getRegionalAccessibility (int region) {
        return regionalAccessibility[region];
    }


    public static float getMinDistanceFromZoneToRegion (int zone, int region) {
        return travelTimeToRegion.getValueAt(zone, region);
    }


    public static Matrix getHwySkim() {
        return hwySkim;
    }


    public static Matrix getTransitSkim() {
        return transitSkim;
    }
}
