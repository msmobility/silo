package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.data.DwellingType;
import de.tum.bgu.msm.models.realEstate.ConstructionDemandJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ConstructionDemandTest {
    private ConstructionDemandJSCalculator calculator;

    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("ConstructionDemandCalcMstm"));
        calculator = new ConstructionDemandJSCalculator(reader);
    }

    @Test
    public void testModelOne() throws ScriptException {
        Assert.assertEquals(0.00501, calculator.calculateConstructionDemand(0.05, DwellingType.MF234), 0.00001);
    }
}



