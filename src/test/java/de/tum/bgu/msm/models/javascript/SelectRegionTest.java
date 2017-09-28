package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.data.Nationality;
import de.tum.bgu.msm.relocation.SelectRegionJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

public class SelectRegionTest {

    private Reader reader;
    private SelectRegionJSCalculator calculator;

    @Before
    public void setup() {
        reader = new InputStreamReader(this.getClass().getResourceAsStream("SelectRegionCalc"));
        calculator = new SelectRegionJSCalculator(reader, true);
    }

    @Test
    public void testMovesModelOne() throws ScriptException {
        calculator.setIncomeGroup(2);
        calculator.setNationality(Nationality.german);
        calculator.setAccessibility(100);
        calculator.setForeignersShare(0.5f);
        calculator.setMedianPrice(500);
        Assert.assertEquals(227.225, calculator.calculate(), 0.);
    }

    @Test
    public void testMovesModelTwo() throws ScriptException {
        calculator.setIncomeGroup(0);
        calculator.setNationality(Nationality.other);
        calculator.setAccessibility(100);
        calculator.setForeignersShare(0.5f);
        calculator.setMedianPrice(500);
        Assert.assertEquals(359.125, calculator.calculate(), 0.);
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
