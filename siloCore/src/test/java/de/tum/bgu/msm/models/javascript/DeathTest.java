package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.models.demography.DeathJSCalculator;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

public class DeathTest {
    private DeathJSCalculator calculator;
    private HashMap<Gender, double[]> probabilities;

    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("DeathProbabilityCalcMuc"));
        calculator = new DeathJSCalculator (reader);
    }

    @Before
    public void setupProbabilities(){
        double alphaFemale = 0.104163121;
        double alphaMale = 0.09156481;
        double scaleFemale = 1.19833E-05;
        double scaleMale = 4.56581E-05;
        double[] probFemale = new double[100];
        double[] probMale = new double[100];
        for (int age = 0; age < 100; age++){
            probFemale[age] = scaleFemale * Math.exp(age * alphaFemale);
            probMale[age] = scaleMale * Math.exp(age * alphaMale);
        }
        probabilities = new HashMap<>();
        probabilities.put(Gender.FEMALE,probFemale);
        probabilities.put(Gender.MALE, probMale);

    }

    @Test
    public void testModelOne() {
        Assert.assertEquals(0.00068366, calculator.calculateDeathProbability(31, Gender.MALE), 0.);
    }

    @Test
    public void testModelTwo() {
        Assert.assertEquals(0.410106, calculator.calculateDeathProbability(200, Gender.MALE), 0.);
    }

    @Test(expected = RuntimeException.class)
    public void testModelFailures() {
        calculator.calculateDeathProbability(-2, Gender.MALE);
    }


    @Test
    public void testProbabilityOneBis(){
        Assert.assertEquals(0.000780309, probabilities.get(Gender.MALE)[31], 0.000001);
    }



}
