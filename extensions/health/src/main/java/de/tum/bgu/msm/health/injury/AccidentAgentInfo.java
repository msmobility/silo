package de.tum.bgu.msm.health.injury;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import java.util.HashMap;
import java.util.Map;

public class AccidentAgentInfo {

    private final Id<Person> personId;
    private final Map<Id<Link>, Map<Integer, String>> linkId2time2mode = new HashMap<>();
    private double lightInjuryRisk;
    private double severeInjuryRisk;


    public AccidentAgentInfo(Id<Person> id) {
        this.personId = id;
    }

    public Id<Person> getPersonId() {
        return personId;
    }

    public Map<Id<Link>, Map<Integer, String>> getLinkId2time2mode() {
        return linkId2time2mode;
    }

    public double getLightInjuryRisk() {
        return lightInjuryRisk;
    }

    public void setLightInjuryRisk(double lightInjuryRisk) {
        this.lightInjuryRisk = lightInjuryRisk;
    }

    public double getSevereInjuryRisk() {
        return severeInjuryRisk;
    }

    public void setSevereInjuryRisk(double severeInjuryRisk) {
        this.severeInjuryRisk = severeInjuryRisk;
    }
}
