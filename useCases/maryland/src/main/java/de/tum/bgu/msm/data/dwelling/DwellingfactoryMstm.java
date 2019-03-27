package de.tum.bgu.msm.data.dwelling;

import org.locationtech.jts.geom.Coordinate;

public class DwellingfactoryMstm implements DwellingFactory {
    @Override
    public Dwelling createDwelling(int id, int zoneId, Coordinate coordinate, int hhId, DwellingType type, int bedrooms, int quality, int price) {
        return new DwellingMstm(id, zoneId, coordinate, hhId, type, bedrooms, quality, price);
    }
}
