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

public class DeathModelTest {

    private static DeathModel model;
 
    private static SiloModelContainer modelContainer;
    private static SiloDataContainer dataContainer;

    @BeforeClass
    public static void setupModel() {
        SiloUtil.siloInitialization("./test/scenarios/annapolis/javaFiles/siloMstm.properties", Implementation.MARYLAND);

        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
        modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, null);
        model = modelContainer.getDeath();
        
        Household household1 = dataContainer.getHouseholdData().createHousehold(1, 1,  0);
        dataContainer.getRealEstateData().createDwelling(1, 1, 1, DwellingType.MF234, 4, 1, 1000, -1, 2000);
        Person person1 = dataContainer.getHouseholdData().createPerson(1, 30, 1, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person1, household1);
        person1.setRole(PersonRole.SINGLE);

        Person person1Child1 = dataContainer.getHouseholdData().createPerson(11, 10, 2, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person1Child1, household1);
        person1Child1.setRole(PersonRole.CHILD);
        Person person1Child2 = dataContainer.getHouseholdData().createPerson(12, 10, 2, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person1Child2, household1);
        person1Child2.setRole(PersonRole.CHILD);
        Person person1Child3 = dataContainer.getHouseholdData().createPerson(13, 10, 2, Race.other, -1, -1, 0);
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
