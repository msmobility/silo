package de.tum.bgu.msm.data.munich;

import de.tum.bgu.msm.data.AreaTypes;
import de.tum.bgu.msm.data.Region;
import org.matsim.api.core.v01.Coord;

import de.tum.bgu.msm.data.geo.ZoneImpl;

public class MunichZone extends ZoneImpl {

    private final Coord coord;
    private final AreaTypes.SGType areaType;
    private final double ptDistance;

    public MunichZone(int id, int msa, float area, Coord coord, AreaTypes.SGType areaType, double initialPTDistance, Region region) {
        super(id, msa, area, region);
        this.coord = coord;
        this.areaType = areaType;
        this.ptDistance = initialPTDistance;
    }

    public Coord getCoord() {
        return coord;
    }

    public double getPTDistance() {
        return ptDistance;
    }

    public AreaTypes.SGType getAreaType() {
        return areaType;
    }
}