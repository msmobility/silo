package de.tum.bgu.msm.data.munich;

import de.tum.bgu.msm.data.AbstractDefaultGeoData;

/**
 * Interface to store zonal, county and regional data used by the SILO Model
 * @author Ana Moreno and Rolf Moeckel, Technical University of Munich
 * Created on 5 April 2017 in Munich
 **/

public class GeoDataMuc extends AbstractDefaultGeoData {

    public GeoDataMuc() {
        super("Zone", "Region");
    }
}
