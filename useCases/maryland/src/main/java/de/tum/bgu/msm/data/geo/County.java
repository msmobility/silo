package de.tum.bgu.msm.data.geo;

public class County {

    private final int id;
    private double crimeRate;

    public County(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public double getCrimeRate() {
        return crimeRate;
    }

    public void setCrimeRate(double crimeRate) {
        this.crimeRate = crimeRate;
    }
}
