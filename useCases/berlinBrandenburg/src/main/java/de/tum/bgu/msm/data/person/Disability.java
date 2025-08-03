package de.tum.bgu.msm.data.person;

public enum Disability {

    WITHOUT (0),
    MENTAL(1),
    PHYSICAL(2);

    private final int disabilityCode;

    Disability(int disabilityCode) {
        this.disabilityCode = disabilityCode;
    }

    public int getDisabilityCode() {
        return disabilityCode;
    }
}
