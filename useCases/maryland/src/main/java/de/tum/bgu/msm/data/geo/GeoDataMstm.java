package de.tum.bgu.msm.data.geo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Zonal, county and regional data used by the SILO Model
 * Author: Rolf Moeckel, University of Maryland
 * Created on 20 April 2015 in College Park
 **/
public class GeoDataMstm extends DefaultGeoData {

    private final static Logger logger = LogManager.getLogger(GeoDataMstm.class);

    private final Map<Integer, County> counties = new HashMap<>();

    public GeoDataMstm() {
        super();
    }

    public void addCounty(County county) {
        final County previous = counties.put(county.getId(), county);
        if(previous != null) {
            logger.warn("Overwriting county " + previous + " with " + county);
        }
    }

    public Map<Integer, County> getCounties() {
        return Collections.unmodifiableMap(counties);
    }
}
