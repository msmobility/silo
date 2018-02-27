package de.tum.bgu.msm.models.jobmography;

import de.tum.bgu.msm.util.concurrent.ConcurrentFunction;

public abstract class EmploymentChangeDefinition implements ConcurrentFunction {

    protected final int zone;
    protected int changes;
    protected final String jobType;

    public EmploymentChangeDefinition(int zone, int changes, String jobType) {
        this.zone = zone;
        this.changes = changes;
        this.jobType = jobType;
    }
}
