package sdg.data;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.vehicles.Vehicle;

public class AnalyzedPerson {
    final private Id<Person> id;
    private Id<Link> currentLink;
    private double currentLinkEnterTime;

    private double congestedTime;
    private double freeFlowTime;

    public AnalyzedPerson(Id<Person> id) {
        this.id = id;
    }

    public void enterThisLink(Id<Link> linkId, double time){
        currentLink = linkId;
        currentLinkEnterTime = time;
    }

    public void leaveThisLink(Id<Link> linkId, double time, Link link){
        if (linkId == currentLink){
            congestedTime = congestedTime + (time - currentLinkEnterTime);
            freeFlowTime = freeFlowTime + (link.getLength() / link.getFreespeed());
        }
    }

    public void leaveTraffic(){
        currentLink = null;
    }

    public Id<Person> getId() {
        return id;
    }

    public double getCongestedTime() {
        return congestedTime;
    }

    public double getFreeFlowTime() {
        return freeFlowTime;
    }
}
