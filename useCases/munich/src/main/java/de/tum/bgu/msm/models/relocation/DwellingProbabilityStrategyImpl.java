package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class DwellingProbabilityStrategyImpl extends JavaScriptCalculator<Double> implements DwellingProbabilityStrategy {

    private final static Reader reader
            = new InputStreamReader(DwellingUtilityStrategyImpl.class.getResourceAsStream("SelectDwellingCalc"));

    public DwellingProbabilityStrategyImpl() {
        super(reader);
    }

    public double calculateSelectDwellingProbability(double dwellingUtility) {
        return super.calculate("calculateSelectDwellingProbability", dwellingUtility);
    }
}
