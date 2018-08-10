package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.models.demography.DeathJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;

public class DeathTest {
    private DeathJSCalculator calculator;

    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("DeathProbabilityCalcMuc"));
        calculator = new DeathJSCalculator (reader);
    }

    @Test
    public void testModelOne() {
        Assert.assertEquals(0.00068366, calculator.calculateDeathProbability(31, Person.Gender.MALE), 0.);
    }

    @Test
    public void testModelTwo() {
        Assert.assertEquals(0.410106, calculator.calculateDeathProbability(200, Person.Gender.MALE), 0.);
    }

    @Test(expected = RuntimeException.class)
    public void testModelFailures() {
        calculator.calculateDeathProbability(-2, Person.Gender.MALE);
    }

}
