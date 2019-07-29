package de.tum.bgu.msm.models.relocation.moves;

import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class SelectRegionStrategyImpl extends JavaScriptCalculator<Double> implements SelectRegionStrategy {

    private final static Reader reader
            = new InputStreamReader(DwellingUtilityStrategyImpl.class.getResourceAsStream("SelectRegionCalc"));


    public SelectRegionStrategyImpl() {
        super(reader);
    }

    @Override
    public double calculateSelectRegionProbability(IncomeCategory incomeCategory,
                                                   float price, float accessibility, float share) {
        return super.calculate("calculateSelectRegionProbability", incomeCategory, price, accessibility, share);
    }
}
