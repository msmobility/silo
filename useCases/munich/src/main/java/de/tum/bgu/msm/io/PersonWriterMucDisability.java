package de.tum.bgu.msm.io;

import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMucDisability;
import de.tum.bgu.msm.io.output.PersonWriter;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;

public class PersonWriterMucDisability implements PersonWriter {

    private final static Logger logger = Logger.getLogger(PersonWriterMucDisability.class);

    private final HouseholdDataManager householdData;

    public PersonWriterMucDisability(HouseholdDataManager householdData) {
        this.householdData = householdData;
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
        pwp.print("jobType");
        pwp.print(",");
        pwp.print("jobDuration");
        pwp.print(",");
        pwp.print("jobStartTimeWorkdays");
        pwp.print(",");
        pwp.print("jobStartTimeWeekends");

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
            pwp.print("0");
            pwp.print(",");
            pwp.print(pp.getAttribute("disability").get().toString());
            pwp.print(",");
            pwp.print("0");
            pwp.print(",");
            pwp.print(pp.getAttribute("jobDurationType").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("jobDuration").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("jobStartTimeWorkday").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("jobStartTimeWeekend").get().toString());
            pwp.println();
            if (pp.getId() == SiloUtil.trackPp) {
                SiloUtil.trackingFile("Writing pp " + pp.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(pp.toString());
            }
        }
        pwp.close();
    }
}
