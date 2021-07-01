package de.tum.bgu.msm.syntheticPopulationGenerator.germany.io;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobMuc;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.io.output.PersonWriter;
import de.tum.bgu.msm.schools.School;
import de.tum.bgu.msm.schools.SchoolData;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class PersonJobWriterMucMito implements PersonWriter {

    private final static Logger logger = Logger.getLogger(PersonJobWriterMucMito.class);

    private final HouseholdDataManager householdData;
    private final JobDataManager jobData;
    private final RealEstateDataManager realEstateData;
    private final SchoolData schoolData;

    public PersonJobWriterMucMito(HouseholdDataManager householdData, JobDataManager jobData,
                                  RealEstateDataManager realEstateData, SchoolData schoolData) {
        this.householdData = householdData;
        this.jobData = jobData;
        this.realEstateData = realEstateData;
        this.schoolData = schoolData;
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


    public void writePersonsWithJob(String path, String pathJob) {

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

        logger.info("  Writing job file to " + pathJob);
        PrintWriter pwj = SiloUtil.openFileForSequentialWriting(pathJob, false);
        pwj.print("id,zone,personId,type");
        pwj.print(",");
        pwj.print("coordX");
        pwj.print(",");
        pwj.print("coordY");
        pwj.print(",");
        pwj.print("startTime");
        pwj.print(",");
        pwj.print("duration");
        pwj.println();

        TableDataSet jobDuration = SiloUtil.readCSVfile("C:/models/silo/germany/input/jobDurationDistributions.csv");
        TableDataSet jobStart = SiloUtil.readCSVfile("C:/models/silo/germany/input/jobStartTimeDistributions.csv");
        int[] times = jobStart.getColumnAsInt("time");
        int[] durations = jobDuration.getColumnAsInt("time");

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
            if (pp.getJobId() > 0) {
                Job jj = jobData.getJobFromId(pp.getJobId());
                pwj.print(jj.getId());
                pwj.print(",");
                pwj.print(jj.getZoneId());
                pwj.print(",");
                pwj.print(jj.getWorkerId());
                pwj.print(",\"");
                pwj.print(jj.getType());
                pwj.print("\"");

                Coordinate coordinate = jj.getCoordinate();
                pwj.print(",");
                pwj.print(coordinate.x);
                pwj.print(",");
                pwj.print(coordinate.y);
                pwj.print(",");
                int startTime = times[SiloUtil.select(jobStart.getColumnAsFloat(jj.getType()))];
                int duration = durations[SiloUtil.select(jobDuration.getColumnAsFloat(jj.getType()))];
                pwj.print(startTime);
                pwj.print(",");
                pwj.print(duration);
                pwj.println();
            }

            if (pp.getId() == SiloUtil.trackPp) {
                SiloUtil.trackingFile("Writing pp " + pp.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(pp.toString());
            }
        }
        pwp.close();
        pwj.close();
    }


    public void writePersonsWithJobAttributes(String path, int subPopulation) {

        logger.info("  Writing person file to " + path);
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(path, true);
        if (subPopulation == 0) {
            pwp.print("id,hhid,age,gender,occupation,driversLicense,workplace,income");
            pwp.print(",");
            pwp.print("homeZone");
            pwp.print(",");
            pwp.print("jobType");
            pwp.print(",");
            pwp.print("workZone");
            pwp.print(",");
            pwp.print("jobCoordX");
            pwp.print(",");
            pwp.print("jobCoordy");
            pwp.print(",");
            pwp.print("schoolType");
            pwp.print(",");
            pwp.print("schoolId");
            pwp.print(",");
            pwp.print("schoolZone");
            pwp.print(",");
            pwp.print("schoolCoordX");
            pwp.print(",");
            pwp.print("schoolCoordY");
            pwp.println();
        }

//        TableDataSet zonesInState10 = SiloUtil.readCSVfile("C:/models/silo/germany/input/syntheticPopulation/zoneSystem.csv");
        for (Person pp : householdData.getPersons()) {
            Household hh = pp.getHousehold();
            pwp.print(pp.getId());
            pwp.print(",");
            pwp.print(hh.getId());
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
            int zone = realEstateData.getDwelling(hh.getDwellingId()).getZoneId();
            int income = pp.getAnnualIncome();
/*            if (zonesInState10.getValueAt(zone,"Region") != 10){
                income = income / 12;
            }*/
            pwp.print(income);
            pwp.print(",");
            pwp.print(zone);
            pwp.print(",");
            pwp.print(pp.getAttribute("jobType").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("workZone").get().toString());
            pwp.print(",");
            if (pp.getJobId() > 0){
                pwp.print(jobData.getJobFromId(pp.getJobId()).getCoordinate().x);
                pwp.print(",");
                pwp.print(jobData.getJobFromId(pp.getJobId()).getCoordinate().y);
                pwp.print(",");
            } else {
                pwp.print(0);
                pwp.print(",");
                pwp.print(0);
                pwp.print(",");
            }
            pwp.print(pp.getAttribute("schoolType").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("schoolId").get().toString());
            pwp.print(",");
            pwp.print(pp.getAttribute("schoolPlace").get().toString());
            pwp.print(",");
            if (pp.getOccupation().equals(Occupation.STUDENT)){
                int schoolId = Integer.parseInt(String.valueOf(pp.getAttribute("schoolId").get()));
                Coordinate coordinate = ((MicroLocation) schoolData.getSchoolFromId(schoolId)).getCoordinate();
                pwp.print(coordinate.x);
                pwp.print(",");
                pwp.print(coordinate.y);
            } else {
                pwp.print(0);
                pwp.print(",");
                pwp.print(0);
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
