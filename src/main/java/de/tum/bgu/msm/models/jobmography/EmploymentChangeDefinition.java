package de.tum.bgu.msm.models.jobmography;

import java.util.concurrent.Callable;

public abstract class EmploymentChangeDefinition implements Callable {

    protected final int zone;
    protected int changes;
    protected final String jobType;

    public EmploymentChangeDefinition(int zone, int changes, String jobType) {
        this.zone = zone;
        this.changes = changes;
        this.jobType = jobType;
    }
}
