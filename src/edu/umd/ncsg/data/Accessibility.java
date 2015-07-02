package edu.umd.ncsg.data;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SiloUtil;
import omx.OmxFile;
import omx.OmxMatrix;
import org.apache.log4j.Logger;

import java.util.ResourceBundle;

/**
 * Calculates and stores accessibilities
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 12 December 2012 in Santa Fe
 **/

public class Accessibility {

    protected static final String PROPERTIES_AUTO_PEAK_SKIM                  = "auto.peak.sov.skim.";
    protected static final String PROPERTIES_TRANSIT_PEAK_SKIM               = "transit.peak.time.";
    protected static final String PROPERTIES_AUTO_ACCESSIBILITY_ALPHA        = "auto.accessibility.alpha";
    protected static final String PROPERTIES_AUTO_ACCESSIBILITY_BETA         = "auto.accessibility.beta";
    protected static final String PROPERTIES_TRANSIT_ACCESSIBILITY_ALPHA     = "transit.accessibility.a";
    protected static final String PROPERTIES_TRANSIT_ACCESSIBILITY_BETA      = "transit.accessibility.b";
    protected static final String PROPERTIES_HTS_WORK_TLFD                   = "hts.work.tlfd";
    protected static final String PROPERTIES_AUTO_OPERATING_COSTS            = "auto.operating.costs";

    static Logger logger = Logger.getLogger(Accessibility.class);
    private ResourceBundle rb;
    private static Matrix hwySkim;
    private static Matrix transitSkim;
    private static double[] autoAccessibility;
    private static double[] transitAccessibility;
    private static double[] regionalAccessibility;
    private static float[] workTLFD;
    private static float autoOperatingCosts;
    private static Matrix travelTimeToRegion;

    public Accessibility(ResourceBundle rb, int year) {
        this.rb = rb;
        readSkim(year);
        calculateAccessibilities(year);
        readWorkTripLengthFrequencyDistribution();
        calculateDistanceToRegions();
        autoOperatingCosts = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_AUTO_OPERATING_COSTS);
    }


    public void readSkim(int year) {
        // Read hwySkim matrix for year
        logger.info("  Reading skims for " + year);

        String hwyFileName = SiloUtil.baseDirectory + "skims/" + rb.getString(PROPERTIES_AUTO_PEAK_SKIM + year);
        // Read highway hwySkim
        OmxFile hSkim = new OmxFile(hwyFileName);
        hSkim.openReadOnly();
        OmxMatrix timeOmxSkimAutos = hSkim.getMatrix("HOVTime");
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
        // Read transit hwySkim
        String transitFileName = SiloUtil.baseDirectory + "skims/" + rb.getString(PROPERTIES_TRANSIT_PEAK_SKIM + year);
        OmxFile tSkim = new OmxFile(transitFileName);
        tSkim.openReadOnly();
        OmxMatrix timeOmxSkimTransit = tSkim.getMatrix("CheapJrnyTime");
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
        // Calculate Hansen Accessibility (recalculated every year)

        logger.info("  Calculating accessibilities for " + year);
        float alphaAuto = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_AUTO_ACCESSIBILITY_ALPHA);
        float betaAuto = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_AUTO_ACCESSIBILITY_BETA);
        float alphaTransit = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_TRANSIT_ACCESSIBILITY_ALPHA);
        float betaTransit = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_TRANSIT_ACCESSIBILITY_BETA);

        int[] zones = geoData.getZones();
        int[] pop = summarizeData.getPopulationByZone();
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
                autoAccessibility[geoData.getZoneIndex(orig)] += Math.pow(pop[dest], alphaAuto) * autoImpedance;
                transitAccessibility[geoData.getZoneIndex(orig)] += Math.pow(pop[dest], alphaTransit) * transitImpedance;
            }
        }
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


    public static double getAutoAccessibility(int zone) {
        return autoAccessibility[geoData.getZoneIndex(zone)];
    }
    public static double getTransitAccessibility(int zone) {
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
}
