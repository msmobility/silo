package de.tum.bgu.msm.models;

import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public class MstmDwellingUtilityJSCalculator extends JavaScriptCalculator<Double> {
    protected MstmDwellingUtilityJSCalculator(Reader reader) {
        super(reader);
    }

    public double calculateSelectDwellingUtility(HouseholdType ht, double ddSizeUtility, double ddPriceUtility,
                                                 double ddQualityUtility, double ddAutoAccessibilityUtility,
                                                 double transitAccessibilityUtility, double ddSchoolQuality,
                                                 double ddCrimeIndex, double ddWorkDistanceUtility) {
        return super.calculate("calculateSelectDwellingUtility", ht, ddSizeUtility, ddPriceUtility,
                ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility, ddSchoolQuality,
                ddCrimeIndex, ddWorkDistanceUtility);
    }
}

