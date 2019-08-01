package de.tum.bgu.msm.models;

import de.tum.bgu.msm.data.person.Race;
import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class RegionUilityStrategyMstm extends JavaScriptCalculator<Double> {

    private final static Reader reader
            = new InputStreamReader(DwellingUtilityStrategyMstm.class.getResourceAsStream("RegionUtilityCalcMstm"));


    public RegionUilityStrategyMstm() {
        super(reader);
    }

    double calculateRegionUtility(IncomeCategory incomeCategory, Race race, float price,
                                  float accessibility, float share, float schoolQuality, float crimeRate) {
        return super.calculate("calculateRegionUtility", incomeCategory, race, price, accessibility, share, schoolQuality, crimeRate);
    }

}
