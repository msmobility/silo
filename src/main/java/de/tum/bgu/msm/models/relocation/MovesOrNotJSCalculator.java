package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.util.js.JavaScriptCalculator;
import sun.plugin2.message.JavaScriptCallMessage;

import java.io.Reader;

public class MovesOrNotJSCalculator extends JavaScriptCalculator<Double> {
    protected MovesOrNotJSCalculator(Reader reader) {
        super(reader);
    }

    public double getMovingProbability(double householdSatisfaction, double currentDwellingUtility) {
        return super.calculate("calculateMovingProbability", householdSatisfaction, currentDwellingUtility);
    }

}
