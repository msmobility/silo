package de.tum.bgu.msm.models.realEstate.construction;

import de.tum.bgu.msm.data.dwelling.DwellingType;

public interface ConstructionLocationStrategy {

    /**
     * TODO
     * @param dwellingType
     * @param avgPrice
     * @param autoAccessibility
     * @return
     */
    double calculateConstructionProbability(DwellingType dwellingType, double avgPrice, double autoAccessibility);
}
