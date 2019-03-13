package de.tum.bgu.msm.models.jobmography;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.HouseholdData;
import de.tum.bgu.msm.data.JobData;

import java.util.concurrent.Callable;

public abstract class EmploymentChangeDefinition implements Callable {

    protected final Zone zone;
    protected int changes;
    protected final String jobType;
    protected final JobData jobData;
    protected final HouseholdData householdData;


    public EmploymentChangeDefinition(Zone zone, int changes, String jobType, DataContainer dataContainer) {
        this.zone = zone;
        this.changes = changes;
        this.jobType = jobType;
        this.jobData = dataContainer.getJobData();
        this.householdData = dataContainer.getHouseholdData();
    }
}
