package de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import org.apache.log4j.Logger;

import java.util.*;


public class GenerateJobCounters {

    private static final Logger logger = Logger.getLogger(GenerateJobCounters.class);

    private final DataSetSynPop dataSetSynPop;
    private final DataContainer dataContainer;
    private int assignedJobs;

    public GenerateJobCounters(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }


    public void run() {
        logger.info("   Running module: job de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation.Generate job counters");
        generateJobCounters();
        //calculateDistanceProbabilityByJobType();
        logger.info("   Finished job de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation.Generate job counters. Generated " + assignedJobs
                + " jobs using a vacancy rate of " + PropertiesSynPop.get().main.vacantJobPercentage + " percent.");

    }


    private void generateJobCounters(){
        logger.info("  Generating counters with vacant jobs by zone");
        Map<String, Map<Integer, Integer>> vacantJobsByTypeAndZone = new HashMap<>();
        Map<String, Map<Integer, Integer>> assignedJobsByTypeAndZone = new HashMap<>();
        assignedJobs = 0;
        TableDataSet jobsByTaz = PropertiesSynPop.get().main.jobsByTaz;
        for (String jobType : JobType.getJobTypes()) {
            //vacantJobsByTypeAndZone.putIfAbsent(jobType, new HashMap<>());
            assignedJobsByTypeAndZone.putIfAbsent(jobType, new HashMap<>());
            for (int taz : jobsByTaz.getColumnAsInt("taz")) {
                //int jobs = (int) jobsByTaz.getValueAt(taz, jobType) * (1 + PropertiesSynPop.get().main.vacantJobPercentage / 100);
                //vacantJobsByTypeAndZone.get(jobType).putIfAbsent(taz, jobs);
                //assignedJobs = assignedJobs + jobs;
                assignedJobsByTypeAndZone.get(jobType).putIfAbsent(taz, 0);
            }
        }

        vacantJobsByTypeAndZone.putIfAbsent("all", new HashMap<>());
        for (int taz : jobsByTaz.getColumnAsInt("taz")) {
            int jobs = 0;
            for (String jobType : JobType.getJobTypes()) {
                jobs = jobs + (int) jobsByTaz.getValueAt(taz, jobType) * (1 + PropertiesSynPop.get().main.vacantJobPercentage / 100);
            }
            jobs = Math.max(0,jobs);
            vacantJobsByTypeAndZone.get("all").putIfAbsent(taz, jobs);
            assignedJobs = assignedJobs + jobs;
        }

        dataSetSynPop.setVacantJobsByTypeAndZone(vacantJobsByTypeAndZone);
        dataSetSynPop.setAssignedJobsByTypeAndZone(assignedJobsByTypeAndZone);
    }


    private static boolean isPowerOfFour(int number){
        double pow = Math.pow(number, 0.25);
        if (pow - Math.floor(pow) == 0){
            return  true;
        } else {
            return false;
        }

    }
}
