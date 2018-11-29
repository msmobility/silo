package de.tum.bgu.msm.data.school;

import com.vividsolutions.jts.geom.Coordinate;

public interface SchoolFactory {
    School createSchool(int id, int type, int capacity, int occupancy, Coordinate coordinate, int zoneId);
}
