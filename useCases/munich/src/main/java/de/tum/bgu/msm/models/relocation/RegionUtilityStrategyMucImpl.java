package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.data.person.Nationality;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class RegionUtilityStrategyMucImpl extends JavaScriptCalculator<Double> implements RegionUtilityStrategyMuc {

    private final static Reader reader
            = new InputStreamReader(RegionUtilityStrategyMucImpl.class.getResourceAsStream("RegionUtilityCalc"));


    public RegionUtilityStrategyMucImpl() {
        super(reader);
    }

    @Override
    public double calculateRegionUtility(IncomeCategory incomeCategory, Nationality nationality,
                                         float price, float accessibility, float share) {
        return super.calculate("calculateRegionUtility", incomeCategory, nationality, price, accessibility, share);
    }
}
