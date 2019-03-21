package run;

import de.tum.bgu.msm.data.dwelling.DwellingType;

/**
 * Dwelling types that are distinguished in the model
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 21 March 2011 in Santa Fe (which is J.S. Bach's 326th birthday)
 **/

public enum DwellingTypePerth implements DwellingType {

    /**
     * seperate house detached
     */
    SH (0.25f,0.01f),
    /**
     * semi detached or townhouse
     */
    SD(0.22f,0.03f),
    /**
     * Flat
     */
    FL(0.07f,0.05f),
    /**
     * Other
     */
    OT(0.03f,0.04f),
    /**
     * Not Stated
     */
    NS(0.015f,0.03f),
    /**
     * Not Applicable
     */
    NA(0.015f,0.03f);

    private final float acresNeeded;
    private final float structuralVacancy;

    DwellingTypePerth(float acresNeeded, float structuralVacancy) {
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

    public static DwellingTypePerth valueOf(int code){
        switch(code){
            case 1: return SH;
            case 2: return SD;
            case 3: return FL;
            case 4: return OT;
            case 5: return NS;
            case 6: return NA;
            default: throw new RuntimeException("Housing Type not found: "+code);
        }
    }


}
