package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.ResourceBundle;

public final class GeoProperties {

    /**
     * Zone system filepath.
     */
    public final String zonalDataFile;
    /**
     *  Zone system Shape filepath.
     */
    public final String zoneShapeFile;

    public final String zonalSchoolQualityFile;
    public final String countyCrimeFile;
    public final String landUseAreaFile;
    public final int[] developableLandUseTypes;
    public final String developmentRestrictionsFile;
    public final boolean useCapacityForDwellings;
    public final String capacityFile;


    public GeoProperties(ResourceBundle bundle) {
        PropertiesUtil.newPropertySubmodule("Geo properties");
        zonalDataFile = PropertiesUtil.getStringProperty(bundle, "zonal.data.file", "input/zoneSystem.csv");
        zoneShapeFile = PropertiesUtil.getStringProperty(bundle, "zones.shapefile", "input/zonesShapefile/zones.shp" );

        zonalSchoolQualityFile = PropertiesUtil.getStringProperty(bundle, "school.quality.index", "input/schoolQualityIndex.csv");
        countyCrimeFile = PropertiesUtil.getStringProperty(bundle, "crime.index", "input/crimeIndex.csv");
        landUseAreaFile = PropertiesUtil.getStringProperty(bundle, "land.use.area.by.taz", "landUse");
        developableLandUseTypes = PropertiesUtil.getIntPropertyArray(bundle, "developable.lu.category", new int[]{41});
        developmentRestrictionsFile = PropertiesUtil.getStringProperty(bundle, "development.restrictions", "input/developmentConstraints.csv");

        PropertiesUtil.newPropertySubmodule("Geo - grwoth capacity model?");
        useCapacityForDwellings = PropertiesUtil.getBooleanProperty(bundle, "use.growth.capacity.data", false);
        capacityFile = PropertiesUtil.getStringProperty(bundle, "growth.capacity.file", "INSERT_DEFAULT_VALUE");
    }
}


