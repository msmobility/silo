package de.tum.bgu.msm.data.job;

import java.util.Collection;

public interface JobData {
    Job get(int jobId);

    Collection<Job> getJobs();

    void removeJob(int id);

    void addJob(Job jj);
}
