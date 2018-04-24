package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.properties.Properties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmploymentModelTest {

    private static EmploymentModel model;

    private static SiloModelContainer modelContainer;
    private static SiloDataContainer dataContainer;

    @BeforeClass
    public static void setupModel() {
        SiloUtil.siloInitialization("./test/scenarios/annapolis/javaFiles/siloMstm.properties", Implementation.MARYLAND);
        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
        dataContainer.getHouseholdData().calculateInitialSettings();
        dataContainer.getJobData().identifyVacantJobs();
        modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, null);
        model = modelContainer.getEmployment();
    }

    @Before
    public void setupMicroData() {
        Household household1 = dataContainer.getHouseholdData().createHousehold(1, 1, 0);
        dataContainer.getRealEstateData().createDwelling(1, 1, 1, DwellingType.MF234, 4, 1, 1000, -1, 2000);
        Person person1 = dataContainer.getHouseholdData().createPerson(1, 30, 1, Race.other, -1, -1, 0);
        dataContainer.getHouseholdData().addPersonToHousehold(person1, household1);
        person1.setRole(PersonRole.SINGLE);
    }

    @Test
    public void testNewJob() {
        model.lookForJob(1);
        final Person person = dataContainer.getHouseholdData().getPersonFromId(1);
        final int job = person.getWorkplace();
        Assert.assertEquals(-1, job);
    }

    @Test
    public void testFindJob() {
        final Person person = dataContainer.getHouseholdData().getPersonFromId(1);
        final Job job = model.findJob(person);
        Assert.assertNull(job);
    }

    @Test
    public void testTakeJob() {
        dataContainer.getHouseholdData().clearUpdatedHouseholds();
        final Person person = dataContainer.getHouseholdData().getPersonFromId(1);
        Assert.assertEquals(-1, person.getWorkplace());
        Assert.assertEquals(-1, person.getOccupation());
        Assert.assertEquals(0, person.getIncome());

        final Job job = dataContainer.getJobData().createJob(1, 1, -1, "dummy");
        model.takeNewJob(person, job);
        Assert.assertEquals(1, person.getWorkplace());
        Assert.assertEquals(1, job.getWorkerId());
        Assert.assertEquals(1, person.getOccupation());
        Assert.assertEquals(1022, person.getIncome());
        Assert.assertTrue(dataContainer.getHouseholdData().getUpdatedHouseholds().containsKey(1));
    }

    @Test
    public void testTakeAndQuitJob() {
        dataContainer.getHouseholdData().clearUpdatedHouseholds();
        final Person person = dataContainer.getHouseholdData().getPersonFromId(1);
        Assert.assertEquals(-1, person.getWorkplace());
        Assert.assertEquals(-1, person.getOccupation());
        Assert.assertEquals(0, person.getIncome());

        final Job job = dataContainer.getJobData().createJob(2, 1, -1, "dummy");
        model.takeNewJob(person, job);

        int income = person.getIncome();
        model.quitJob(1);
        Assert.assertTrue(dataContainer.getHouseholdData().getUpdatedHouseholds().containsKey(1));
        Assert.assertEquals(-1, job.getWorkerId());
        Assert.assertEquals(2, person.getOccupation());
        Assert.assertEquals((int)(income * 0.6 + 0.5), person.getIncome());
    }
}
