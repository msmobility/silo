package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.data.Nationality;
import de.tum.bgu.msm.models.relocation.SelectRegionJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

public class SelectRegionTest {

    private SelectRegionJSCalculator calculator;

    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("SelectRegionCalc"));
        calculator = new SelectRegionJSCalculator(reader);
    }

    @Test
    public void testMovesModelOne() throws ScriptException {
        Assert.assertEquals(227.225, calculator.calculateSelectRegionProbability(2,
                Nationality.german, 500, 100, 0.5f), 0.);
    }

    @Test
    public void testMovesModelTwo() throws ScriptException {
        Assert.assertEquals(359.125, calculator.calculateSelectRegionProbability(0,
                Nationality.other, 500, 100, 0.5f), 0.);
    }
}
