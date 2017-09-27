package de.tum.bgu.msm.jobmography;

public abstract class EmploymentChangeDefinition {

    protected final int zone;
    protected int changes;
    protected final String jobType;

    public EmploymentChangeDefinition(int zone, int changes, String jobType) {
        this.zone = zone;
        this.changes = changes;
        this.jobType = jobType;
    }

    public abstract void execute();
}
