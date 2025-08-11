package de.tum.bgu.msm.health;

import de.tum.bgu.msm.data.Day;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;

public class VisitedLink {
    public final Id<Link> linkId;
    public final int hour;
    public final Day day;
    public final String mode;

    public VisitedLink(Id<Link> linkId, int hour, Day day, String mode) {
        this.linkId = linkId;
        this.hour = hour;
        this.day = day;
        this.mode = mode;
    }
}