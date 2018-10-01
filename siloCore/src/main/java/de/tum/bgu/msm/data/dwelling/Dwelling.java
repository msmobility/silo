package de.tum.bgu.msm.data.dwelling;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.data.HouseholdType;
import de.tum.bgu.msm.data.Location;

import java.util.EnumMap;

public interface Dwelling extends Location {

    int getId();

    int getQuality();

    int getResidentId();

    int getPrice();

    DwellingType getType();

    int getBedrooms();

    int getYearBuilt();

    float getRestriction();

    double getUtilOfResident();

    void setResidentID(int residentID);

    void setQuality(int quality);

    void setPrice(int price);

    void setRestriction(float restriction);

    void setUtilitiesByHouseholdType(EnumMap<HouseholdType, Double> utilitiesByHouseholdType);

    void setUtilOfResident(double utilOfResident);

    void setFloorSpace(int floorSpace);

    int getFloorSpace();

    void setCoordinate(Coordinate coordinate);

    //TODO: magic numbers
    void setBuildingSize(int buildingSize);

    int getBuildingSize();

    void setUsage(DwellingImpl.Usage usage);

    DwellingImpl.Usage getUsage();

    //TODO: magic numbers
    //TODO: use case specific
    void setYearConstructionDE(int yearConstructionDE);

    //TODO: use case specific
    int getYearConstructionDE();
}
