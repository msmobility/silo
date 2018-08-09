package de.tum.bgu.msm.data.munich;

import de.tum.bgu.msm.data.AreaTypes;
import org.matsim.api.core.v01.Coord;

import de.tum.bgu.msm.data.ZoneImpl;

public class MunichZone extends ZoneImpl {

    private final Coord coord;
    private final AreaTypes.SGType areaType;
    private double ptDistance;

    public MunichZone(int id, int msa, float area, Coord coord, AreaTypes.SGType areaType, double initialPTDistance) {
        super(id, msa, area);
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

    public void setPtDistance(double ptDistance) {
        this.ptDistance = ptDistance;
    }

    public AreaTypes.SGType getAreaType() {
        return areaType;
    }
}