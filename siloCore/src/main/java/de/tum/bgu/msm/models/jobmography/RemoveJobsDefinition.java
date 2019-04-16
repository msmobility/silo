package de.tum.bgu.msm.models.jobmography;

import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.person.Person;

import java.util.List;

public class RemoveJobsDefinition extends EmploymentChangeDefinition {

    private final List<Integer> vacantJobs;
    private final List<Integer> occupiedJobs;

    public RemoveJobsDefinition(Zone zone, int change, String jobType, List<Integer> vacantJobs,
                                List<Integer> occupiedJobs, DataContainer dataContainer) {
        super(zone, change, jobType, dataContainer);
        this.vacantJobs = vacantJobs;
        this.occupiedJobs = occupiedJobs;
    }

    @Override
    public Object call() {
        // first, try to eliminate only jobs that are vacant
        removeVacantJobs();

        // if necessary (i.e., change still > 0) remove jobs that are filled with workers
        if(changes > 0) {
            removeOccupiedJobs();
        }
        return null;
    }

    private void removeVacantJobs() {
        for (Integer job : vacantJobs) {
            removeJob(job);
            if (job == SiloUtil.trackJj) {
                SiloUtil.trackWriter.println("Vacant job " + job +
                        " of type " + jobType + " was removed in zone " + zone + " based on exogenous forecast.");
            }
            this.changes--;
        }
    }

    private void removeOccupiedJobs() {
        for (Integer occupiedJob : occupiedJobs) {
            firePerson(occupiedJob);
            removeJob(occupiedJob);
            if (occupiedJob == SiloUtil.trackJj) SiloUtil.trackWriter.println("Previously occupied job " +
                    occupiedJob + " of type " + jobType + " was removed in zone " + zone + " based on exogenous forecast.");
            this.changes--;
        }
    }

    private void firePerson(Integer occupiedJob) {
        Job jobToBeRemoved = jobDataManager.getJobFromId(occupiedJob);
        Person person = householdDataManager.getPersonFromId(jobToBeRemoved.getWorkerId());
        jobDataManager.quitJob(false, person);
    }

    private synchronized void removeJob(int job) {
        this.jobDataManager.removeJob(job);
    }
}
