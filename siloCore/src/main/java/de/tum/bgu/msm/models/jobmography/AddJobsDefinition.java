package de.tum.bgu.msm.models.jobmography;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.job.JobFactory;
import de.tum.bgu.msm.data.job.JobFactoryImpl;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.job.Job;

import java.util.List;
import java.util.concurrent.Callable;

public class AddJobsDefinition extends EmploymentChangeDefinition implements Callable {

    public final List<Integer> ids;
    public final GeoData geoData;
    private final JobFactory factory;

    public AddJobsDefinition(Zone zone, int change, String jobType, SiloDataContainer dataContainer, JobFactory factory) {
        super(zone, change, jobType, dataContainer);
        this.ids = jobDataManager.getNextJobIds(change);
        this.geoData = dataContainer.getGeoData();
        this.factory = factory;
    }

    @Override
    public Object call() {

        for (int i = 0; i < changes; i++) {
            int id = ids.get(i);
            synchronized (Job.class) {
                Coordinate coordinate = null;
                if (Properties.get().main.useMicrolocation) {
                    coordinate = zone.getRandomCoordinate();
                }
                final Job job = factory.createJob(id, zone.getZoneId(), coordinate, -1, jobType);
                ((JobFactoryImpl) factory).assignWorkingTimesFromDistribution(job);
                jobDataManager.addJob(job);
            }
            if (id == SiloUtil.trackJj) {
                SiloUtil.trackWriter.println("Job " + id + " of type " + jobType +
                        " was newly created in zone " + zone + " based on exogenous forecast.");
            }
        }
        return null;
    }
}
