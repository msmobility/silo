//package de.tum.bgu.msm.models.demography;
//
//import de.tum.bgu.msm.Implementation;
//import de.tum.bgu.msm.container.DataContainerImpl;
//import de.tum.bgu.msm.container.ModelContainerImpl;
//import de.tum.bgu.msm.data.dwelling.RealEstateDataManagerImpl;
//import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypeImpl;
//import de.tum.bgu.msm.models.demography.death.DeathModelImpl;
//import de.tum.bgu.msm.utils.SiloUtil;
//import de.tum.bgu.msm.data.dwelling.DwellingUtils;
//import de.tum.bgu.msm.data.household.Household;
//import de.tum.bgu.msm.data.household.HouseholdUtil;
//import de.tum.bgu.msm.data.person.*;
//import de.tum.bgu.msm.properties.Properties;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import java.util.Arrays;
//
//public class DeathModelTest {
//
//    private static DeathModelImpl model;
//
//    private static ModelContainer modelContainer;
//    private static DataContainerImpl dataContainer;
//    private static Household household1;
//
//    @BeforeClass
//    public static void setupModel() {
//        Properties properties = SiloUtil.siloInitialization(Implementation.MARYLAND, "./test/scenarios/annapolis/javaFiles/siloMstm.properties");
//
//        dataContainer = DataContainerImpl.loadSiloDataContainer(Properties.get());
//        modelContainer = ModelContainerImpl.createSiloModelContainer(dataContainer, null, properties);
//        model = new DeathModelImpl(dataContainer, properties);
//
//
//        household1 = HouseholdUtil.getFactory().createHousehold(1, 1,  0);
//        dataContainer.getHouseholdDataManager().addHousehold(household1);
//        dataContainer.getRealEstateDataManager().addDwelling(DwellingUtils.getFactory().createDwelling(1,99, null, 1, DefaultDwellingTypeImpl.MF234, 4, 1, 1000, -1, 2000));
//        Person person1 = PersonUtils.getFactory().createPerson(1, 30, Gender.MALE, Race.other, Occupation.UNEMPLOYED, PersonRole.SINGLE,  -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person1);
//
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person1, household1);
//        person1.setRole(PersonRole.SINGLE);
//
//        Person person1Child1 = PersonUtils.getFactory().createPerson(11, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED,PersonRole.SINGLE, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person1Child1);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person1Child1, household1);
//        person1Child1.setRole(PersonRole.CHILD);
//        Person person1Child2 = PersonUtils.getFactory().createPerson(12, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, PersonRole.SINGLE, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person1Child2);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person1Child2, household1);
//        person1Child2.setRole(PersonRole.CHILD);
//        Person person1Child3 = PersonUtils.getFactory().createPerson(13, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, PersonRole.SINGLE, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person1Child3);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person1Child3, household1);
//        person1Child3.setRole(PersonRole.CHILD);
//
//        dataContainer.getRealEstateDataManager().identifyVacantDwellings();
//
//    }
//
//    @Test
//    public void testDeathOfParent() {
//        dataContainer.getHouseholdDataManager().endYear(42);
//        final RealEstateDataManagerImpl realEstateData = dataContainer.getRealEstateDataManager();
//        final int[] listOfVacantDwellingsInRegion = realEstateData.getListOfVacantDwellingsInRegion(
//                dataContainer.getGeoData().getZones().get(1).getRegion().getId());
//        Assert.assertEquals(0,Arrays.stream(listOfVacantDwellingsInRegion).filter(value -> value == 1).count());
//        Assert.assertEquals(1, realEstateData.getDwelling(1).getResidentId());
//        model.die(dataContainer.getHouseholdDataManager().getPersonFromId(1));
//        Assert.assertNull(dataContainer.getHouseholdDataManager().getHouseholdFromId(1));
//        Assert.assertNull(dataContainer.getHouseholdDataManager().getPersonFromId(1));
//        Assert.assertNull(dataContainer.getHouseholdDataManager().getPersonFromId(11));
//        Assert.assertNull(dataContainer.getHouseholdDataManager().getPersonFromId(12));
//        Assert.assertNull(dataContainer.getHouseholdDataManager().getPersonFromId(13));
//        Assert.assertEquals(-1, realEstateData.getDwelling(1).getResidentId());
//        final int[] listOfVacantDwellingsInRegionAfter = realEstateData.getListOfVacantDwellingsInRegion(
//                dataContainer.getGeoData().getZones().get(1).getRegion().getId());
//        Assert.assertEquals(1,Arrays.stream(listOfVacantDwellingsInRegionAfter).filter(value -> value == 1).count());
//        Assert.assertEquals(false, dataContainer.getHouseholdDataManager().getHouseholdMementos().contains(household1));
//    }
//}
