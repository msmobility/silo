package de.tum.bgu.msm.models.jobmography;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.job.*;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Reads exogenous forecast for jobs and adds/removes jobs accordingly
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 25 February 2013 in Santa Fe, NM
 * Revised on 11 March 2014 in College Park, MD
 **/
public class JobMarketUpdateImpl extends AbstractModel implements JobMarketUpdate {

    private final Logger LOGGER = Logger.getLogger(JobMarketUpdateImpl.class);
    private JobFactory factory;

    public JobMarketUpdateImpl(DataContainer dataContainer, Properties properties, Random rnd) {
        super(dataContainer, properties, rnd);
        this.factory = dataContainer.getJobDataManager().getFactory();
    }

    @Override
    public void setup() {

    }

    @Override
    public void prepareYear(int year) {
        if(year != properties.main.baseYear) {
            updateJobInventoryMultiThreadedThisYear(year);
        }
    }

    @Override
    public void endYear(int year) {}

    @Override
    public void endSimulation() {

    }

    private void updateJobInventoryMultiThreadedThisYear(int year) {
        // read exogenous job forecast and add or remove jobs for each zone accordingly in multi-threaded procedure

        LOGGER.info("  Updating job market based on exogenous forecast for " + year + " (multi-threaded step)");
        final int highestId = dataContainer.getGeoData().getZones().keySet()
                .stream().mapToInt(Integer::intValue).max().getAsInt();
        int[][] jobsByZone = new int[JobType.getNumberOfJobTypes()][highestId + 1];

        JobDataManager jobDataManager = dataContainer.getJobDataManager();

        for (Job jj : jobDataManager.getJobs()) {
            int jobTypeId = JobType.getOrdinal(jj.getType());
            jobsByZone[jobTypeId][jj.getZoneId()]++;
        }

        //String dir = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/employmentForecast/";
        //String forecastFileName = dir + properties.jobDataManager.employmentForeCastFile + year + ".csv";
        //TableDataSet forecast = SiloUtil.readCSVfile(forecastFileName);


        Map<String, List<Integer>> jobsAvailableForRemoval = new HashMap<>();
        for (Job jj : jobDataManager.getJobs()) {
            String token = jj.getType() + "." + jj.getZoneId() + "." + (jj.getWorkerId() == -1);
            if (jobsAvailableForRemoval.containsKey(token)) {
                List<Integer> jobList = jobsAvailableForRemoval.get(token);
                jobList.add(jj.getId());
                jobsAvailableForRemoval.put(token, jobList);
            } else {
                List<Integer> jobList = new ArrayList<>();
                jobList.add(jj.getId());
                jobsAvailableForRemoval.put(token, jobList);
            }
        }


        ConcurrentExecutor<Void> executor = ConcurrentExecutor.cachedService();
        //for (int row = 1; row <= forecast.getRowCount(); row++) {
        for (Zone zone : dataContainer.getGeoData().getZones().values()){

            //int zoneId = (int) forecast.getValueAt(row, "zone");

            int zoneId = zone.getZoneId();
            //Zone zone = dataContainer.getGeoData().getZones().get(zoneId);

            for (String jt : JobType.getJobTypes()) {
                //int jobsExogenousForecast = (int) forecast.getValueAt(row, jt);
                int jobsExogenousForecast = (int) jobDataManager.getJobForecast(year, zoneId, jt);
                if (jobsExogenousForecast > jobsByZone[JobType.getOrdinal(jt)][zoneId]) {
                    int change = jobsExogenousForecast - jobsByZone[JobType.getOrdinal(jt)][zoneId];
                    executor.addTaskToQueue(new AddJobsDefinition(zone, change, jt, dataContainer, factory, new Random(random.nextLong())));
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
    }
}
