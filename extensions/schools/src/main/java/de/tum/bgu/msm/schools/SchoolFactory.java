package de.tum.bgu.msm.schools;


import org.locationtech.jts.geom.Coordinate;

public interface SchoolFactory {
    School createSchool(int id, int type, int capacity, int occupancy, Coordinate coordinate, int zoneId);
}
