package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.data.DwellingType;
import de.tum.bgu.msm.realEstate.ConstructionLocationJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ConstructionLocationTest {

    private Reader reader;
    private ConstructionLocationJSCalculator calculator;

    @Before
    public void setup() {
        reader = new InputStreamReader(this.getClass().getResourceAsStream("ConstructionCalc"));
        calculator = new ConstructionLocationJSCalculator(reader, true);
    }

    @Test
    public void testModel() throws ScriptException {
        calculator.setDwellingType(DwellingType.SFD);
        calculator.setPrice(100);
        calculator.setAccessibility(100);
        Assert.assertEquals(100, calculator.calculate(), 0.);
    }
}
