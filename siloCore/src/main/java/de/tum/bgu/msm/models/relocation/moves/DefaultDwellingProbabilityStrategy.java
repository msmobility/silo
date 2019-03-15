package de.tum.bgu.msm.models.relocation.moves;

import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class DefaultDwellingProbabilityStrategy extends JavaScriptCalculator<Double> implements DwellingProbabilityStrategy {

    private final static Reader reader
            = new InputStreamReader(ScriptInputProvider.getDwellingProbabilityScriptInput());

    public DefaultDwellingProbabilityStrategy() {
        super(reader);
    }

    public double calculateSelectDwellingProbability(double dwellingUtility) {
        return super.calculate("calculateSelectDwellingProbability", dwellingUtility);
    }
}
