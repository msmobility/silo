package de.tum.bgu.msm.models.demography.marriage;

import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class DefaultMarriageStrategy extends JavaScriptCalculator<Double> implements MarriageStrategy{

    private final static Reader reader = new InputStreamReader(ScriptInputProvider.getMarriageProbabilityScriptInput());

    public DefaultMarriageStrategy() {
        super(reader);
    }

    public double calculateMarriageProbability(Person person) {
        return super.calculate("calculateMarriageProbability", person);
    }
}
