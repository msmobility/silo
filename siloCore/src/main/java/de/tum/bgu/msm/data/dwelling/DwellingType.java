package de.tum.bgu.msm.data.dwelling;

public interface DwellingType {

    /**
     * Returns the area needed for one dwelling of this type
     * @return
     */
    float getAreaPerDwelling();

    /**
     * Returns the structural vacancy rate of this type
     * @return
     */
    float getStructuralVacancyRate();


}
