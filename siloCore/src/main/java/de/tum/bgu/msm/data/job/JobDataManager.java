package de.tum.bgu.msm.data.job;

import de.tum.bgu.msm.container.Data;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.person.Person;

import java.util.Collection;
import java.util.List;

public interface JobDataManager extends Data {
    Job getJobFromId(int jobId);

    Collection<Job> getJobs();

    void removeJob(int id);

    int getNextJobId();

    List<Integer> getNextJobIds(int amount);

    float getJobForecast(int year, int zone, String jobType);

    void quitJob(boolean makeJobAvailableToOthers, Person person);

    int getNumberOfVacantJobsByRegion(int region);

    int findVacantJob(Zone homeZone, Collection<Region> regions);

    void addJobToVacancyList(int zone, int jobId);

    double getJobDensityInZone(int zone);

    int getJobDensityCategoryOfZone(int zone);

    void addJob(Job jj);

    JobFactory getFactory();
}
