package de.tum.bgu.msm.data.geo;

import de.tum.bgu.msm.data.AreaTypes;
import de.tum.bgu.msm.data.Region;

public class ZoneMuc extends ZoneImpl {

    private final AreaTypes.SGType areaType;
    private final double ptDistance_m;
    private final int TAZ;

    public ZoneMuc(int id, float area, AreaTypes.SGType areaType, double initialPTDistance_m, Region region, int taz) {

        super(id, area, region);
        this.areaType = areaType;
        this.ptDistance_m = initialPTDistance_m;
        this.TAZ = taz;
    }

    public double getPTDistance_m() {
        return ptDistance_m;
    }

    public AreaTypes.SGType getAreaTypeSG() {
        return areaType;
    }
}