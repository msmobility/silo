package de.tum.bgu.msm.data;

import de.tum.bgu.msm.container.SiloDataContainer;

/**
 * Interface to store zonal, county and regional data used by the SILO Model
 * @author Ana Moreno and Rolf Moeckel, Technical University of Munich
 * Created on 5 April 2017 in Munich
 **/

public interface GeoData {

    void setInitialData();

    int[] getZones();

    int[] getRegionList();

    int getRegionOfZone(int zone);

    int getRegionIndex(int region);

    int getZoneIndex(int zone);

    float[] getSizeOfZonesInAcres();

    float getSizeOfZoneInAcres(int zone);

    int getHighestZonalId();

    int[] getZonesInRegion(int region);

    boolean useNumberOfDwellingsAsCapacity();

    float getDevelopmentCapacity(int zone);

    int getMSAOfZone(int zone);

    void reduceDevelopmentCapacityByOneDwelling(int zone);

    void reduceDevelopmentCapacityByDevelopableAcres(int zone, float acres);

    int[] getDevelopableLandUseTypes();

    float getAreaOfLandUse(String landUseType, int zone);

    boolean isThisDwellingTypeAllowed(String dwellingType, int zone);

    void writeOutDevelopmentCapacityFile(SiloDataContainer dataContainer);


}
