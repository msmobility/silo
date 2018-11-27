package de.tum.bgu.msm.data.maryland;

import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.geo.ZoneImpl;

public final class MstmZone extends ZoneImpl {

    private final int puma;
    private final int simplifiedPuma;
    private final County county;

    MstmZone(int id, int msa, float area, int puma, int simplifiedPuma, County county, Region region) {
        super(id, msa, area, region);
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
}
