package de.tum.bgu.msm.scenarios.health;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.io.output.PersonWriter;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.contrib.emissions.Pollutant;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        pwp.print("nationality");
        pwp.print(",");
        pwp.print("disability");
        pwp.print(",");
        pwp.print("schoolId");
        pwp.print(",");
        pwp.print("lightInjuryRisk");
        pwp.print(",");
        pwp.print("severeInjuryRisk");
        pwp.print(",");
        pwp.print("fatalityRisk");
        pwp.print(",");
        pwp.print("walkMmetHours");
        pwp.print(",");
        pwp.print("cycleMmetHours");

        //order of Set is not fixed
        List<Pollutant> fixedPollutantList = new ArrayList<>();
        for(Pollutant pollutant : ((HealthDataContainerImpl) dataContainer).getPollutantSet()){
            fixedPollutantList.add(pollutant);
            pwp.print(",");
            pwp.print(pollutant.name());
        }
        pwp.print(",");
        pwp.print("all_cause_RR");
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
            pwp.print(((PersonMuc)pp).getNationality().toString());
            pwp.print(",");
            pwp.print(0);
            pwp.print(",");
            pwp.print(((PersonMuc)pp).getSchoolId());
            pwp.print(",");
            pwp.print(((PersonMuc)pp).getWeeklyLightInjuryRisk());
            pwp.print(",");
            pwp.print(((PersonMuc)pp).getWeeklySevereInjuryRisk());
            pwp.print(",");
            pwp.print(((PersonMuc)pp).getWeeklyFatalityInjuryRisk());
            pwp.print(",");
            pwp.print(((PersonMuc)pp).getWeeklyPhysicalActivityMmetHours(Mode.walk));
            pwp.print(",");
            pwp.print(((PersonMuc)pp).getWeeklyPhysicalActivityMmetHours(Mode.bicycle));
            for(Pollutant pollutant : fixedPollutantList){
                pwp.print(",");
                pwp.print(((PersonMuc)pp).getWeeklyExposureByPollutant().get(pollutant.name()));
            }
            pwp.print(",");
            pwp.print(((PersonMuc)pp).getAllCauseRR());
            pwp.println();
            if (pp.getId() == SiloUtil.trackPp) {
                SiloUtil.trackingFile("Writing pp " + pp.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(pp.toString());
            }
        }
        pwp.close();
    }
}
