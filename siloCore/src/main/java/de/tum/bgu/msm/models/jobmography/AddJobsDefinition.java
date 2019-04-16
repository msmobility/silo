package de.tum.bgu.msm.models.jobmography;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobFactory;
import de.tum.bgu.msm.utils.SiloUtil;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class AddJobsDefinition extends EmploymentChangeDefinition implements Callable {

    public final List<Integer> ids;
    public final GeoData geoData;
    private final JobFactory factory;
    private final Random random;

    public AddJobsDefinition(Zone zone, int change, String jobType, DataContainer dataContainer, JobFactory factory, Random random) {
        super(zone, change, jobType, dataContainer);
        this.ids = jobDataManager.getNextJobIds(change);
        this.geoData = dataContainer.getGeoData();
        this.factory = factory;
        this.random = random;
    }

    @Override
    public Object call() {

        for (int i = 0; i < changes; i++) {
            int id = ids.get(i);
            Coordinate coordinate = zone.getRandomCoordinate(random);
            final Job job = factory.createJob(id, zone.getZoneId(), coordinate, -1, jobType);
            jobDataManager.addJob(job);
            if (id == SiloUtil.trackJj) {
                SiloUtil.trackWriter.println("Job " + id + " of type " + jobType +
                        " was newly created in zone " + zone + " based on exogenous forecast.");
            }
        }
        return null;
    }
}
