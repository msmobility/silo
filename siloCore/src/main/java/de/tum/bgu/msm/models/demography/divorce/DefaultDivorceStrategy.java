package de.tum.bgu.msm.models.demography.divorce;

import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class DefaultDivorceStrategy extends JavaScriptCalculator<Double> implements DivorceStrategy {

    private final static Reader reader = new InputStreamReader(ScriptInputProvider.getDivorceProbabilityScriptInput());

    public DefaultDivorceStrategy() {
        super(reader);
    }

    @Override
    public double calculateDivorceProbability(Person per) {
        return super.calculate("calculateDivorceProbability", per.getType().ordinal());
    }
}
