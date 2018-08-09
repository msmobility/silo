package de.tum.bgu.msm;

public enum Implementation {
    MUNICH(2011),
    MARYLAND(2000),
    //TODO: Define for Cape-Town
    CAPE_TOWN(2011),
    MSP(2000);

    public final int BASE_YEAR;

    Implementation(int year) {
        this.BASE_YEAR = year;
    }


}
