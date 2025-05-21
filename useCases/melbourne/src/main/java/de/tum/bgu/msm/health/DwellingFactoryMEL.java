package de.tum.bgu.msm.health;

import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import org.locationtech.jts.geom.Coordinate;

public class DwellingFactoryMEL implements DwellingFactory {

    private DwellingFactory delegate;

    public DwellingFactoryMEL(DwellingFactory delegate) {
        this.delegate = delegate;

    }
    @Override
    public NoiseDwellingMEL createDwelling(int id, int zoneId, Coordinate coordinate, int hhId, DwellingType type, int bedrooms, int quality, int price, int year) {
        return new NoiseDwellingMEL(delegate.createDwelling(id, zoneId, coordinate, hhId, type, bedrooms, quality, price, year));
    }
}
