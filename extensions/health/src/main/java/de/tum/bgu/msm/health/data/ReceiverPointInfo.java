package de.tum.bgu.msm.health.data;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.health.injury.AccidentType;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.emissions.Pollutant;
import org.matsim.contrib.noise.ReceiverPoint;

import java.util.HashMap;
import java.util.Map;

public class ReceiverPointInfo {

    private final String rpId;

    private final Coordinate coordinate;

    private Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin = new HashMap<>();

    private OpenIntFloatHashMap noiseLevel2TimeBin = new OpenIntFloatHashMap();

    private double ndvi;

    public ReceiverPointInfo(String id, Coordinate coordinate) {
        this.rpId = id;
        this.coordinate = coordinate;
    }

    public String getRpId() {
        return rpId;
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
}
