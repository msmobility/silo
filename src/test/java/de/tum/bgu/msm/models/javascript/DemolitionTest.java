package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.realEstate.DemolitionJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

public class DemolitionTest {

    private DemolitionJSCalculator calculator;

    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("DemolitionCalc"));
        calculator = new DemolitionJSCalculator(reader, true);
    }

    @Test
    public void testModel() throws ScriptException {
        calculator.setDwellingQuality(1);
        calculator.setOccupied(true);
        Assert.assertEquals(0.0001, calculator.calculate(), 0.);
    }

    @Test (expected = ScriptException.class)
    public void testModelFailure() throws ScriptException {
        calculator.setDwellingQuality(5);
        calculator.setOccupied(true);
        calculator.calculate();
    }
}
