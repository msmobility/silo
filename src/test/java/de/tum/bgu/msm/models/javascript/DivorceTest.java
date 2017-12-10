package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.demography.DivorceJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by matthewokrah on 26/09/2017.
 */
public class DivorceTest {
    private DivorceJSCalculator calculator;

    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("DivorceCalc"));
        calculator = new DivorceJSCalculator (reader);
    }

    @Test
    public void testModelOne() {
        Assert.assertEquals(0.02156, calculator.calculateDivorceProbability(31), 0.);
    }

    @Test(expected = RuntimeException.class)
    public void testModelFailures() {
        calculator.calculateDivorceProbability(200);
    }

    @Test(expected = RuntimeException.class)
    public void testModelFailuresTwo() {
        calculator.calculateDivorceProbability(-2);
    }

}
