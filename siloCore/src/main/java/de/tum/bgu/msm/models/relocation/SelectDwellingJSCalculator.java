package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public class SelectDwellingJSCalculator extends JavaScriptCalculator<Double> {

    public SelectDwellingJSCalculator(Reader reader) {
        super(reader);
    }

    public double calculateSelectDwellingProbability(double dwellingUtility) {
        return super.calculate("calculateSelectDwellingProbability", dwellingUtility);
    }
}
