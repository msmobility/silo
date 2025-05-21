package de.tum.bgu.msm.health;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.health.disease.HealthExposures;
import de.tum.bgu.msm.io.output.PersonWriter;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;

public class HealthPersonWriter implements PersonWriter {

    private final static Logger logger = LogManager.getLogger(HealthPersonWriter.class);
    protected final DataContainer dataContainer;
    private final HouseholdDataManager householdData;

    public HealthPersonWriter(DataContainer dataContainer) {
        this.householdData = dataContainer.getHouseholdDataManager();
        this.dataContainer = dataContainer;
    }

    @Override
    public void writePersons(String path) {
    }

    public void writePersonExposure(String path) {

        logger.info("  Writing person exposure file to " + path);
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(path, false);
        pwp.print("id,hhid,age,gender,relationShip,occupation,driversLicense,workplace,income");
        pwp.print(",");
        pwp.print("schoolId");
        pwp.print(",");
        pwp.print("ethnicity");
        pwp.print(",");
        pwp.print("zone");
        pwp.print(",");
        pwp.print("totalTravelTime_sec");
        pwp.print(",");
        pwp.print("totalActivityTime_min");
        pwp.print(",");
        pwp.print("totalTimeAtHome_min");
        pwp.print(",");
        pwp.print("lightInjuryRisk");
        pwp.print(",");
        pwp.print("severeInjuryRisk");
        pwp.print(",");
        pwp.print("fatalityRisk");
        pwp.print(",");
        pwp.print("mmetHr_walk");
        pwp.print(",");
        pwp.print("mmetHr_cycle");
        pwp.print(",");
        pwp.print("mmetHr_otherSport");
        pwp.print(",");
        pwp.print("exposure_normalised_pm25");
        pwp.print(",");
        pwp.print("exposure_normalised_no2");
        pwp.print(",");
        pwp.print("exposure_normalised_noise_Lden");
        pwp.print(",");
        pwp.print("exposure_normalised_ndvi");
        pwp.print(",");
        pwp.print("exposure_noise_HA");
        pwp.print(",");
        pwp.print("exposure_noise_HSD");
        pwp.println();

        for (Person pp : householdData.getPersons()) {
            pwp.print(pp.getId());
            pwp.print(",");
            pwp.print(pp.getHousehold().getId());
            pwp.print(",");
            pwp.print(pp.getAge());
            pwp.print(",");
            pwp.print(pp.getGender().getCode());
            pwp.print(",");
            String role = pp.getRole().toString();
            pwp.print(role);
            pwp.print(",");
            pwp.print(pp.getOccupation().getCode());
            pwp.print(",");
            pwp.print(pp.hasDriverLicense());
            pwp.print(",");
            pwp.print(pp.getJobId());
            pwp.print(",");
            pwp.print(pp.getAnnualIncome());
            pwp.print(",");
            pwp.print(((PersonHealthMEL)pp).getSchoolId());
            pwp.print(",");
            pwp.print(((PersonHealthMEL)pp).getEthnic().toString());
            pwp.print(",");
            pwp.print(dataContainer.getRealEstateDataManager().getDwelling(pp.getHousehold().getDwellingId()).getZoneId());
            pwp.print(",");
            pwp.print(((PersonHealthMEL)pp).getWeeklyTravelSeconds());
            pwp.print(",");
            pwp.print(((PersonHealthMEL)pp).getWeeklyActivityMinutes());
            pwp.print(",");
            pwp.print(((PersonHealthMEL)pp).getWeeklyHomeMinutes());
            pwp.print(",");
            pwp.print(((PersonHealthMEL)pp).getWeeklyAccidentRisk("lightInjury"));
            pwp.print(",");
            pwp.print(((PersonHealthMEL)pp).getWeeklyAccidentRisk("severeInjury"));
            pwp.print(",");
            pwp.print(((PersonHealthMEL)pp).getWeeklyAccidentRisk("fatality"));
            pwp.print(",");
            pwp.print(((PersonHealthMEL)pp).getWeeklyMarginalMetHours(Mode.walk));
            pwp.print(",");
            pwp.print(((PersonHealthMEL)pp).getWeeklyMarginalMetHours(Mode.bicycle));
            pwp.print(",");
            pwp.print(((PersonHealthMEL)pp).getWeeklyMarginalMetHoursSport());
            pwp.print(",");
            pwp.print((((PersonHealthMEL) pp).getWeeklyExposureByPollutantNormalised("pm2.5")));
            pwp.print(",");
            pwp.print((((PersonHealthMEL) pp).getWeeklyExposureByPollutantNormalised("no2")));
            pwp.print(",");
            pwp.print((((PersonHealthMEL) pp).getWeeklyNoiseExposuresNormalised()));
            pwp.print(",");
            pwp.print((((PersonHealthMEL) pp).getWeeklyGreenExposuresNormalised()));
            pwp.print(",");
            pwp.print((((PersonHealthMEL) pp).getNoiseHighAnnoyedPercentage()));
            pwp.print(",");
            pwp.print((((PersonHealthMEL) pp).getNoiseHighSleepDisturbancePercentage()));
            pwp.println();
            if (pp.getId() == SiloUtil.trackPp) {
                SiloUtil.trackingFile("Writing pp " + pp.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(pp.toString());
            }
        }
        pwp.close();
    }


    public void writePersonRelativeRisk(String path) {

        logger.info("  Writing person relative risk file to " + path);
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(path, false);
        pwp.print("id,hhid,age,gender,relationShip,occupation,driversLicense,workplace,income");
        pwp.print(",");
        pwp.print("schoolId");
        pwp.print(",");
        pwp.print("ethnicity");
        pwp.print(",");
        pwp.print("zone");


        for(HealthExposures exposures : HealthExposures.values()){
            for(Diseases diseases : ((DataContainerHealth)dataContainer).getDoseResponseData().get(exposures).keySet()){
                pwp.print(",");
                pwp.print("rr_" + exposures.name() + "_" + diseases.name());
            }
        }
        pwp.println();

        for (Person pp : householdData.getPersons()) {
            pwp.print(pp.getId());
            pwp.print(",");
            pwp.print(pp.getHousehold().getId());
            pwp.print(",");
            pwp.print(pp.getAge());
            pwp.print(",");
            pwp.print(pp.getGender().getCode());
            pwp.print(",\"");
            String role = pp.getRole().toString();
            pwp.print(role);
            pwp.print("\",");
            pwp.print(pp.getOccupation().getCode());
            pwp.print(",");
            pwp.print(pp.hasDriverLicense());
            pwp.print(",");
            pwp.print(pp.getJobId());
            pwp.print(",");
            pwp.print(pp.getAnnualIncome());
            pwp.print(",");
            pwp.print(((PersonHealthMEL)pp).getSchoolId());
            pwp.print(",");
            pwp.print(((PersonHealthMEL)pp).getEthnic().toString());
            pwp.print(",");
            pwp.print(dataContainer.getRealEstateDataManager().getDwelling(pp.getHousehold().getDwellingId()).getZoneId());

            for(HealthExposures exposures : HealthExposures.values()){
                for(Diseases diseases : ((DataContainerHealth)dataContainer).getDoseResponseData().get(exposures).keySet()){
                    pwp.print(",");
                    if(((PersonHealthMEL) pp).getRelativeRisksByDisease().get(exposures)==null){
                        pwp.print(0.);
                    }else {
                        pwp.print(((PersonHealthMEL) pp).getRelativeRisksByDisease().get(exposures).getOrDefault(diseases, 0.f));
                    }
                }
            }
            pwp.println();
            if (pp.getId() == SiloUtil.trackPp) {
                SiloUtil.trackingFile("Writing pp " + pp.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(pp.toString());
            }
        }
        pwp.close();
    }
}
