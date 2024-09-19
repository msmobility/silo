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
         * single-family house attached or townhouse
         */
        SFA(0.22f,0.03f),
        /**
         * duplexes and buildings 2-4 units (not including those that fit Attached or Townhouse definition)
         */
        MF234(0.07f,0.04f),
        /**
         * Multi-family houses with 5+ units (flat and other)
         */
        MF5plus(0.03f,0.05f);

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
                    return MF234;
                case 4:
                    return MF5plus;
                default:
                    throw new IllegalArgumentException(String.format("Code %d not valid.", code));
            }
        }

        public int getsizeOfDwelling(){
            switch(this){
                case MF5plus: return 50;
                case MF234: return 80;
                case SFA: return 120;
                case SFD: return 200;
                default: throw new RuntimeException("Housing Type not found: " + this);
            }
        }
    }
}
