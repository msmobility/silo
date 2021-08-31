package de.tum.bgu.msm.syntheticPopulationGenerator.germany.io;

import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.io.output.PersonWriter;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.PrintWriter;

public class PersonWriterMucMito implements PersonWriter {

    private final static Logger logger = Logger.getLogger(PersonWriterMucMito.class);

    private final HouseholdDataManager householdData;

    public PersonWriterMucMito(HouseholdDataManager householdData) {
        this.householdData = householdData;
    }

    @Override
    public void writePersons(String path) {

        logger.info("  Writing person file to " + path);
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(path, false);
        pwp.print("id,hhid,age,gender,occupation,driversLicense,workplace,income");
        pwp.print(",");
        pwp.print("jobType");
        pwp.print(",");
        pwp.print("disability");
        pwp.print(",");
        pwp.print("schoolId");
        pwp.print(",");
        pwp.print("schoolType");
        pwp.print(",");
        pwp.print("schoolPlace");

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
            pwp.print(pp.getOccupation().getCode());
            pwp.print(",");
            pwp.print(pp.hasDriverLicense());
            pwp.print(",");
            pwp.print(pp.getJobId());
            pwp.print(",");
            pwp.print(pp.getAnnualIncome() / 12);
            pwp.print(",");
            pwp.print(pp.getAttribute("jobType").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("disability").get().toString());
            pwp.print(",");
            pwp.print(0);
            //pwp.print(pp.getAttribute("schoolId").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("schoolType").get().toString());
            pwp.print(",");
            pwp.print(((PersonMuc)pp).getSchoolPlace());
            pwp.println();
            if (pp.getId() == SiloUtil.trackPp) {
                SiloUtil.trackingFile("Writing pp " + pp.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(pp.toString());
            }
        }
        pwp.close();
    }


    public void writePersonsWithJob(String path) {

        logger.info("  Writing person file to " + path);
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(path, false);
        pwp.print("id,hhid,age,gender,occupation,driversLicense,workplace,income");
        pwp.print(",");
        pwp.print("jobType");
        pwp.print(",");
        pwp.print("disability");
        pwp.print(",");
        pwp.print("schoolId");
        pwp.print(",");
        pwp.print("schoolType");
        pwp.print(",");
        pwp.print("schoolPlace");
        pwp.print(",");
        pwp.print("state");
        pwp.print(",");
        pwp.print("originalId");
        pwp.print(",");
        pwp.print("workZone");
        pwp.print(",");
        pwp.print("commuteDistanceKm");
        pwp.print(",");
        pwp.print("commuteEduDistanceKm");
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
            pwp.print(pp.getOccupation().getCode());
            pwp.print(",");
            pwp.print(pp.hasDriverLicense());
            pwp.print(",");
            pwp.print(pp.getJobId());
            pwp.print(",");
            pwp.print(pp.getAnnualIncome());
            pwp.print(",");
            pwp.print(pp.getAttribute("jobType").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("disability").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("schoolId").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("schoolType").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("schoolPlace").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("state").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("originalId").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("workZone").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("commuteDistance").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("commuteEduDistance").get().toString());
            pwp.println();
            if (pp.getId() == SiloUtil.trackPp) {
                SiloUtil.trackingFile("Writing pp " + pp.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(pp.toString());
            }
        }
        pwp.close();
    }
}
