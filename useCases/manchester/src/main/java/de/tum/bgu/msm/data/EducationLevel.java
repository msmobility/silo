package de.tum.bgu.msm.data;

public enum EducationLevel {
    no (0),
    low (1),
    medium (2),
    high(3);

    private final int educationCode;

    EducationLevel(int ethnicCode) {
        this.educationCode = ethnicCode;
    }

    public int getEthnicCode() {
        return educationCode;
    }

}
