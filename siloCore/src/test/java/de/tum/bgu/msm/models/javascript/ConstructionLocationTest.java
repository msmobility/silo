package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.data.DwellingType;
import de.tum.bgu.msm.models.realEstate.ConstructionLocationJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;

public class ConstructionLocationTest {

    private ConstructionLocationJSCalculator calculator;

    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("ConstructionLocationCalc"));
        calculator = new ConstructionLocationJSCalculator(reader);
    }

    @Test
    public void testModel() {
        Assert.assertEquals(100, calculator.calculateConstructionProbability(DwellingType.SFD, 100, 100), 0.);
    }
}
