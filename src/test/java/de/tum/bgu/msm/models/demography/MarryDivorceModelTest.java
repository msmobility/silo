package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class MarryDivorceModelTest {

    private static MarryDivorceModel model;
    private static int[] couple1;
    private static int[] couple2;
    private static int[] couple3;

    private static SiloModelContainer modelContainer;
    private static SiloDataContainer dataContainer;

    @BeforeClass
    public static void setupModel() {
        SiloUtil.siloInitialization("./test/scenarios/annapolis/javaFiles/siloMstm.properties", Implementation.MARYLAND);

        dataContainer = SiloDataContainer.createSiloDataContainer();
        modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer);
        model = new MarryDivorceModel();

        couple1 = new int[]{1,2};
        Household household1 = new Household(1,  1, 0);
        Dwelling dwelling1 = new Dwelling(1, 1, 1, DwellingType.MF234, 2, 1, 1000, -1, 2000);
        Person person1 = new Person(1, 30, 2, Race.other, -1, -1, 0);
        person1.setHousehold(household1);
        household1.addPerson(person1);
        person1.setRole(PersonRole.SINGLE);

        Household household2 = new Household(1, 2, 0);
        Dwelling dwelling2 = new Dwelling(2, 1, 2, DwellingType.SFD, 4, 1, 1000, -1, 2000);
        Person person2 = new Person(2, 30, 2, Race.other, -1, -1, 0);
        person2.setHousehold(household2);
        household2.addPerson(person2);
        person2.setRole(PersonRole.SINGLE);

        couple2 = new int[]{3,4};
        Household household3 = new Household(3, 3,  0);
        Dwelling dwelling3 = new Dwelling(3, 1, 3, DwellingType.MF234, 4, 1, 1000, -1, 2000);
        Person person3 = new Person(3, 30, 2, Race.other, -1, -1, 0);
        person3.setHousehold(household3);
        household3.addPerson(person3);
        person3.setRole(PersonRole.SINGLE);

        Household household4 = new Household(4, 4,  0);
        Dwelling dwelling4 = new Dwelling(4, 1, 4, DwellingType.SFD, 2, 1, 1000, -1, 2000);
        Person person4 = new Person(4, 30, 2, Race.other, -1, -1, 0);
        person4.setHousehold(household4);
        household4.addPerson(person4);
        person4.setRole(PersonRole.SINGLE);

        couple3 = new int[]{5,6};
        Household household5 = new Household(5, 5,  0);
        Dwelling dwelling5 = new Dwelling(5, 1, 5, DwellingType.MF234, 4, 1, 1000, -1, 2000);
        Person person5 = new Person(5, 30, 2, Race.other, -1, -1, 0);
        person5.setHousehold(household5);
        household5.addPerson(person5);
        person5.setRole(PersonRole.SINGLE);

        Household household6 = new Household(6, 6,  0);
        Dwelling dwelling6 = new Dwelling(6, 1, 6, DwellingType.SFD, 2, 1, 1000, -1, 2000);
        Person person6 = new Person(6, 30, 2, Race.other, -1, -1, 0);
        person6.setHousehold(household6);
        household6.addPerson(person6);
        Person person6Child = new Person(61, 10, 2, Race.other, -1, -1, 0);
        person6Child.setHousehold(household6);
        household6.addPerson(person6Child);
        person6.setRole(PersonRole.CHILD);
    }

    @Test
    public void testMarriageGroomMovingToBride() {
        model.marryCouple(couple1, modelContainer, dataContainer);
        //both persons should be married
        Assert.assertEquals(PersonRole.MARRIED, Person.getPersonFromId(1).getRole());
        Assert.assertEquals(PersonRole.MARRIED, Person.getPersonFromId(2).getRole());
        //household 1 has disbanded
        Assert.assertNull(Household.getHouseholdFromId(1));
        //person 1 moved to household 2
        Assert.assertEquals(2, Household.getHouseholdFromId(2).getHhSize());
    }

    @Test
    public void testMarriageBrideMovingToGroom() {
        model.marryCouple(couple2, modelContainer, dataContainer);
        //both persons should be married
        Assert.assertEquals(PersonRole.MARRIED, Person.getPersonFromId(3).getRole());
        Assert.assertEquals(PersonRole.MARRIED, Person.getPersonFromId(4).getRole());
        //person 4 moved to household 3
        Assert.assertEquals(2, Household.getHouseholdFromId(3).getHhSize());
        //household 4 has disbanded
        Assert.assertNull(Household.getHouseholdFromId(4));
    }

    @Test
    public void testMarriageBrideMovingToGroomWithChild() {
        model.marryCouple(couple3, modelContainer, dataContainer);
        //both persons should be married
        Assert.assertEquals(PersonRole.MARRIED, Person.getPersonFromId(5).getRole());
        Assert.assertEquals(PersonRole.MARRIED, Person.getPersonFromId(6).getRole());
        //person 6 and 7 moved to household 5
        Assert.assertEquals(3, Household.getHouseholdFromId(5).getHhSize());
        //household 6 has disbanded
        Assert.assertNull(Household.getHouseholdFromId(6));
    }
}
