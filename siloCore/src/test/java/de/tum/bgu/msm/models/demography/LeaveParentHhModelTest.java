package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.Occupation;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.DwellingUtils;
import de.tum.bgu.msm.data.person.*;
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
        Properties properties = SiloUtil.siloInitialization("./test/scenarios/annapolis/javaFiles/siloMstm.properties", Implementation.MARYLAND);
        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
        SiloModelContainer modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, null, properties);
        model = modelContainer.getLph();

        Dwelling dd = DwellingUtils.getFactory()
                .createDwelling(999, 1093, null, -1, DwellingType.SFD, 1, 1, 0, 1, 1999);
        dataContainer.getRealEstateData().addDwelling(dd);

        household = dataContainer.getHouseholdData().createHousehold(999, 1, 0);
        Person parent1 = PersonUtils.getFactory().createPerson(123, 40, Gender.MALE, Race.other, Occupation.EMPLOYED, 0, 0);
        Person parent2 = PersonUtils.getFactory().createPerson(456, 40, Gender.FEMALE, Race.other, Occupation.EMPLOYED, 0, 0);
        dataContainer.getHouseholdData().addPerson(parent1);
        dataContainer.getHouseholdData().addPerson(parent2);

        person = PersonUtils.getFactory().createPerson(0, 20, Gender.FEMALE, Race.other, Occupation.STUDENT, 0, 0);
        dataContainer.getHouseholdData().addPerson(person);
        person.setRole(PersonRole.CHILD);
        dataContainer.getHouseholdData().addPersonToHousehold(person, household);
        dataContainer.getHouseholdData().addPersonToHousehold(parent1, household);
        dataContainer.getHouseholdData().addPersonToHousehold(parent2, household);

        dataContainer.getHouseholdData().setHighestHouseholdAndPersonId();
        dataContainer.getRealEstateData().setHighestVariablesAndCalculateRentShareByIncome();
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
