package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.models.realEstate.RenovationJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;

public class RenovationTest {

    private RenovationJSCalculator calculator;

    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("RenovationCalc"));
        calculator = new RenovationJSCalculator(reader);
    }

    @Test
    public void testModelOne() {
        double[] expected = new double[]{0.05, 0.10, 0.75, 0.10, 0.};
        for (int i = 0; i < expected.length; i++) {
            double result = calculator.calculateRenovationProbability(3, i+1);
            Assert.assertEquals(expected[i], result, 0.);
        }
    }

    @Test
    public void testModelTwo() {
        double[] expected = new double[]{0., 0., 0.93, 0.05, 0.02};
        for (int i = 0; i < expected.length; i++) {
            double result = calculator.calculateRenovationProbability(1, i+1);
            Assert.assertEquals(expected[i], result, 0.);
        }
    }

}
