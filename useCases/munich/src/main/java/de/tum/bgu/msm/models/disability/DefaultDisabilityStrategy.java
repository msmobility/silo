package de.tum.bgu.msm.models.disability;

import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class DefaultDisabilityStrategy extends JavaScriptCalculator <Double> implements DisabilityStrategy {

    private final static Reader reader = new InputStreamReader(DefaultDisabilityStrategy.class.getResourceAsStream("DisabilityCalc"));

    public DefaultDisabilityStrategy() {
        super(reader);
    }

    @Override
    public double calculateDisabilityProbability(Person person) {
        final int age = Math.min(person.getAge(), 100);
        final int gender = person.getGender().getCode();
        return super.calculate("calculateDisabilityProbability", age, gender);
    }

    public double calculateDisabilityType(Person person) {
        final int age = Math.min(person.getAge(), 100);
        return super.calculate("probabilityForPhysicalDisability", age, person.getGender().getCode());
    }

    public double calculateBaseYearDisabilityProbability(Person person) {
        final int age = Math.min(person.getAge(), 100);
        return super.calculate("calculateBaseYearDisabilityProbability", age, person.getGender().getCode());
    }
}