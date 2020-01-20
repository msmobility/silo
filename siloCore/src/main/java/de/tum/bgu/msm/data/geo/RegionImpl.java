package de.tum.bgu.msm.data.geo;

import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;

import java.util.*;

public class RegionImpl implements Region {

    private final Set<Zone> zones = new LinkedHashSet<>();
    private final int id;
    private final Map<String, Object> attributes = new HashMap<>();

    public RegionImpl(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Collection<Zone> getZones() {
        return Collections.unmodifiableSet(zones);
    }

    @Override
    public boolean addZone(Zone zone) {
        return zones.add(zone);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "Region " + id +": #zones= " + zones.size();
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Region && ((Region) o).getId() == this.id;
    }
}
