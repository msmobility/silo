package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.models.demography.BirthJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;

public class BirthTest {
    private BirthJSCalculator calculator;

    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("BirthProbabilityCalcMstm"));
        float localScaler = 0.87f;
        calculator = new BirthJSCalculator (reader, localScaler);
    }

    @Test
    public void testModelOne() {
        float scaler = 0.87f;
        Assert.assertEquals((91.2/1000.*scaler), calculator.calculateBirthProbability(31), 0.);
    }

    @Test
    public void testModelTwo() {
        Assert.assertEquals(0.0, calculator.calculateBirthProbability(200), 0.);
    }

    @Test(expected = RuntimeException.class)
    public void testModelFailures() {
        calculator.calculateBirthProbability(-2);
    }

}
