package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Accessibility;
import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.Job;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.events.*;
import de.tum.bgu.msm.models.AbstractModel;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simulates finding a new job and quitting a job
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 1 March 2013 in Santa Fe
 **/

public class EmploymentModel extends AbstractModel implements EventHandler, EventCreator{

    private final static Logger LOGGER = Logger.getLogger(EmploymentModel.class);

    private final Accessibility accessibility;
    private float[][] laborParticipationShares;

    public EmploymentModel(SiloDataContainer dataContainer, Accessibility accessibility) {
        super(dataContainer);
        this.accessibility = accessibility;
        calculateInitialLaborParticipation();
    }

    public void lookForJob(int perId) {
        final Person pp = dataContainer.getHouseholdData().getPersonFromId(perId);
        if (pp == null) {
            return;  // person has died or moved away
        }

        final Job jj = findJob(pp);
        if (jj != null) {
            takeNewJob(pp, jj);
        } else {
            IssueCounter.countMissingJob();
        }
    }

    private Job findJob(Person pp) {
        final Dwelling dwelling = dataContainer.getRealEstateData().getDwelling(pp.getHh().getDwellingId());
        int zoneId = -1;
        if (dwelling != null) {
            zoneId = dwelling.getZone();
        }
        final int idVacantJob = dataContainer.getJobData().findVacantJob(zoneId, dataContainer.getGeoData().getRegions().keySet(),
                accessibility);
        return dataContainer.getJobData().getJobFromId(idVacantJob);
    }

    void takeNewJob(Person person, Job job) {
        job.setWorkerID(person.getId());
        person.setWorkplace(job.getId());
        person.setOccupation(1);
        dataContainer.getHouseholdData().selectIncomeForPerson(person);
        EventManager.countEvent(EventType.FIND_NEW_JOB);
        dataContainer.getHouseholdData().addHouseholdThatChanged(person.getHh());
        if (person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + person.getId() + " started working for job " + job.getId());
        }
    }

    public void quitJob(int perId) {
        final Person person = dataContainer.getHouseholdData().getPersonFromId(perId);
        if (person == null) {
            return;  // person has died or moved away
        }
        dataContainer.getJobData().quitJob(true, person);
        EventManager.countEvent(EventType.QUIT_JOB);
        dataContainer.getHouseholdData().addHouseholdThatChanged(person.getHh());
        if (perId == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + perId + " quit her/his job.");
        }
    }

    @Override
    public void handleEvent(Event event) {
        switch(event.getType()) {
            case FIND_NEW_JOB:
                lookForJob(event.getId());
                break;
            case QUIT_JOB:
                quitJob(event.getId());
                break;
        }
    }

    @Override
    public Collection<Event> createEvents(int year) {
        final List<Event> events = new ArrayList<>();

        // select people that will lose employment or start new job
        LOGGER.info("  Planning job changes (hire and fire) for the year " + year);

        // count currently employed people
        final float[][] currentlyEmployed = new float[2][100];
        final float[][] currentlyUnemployed = new float[2][100];
        for (Person pp : dataContainer.getHouseholdData().getPersons()) {
            int age = pp.getAge();
            if (age > 99) continue;  // people older than 99 will always be unemployed/retired
            int gender = pp.getGender();
            boolean employed = pp.getWorkplace() > 0;
            if (employed) {
                currentlyEmployed[gender - 1][age]++;
            } else {
                currentlyUnemployed[gender - 1][age]++;
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

        for (Person pp : dataContainer.getHouseholdData().getPersons()) {
            int age = pp.getAge();
            if (age > 99) {
                continue;  // people older than 99 will always be unemployed/retired
            }
            int gen = pp.getGender() - 1;
            boolean employed = pp.getWorkplace() > 0;

            // find job
            if (changeRate[gen][age] > 0 && !employed) {
                if (SiloUtil.getRandomNumberAsFloat() < changeRate[gen][age]) {
                    events.add(new EventImpl(EventType.FIND_NEW_JOB, pp.getId(), year));
                }
            }
            // lose job
            if (changeRate[gen][age] < 0 && employed) {
                if (SiloUtil.getRandomNumberAsFloat() < Math.abs(changeRate[gen][age])) {
                    events.add(new EventImpl(EventType.QUIT_JOB, pp.getId(), year));
                }
            }
        }

        return events;
    }


    private void calculateInitialLaborParticipation() {
        // calculate share of people employed by age and gender

        laborParticipationShares = new float[2][100];
        int[][] count = new int[2][100];
        for (Person pp: dataContainer.getHouseholdData().getPersons()) {
            int age = pp.getAge();
            if (age > 99) {
                continue;  // people older than 99 will always be unemployed/retired
            }
            int gender = pp.getGender();
            boolean employed = pp.getWorkplace() > 0;
            if (employed) laborParticipationShares[gender-1][age]++;
            count[gender-1][age]++;
        }
        // calculate shares
        for (int gen = 0; gen <=1; gen++) {
            for (int age = 0; age < 100; age++) {
                if (count[gen][age] > 0) laborParticipationShares[gen][age] = laborParticipationShares[gen][age] / (1f * count[gen][age]);
            }

            // smooth out shares
            for (int age = 18; age < 98; age++) {
                laborParticipationShares[gen][age] = (laborParticipationShares[gen][age-2]/4f +
                        laborParticipationShares[gen][age-1]/2f + laborParticipationShares[gen][age] +
                        laborParticipationShares[gen][age+1]/2f + laborParticipationShares[gen][age+2]/4f) / 2.5f;
            }
        }
    }
}
