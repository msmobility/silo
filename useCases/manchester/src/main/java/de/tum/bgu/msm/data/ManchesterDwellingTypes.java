package de.tum.bgu.msm.data;

import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.DwellingTypes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ManchesterDwellingTypes implements DwellingTypes {

    private final static List<DwellingType> TYPES = Collections.unmodifiableList(Arrays.asList(DwellingTypeManchester.values()));

    @Override
    public DwellingType valueOf(String tp) {
        return DwellingTypeManchester.valueOf(tp);
    }

    @Override
    public List<DwellingType> getTypes() {
        return TYPES;
    }

    public enum DwellingTypeManchester implements DwellingType {

        /**
         * single-family house detached
         */
        SFD (0.25f,0.03f),
        /**
         * other house: semi-detached, terraced/end of terrace
         */
        SFA(0.22f,0.03f),
        /**
         * flat
         */
        FLAT(0.05f,0.04f),
        /**
         * mobile home
         */
        MH(0.015f,0.03f);

        private final float acresNeeded;
        private final float structuralVacancy;

        DwellingTypeManchester(float acresNeeded, float structuralVacancy) {
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

        public static DwellingTypeManchester valueOf(int code){
            switch (code){
                case 1:
                    return SFD;
                case 2:
                    return SFA;
                case 3:
                    return FLAT;
                case 4:
                    return MH;
                default:
                    throw new IllegalArgumentException(String.format("Code %d not valid.", code));
            }
        }

        public int getsizeOfDwelling(){
            switch(this){
                case MH: return 30;
                case FLAT: return 60;
                case SFA: return 120;
                case SFD: return 200;
                default: throw new RuntimeException("Housing Type not found: " + this);
            }
        }
    }
}
