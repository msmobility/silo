package de.tum.bgu.msm;

import de.tum.bgu.msm.data.Nationality;
import de.tum.bgu.msm.relocation.MovesModelJSCalculator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert;
import org.junit.rules.ExpectedException;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

public class MovesModelTest {

    private Reader reader;
    private MovesModelJSCalculator calculator;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        reader = new InputStreamReader(this.getClass().getResourceAsStream("MovesModelCalc"));
        calculator = new MovesModelJSCalculator(reader, true);
    }

    @Test
    public void testMovesModelOne() {
        calculator.setIncomeGroup(2);
        calculator.setNationality(Nationality.german);
        calculator.setAccessibility(100);
        calculator.setForeignersShare(0.5f);
        calculator.setMedianPrice(500);
        Assert.assertEquals(227.225, (double) calculator.calculate(), 0.);
    }

    @Test
    public void testMovesModelTwo() {
        calculator.setIncomeGroup(0);
        calculator.setNationality(Nationality.other);
        calculator.setAccessibility(100);
        calculator.setForeignersShare(0.5f);
        calculator.setMedianPrice(500);
        Assert.assertEquals(359.125, (double) calculator.calculate(), 0.);
    }

    @Test
    public void testMovesModelFailures() {
        calculator.setIncomeGroup(5);
        calculator.setNationality(Nationality.other);
        calculator.setAccessibility(100);
        calculator.setForeignersShare(0.5f);
        calculator.setMedianPrice(500);
        exception.expect(ScriptException.class);
        calculator.calculate();
    }
}
