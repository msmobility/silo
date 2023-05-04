package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import com.google.common.math.LongMath;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.syntheticPopulationGenerator.CoefficientsReader;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.util.*;

public class AssignPropertiesToJobs {

    private static final Logger logger = Logger.getLogger(AssignPropertiesToJobs.class);

    private final DataSetSynPop dataSetSynPop;
    private final DataContainer dataContainer;
    private String[] jobStringTypes;
    JobDataManager jobData;
    Map<String, Map<String, Double>> coefficientsFullTime;
    Map<String, Map<String, Double>> coefficientsDuration;
    Map<String, Map<String, Double>> coefficientsStartTimeWorkday;
    Map<String, Map<String, Double>> coefficientsStartTimeWeekend;


    public AssignPropertiesToJobs(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;

    }


    public void run() {
        logger.info("   Running module: job allocation");
        readCoefficients();
        jobData = dataContainer.getJobDataManager();
        int assignedJobs = 0;
        for (Person pp : dataContainer.getHouseholdDataManager().getPersons()) {
            if (pp.getOccupation() == Occupation.EMPLOYED) {
                setFullOrPartTime(pp);
                setDurationAndStartTime(pp);
            } else {
                pp.setAttribute("jobDurationType","0");
                pp.setAttribute("jobDuration", "0");
                pp.setAttribute("jobStartTimeWorkday","0");
                pp.setAttribute("jobStartTimeWeekend","0");
            }
            if (LongMath.isPowerOfTwo(assignedJobs)){
                logger.info("   Assigned " + assignedJobs + " jobs.");
            }
            assignedJobs++;
            //logger.info("   Finished job properties assignment. Assigned " + assignedJobs + " jobs.");
        }
    }

    private void setFullOrPartTime(Person pp) {
        String type = getWorkerCategory(pp);
        Double probability = coefficientsFullTime.get(jobData.getJobFromId(pp.getJobId()).getType()).get(type);
        if (probability > SiloUtil.getRandomNumberAsDouble()){
            pp.setAttribute("jobDurationType","fullTime");
        } else {
            pp.setAttribute("jobDurationType", "partTime");
        }
    }

    private void setDurationAndStartTime(Person pp){

        String q = pp.getAttribute("jobDurationType").toString();
        String durationInMinutes = SiloUtil.select(coefficientsDuration.get(pp.getAttribute("jobDurationType").get().toString()));
        pp.setAttribute("jobDuration", durationInMinutes);
        String startTimeWorkdayInMinutes = "";
        String startTimeWeekendInMinutes = "";
        String durationKey = "";
        if (Integer.parseInt(durationInMinutes) < 3*60){
            durationKey = "0_3";
        } else if (Integer.parseInt(durationInMinutes) < 6*60){
            durationKey = "4_6";
        } else if (Integer.parseInt(durationInMinutes) < 10*60){
            durationKey = "7_10";
        } else {
            durationKey = "11+";
        }
        startTimeWorkdayInMinutes = SiloUtil.select(coefficientsStartTimeWorkday.get(durationKey));
        startTimeWeekendInMinutes = SiloUtil.select(coefficientsStartTimeWeekend.get(durationKey));
        pp.setAttribute("jobStartTimeWorkday",startTimeWorkdayInMinutes);
        pp.setAttribute("jobStartTimeWeekend",startTimeWeekendInMinutes);
    }

    private void readCoefficients() {
        /*coefficients = PropertiesSynPop.get().main.fullTimeProbabilityTable;
        coefficients.buildStringIndex(1);*/
        coefficientsFullTime = new HashMap<>();
        coefficientsDuration = new HashMap<>();
        coefficientsStartTimeWeekend = new HashMap<>();
        coefficientsStartTimeWorkday = new HashMap<>();

        for (String jobType : PropertiesSynPop.get().main.jobStringType) {
            Map<String, Double> coefficientsByJobType =
                    new CoefficientsReader(dataSetSynPop, jobType,
                            Path.of(PropertiesSynPop.get().main.fullTimeFileName)).readCoefficients();
            coefficientsFullTime.putIfAbsent(jobType, coefficientsByJobType);
        }
        coefficientsDuration.putIfAbsent("fullTime", new CoefficientsReader(dataSetSynPop, "duration_workFullTime",
                Path.of(PropertiesSynPop.get().main.durationFileName)).readCoefficients());

        coefficientsDuration.put("partTime", new CoefficientsReader(dataSetSynPop, "duration_workHalfTime",
                Path.of(PropertiesSynPop.get().main.durationFileName)).readCoefficients());

        String[] durationSegments = {"0_3","4_6","7_10","11+"};
        for (String duration : durationSegments){
            String durationWorkday = "work_wkday_duration_" + duration;
            coefficientsStartTimeWorkday.putIfAbsent(duration, new CoefficientsReader(dataSetSynPop, durationWorkday,
                    Path.of(PropertiesSynPop.get().main.startTimeFileName)).readCoefficients());

            String durationWeekend = "work_wkend_duration_" + duration;
            coefficientsStartTimeWeekend.putIfAbsent(duration, new CoefficientsReader(dataSetSynPop, durationWeekend,
                    Path.of(PropertiesSynPop.get().main.startTimeFileName)).readCoefficients());
        }
    }

    private String getWorkerCategory(Person person) {

        String category = "";
            if (person.getAnnualIncome() < 500*12){
                category = "1";
                if (person.getGender().equals(Gender.MALE)) {
                    category = category + "_M_1";
                } else {
                    category = category + "_F_";
                    if (person.getAge() < 31){
                        category = category + "2";
                    } else if (person.getAge() < 51){
                        category = category + "3";
                    } else {
                        category = category + "4";
                    }
                }
            } else if (person.getAnnualIncome() < 900*12){
                category = "2";
                if (person.getGender().equals(Gender.MALE)) {
                    category = category + "_M_1";
                } else {
                    category = category + "_F_";
                    if (person.getAge() < 31){
                        category = category + "2";
                    } else if (person.getAge() < 51){
                        category = category + "3";
                    } else {
                        category = category + "4";
                    }
                }
            } else if (person.getAnnualIncome() < 1100*12){
                category = "3";
                if (person.getGender().equals(Gender.MALE)) {
                    category = category + "_M_1";
                } else {
                    category = category + "_F_";
                    if (person.getAge() < 31){
                        category = category + "2";
                    } else if (person.getAge() < 51){
                        category = category + "3";
                    } else {
                        category = category + "4";
                    }
                }
            } else if (person.getAnnualIncome() < 1300*12){
                category = "4";
                if (person.getGender().equals(Gender.MALE)) {
                    category = category + "_M_1";
                } else {
                    category = category + "_F_";
                    if (person.getAge() < 31){
                        category = category + "2";
                    } else if (person.getAge() < 51){
                        category = category + "3";
                    } else {
                        category = category + "4";
                    }
                }
            } else if (person.getAnnualIncome() < 1500*12){
                category = "5";
                if (person.getGender().equals(Gender.MALE)) {
                    category = category + "_M_1";
                } else {
                    category = category + "_F_";
                    if (person.getAge() < 31){
                        category = category + "2";
                    } else if (person.getAge() < 51){
                        category = category + "3";
                    } else {
                        category = category + "4";
                    }
                }
            } else {
                category = "6";
                if (person.getGender().equals(Gender.MALE)) {
                    category = category + "_M_1";
                } else {
                    category = category + "_F_";
                    if (person.getAge() < 31){
                        category = category + "2";
                    } else if (person.getAge() < 51){
                        category = category + "3";
                    } else {
                        category = category + "4";
                    }
                }
            }


        return category;
    }



}
