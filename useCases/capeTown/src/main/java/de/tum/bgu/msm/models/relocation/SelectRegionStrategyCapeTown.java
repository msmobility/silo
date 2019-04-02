package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.data.person.RaceCapeTown;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class SelectRegionStrategyCapeTown extends JavaScriptCalculator<Double> {

    private final static Reader reader
            = new InputStreamReader(SelectRegionStrategyCapeTown.class.getResourceAsStream("SelectRegionCalc"));

    public SelectRegionStrategyCapeTown() {
        super(reader);
    }

    public double calculateSelectRegionProbability(
            IncomeCategory incomeCategory, RaceCapeTown race,
            float priceUtil, float accessibilityUtil, double racialShare) {
        return super.calculate("calculateSelectRegionProbability", incomeCategory, race, priceUtil, accessibilityUtil, racialShare);
    }
}
