package de.tum.bgu.msm.data.dwelling;

import de.tum.bgu.msm.data.Id;
import de.tum.bgu.msm.data.Location;
import org.locationtech.jts.geom.Coordinate;

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

    void setUsage(DwellingUsage usage);

    DwellingUsage getUsage();


}
