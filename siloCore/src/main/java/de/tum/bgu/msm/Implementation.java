package de.tum.bgu.msm;

public enum Implementation {
    MUNICH(2011),
    MARYLAND(2000),
    //TODO: Define base year for Cape-Town
    CAPE_TOWN(2011),
    PERTH(2011),
    MSP(2000),
    KAGAWA(2010),
    // to do: set base year for Austin as 2010 or 2000, depending on available data
	AUSTIN(2010);

    public final int BASE_YEAR;

    Implementation(int year) {
        this.BASE_YEAR = year;
    }


}
