package de.tum.bgu.msm.data;

import java.util.Collection;

public interface Region extends Id{

    Collection<Zone> getZones();

    boolean addZone(Zone zone);
}
