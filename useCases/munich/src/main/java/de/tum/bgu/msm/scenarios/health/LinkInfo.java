package de.tum.bgu.msm.scenarios.health;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.accidents.AccidentType;
import org.matsim.contrib.emissions.Pollutant;

import java.util.HashMap;
import java.util.Map;

public class LinkInfo {

    private final Id<Link> linkId;

    private  Map<AccidentType, Map<Integer, Double>> lightCasualityExposureByAccidentTypeByTime = new HashMap<>();

    private  Map<AccidentType, Map<Integer, Double>> severeFatalCasualityExposureByAccidentTypeByTime = new HashMap<>();

    private Map<Pollutant, Map<Double, Double>> exposure2Pollutant2TimeBin = new HashMap<>();

    public LinkInfo(Id<Link> linkId) {
        this.linkId = linkId;
    }

    public Id<Link> getLinkId() {
        return linkId;
    }

    public Map<AccidentType, Map<Integer, Double>> getLightCasualityExposureByAccidentTypeByTime() {
        return lightCasualityExposureByAccidentTypeByTime;
    }

    public Map<AccidentType, Map<Integer, Double>> getSevereFatalCasualityExposureByAccidentTypeByTime() {
        return severeFatalCasualityExposureByAccidentTypeByTime;
    }

    public void setLightCasualityExposureByAccidentTypeByTime(Map<AccidentType, Map<Integer, Double>> lightCasualityExposureByAccidentTypeByTime) {
        this.lightCasualityExposureByAccidentTypeByTime = lightCasualityExposureByAccidentTypeByTime;
    }

    public void setSevereFatalCasualityExposureByAccidentTypeByTime(Map<AccidentType, Map<Integer, Double>> severeFatalCasualityExposureByAccidentTypeByTime) {
        this.severeFatalCasualityExposureByAccidentTypeByTime = severeFatalCasualityExposureByAccidentTypeByTime;
    }

    public Map<Pollutant, Map<Double, Double>> getExposure2Pollutant2TimeBin() {
        return exposure2Pollutant2TimeBin;
    }

    public void setExposure2Pollutant2TimeBin(Map<Pollutant, Map<Double, Double>> exposure2Pollutant2TimeBin) {
        this.exposure2Pollutant2TimeBin = exposure2Pollutant2TimeBin;
    }
}
