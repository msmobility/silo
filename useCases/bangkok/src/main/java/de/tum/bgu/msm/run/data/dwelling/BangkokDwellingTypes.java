package de.tum.bgu.msm.run.data.dwelling;

import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.DwellingTypes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BangkokDwellingTypes implements DwellingTypes {

    private final static List<DwellingType> TYPES = Collections.unmodifiableList(Arrays.asList(DwellingTypeBangkok.values()));

    @Override
    public DwellingType valueOf(String tp) {
        return DwellingTypeBangkok.valueOf(tp);
    }

    @Override
    public List<DwellingType> getTypes() {
        return TYPES;
    }

    public enum DwellingTypeBangkok implements DwellingType {

        /**
         * Refers to "Condo"
         */
        HIGH_RISE_CONDOMINIUM_30(0.000519f,0.01f),
        /**
         * Refers to "Condo"
         */
        HIGH_RISE_CONDOMINIUM_50(0.000889f,0.01f),
        /**
         * Refers to "apartment"
         */
        LOW_RISE_CONDOMINIUM_30(0.001161f,0.03f),
        /**
         * Refers to "apartment"
         */
        LOW_RISE_CONDOMINIUM_50(0.001729f,0.03f),
        /**
         * Refers to "detached house"
         */
        DETATCHED_HOUSE_120(0.01482f,0.05f),
        /**
         * Refers to "detached house"
         */
        DETATCHED_HOUSE_200(0.0247f,0.05f);

        private final float acresNeeded;
        private final float structuralVacancy;

        DwellingTypeBangkok(float acresNeeded, float structuralVacancy) {
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

        public static DwellingTypeBangkok valueOf(int code){
            switch(code){
                case 1: return HIGH_RISE_CONDOMINIUM_30;
                case 2: return HIGH_RISE_CONDOMINIUM_50;
                case 3: return LOW_RISE_CONDOMINIUM_30;
                case 4: return LOW_RISE_CONDOMINIUM_50;
                case 5: return DETATCHED_HOUSE_120;
                case 6: return DETATCHED_HOUSE_200;
                default: throw new RuntimeException("Housing Type not found: "+code);
            }
        }

        public int getsizeOfDwelling(){
            switch(this){
                case HIGH_RISE_CONDOMINIUM_30: return 30;
                case HIGH_RISE_CONDOMINIUM_50: return 50;
                case LOW_RISE_CONDOMINIUM_30: return 30;
                case LOW_RISE_CONDOMINIUM_50: return 50;
                case DETATCHED_HOUSE_120: return 120;
                case DETATCHED_HOUSE_200: return 200;
                default: throw new RuntimeException("Housing Type not found: ");
            }
        }
    }
}
