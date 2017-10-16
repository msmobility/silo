package de.tum.bgu.msm.data;

import de.tum.bgu.msm.container.SiloDataContainer;

/**
 * Interface to store zonal, county and regional data used by the SILO Model
 * @author Ana Moreno and Rolf Moeckel, Technical University of Munich
 * Created on 5 April 2017 in Munich
 **/

public interface GeoData {

    public void setInitialData();

    public int[] getZones ();

    public int[] getRegionList();

    public int getRegionOfZone (int zone);

    public int getRegionIndex(int region);

    public int getZoneIndex(int zone);

    public float[] getSizeOfZonesInAcres();

    public float getSizeOfZoneInAcres(int zone);

    public int getHighestZonalId ();

    public int[] getZonesInRegion (int region);

    public boolean useNumberOfDwellingsAsCapacity ();

    public float getDevelopmentCapacity (int zone);

    public void reduceDevelopmentCapacityByOneDwelling (int zone);

    public void reduceDevelopmentCapacityByDevelopableAcres (int zone, float acres);

    public int[] getDevelopableLandUseTypes();

    public float getAreaOfLandUse (String landUseType, int zone);

    public boolean isThisDwellingTypeAllowed (String dwellingType, int zone);

    public void writeOutDevelopmentCapacityFile (SiloDataContainer dataContainer);


}
