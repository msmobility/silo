package de.tum.bgu.msm.data.dwelling;

import java.util.*;

public class DefaultDwellingTypes implements DwellingTypes {

    private final static List<DwellingType> TYPES = Collections.unmodifiableList(Arrays.asList(DefaultDwellingTypeImpl.values()));

    @Override
    public DwellingType valueOf(String tp) {
        return DefaultDwellingTypeImpl.valueOf(tp);
    }

    @Override
    public List<DwellingType> getTypes() {
        return TYPES;
    }

    /**
     * Dwelling types that are distinguished in the model
     * Author: Rolf Moeckel, PB Albuquerque
     * Created on 21 March 2011 in Santa Fe (which is J.S. Bach's 326th birthday)
     **/
    public enum DefaultDwellingTypeImpl implements DwellingType {

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
         * Multi-family houses with 5+ units
         */
        MF5plus(0.03f,0.05f),
        /**
         * mobile home
         */
        MH(0.015f,0.03f);

        private final float acresNeeded;
        private final float structuralVacancy;

        DefaultDwellingTypeImpl(float acresNeeded, float structuralVacancy) {
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
    }
}
