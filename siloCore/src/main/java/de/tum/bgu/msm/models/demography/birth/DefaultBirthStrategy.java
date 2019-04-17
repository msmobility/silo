package de.tum.bgu.msm.models.demography.birth;

import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class DefaultBirthStrategy extends JavaScriptCalculator <Double> implements BirthStrategy {

    private final static Reader reader = new InputStreamReader(ScriptInputProvider.getBirthScriptInputStream());

    /**
     * //TODO
     */
    public DefaultBirthStrategy() {
        super(reader);
    }

    @Override
    public double calculateBirthProbability(int personAge, int numberOfChildren) {
        return super.calculate("calculateBirthProbability", personAge, numberOfChildren);
    }

    @Override
    public double getProbabilityForGirl() {
        return super.calculate("probabilityForAGirl");
    }

}