package de.tum.bgu.msm.data.geo;


import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultGeoData implements GeoData {

    private static final Logger logger = Logger.getLogger(DefaultGeoData.class);

    private final Map<Integer, Zone> zones = new LinkedHashMap<>();
    private final Map<Integer, Region> regions = new LinkedHashMap<>();

    @Override
    public Map<Integer, Zone> getZones() {
        return Collections.unmodifiableMap(zones);
    }

    @Override
    public Map<Integer, Region> getRegions() {
        return Collections.unmodifiableMap(regions);
    }

    @Override
    public void addZone(Zone zone) {
        final Zone previous = zones.put(zone.getId(), zone);
        if(previous != null) {
            logger.warn("Overwriting zone " + previous + " with " + zone);
        }
    }

    @Override
    public void addRegion(Region region) {
        final Region previous = regions.put(region.getId(), region);
        if(previous != null) {
            logger.warn("Overwriting zone " + previous + " with " + region);
        }
    }

    @Override
    public void setup() {

    }

    @Override
    public void prepareYear(int year) {

    }

    @Override
    public void endYear(int year) {

    }

    @Override
    public void endSimulation() {

    }
}