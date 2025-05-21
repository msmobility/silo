package de.tum.bgu.msm.data;

import de.tum.bgu.msm.data.geo.ZoneImpl;
import org.locationtech.jts.geom.Coordinate;

public class ZoneMEL extends ZoneImpl {

    private final AreaTypes.RType areaType;
    private final Coordinate popCentroidCoord;
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

    public String getLsoaCode() {
        return lsoaCode;
    }

    public void setLsoaCode(String lsoaCode) {
        this.lsoaCode = lsoaCode;
    }

    public int getImd10() {
        return imd10;
    }

    public void setImd10(int imd10) {
        this.imd10 = imd10;
    }
}