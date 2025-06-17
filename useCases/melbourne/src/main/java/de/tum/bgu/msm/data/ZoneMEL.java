package de.tum.bgu.msm.data;

import de.tum.bgu.msm.data.geo.ZoneImpl;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

public class ZoneMEL extends ZoneImpl {

    private final AreaTypes.RType areaType;
    private Coordinate popCentroidCoord;
    private String lsoaCode;
    private int imd10; //index of multiple deprivation 2010

    public ZoneMEL(int id, float area, AreaTypes.RType areaType, Coordinate coordinate, Region region) {

        super(id, area, region);
        this.areaType = areaType;
        this.popCentroidCoord = coordinate;
    }

    public AreaTypes.RType getAreaTypeRType() {
        return areaType;
    }

    public Coordinate getPopCentroidCoord() {
        return popCentroidCoord;
    }

    public void setPopCentroidCoord(Coordinate popCentroidCoord) {
        this.popCentroidCoord = popCentroidCoord;
    }

    public Geometry getGeometry() {
        if (getZoneFeature() != null) {
            return (Geometry) getZoneFeature().getDefaultGeometry();
        } else {
            return null;
        }
    }

    public String getLsoaCode() {
        return lsoaCode;
    }

    public void setCatchmentCode(String lsoaCode) {
        this.lsoaCode = lsoaCode;
    }

    public int getImd10() {
        return imd10;
    }

    public void setSocioEconomicDisadvantageDeciles(int imd10) {
        this.imd10 = imd10;
    }
}