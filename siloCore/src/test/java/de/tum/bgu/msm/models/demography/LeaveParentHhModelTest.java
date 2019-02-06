package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypeImpl;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingUtils;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
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
        Properties properties = SiloUtil.siloInitialization(Implementation.MARYLAND, "./test/scenarios/annapolis/javaFiles/siloMstm.properties");
        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
        SiloModelContainer modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, null, properties);
        model = modelContainer.getLph();

        Dwelling dd = DwellingUtils.getFactory()
                .createDwelling(999, 1093, null, -1, DefaultDwellingTypeImpl.SFD, 1, 1, 0, 1, 1999);
        dataContainer.getRealEstateData().addDwelling(dd);

        household = HouseholdUtil.getFactory().createHousehold(999, 1, 0);
        dataContainer.getHouseholdData().addHousehold(household);
        Person parent1 = PersonUtils.getFactory().createPerson(123, 40, Gender.MALE, Race.other, Occupation.EMPLOYED, PersonRole.SINGLE, 0, 0);
        Person parent2 = PersonUtils.getFactory().createPerson(456, 40, Gender.FEMALE, Race.other, Occupation.EMPLOYED, PersonRole.SINGLE, 0, 0);
        dataContainer.getHouseholdData().addPerson(parent1);
        dataContainer.getHouseholdData().addPerson(parent2);

        person = PersonUtils.getFactory().createPerson(0, 20, Gender.FEMALE, Race.other, Occupation.STUDENT, PersonRole.SINGLE, 0, 0);
        dataContainer.getHouseholdData().addPerson(person);
        person.setRole(PersonRole.CHILD);
        dataContainer.getHouseholdData().addPersonToHousehold(person, household);
        dataContainer.getHouseholdData().addPersonToHousehold(parent1, household);
        dataContainer.getHouseholdData().addPersonToHousehold(parent2, household);

        dataContainer.getHouseholdData().identifyHighestHouseholdAndPersonId();
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
        Household household = person.getHousehold();
        Assert.assertEquals(999, household.getDwellingId());
        Assert.assertEquals(PersonRole.SINGLE, person.getRole());
    }
}
