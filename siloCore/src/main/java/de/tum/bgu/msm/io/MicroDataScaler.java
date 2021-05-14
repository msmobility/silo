package de.tum.bgu.msm.io;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.Collection;

public class MicroDataScaler {

    private final DataContainer dataContainer;
    private final Properties properties;
    private static Logger logger = Logger.getLogger(MicroDataScaler.class);


    public MicroDataScaler(DataContainer dataContainer, Properties properties) {
        this.dataContainer = dataContainer;
        this.properties = properties;
    }

    public void scale() {
        double scaleFactor = properties.main.scaleFactor;

        if (scaleFactor > 1.) {
            throw new RuntimeException("Scale factors higher than 1 are not accepted");
        }

        if (scaleFactor != 1.) {


            //scale households
            HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
            Collection<Household> households = householdDataManager.getHouseholds();

            JobDataManager jobDataManager = dataContainer.getJobDataManager();
            Collection<Job> jobs = jobDataManager.getJobs();
            RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
            Collection<Dwelling> dwellings = realEstateDataManager.getDwellings();

            for (Household hh : households) {
                if (SiloUtil.getRandomNumberAsDouble() > scaleFactor) {
                    int dwellingId = hh.getDwellingId();
                    for (Person person : hh.getPersons().values()) {
                        person.setHousehold(null);
                        householdDataManager.removePersonFromHousehold(person);
                        householdDataManager.removePerson(person.getId());
                        Job job = jobDataManager.getJobFromId(person.getJobId());
                        if (job != null){
                            jobDataManager.removeJob(job.getId());
                        }
                    }
                    householdDataManager.removeHousehold(hh.getId());
                    realEstateDataManager.removeDwellingFromVacancyList(dwellingId);
                    realEstateDataManager.removeDwelling(dwellingId);

                }
            }

            logger.warn("The population was scaled to " + householdDataManager.getHouseholds().size() + " households with " + householdDataManager.getPersons().size() + " persons");

            //random subsample of vacants dd and jj
            for (Dwelling dd : dwellings) {
                if (dd.getResidentId() == -1) {
                    if (SiloUtil.getRandomNumberAsDouble() > scaleFactor) {
                        realEstateDataManager.removeDwelling(dd.getId());

                    }
                }
            }

            for (Job jj : jobs) {
                if (jj.getWorkerId() == -1) {
                    if (SiloUtil.getRandomNumberAsDouble() > scaleFactor) {
                        jobDataManager.removeJob(jj.getId());
                    }
                }
            }

            logger.warn("The population was scaled to " + realEstateDataManager.getDwellings().size() + " dwellings and " + jobDataManager.getJobs().size() + " jobs");
        }

    }

}
