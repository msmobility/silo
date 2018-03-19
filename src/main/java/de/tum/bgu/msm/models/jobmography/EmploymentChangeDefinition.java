package de.tum.bgu.msm.models.jobmography;

import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.JobDataManager;

import java.util.concurrent.Callable;

public abstract class EmploymentChangeDefinition implements Callable {

    protected final int zone;
    protected int changes;
    protected final String jobType;
    protected final JobDataManager jobDataManager;
    protected final HouseholdDataManager householdDataManager;


    public EmploymentChangeDefinition(int zone, int changes, String jobType, SiloDataContainer dataContainer) {
        this.zone = zone;
        this.changes = changes;
        this.jobType = jobType;
        this.jobDataManager = dataContainer.getJobData();
        this.householdDataManager = dataContainer.getHouseholdData();
    }
}
