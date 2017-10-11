package de.tum.bgu.msm.data;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ResourceBundle;

import de.tum.bgu.msm.container.SiloDataContainer;
import org.apache.log4j.Logger;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;

import de.tum.bgu.msm.SiloUtil;

/**
 * Zonal, county and regional data used by the SILO Model
 * Author: Rolf Moeckel, University of Maryland
 * Created on 20 April 2015 in College Park
 **/

public class geoDataMstm implements GeoData {
    private static final Logger logger = Logger.getLogger(geoDataMstm.class);

    protected static final String PROPERTIES_ZONAL_DATA_FILE            = "zonal.data.file";
    protected static final String PROPERTIES_ZONAL_SCHOOL_QUALITY_INDEX = "school.quality.index";
    protected static final String PROPERTIES_COUNTY_CRIME_INDEX         = "crime.index";
    protected static final String PROPERTIES_REGION_DEF_FILE            = "region.definition.file";
    protected static final String PROPERTIES_LAND_USE_AREA              = "land.use.area.by.taz";
    protected static final String PROPERTIES_DEVELOPABLE                = "developable.lu.category";
    protected static final String PROPERTIES_DEVELOPM_RESTR             = "development.restrictions";
    protected static final String PROPERTIES_USE_CAPACITY               = "use.growth.capacity.data";
    protected static final String PROPERTIES_CAPACITY_FILE              = "growth.capacity.file";

    private ResourceBundle rb;
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
    private static TableDataSet landUse;
    private int[] developableLUtypes;
    private static TableDataSet developmentRestrictions;
    private TableDataSet developmentCapacity;
    private boolean useCapacityAsNumberOfDwellings;


    public geoDataMstm(ResourceBundle rb) {
        this.rb = rb;
    }

    public void setInitialData () {
        SiloUtil.startYear = ResourceUtil.getIntegerProperty(rb, SiloUtil.PROPERTIES_START_YEAR);
        SiloUtil.endYear = ResourceUtil.getIntegerProperty(rb, SiloUtil.PROPERTIES_END_YEAR);
        SiloUtil.simulationLength = ResourceUtil.getIntegerProperty(rb, SiloUtil.PROPERTIES_SIMULATION_PERIOD_LENGTH);
        SiloUtil.gregorianIterator = ResourceUtil.getIntegerProperty(rb, SiloUtil.PROPERTIES_GREGORIAN_ITERATOR);
        SiloUtil.incBrackets = ResourceUtil.getIntegerArray(rb, SiloUtil.PROPERTIES_INCOME_BRACKETS);
        SiloUtil.numberOfQualityLevels = ResourceUtil.getIntegerProperty(rb, SiloUtil.PROPERTIES_NUMBER_OF_DWELLING_QUALITY_LEVELS);
        readZones();
        readLandUse();
    }


    private void readZones() {
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


    private void readLandUse() {
        // read land use data
        logger.info("Reading land use data");
        String fileName;
        if (SiloUtil.startYear == SiloUtil.getBaseYear()) {  // start in year 2000
            fileName = SiloUtil.baseDirectory + "input/" + ResourceUtil.getProperty(rb, PROPERTIES_LAND_USE_AREA) + ".csv";
        } else {                                             // start in different year (continue previous run)
            fileName = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName + "/" +
                    ResourceUtil.getProperty(rb, PROPERTIES_LAND_USE_AREA) + "_" + SiloUtil.startYear + ".csv";
        }
        landUse = SiloUtil.readCSVfile(fileName);
        landUse.buildIndex(landUse.getColumnPosition("Zone"));

        // read developers data
        developableLUtypes = ResourceUtil.getIntegerArray(rb, PROPERTIES_DEVELOPABLE);

        String restrictionsFileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_DEVELOPM_RESTR);
        developmentRestrictions = SiloUtil.readCSVfile(restrictionsFileName);
        developmentRestrictions.buildIndex(developmentRestrictions.getColumnPosition("Zone"));

        useCapacityAsNumberOfDwellings = ResourceUtil.getBooleanProperty(rb, PROPERTIES_USE_CAPACITY, false);
        if (useCapacityAsNumberOfDwellings) {
            String capacityFileName;
            if (SiloUtil.startYear == SiloUtil.getBaseYear()) {  // start in year 2000
                capacityFileName = SiloUtil.baseDirectory + "input/" + ResourceUtil.getProperty(rb, PROPERTIES_CAPACITY_FILE) + ".csv";
            } else {                                             // start in different year (continue previous run)
                capacityFileName = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName + "/" +
                        ResourceUtil.getProperty(rb, PROPERTIES_CAPACITY_FILE) + "_" + SiloUtil.startYear + ".csv";
            }
            developmentCapacity = SiloUtil.readCSVfile(capacityFileName);
            developmentCapacity.buildIndex(developmentCapacity.getColumnPosition("Zone"));
        }
    }


    public boolean useNumberOfDwellingsAsCapacity () {
        return useCapacityAsNumberOfDwellings;
    }


    public float getDevelopmentCapacity (int zone) {
        return developmentCapacity.getIndexedValueAt(zone, "DevCapacity");
    }

    public void reduceDevelopmentCapacityByOneDwelling (int zone) {
        float capacity = Math.max(getDevelopmentCapacity(zone) - 1, 0);
        developmentCapacity.setIndexedValueAt(zone, "DevCapacity", capacity);
    }

    public void reduceDevelopmentCapacityByDevelopableAcres (int zone, float acres) {
        for (int type: getDevelopableLandUseTypes()) {
            String column = "LU" + type;
            float existing = landUse.getIndexedValueAt(zone, column);
            if (existing < acres) {
                landUse.setIndexedValueAt(zone, column, 0);
                acres = acres - existing;
            } else {
                float reduced = existing - acres;
                landUse.setIndexedValueAt(zone, column, reduced);
                acres = 0;
            }
            if (acres <= 0) return;
        }
    }


    public int[] getDevelopableLandUseTypes() {
        return developableLUtypes;
    }

    public void writeOutDevelopmentCapacityFile (SiloDataContainer dataContainer) {
        // write out development capacity file to allow model run to be continued from this point later

        boolean useCapacityAsNumberOfDwellings = ResourceUtil.getBooleanProperty(rb, PROPERTIES_USE_CAPACITY, false);
        if(useCapacityAsNumberOfDwellings)	{
            String capacityFileName = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName + "/" +
                    ResourceUtil.getProperty(rb, PROPERTIES_CAPACITY_FILE) + "_" + SiloUtil.getEndYear() + ".csv";
            PrintWriter pwc = SiloUtil.openFileForSequentialWriting(capacityFileName, false);
            pwc.println("Zone,DevCapacity");
            for (int zone: getZones()) pwc.println(zone + "," + getDevelopmentCapacity(zone));
            pwc.close();
        }

        String landUseFileName = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName + "/" +
                ResourceUtil.getProperty(rb, PROPERTIES_LAND_USE_AREA) + "_" + SiloUtil.getEndYear() + ".csv";
        PrintWriter pwl = SiloUtil.openFileForSequentialWriting(landUseFileName, false);
        pwl.println("Zone,lu41");
        for (int zone: getZones()) pwl.println(zone + "," + dataContainer.getRealEstateData().getDevelopableLand(zone));
        pwl.close();
    }


    public float getAreaOfLandUse (String landUseType, int zone) {
        return landUse.getIndexedValueAt(zone, landUseType);
    }

    public boolean isThisDwellingTypeAllowed (String dwellingType, int zone) {
        // return TRUE if this dwellingType can be built in this zone, otherwise return false

        int col = 0;
        try {
            col = developmentRestrictions.getColumnPosition(dwellingType);
        } catch (Exception e) {
            logger.error("Unknown DwellingType " + dwellingType + ". Cannot find it in developmentRestrictions.");
            System.exit(1);
        }
        return (developmentRestrictions.getIndexedValueAt(zone, col) == 1);
    }

    public int getHighestZonalId () {
        // return highest zone ID
        return highestZonalId;
    }

    public int[] getZones () {
        // return array with zone IDs
        return SiloUtil.zonalData.getColumnAsInt("ZoneId");
    }

    public float getSizeOfZoneInAcres(int zone) {
        return SiloUtil.zonalData.getIndexedValueAt(zone, "Area");
    }

    public float[] getSizeOfZonesInAcres() {
        float[] size = new float[getZones().length];
        for (int zone: getZones()) {
            size[getZoneIndex(zone)] = getSizeOfZoneInAcres(zone);
        }
        return size;
    }

    public static int getCountyOfZone(int zone) {
        return (int) SiloUtil.zonalData.getIndexedValueAt(zone, "COUNTYFIPS");
    }

    public int getZoneIndex(int zone) {
        return zoneIndex[zone];
    }

    public int[] getRegionList() {
        return regionList;
    }

    public int getRegionIndex(int region) {
        return regionIndex[region];
    }

    public int[] getZonesInRegion (int region) {
        return regionDefinition.get(region);
    }

    public int getRegionOfZone (int zone) {
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
