package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.IssueCounter;

/**
 * Simulates finding a new job and quitting a job
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 1 March 2013 in Santa Fe
 **/

public class ChangeEmploymentModel {
    private final SiloDataContainer dataContainer;
    private final Accessibility accessibility;

    public ChangeEmploymentModel(SiloDataContainer dataContainer, Accessibility accessibility) {
        this.dataContainer = dataContainer;
        this.accessibility = accessibility;
    }

    public boolean findNewJob (int perId) {
        // find new job for person perId

        Person pp = dataContainer.getHouseholdData().getPersonFromId(perId);
        if (pp == null) return false;  // person has died or moved away

        int sm = 0;
        for (int reg: dataContainer.getGeoData().getRegions().keySet()) {
            sm += JobDataManager.getNumberOfVacantJobsByRegion(reg);
        }
        if (sm == 0) {
            IssueCounter.countMissingJob();
            return false;
        } else {
            Dwelling dwelling = dataContainer.getRealEstateData().getDwelling(pp.getHh().getDwellingId());
            int zoneId = -1;
            if(dwelling != null) {
                zoneId = dwelling.getZone();
            }
            int idVacantJob = JobDataManager.findVacantJob(zoneId, dataContainer.getGeoData().getRegions().keySet(), accessibility);
            if (idVacantJob == -1) {
                IssueCounter.countMissingJob();
                return false;
            }
            Job jj = dataContainer.getJobData().getJobFromId(idVacantJob);
            jj.setWorkerID(perId);
            pp.setWorkplace(jj.getId());
            pp.setOccupation(1);
            int gender = pp.getGender() - 1;
            int age = Math.min(99, pp.getAge());
            int inc = HouseholdDataManager.selectIncomeForPerson(gender, age, 1);
            pp.setIncome(inc);
            EventManager.countEvent(EventTypes.FIND_NEW_JOB);
            dataContainer.getHouseholdData().addHouseholdThatChanged(pp.getHh());
            if (perId == SiloUtil.trackPp) SiloUtil.trackWriter.println("Person " + perId + " started working for job " + jj.getId());
            return true;
        }
    }

    public void quitJob (int perId) {
        // Let person perId quit her/his job and make this job available to others

        Person pp = dataContainer.getHouseholdData().getPersonFromId(perId);
        if (pp == null) return;  // person has died or moved away
        dataContainer.getJobData().quitJob(true, pp);
        EventManager.countEvent(EventTypes.QUIT_JOB);
        dataContainer.getHouseholdData().addHouseholdThatChanged(pp.getHh());
        if (perId == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + perId + " quit her/his job.");
        }
    }
}
