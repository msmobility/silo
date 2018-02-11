package de.tum.bgu.msm.data;

import java.util.LinkedHashSet;
import java.util.Set;

public class ZoneImpl implements Zone {

    private final int id;
    private final int msa;
    private final float area;

    private final Set<Job> jobs = new LinkedHashSet<>();
    private final Set<Dwelling> dwellings = new LinkedHashSet<>();

    private Region region;

    public ZoneImpl(int id, int msa, float area) {
        this.id = id;
        this.msa = msa;
        this.area = area;
    }

    @Override
    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Region getRegion() {
        return this.region;
    }

    @Override
    public int getMsa() {
        return this.msa;
    }

    @Override
    public float getArea() {
        return area;
    }
}
