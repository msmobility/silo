package de.tum.bgu.msm.health;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.io.output.PersonWriter;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;

public class HealthPersonWriter implements PersonWriter {

    private final static Logger logger = Logger.getLogger(HealthPersonWriter.class);
    protected final DataContainer dataContainer;
    private final HouseholdDataManager householdData;

    public HealthPersonWriter(DataContainer dataContainer) {
        this.householdData = dataContainer.getHouseholdDataManager();
        this.dataContainer = dataContainer;
    }

    @Override
    public void writePersons(String path) {

        logger.info("  Writing person file to " + path);
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(path, false);
        pwp.print("id,hhid,age,gender,relationShip,occupation,driversLicense,workplace,income");
        pwp.print(",");
        pwp.print("disability");
        pwp.print(",");
        pwp.print("schoolId");
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
        pwp.print("exposure_pm25");
        pwp.print(",");
        pwp.print("exposure_no2");
        pwp.print(",");
        pwp.print("exposure_normalised_pm25");
        pwp.print(",");
        pwp.print("exposure_normalised_no2");
        pwp.print(",");
        pwp.print("rr_walk");
        pwp.print(",");
        pwp.print("rr_cycle");
        pwp.print(",");
        pwp.print("rr_pm25");
        pwp.print(",");
        pwp.print("rr_no2");
        pwp.print(",");
        pwp.print("rr_all");
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
            pwp.print(0);
            pwp.print(",");
            pwp.print(((PersonHealthMCR)pp).getSchoolId());
            pwp.print(",");
            pwp.print(((PersonHealthMCR)pp).getWeeklyTravelSeconds());
            pwp.print(",");
            pwp.print(((PersonHealthMCR)pp).getWeeklyActivityMinutes());
            pwp.print(",");
            pwp.print(((PersonHealthMCR)pp).getWeeklyHomeMinutes());
            pwp.print(",");
            pwp.print(((PersonHealthMCR)pp).getWeeklyAccidentRisk("lightInjury"));
            pwp.print(",");
            pwp.print(((PersonHealthMCR)pp).getWeeklyAccidentRisk("severeInjury"));
            pwp.print(",");
            pwp.print(((PersonHealthMCR)pp).getWeeklyAccidentRisk("fatality"));
            pwp.print(",");
            pwp.print(((PersonHealthMCR)pp).getWeeklyMarginalMetHours(Mode.walk));
            pwp.print(",");
            pwp.print(((PersonHealthMCR)pp).getWeeklyMarginalMetHours(Mode.bicycle));
            pwp.print(",");
            pwp.print((((PersonHealthMCR) pp).getWeeklyExposureByPollutant("pm2.5")));
            pwp.print(",");
            pwp.print((((PersonHealthMCR) pp).getWeeklyExposureByPollutant("no2")));
            pwp.print(",");
            pwp.print((((PersonHealthMCR) pp).getWeeklyExposureByPollutantNormalised("pm2.5")));
            pwp.print(",");
            pwp.print((((PersonHealthMCR) pp).getWeeklyExposureByPollutantNormalised("no2")));
            pwp.print(",");
            pwp.print((((PersonHealthMCR) pp).getRelativeRiskByType("walk")));
            pwp.print(",");
            pwp.print((((PersonHealthMCR) pp).getRelativeRiskByType("cycle")));
            pwp.print(",");
            pwp.print((((PersonHealthMCR) pp).getRelativeRiskByType("pm2.5")));
            pwp.print(",");
            pwp.print((((PersonHealthMCR) pp).getRelativeRiskByType("no2")));
            pwp.print(",");
            pwp.print(((PersonHealthMCR)pp).getAllCauseRR());
            pwp.println();
            if (pp.getId() == SiloUtil.trackPp) {
                SiloUtil.trackingFile("Writing pp " + pp.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(pp.toString());
            }
        }
        pwp.close();
    }
}
