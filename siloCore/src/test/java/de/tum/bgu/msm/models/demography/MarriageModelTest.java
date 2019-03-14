//package de.tum.bgu.msm.models.demography;
//
//import de.tum.bgu.msm.Implementation;
//import de.tum.bgu.msm.container.DataContainerImpl;
//import de.tum.bgu.msm.data.household.HouseholdDataManager;
//import de.tum.bgu.msm.data.household.HouseholdDataManagerImpl;
//import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypeImpl;
//import de.tum.bgu.msm.data.dwelling.DwellingUtils;
//import de.tum.bgu.msm.data.household.Household;
//import de.tum.bgu.msm.data.household.HouseholdUtil;
//import de.tum.bgu.msm.data.person.*;
//import de.tum.bgu.msm.events.impls.MarriageEvent;
//import de.tum.bgu.msm.models.autoOwnership.munich.CreateCarOwnershipModel;
//import de.tum.bgu.msm.models.demography.driversLicense.DriversLicenseModelImpl;
//import de.tum.bgu.msm.models.demography.employment.EmploymentModel;
//import de.tum.bgu.msm.models.demography.employment.EmploymentModelImpl;
//import de.tum.bgu.msm.models.demography.marriage.MarriageModelImpl;
//import de.tum.bgu.msm.models.relocation.migration.InOutMigrationImpl;
//import de.tum.bgu.msm.models.relocation.moves.MovesModel;
//import de.tum.bgu.msm.models.relocation.mstm.MovesModelImplMstm;
//import de.tum.bgu.msm.properties.Properties;
//import de.tum.bgu.msm.utils.SiloUtil;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import java.util.*;
//
//public class MarriageModelTest {
//
//    private static MarriageModelImpl model;
//    private static int[] couple1;
//    private static int[] couple2;
//    private static int[] couple3;
//    private static int[] couple4;
//
//    private static DataContainerImpl dataContainer;
//    private static HouseholdDataManager singleHouseholds;
//
//    @BeforeClass
//    public static void setupModel() {
//        Properties properties = SiloUtil.siloInitialization(Implementation.MARYLAND, "./test/scenarios/annapolis/javaFiles/siloMstm.properties");
//
//        dataContainer = DataContainerImpl.loadSiloDataContainer(Properties.get());
//        MovesModel moves = new MovesModelImplMstm(dataContainer, properties);
//        CreateCarOwnershipModel carOwnership = new CreateCarOwnershipModel(dataContainer);
//        EmploymentModel employment = new EmploymentModelImpl(dataContainer, properties);
//        DriversLicenseModelImpl driversLicense = new DriversLicenseModelImpl(dataContainer, properties);
//        InOutMigrationImpl iomig = new InOutMigrationImpl(dataContainer, employment, moves, carOwnership, driversLicense, properties);
//        model = new MarriageModelImpl(dataContainer, moves, iomig, carOwnership, HouseholdUtil.getFactory(), properties);
//
//        couple1 = new int[]{1,2};
//        Household household1 = HouseholdUtil.getFactory().createHousehold(1,  1, 0);
//        dataContainer.getHouseholdDataManager().addHousehold(household1);
//        dataContainer.getRealEstateDataManager().addDwelling(DwellingUtils.getFactory().createDwelling(1, 999, null, 1, DefaultDwellingTypeImpl.MF234, 2, 1, 1000, -1, 2000));
//        Person person1 = PersonUtils.getFactory().createPerson(1, 30, Gender.MALE, Race.other, Occupation.UNEMPLOYED, PersonRole.SINGLE, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person1);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person1, household1);
//
//        Household household2 = HouseholdUtil.getFactory().createHousehold(2, 2, 0);
//        dataContainer.getHouseholdDataManager().addHousehold(household2);
//        dataContainer.getRealEstateDataManager().addDwelling(DwellingUtils.getFactory().createDwelling(2, 999, null, 2, DefaultDwellingTypeImpl.SFD, 4, 1, 1000, -1, 2000));
//        Person person2 = PersonUtils.getFactory().createPerson(2, 30, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, PersonRole.SINGLE, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person2);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person2, household2);
//
//        couple2 = new int[]{3,4};
//        Household household3 = HouseholdUtil.getFactory().createHousehold(3, 3,  0);
//        dataContainer.getHouseholdDataManager().addHousehold(household3);
//        dataContainer.getRealEstateDataManager().addDwelling(DwellingUtils.getFactory().createDwelling(3, 999, null, 3, DefaultDwellingTypeImpl.MF234, 4, 1, 1000, -1, 2000));
//        Person person3 = PersonUtils.getFactory().createPerson(3, 30, Gender.MALE, Race.other, Occupation.UNEMPLOYED, PersonRole.SINGLE, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person3);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person3, household3);
//
//        Household household4 = HouseholdUtil.getFactory().createHousehold(4, 4,  0);
//        dataContainer.getHouseholdDataManager().addHousehold(household4);
//        dataContainer.getRealEstateDataManager().addDwelling(DwellingUtils.getFactory().createDwelling(4, 999, null, 4, DefaultDwellingTypeImpl.SFD, 2, 1, 1000, -1, 2000));
//        Person person4 = PersonUtils.getFactory().createPerson(4, 30, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, PersonRole.SINGLE, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person4);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person4, household4);
//
//        couple3 = new int[]{5,6};
//        Household household5 = HouseholdUtil.getFactory().createHousehold(5, 5,  0);
//        dataContainer.getHouseholdDataManager().addHousehold(household5);
//        dataContainer.getRealEstateDataManager().addDwelling(DwellingUtils.getFactory().createDwelling(5, 999, null, 5, DefaultDwellingTypeImpl.MF234, 4, 1, 1000, -1, 2000));
//        Person person5 = PersonUtils.getFactory().createPerson(5, 30, Gender.MALE, Race.other, Occupation.UNEMPLOYED, PersonRole.SINGLE, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person5);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person5, household5);
//
//        Household household6 = HouseholdUtil.getFactory().createHousehold(6, 6,  0);
//        dataContainer.getHouseholdDataManager().addHousehold(household6);
//        dataContainer.getRealEstateDataManager().addDwelling(DwellingUtils.getFactory().createDwelling(6, 999, null, 6, DefaultDwellingTypeImpl.SFD, 2, 1, 1000, -1, 2000));
//        Person person6 = PersonUtils.getFactory().createPerson(6, 30, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, PersonRole.SINGLE, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person6);
//
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person6, household6);
//        Person person6Child = PersonUtils.getFactory().createPerson(61, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, PersonRole.SINGLE, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person6Child);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person6Child, household6);
//
//        couple4 = new int[]{7,8};
//        Household household7 = HouseholdUtil.getFactory().createHousehold(7, 7,  0);
//        dataContainer.getHouseholdDataManager().addHousehold(household7);
//        dataContainer.getRealEstateDataManager().addDwelling(DwellingUtils.getFactory().createDwelling(7, 999, null, 7, DefaultDwellingTypeImpl.MF234, 4, 1, 1000, -1, 2000));
//        Person person7 = PersonUtils.getFactory().createPerson(7, 30, Gender.MALE, Race.other, Occupation.UNEMPLOYED, PersonRole.SINGLE, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person7);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person7, household7);
//
//        Person person7Child1 = PersonUtils.getFactory().createPerson(71, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, PersonRole.CHILD, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person7Child1);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person7Child1, household7);
//        Person person7Child2 = PersonUtils.getFactory().createPerson(72, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, PersonRole.CHILD, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person7Child2);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person7Child2, household7);
//        Person person7Child3 = PersonUtils.getFactory().createPerson(73, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, PersonRole.CHILD, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person7Child3);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person7Child3, household7);
//
//        Household household8 = HouseholdUtil.getFactory().createHousehold(8, 8,  0);
//        dataContainer.getHouseholdDataManager().addHousehold(household8);
//        dataContainer.getRealEstateDataManager().addDwelling(DwellingUtils.getFactory().createDwelling(8, 999, null, 8, DefaultDwellingTypeImpl.SFD, 2, 1, 1000, -1, 2000));
//        Person person8 = PersonUtils.getFactory().createPerson(8, 30, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED,PersonRole.SINGLE, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person8);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person8, household8);
//
//        Person person8Child1 = PersonUtils.getFactory().createPerson(81, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED,PersonRole.SINGLE, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person8Child1);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person8Child1, household8);
//        Person person8Child2 = PersonUtils.getFactory().createPerson(82, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, PersonRole.CHILD, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person8Child2);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person8Child2, household8);
//        Person person8Child3 = PersonUtils.getFactory().createPerson(83, 10, Gender.FEMALE, Race.other, Occupation.UNEMPLOYED, PersonRole.CHILD, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person8Child3);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person8Child3, household8);
//
//        dataContainer.getRealEstateDataManager().setup();
//
//        Random rnd = new Random(42);
//        singleHouseholds = new HouseholdDataManagerImpl(dataContainer, PersonUtils.getFactory(), HouseholdUtil.getFactory());
//        PrimitiveIterator.OfInt ages = rnd.ints(20, 60).iterator();
//        PrimitiveIterator.OfInt genders = rnd.ints(1,3).iterator();
//        Iterator<Race> races = rnd.ints(0, 4).mapToObj(i -> Race.values()[i]).iterator();
//        PrimitiveIterator.OfInt occupations = rnd.ints(0,5).iterator();
//        for(int i = 0; i < 10000; i++) {
//            final Household household = HouseholdUtil.getFactory().createHousehold(i,i,0);
//                    singleHouseholds.addHousehold(household);
//            final Person p = PersonUtils.getFactory().createPerson(i, ages.nextInt(), Gender.valueOf(genders.nextInt()), races.next(), Occupation.valueOf(occupations.nextInt()), PersonRole.SINGLE,0, 0);
//            singleHouseholds.addPerson(p);
//            singleHouseholds.addPersonToHousehold(p, household);
//        }
//
//    }
//
//    @Test
//    public void testMarriageGroomMovingToBride() {
//        model.handleEvent(new MarriageEvent(couple1[0], couple1[1]));
//        //both persons should be married
//        Assert.assertEquals(PersonRole.MARRIED, dataContainer.getHouseholdDataManager().getPersonFromId(1).getRole());
//        Assert.assertEquals(PersonRole.MARRIED, dataContainer.getHouseholdDataManager().getPersonFromId(2).getRole());
//        //household 1 has disbanded
//        Assert.assertNull(dataContainer.getHouseholdDataManager().getHouseholdFromId(1));
//        //person 1 moved to household 2
//        Assert.assertEquals(2, dataContainer.getHouseholdDataManager().getHouseholdFromId(2).getHhSize());
//    }
//
//    @Test
//    public void testMarriageBrideMovingToGroom() {
//        model.handleEvent(new MarriageEvent(couple2[0], couple2[1]));
//        //both persons should be married
//        Assert.assertEquals(PersonRole.MARRIED, dataContainer.getHouseholdDataManager().getPersonFromId(3).getRole());
//        Assert.assertEquals(PersonRole.MARRIED, dataContainer.getHouseholdDataManager().getPersonFromId(4).getRole());
//        //person 4 moved to household 3
//        Assert.assertEquals(2, dataContainer.getHouseholdDataManager().getHouseholdFromId(3).getHhSize());
//        //household 4 has disbanded
//        Assert.assertNull(dataContainer.getHouseholdDataManager().getHouseholdFromId(4));
//    }
//
//    @Test
//    public void testMarriageBrideMovingToGroomWithChild() {
//        model.handleEvent(new MarriageEvent(couple3[0], couple3[1]));
//        //both persons should be married
//        Assert.assertEquals(PersonRole.MARRIED, dataContainer.getHouseholdDataManager().getPersonFromId(5).getRole());
//        Assert.assertEquals(PersonRole.MARRIED, dataContainer.getHouseholdDataManager().getPersonFromId(6).getRole());
//        //person 6 and 7 moved to household 5
//        Assert.assertEquals(3, dataContainer.getHouseholdDataManager().getHouseholdFromId(5).getHhSize());
//        //household 6 has disbanded
//        Assert.assertNull(dataContainer.getHouseholdDataManager().getHouseholdFromId(6));
//    }
//
//    @Ignore
//    @Test
//    public void testMarriageOutmigratingNewHousehold() {
//        //TODO: revive this test once it's easier to set up the moves model.
////        model.handleEvent(new MarriageEvent(couple4[0], couple4[1]));
////        //new household outmigrated
////        Assert.assertNull(dataContainer.getHouseholdDataManager().getPersonFromId(7));
////        Assert.assertNull(dataContainer.getHouseholdDataManager().getPersonFromId(8));
////        Assert.assertNull(dataContainer.getHouseholdDataManager().getHouseholdFromId(7));
////        Assert.assertNull(dataContainer.getHouseholdDataManager().getHouseholdFromId(8));
//    }
//
//    @Test
//    public void testSelectMarryingCouples() {
//        SiloUtil.getRandomObject().setSeed(42);
//        final List<MarriageEvent> couples = model.selectCouplesToGetMarriedThisYear(singleHouseholds.getPersons());
//        Assert.assertEquals(689, couples.size());
//        final long manMarriesYoungerWoman = couples.stream()
//                .filter(couple -> {
//                            Person partner1 = singleHouseholds.getPersonFromId(couple.getFirstId());
//                            Person partner2 = singleHouseholds.getPersonFromId(couple.getSecondId());
//                    return (partner1.getGender() == Gender.MALE
//                            && partner1.getAge() > partner2.getAge())
//                            || (partner2.getGender() == Gender.MALE
//                            && partner1.getAge() < partner2.getAge());
//                        }
//                ).count();
//
//        Assert.assertEquals(440, manMarriesYoungerWoman);
//        Assert.assertTrue(model.selectCouplesToGetMarriedThisYear(Collections.EMPTY_LIST).isEmpty());
//    }
//}
