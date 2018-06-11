package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public class DisabilityJSCalculator extends JavaScriptCalculator <Double> {

    public DisabilityJSCalculator(Reader reader){
        super(reader);
    }

    public double calculateDisabilityProbability(int personAge, int personSex){
        return super.calculate("calculateDisabilityProbability", personAge, personSex);
    }

    public double calculateDisabilityTypeProbability(int personAge, int personSex){
        return super.calculate("calculateDisabilityTypeProbability", personAge, personSex);
    }
}
