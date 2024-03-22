package de.tum.bgu.msm.data.dwelling;

public enum DwellingUsage {
    GROUP_QUARTER_OR_DEFAULT, OWNED, RENTED, VACANT;

    public static DwellingUsage valueOf(int code) {
        switch (code) {
            case 1:
            case 2:
                return OWNED;
            case 3:
            case 4:
                return RENTED;
            case 5:
                return GROUP_QUARTER_OR_DEFAULT;
            default:
                throw new RuntimeException("Undefined dwelling usage code " + code);
        }
    }
}
