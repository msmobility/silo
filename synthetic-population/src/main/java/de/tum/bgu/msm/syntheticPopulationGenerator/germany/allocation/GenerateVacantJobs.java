package de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobUtils;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.util.Collection;
import java.util.Map;

public class GenerateVacantJobs {

    private static final Logger logger = Logger.getLogger(GenerateVacantJobs.class);

    private final DataContainer dataContainer;
    private DataSetSynPop dataSetSynPop;

    public GenerateVacantJobs(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataContainer = dataContainer;
        this.dataSetSynPop = dataSetSynPop;
    }

    public void run(){
        logger.info("   Running module: job generation");
        //read persons, their work zone id and job type. Generate a job with those values
        //update the counters for the final vacant jobs to add to each zone

        JobDataManager jobData = dataContainer.getJobDataManager();
        int jobCounter = 0;
        for (Job jj : jobData.getJobs()){
            jobCounter++;
            if(jobCounter % PropertiesSynPop.get().main.vacantJobPercentage == 0){
                int jobId = dataSetSynPop.getNextVacantJobId();
                jobData.addJob(JobUtils.getFactory().createJob(jobId, jj.getZoneId(), jj.getCoordinate(), -1, jj.getType()));
            }
        }



/*        for (String jobType : dataSetSynPop.getAssignedJobsByTypeAndZone().keySet()){
            Map<Integer, Integer> jobsByZone = dataSetSynPop.getAssignedJobsByTypeAndZone().get(jobType);
            for (Map.Entry<Integer, Integer> jobEntry : jobsByZone.entrySet()){
                int workZone = jobEntry.getKey();
                int jobs = jobEntry.getValue();
                int vacantJobs = (int) ((int) jobs * PropertiesSynPop.get().main.vacantJobPercentage / 100);
                for (int job = 0; job < vacantJobs; job++){
                    int jobId = jobData.getNextJobId();
                    Coordinate coords = dataSetSynPop.getMicrolocationsJobsByTypeAndZone().get(jobType).get(workZone).get(job+1);
                    jobData.addJob(JobUtils.getFactory().createJob(jobId, workZone, coords, -1, jobType));
                }
            }
        }*/
    }


}
