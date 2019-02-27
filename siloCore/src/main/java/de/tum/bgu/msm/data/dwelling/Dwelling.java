package de.tum.bgu.msm.data.dwelling;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.data.Id;
import de.tum.bgu.msm.data.Location;

public interface Dwelling extends Location, Id {

    int getQuality();

    int getResidentId();

    int getPrice();

    DwellingType getType();

    int getBedrooms();

    int getYearBuilt();

    float getRestriction();

    void setResidentID(int residentID);

    void setQuality(int quality);

    void setPrice(int price);

    void setRestriction(float restriction);

    void setFloorSpace(int floorSpace);

    int getFloorSpace();

    void setCoordinate(Coordinate coordinate);

    //TODO: magic numbers
    void setBuildingSize(int buildingSize);

    int getBuildingSize();

    void setUsage(DwellingUsage usage);

    DwellingUsage getUsage();

    //TODO: magic numbers
    //TODO: use case specific
    void setYearConstructionDE(int yearConstructionDE);

    //TODO: use case specific
    int getYearConstructionDE();

}
