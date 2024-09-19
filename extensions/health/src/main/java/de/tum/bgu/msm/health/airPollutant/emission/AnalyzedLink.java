package de.tum.bgu.msm.health.airPollutant.emission;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;

public class AnalyzedLink extends AnalyzedObject<Link> {

    private Link matsimLink;

    public AnalyzedLink(Id<Link> id, Link matsimLink) {
        super(id);
        this.matsimLink = matsimLink;
    }

    public Link getMatsimLink() {
        return matsimLink;
    }

}
