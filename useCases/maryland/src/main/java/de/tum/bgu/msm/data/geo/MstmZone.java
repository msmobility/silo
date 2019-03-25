package de.tum.bgu.msm.data.geo;

import de.tum.bgu.msm.data.Region;

public final class MstmZone extends ZoneImpl {

    private final int puma;
    private final int simplifiedPuma;
    private final County county;
    private final int msa;

    public MstmZone(int id, int msa, float area, int puma, int simplifiedPuma, County county, Region region) {
        super(id, area, region);
        this.msa = msa;
        this.puma = puma;
        this.simplifiedPuma = simplifiedPuma;
        this.county = county;
    }

    public int getPuma() {
        return puma;
    }

    public int getSimplifiedPuma() {
        return simplifiedPuma;
    }

    public County getCounty() {
        return county;
    }

    public int getMsa() {
        return msa;
    }
}
