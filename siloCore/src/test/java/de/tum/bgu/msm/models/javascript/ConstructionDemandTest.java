package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypeImpl;
import de.tum.bgu.msm.models.realEstate.construction.DefaultConstructionDemandStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;

public class ConstructionDemandTest {
    private DefaultConstructionDemandStrategy calculator;

    @Before
    public void setup() {
        calculator = new DefaultConstructionDemandStrategy();
    }

    @Test
    public void testModelOne() throws ScriptException {
        Assert.assertEquals(0.025, calculator.calculateConstructionDemand(0.015, DefaultDwellingTypeImpl.SFD), 0.00001);
    }
    public void testModelTwo() throws ScriptException {
        Assert.assertEquals(0.003723333, calculator.calculateConstructionDemand(0.05, DefaultDwellingTypeImpl.SFD), 0.00001);
    }
}
