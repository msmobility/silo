package de.tum.bgu.msm.data.dwelling;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CapeTownDwellingTypes implements DwellingTypes {

    private final static List<DwellingType> TYPES = Collections.unmodifiableList(Arrays.asList(DwellingTypeCapeTown.values()));

    @Override
    public DwellingType valueOf(String tp) {
        return DwellingTypeCapeTown.valueOf(tp);
    }

    @Override
    public List<DwellingType> getTypes() {
        return TYPES;
    }

    public enum DwellingTypeCapeTown implements DwellingType {

        /**
         * Refers to "Formal house", "Backyard formal", "Other" from the South African census
         */
        FORMAL(0.25f,0.01f),
        /**
         * Refers to "Townhouse", "Semi-detached" from the South African census
         */
        SEMIDETACHED(0.22f,0.03f),
        /**
         * Refers to "Apartment", "Cluster" from the South African census
         */
        MULTIFAMILY (0.07f,0.05f),
        /**
         * Refers to "Backyard informal" from the South African census
         */
        BACKYARD_INFORMAL (0.015f,0.03f),
        /**
         * Refers to "Informal", "Caravan or tent", "Traditional" from the South African census
         */
        INFORMAL(0.010f,0.02f);

        private final float acresNeeded;
        private final float structuralVacancy;

        DwellingTypeCapeTown(float acresNeeded, float structuralVacancy) {
            this.acresNeeded = acresNeeded;
            this.structuralVacancy = structuralVacancy;
        }

        @Override
        public float getAreaPerDwelling() {
            return acresNeeded;
        }

        @Override
        public float getStructuralVacancyRate() {
            return structuralVacancy;
        }


        public int getId() {
            return this.ordinal();
        }

        public static DwellingTypeCapeTown valueOf(int code){
            switch(code){
                case 1: return FORMAL;
                case 2: return SEMIDETACHED;
                case 3: return MULTIFAMILY;
                case 4: return BACKYARD_INFORMAL;
                case 5: return INFORMAL;
                default: throw new RuntimeException("Housing Type not found: "+code);
            }
        }
    }
}
