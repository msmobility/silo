//package de.tum.bgu.msm.models.javascript;
//
//import de.tum.bgu.msm.models.relocation.moves.SelectDwellingJSCalculator;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import javax.script.ScriptException;
//import java.io.InputStreamReader;
//import java.io.Reader;
//
//public class SelectDwellingTest {
//
//    private SelectDwellingJSCalculator calculator;
//
//    @Before
//    public void setup() {
//        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("SelectDwellingCalc"));
//        calculator = new SelectDwellingJSCalculator (reader);
//    }
//
//    @Test
//    public void testModelOne() throws ScriptException {
//        Assert.assertEquals(0.22313016014842982, calculator.calculateSelectDwellingProbability(3), 0.);
//    }
//}
