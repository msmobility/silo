package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.PersonType;
import de.tum.bgu.msm.models.demography.LeaveParentHhJSCalculator;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

public class LeaveParentHhTest {
    private LeaveParentHhJSCalculator calculator;
    HashMap<Gender, double[]> probabilities;

    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("LeaveParentHhCalcMstm"));
        calculator = new LeaveParentHhJSCalculator (reader);
    }

    @Before
    public void  setupLPHModelDistribution() {
        LogNormalDistribution femaleDistribution = new LogNormalDistribution(3.11674006,0.17902129);
        LogNormalDistribution maleDistribution = new LogNormalDistribution(3.15199,0.1819);
        double scaleFemale = 0.43609131;
        double scaleMale = 0.41366882;
        double[] probFemale = new double[100];
        double[] probMale = new double[100];
        for (int age = 0; age < 100; age++){
            probFemale[age] = scaleFemale * femaleDistribution.density((double) age);
            probMale[age] = scaleMale * maleDistribution.density((double) age);
        }
        probabilities = new HashMap<>();
        probabilities.put(Gender.FEMALE,probFemale);
        probabilities.put(Gender.MALE, probMale);
    }

    @Test
    public void testModelOne() {
        Assert.assertEquals(0.0003, calculator.calculateLeaveParentsProbability(PersonType.WOMEN_AGE_40_TO_44), 0.);
    }

    @Test
    public void testModelOneDistribution() {
        Assert.assertEquals(0.01682, probabilities.get(Gender.FEMALE)[28], 0.0001);
    }

}
