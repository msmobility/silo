package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.data.person.PersonRole;
import de.tum.bgu.msm.models.demography.BirthJSCalculator;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

public class BirthTest {
    private BirthJSCalculator calculator;
    private HashMap<PersonRole, HashMap<Integer, double[]>> probabilities;

    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("BirthProbabilityCalcMuc"));
        float localScaler = 0.87f;
        calculator = new BirthJSCalculator (reader, localScaler);
    }

    @Before
    public void setupProbabilities(){
        NormalDistribution d0 = new NormalDistributionImpl(29.73, 5.40);
        NormalDistribution d1 = new NormalDistributionImpl(32.23, 5.04);
        NormalDistribution d2 = new NormalDistributionImpl(33.43, 5.21);
        NormalDistribution d3 = new NormalDistributionImpl(34.41, 5.32);
        double scale0 = 1.65492f * 2.243;
        double scale1 = 1.21375f * 2.243;
        double scale2 = 0.42201f * 2.243;
        double scale3 = 0.20629f * 2.243;
        double scale0single = 1.65492f * 0.1;
        double scale1single = 1.21375f * 0.1;
        double scale2single = 0.42201f * 0.1;
        double scale3single = 0.20629f * 0.1;
        double regionScaler = 0.87f;
        double[] prob0married = new double[100];
        double[] prob1married = new double[100];
        double[] prob2married = new double[100];
        double[] prob3married = new double[100];
        double[] prob0single = new double[100];
        double[] prob1single = new double[100];
        double[] prob2single = new double[100];
        double[] prob3single = new double[100];
        for (int age = 15; age < 50; age++){
            prob0married[age] = d0.density((double) age)*scale0*regionScaler;
            prob1married[age] = d1.density((double) age)*scale1*regionScaler;
            prob2married[age] = d2.density((double) age)*scale2*regionScaler;
            prob3married[age] = d3.density((double) age)*scale3*regionScaler;
            prob0single[age] = prob0married[age] * scale0single;
            prob1single[age] = prob0married[age] * scale1single;
            prob2single[age] = prob0married[age] * scale2single;
            prob3single[age] = prob0married[age] * scale3single;
        }
        probabilities = new HashMap<>();
        HashMap<Integer, double[]> married = new HashMap<>();
        married.put(0,prob0married);
        married.put(1,prob1married);
        married.put(2,prob2married);
        married.put(3,prob3married);
        HashMap<Integer, double[]> single = new HashMap<>();
        single.put(0,prob0single);
        single.put(1,prob1single);
        single.put(2,prob2single);
        single.put(3,prob3single);
        probabilities.put(PersonRole.MARRIED, married);
        probabilities.put(PersonRole.SINGLE, single);
    }

    @Test
    public void testModelOne() {
        float scaler = 0.87f;
        Assert.assertEquals((12.1/1000.*scaler), calculator.calculateBirthProbability(40,1), 0.);
    }

    @Test
    public void testModelTwo() {
        Assert.assertEquals(0.0, calculator.calculateBirthProbability(200,0), 0.);
    }

    @Test(expected = RuntimeException.class)
    public void testModelFailures() {
        calculator.calculateBirthProbability(-2,0);
    }

    @Test
    public void testModelOneDistribution() {
        float scaler = 0.87f;
        Assert.assertEquals((65.66/1000.*scaler), probabilities.get(PersonRole.MARRIED).get(1)[40], 0.00001);
    }

    @Test
    public void testModelTwoDistribution() {
        float scaler = 0.87f;
        Assert.assertEquals((0/1000.*scaler), probabilities.get(PersonRole.MARRIED).get(1)[60], 0.00001);
    }

}
