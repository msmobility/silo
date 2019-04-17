package de.tum.bgu.msm.models;

import de.tum.bgu.msm.data.person.Race;
import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class SelectRegionStrategyMstm extends JavaScriptCalculator<Double> {

    private final static Reader reader
            = new InputStreamReader(DwellingUtilityStrategyMstm.class.getResourceAsStream("SelectRegionCalcMstm"));


    public SelectRegionStrategyMstm() {
        super(reader);
    }

    double calculateSelectRegionProbability(IncomeCategory incomeCategory, Race race, float price,
                                            float accessibility, float share, float schoolQuality, float crimeRate) {
        return super.calculate("calculateSelectRegionProbability", incomeCategory, race, price, accessibility, share, schoolQuality, crimeRate);
    }

}
