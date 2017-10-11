package de.tum.bgu.msm.demography;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.IssueCounter;
import org.apache.log4j.Logger;

/**
 * Simulates finding a new job and quitting a job
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 1 March 2013 in Santa Fe
 **/

public class ChangeEmploymentModel {
    static Logger logger = Logger.getLogger(ChangeEmploymentModel.class);
    private GeoData geoData;


    public ChangeEmploymentModel(GeoData geoData) {
        // constructor
        this.geoData = geoData;
    }


    public boolean findNewJob (int perId) {
        // find new job for person perId

        Person pp = Person.getPersonFromId(perId);
        if (pp == null) return false;  // person has died or moved away

        int sm = 0;
        for (int reg: geoData.getRegionList()) sm += JobDataManager.getNumberOfVacantJobsByRegion(reg);
        if (sm == 0) {
            IssueCounter.countMissingJob();
            return false;
        } else {
            int homeZone = pp.getHomeTaz();
            int idVacantJob = JobDataManager.findVacantJob(homeZone, geoData.getRegionList());
            if (idVacantJob == -1) {
                IssueCounter.countMissingJob();
                return false;
            }
            Job jj = Job.getJobFromId(idVacantJob);
            jj.setWorkerID(perId);
            pp.setWorkplace(jj.getId());
            pp.setOccupation(1);
            int gender = pp.getGender() - 1;
            int age = Math.min(99, pp.getAge());
            int inc = HouseholdDataManager.selectIncomeForPerson(gender, age, 1);
            pp.setIncome(inc);
            EventManager.countEvent(EventTypes.findNewJob);
            if (perId == SiloUtil.trackPp) SiloUtil.trackWriter.println("Person " + perId + " started working for job " + jj.getId());
            return true;
        }
    }


    public void quitJob (int perId, JobDataManager jobDataManager) {
        // Let person perId quit her/his job and make this job available to others

        Person pp = Person.getPersonFromId(perId);
        if (pp == null) return;  // person has died or moved away
        pp.quitJob(true, jobDataManager);
        EventManager.countEvent(EventTypes.quitJob);
        if (perId == SiloUtil.trackPp) SiloUtil.trackWriter.println("Person " + perId + " quit her/his job.");
    }
}
