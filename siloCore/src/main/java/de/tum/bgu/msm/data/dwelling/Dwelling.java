package de.tum.bgu.msm.data.dwelling;

import de.tum.bgu.msm.data.Id;
import de.tum.bgu.msm.data.MicroLocation;
import org.locationtech.jts.geom.Coordinate;

import java.util.Map;
import java.util.Optional;

public interface Dwelling extends MicroLocation, Id {

    int getQuality();

    int getResidentId();

    int getPrice();

    DwellingType getType();

    int getBedrooms();

    int getYearBuilt();

    void setResidentID(int residentID);

    void setQuality(int quality);

    void setPrice(int price);

    void setFloorSpace(int floorSpace);

    int getFloorSpace();

    void setCoordinate(Coordinate coordinate);

    void setUsage(DwellingUsage usage);

    DwellingUsage getUsage();

    Optional<Object> getAttribute(String key);

    void setAttribute(String key, Object value);
}
