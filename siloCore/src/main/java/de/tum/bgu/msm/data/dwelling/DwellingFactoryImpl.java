package de.tum.bgu.msm.data.dwelling;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.data.Location;

public class DwellingFactoryImpl implements DwellingFactory {

    @Override
    public Dwelling createDwelling(int id, int zoneId, Coordinate coordinate, int hhId, DwellingType type, int bedrooms, int quality, int price, float restriction, int year) {
        return new DwellingImpl(id, zoneId, coordinate, hhId, type, bedrooms, quality, price, restriction, year);
    }
}
