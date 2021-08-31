package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.data.person.PersonType;
import de.tum.bgu.msm.properties.Properties;
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

    /**
     * Land use development capacity (in acres and/or dwellings). Default is input/development.csv
     */
    public final String landUseAndDevelopmentFile;

    /**
     * Use dwelling capacity as development capacity (instead of land use area). Default is false
     */
    public final boolean useCapacityForDwellings;
    public final String parkingZonalDataFile;


    public GeoProperties(ResourceBundle bundle) {
        PropertiesUtil.newPropertySubmodule("Geo properties");
        zonalDataFile = PropertiesUtil.getStringProperty(bundle, "zonal.data.file", "input/zoneSystem.csv");
        zoneShapeFile = PropertiesUtil.getStringProperty(bundle, "zones.shapefile", "input/zonesShapefile/zones.shp" );
        countyCrimeFile = PropertiesUtil.getStringProperty(bundle, "crime.index", "input/crimeIndex.csv");

        PropertiesUtil.newPropertySubmodule("Development properties");
        landUseAndDevelopmentFile = PropertiesUtil.getStringProperty(bundle, "development.file", "input/development.csv");
        useCapacityForDwellings = PropertiesUtil.getBooleanProperty(bundle, "use.growth.capacity.data", false);

        parkingZonalDataFile = PropertiesUtil.getStringProperty(bundle, "parking.zone.data", "scenarios/av/parking_by_zone.csv");
    }
}


