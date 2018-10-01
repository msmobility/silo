package de.tum.bgu.msm.data;

public class School {
    private final int id;
    private final int type;
    private final int capacity;
    private final Location location;
    private int currentOccupancy;

    School (int id, int type, int capacity,Location location) {
        this.id = id;
        this.location = location;
        this.type = type;
        this.capacity = capacity;
    }

    public int determineZoneId() {
        if (location instanceof MicroLocation) {
            return ((MicroLocation) location).getZone().getId();
        } else if (location instanceof Zone) {
            return ((Zone) location).getId();
        } else {
            throw new IllegalStateException("No implementation for Location of type " + location.getClass().getName());
        }
    }

    public int getId () {
        return id;
    }

    public int getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }
}
