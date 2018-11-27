package de.tum.bgu.msm.data;

import de.tum.bgu.msm.container.SiloDataContainer;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Map;

/**
 * Interface to store zonal, county and regional data used by the SILO Model
 * @author Ana Moreno and Rolf Moeckel, Technical University of Munich
 * Created on 5 April 2017 in Munich
 **/

public interface GeoData {

    void readData();

    /**
     * Returns an immutable map of all zones mapped to their IDs
     */
    Map<Integer, Zone> getZones();

    /**
     * Returns an immutable map of all regions mapped to their IDs
     */
    Map<Integer, Region> getRegions();

    /**
     * Returns an immutable map of all zoneFeatures mapped to their zone IDs
     */
    Map<Integer, SimpleFeature> getZoneFeatureMap();

}
