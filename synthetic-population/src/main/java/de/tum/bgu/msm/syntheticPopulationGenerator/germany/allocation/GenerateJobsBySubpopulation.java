package de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation;

import com.google.common.math.LongMath;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobUtils;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GenerateJobsBySubpopulation {

    private static final Logger logger = Logger.getLogger(GenerateJobsBySubpopulation.class);

    private final DataContainer dataContainer;
    private DataSetSynPop dataSetSynPop;

    public GenerateJobsBySubpopulation(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataContainer = dataContainer;
        this.dataSetSynPop = dataSetSynPop;
    }

    public void run(){
        logger.info("   Running module: job generation");
        //read persons, their work zone id and job type. Generate a job with those values
        //update the counters for the final vacant jobs to add to each zone
        JobDataManager jobData = dataContainer.getJobDataManager();
        for (Person pp : dataContainer.getHouseholdDataManager().getPersons()){
            int workZone = Integer.parseInt(pp.getAttribute("workZone").get().toString());
            if (pp.getOccupation().equals(Occupation.EMPLOYED) && workZone > 0) {
                int jobId = jobData.getNextJobId();
                String jobType = pp.getAttribute("jobType").get().toString();
                int jobsInZoneAndType = dataSetSynPop.getAssignedJobsByTypeAndZone().get(jobType).get(workZone) + 1;
                dataSetSynPop.getAssignedJobsByTypeAndZone().get(jobType).put(workZone, jobsInZoneAndType);
                int maxJobs = dataSetSynPop.getMicrolocationsJobsByTypeAndZone().get(jobType).get(workZone).keySet().size();
                int jobToCopyCoord = 0;
                if (maxJobs > 1){
                    if (jobsInZoneAndType == maxJobs) {
                        jobToCopyCoord = 1;
                    } else if (jobsInZoneAndType < maxJobs){
                        jobToCopyCoord = jobsInZoneAndType;
                    } else {
                        jobToCopyCoord = Math.min(jobsInZoneAndType - maxJobs -1, maxJobs - 1);
                    }
                }
                Coordinate coords = dataSetSynPop.getMicrolocationsJobsByTypeAndZone().get(jobType).get(workZone).get(jobToCopyCoord);
/*                if (coords == null){
                    coords = dataSetSynPop.getMicrolocationsJobsByTypeAndZone().get(jobType).get(workZone).get(0);
                }*/
                jobData.addJob(JobUtils.getFactory().createJob(jobId, workZone, coords, pp.getId(), jobType));
                pp.setWorkplace(jobId);
            }
        }
    }


}
