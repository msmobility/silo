package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.properties.Properties;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MarryDivorceModelTest {

    private static MarryDivorceModel model;
    private static int[] couple1;
    private static int[] couple2;
    private static int[] couple3;
    private static int[] couple4;

    private static SiloModelContainer modelContainer;
    private static SiloDataContainer dataContainer;
    private static HouseholdDataManager singleHouseholds;

    @BeforeClass
    public static void setupModel() {
        SiloUtil.siloInitialization("./test/scenarios/annapolis/javaFiles/siloMstm.properties", Implementation.MARYLAND);

        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
        modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, null);
        model = modelContainer.getMardiv();

        couple1 = new int[]{1,2};
        Household household1 = dataContainer.getHouseholdData().createHousehold(1,  1, 0);
        dataContainer.getRealEstateData().createDwelling(1, 1, 1, DwellingType.MF234, 2, 1, 1000, -1, 2000);
        Person person1 = dataContainer.getHouseholdData().createPerson(1, 30, 1, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person1, household1);
        person1.setRole(PersonRole.SINGLE);

        Household household2 = dataContainer.getHouseholdData().createHousehold(2, 2, 0);
        dataContainer.getRealEstateData().createDwelling(2, 1, 2, DwellingType.SFD, 4, 1, 1000, -1, 2000);
        Person person2 = dataContainer.getHouseholdData().createPerson(2, 30, 2, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person2, household2);
        person2.setRole(PersonRole.SINGLE);

        couple2 = new int[]{3,4};
        Household household3 = dataContainer.getHouseholdData().createHousehold(3, 3,  0);
        dataContainer.getRealEstateData().createDwelling(3, 1, 3, DwellingType.MF234, 4, 1, 1000, -1, 2000);
        Person person3 = dataContainer.getHouseholdData().createPerson(3, 30, 1, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person3, household3);
        person3.setRole(PersonRole.SINGLE);

        Household household4 = dataContainer.getHouseholdData().createHousehold(4, 4,  0);
        dataContainer.getRealEstateData().createDwelling(4, 1, 4, DwellingType.SFD, 2, 1, 1000, -1, 2000);
        Person person4 = dataContainer.getHouseholdData().createPerson(4, 30, 2, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person4, household4);
        person4.setRole(PersonRole.SINGLE);

        couple3 = new int[]{5,6};
        Household household5 = dataContainer.getHouseholdData().createHousehold(5, 5,  0);
        dataContainer.getRealEstateData().createDwelling(5, 1, 5, DwellingType.MF234, 4, 1, 1000, -1, 2000);
        Person person5 = dataContainer.getHouseholdData().createPerson(5, 30, 1, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person5, household5);
        person5.setRole(PersonRole.SINGLE);

        Household household6 = dataContainer.getHouseholdData().createHousehold(6, 6,  0);
        dataContainer.getRealEstateData().createDwelling(6, 1, 6, DwellingType.SFD, 2, 1, 1000, -1, 2000);
        Person person6 = dataContainer.getHouseholdData().createPerson(6, 30, 2, Race.other, -1, -1, 0);
        person6.setRole(PersonRole.SINGLE);
        dataContainer.getHouseholdData().addPersonToHousehold(person6, household6);
        Person person6Child = dataContainer.getHouseholdData().createPerson(61, 10, 2, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person6Child, household6);
        person6.setRole(PersonRole.CHILD);

        couple4 = new int[]{7,8};
        Household household7 = dataContainer.getHouseholdData().createHousehold(7, 7,  0);
        dataContainer.getRealEstateData().createDwelling(7, 1, 7, DwellingType.MF234, 4, 1, 1000, -1, 2000);
        Person person7 = dataContainer.getHouseholdData().createPerson(7, 30, 1, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person7, household7);
        person7.setRole(PersonRole.SINGLE);

        Person person7Child1 = dataContainer.getHouseholdData().createPerson(71, 10, 2, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person7Child1, household7);
        person7Child1.setRole(PersonRole.CHILD);
        Person person7Child2 = dataContainer.getHouseholdData().createPerson(72, 10, 2, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person7Child2, household7);
        person7Child2.setRole(PersonRole.CHILD);
        Person person7Child3 = dataContainer.getHouseholdData().createPerson(73, 10, 2, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person7Child3, household7);
        person7Child3.setRole(PersonRole.CHILD);

        Household household8 = dataContainer.getHouseholdData().createHousehold(8, 8,  0);
        dataContainer.getRealEstateData().createDwelling(8, 1, 8, DwellingType.SFD, 2, 1, 1000, -1, 2000);
        Person person8 = dataContainer.getHouseholdData().createPerson(8, 30, 2, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person8, household8);
        person8.setRole(PersonRole.SINGLE);

        Person person8Child1 = dataContainer.getHouseholdData().createPerson(81, 10, 2, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person8Child1, household8);
        person8Child1.setRole(PersonRole.CHILD);
        Person person8Child2 = dataContainer.getHouseholdData().createPerson(82, 10, 2, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person8Child2, household8);
        person8Child2.setRole(PersonRole.CHILD);
        Person person8Child3 = dataContainer.getHouseholdData().createPerson(83, 10, 2, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person8Child3, household8);
        person8Child3.setRole(PersonRole.CHILD);

        dataContainer.getHouseholdData().setHighestHouseholdAndPersonId();
        dataContainer.getRealEstateData().setHighestVariables();
        dataContainer.getRealEstateData().identifyVacantDwellings();
        modelContainer.getMove().calculateRegionalUtilities();

        Random rnd = new Random(42);
        singleHouseholds = new HouseholdDataManager(dataContainer);
        PrimitiveIterator.OfInt ages = rnd.ints(20, 60).iterator();
        PrimitiveIterator.OfInt genders = rnd.ints(1,3).iterator();
        Iterator<Race> races = rnd.ints(0, 4).mapToObj(i -> Race.values()[i]).iterator();
        PrimitiveIterator.OfInt occupations = rnd.ints(0,4).iterator();
        for(int i = 0; i < 10000; i++) {
            final Household household = singleHouseholds.createHousehold(i, i, 0);
            final Person p = singleHouseholds.createPerson(i, ages.nextInt(), genders.nextInt(), races.next(), occupations.nextInt(), 0, 0);
            p.setRole(PersonRole.SINGLE);
            singleHouseholds.addPersonToHousehold(p, household);
        }

    }

    @Test
    public void testMarriageGroomMovingToBride() {
        model.marryCouple(couple1);
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
        model.marryCouple(couple2);
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
        model.marryCouple(couple3);
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
        model.marryCouple(couple4);
        //new household outmigrated
        Assert.assertNull(dataContainer.getHouseholdData().getPersonFromId(7));
        Assert.assertNull(dataContainer.getHouseholdData().getPersonFromId(8));
        Assert.assertNull(dataContainer.getHouseholdData().getHouseholdFromId(7));
        Assert.assertNull(dataContainer.getHouseholdData().getHouseholdFromId(8));
    }

    @Test
    public void testSelectMarryingCouples() {
        SiloUtil.getRandomObject().setSeed(42);
        final List<Couple> couples = model.selectCouplesToGetMarriedThisYear(singleHouseholds.getPersons());
        Assert.assertEquals(689, couples.size());
        final long manMarriesYoungerWoman = couples.stream()
                .filter(couple ->
                        (couple.getPartner1().getGender() == 1
                                && couple.getPartner1().getAge() > couple.getPartner2().getAge())
                        || (couple.getPartner2().getGender() == 1
                                && couple.getPartner1().getAge() < couple.getPartner2().getAge())
                ).count();
        final long sameSexMarriages = couples.stream().filter(couple ->
                couple.getPartner1().getGender() == couple.getPartner2().getGender()).count();
        Assert.assertEquals(0, sameSexMarriages);
        Assert.assertEquals(440, manMarriesYoungerWoman);
        Assert.assertTrue(model.selectCouplesToGetMarriedThisYear(Collections.EMPTY_LIST).isEmpty());
    }
}
