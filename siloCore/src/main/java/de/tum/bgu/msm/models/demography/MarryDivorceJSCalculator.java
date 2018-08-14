package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public class MarryDivorceJSCalculator extends JavaScriptCalculator<Double> {

    private final double scale;

    public MarryDivorceJSCalculator(Reader reader, double scale) {
        super(reader);
        this.scale = scale;
    }

    public double calculateMarriageProbability(Person person) {
        return super.calculate("calculateMarriageProbability", person, scale);
    }

    public double calculateDivorceProbability (int personType) {
        return super.calculate("calculateDivorceProbability", personType);
    }
}
