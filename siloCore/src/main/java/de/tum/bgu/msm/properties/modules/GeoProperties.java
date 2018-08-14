package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.ResourceBundle;

public class GeoProperties {

    public final String zonalDataFile;
    public final String regionDefinitionFile;
    public final String zonalSchoolQualityFile;
    public final String countyCrimeFile;
    public final String landUseAreaFile;
    public final int[] developableLandUseTypes;
    public final String developmentRestrictionsFile;
    public final boolean useCapacityForDwellings;
    //public final String transitAccessTime;
    //public final String zonalAttributesFile;
    public final String capacityFile;
    public final String zoneShapeFile;

    public GeoProperties(ResourceBundle bundle, Implementation implementation) {
        PropertiesUtil.newPropertySubmodule("Geo properties");
        zonalDataFile = PropertiesUtil.getStringProperty(bundle, "zonal.data.file", "input/zoneSystem.csv");
        zoneShapeFile = PropertiesUtil.getStringProperty(bundle, "zones.shapefile", "input/zonesShapefile/zones.shp" );
        regionDefinitionFile = PropertiesUtil.getStringProperty(bundle, "region.definition.file", "input/regionDefinition.csv");
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


