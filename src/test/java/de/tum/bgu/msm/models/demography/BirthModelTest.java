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

import java.util.Arrays;

public class BirthModelTest {

    private static BirthModel model;

    private static SiloModelContainer modelContainer;
    private static SiloDataContainer dataContainer;

    @BeforeClass
    public static void setupModel() {
        SiloUtil.siloInitialization("./test/scenarios/annapolis/javaFiles/siloMstm.properties", Implementation.MARYLAND);

        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
        modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, null);
        model = modelContainer.getBirth();

        Household household1 = dataContainer.getHouseholdData().createHousehold(1, 1,  0);
        dataContainer.getRealEstateData().createDwelling(1, 1, 1, DwellingType.MF234, 4, 1, 1000, -1, 2000);
        Person person1 = dataContainer.getHouseholdData().createPerson(1, 30, 1, Race.other, -1, -1, 0);
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

    @Test
    public void testBirthDay() {
        final Person person = dataContainer.getHouseholdData().getPersonFromId(1);
        final int before = person.getAge();
        model.celebrateBirthday(person);
        final int after = person.getAge();
        Assert.assertTrue(after > before);
    }
}
