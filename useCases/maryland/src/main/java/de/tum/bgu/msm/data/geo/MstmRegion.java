package de.tum.bgu.msm.data.geo;

import de.tum.bgu.msm.data.geo.RegionImpl;

public class MstmRegion extends RegionImpl {

    private double schoolQuality;
    private double crimeRate;

    public MstmRegion(int id) {
        super(id);
    }

    public double getSchoolQuality() {
        return this.schoolQuality;
    }

    public void setSchoolQuality(double schoolQuality) {
        this.schoolQuality = schoolQuality;
    }

    public double getCrimeRate() {
        return crimeRate;
    }

    public void setCrimeRate(double crimeRate) {
        this.crimeRate = crimeRate;
    }

}
