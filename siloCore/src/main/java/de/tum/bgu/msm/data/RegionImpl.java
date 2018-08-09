package de.tum.bgu.msm.data;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class RegionImpl implements Region {

    private final Set<Zone> zones = new LinkedHashSet<>();
    private final int id;

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


}
