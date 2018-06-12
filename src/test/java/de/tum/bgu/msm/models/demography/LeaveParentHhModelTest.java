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

public class LeaveParentHhModelTest {

    private static LeaveParentHhModel model;
    private static SiloDataContainer dataContainer;
    private static Person person;
    private static Household household;

    @BeforeClass
    public static void setupModel() {
        SiloUtil.siloInitialization("./test/scenarios/annapolis/javaFiles/siloMstm.properties", Implementation.MARYLAND);
        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
        SiloModelContainer modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, null);
        model = modelContainer.getLph();

        Dwelling dd = dataContainer.getRealEstateData()
                .createDwelling(999, 966, -1, DwellingType.SFD, 1, 1, 0, 1, 1999);

        household = dataContainer.getHouseholdData().createHousehold(999, 1, 0);
        Person parent1 = dataContainer.getHouseholdData().createPerson(123, 40, 1, Race.other, 3, 0, 0);
        Person parent2 = dataContainer.getHouseholdData().createPerson(456, 40, 2, Race.other, 3, 0, 0);

        person = dataContainer.getHouseholdData().createPerson(0, 20, 2, Race.other, 3, 0, 0);
        person.setRole(PersonRole.CHILD);
        dataContainer.getHouseholdData().addPersonToHousehold(person, household);
        dataContainer.getHouseholdData().addPersonToHousehold(parent1, household);
        dataContainer.getHouseholdData().addPersonToHousehold(parent2, household);

        dataContainer.getHouseholdData().setHighestHouseholdAndPersonId();
        dataContainer.getRealEstateData().setHighestVariables();
        dataContainer.getRealEstateData().identifyVacantDwellings();
        dataContainer.getRealEstateData().addDwellingToVacancyList(dd);
        dataContainer.getHouseholdData().calculateMedianHouseholdIncomeByMSA(dataContainer.getGeoData());
        modelContainer.getMove().calculateRegionalUtilities();
    }

    @Test
    public void testLeaveParents() {
        model.leaveHousehold(person);
        Assert.assertEquals(2, dataContainer.getHouseholdData().getHouseholdFromId(999).getHhSize());
        Assert.assertEquals(999, person.getHh().getDwellingId());
        Assert.assertEquals(PersonRole.SINGLE, person.getRole());
    }
}
