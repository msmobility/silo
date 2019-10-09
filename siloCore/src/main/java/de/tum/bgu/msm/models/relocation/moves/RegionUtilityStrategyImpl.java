package de.tum.bgu.msm.models.relocation.moves;

import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class RegionUtilityStrategyImpl extends JavaScriptCalculator<Double> implements RegionUtilityStrategy {

    private final static Reader reader = new InputStreamReader(ScriptInputProvider.getRegionUtilityScriptInput());


    public RegionUtilityStrategyImpl() {
        super(reader);
    }

    @Override
    public double calculateSelectRegionProbability(IncomeCategory incomeCategory,
                                                   float price, float accessibility, float share) {
        return super.calculate("calculateRegionUtility", incomeCategory, price, accessibility, share);
    }
}
