package de.tum.bgu.msm.syntheticPopulationGenerator.munich.disability;

import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public class BaseYearDisabilityJSCalculator extends JavaScriptCalculator<Double> {


    protected BaseYearDisabilityJSCalculator(Reader reader) {
        super(reader);
    }

    public double calculateBaseYearDisabilityProbability(Person person, int gender) {
        return super.calculate("calculateBaseYearDisabilityProbability", person, gender);
    }

    public double calculateDisabilityTypeProbability(Person person, int gender){
        return super.calculate("calculateDisabilityTypeProbability", person, gender);
    }
}
