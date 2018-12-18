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
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

public class DemographicDistributionBirthTest {

    private static BirthModel model;
    private static SiloModelContainer modelContainer;
    private static SiloDataContainer dataContainer;
    private BirthJSCalculator calculator;
    private static NormalDistribution d0;
    private static NormalDistribution d1;
    private static NormalDistribution d2;
    private static NormalDistribution d3;
    private float scale0;
    private float scale1;
    private float scale2;
    private float scale3;
    private HashMap<Integer, double[]> probabilities;

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
        person1.setRole(PersonRole.SINGLE);
    }

    @Test
    public void main(){
        testBirth((int) 1e6);
        testBirth((int)100e6);
    }



    public void testBirth(int n) {

        //JavaScript
        long startTime = System.nanoTime();
        setupBirthModel();
        long startJSRun = System.nanoTime();
        for (int i = 0; i < n; i++){
            double birthProb = calculator.calculateBirthProbability(dataContainer.getHouseholdData().getPersonFromId(1).getAge(),
                    HouseholdUtil.getNumberOfChildren(dataContainer.getHouseholdData().getHouseholdFromId(1)));
        }
        long endJSRun = System.nanoTime();

        //On the fly distribution
        setupDistribution();
        long startDistributionRun = System.nanoTime();
        for (int i = 0; i < n; i++){
            int age = dataContainer.getHouseholdData().getPersonFromId(1).getAge();
            int numberOfChildren = HouseholdUtil.getNumberOfChildren(dataContainer.getHouseholdData().getHouseholdFromId(1));
            double birthProb;
            if (numberOfChildren == 0) {
                birthProb = d0.density((double) age) * scale0;
            } else if (numberOfChildren == 1){
                birthProb = d1.density((double) age) * scale1;
            } else if (numberOfChildren == 2){
                birthProb = d2.density((double) age) * scale2;
            } else {
                birthProb = d3.density((double) age) * scale3;
            }
        }
        long endDistributionRun = System.nanoTime();

        //Precalculated distribution
        precalculateDistribution();
        long startPrecalculatedRun = System.nanoTime();
        for (int i = 0; i < n; i++){
            double birthProb = probabilities.get(HouseholdUtil.getNumberOfChildren(dataContainer.getHouseholdData().getHouseholdFromId(1)))
                    [dataContainer.getHouseholdData().getPersonFromId(1).getAge()];
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

    private void setupBirthModel() {
        final Reader reader;
        reader = new InputStreamReader(this.getClass().getResourceAsStream("BirthProbabilityCalcMuc"));
        float localScaler = Properties.get().demographics.localScaler;
        calculator = new BirthJSCalculator(reader, localScaler);
    }

    private void setupDistribution(){
        //Dummy distributions for second or more children, only to calculate runtime (for the first child is correct)
        d0 = new NormalDistributionImpl(31.175, 5.5318);
        d1 = new NormalDistributionImpl(31.175, 5.5318);
        d2 = new NormalDistributionImpl(31.175, 5.5318);
        d3 = new NormalDistributionImpl(31.175, 5.5318);
        scale0 = 3384.07f;
        scale1 = 3384.07f;
        scale2 = 3384.07f;
        scale3 = 3384.07f;
    }

    private void precalculateDistribution(){
        //Dummy distributions for second or more children, only to calculate runtime (for the first child is correct)
        NormalDistribution d0 = new NormalDistributionImpl(31.175, 5.5318);
        NormalDistribution d1 = new NormalDistributionImpl(31.175, 5.5318);
        NormalDistribution d2 = new NormalDistributionImpl(31.175, 5.5318);
        NormalDistribution d3 = new NormalDistributionImpl(31.175, 5.5318);
        double scale0 = 3384.07f;
        double scale1 = 3384.07f;
        double scale2 = 3384.07f;
        double scale3 = 3384.07f;
        double[] prob0 = new double[100];
        double[] prob1 = new double[100];
        double[] prob2 = new double[100];
        double[] prob3 = new double[100];
        for (int age = 0; age < 100; age++){
            prob0[age] = d0.density((double) age)*scale0;
            prob1[age] = d1.density((double) age)*scale1;
            prob2[age] = d2.density((double) age)*scale2;
            prob3[age] = d3.density((double) age)*scale3;
        }
        probabilities = new HashMap<Integer, double[]>();
        probabilities.put(0,prob0);
        probabilities.put(1,prob1);
        probabilities.put(2,prob2);
        probabilities.put(3,prob3);
    }
}
