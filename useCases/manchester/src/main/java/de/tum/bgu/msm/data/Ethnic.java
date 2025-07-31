package de.tum.bgu.msm.data;

public enum Ethnic {
    white (1),
    mix (2),
    asia (3),
    black(4),

    other(5);

    private final int ethnicCode;

    Ethnic(int ethnicCode) {
        this.ethnicCode = ethnicCode;
    }

    public int getEthnicCode() {
        return ethnicCode;
    }

}
