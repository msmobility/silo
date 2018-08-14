package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.data.PersonType;
import de.tum.bgu.msm.models.demography.LeaveParentHhJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;

public class LeaveParentHhTest {
    private LeaveParentHhJSCalculator calculator;

    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("LeaveParentHhCalcMstm"));
        calculator = new LeaveParentHhJSCalculator (reader);
    }

    @Test
    public void testModelOne() {
        Assert.assertEquals(0.0003, calculator.calculateLeaveParentsProbability(PersonType.WOMEN_AGE_40_TO_44), 0.);
    }

}
