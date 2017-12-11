package de.tum.bgu.msm.realEstate;

import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public final class DemolitionJSCalculator extends JavaScriptCalculator<Double> {

    public DemolitionJSCalculator(Reader reader) {
        super(reader);
    }

    public double calculateDemolitionProbability(boolean occupied, int quality) {
        return super.calculate("calculateDemolitionProbability", occupied, quality);
    }
}
