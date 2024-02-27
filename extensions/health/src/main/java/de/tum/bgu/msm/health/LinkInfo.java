package de.tum.bgu.msm.health;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.accidents.AccidentType;
import org.matsim.contrib.emissions.Pollutant;

import java.util.HashMap;
import java.util.Map;

public class LinkInfo {

    private final Id<Link> linkId;

    //private  Map<AccidentType, Map<Integer, Double>> lightCasualityExposureByAccidentTypeByTime = new HashMap<>();

    private  Map<AccidentType, OpenIntFloatHashMap> severeFatalCasualityExposureByAccidentTypeByTime = new HashMap<>();

    private Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin = new HashMap<>();

    public LinkInfo(Id<Link> linkId) {
        this.linkId = linkId;
    }

    public Id<Link> getLinkId() {
        return linkId;
    }

//    public Map<AccidentType, Map<Integer, Double>> getLightCasualityExposureByAccidentTypeByTime() {
//        return lightCasualityExposureByAccidentTypeByTime;
//    }

    public Map<AccidentType, OpenIntFloatHashMap> getSevereFatalCasualityExposureByAccidentTypeByTime() {
        return severeFatalCasualityExposureByAccidentTypeByTime;
    }

//    public void setLightCasualityExposureByAccidentTypeByTime(Map<AccidentType, Map<Integer, Double>> lightCasualityExposureByAccidentTypeByTime) {
//        this.lightCasualityExposureByAccidentTypeByTime = lightCasualityExposureByAccidentTypeByTime;
//    }

    public void setSevereFatalCasualityExposureByAccidentTypeByTime(Map<AccidentType, OpenIntFloatHashMap> severeFatalCasualityExposureByAccidentTypeByTime) {
        this.severeFatalCasualityExposureByAccidentTypeByTime = severeFatalCasualityExposureByAccidentTypeByTime;
    }

    public Map<Pollutant, OpenIntFloatHashMap> getExposure2Pollutant2TimeBin() {
        return exposure2Pollutant2TimeBin;
    }

    public void setExposure2Pollutant2TimeBin(Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin) {
        this.exposure2Pollutant2TimeBin = exposure2Pollutant2TimeBin;
    }

    public void clearAccidentInfo(){
        severeFatalCasualityExposureByAccidentTypeByTime.clear();
    }
}
