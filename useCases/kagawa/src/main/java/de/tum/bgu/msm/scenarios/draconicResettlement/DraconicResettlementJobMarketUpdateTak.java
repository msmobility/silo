package de.tum.bgu.msm.scenarios.draconicResettlement;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes;
import de.tum.bgu.msm.data.job.*;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.jobmography.JobMarketUpdate;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import de.tum.bgu.msm.utils.SampleException;
import de.tum.bgu.msm.utils.Sampler;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Reads exogenous forecast for jobs and adds/removes jobs accordingly
 * Author: Rolf Moeckel
 * Created on 21 November 2019
 **/
public class DraconicResettlementJobMarketUpdateTak extends AbstractModel implements JobMarketUpdate {

    private final Logger logger = LogManager.getLogger(DraconicResettlementJobMarketUpdateTak.class);

    private final JobDataManager jobDataManager;
    private JobFactory factory;

    public DraconicResettlementJobMarketUpdateTak(DataContainer dataContainer, Properties properties, Random rnd) {
        super(dataContainer, properties, rnd);
        factory = JobUtils.getFactory();
        jobDataManager = dataContainer.getJobDataManager();
    }

    @Override
    public void setup() {
        if (properties.jobData.growthRateInPercentByJobType.values().stream().anyMatch(d -> d < 0)) {
            String message = "This job update implementation only works for positive job growth forecast rates. Aborting..";
            logger.error(message, new RuntimeException(message));
        }
    }

    @Override
    public void prepareYear(int year) {
        if (year != properties.main.baseYear) {
            updateJobInventoryMultiThreadedThisYear(year);
        }
    }

    @Override
    public void endYear(int year) {
    }

    @Override
    public void endSimulation() {

    }

    private void updateJobInventoryMultiThreadedThisYear(int year) {
        // read exogenous job forecast and add or remove jobs for each zone accordingly in multi-threaded procedure

        logger.info("  Updating job market based on exogenous forecast for " + year + " (multi-threaded step)");


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

        Sampler<Zone> zoneSampler = new Sampler<>(dataContainer.getGeoData().getZones().size(), Zone.class);
        for (Zone zone : dataContainer.getGeoData().getZones().values()) {
            boolean isRural = true;
            for (DefaultDwellingTypes.DefaultDwellingTypeImpl dTp : DefaultDwellingTypes.DefaultDwellingTypeImpl.values()) {
                if (zone.getDevelopment().isThisDwellingTypeAllowed(dTp)) isRural = false;

            }
            if (!isRural) {
                zoneSampler.incrementalAdd(zone, jobDataManager.getJobDensityInZone(zone.getZoneId()));
            }
        }

        for (Job job : jobDataManager.getJobs()) {
            if (random.nextDouble() < 0.1) {
                int zoneId = job.getZoneId();
                Zone zone = dataContainer.getGeoData().getZones().get(zoneId);
                boolean isRural = true;
                for (DefaultDwellingTypes.DefaultDwellingTypeImpl dTp : DefaultDwellingTypes.DefaultDwellingTypeImpl.values()) {
                    if (zone.getDevelopment().isThisDwellingTypeAllowed(dTp)) isRural = false;

                }
                if (isRural) {
                    try {
                        Zone urbanZone = zoneSampler.sampleObject();
                        Coordinate urbanCoord = urbanZone.getRandomCoordinate(random);
                        job.relocateJob(urbanZone, urbanCoord);
                    } catch (SampleException e) {
                        logger.warn(e);
                    }
                }
            }
        }


        ConcurrentExecutor<Void> executor = ConcurrentExecutor.cachedService();
        Map<String, List<Job>> jobsByType = jobDataManager.getJobs().stream().collect(Collectors.groupingBy(Job::getType));

        for (String jt : jobsByType.keySet()) {
            int newJobs = (int) (jobsByType.get(jt).size() * properties.jobData.growthRateInPercentByJobType.get(jt)) / 100;
            for (int i = 0; i < newJobs; i++) {
                try {
                    Zone zone = zoneSampler.sampleObject();
                    int id = jobDataManager.getNextJobId();
                    Coordinate coordinate = zone.getRandomCoordinate(random);
                    final Job job = factory.createJob(id, zone.getZoneId(), coordinate, -1, jt);
                    jobDataManager.addJob(job);
                    if (id == SiloUtil.trackJj) {
                        SiloUtil.trackWriter.println("Job " + id + " of type " + jt +
                                " was newly created in zone " + zone + " based on exogenous forecast.");
                    }
                } catch (SampleException e) {
                    logger.warn(e);
                }
            }
        }
        executor.execute();
    }
}
