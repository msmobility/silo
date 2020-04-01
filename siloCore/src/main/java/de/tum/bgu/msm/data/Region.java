package de.tum.bgu.msm.data;

import java.util.Collection;
import java.util.Map;

public interface Region extends Id {

    Collection<Zone> getZones();

    boolean addZone(Zone zone);

    Map<String, Object> getAttributes();
}
