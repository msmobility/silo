package de.tum.bgu.msm.models.realEstate.renovation;


import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class DefaultRenovationStrategy extends JavaScriptCalculator<Double> implements RenovationStrategy {

    private final static Reader reader = new InputStreamReader(ScriptInputProvider.getRenovationScriptInput());

    public DefaultRenovationStrategy() {super(reader);}

    public double calculateRenovationProbability(int quality, int newQuality) {
        return super.calculate("calculateRenovationProbability", quality, newQuality);
    }
}
