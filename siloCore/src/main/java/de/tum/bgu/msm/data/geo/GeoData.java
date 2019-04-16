package de.tum.bgu.msm.data.geo;

import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.models.ModelUpdateListener;

import java.util.Map;

/**
 * Interface to store zonal, county and regional data used by the SILO Model
 * @author Ana Moreno and Rolf Moeckel, Technical University of Munich
 * Created on 5 April 2017 in Munich
 **/

public interface GeoData extends ModelUpdateListener {

    /**
     * Returns a map of all zones mapped to their IDs
     */
    Map<Integer, Zone> getZones();

    /**
     * Returns a map of all regions mapped to their IDs
     */
    Map<Integer, Region> getRegions();

    /**
     * TODO
     * @param zone
     * @return
     */
    void addZone(Zone zone);

    /**
     * TODO
     * @return
     */
    void addRegion(Region region);


}
