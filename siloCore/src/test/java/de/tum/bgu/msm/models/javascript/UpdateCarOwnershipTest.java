//package de.tum.bgu.msm.models.javascript;
//
//import de.tum.bgu.msm.models.autoOwnership.munich.MunichCarOwnershipJSCalculator;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import javax.script.ScriptException;
//import java.io.InputStreamReader;
//import java.io.Reader;
//
///**
// * Created by matthewokrah on 29/09/2017.
// */
//public class UpdateCarOwnershipTest {
//
//    private MunichCarOwnershipJSCalculator calculator;
//
//    @Before
//    public void setup() {
//        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("UpdateCarOwnershipCalc"));
//        calculator = new MunichCarOwnershipJSCalculator(reader);
//    }
//
//    @Test
//    public void testModelOne() throws ScriptException {
//        double[] expected = new double[]{0.801, 0.1574, 0.0415};
//        double[] result = calculator.calculateCarOwnerShipProbabilities(2, 1, 0, 1, 0, 0, 0);
//        for (int i = 0; i < expected.length; i++){
//            Assert.assertEquals(expected[i], result[i], 0.01);
//        }
//    }
//}
