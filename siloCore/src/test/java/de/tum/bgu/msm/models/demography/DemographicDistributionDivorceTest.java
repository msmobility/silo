package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.DwellingUtils;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.GammaDistribution;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

public class DemographicDistributionDivorceTest {

    private static BirthModel model;
    private static SiloModelContainer modelContainer;
    private static SiloDataContainer dataContainer;
    private MarryDivorceJSCalculator calculator;
    private static NormalDistribution d;
    private static GammaDistribution g;
    private double scaleN;
    private double scaleG;
    private static NormalDistribution dm;
    private static GammaDistribution gm;
    private double scaleNm;
    private double scaleGm;
    private HashMap<Gender, double[]> probabilities;

    @BeforeClass
    public static void setupModel() {
        Properties properties = SiloUtil.siloInitialization(Implementation.MARYLAND, "./test/scenarios/annapolis/javaFiles/siloMstm.properties");

        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get(), null, null);
        modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, null, properties, null);
        model = modelContainer.getBirth();

        Household household1 = HouseholdUtil.getFactory().createHousehold(1, 1,  0);
        dataContainer.getHouseholdData().addHousehold(household1);

        dataContainer.getRealEstateData().addDwelling(DwellingUtils.getFactory().createDwelling(1, -1, null, 1, DwellingType.MF234, 4, 1, 1000, -1, 2000));
        Person person1 = PersonUtils.getFactory().createPerson(1, 30, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person1);
        dataContainer.getHouseholdData().addPersonToHousehold(person1, household1);
        person1.setRole(PersonRole.MARRIED);
    }

    @Test
    public void main(){
        testBirth((int) 1e6);
        testBirth((int)100e6);
    }


    public void testBirth(int n) {

        long startTime = System.nanoTime();

        //JavaScript
        setupDivorceModel();
        long startJSRun = System.nanoTime();
        for (int i = 0; i < n; i++){
            double divorceProbability = calculator.calculateDivorceProbability(dataContainer.getHouseholdData().getPersonFromId(1).getType().ordinal()) / 2;
        }
        long endJSRun = System.nanoTime();

        //On the fly distribution
        setupDistribution();
        long startDistributionRun = System.nanoTime();
        for (int i = 0; i < n; i++){
            double divorceProbability;
            Person pp = dataContainer.getHouseholdData().getPersonFromId(1);
            if (pp.getGender().equals(Gender.FEMALE)) {
                divorceProbability = d.density((double) pp.getAge())*scaleN + g.density((double) pp.getAge())*scaleG;
            } else {
                divorceProbability = dm.density((double) pp.getAge())*scaleNm + gm.density((double) pp.getAge())*scaleGm;
            }
        }
        long endDistributionRun = System.nanoTime();

        //precalculated distribution
        precalculateDistribution();
        long startPrecalculatedRun = System.nanoTime();
        for (int i = 0; i < n; i++){
            Person pp = dataContainer.getHouseholdData().getPersonFromId(1);
            double divorceProbability = probabilities.get(pp.getGender())[pp.getAge()];
        }
        long finalTime = System.nanoTime();

        //outputs
        double t1 = (startJSRun - startTime) / 1_000_000_000.0;
        double t2 = (endJSRun - startJSRun) / 1_000_000_000.0;
        double tJS = t1 + t2;
        double t3 = (startDistributionRun - endJSRun) / 1_000_000_000.0;
        double t4 = (endDistributionRun - startDistributionRun) / 1_000_000_000.0;
        double tDist = t3 + t4;
        double t5 = (startPrecalculatedRun - endDistributionRun)/ 1_000_000_000.0;
        double t6 = (finalTime - startPrecalculatedRun)/ 1_000_000_000.0;
        double tDist2 = t5 + t6;
        System.out.println("Results for " + n/1000000 + " million iterations.");
        System.out.println("    JS. RunTime: " + tJS + ". Setup: " + t1 + " and execution: " + t2);
        System.out.println("    Distribution on the fly. RunTime: " + tDist + ". Setup: " + t3 + " and execution: " + t4);
        System.out.println("    Precalculated distribution. RunTime: " + tDist2 + ". Setup: " + t5 + " and execution: " + t6);
    }


    private void setupDivorceModel() {
        final Reader reader;
        reader = new InputStreamReader(this.getClass().getResourceAsStream("MarryDivorceCalcMuc"));
        calculator = new MarryDivorceJSCalculator(reader, 0);
    }

    private void setupDistribution(){
        d = new NormalDistributionImpl(3.7394, 0.25);
        g = new GammaDistributionImpl(27.213, 0.9039);
        scaleN = 0.4446;
        scaleG = 0.2364;
        dm = new NormalDistributionImpl(3.7394, 0.25);
        gm = new GammaDistributionImpl(27.213, 0.9039);
        scaleNm = 0.4446;
        scaleGm = 0.2364;
    }

    private void precalculateDistribution(){
        NormalDistribution femalesN = new NormalDistributionImpl(3.7394, 0.25);
        GammaDistribution femalesG = new GammaDistributionImpl(27.213, 0.9039);
        NormalDistribution malesN = new NormalDistributionImpl(3.7394, 0.25);
        GammaDistribution malesG = new GammaDistributionImpl(27.213, 0.9039);
        double scaleN = 0.4446;
        double scaleG = 0.2364;
        double scaleNm = 0.4446;
        double scaleGm = 0.2364;
        double[] probFemale = new double[100];
        double[] probMale = new double[100];
        for (int age = 0; age < 15; age++){
            probFemale[age] = 0;
            probMale[age] = 0;
        }
        for (int age = 15; age < 100; age++){
            probFemale[age] = d.density((double) age)*scaleN + g.density((double) age)*scaleG;
            probMale[age] = dm.density((double) age)*scaleNm + gm.density((double) age)*scaleGm;
        }
        probabilities = new HashMap<Gender, double[]>();
        probabilities.put(Gender.FEMALE,probFemale);
        probabilities.put(Gender.MALE, probMale);

    }
}
