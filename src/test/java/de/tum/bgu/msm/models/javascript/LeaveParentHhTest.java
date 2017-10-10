package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.demography.LeaveParentHhJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

public class LeaveParentHhTest {
    private Reader reader;
    private LeaveParentHhJSCalculator calculator;

    @Before
    public void setup() {
        reader = new InputStreamReader(this.getClass().getResourceAsStream("LeaveParentHhCalc"));
        calculator = new LeaveParentHhJSCalculator (reader, true);
    }

    @Test
    public void testModelOne() throws ScriptException {
        calculator.setPersonType(31);
        Assert.assertEquals(0.0003, calculator.calculate(), 0.);
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
