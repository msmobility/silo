package de.tum.bgu.msm.data.development;

import de.tum.bgu.msm.data.dwelling.DwellingType;

public interface Development {

    double getDevelopableArea();

    int getDwellingCapacity();


    /**
     * Increases or decreases the developable land by {@code area}
     * @param area the change in the area (negative values for decrease).
     */
    void changeAreaBy(double area);

    /**
     * Increases or decreases the dwelling capacity by {@code dwellings}
     * @param dwellings the change in the capacity (negative values for decrease).
     */
    void changeCapacityBy(int dwellings);

    boolean isUseDwellingCapacity();

    boolean isThisDwellingTypeAllowed(DwellingType dwellingType);
}
