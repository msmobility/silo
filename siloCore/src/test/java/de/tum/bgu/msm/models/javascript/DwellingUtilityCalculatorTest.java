//package de.tum.bgu.msm.models.javascript;
//
//import de.tum.bgu.msm.data.household.HouseholdType;
//import de.tum.bgu.msm.models.relocation.munich.MucDwellingUtilityJSCalculator;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import javax.script.ScriptException;
//import java.io.InputStreamReader;
//import java.io.Reader;
//
///**
// * Created by matthewokrah on 29/09/2017.
// */
//public class DwellingUtilityCalculatorTest {
//
//    private MucDwellingUtilityJSCalculator calculator;
//
//    @Before
//    public void setup() {
//        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("DwellingUtilityCalc"));
//        calculator = new MucDwellingUtilityJSCalculator(reader);
//    }
//
//    @Test
//    @Ignore
//    public void testModel() throws ScriptException {
//        double expected = 0.82063;
//        double result = calculator.calculateSelectDwellingUtility(HouseholdType.SIZE_1_INC_LOW, 0.9,0.9,0.9,0.9,0.9, 1);
//        //result = calculator.personalizeUtility(HouseholdType.SIZE_1_INC_LOW, result, 0.45, 1);
//
//
//            Assert.assertEquals(expected, result, 0.0001);
//
//
//    }
//}
