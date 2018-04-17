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

        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
        modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, null);
        model = new MarryDivorceModel(dataContainer);

        couple1 = new int[]{1,2};
        Household household1 = dataContainer.getHouseholdData().createHousehold(1,  1, 0);
        dataContainer.getRealEstateData().createDwelling(1, 1, 1, DwellingType.MF234, 2, 1, 1000, -1, 2000);
        Person person1 = dataContainer.getHouseholdData().createPerson(1, 30, 2, Race.other, -1, -1, 0);
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
        Person person3 = dataContainer.getHouseholdData().createPerson(3, 30, 2, Race.other, -1, -1, 0);
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
        Person person5 = dataContainer.getHouseholdData().createPerson(5, 30, 2, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person5, household5);
        person5.setRole(PersonRole.SINGLE);

        Household household6 = dataContainer.getHouseholdData().createHousehold(6, 6,  0);
        dataContainer.getRealEstateData().createDwelling(6, 1, 6, DwellingType.SFD, 2, 1, 1000, -1, 2000);
        Person person6 = dataContainer.getHouseholdData().createPerson(6, 30, 2, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person6, household6);
        Person person6Child = dataContainer.getHouseholdData().createPerson(61, 10, 2, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person6Child, household6);
        person6.setRole(PersonRole.CHILD);

        dataContainer.getRealEstateData().identifyVacantDwellings();
    }

    @Test
    public void testMarriageGroomMovingToBride() {
        model.marryCouple(couple1, modelContainer);
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
        model.marryCouple(couple2, modelContainer);
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
        model.marryCouple(couple3, modelContainer);
        //both persons should be married
        Assert.assertEquals(PersonRole.MARRIED, dataContainer.getHouseholdData().getPersonFromId(5).getRole());
        Assert.assertEquals(PersonRole.MARRIED, dataContainer.getHouseholdData().getPersonFromId(6).getRole());
        //person 6 and 7 moved to household 5
        Assert.assertEquals(3, dataContainer.getHouseholdData().getHouseholdFromId(5).getHhSize());
        //household 6 has disbanded
        Assert.assertNull(dataContainer.getHouseholdData().getHouseholdFromId(6));
    }
}
