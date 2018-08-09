package de.tum.bgu.msm.data.maryland;

import de.tum.bgu.msm.data.ZoneImpl;

public final class MstmZone extends ZoneImpl {

    private final int puma;
    private final int simplifiedPuma;
    private final County county;
    private double schoolQuality;

    MstmZone(int id, int msa, float area, int puma, int simplifiedPuma, County county) {
        super(id, msa, area);
        this.puma = puma;
        this.simplifiedPuma = simplifiedPuma;
        this.county = county;
    }

    public double getSchoolQuality() {
        return this.schoolQuality;
    }

    public void setSchoolQuality(double quality) {
        this.schoolQuality = quality;
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
