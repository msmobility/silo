package de.tum.bgu.msm.data;

public enum ManchesterDwellingUsage {
    GROUP_QUARTER_OR_DEFAULT, OWNED, RENTED, VACANT;

    public static ManchesterDwellingUsage valueOf(int code) {
        switch (code) {
            case 0:
                return GROUP_QUARTER_OR_DEFAULT;
            case 1:
                return OWNED;
            case 2:
                return RENTED;
            case 3:
                return VACANT;
            default:
                throw new RuntimeException("Undefined dwelling usage code " + code);
        }
    }
}
