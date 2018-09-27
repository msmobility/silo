package de.tum.bgu.msm.models.demography;


import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.Occupation;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobUtils;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SkimUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmploymentModelTest {

    private static EmploymentModel model;

    private static SiloDataContainer dataContainer;
    private static SiloModelContainer modelContainer;

    @BeforeClass
    public static void setupModel() {
        Properties properties = SiloUtil.siloInitialization("./test/scenarios/annapolis/javaFiles/siloMstm.properties", Implementation.MARYLAND);
        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
        dataContainer.getHouseholdData().calculateInitialSettings();
        dataContainer.getJobData().identifyVacantJobs();
        modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, null, properties);
        model = modelContainer.getEmployment();
    }

    @Before
    public void setupMicroData() {
        Household household1 = dataContainer.getHouseholdData().createHousehold(1, -1, 0);
        Person person1 = PersonUtils.getFactory().createPerson(1, 30, Gender.MALE, Race.other, Occupation.UNEMPLOYED, -1, 0);
        dataContainer.getHouseholdData().addPerson(person1);
        dataContainer.getHouseholdData().addPersonToHousehold(person1, household1);
        person1.setRole(PersonRole.SINGLE);
        SkimUtil.updateCarSkim((SkimTravelTimes) dataContainer.getTravelTimes(), 2000, Properties.get());
        modelContainer.getAcc().initialize();
    }

    @Test
    public void testTakeJob() {
        dataContainer.getHouseholdData().clearUpdatedHouseholds();
        final Person person = dataContainer.getHouseholdData().getPersonFromId(1);
        Assert.assertEquals(-1, person.getWorkplace());
        Assert.assertEquals(Occupation.UNEMPLOYED, person.getOccupation());
        Assert.assertEquals(0, person.getIncome());

        Job job = JobUtils.getFactory().createJob(1, -1, null, -1, "dummy");
        dataContainer.getJobData().addJob(job);
        model.takeNewJob(person, job);
        Assert.assertEquals(1, person.getWorkplace());
        Assert.assertEquals(1, job.getWorkerId());
        Assert.assertEquals(Occupation.EMPLOYED, person.getOccupation());
        Assert.assertEquals(1022, person.getIncome());
        Assert.assertTrue(dataContainer.getHouseholdData().getUpdatedHouseholds().containsKey(1));
    }

    @Test
    public void testTakeAndQuitJob() {
        dataContainer.getHouseholdData().clearUpdatedHouseholds();
        final Person person = dataContainer.getHouseholdData().getPersonFromId(1);
        Assert.assertEquals(-1, person.getWorkplace());
        Assert.assertEquals(Occupation.UNEMPLOYED, person.getOccupation());
        Assert.assertEquals(0, person.getIncome());

        final Job job = JobUtils.getFactory().createJob(2, 1,null, -1, "dummy");
        dataContainer.getJobData().addJob(job);
        model.takeNewJob(person, job);

        int income = person.getIncome();
        model.quitJob(1);
        Assert.assertTrue(dataContainer.getHouseholdData().getUpdatedHouseholds().containsKey(1));
        Assert.assertEquals(-1, job.getWorkerId());
        Assert.assertEquals(Occupation.UNEMPLOYED, person.getOccupation());
        Assert.assertEquals((int)(income * 0.6 + 0.5), person.getIncome());
    }
}
