package de.tum.bgu.msm.data.person;

public enum Occupation {

    TODDLER {
        @Override
        public int getCode() {
            return 0;
        }
    },
    STUDENT {
        @Override
        public int getCode() {
            return 3;
        }
    },
    EMPLOYED {
        @Override
        public int getCode() {
            return 1;
        }
    },
    UNEMPLOYED {
        @Override
        public int getCode() {
            return 2;
        }
    },
    RETIREE {
        @Override
        public int getCode() {
            return 4;
        }
    };

    public static Occupation valueOf(int code) {
        switch(code) {
            case 0:
                return TODDLER;
            case 1:
                return EMPLOYED;
            case 2:
                return UNEMPLOYED;
            case 3:
                return STUDENT;
            case 4:
                return RETIREE;
            default:
                throw new IllegalArgumentException(String.format("Code %d not valid.", code));
        }
    }

    public abstract int getCode();

}
