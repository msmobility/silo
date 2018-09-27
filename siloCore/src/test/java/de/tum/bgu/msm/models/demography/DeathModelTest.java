package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.Occupation;
import de.tum.bgu.msm.data.RealEstateDataManager;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.DwellingUtils;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.properties.Properties;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

public class DeathModelTest {

    private static DeathModel model;
 
    private static SiloModelContainer modelContainer;
    private static SiloDataContainer dataContainer;

    @BeforeClass
    public static void setupModel() {
        Properties properties = SiloUtil.siloInitialization("./test/scenarios/annapolis/javaFiles/siloMstm.properties", Implementation.MARYLAND);

        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
        modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, null, properties);
        model = modelContainer.getDeath();
        
        Household household1 = dataContainer.getHouseholdData().createHousehold(1, 1,  0);
        dataContainer.getRealEstateData().addDwelling(DwellingUtils.getFactory().createDwelling(1,99, null, 1, DwellingType.MF234, 4, 1, 1000, -1, 2000));
        Person person1 = PersonUtils.getFactory().createPerson(1, 30, Gender.MALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person1);

        dataContainer.getHouseholdData().addPersonToHousehold(person1, household1);
        person1.setRole(PersonRole.SINGLE);

        Person person1Child1 = PersonUtils.getFactory().createPerson(11, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person1Child1);
        dataContainer.getHouseholdData().addPersonToHousehold(person1Child1, household1);
        person1Child1.setRole(PersonRole.CHILD);
        Person person1Child2 = PersonUtils.getFactory().createPerson(12, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person1Child2);
        dataContainer.getHouseholdData().addPersonToHousehold(person1Child2, household1);
        person1Child2.setRole(PersonRole.CHILD);
        Person person1Child3 = PersonUtils.getFactory().createPerson(13, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person1Child3);
        dataContainer.getHouseholdData().addPersonToHousehold(person1Child3, household1);
        person1Child3.setRole(PersonRole.CHILD);

        dataContainer.getRealEstateData().identifyVacantDwellings();

    }

    @Test
    public void testDeathOfParent() {
        dataContainer.getHouseholdData().clearUpdatedHouseholds();
        final int[] listOfVacantDwellingsInRegion = RealEstateDataManager.getListOfVacantDwellingsInRegion(
                dataContainer.getGeoData().getZones().get(1).getRegion().getId());
        Assert.assertEquals(0,Arrays.stream(listOfVacantDwellingsInRegion).filter(value -> value == 1).count());
        Assert.assertEquals(1, dataContainer.getRealEstateData().getDwelling(1).getResidentId());
        model.die(dataContainer.getHouseholdData().getPersonFromId(1));
        Assert.assertNull(dataContainer.getHouseholdData().getHouseholdFromId(1));
        Assert.assertNull(dataContainer.getHouseholdData().getPersonFromId(1));
        Assert.assertNull(dataContainer.getHouseholdData().getPersonFromId(11));
        Assert.assertNull(dataContainer.getHouseholdData().getPersonFromId(12));
        Assert.assertNull(dataContainer.getHouseholdData().getPersonFromId(13));
        Assert.assertEquals(-1, dataContainer.getRealEstateData().getDwelling(1).getResidentId());
        final int[] listOfVacantDwellingsInRegionAfter = RealEstateDataManager.getListOfVacantDwellingsInRegion(
                dataContainer.getGeoData().getZones().get(1).getRegion().getId());
        Assert.assertEquals(1,Arrays.stream(listOfVacantDwellingsInRegionAfter).filter(value -> value == 1).count());
        Assert.assertEquals(false, dataContainer.getHouseholdData().getUpdatedHouseholds().containsKey(1));
    }
}
