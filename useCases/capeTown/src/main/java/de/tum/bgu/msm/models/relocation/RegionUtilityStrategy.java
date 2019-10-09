package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.data.person.RaceCapeTown;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class RegionUtilityStrategy extends JavaScriptCalculator<Double> {

    private final static Reader reader
            = new InputStreamReader(RegionUtilityStrategy.class.getResourceAsStream("RegionUtilityCalc"));

    public RegionUtilityStrategy() {
        super(reader);
    }

    public double calculateRegionUtility(
            IncomeCategory incomeCategory, RaceCapeTown race,
            float priceUtil, float accessibilityUtil, double racialShare) {
        return super.calculate("calculateRegionUtility", incomeCategory, race, priceUtil, accessibilityUtil, racialShare);
    }
}
