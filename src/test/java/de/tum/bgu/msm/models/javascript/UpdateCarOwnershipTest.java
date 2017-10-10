package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.autoOwnership.UpdateCarOwnershipJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by matthewokrah on 29/09/2017.
 */
public class UpdateCarOwnershipTest {

    private Reader reader;
    private UpdateCarOwnershipJSCalculator calculator;

    @Before
    public void setup() {
        reader = new InputStreamReader(this.getClass().getResourceAsStream("UpdateCarOwnershipCalc"));
        calculator = new UpdateCarOwnershipJSCalculator (reader, true);
    }

    @Test
    public void testModelOne() throws ScriptException {
        calculator.setPreviousCars(2);
        calculator.setHHSizePlus(1);
        calculator.setHHSizeMinus(0);
        calculator.setHHIncomePlus(1);
        calculator.setHHIncomeMinus(0);
        calculator.setLicensePlus(0);
        calculator.setChangeResidence(0);
        double[] expected = new double[]{0.801, 0.1574, 0.0415};
        double[] result = calculator.calculate();
        for (int i = 0; i < expected.length; i++){
            System.out.println(result[i]);
            Assert.assertEquals(expected[i], result[i], 0.01);
        }
    }


    @Test(expected = ScriptException.class)
    public void testModelFailures() throws ScriptException {
        calculator.setPreviousCars(2);
        calculator.setHHSizePlus(1);
        calculator.setHHSizeMinus(0);
        calculator.setHHIncomePlus(1);
        calculator.setHHIncomeMinus(0);
        //calculator.setLicensePlus(0);
        calculator.setChangeResidence(0);
        double[] result = calculator.calculate();
        for (int i = 0; i < 3; i++){
            System.out.println(result[i]);
        }
    }
}
