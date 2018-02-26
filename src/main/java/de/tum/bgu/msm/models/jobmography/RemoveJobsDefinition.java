package de.tum.bgu.msm.models.jobmography;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.Job;
import de.tum.bgu.msm.data.JobDataManager;
import de.tum.bgu.msm.data.Person;

import java.util.List;

public class RemoveJobsDefinition extends EmploymentChangeDefinition {

    private final List<Integer> vacantJobs;
    private final List<Integer> occupiedJobs;
    private final JobDataManager jobDataManager;

    public RemoveJobsDefinition(int zone, int change, String jobType, List<Integer> vacantJobs, List<Integer> occupiedJobs, JobDataManager jobDataManager) {
        super(zone, change, jobType);
        this.vacantJobs = vacantJobs;
        this.occupiedJobs = occupiedJobs;
        this.jobDataManager = jobDataManager;
    }

    @Override
    public void execute() {
        // remove jobs

        // first, try to eliminate only jobs that are vacant
        removeVacantJobs();

        // if necessary (i.e., change still > 0) remove jobs that are filled with workers
        if(changes > 0) {
            removeOccupiedJobs();
        }
    }

    private void removeVacantJobs() {
        for (Integer job : vacantJobs) {
            removeJob(job);
            if (job == SiloUtil.trackJj) {
                SiloUtil.trackWriter.println("Vacant job " + job +
                        " of type " + jobType + " was removed in zone " + zone + " based on exogenous forecast.");
            }
            changes--;
        }
    }

    private void removeOccupiedJobs() {
        for (Integer occupiedJob : occupiedJobs) {
            firePerson(occupiedJob);
            removeJob(occupiedJob);
            if (occupiedJob == SiloUtil.trackJj) SiloUtil.trackWriter.println("Previously occupied job " +
                    occupiedJob + " of type " + jobType + " was removed in zone " + zone + " based on exogenous forecast.");
            changes--;
        }
    }

    private void firePerson(Integer occupiedJob) {
        Job jobToBeRemoved = Job.getJobFromId(occupiedJob);
        int personId = jobToBeRemoved.getWorkerId();
        Person.getPersonFromId(personId).quitJob(false, jobDataManager);
    }

    private synchronized static void removeJob(int job) {
        Job.removeJob(job);
    }
}
