package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.models.AbstractModel;

/**
 * Simulates finding a new job and quitting a job
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 1 March 2013 in Santa Fe
 **/

public class EmploymentModel extends AbstractModel {
    private final Accessibility accessibility;

    public EmploymentModel(SiloDataContainer dataContainer, Accessibility accessibility) {
        super(dataContainer);
        this.accessibility = accessibility;
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

    Job findJob(Person pp) {
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
        EventManager.countEvent(EventTypes.FIND_NEW_JOB);
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
        EventManager.countEvent(EventTypes.QUIT_JOB);
        dataContainer.getHouseholdData().addHouseholdThatChanged(person.getHh());
        if (perId == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + perId + " quit her/his job.");
        }
    }
}
