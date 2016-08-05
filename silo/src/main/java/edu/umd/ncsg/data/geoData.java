package edu.umd.ncsg.data;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SiloUtil;

import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Zonal, county and regional data used by the SILO Model
 * Author: Rolf Moeckel, University of Maryland
 * Created on 20 April 2015 in College Park
 **/

public class geoData {

    protected static final String PROPERTIES_ZONAL_DATA_FILE                   = "zonal.data.file";
    protected static final String PROPERTIES_ZONAL_SCHOOL_QUALITY_INDEX        = "school.quality.index";
    protected static final String PROPERTIES_COUNTY_CRIME_INDEX                = "crime.index";
    protected static final String PROPERTIES_REGION_DEF_FILE                   = "region.definition.file";

    private static int[] zoneIndex;
    private static int highestZonalId;
    private static HashMap<Integer, int[]> regionDefinition;
    private static int[] regionList;
    private static int[] regionIndex;
    private static TableDataSet regDef;
    private static int[] counties;
    private static int[] countyIndex;
    private static float[] zonalSchoolQuality;
    private static float[] regionalSchoolQuality;
    private static float[] countyCrimeRate;
    private static float[] regionalCrimeRate;


    public static void setInitialData (ResourceBundle rb) {
        SiloUtil.startYear = ResourceUtil.getIntegerProperty(rb, SiloUtil.PROPERTIES_START_YEAR);
        SiloUtil.endYear = ResourceUtil.getIntegerProperty(rb, SiloUtil.PROPERTIES_END_YEAR);
        SiloUtil.simulationLength = ResourceUtil.getIntegerProperty(rb, SiloUtil.PROPERTIES_SIMULATION_PERIOD_LENGTH);
        SiloUtil.gregorianIterator = ResourceUtil.getIntegerProperty(rb, SiloUtil.PROPERTIES_GREGORIAN_ITERATOR);
        SiloUtil.incBrackets = ResourceUtil.getIntegerArray(rb, SiloUtil.PROPERTIES_INCOME_BRACKETS);
        SiloUtil.numberOfQualityLevels = ResourceUtil.getIntegerProperty(rb, SiloUtil.PROPERTIES_NUMBER_OF_DWELLING_QUALITY_LEVELS);
        readZones(rb);
    }


    private static void readZones(ResourceBundle rb) {
        // read zonal data
        String fileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_ZONAL_DATA_FILE);
        SiloUtil.zonalData = SiloUtil.readCSVfile(fileName);
        highestZonalId = SiloUtil.getHighestVal(SiloUtil.zonalData.getColumnAsInt("ZoneId"));
        SiloUtil.zonalData.buildIndex(SiloUtil.zonalData.getColumnPosition("ZoneId"));

        int[] zones = getZones();
        zoneIndex = SiloUtil.createIndexArray(zones);

        // read region definition
        String regFileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_REGION_DEF_FILE);
        regDef = SiloUtil.readCSVfile(regFileName);
        regionDefinition = new HashMap<>();
        for (int row = 1; row <= regDef.getRowCount(); row++) {
            int taz = (int) regDef.getValueAt(row, "ZoneId");
            int reg = (int) regDef.getValueAt(row, "Region");
            if (regionDefinition.containsKey(reg)) {
                int[] zoneInThisRegion = regionDefinition.get(reg);
                int[] newZones = SiloUtil.expandArrayByOneElement(zoneInThisRegion, taz);
                regionDefinition.put(reg, newZones);
            } else {
                regionDefinition.put(reg, new int[]{taz});
            }
        }
        regionList = SiloUtil.idendifyUniqueValues(regDef.getColumnAsInt("Region"));
        regionIndex = SiloUtil.createIndexArray(regionList);
        regDef.buildIndex(regDef.getColumnPosition("ZoneId"));

        // read school quality
        String sqFileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_ZONAL_SCHOOL_QUALITY_INDEX);
        TableDataSet tblSchoolQualityIndex = SiloUtil.readCSVfile(sqFileName);
        zonalSchoolQuality = new float[zones.length];
        for (int row = 1; row <= tblSchoolQualityIndex.getRowCount(); row++) {
            int taz = (int) tblSchoolQualityIndex.getValueAt(row, "Zone");
            zonalSchoolQuality[zoneIndex[taz]] = tblSchoolQualityIndex.getValueAt(row, "SchoolQualityIndex");
        }
        regionalSchoolQuality = new float[SiloUtil.getHighestVal(regionList) + 1];
        for (int zone: zones) {
            int reg = getRegionOfZone(zone);
            regionalSchoolQuality[reg] += getZonalSchoolQuality(zone);
        }
        for (int region: regionList)
            regionalSchoolQuality[region] = regionalSchoolQuality[region] / regionDefinition.get(region).length;

        // create list of county FIPS codes
        counties = SiloUtil.idendifyUniqueValues(SiloUtil.zonalData.getColumnAsInt("COUNTYFIPS"));
        countyIndex = SiloUtil.createIndexArray(counties);

        // read county-level crime data
        countyCrimeRate = new float[counties.length];
        String crimeFileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_COUNTY_CRIME_INDEX);
        TableDataSet tblCrimeIndex = SiloUtil.readCSVfile(crimeFileName);
        for (int row = 1; row <= tblCrimeIndex.getRowCount(); row++) {
            int county = (int) tblCrimeIndex.getValueAt(row, "FIPS");
            countyCrimeRate[countyIndex[county]] = tblCrimeIndex.getValueAt(row, "CrimeIndicator");
        }
        regionalCrimeRate = new float[SiloUtil.getHighestVal(regionList) + 1];
        float[] regionalArea = new float[SiloUtil.getHighestVal(regionList) + 1];
        for (int zone: zones) {
            int reg = getRegionOfZone(zone);
            int fips = getCountyOfZone(zone);
            regionalCrimeRate[reg] += countyCrimeRate[countyIndex[fips]] * getSizeOfZoneInAcres(zone);  // weight by bedrooms
            regionalArea[reg] += getSizeOfZoneInAcres(zone);
        }
        for (int region: regionList)
            regionalCrimeRate[region] = regionalCrimeRate[region] / regionalArea[region];
    }

    public static int getHighestZonalId () {
        // return highest zone ID
        return highestZonalId;
    }

    public static int[] getZones () {
        // return array with zone IDs
        return SiloUtil.zonalData.getColumnAsInt("ZoneId");
    }

    public static float getSizeOfZoneInAcres(int zone) {
        return SiloUtil.zonalData.getIndexedValueAt(zone, "ACRES");
    }

    public static int getCountyOfZone(int zone) {
        return (int) SiloUtil.zonalData.getIndexedValueAt(zone, "COUNTYFIPS");
    }

    public static int getZoneIndex(int zone) {
        return zoneIndex[zone];
    }

    public static int[] getRegionList() {
        return regionList;
    }

    public static int getRegionIndex(int region) {
        return regionIndex[region];
    }

    public static int[] getZonesInRegion (int region) {
        return regionDefinition.get(region);
    }

    public static int getRegionOfZone (int zone) {
        return (int) regDef.getIndexedValueAt(zone, "Region");
    }

    public static int getPUMAofZone (int taz) {
        // return PUMA in which taz is located
        return (int) SiloUtil.zonalData.getIndexedValueAt(taz, "PUMA");
    }

    public static int getMSAOfZone (int zone) {
        return (int) SiloUtil.zonalData.getIndexedValueAt(zone, "msa");
    }

    public static int getSimplifiedPUMAofZone (int taz) {
        // return PUMA in which taz is located (less geographic detail, last digit is rounded to 1)
        return (int) SiloUtil.zonalData.getIndexedValueAt(taz, "simplifiedPUMA");
    }

    public static float getZonalSchoolQuality (int zone) {
        return zonalSchoolQuality[zoneIndex[zone]];
    }

    public static float getRegionalSchoolQuality (int region) {
        return regionalSchoolQuality[region];
    }

    public static float getCountyCrimeRate (int fips) {
        return countyCrimeRate[countyIndex[fips]];
    }

    public static float getRegionalCrimeRate (int region) {
        return regionalCrimeRate[region];
    }


}
