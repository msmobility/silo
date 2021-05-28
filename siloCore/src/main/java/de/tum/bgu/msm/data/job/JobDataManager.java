package de.tum.bgu.msm.data.job;

import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.models.ModelUpdateListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface JobDataManager extends ModelUpdateListener {
    Job getJobFromId(int jobId);

    Collection<Job> getJobs();

    void removeJob(int id);

    int getNextJobId();

    List<Integer> getNextJobIds(int amount);

    float getJobForecast(int year, int zone, String jobType);

    void quitJob(boolean makeJobAvailableToOthers, Person person);

    Job findVacantJob(Zone homeZone, Collection<Region> regions);

    double getJobDensityInZone(int zone);

    int getJobDensityCategoryOfZone(int zone);

    void addJob(Job jj);

    JobFactory getFactory();

    Map<Integer, List<Job>> getVacantJobsByRegion();
}
