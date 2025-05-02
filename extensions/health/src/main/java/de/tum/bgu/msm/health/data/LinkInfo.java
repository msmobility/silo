package de.tum.bgu.msm.health.data;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.health.injury.AccidentType;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.emissions.Pollutant;

import java.util.HashMap;
import java.util.Map;

public class LinkInfo {

    private final Id<Link> linkId;

    private  Map<AccidentType, OpenIntFloatHashMap> severeFatalCasualityExposureByAccidentTypeByTime = new HashMap<>();

    private Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin = new HashMap<>();

    private OpenIntFloatHashMap noiseLevel2TimeBin = new OpenIntFloatHashMap();

    private double ndvi;

    public LinkInfo(Id<Link> linkId) {
        this.linkId = linkId;
    }

    public Id<Link> getLinkId() {
        return linkId;
    }

    public Map<AccidentType, OpenIntFloatHashMap> getSevereFatalCasualityExposureByAccidentTypeByTime() {
        return severeFatalCasualityExposureByAccidentTypeByTime;
    }

    public void setSevereFatalCasualityExposureByAccidentTypeByTime(Map<AccidentType, OpenIntFloatHashMap> severeFatalCasualityExposureByAccidentTypeByTime) {
        this.severeFatalCasualityExposureByAccidentTypeByTime = severeFatalCasualityExposureByAccidentTypeByTime;
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

    public void reset(){
        severeFatalCasualityExposureByAccidentTypeByTime.clear();
        exposure2Pollutant2TimeBin.clear();
        noiseLevel2TimeBin.clear();
    }
}
