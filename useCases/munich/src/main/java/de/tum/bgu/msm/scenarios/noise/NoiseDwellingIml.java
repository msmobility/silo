package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.DwellingUsage;
import de.tum.bgu.msm.matsim.noise.NoiseDwelling;
import org.locationtech.jts.geom.Coordinate;

import java.util.Optional;

public class NoiseDwellingIml implements NoiseDwelling {

    private final Dwelling delegate;
    private double immission;

    public NoiseDwellingIml(Dwelling delegate) {
        this.delegate = delegate;
    }

    @Override
    public Coordinate getCoordinate() {
        return delegate.getCoordinate();
    }

    @Override
    public int getZoneId() {
        return delegate.getZoneId();
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public int getQuality() {
        return delegate.getQuality();
    }

    @Override
    public int getResidentId() {
        return delegate.getResidentId();
    }

    @Override
    public int getPrice() {
        return delegate.getPrice();
    }

    @Override
    public DwellingType getType() {
        return delegate.getType();
    }

    @Override
    public int getBedrooms() {
        return delegate.getBedrooms();
    }

    @Override
    public int getYearBuilt() {
        return delegate.getYearBuilt();
    }

    @Override
    public void setResidentID(int residentID) {
        delegate.setResidentID(residentID);
    }

    @Override
    public void setQuality(int quality) {
        delegate.setQuality(quality);
    }

    @Override
    public void setPrice(int price) {
        delegate.setPrice(price);
    }

    @Override
    public void setFloorSpace(int floorSpace) {
        delegate.setFloorSpace(floorSpace);
    }

    @Override
    public int getFloorSpace() {
        return delegate.getFloorSpace();
    }

    @Override
    public void setCoordinate(Coordinate coordinate) {
        delegate.setCoordinate(coordinate);
    }

    @Override
    public void setUsage(DwellingUsage usage) {
        delegate.setUsage(usage);
    }

    @Override
    public DwellingUsage getUsage() {
        return delegate.getUsage();
    }

    @Override
    public Optional<Object> getAttribute(String key) {
        return delegate.getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        delegate.setAttribute(key, value);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public void setNoiseImmision(double lden) {
        this.immission = lden;
    }

    @Override
    public double getNoiseImmission() {
        return this.immission;
    }
}
