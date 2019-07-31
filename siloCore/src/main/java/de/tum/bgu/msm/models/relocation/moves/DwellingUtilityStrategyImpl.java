package de.tum.bgu.msm.models.relocation.moves;

import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class DwellingUtilityStrategyImpl extends JavaScriptCalculator<Double> implements DwellingUtilityStrategy {

    private final static Reader reader = new InputStreamReader(ScriptInputProvider.getDwellingUtilityScriptInput());


    public DwellingUtilityStrategyImpl() {
        super(reader);
    }

    @Override
    public double calculateSelectDwellingUtility(HouseholdType ht, double ddSizeUtility, double ddPriceUtility,
                                                 double ddQualityUtility, double ddAutoAccessibilityUtility,
                                                 double transitAccessibilityUtility, double ddWorkDistanceUtility) {
       return super.calculate("calculateSelectDwellingUtility", ht, ddSizeUtility, ddPriceUtility,
                ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility, ddWorkDistanceUtility);
    }
}
