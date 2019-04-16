package de.tum.bgu.msm.models;

import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class DwellingUtilityStrategyMstm extends JavaScriptCalculator<Double> {

    private final static Reader reader
            = new InputStreamReader(DwellingUtilityStrategyMstm.class.getResourceAsStream("DwellingUtilityCalcMstm"));


    public DwellingUtilityStrategyMstm() {
        super(reader);
    }

    public synchronized double calculateSelectDwellingUtility(HouseholdType ht, double ddSizeUtility, double ddPriceUtility,
                                                 double ddQualityUtility, double ddAutoAccessibilityUtility,
                                                 double transitAccessibilityUtility, double ddSchoolQuality,
                                                 double ddCrimeIndex, double ddWorkDistanceUtility) {
        return super.calculate("calculateSelectDwellingUtility", ht, ddSizeUtility, ddPriceUtility,
                ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility, ddSchoolQuality,
                ddCrimeIndex, ddWorkDistanceUtility);
    }
}
