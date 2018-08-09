package de.tum.bgu.msm.data.maryland;

import de.tum.bgu.msm.data.RegionImpl;

public class MstmRegion extends RegionImpl {

    private double schoolQuality;
    private double crimeRate;

    public MstmRegion(int id) {
        super(id);
    }

    public double getSchoolQuality() {
        return this.schoolQuality;
    }

    public void calculateRegionalSchoolQuality() {
        this.schoolQuality = getZones().stream().mapToDouble(
                zone -> ((MstmZone) zone).getSchoolQuality()).average().getAsDouble();
    }

    public double getCrimeRate() {
        return crimeRate;
    }

    public void setCrimeRate(double crimeRate) {
        this.crimeRate = crimeRate;
    }

}
