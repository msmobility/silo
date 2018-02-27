package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.DwellingType;
import de.tum.bgu.msm.models.realEstate.DemolitionJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;

public class DemolitionTest {

    private DemolitionJSCalculator calculator;
    private Dwelling dwelling1;
    private Dwelling dwelling2;


    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("DemolitionCalc"));
        calculator = new DemolitionJSCalculator(reader);
        dwelling1 = new Dwelling(1,1,1, DwellingType.SFD, 1,1,1,1,1);
        dwelling2 = new Dwelling(1,1,1, DwellingType.SFD, 1,5,1,1,5);

    }

    @Test
    public void testModel() {
        Assert.assertEquals(0.0001, calculator.calculateDemolitionProbability(dwelling1,0), 0.);
    }

    @Test (expected = RuntimeException.class)
    public void testModelFailure() {
        calculator.calculateDemolitionProbability(dwelling2, 0);
    }
}
