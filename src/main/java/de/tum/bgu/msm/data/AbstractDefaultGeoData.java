package de.tum.bgu.msm.data;


import com.pb.common.datafile.TableDataSet;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractDefaultGeoData implements GeoData {

    private static final Logger logger = Logger.getLogger(AbstractDefaultGeoData.class);

    protected final String zoneIdColumnName;
    protected final String regionColumnName;
    private final String shapeIdentifier;

    protected final Map<Integer, Zone> zones = new LinkedHashMap<>();
    protected final Map<Integer, Region> regions = new LinkedHashMap<>();
    public Map<Integer, SimpleFeature> zoneFeatureMap = new LinkedHashMap<>();;

    private int[] developableLUtypes;
    private TableDataSet landUse;
    private TableDataSet developmentRestrictions;
    private TableDataSet developmentCapacity;

    private boolean useCapacityAsNumberOfDwellings;

    public AbstractDefaultGeoData(String zoneIdColumnName, String regionColumnName, String shapeIdentifier) {
        this.zoneIdColumnName = zoneIdColumnName;
        this.regionColumnName = regionColumnName;
        this.shapeIdentifier = shapeIdentifier;
    }

    @Override
    public void readData() {
        readZones();
        readShapes();
        readRegionDefinition();
        readLandUse();
        readDeveloperData();
    }

	private void readShapes() {
		String zoneShapeFile = Properties.get().geo.zoneShapeFile;
		if(zoneShapeFile == null) {
		    logger.error("No shape file found!");
		    throw new RuntimeException("No shape file found!");
        }
        for (SimpleFeature feature: ShapeFileReader.getAllFeatures(Properties.get().main.baseDirectory + zoneShapeFile)) {
            int zoneId = Integer.parseInt(feature.getAttribute(shapeIdentifier).toString());
            Zone zone = zones.get(zoneId);
            if (zone != null){
                zone.setZoneFeature(feature);
                zoneFeatureMap.put(zoneId,feature);
            }else{
                logger.warn("zoneId: " + zoneId + " does not exist in silo zone system");
            }
        }
	}

    @Override
    public Map<Integer, Zone> getZones() {
        return Collections.unmodifiableMap(zones);
    }


    @Override
    public Map<Integer, Region> getRegions() {
        return Collections.unmodifiableMap(regions);
    }

    public Map<Integer, SimpleFeature> getZoneFeatureMap() {
        return this.zoneFeatureMap;
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


    protected abstract void readZones();

    protected abstract void readRegionDefinition();

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
            for (int zone: zones.keySet()) {
                pwc.println(zone + "," + getDevelopmentCapacity(zone));
            }
            pwc.close();
        }

        String landUseFileName = baseDirectory + "scenOutput/" + scenarioName + "/" +
                Properties.get().geo.landUseAreaFile + "_" + endYear + ".csv";
        PrintWriter pwl = SiloUtil.openFileForSequentialWriting(landUseFileName, false);
        pwl.println("Zone,lu41");
        for (int zone: zones.keySet()) {
            pwl.println(zone + "," + dataContainer.getRealEstateData().getDevelopableLand(zone));
        }
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