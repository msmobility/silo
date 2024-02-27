package de.tum.bgu.msm.health;

import org.matsim.api.core.v01.Id;
import org.matsim.contrib.emissions.Pollutant;

import java.util.HashMap;
import java.util.Map;

public class AnalyzedObject<T> {
    protected Id<T> id;
    protected Map<Pollutant, Double> warmEmissions;
    protected Map<Pollutant, Double> coldEmissions;

    public AnalyzedObject(Id<T> id) {
        this.id = id;
        this.warmEmissions = new HashMap<>();
        this.coldEmissions = new HashMap<>();
    }

    public Map<Pollutant, Double>  getWarmEmissions() {
        return warmEmissions;
    }

    public Map<Pollutant, Double>  getColdEmissions() {
        return coldEmissions;
    }

    public Id<T> getId(){
        return id;
    }
}
