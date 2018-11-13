package de.tum.bgu.msm;

public enum Implementation {
    MUNICH(2011),
    MARYLAND(2000),
    //TODO: Define base year for Cape-Town
    CAPE_TOWN(2011),
    PERTH(2010),
    MSP(2000),
    KAGAWA(2010);

    public final int BASE_YEAR;

    Implementation(int year) {
        this.BASE_YEAR = year;
    }


}
