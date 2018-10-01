package de.tum.bgu.msm.models.jobmography;

import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.JobDataManager;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobFactory;
import de.tum.bgu.msm.data.job.JobUtils;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Reads exogenous forecast for jobs and adds/removes jobs accordingly
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 25 February 2013 in Santa Fe, NM
 * Revised on 11 March 2014 in College Park, MD
 **/

public class UpdateJobs extends AbstractModel {

    private final Logger LOGGER = Logger.getLogger(UpdateJobs.class);
    private JobFactory factory;

    public UpdateJobs(SiloDataContainer dataContainer) {
        super(dataContainer);
        factory = JobUtils.getFactory();
    }

    public void updateJobInventoryMultiThreadedThisYear(int year) {
        // read exogenous job forecast and add or remove jobs for each zone accordingly in multi-threaded procedure

        LOGGER.info("  Updating job market based on exogenous forecast for " + year + " (multi-threaded step)");
        final int highestId = dataContainer.getGeoData().getZones().keySet()
                .stream().mapToInt(Integer::intValue).max().getAsInt();
        int[][] jobsByZone = new int[JobType.getNumberOfJobTypes()][highestId + 1];

        JobDataManager jobData = dataContainer.getJobData();

        for (Job jj :jobData.getJobs()) {
            int jobTypeId = JobType.getOrdinal(jj.getType());
            jobsByZone[jobTypeId][jj.getZoneId()]++;
        }

        //String dir = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName + "/employmentForecast/";
        //String forecastFileName = dir + Properties.get().jobData.employmentForeCastFile + year + ".csv";
        //TableDataSet forecast = SiloUtil.readCSVfile(forecastFileName);


        Map<String, List<Integer>> jobsAvailableForRemoval = new HashMap<>();
        for (Job jj : jobData.getJobs()) {
            String token = jj.getType() + "." + jj.getZoneId() + "." + (jj.getWorkerId() == -1);
            if (jobsAvailableForRemoval.containsKey(token)) {
                List<Integer> jobList = jobsAvailableForRemoval.get(token);
                jobList.add(jj.getId());
                jobsAvailableForRemoval.put(token, jobList);
            } else {
                List<Integer> jobList = new ArrayList();
                jobList.add(jj.getId());
                jobsAvailableForRemoval.put(token, jobList);
            }
        }


        ConcurrentExecutor executor = ConcurrentExecutor.cachedService();
        //for (int row = 1; row <= forecast.getRowCount(); row++) {
        for (Zone zone : dataContainer.getGeoData().getZones().values()){

            //int zoneId = (int) forecast.getValueAt(row, "zone");

            int zoneId = zone.getZoneId();
            //Zone zone = dataContainer.getGeoData().getZones().get(zoneId);

            for (String jt : JobType.getJobTypes()) {
                //int jobsExogenousForecast = (int) forecast.getValueAt(row, jt);
                int jobsExogenousForecast = (int) jobData.getJobForecast(year, zoneId, jt);
                if (jobsExogenousForecast > jobsByZone[JobType.getOrdinal(jt)][zoneId]) {
                    int change = jobsExogenousForecast - jobsByZone[JobType.getOrdinal(jt)][zoneId];
                    executor.addTaskToQueue(new AddJobsDefinition(zone, change, jt, dataContainer, factory));
                } else if (jobsExogenousForecast < jobsByZone[JobType.getOrdinal(jt)][zoneId]) {
                    int change = jobsByZone[JobType.getOrdinal(jt)][zoneId] - jobsExogenousForecast;
                    List<Integer> vacantJobs = jobsAvailableForRemoval.get(jt + "." + zone + "." + true);
                    List<Integer> occupiedJobs = jobsAvailableForRemoval.get(jt + "." + zone + "." + false);
                    if(vacantJobs == null) {
                        vacantJobs = Collections.EMPTY_LIST;
                    }
                    if(occupiedJobs == null) {
                        occupiedJobs = Collections.EMPTY_LIST;
                    }
                    executor.addTaskToQueue(new RemoveJobsDefinition(zone, change, jt, vacantJobs, occupiedJobs, dataContainer));
                }
            }
        }
        executor.execute();

        // Fix job map, which for some reason keeps getting messed up
        for (int jobId : jobData.getJobMapIDs()) {
            Job jj = jobData.getJobFromId(jobId);
            if (jj == null) {
                jobData.removeJob(jobId);
                LOGGER.warn("Had to manually remove Job " + jobId + " from job map.");
            }
        }
    }
}
