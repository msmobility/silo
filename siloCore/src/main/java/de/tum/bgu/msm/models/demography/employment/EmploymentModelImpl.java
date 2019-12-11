package de.tum.bgu.msm.models.demography.employment;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.events.impls.person.EmploymentEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Simulates finding a new job and quitting a job
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 1 March 2013 in Santa Fe
 **/
public class EmploymentModelImpl extends AbstractModel implements EmploymentModel {

    private final static Logger logger = Logger.getLogger(EmploymentModelImpl.class);

    private float[][] laborParticipationShares;
    private int missingJob;

    public EmploymentModelImpl(DataContainer dataContainer, Properties properties, Random rnd) {
        super(dataContainer, properties, rnd);
    }

    @Override
    public Collection<EmploymentEvent> getEventsForCurrentYear(int year) {
        final List<EmploymentEvent> events = new ArrayList<>();

        // select people that will lose employment or start new job
        logger.info("  Planning job changes (hire and fire) for the year " + year);

        // count currently employed people
        final float[][] currentlyEmployed = new float[2][100];
        final float[][] currentlyUnemployed = new float[2][100];
        for (Person pp : dataContainer.getHouseholdDataManager().getPersons()) {
            int age = pp.getAge();
            if (age > 99) {
                // people older than 99 will always be unemployed/retired
                continue;
            }
            Gender gender = pp.getGender();
            boolean employed = pp.getJobId() > 0;
            if (employed) {
                currentlyEmployed[gender.ordinal()][age]++;
            } else {
                currentlyUnemployed[gender.ordinal()][age]++;
            }
        }

        // calculate change rates
        float[][] changeRate = new float[2][100];
        for (int gen = 0; gen <= 1; gen++) {
            for (int age = 0; age < 100; age++) {
                float change = laborParticipationShares[gen][age] *
                        (currentlyEmployed[gen][age] + currentlyUnemployed[gen][age]) - currentlyEmployed[gen][age];
                if (change > 0) {
                    // probability to find job
                    changeRate[gen][age] = (change / (1f * currentlyUnemployed[gen][age]));
                } else {
                    // probability to lose job
                    changeRate[gen][age] = (change / (1f * currentlyEmployed[gen][age]));
                }
            }
        }

        for (Person pp : dataContainer.getHouseholdDataManager().getPersons()) {
            int age = pp.getAge();
            if (age > 99) {
                continue;  // people older than 99 will always be unemployed/retired
            }
            int gen = pp.getGender().ordinal();
            boolean employed = pp.getJobId() > 0;

            // find job
            if (changeRate[gen][age] > 0 && !employed) {
                if (random.nextDouble() < changeRate[gen][age]) {
                    events.add(new EmploymentEvent(pp.getId(), EmploymentEvent.Type.FIND));
                }
            }
            // lose job
            if (changeRate[gen][age] < 0 && employed) {
                if (random.nextDouble() < Math.abs(changeRate[gen][age])) {
                    events.add(new EmploymentEvent(pp.getId(), EmploymentEvent.Type.QUIT));
                }
            }
        }
        return events;
    }

    @Override
    public boolean handleEvent(EmploymentEvent event) {
        switch(event.getType()) {
            case FIND:
                return lookForJob(event.getPersonId());
            case QUIT:
                return quitJob(event.getPersonId());
            default:
                throw new RuntimeException("Unknown employment event type: " + event.getType());
        }
    }

    @Override
    public void endYear(int year) {
        if (missingJob > 0) {
            logger.warn("  Encountered " + missingJob + " cases where a person should have started a " +
                    "new job to keep constant labor participation rates but could not find a job.");
        }
    }

    @Override
    public void endSimulation() {

    }

    @Override
    public void setup() {
        // calculate share of people employed by age and gender

        laborParticipationShares = new float[2][100];
        int[][] count = new int[2][100];
        for (Person pp: dataContainer.getHouseholdDataManager().getPersons()) {
            int age = pp.getAge();
            if (age > 99) {
                // people older than 99 will always be unemployed/retired
                continue;
            }
            int gender = pp.getGender().ordinal();
            boolean employed = pp.getJobId() > 0;
            if (employed) {
                laborParticipationShares[gender][age]++;
            }
            count[gender][age]++;
        }
        // calculate shares
        for (int gen = 0; gen <=1; gen++) {
            for (int age = 0; age < 100; age++) {
                if (count[gen][age] > 0) {
                    laborParticipationShares[gen][age] = laborParticipationShares[gen][age] / (1f * count[gen][age]);
                }
            }

            // smooth out shares
            for (int age = 18; age < 98; age++) {
                laborParticipationShares[gen][age] = (laborParticipationShares[gen][age-2]/4f +
                        laborParticipationShares[gen][age-1]/2f + laborParticipationShares[gen][age] +
                        laborParticipationShares[gen][age+1]/2f + laborParticipationShares[gen][age+2]/4f) / 2.5f;
            }
        }
    }

    @Override
    public void prepareYear(int year) {
        missingJob = 0;
    }

    @Override
    public boolean lookForJob(int perId) {
        final Person pp = dataContainer.getHouseholdDataManager().getPersonFromId(perId);
        if (pp != null) {
            final Job jj = findJob(pp);
            if (jj != null) {
                return takeNewJob(pp, jj);
            } else {
                missingJob++;
            }
        }
        return false;
    }

    private Job findJob(Person pp) {
        final Household household = pp.getHousehold();
        final Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(household.getDwellingId());
        Zone zone = null;
        if (dwelling != null) {
            zone = dataContainer.getGeoData().getZones().get(dwelling.getZoneId());
        }
        return dataContainer.getJobDataManager().findVacantJob(
                zone, dataContainer.getGeoData().getRegions().values());
    }

    boolean takeNewJob(Person person, Job job) {
        Household household = person.getHousehold();
        dataContainer.getHouseholdDataManager().saveHouseholdMemento(household);
        job.setWorkerID(person.getId());
        person.setWorkplace(job.getId());
        person.setOccupation(Occupation.EMPLOYED);


        final Gender gender = person.getGender();
        final int age = Math.min(99, person.getAge());
        final float meanIncomeChange = properties.householdData.meanIncomeChange;
        final double[] prob = new double[21];
        final int[] change = new int[21];
        for (int i = 0; i < prob.length; i++) {
            // normal distribution to calculate change of income
            //TODO: Use normal distribution from library (e.g. commons math)
            change[i] = (int) (-5000f + 10000f * (float) i / (prob.length - 1f));
            prob[i] = (1 / (meanIncomeChange * Math.sqrt(2 * 3.1416))) *
                    Math.exp(-(Math.pow(change[i], 2) / (2 * Math.pow(meanIncomeChange, 2))));
        }
        final int sel = SiloUtil.select(prob, random);
        float avgIncome = dataContainer.getHouseholdDataManager().getAverageIncome(gender, age, person.getOccupation());
        final int inc = Math.max((int) avgIncome + change[sel], 0);
        person.setIncome(inc);


        if (person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + person.getId() + " started working for job " + job.getId());
        }
        return true;
    }

    @Override
    public boolean quitJob(int perId) {
        final Person person = dataContainer.getHouseholdDataManager().getPersonFromId(perId);
        if (person != null) {
            Household household = person.getHousehold();
            dataContainer.getHouseholdDataManager().saveHouseholdMemento(household);
            dataContainer.getJobDataManager().quitJob(true, person);
            if (perId == SiloUtil.trackPp) {
                SiloUtil.trackWriter.println("Person " + perId + " quit her/his job.");
            }
            return true;
        }
        return false;
    }
}
