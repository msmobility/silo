package de.tum.bgu.msm.matsim;

import de.tum.bgu.msm.data.Id;
import org.matsim.api.core.v01.Coord;

import java.util.List;

public interface ZoneConnectorManager {
    List<Coord> getCoordsForZone(int zoneId);

    public enum ZoneConnectorMethod {RANDOM, WEIGHTED_BY_POPULATION, GEOMETRIC_CENTROID}
}
