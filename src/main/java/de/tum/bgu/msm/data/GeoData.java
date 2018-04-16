package de.tum.bgu.msm.data;

import de.tum.bgu.msm.container.SiloDataContainer;

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
     * @deprecated  As of jan'18. Use  {@link #getZones()} instead
     */
    @Deprecated
    int[] getZoneIdsArray();

    /**
     * @deprecated  As of jan'18. Use  {@link #getZones()} instead
     */
    @Deprecated
    int[] getRegionIdsArray();

    /**
     * @deprecated  As of jan'18. No need to use with new Collections access {@link #getZones()}
     */
    @Deprecated
    int getZoneIndex(int zone);

    /**
     * @deprecated  As of jan'18. No need to use with new Collections access {@link #getZones()}
     */
    @Deprecated
    int getHighestZonalId();

    boolean useNumberOfDwellingsAsCapacity();

    float getDevelopmentCapacity(int zone);

    void reduceDevelopmentCapacityByOneDwelling(int zone);

    void reduceDevelopmentCapacityByDevelopableAcres(int zone, float acres);

    int[] getDevelopableLandUseTypes();

    float getAreaOfLandUse(String landUseType, int zone);

    boolean isThisDwellingTypeAllowed(String dwellingType, int zone);

    void writeOutDevelopmentCapacityFile(SiloDataContainer dataContainer);
}
