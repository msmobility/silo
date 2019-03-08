package de.tum.bgu.msm.models.relocation.munich;

import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public class MucDwellingUtilityJSCalculator extends JavaScriptCalculator<Double> {
    public MucDwellingUtilityJSCalculator(Reader reader) {
        super(reader);
    }

    public double calculateSelectDwellingUtility(HouseholdType ht, double ddSizeUtility, double ddPriceUtility,
                                                 double ddQualityUtility, double ddAutoAccessibilityUtility,
                                                 double transitAccessibilityUtility, double ddWorkDistanceUtility) {
       return super.calculate("calculateSelectDwellingUtility", ht, ddSizeUtility, ddPriceUtility,
                ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility, ddWorkDistanceUtility);
    }
}
