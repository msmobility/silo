package de.tum.bgu.msm.data.job;

import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.data.Zone;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.utils.objectattributes.attributable.Attributes;
import org.matsim.utils.objectattributes.attributable.AttributesImpl;

import java.util.Optional;

public class JobImpl implements Job, MicroLocation {

    private final int id;
    private int workerId;
    private final String type;
    private Coordinate coordinate;
    private int zoneId;

    private final Attributes attributes = new AttributesImpl();

    JobImpl (int id, int zoneId, Coordinate coordinate, int workerId, String type) {
        this.id = id;
        this.coordinate = coordinate;
        this.workerId = workerId;
        this.type = type;
        this.zoneId = zoneId;
    }

    @Override
    public int getId () {
        return id;
    }

    @Override
    public int getWorkerId() {
        return workerId;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public void setWorkerID(int personID) {
        this.workerId = personID;
    }

    @Override
    public void relocateJob(Zone newZone, Coordinate newCoordinate) {
        this.zoneId = newZone.getZoneId();
        this.coordinate = newCoordinate;
    }

    @Override
    public Optional<Integer> getStartTimeInSeconds() {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getWorkingTimeInSeconds() {
        return Optional.empty();
    }

    @Override
    public Optional<Object> getAttribute(String key) {
        return Optional.ofNullable(attributes.getAttribute(key));
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.putAttribute(key, value);
    }

    @Override
    public String toString() {
        return "Attributes of job       " + id
//                + "\nLocated in zone         " + zone
                + "\nLocated at         " + coordinate.toString() // TODO implement toString methods
                + "\nFilled by person        " + workerId
                + "\nJob type                " + type;
    }

    @Override
    public int getZoneId() {
        return this.zoneId;
    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Job && ((Job) o).getId() == this.id;
    }
}
