package de.tum.bgu.msm.data;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.HashMap;

public abstract class AbstractDefaultGeoData implements GeoData {

    private static final Logger logger = Logger.getLogger(AbstractDefaultGeoData.class);

    private final String zoneIdColumnName;
    private final String regionColumnName;


    protected int[] zoneIndex;
    protected int[] zones;
    private int highestZonalId;
    protected TableDataSet zonalData;

    protected HashMap<Integer, int[]> regionDefinition;
    protected int[] regionList;
    private int[] regionIndex;
    private TableDataSet regDef;

    protected int[] countyIndex;

    private int[] developableLUtypes;
    private TableDataSet landUse;
    private TableDataSet developmentRestrictions;
    private TableDataSet developmentCapacity;

    private boolean useCapacityAsNumberOfDwellings;

    public AbstractDefaultGeoData(String zoneIdColumnName, String regionColumnName) {
        this.zoneIdColumnName = zoneIdColumnName;
        this.regionColumnName = regionColumnName;
    }

    @Override
    public void setInitialData () {
        readZones();
        readRegionDefinition();
        readLandUse();
        readDeveloperData();
    }

    @Override
    public int[] getZones () {
        return zonalData.getColumnAsInt(zoneIdColumnName);
    }

    @Override
    public int getZoneIndex(int zone) {
        return zoneIndex[zone];
    }

    @Override
    public int getHighestZonalId () {
        return highestZonalId;
    }

    @Override
    public int[] getZonesInRegion (int region) {
        return regionDefinition.get(region);
    }

    @Override
    public int[] getRegionList() {
        return regionList;
    }

    @Override
    public int getRegionIndex(int region) {
        return regionIndex[region];
    }

    @Override
    public int getRegionOfZone (int zone) {
        return (int) regDef.getIndexedValueAt(zone, "Region");
    }

    @Override
    public float getSizeOfZoneInAcres(int zone) {
        return zonalData.getIndexedValueAt(zone, "Area");
    }

    @Override
    public float[] getSizeOfZonesInAcres() {
        float[] size = new float[getZones().length];
        for (int zone: getZones()) {
            size[getZoneIndex(zone)] = getSizeOfZoneInAcres(zone);
        }
        return size;
    }

    @Override
    public float getAreaOfLandUse (String landUseType, int zone) {
        return landUse.getIndexedValueAt(zone, landUseType);
    }

    @Override
    public int[] getDevelopableLandUseTypes() {
        return developableLUtypes;
    }

    @Override
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

    @Override
    public int getMSAOfZone(int zone) {
        return (int) zonalData.getIndexedValueAt(zone, "msa");
    }

    @Override
    public void reduceDevelopmentCapacityByOneDwelling (int zone) {
        float capacity = Math.max(getDevelopmentCapacity(zone) - 1, 0);
        developmentCapacity.setIndexedValueAt(zone, "DevCapacity", capacity);
    }

    @Override
    public float getDevelopmentCapacity (int zone) {
        return developmentCapacity.getIndexedValueAt(zone, "DevCapacity");
    }

    @Override
    public boolean useNumberOfDwellingsAsCapacity () {
        return useCapacityAsNumberOfDwellings;
    }

    private void readZones() {
        String fileName = Properties.get().main.baseDirectory + Properties.get().geo.zonalDataFile;
        zonalData = SiloUtil.readCSVfile(fileName);
        highestZonalId = SiloUtil.getHighestVal(zonalData.getColumnAsInt(zoneIdColumnName));
        zonalData.buildIndex(zonalData.getColumnPosition(zoneIdColumnName));
        zones = getZones();
        zoneIndex = SiloUtil.createIndexArray(zones);
    }

    private void readRegionDefinition() {
        String regFileName = Properties.get().main.baseDirectory + Properties.get().geo.regionDefinitionFile;
        regDef = SiloUtil.readCSVfile(regFileName);
        regionDefinition = new HashMap<>();
        for (int row = 1; row <= regDef.getRowCount(); row++) {
            int taz = (int) regDef.getValueAt(row, zoneIdColumnName);
            int reg = (int) regDef.getValueAt(row, regionColumnName);
            if (regionDefinition.containsKey(reg)) {
                int[] zoneInThisRegion = regionDefinition.get(reg);
                int[] newZones = SiloUtil.expandArrayByOneElement(zoneInThisRegion, taz);
                regionDefinition.put(reg, newZones);
            } else {
                regionDefinition.put(reg, new int[]{taz});
            }
        }
        regionList = SiloUtil.idendifyUniqueValues(regDef.getColumnAsInt(regionColumnName));
        regionIndex = SiloUtil.createIndexArray(regionList);
        regDef.buildIndex(regDef.getColumnPosition(zoneIdColumnName));
    }

    private void readDeveloperData() {
        String baseDirectory = Properties.get().main.baseDirectory;
        int startYear = Properties.get().main.startYear;
        developableLUtypes = Properties.get().geo.developableLandUseTypes;

        String restrictionsFileName = baseDirectory + Properties.get().geo.developmentRestrictionsFile;
        developmentRestrictions = SiloUtil.readCSVfile(restrictionsFileName);
        developmentRestrictions.buildIndex(developmentRestrictions.getColumnPosition("Zone"));

        useCapacityAsNumberOfDwellings = Properties.get().geo.useCapacityForDwellings;
        if (useCapacityAsNumberOfDwellings) {
            String capacityFileName;
            if (startYear == Properties.get().main.implementation.BASE_YEAR) {
                capacityFileName = baseDirectory + "input/" + Properties.get().geo.capacityFile + ".csv";
            } else {
                capacityFileName = baseDirectory + "scenOutput/" + Properties.get().main.scenarioName + "/" +
                        Properties.get().geo.capacityFile + "_" + startYear + ".csv";
            }
            developmentCapacity = SiloUtil.readCSVfile(capacityFileName);
            developmentCapacity.buildIndex(developmentCapacity.getColumnPosition("Zone"));
        }
    }

    private void readLandUse() {
        logger.info("Reading land use data");
        String fileName;
        String baseDirectory = Properties.get().main.baseDirectory;
        int startYear = Properties.get().main.startYear;
        if (startYear == Properties.get().main.implementation.BASE_YEAR) {  // start in year 2000
            fileName = baseDirectory + "input/" + Properties.get().geo.landUseAreaFile + ".csv";
        } else {                                             // start in different year (continue previous run)
            fileName = baseDirectory + "scenOutput/" + Properties.get().main.scenarioName + "/" +
                    Properties.get().geo.landUseAreaFile + "_" + startYear + ".csv";
        }
        landUse = SiloUtil.readCSVfile(fileName);
        landUse.buildIndex(landUse.getColumnPosition("Zone"));
    }

    @Override
    public void writeOutDevelopmentCapacityFile (SiloDataContainer dataContainer) {
        // write out development capacity file to allow model run to be continued from this point later

        boolean useCapacityAsNumberOfDwellings = Properties.get().geo.useCapacityForDwellings;
        String baseDirectory = Properties.get().main.baseDirectory;
        String scenarioName = Properties.get().main.scenarioName;
        int endYear = Properties.get().main.endYear;
        if(useCapacityAsNumberOfDwellings)	{
            String capacityFileName = baseDirectory + "scenOutput/" + scenarioName + "/" +
                    Properties.get().geo.capacityFile + "_" + endYear + ".csv";
            PrintWriter pwc = SiloUtil.openFileForSequentialWriting(capacityFileName, false);
            pwc.println("Zone,DevCapacity");
            for (int zone: getZones()) pwc.println(zone + "," + getDevelopmentCapacity(zone));
            pwc.close();
        }

        String landUseFileName = baseDirectory + "scenOutput/" + scenarioName + "/" +
                Properties.get().geo.landUseAreaFile + "_" + endYear + ".csv";
        PrintWriter pwl = SiloUtil.openFileForSequentialWriting(landUseFileName, false);
        pwl.println("Zone,lu41");
        for (int zone: getZones()) pwl.println(zone + "," + dataContainer.getRealEstateData().getDevelopableLand(zone));
        pwl.close();
    }

    @Override
    public boolean isThisDwellingTypeAllowed (String dwellingType, int zone) {

        int col = 0;
        try {
            col = developmentRestrictions.getColumnPosition(dwellingType);
        } catch (Exception e) {
            logger.error("Unknown DwellingType " + dwellingType + ". Cannot find it in developmentRestrictions.");
            System.exit(1);
        }
        return (developmentRestrictions.getIndexedValueAt(zone, col) == 1);
    }
}
