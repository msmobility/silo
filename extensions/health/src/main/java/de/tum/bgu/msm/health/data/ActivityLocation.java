package de.tum.bgu.msm.health.data;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.contrib.emissions.Pollutant;

import java.util.HashMap;
import java.util.Map;

public class ActivityLocation {

    private final String locationId;

    private final Coordinate coordinate;

    private Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin = new HashMap<>();

    private OpenIntFloatHashMap noiseLevel2TimeBin = new OpenIntFloatHashMap();

    private double ndvi;

    /* for concentration at activity location, all dwelling, school and poi locations are used.
    Additionally, we add zone centroid as receiver points when no specific activity location is defined.
    */
    public ActivityLocation(String id, Coordinate coordinate) {
        this.locationId = id;
        this.coordinate = coordinate;
    }

    public String getLocationId() {
        return locationId;
    }

    public Map<Pollutant, OpenIntFloatHashMap> getExposure2Pollutant2TimeBin() {
        return exposure2Pollutant2TimeBin;
    }

    public void setExposure2Pollutant2TimeBin(Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin) {
        this.exposure2Pollutant2TimeBin = exposure2Pollutant2TimeBin;
    }

    public OpenIntFloatHashMap getNoiseLevel2TimeBin() {
        return noiseLevel2TimeBin;
    }

    public void setNoiseLevel2TimeBin(OpenIntFloatHashMap noiseLevel2TimeBin) {
        this.noiseLevel2TimeBin = noiseLevel2TimeBin;
    }

    public double getNdvi() {
        return ndvi;
    }

    public void setNdvi(double ndvi) {
        this.ndvi = ndvi;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void reset(){
        exposure2Pollutant2TimeBin.clear();
        noiseLevel2TimeBin.clear();
    }
}
