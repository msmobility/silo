package de.tum.bgu.msm.models.javascript;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonUtils;
import de.tum.bgu.msm.data.person.Race;
import de.tum.bgu.msm.models.demography.MarryDivorceJSCalculator;
import de.tum.bgu.msm.properties.Properties;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

public class MarryDivorceTest {

    private MarryDivorceJSCalculator calculator;
    private final double SCALE = 1.1;
    private SiloDataContainer dataContainer;
    private HashMap<Gender, double[]> probabilitiesMarriage;
    private HashMap<Gender, double[]> probabilitiesDivorce;

    @BeforeClass
    public static void intitializeProperties() {
        SiloUtil.siloInitialization(Implementation.MARYLAND, "./test/scenarios/annapolis/javaFiles/siloMstm.properties");
    }

    @Before
    public void setup() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("MarryDivorceCalcMstm"));
        calculator = new MarryDivorceJSCalculator(reader, SCALE);
        this.dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
    }

    @Before
    public void setupDistributionMarriage(){
        NormalDistribution femaleNormalDistribution = new NormalDistribution(44.2565465,12.6221495);
        NormalDistribution maleNormalDistribution = new NormalDistribution(52.1206204,13.0923808);
        GammaDistribution femaleGammaDistribution = new GammaDistribution(36.5023455, 0.85680438);
        GammaDistribution maleGammaDistribution = new GammaDistribution(33.1783079, 1.01027592);
        double scaleFemale = 0.78372893;
        double scaleMale = 0.75316272;
        double[] probFemale = new double[100];
        double[] probMale = new double[100];
        for (int age = 0; age < 100; age++){
            probFemale[age] = scaleFemale * (femaleNormalDistribution.density((double) age) + femaleGammaDistribution.density((double) age));
            probMale[age] = scaleMale * (maleNormalDistribution.density((double) age) + maleGammaDistribution.density((double) age));
        }
        probabilitiesMarriage = new HashMap<>();
        probabilitiesMarriage.put(Gender.FEMALE,probFemale);
        probabilitiesMarriage.put(Gender.MALE, probMale);
    }


    @Before
    public void setupDistributionDivorce(){
        LogNormalDistribution femaleNormalDistribution = new LogNormalDistribution(3.739433, 0.25);
        LogNormalDistribution maleNormalDistribution = new LogNormalDistribution(3.7451,0.2459);
        GammaDistribution femaleGammaDistribution = new GammaDistribution(27.2130, 0.903879);
        GammaDistribution maleGammaDistribution = new GammaDistribution(25.4355, 0.9712);
        double scaleFemaleNormal = 0.4446;
        double scaleMaleNormal = 0.4357;
        double scaleFemaleGamma = 0.2364;
        double scaleMaleGamma = 0.2476;
        double[] probFemale = new double[100];
        double[] probMale = new double[100];
        for (int age = 15; age < 100; age++){
            probFemale[age] = scaleFemaleNormal * femaleNormalDistribution.density((double) age) +
                                scaleFemaleGamma * femaleGammaDistribution.density((double) age);
            probMale[age] = scaleMaleNormal * maleNormalDistribution.density((double) age) +
                                scaleMaleGamma * maleGammaDistribution.density((double) age);
        }
        probabilitiesDivorce = new HashMap<>();
        probabilitiesDivorce.put(Gender.FEMALE,probFemale);
        probabilitiesDivorce.put(Gender.MALE, probMale);
    }

    @Test
    public void testModelOne() {
        Person person = PersonUtils.getFactory().createPerson(1, 20, Gender.MALE, Race.other, Occupation.EMPLOYED, -1, 0);
        Assert.assertEquals((0.05926 /2) * SCALE, calculator.calculateMarriageProbability(person), 0.);
    }

    @Test
    public void testModelTwo() {
        Person person = PersonUtils.getFactory().createPerson(1, 50, Gender.FEMALE, Race.other, Occupation.EMPLOYED, -1, 0);
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


    @Test
    public void testModelFemaleMarriage() {
        Person person = PersonUtils.getFactory().createPerson(1, 20, Gender.FEMALE, Race.other, Occupation.EMPLOYED, -1, 0);
        Assert.assertEquals((0.007904973), probabilitiesMarriage.get(person.getGender())[person.getAge()], 0.0000001);
    }

    @Test
    public void testModelFemaleDivorce() {
        Person person = PersonUtils.getFactory().createPerson(1, 20, Gender.FEMALE, Race.other, Occupation.EMPLOYED, -1, 0);
        Assert.assertEquals((0.014656681), probabilitiesDivorce.get(person.getGender())[person.getAge()], 0.00001);
    }

    @Test
    public void testModelOneMaleMarriage() {
        Person person = PersonUtils.getFactory().createPerson(1, 20, Gender.MALE, Race.other, Occupation.EMPLOYED, -1, 0);
        Assert.assertEquals((0.00315949), probabilitiesMarriage.get(person.getGender())[person.getAge()], 0.0000001);
    }

    @Test
    public void testModelMaleDivorce() {
        Person person = PersonUtils.getFactory().createPerson(1, 20, Gender.MALE, Race.other, Occupation.EMPLOYED, -1, 0);
        Assert.assertEquals((0.01496349), probabilitiesDivorce.get(person.getGender())[person.getAge()], 0.0000001);
    }
}
