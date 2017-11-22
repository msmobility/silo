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
        calculator = new DivorceJSCalculator (reader, true);
    }

    @Test
    public void testModelOne() throws ScriptException {
        calculator.setPersonType(31);
        Assert.assertEquals(0.02156, calculator.calculate(), 0.);
    }

    @Test(expected = ScriptException.class)
    public void testModelFailures() throws ScriptException {
        calculator.setPersonType(200);
        calculator.calculate();
    }

    @Test(expected = ScriptException.class)
    public void testModelFailuresTwo() throws ScriptException {
        calculator.setPersonType(-2);
        calculator.calculate();
    }

}
