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

    /**
     * Crime index by county filepath.
     */
    public final String countyCrimeFile;


    public final String landUseAndDevelopmentFile;


    public final boolean useCapacityForDwellings;



    public GeoProperties(ResourceBundle bundle) {
        PropertiesUtil.newPropertySubmodule("Geo properties");
        zonalDataFile = PropertiesUtil.getStringProperty(bundle, "zonal.data.file", "input/zoneSystem.csv");
        zoneShapeFile = PropertiesUtil.getStringProperty(bundle, "zones.shapefile", "input/zonesShapefile/zones.shp" );

        countyCrimeFile = PropertiesUtil.getStringProperty(bundle, "crime.index", "input/crimeIndex.csv");

        landUseAndDevelopmentFile = PropertiesUtil.getStringProperty(bundle, "development.file", "input/development.csv");

        PropertiesUtil.newPropertySubmodule("Geo - growth capacity model?");
        useCapacityForDwellings = PropertiesUtil.getBooleanProperty(bundle, "use.growth.capacity.data", false);

    }
}


