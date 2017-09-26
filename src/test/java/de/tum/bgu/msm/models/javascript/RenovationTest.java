package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.realEstate.RenovationJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

public class RenovationTest {

    private Reader reader;
    private RenovationJSCalculator calculator;

    @Before
    public void setup() {
        reader = new InputStreamReader(this.getClass().getResourceAsStream("RenovationCalc"));
        calculator = new RenovationJSCalculator(reader, true);
    }

    @Test
    public void testModelOne() throws ScriptException {
        calculator.setQuality(3);
        double[] expected = new double[]{0.05, 0.10, 0.75, 0.10, 0.};
        for (int i = 0; i < expected.length; i++) {
            calculator.setAlternative(i + 1);
            double result = calculator.calculate();
            Assert.assertEquals(expected[i], result, 0.);
        }
    }

    @Test
    public void testModelTwo() throws ScriptException {
        calculator.setQuality(1);
        double[] expected = new double[]{0., 0., 0.93, 0.05, 0.02};
        for (int i = 0; i < expected.length; i++) {
            calculator.setAlternative(i + 1);
            double result = calculator.calculate();
            Assert.assertEquals(expected[i], result, 0.);
        }
    }

}
