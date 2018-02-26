package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.Race;
import de.tum.bgu.msm.models.demography.MarryDivorceJSCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;

public class MarryDivorceTest {

    private MarryDivorceJSCalculator calculator;
    private final double SCALE = 1.1;

    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("MarryDivorceCalcMstm"));
        calculator = new MarryDivorceJSCalculator(reader, SCALE);
    }

    @Test
    public void testModelOne() {
        Person person = new Person(1, 20, 1, Race.other, 1, -1, 0);
        Assert.assertEquals((0.05926 /2) * SCALE, calculator.calculateMarriageProbability(person), 0.);
    }

    @Test
    public void testModelTwo() {
        Person person = new Person(1, 50, 2, Race.other, 1, -1, 0);
        Assert.assertEquals((0.02514 /2) * SCALE, calculator.calculateMarriageProbability(person), 0.);    }

    @Test(expected = RuntimeException.class)
    public void testModelFailuresOne() {
        calculator.calculateMarriageProbability(null);
    }


    @Test
    public void testModelThree() {
        Assert.assertEquals(0.02156, calculator.calculateDivorceProbability(31), 0.);
    }

    @Test(expected = RuntimeException.class)
    public void testModelFailuresTwo() {
        calculator.calculateDivorceProbability(200);
    }

    @Test(expected = RuntimeException.class)
    public void testModelFailuresThree() {
        calculator.calculateDivorceProbability(-2);
    }

}
