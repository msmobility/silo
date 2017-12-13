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
        calculator = new DemolitionJSCalculator(reader);
    }

    @Test
    public void testModel() {
        Assert.assertEquals(0.0001, calculator.calculateDemolitionProbability(true, 1), 0.);
    }

    @Test (expected = RuntimeException.class)
    public void testModelFailure() {
        calculator.calculateDemolitionProbability(true, 5);
    }
}
