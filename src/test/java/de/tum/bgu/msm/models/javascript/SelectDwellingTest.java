package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.relocation.SelectDwellingJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

public class SelectDwellingTest {

    private Reader reader;
    private SelectDwellingJSCalculator calculator;

    @Before
    public void setup() {
        reader = new InputStreamReader(this.getClass().getResourceAsStream("SelectDwellingCalc"));
        calculator = new SelectDwellingJSCalculator (reader, true);
    }

    @Test
    public void testModelOne() throws ScriptException {
        calculator.setDwellingUtility(3);
        Assert.assertEquals(0.22313016014842982, calculator.calculate(), 0.);
    }
}
