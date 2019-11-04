package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.dwelling.DwellingImpl;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import org.locationtech.jts.geom.Coordinate;

public class NoiseDwellingFactory implements DwellingFactory {

    private DwellingFactory delegate;

    public NoiseDwellingFactory(DwellingFactory delegate) {
        this.delegate = delegate;
    }


    @Override
    public Dwelling createDwelling(int id, int zoneId, Coordinate coordinate, int hhId, DwellingType type, int bedrooms, int quality, int price, int year) {
        return new NoiseDwellingIml(delegate.createDwelling(id, zoneId, coordinate, hhId, type, bedrooms, quality, price, year));
    }
}
