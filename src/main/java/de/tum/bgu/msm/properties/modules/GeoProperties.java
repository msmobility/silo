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
        PropertiesUtil.printOutModuleTitle("Geo properties");
        zonalDataFile = PropertiesUtil.getStringProperty(bundle, "zonal.data.file", "input/zoneSystem.csv");
        zoneShapeFile = PropertiesUtil.getStringProperty(bundle, "zones.shapefile", "input/zonesShapefile/zones.shp" );
        regionDefinitionFile = PropertiesUtil.getStringProperty(bundle, "region.definition.file", "input/regionDefinition.csv");
        zonalSchoolQualityFile = PropertiesUtil.getStringProperty(bundle, "school.quality.index", "input/schoolQualityIndex.csv");
        countyCrimeFile = PropertiesUtil.getStringProperty(bundle, "crime.index", "input/crimeIndex.csv");
        landUseAreaFile = PropertiesUtil.getStringProperty(bundle, "land.use.area.by.taz", "landUse");
        developableLandUseTypes = PropertiesUtil.getIntPropertyArray(bundle, "developable.lu.category", new int[]{41});
        developmentRestrictionsFile = PropertiesUtil.getStringProperty(bundle, "development.restrictions", "input/developmentConstraints.csv");

        PropertiesUtil.printOutModuleTitle("Geo - grwoth capacity model?");
        useCapacityForDwellings = PropertiesUtil.getBooleanProperty(bundle, "use.growth.capacity.data", false);
        capacityFile = PropertiesUtil.getStringProperty(bundle, "growth.capacity.file", "INSERT_DEFAULT_VALUE");

        //this property is doubled - in Munich, is equal to zonalDataFile
        /*PropertiesUtil.printOutModuleTitle("Geo additional input data for zones");
        if(implementation == Implementation.MUNICH) {
            zonalAttributesFile = PropertiesUtil.getStringProperty(bundle, "raster.cells.definition","input/zoneSystem.csv");
        } else {
            zonalAttributesFile = null;
        }*/
        //transitAccessTime = PropertiesUtil.getStringProperty(bundle, "transit.access.time");

    }
}


