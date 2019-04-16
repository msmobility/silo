package de.tum.bgu.msm.models.demography.death;

import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class DefaultDeathStrategy extends JavaScriptCalculator <Double> implements DeathStrategy {

    private final static Reader reader = new InputStreamReader(ScriptInputProvider.getDeathProbabilityScriptInput());

    public DefaultDeathStrategy() {
        super(reader);
    }

    @Override
    public double calculateDeathProbability(Person person) {
        final int age = Math.min(person.getAge(), 100);
        return super.calculate("calculateDeathProbability", age, person.getGender());
    }
}