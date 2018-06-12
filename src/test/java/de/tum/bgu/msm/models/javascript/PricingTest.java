package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.models.realEstate.PricingJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

public class PricingTest {

    private PricingJSCalculator calculator;


    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("PricingCalc"));
        calculator = new PricingJSCalculator(reader);



    }

    @Test
    public void test1() throws ScriptException {
        Assert.assertEquals(0.333, calculator.getLowInflectionPoint(), 0.);
    }
    @Test
    public void test2() throws ScriptException {
        Assert.assertEquals(2, calculator.getHighInflectionPoint(), 0.);
    }
    @Test
    public void test3() throws ScriptException {
        Assert.assertEquals(-5, calculator.getLowerSlope(), 0.);
    }
    @Test
    public void test4() throws ScriptException {
        Assert.assertEquals(-1, calculator.getMainSlope(), 0.);
    }
    @Test
    public void test5() throws ScriptException {
        Assert.assertEquals(-0.1, calculator.getHighSlope(), 0.);
    }
    @Test
    public void test6() throws ScriptException {
        Assert.assertEquals(0.02, calculator.getMaximumChange(), 0.);
    }


}
