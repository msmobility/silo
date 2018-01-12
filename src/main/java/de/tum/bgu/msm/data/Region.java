package de.tum.bgu.msm.data;

import java.util.Collection;

public interface Region {

    int getId();

    Collection<Zone> getZones();

    boolean addZone(Zone zone);

    int getNumberOfHouseholds();
}
