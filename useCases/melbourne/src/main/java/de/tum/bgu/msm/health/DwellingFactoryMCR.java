package de.tum.bgu.msm.health;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import org.locationtech.jts.geom.Coordinate;

public class DwellingFactoryMCR implements DwellingFactory {

    private DwellingFactory delegate;

    public DwellingFactoryMCR(DwellingFactory delegate) {
        this.delegate = delegate;

    }
    @Override
    public NoiseDwellingMCR createDwelling(int id, int zoneId, Coordinate coordinate, int hhId, DwellingType type, int bedrooms, int quality, int price, int year) {
        return new NoiseDwellingMCR(delegate.createDwelling(id, zoneId, coordinate, hhId, type, bedrooms, quality, price, year));
    }
}
