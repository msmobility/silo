package de.tum.bgu.msm.data.dwelling;


import org.locationtech.jts.geom.Coordinate;

public interface DwellingFactory {

    Dwelling createDwelling(int id, int zoneId, Coordinate coordinate,
                            int hhId, DwellingType type, int bedrooms,
                            int quality, int price, int year);

}
