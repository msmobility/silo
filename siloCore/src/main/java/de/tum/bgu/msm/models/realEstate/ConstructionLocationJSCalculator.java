package de.tum.bgu.msm.models.realEstate;

import de.tum.bgu.msm.data.DwellingType;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public final class ConstructionLocationJSCalculator extends JavaScriptCalculator<Double> {

    public ConstructionLocationJSCalculator(Reader reader) {
        super(reader);
    }

    public double calculateConstructionProbability(DwellingType dwellingType, float price, double accessibility) {
        return super.calculate("calculateConstructionUtility", dwellingType, price, accessibility);
    }
}
