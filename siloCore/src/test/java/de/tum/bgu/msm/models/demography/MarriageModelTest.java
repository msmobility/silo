package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.Occupation;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.DwellingUtils;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.events.impls.MarriageEvent;
import de.tum.bgu.msm.properties.Properties;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

public class MarriageModelTest {

    private static DefaultMarriageModel model;
    private static int[] couple1;
    private static int[] couple2;
    private static int[] couple3;
    private static int[] couple4;

    private static SiloDataContainer dataContainer;
    private static HouseholdDataManager singleHouseholds;

    @BeforeClass
    public static void setupModel() {
        Properties properties = SiloUtil.siloInitialization("./test/scenarios/annapolis/javaFiles/siloMstm.properties", Implementation.MARYLAND);

        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
        SiloModelContainer modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, null, properties);
        model = (DefaultMarriageModel) modelContainer.getMarriage();

        couple1 = new int[]{1,2};
        Household household1 = dataContainer.getHouseholdData().createHousehold(1,  1, 0);
        dataContainer.getRealEstateData().addDwelling(DwellingUtils.getFactory().createDwelling(1, 999, null, 1, DwellingType.MF234, 2, 1, 1000, -1, 2000));
        Person person1 = PersonUtils.getFactory().createPerson(1, 30, Gender.MALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person1);
        dataContainer.getHouseholdData().addPersonToHousehold(person1, household1);
        person1.setRole(PersonRole.SINGLE);

        Household household2 = dataContainer.getHouseholdData().createHousehold(2, 2, 0);
        dataContainer.getRealEstateData().addDwelling(DwellingUtils.getFactory().createDwelling(2, 999, null, 2, DwellingType.SFD, 4, 1, 1000, -1, 2000));
        Person person2 = PersonUtils.getFactory().createPerson(2, 30, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person2);
        dataContainer.getHouseholdData().addPersonToHousehold(person2, household2);
        person2.setRole(PersonRole.SINGLE);

        couple2 = new int[]{3,4};
        Household household3 = dataContainer.getHouseholdData().createHousehold(3, 3,  0);
        dataContainer.getRealEstateData().addDwelling(DwellingUtils.getFactory().createDwelling(3, 999, null, 3, DwellingType.MF234, 4, 1, 1000, -1, 2000));
        Person person3 = PersonUtils.getFactory().createPerson(3, 30, Gender.MALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person3);
        dataContainer.getHouseholdData().addPersonToHousehold(person3, household3);
        person3.setRole(PersonRole.SINGLE);

        Household household4 = dataContainer.getHouseholdData().createHousehold(4, 4,  0);
        dataContainer.getRealEstateData().addDwelling(DwellingUtils.getFactory().createDwelling(4, 999, null, 4, DwellingType.SFD, 2, 1, 1000, -1, 2000));
        Person person4 = PersonUtils.getFactory().createPerson(4, 30, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person4);
        dataContainer.getHouseholdData().addPersonToHousehold(person4, household4);
        person4.setRole(PersonRole.SINGLE);

        couple3 = new int[]{5,6};
        Household household5 = dataContainer.getHouseholdData().createHousehold(5, 5,  0);
        dataContainer.getRealEstateData().addDwelling(DwellingUtils.getFactory().createDwelling(5, 999, null, 5, DwellingType.MF234, 4, 1, 1000, -1, 2000));
        Person person5 = PersonUtils.getFactory().createPerson(5, 30, Gender.MALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person5);
        dataContainer.getHouseholdData().addPersonToHousehold(person5, household5);
        person5.setRole(PersonRole.SINGLE);

        Household household6 = dataContainer.getHouseholdData().createHousehold(6, 6,  0);
        dataContainer.getRealEstateData().addDwelling(DwellingUtils.getFactory().createDwelling(6, 999, null, 6, DwellingType.SFD, 2, 1, 1000, -1, 2000));
        Person person6 = PersonUtils.getFactory().createPerson(6, 30, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person6);
        person6.setRole(PersonRole.SINGLE);
        dataContainer.getHouseholdData().addPersonToHousehold(person6, household6);
        Person person6Child = PersonUtils.getFactory().createPerson(61, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person6Child);
        dataContainer.getHouseholdData().addPersonToHousehold(person6Child, household6);
        person6.setRole(PersonRole.CHILD);

        couple4 = new int[]{7,8};
        Household household7 = dataContainer.getHouseholdData().createHousehold(7, 7,  0);
        dataContainer.getRealEstateData().addDwelling(DwellingUtils.getFactory().createDwelling(7, 999, null, 7, DwellingType.MF234, 4, 1, 1000, -1, 2000));
        Person person7 = PersonUtils.getFactory().createPerson(7, 30, Gender.MALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person7);
        dataContainer.getHouseholdData().addPersonToHousehold(person7, household7);
        person7.setRole(PersonRole.SINGLE);

        Person person7Child1 = PersonUtils.getFactory().createPerson(71, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person7Child1);
        dataContainer.getHouseholdData().addPersonToHousehold(person7Child1, household7);
        person7Child1.setRole(PersonRole.CHILD);
        Person person7Child2 = PersonUtils.getFactory().createPerson(72, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person7Child2);
        dataContainer.getHouseholdData().addPersonToHousehold(person7Child2, household7);
        person7Child2.setRole(PersonRole.CHILD);
        Person person7Child3 = PersonUtils.getFactory().createPerson(73, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person7Child3);
        dataContainer.getHouseholdData().addPersonToHousehold(person7Child3, household7);
        person7Child3.setRole(PersonRole.CHILD);

        Household household8 = dataContainer.getHouseholdData().createHousehold(8, 8,  0);
        dataContainer.getRealEstateData().addDwelling(DwellingUtils.getFactory().createDwelling(8, 999, null, 8, DwellingType.SFD, 2, 1, 1000, -1, 2000));
        Person person8 = PersonUtils.getFactory().createPerson(8, 30, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person8);
        dataContainer.getHouseholdData().addPersonToHousehold(person8, household8);
        person8.setRole(PersonRole.SINGLE);

        Person person8Child1 = PersonUtils.getFactory().createPerson(81, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person8Child1);
        dataContainer.getHouseholdData().addPersonToHousehold(person8Child1, household8);
        person8Child1.setRole(PersonRole.CHILD);
        Person person8Child2 = PersonUtils.getFactory().createPerson(82, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person8Child2);
        dataContainer.getHouseholdData().addPersonToHousehold(person8Child2, household8);
        person8Child2.setRole(PersonRole.CHILD);
        Person person8Child3 = PersonUtils.getFactory().createPerson(83, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person8Child3);
        dataContainer.getHouseholdData().addPersonToHousehold(person8Child3, household8);
        person8Child3.setRole(PersonRole.CHILD);

        dataContainer.getHouseholdData().setHighestHouseholdAndPersonId();
        dataContainer.getRealEstateData().setHighestVariablesAndCalculateRentShareByIncome();
        dataContainer.getRealEstateData().identifyVacantDwellings();
        modelContainer.getMove().calculateRegionalUtilities();

        Random rnd = new Random(42);
        singleHouseholds = new HouseholdDataManager(dataContainer, PersonUtils.getFactory());
        PrimitiveIterator.OfInt ages = rnd.ints(20, 60).iterator();
        PrimitiveIterator.OfInt genders = rnd.ints(1,3).iterator();
        Iterator<Race> races = rnd.ints(0, 4).mapToObj(i -> Race.values()[i]).iterator();
        PrimitiveIterator.OfInt occupations = rnd.ints(0,5).iterator();
        for(int i = 0; i < 10000; i++) {
            final Household household = singleHouseholds.createHousehold(i, i, 0);
            final Person p = PersonUtils.getFactory().createPerson(i, ages.nextInt(), Gender.valueOf(genders.nextInt()), races.next(), Occupation.valueOf(occupations.nextInt()), 0, 0);
            singleHouseholds.addPerson(p);
            p.setRole(PersonRole.SINGLE);
            singleHouseholds.addPersonToHousehold(p, household);
        }

    }

    @Test
    public void testMarriageGroomMovingToBride() {
        model.handleEvent(new MarriageEvent(couple1[0], couple1[1]));
        //both persons should be married
        Assert.assertEquals(PersonRole.MARRIED, dataContainer.getHouseholdData().getPersonFromId(1).getRole());
        Assert.assertEquals(PersonRole.MARRIED, dataContainer.getHouseholdData().getPersonFromId(2).getRole());
        //household 1 has disbanded
        Assert.assertNull(dataContainer.getHouseholdData().getHouseholdFromId(1));
        //person 1 moved to household 2
        Assert.assertEquals(2, dataContainer.getHouseholdData().getHouseholdFromId(2).getHhSize());
    }

    @Test
    public void testMarriageBrideMovingToGroom() {
        model.handleEvent(new MarriageEvent(couple2[0], couple2[1]));
        //both persons should be married
        Assert.assertEquals(PersonRole.MARRIED, dataContainer.getHouseholdData().getPersonFromId(3).getRole());
        Assert.assertEquals(PersonRole.MARRIED, dataContainer.getHouseholdData().getPersonFromId(4).getRole());
        //person 4 moved to household 3
        Assert.assertEquals(2, dataContainer.getHouseholdData().getHouseholdFromId(3).getHhSize());
        //household 4 has disbanded
        Assert.assertNull(dataContainer.getHouseholdData().getHouseholdFromId(4));
    }

    @Test
    public void testMarriageBrideMovingToGroomWithChild() {
        model.handleEvent(new MarriageEvent(couple3[0], couple3[1]));
        //both persons should be married
        Assert.assertEquals(PersonRole.MARRIED, dataContainer.getHouseholdData().getPersonFromId(5).getRole());
        Assert.assertEquals(PersonRole.MARRIED, dataContainer.getHouseholdData().getPersonFromId(6).getRole());
        //person 6 and 7 moved to household 5
        Assert.assertEquals(3, dataContainer.getHouseholdData().getHouseholdFromId(5).getHhSize());
        //household 6 has disbanded
        Assert.assertNull(dataContainer.getHouseholdData().getHouseholdFromId(6));
    }

    @Test
    public void testMarriageOutmigratingNewHousehold() {
        model.handleEvent(new MarriageEvent(couple4[0], couple4[1]));
        //new household outmigrated
        Assert.assertNull(dataContainer.getHouseholdData().getPersonFromId(7));
        Assert.assertNull(dataContainer.getHouseholdData().getPersonFromId(8));
        Assert.assertNull(dataContainer.getHouseholdData().getHouseholdFromId(7));
        Assert.assertNull(dataContainer.getHouseholdData().getHouseholdFromId(8));
    }

    @Test
    public void testSelectMarryingCouples() {
        SiloUtil.getRandomObject().setSeed(42);
        final List<MarriageEvent> couples = model.selectCouplesToGetMarriedThisYear(singleHouseholds.getPersons());
        Assert.assertEquals(689, couples.size());
        final long manMarriesYoungerWoman = couples.stream()
                .filter(couple -> {
                            Person partner1 = singleHouseholds.getPersonFromId(couple.getFirstId());
                            Person partner2 = singleHouseholds.getPersonFromId(couple.getSecondId());
                    return (partner1.getGender() == Gender.MALE
                            && partner1.getAge() > partner2.getAge())
                            || (partner2.getGender() == Gender.MALE
                            && partner1.getAge() < partner2.getAge());
                        }
                ).count();

        Assert.assertEquals(440, manMarriesYoungerWoman);
        Assert.assertTrue(model.selectCouplesToGetMarriedThisYear(Collections.EMPTY_LIST).isEmpty());
    }
}
