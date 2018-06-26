package de.tum.bgu.msm.data.munich;

import de.tum.bgu.msm.data.ZoneImpl;
import org.matsim.api.core.v01.Coord;
import org.opengis.feature.simple.SimpleFeature;

public class MunichZone extends ZoneImpl {

    private final Coord coord;
    private double ptDistance;
    private SimpleFeature zoneFeature;
    public SimpleFeature getZoneFeature() {
        return zoneFeature;
    }
    public void setZoneFeature(SimpleFeature zoneFeature) {
        this.zoneFeature = zoneFeature;
    }

    public MunichZone(int id, int msa, float area, Coord coord, double initialPTDistance) {
        super(id, msa, area);
        this.coord = coord;
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
}
