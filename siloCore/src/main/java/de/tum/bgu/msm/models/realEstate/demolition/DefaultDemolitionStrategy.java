package de.tum.bgu.msm.models.realEstate.demolition;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public final class DefaultDemolitionStrategy extends JavaScriptCalculator<Double> implements DemolitionStrategy {

    private final static Reader reader = new InputStreamReader(ScriptInputProvider.getDemolitionScriptInput());

    public DefaultDemolitionStrategy() {
        super(reader);
    }

    public double calculateDemolitionProbability(Dwelling dwelling, int year) {
        return super.calculate("calculateDemolitionProbability", dwelling, year);
    }
}
