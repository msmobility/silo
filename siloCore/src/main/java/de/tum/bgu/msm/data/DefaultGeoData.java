package de.tum.bgu.msm.data;


import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultGeoData implements GeoData {

    private static final Logger logger = Logger.getLogger(DefaultGeoData.class);
    protected final Map<Integer, Zone> zones = new LinkedHashMap<>();
    protected final Map<Integer, Region> regions = new LinkedHashMap<>();


    @Override
    public Map<Integer, Zone> getZones() {
        return Collections.unmodifiableMap(zones);
    }

    @Override
    public Map<Integer, Region> getRegions() {
        return Collections.unmodifiableMap(regions);
    }


    @Override
    public void setup() {

    }

    @Override
    public void prepareYear(int year) {

    }

    @Override
    public void finishYear(int year) {

    }
}