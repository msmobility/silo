package de.tum.bgu.msm.data.job;


import org.locationtech.jts.geom.Coordinate;

public interface JobFactory {
    Job createJob(int id, int zoneId, Coordinate coordinate, int workerId, String type);
}
