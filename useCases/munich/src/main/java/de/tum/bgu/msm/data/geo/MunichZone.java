package de.tum.bgu.msm.data.geo;

import de.tum.bgu.msm.data.AreaTypes;
import de.tum.bgu.msm.data.Region;

public class MunichZone extends ZoneImpl {

    private final AreaTypes.SGType areaType;
    private final double ptDistance;

    public MunichZone(int id, float area, AreaTypes.SGType areaType, double initialPTDistance, Region region) {
        super(id, area, region);
        this.areaType = areaType;
        this.ptDistance = initialPTDistance;
    }

    public double getPTDistance() {
        return ptDistance;
    }

    public AreaTypes.SGType getAreaType() {
        return areaType;
    }
}