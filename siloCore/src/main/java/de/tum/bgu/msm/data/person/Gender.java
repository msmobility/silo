package de.tum.bgu.msm.data.person;

public enum Gender {
    MALE() {
        @Override
        public Gender opposite() {
            return FEMALE;
        }

        @Override
        public int getCode() {
            return 1;
        }
    },
    FEMALE() {
        @Override
        public Gender opposite() {
            return MALE;
        }

        @Override
        public int getCode() {
            return 2;
        }
    };

    public static Gender valueOf(int code) {
        switch(code) {
            case 1:
                return MALE;
            case 2:
                return FEMALE;
            default:
                throw new IllegalArgumentException(String.format("Code %d not valid. Use 1 (MALE) or 2 (FEMALE).", code));
        }
    }

    public abstract Gender opposite();
    public abstract int getCode();
}
