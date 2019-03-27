package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class DwellingUtilityStrategyCapeTown extends JavaScriptCalculator<Double> {

    private final static Reader reader = new InputStreamReader(DwellingUtilityStrategyCapeTown.class.getResourceAsStream("DwellingUtilityCalc"));

    public DwellingUtilityStrategyCapeTown() {
        super(reader);
    }

    public double calculateSelectDwellingUtility(HouseholdType ht, double ddSizeUtility,
                                                 double ddPriceUtility, double ddQualityUtility,
                                                 double ddAutoAccessibilityUtility,
                                                 double transitAccessibilityUtility,
                                                 double workDistanceUtility) {

        return super.calculate("calculateSelectDwellingUtility", ht, ddSizeUtility,
                ddPriceUtility, ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility, workDistanceUtility);
    }
}
