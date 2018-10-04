package de.tum.bgu.msm.data.school;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.data.MicroLocation;

public class SchoolImpl implements School, MicroLocation {
    private final int id;
    private final int type;
    private double startTimeInSeconds;
    private double studyTimeInSeconds;
    private final int capacity;
    private int currentOccupancy;
    private final Coordinate coordinate;
    private final int zoneId;


    public SchoolImpl(int id, int type, int capacity, Coordinate coordinate, int zoneId) {
        this.id = id;
        this.type = type;
        this.capacity = capacity;
        this.coordinate = coordinate;
        this.zoneId = zoneId;
    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setSchoolStudyingTime(double startTimeInSeconds, double studyTimeInSeconds) {
        this.startTimeInSeconds = startTimeInSeconds;
        this.studyTimeInSeconds = studyTimeInSeconds;
    }

    public double getStartTimeInSeconds() {
        return startTimeInSeconds;
    }

    public double getStudyTimeInSeconds() {
        return studyTimeInSeconds;
    }

    @Override
    public int getZoneId() {
        return zoneId;
    }
}
