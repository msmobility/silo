//package de.tum.bgu.msm.models.demography;
//
//
//import de.tum.bgu.msm.Implementation;
//import de.tum.bgu.msm.container.DataContainerImpl;
//import de.tum.bgu.msm.container.ModelContainerImpl;
//import de.tum.bgu.msm.models.demography.employment.EmploymentModelImpl;
//import de.tum.bgu.msm.utils.SiloUtil;
//import de.tum.bgu.msm.data.household.Household;
//import de.tum.bgu.msm.data.household.HouseholdUtil;
//import de.tum.bgu.msm.data.job.Job;
//import de.tum.bgu.msm.data.job.JobUtils;
//import de.tum.bgu.msm.data.person.*;
//import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
//import de.tum.bgu.msm.properties.Properties;
//import de.tum.bgu.msm.utils.TravelTimeUtil;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//public class EmploymentModelTest {
//
//    private static EmploymentModelImpl model;
//
//    private static DataContainerImpl dataContainer;
//    private static ModelContainer modelContainer;
//
//    @BeforeClass
//    public static void setupModel() {
//        Properties properties = SiloUtil.siloInitialization(Implementation.MARYLAND, "./test/scenarios/annapolis/javaFiles/siloMstm.properties");
//        dataContainer = DataContainerImpl.loadSiloDataContainer(Properties.get());
//        dataContainer.getHouseholdDataManager().calculateInitialSettings();
//        dataContainer.getJobDataManager().identifyVacantJobs();
//        modelContainer = ModelContainerImpl.createSiloModelContainer(dataContainer, null, properties);
//        model = modelContainer.getEmployment();
//    }
//
//    @Before
//    public void setupMicroData() {
//        Household household1 = HouseholdUtil.getFactory().createHousehold(1,-1,0);
//        dataContainer.getHouseholdDataManager().addHousehold(household1);
//        Person person1 = PersonUtils.getFactory().createPerson(1, 24, Gender.MALE, Race.other, Occupation.UNEMPLOYED, PersonRole.SINGLE, -1, 0);
//        dataContainer.getHouseholdDataManager().addPerson(person1);
//        dataContainer.getHouseholdDataManager().addPersonToHousehold(person1, household1);
//        person1.setRole(PersonRole.SINGLE);
//        person1.setIncome(0);
//        TravelTimeUtil.updateCarSkim((SkimTravelTimes) dataContainer.getTravelTimes(), 2000, Properties.get());
//        modelContainer.getAcc().initialize();
//    }
//
//    @Test
//    public void testTakeJob() {
//        dataContainer.getHouseholdDataManager().clearUpdatedHouseholds();
//        final Person person = dataContainer.getHouseholdDataManager().getPersonFromId(1);
//        Assert.assertEquals(-1, person.getJobId());
//        Assert.assertEquals(Occupation.UNEMPLOYED, person.getOccupation());
//        Assert.assertEquals(0, person.getIncome());
//
//        Job job = JobUtils.getFactory().createJob(1, -1, null, -1, "dummy");
//        dataContainer.getJobDataManager().addJob(job);
//        model.takeNewJob(person, job);
//        Assert.assertEquals(1, person.getJobId());
//        Assert.assertEquals(1, job.getWorkerId());
//        Assert.assertEquals(Occupation.EMPLOYED, person.getOccupation());
//        Assert.assertEquals(2320, person.getIncome());
//        Assert.assertTrue(dataContainer.getHouseholdDataManager().getHouseholdMementos().containsKey(1));
//    }
//
//    @Test
//    public void testTakeAndQuitJob() {
//        dataContainer.getHouseholdDataManager().clearUpdatedHouseholds();
//        final Person person = dataContainer.getHouseholdDataManager().getPersonFromId(1);
//        Assert.assertEquals(-1, person.getJobId());
//        Assert.assertEquals(Occupation.UNEMPLOYED, person.getOccupation());
//        Assert.assertEquals(0, person.getIncome());
//
//        final Job job = JobUtils.getFactory().createJob(2, 1,null, -1, "dummy");
//        dataContainer.getJobDataManager().addJob(job);
//        model.takeNewJob(person, job);
//
//        int income = person.getIncome();
//        model.quitJob(1);
//        Assert.assertTrue(dataContainer.getHouseholdDataManager().getHouseholdMementos().containsKey(1));
//        Assert.assertEquals(-1, job.getWorkerId());
//        Assert.assertEquals(Occupation.UNEMPLOYED, person.getOccupation());
//        Assert.assertEquals((int)(income * 0.6 + 0.5), person.getIncome());
//    }
//}
