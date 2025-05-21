package de.tum.bgu.msm.data.job;

import org.locationtech.jts.geom.Coordinate;

public class JobFactoryMEL implements JobFactory {

    @Override
    public Job createJob(int id, int zoneId, Coordinate coordinate, int workerId, String type) {
        return new JobMEL(id, zoneId, coordinate, workerId, type);
    }
}
