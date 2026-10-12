package de.tum.bgu.msm.matsim;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.utils.objectattributes.attributable.Attributes;

public class PreviousYearPlan {

    Id agentId;
    Plan previousYearPlan;
    int jobId;
    int dwellingId;

    public PreviousYearPlan(Id agentId, Plan previousYearPlan, int jobId, int dwellingId) {
        this.agentId = agentId;
        this.previousYearPlan = previousYearPlan;
        this.jobId =  jobId;
        this.dwellingId = dwellingId;
    }
}
