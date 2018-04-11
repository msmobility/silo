package de.tum.bgu.msm.models.jobmography;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Job;
import de.tum.bgu.msm.data.JobDataManager;

import java.util.List;
import java.util.concurrent.Callable;

public class AddJobsDefinition extends EmploymentChangeDefinition implements Callable {

    public final List<Integer> ids;

    public AddJobsDefinition(int zone, int change, String jobType, SiloDataContainer dataContainer) {
        super(zone, change, jobType, dataContainer);
        this.ids = jobDataManager.getNextJobIds(change);
    }

    @Override
    public Object call() throws Exception {
        for (int i = 0; i < changes; i++) {
            int id = ids.get(i);
            synchronized (Job.class) {
                jobDataManager.createJob(id, zone, -1, jobType);
            }
            if (id == SiloUtil.trackJj) {
                SiloUtil.trackWriter.println("Job " + id + " of type " + jobType +
                        " was newly created in zone " + zone + " based on exogenous forecast.");
            }
        }
        return null;
    }
}
