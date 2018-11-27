package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public class BirthJSCalculator extends JavaScriptCalculator <Double> {

    private float scaler;


    public BirthJSCalculator(Reader reader, float scaler) {
        super(reader);
        this.scaler = scaler;
    }

    public double calculateBirthProbability(int personAge, int numberOfChildren) {
        return super.calculate("calculateBirthProbability", personAge, numberOfChildren, scaler);
    }

    public double getProbabilityForGirl() {
        return super.calculate("probabilityForAGirl");
    }

}