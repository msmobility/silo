package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.data.DwellingType;
import de.tum.bgu.msm.realEstate.ConstructionDemandJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ConstructionDemandTest {
    private Reader reader;
    private ConstructionDemandJSCalculator calculator;

    @Before
    public void setup() {
        reader = new InputStreamReader(this.getClass().getResourceAsStream("ConstructionDemandCalc"));
        calculator = new ConstructionDemandJSCalculator(reader, true);
    }

    @Test
    public void testModelOne() throws ScriptException {
        calculator.setDwellingType(DwellingType.MF234);
        calculator.setVacancyByRegion(0.05);
        Assert.assertEquals(0.00501, calculator.calculate(), 0.00001);
    }
}



