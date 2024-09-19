package de.tum.bgu.msm.data;

public enum Ethnic {
    white (0),
    mix (1),
    asia (2),
    black(3),

    other(4);

    private final int ethnicCode;

    Ethnic(int ethnicCode) {
        this.ethnicCode = ethnicCode;
    }

    public int getEthnicCode() {
        return ethnicCode;
    }

}
