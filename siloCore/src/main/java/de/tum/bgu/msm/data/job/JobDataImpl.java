package de.tum.bgu.msm.data.job;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JobDataImpl implements JobData {

    private final Map<Integer, Job> jobs = new ConcurrentHashMap<>();

    @Override
    public Job get(int jobId) {
        return jobs.get(jobId);
    }

    @Override
    public Collection<Job> getJobs() {
        return jobs.values();
    }

    @Override
    public void removeJob(int id) {
        jobs.remove(id);
    }

    @Override
    public void addJob(Job jj) {
        jobs.put(jj.getId(), jj);
    }
}
