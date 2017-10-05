package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.autoOwnership.CreateCarOwnershipJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by matthewokrah on 28/09/2017.
 */
public class CreateCarOwnershipTest {

    private Reader reader;
    private CreateCarOwnershipJSCalculator calculator;

    @Before
    public void setup() {
        reader = new InputStreamReader(this.getClass().getResourceAsStream("CreateCarOwnershipCalc"));
        calculator = new CreateCarOwnershipJSCalculator (reader, true);
    }

    @Test
    public void testModelOne() throws ScriptException {
        calculator.setLicense(1);
        calculator.setIncome(2500);
        calculator.setLogDistanceToTransit(4);
        calculator.setAreaType(2);
        calculator.setWorkers(2);
        double[] expected = new double[]{0.09549, 0.79149, 0.10943,	0.00358};
        double[] result = calculator.calculate();
        for (int i = 0; i < expected.length; i++){
            System.out.println(result[i]);
            Assert.assertEquals(expected[i], result[i], 0.01);
        }
    }


    @Test(expected = ScriptException.class)
    public void testModelFailures() throws ScriptException {
        calculator.setLicense(1);
        calculator.setIncome(2500);
        calculator.setLogDistanceToTransit(4);
        calculator.setAreaType(4);
        calculator.setWorkers(2);
        double[] result = calculator.calculate();
        for (int i = 0; i < 4; i++){
            System.out.println(result[i]);
        }
    }
}
