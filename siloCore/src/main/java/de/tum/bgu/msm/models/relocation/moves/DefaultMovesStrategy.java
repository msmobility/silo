package de.tum.bgu.msm.models.relocation.moves;

import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class DefaultMovesStrategy extends JavaScriptCalculator<Double> implements MovesStrategy{

    private final static Reader reader = new InputStreamReader(ScriptInputProvider.getMovesScriptInput());

    public DefaultMovesStrategy() {
        super(reader);
    }

    public double getMovingProbability(double householdSatisfaction, double currentDwellingUtility) {
        return super.calculate("calculateMovingProbability", householdSatisfaction, currentDwellingUtility);
    }

}
