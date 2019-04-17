//package de.tum.bgu.msm.models.javascript;
//
//import de.tum.bgu.msm.models.autoOwnership.munich.CreateCarOwnershipJSCalculator;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import javax.script.ScriptException;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.util.Random;
//
///**
// * Created by matthewokrah on 28/09/2017.
// */
//public class CreateCarOwnershipTest {
//
//    private CreateCarOwnershipJSCalculator calculator;
//
//    @Before
//    public void setup() {
//        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("CreateCarOwnershipCalc"));
//        calculator = new CreateCarOwnershipJSCalculator (reader);
//    }
//
//    @Test
//    public void testModelOne() throws ScriptException {
//        double[] expected = new double[]{0.09549, 0.79149, 0.10943,	0.00358};
//        double[] result = calculator.calculate(1, 2, 2500, 4, 2);
//        for (int i = 0; i < expected.length; i++){
//            System.out.println(result[i]);
//            Assert.assertEquals(expected[i], result[i], 0.01);
//        }
//    }
//
//    @Test
//    public void testModelTwo() throws ScriptException {
//        double[] result = calculator.calculate(0, 0, 10596, 3, 2);
//        int cars = select(result);
//        System.out.println(cars);
//    }
//
//    public static double getSum (double[] array) {
//        // return sum of all elements in array
//        double sum = 0;
//        for (double val : array) sum += val;
//        return sum;
//    }
//
//    public static int select (double[] probabilities) {
//        // select item based on probabilities (for zero-based double array)
//        double selPos = getSum(probabilities) * getRandomNumberAsFloat();
//        double sum = 0;
//        for (int i = 0; i < probabilities.length; i++) {
//            sum += probabilities[i];
//            if (sum > selPos) {
//                return i;
//            }
//        }
//        return probabilities.length - 1;
//    }
//
//    public static float getRandomNumberAsFloat() {
//        Random rand = new Random();
//        return rand.nextFloat();
//    }
//}
