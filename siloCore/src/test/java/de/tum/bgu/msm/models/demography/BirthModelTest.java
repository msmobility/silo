package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.Occupation;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.DwellingUtils;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.properties.Properties;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class BirthModelTest {

    private static BirthModel model;

    private static SiloModelContainer modelContainer;
    private static SiloDataContainer dataContainer;

    @BeforeClass
    public static void setupModel() {
        Properties properties = SiloUtil.siloInitialization("./test/scenarios/annapolis/javaFiles/siloMstm.properties", Implementation.MARYLAND);

        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
        modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, null, properties);
        model = modelContainer.getBirth();

        Household household1 = dataContainer.getHouseholdData().createHousehold(1, 1,  0);
        dataContainer.getRealEstateData().addDwelling(DwellingUtils.getFactory().createDwelling(1, -1, null, 1, DwellingType.MF234, 4, 1, 1000, -1, 2000));
        Person person1 = PersonUtils.getFactory().createPerson(1, 30, Gender.MALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person1);
        dataContainer.getHouseholdData().addPersonToHousehold(person1, household1);
        person1.setRole(PersonRole.SINGLE);
    }

    @Test
    public void testBirth() {
        Assert.assertEquals(1, dataContainer.getHouseholdData().getHouseholdFromId(1).getHhSize());
        model.giveBirth(dataContainer.getHouseholdData().getPersonFromId(1));
        Assert.assertEquals(2, dataContainer.getHouseholdData().getHouseholdFromId(1).getHhSize());
        Assert.assertTrue(dataContainer.getHouseholdData().getHouseholdFromId(1).getPersons().stream()
                .anyMatch(person -> person.getRole() == PersonRole.CHILD));
        Assert.assertEquals(true, dataContainer.getHouseholdData().getUpdatedHouseholds().containsKey(1));
    }
}
