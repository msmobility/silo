package de.tum.bgu.msm.data.job;

import org.locationtech.jts.geom.Coordinate;

public class JobFactoryImpl implements JobFactory {

    @Override
    public Job createJob(int id, int zoneId, Coordinate coordinate, int workerId, String type) {
        return new JobImpl(id, zoneId, coordinate, workerId, type);
    }
}
