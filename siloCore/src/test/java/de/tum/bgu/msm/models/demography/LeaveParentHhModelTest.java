//package de.tum.bgu.msm.models.demography;
//
//import de.tum.bgu.msm.Implementation;
//import de.tum.bgu.msm.container.DataContainerImpl;
//import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypeImpl;
//import de.tum.bgu.msm.data.dwelling.Dwelling;
//import de.tum.bgu.msm.data.dwelling.DwellingUtils;
//import de.tum.bgu.msm.data.household.Household;
//import de.tum.bgu.msm.data.household.HouseholdUtil;
//import de.tum.bgu.msm.data.person.*;
//import de.tum.bgu.msm.models.autoOwnership.munich.CreateCarOwnershipModel;
//import de.tum.bgu.msm.models.demography.leaveParentalHousehold.LeaveParentHhModelImpl;
//import de.tum.bgu.msm.models.relocation.moves.MovesModel;
//import de.tum.bgu.msm.models.relocation.mstm.MovesModelImplMstm;
//import de.tum.bgu.msm.properties.Properties;
//import de.tum.bgu.msm.utils.SiloUtil;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Ignore;
//import org.junit.Test;
//
//public class LeaveParentHhModelTest {
//
//    private static LeaveParentHhModelImpl model;
//    private static DataContainerImpl dataContainer;
//    private static Person person;
//    private static Household household;
//
//    @BeforeClass
//    public static void setupModel() {
//        Properties properties = SiloUtil.siloInitialization(Implementation.MARYLAND, "./test/scenarios/annapolis/javaFiles/siloMstm.properties");
//        dataContainer = DataContainerImpl.loadSiloDataContainer(Properties.get());
//        MovesModel moves = new MovesModelImplMstm(dataContainer, properties);
//        CreateCarOwnershipModel carOwnership = new CreateCarOwnershipModel(dataContainer);
//        model = new LeaveParentHhModelImpl(dataContainer, moves, carOwnership, HouseholdUtil.getFactory(), properties);
//
//        Dwelling dd = DwellingUtils.getFactory()
//                .createDwelling(999, 99, null, -1, DefaultDwellingTypeImpl.SFD, 1, 1, 0, 1, 1999);
//        dataContainer.getRealEstateDataManager().addDwelling(dd);
//
//        household = HouseholdUtil.getFactory().createHousehold(999, 1, 0);
//        dataContainer.getHouseholdDataManager().addHousehold(household);
//        Person parent1 = PersonUtils.getFactory().createPerson(123, 40, Gender.MALE, Race.other, Occupation.EMPLOYED, PersonRole.SINGLE, 0, 0);
//        Person parent2 = PersonUtils.getFactory().createPerson(456, 40, Gender.FEMALE, Race.other, Occupation.EMPLOYED, PersonRole.SINGLE, 0, 0);
//        dataContainer.getHouseholdDataManager().addPerson(parent1);
//        dataContainer.getHouseholdDataManager().addPerson(parent2);
//
//        person = PersonUtils.getFactory().createPerson(0, 20, Gender.FEMALE, Race.other, Occupation.STUDENT, PersonRole.SINGLE, 0, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person);
//        person.setRole(PersonRole.CHILD);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person, household);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(parent1, household);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(parent2, household);
//
//        dataContainer.getRealEstateDataManager().addDwellingToVacancyList(dd);
//        dataContainer.getRealEstateDataManager().setup();
//        dataContainer.getHouseholdDataManager().setup();
//    }
//
//    @Ignore
//    @Test
//    public void testLeaveParents() {
//        //TODO: revive this test once it's easier to setup moves model
//        model.leaveHousehold(person);
//        Assert.assertEquals(2, dataContainer.getHouseholdDataManager().getHouseholdFromId(999).getHhSize());
//        Household household = person.getHousehold();
//        Assert.assertEquals(999, household.getDwellingId());
//        Assert.assertEquals(PersonRole.SINGLE, person.getRole());
//    }
//}
