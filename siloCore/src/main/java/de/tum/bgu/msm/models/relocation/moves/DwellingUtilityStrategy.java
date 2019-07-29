package de.tum.bgu.msm.models.relocation.moves;

import de.tum.bgu.msm.data.household.HouseholdType;

public interface DwellingUtilityStrategy {
    double calculateSelectDwellingUtility(HouseholdType ht, double ddSizeUtility,
                                          double ddPriceUtility, double ddQualityUtility,
                                          double ddAutoAccessibilityUtility, double transitAccessibilityUtility,
                                          double workDistanceUtility);
}
