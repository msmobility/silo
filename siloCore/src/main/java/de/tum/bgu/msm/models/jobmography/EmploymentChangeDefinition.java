package de.tum.bgu.msm.models.jobmography;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.JobDataManager;

import java.util.concurrent.Callable;

public abstract class EmploymentChangeDefinition implements Callable {

    protected final Zone zone;
    protected int changes;
    protected final String jobType;
    protected final JobDataManager jobDataManager;
    protected final HouseholdDataManager householdDataManager;


    public EmploymentChangeDefinition(Zone zone, int changes, String jobType, DataContainer dataContainer) {
        this.zone = zone;
        this.changes = changes;
        this.jobType = jobType;
        this.jobDataManager = dataContainer.getJobDataManager();
        this.householdDataManager = dataContainer.getHouseholdDataManager();
    }
}
