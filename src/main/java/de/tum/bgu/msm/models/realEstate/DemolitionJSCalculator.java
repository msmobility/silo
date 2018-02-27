package de.tum.bgu.msm.models.realEstate;

import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public final class DemolitionJSCalculator extends JavaScriptCalculator<Double> {

    public DemolitionJSCalculator(Reader reader) {
        super(reader);
    }

    public double calculateDemolitionProbability(Dwelling dwelling, int year) {
        return super.calculate("calculateDemolitionProbability", dwelling, year);
    }
}
