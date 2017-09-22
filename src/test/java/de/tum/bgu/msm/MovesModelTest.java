package de.tum.bgu.msm;

import de.tum.bgu.msm.data.Nationality;
import de.tum.bgu.msm.relocation.MovesModelJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

public class MovesModelTest {

    private Reader reader;
    private MovesModelJSCalculator calculator;

    @Before
    public void setup() {
        reader = new InputStreamReader(this.getClass().getResourceAsStream("MovesModelCalc"));
        calculator = new MovesModelJSCalculator(reader, true);
    }

    @Test
    public void testMovesModelOne() throws ScriptException {
        calculator.setIncomeGroup(2);
        calculator.setNationality(Nationality.german);
        calculator.setAccessibility(100);
        calculator.setForeignersShare(0.5f);
        calculator.setMedianPrice(500);
        Assert.assertEquals(227.225, (double) calculator.calculate(), 0.);
    }

    @Test
    public void testMovesModelTwo() throws ScriptException {
        calculator.setIncomeGroup(0);
        calculator.setNationality(Nationality.other);
        calculator.setAccessibility(100);
        calculator.setForeignersShare(0.5f);
        calculator.setMedianPrice(500);
        Assert.assertEquals(359.125, (double) calculator.calculate(), 0.);
    }

    @Test(expected = ScriptException.class)
    public void testMovesModelFailures() throws ScriptException {
        calculator.setIncomeGroup(5);
        calculator.setNationality(Nationality.other);
        calculator.setAccessibility(100);
        calculator.setForeignersShare(0.5f);
        calculator.setMedianPrice(500);
        calculator.calculate();
    }
}
