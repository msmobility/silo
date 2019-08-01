//package de.tum.bgu.msm.models.javascript;
//
//import de.tum.bgu.msm.data.household.IncomeCategory;
//import de.tum.bgu.msm.data.person.Nationality;
//import de.tum.bgu.msm.data.person.Race;
//import de.tum.bgu.msm.models.relocation.moves.SelectRegionJSCalculator;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import javax.script.ScriptException;
//import java.io.InputStreamReader;
//import java.io.Reader;
//
//public class SelectRegionTest {
//
//    private SelectRegionJSCalculator calculator;
//    private SelectRegionJSCalculator calculatorMstm;
//
//    @Before
//    public void setup() {
//        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("RegionUtilityCalc"));
//        calculator = new SelectRegionJSCalculator(reader);
//
//        Reader readerMstm = new InputStreamReader(this.getClass().getResourceAsStream("SelectRegionCalcMstm"));
//        calculatorMstm = new SelectRegionJSCalculator(readerMstm);
//
//    }
//
//    @Test
//    public void testMovesModelOne() throws ScriptException {
//        Assert.assertEquals(227.225, calculator.calculateSelectRegionProbability(IncomeCategory.HIGH,
//                Nationality.GERMAN, 500, 100, 0.5f), 0.);
//    }
//
//    @Test
//    public void testMovesModelTwo() throws ScriptException {
//        Assert.assertEquals(359.125, calculator.calculateSelectRegionProbability(IncomeCategory.LOW,
//                Nationality.OTHER, 500, 100, 0.5f), 0.);
//    }
//
//    @Test
//    public void testMovesModelMaryland() throws ScriptException {
//        Assert.assertEquals(314.365, calculatorMstm.calculateSelectRegionProbabilityMstm(IncomeCategory.LOW,
//                Race.white, 1000, 100, 0.6f, 0.6f, 0.1f), 0.000001);
//    }
//}
