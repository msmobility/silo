package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.syntheticPopulationGenerator.CreateCarOwnershipJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;

/**
 * Created by matthewokrah on 28/09/2017.
 */
public class CreateCarOwnershipTest {

    private CreateCarOwnershipJSCalculator calculator;

    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("CreateCarOwnershipCalc"));
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

    @Test
    public void testModelTwo() throws ScriptException {
        calculator.setLicense(0);
        calculator.setIncome(10596);
        calculator.setLogDistanceToTransit(3);
        calculator.setAreaType(1);
        calculator.setWorkers(0);
        double[] result = calculator.calculate();
        int cars = select(result);
        System.out.println(cars);
    }

    public static double getSum (double[] array) {
        // return sum of all elements in array
        double sum = 0;
        for (double val : array) sum += val;
        return sum;
    }

    public static int select (double[] probabilities) {
        // select item based on probabilities (for zero-based double array)
        double selPos = getSum(probabilities) * getRandomNumberAsFloat();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                return i;
            }
        }
        return probabilities.length - 1;
    }

    public static float getRandomNumberAsFloat() {
        Random rand = new Random();
        return rand.nextFloat();
    }

    @Test(expected = ScriptException.class)
    public void testModelFailures() throws ScriptException {
        calculator.setLicense(1);
        calculator.setIncome(2500);
        calculator.setLogDistanceToTransit(4);
        calculator.setAreaType(4);
        //calculator.setWorkers(2);
        double[] result = calculator.calculate();
        for (int i = 0; i < 4; i++){
            System.out.println(result[i]);
        }
    }
}
