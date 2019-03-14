package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.data.person.Nationality;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class SelectRegionStrategyImpl extends JavaScriptCalculator<Double> implements SelectRegionStrategy {

    private final static Reader reader
            = new InputStreamReader(DwellingUtilityStrategyImpl.class.getResourceAsStream("SelectRegionCalc"));


    public SelectRegionStrategyImpl() {
        super(reader);
    }

    public double calculateSelectRegionProbability(IncomeCategory incomeCategory, Nationality nationality,
                                                   float price, float accessibility, float share) {
        return super.calculate("calculateSelectRegionProbability", incomeCategory, nationality, price, accessibility, share);
    }
}
