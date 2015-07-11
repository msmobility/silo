package edu.umd.ncsg.jobmography;

import com.pb.common.datafile.TableDataSet;
import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.data.*;
import edu.umd.ncsg.events.EventRules;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.ForkJoinPool;
import com.pb.sawdust.calculator.Function1;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.concurrent.ForkJoinPoolFactory;
import com.pb.sawdust.util.concurrent.IteratorAction;

/**
 * Reads exogenous forecast for jobs and adds/removes jobs accordingly
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 25 February 2013 in Santa Fe, NM
 * Revised on 11 March 2014 in College Park, MD
 **/

public class updateJobs {

    protected transient Logger logger = Logger.getLogger(updateJobs.class);
    protected static final String PROPERTIES_EMPLOYMENT_FORECAST     = "interpol.empl.forecast";
    private ResourceBundle rb;
    private HashMap<String, int[]> jobsAvailableForRemoval;

    public updateJobs(ResourceBundle rb) {
        this.rb = rb;
    }


//    public void updateJobInventoryThisYear(int year) {
//        // read exogenous job forecast and add or remove jobs for each zone accordingly
//
//        if (!EventRules.ruleStartNewJob() && !EventRules.ruleQuitJob()) return;
//        logger.info("  Updating job market based on exogenous forecast for " + year);
//        int[][] jobsByZone = new int[JobType.getNumberOfJobTypes()][SiloUtil.getHighestZonalId()+1];
//        for (Job jj: Job.getJobArray()) {
//            int jobTypeId = JobType.getOrdinal(jj.getType());
//            jobsByZone[jobTypeId][jj.getZone()]++;
//        }
//
//        String dir = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName + "/employmentForecast/";
//        String forecastFileName = dir + rb.getString(PROPERTIES_EMPLOYMENT_FORECAST) + year + ".csv";
//        TableDataSet forecast = SiloUtil.readCSVfile(forecastFileName);
//        for (int row = 1; row <= forecast.getRowCount(); row++) {
//            int zone = (int) forecast.getValueAt(row, "zone");
//            for (String jt: JobType.getJobTypes()) {
//                int jobsExogenousForecast = (int) forecast.getValueAt(row, jt);
//                if (jobsExogenousForecast > jobsByZone[JobType.getOrdinal(jt)][zone]) {
//                    int change = jobsExogenousForecast - jobsByZone[JobType.getOrdinal(jt)][zone];
//                    addJobs(jt, zone, change);
//                } else if (jobsExogenousForecast < jobsByZone[JobType.getOrdinal(jt)][zone]) {
//                    int change = jobsByZone[JobType.getOrdinal(jt)][zone] - jobsExogenousForecast;
//                    removeJobs(jt, zone, change);
//                }
//            }
//        }
//    }


//    private void addJobs(String tp, int zone, int amount) {
//        // add <amount> vacant jobs of type <tp> in <zone>
//
//        for (int i = 1; i <= amount; i++) {
//            int id = JobDataManager.getNextJobId();
//            new Job(id, zone, -1, tp);
//            if (id == SiloUtil.trackJj) SiloUtil.trackWriter.println("Job " + id + " of type " + tp +
//                    " was newly created in zone " + zone + " based on exogenous forecast.");
//            JobDataManager.addJobToVacancyList(zone, id);
//        }
//    }


//    private void removeJobs (String tp, int zone, int amount) {
//        // remove <amount> jobs of type <tp> in <zone>
//
//        // Create list of jobs (vacant and occupied) for this particular zone
//        ArrayList<Job> vacantJobs = new ArrayList<>();
//        ArrayList<Job> occupiedJobs = new ArrayList<>();
//        for (Job jj: Job.getJobArray()) {
//            if (jj.getType().equalsIgnoreCase(tp) && jj.getZone() == zone) {
//                if (jj.getWorkerId() > 0) occupiedJobs.add(jj);
//                else vacantJobs.add(jj);
//            }
//        }
//
//        // first, try to eliminate only jobs that are not filled
//        while (vacantJobs.size() > 0 && amount > 0) {
//            Job removeThisJob = vacantJobs.get(0);
//            vacantJobs.remove(0);
//            int id = removeThisJob.getId();
//            int region = SiloUtil.getRegionOfZone(zone);
//            JobDataManager.removeJobFromVacancyList(id, region, false);  // do not log errors, as job may not be stored in vacancy list if number of vacant jobs in this region exceeded the dimensions of vacantJobsByRegion[][]
//            Job.removeJob(id);
//            if (id == SiloUtil.trackJj) SiloUtil.trackWriter.println("Vacant job " + id + " of type " + tp +
//                    " was removed in zone " + zone + " based on exogenous forecast.");
//            amount = amount - 1;
//        }
//
//        // if necessary (i.e., amount still > 0) remove jobs that are filled with workers
//        while (amount > 0) {
//            Job removeThisJob = occupiedJobs.get(0);
//            occupiedJobs.remove(0);
//            int id = removeThisJob.getId();
//            int personId = removeThisJob.getWorkerId();
//            Person.getPersonFromId(personId).quitJob(false);
//            Job.removeJob(id);
//            if (id == SiloUtil.trackJj) SiloUtil.trackWriter.println("Previously occupied job " + id + " of type " +
//                    tp + " was removed in zone " + zone + " based on exogenous forecast.");
//            amount = amount - 1;
//        }
//    }


    public void updateJobInventoryMultiThreadedThisYear(int year) {
        // read exogenous job forecast and add or remove jobs for each zone accordingly in multi-threaded procedure

        if (!EventRules.ruleStartNewJob() && !EventRules.ruleQuitJob()) return;
        logger.info("  Updating job market based on exogenous forecast for " + year + " (multi-threaded step)");
        int[][] jobsByZone = new int[JobType.getNumberOfJobTypes()][geoData.getHighestZonalId()+1];
        for (Job jj: Job.getJobArray()) {
            int jobTypeId = JobType.getOrdinal(jj.getType());
            jobsByZone[jobTypeId][jj.getZone()]++;
        }

        String dir = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName + "/employmentForecast/";
        String forecastFileName = dir + rb.getString(PROPERTIES_EMPLOYMENT_FORECAST) + year + ".csv";
        TableDataSet forecast = SiloUtil.readCSVfile(forecastFileName);

        ArrayList<String> employmentChangeList = new ArrayList<>();
        for (int row = 1; row <= forecast.getRowCount(); row++) {
            int zone = (int) forecast.getValueAt(row, "zone");
            for (String jt: JobType.getJobTypes()) {
                int jobsExogenousForecast = (int) forecast.getValueAt(row, jt);
                if (jobsExogenousForecast > jobsByZone[JobType.getOrdinal(jt)][zone]) {
                    int change = jobsExogenousForecast - jobsByZone[JobType.getOrdinal(jt)][zone];
                    employmentChangeList.add("add:" + jt + "." + zone + "." + change);
//                    addJobs(jt, zone, change);
                } else if (jobsExogenousForecast < jobsByZone[JobType.getOrdinal(jt)][zone]) {
                    int change = jobsByZone[JobType.getOrdinal(jt)][zone] - jobsExogenousForecast;
//                    removeJobs(jt, zone, change);
                    employmentChangeList.add("rem:" + jt + "." + zone + "." + change);
                }
            }
        }

        jobsAvailableForRemoval = new HashMap<>();
        for (Job jj: Job.getJobArray()) {
            String token = jj.getType() + "." + jj.getZone() + "." + (jj.getWorkerId() == -1);
            if (jobsAvailableForRemoval.containsKey(token)) {
                int[] jobList = jobsAvailableForRemoval.get(token);
                jobsAvailableForRemoval.put(token, SiloUtil.expandArrayByOneElement(jobList, jj.getId()));
            } else {
                jobsAvailableForRemoval.put(token, new int[]{jj.getId()});
            }
        }


        // Multi-threading code
        Function1<String,Void> JobChangeMethod = new Function1<String,Void>() {
            public Void apply(String employmentChangeDefinition) {
                String[] change = employmentChangeDefinition.split(":");
                if (change[0].equalsIgnoreCase("add")) {
                    addJobs(change[1]);
                } else {              // token "rem"
                    removeJobs(change[1]);
                }
                return null;
            }
        };

        String[] code = SiloUtil.convertStringArrayListToArray(employmentChangeList);
        Iterator<String> jobChangeIterator = ArrayUtil.getIterator(code);
        IteratorAction<String> itTask = new IteratorAction<>(jobChangeIterator, JobChangeMethod);
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool();
        pool.execute(itTask);
        itTask.waitForCompletion();

        // Fix job map, which for some reason keeps getting messed up
        for (int jobId: Job.getJobMapIDs()) {
            Job jj = Job.getJobFromId(jobId);
            if (jj == null) {
                Job.removeJob(jobId);
                logger.warn("Had to manually remove Job " + jobId + " from job map.");
            }
        }
    }


    private void addJobs (String addJobsInstruction) {
        // add new jobs

        String[] definition = addJobsInstruction.split("\\.");
        String type = definition[0];
        int zone = Integer.parseInt(definition[1]);
        int change = Integer.parseInt(definition[2]);
        for (int i = 1; i <= change; i++) {
            int id;
            synchronized (Job.class) {
                id = JobDataManager.getNextJobId();
                new Job(id, zone, -1, type);
            }
            if (id == SiloUtil.trackJj) SiloUtil.trackWriter.println("Job " + id + " of type " + type +
                    " was newly created in zone " + zone + " based on exogenous forecast.");
        }
    }


    private void removeJobs (String removeJobsInstructions) {
        // remove jobs

        String[] definition = removeJobsInstructions.split("\\.");
        String type = definition[0];
        int zone = Integer.parseInt(definition[1]);
        int change = Integer.parseInt(definition[2]);

        // first, try to eliminate only jobs that are vacant
        String tokenVacantJobs = type + "." + zone + "." + true;
        if (jobsAvailableForRemoval.containsKey(tokenVacantJobs)) {
            int[] vacantJobs = jobsAvailableForRemoval.get(tokenVacantJobs);
            int counter = 0;
            while (counter < vacantJobs.length && change > 0) {
                synchronized (Job.class) {
                    Job.removeJob(vacantJobs[counter]);
                }
                if (vacantJobs[counter] == SiloUtil.trackJj) SiloUtil.trackWriter.println("Vacant job " + vacantJobs[counter] +
                        " of type " + type + " was removed in zone " + zone + " based on exogenous forecast.");
                counter++;
                change = change - 1;
            }
        }

        // if necessary (i.e., change still > 0) remove jobs that are filled with workers
        String tokenOccupiedJobs = type + "." + zone + "." + false;
        int[] occupiedJobs = jobsAvailableForRemoval.get(tokenOccupiedJobs);
        int counter = 0;
        while (change > 0) {
            Job jobToBeRemoved = Job.getJobFromId(occupiedJobs[counter]);
            int personId = jobToBeRemoved.getWorkerId();
            Person.getPersonFromId(personId).quitJob(false);
            synchronized (Job.class) {
                Job.removeJob(occupiedJobs[counter]);
            }
            if (occupiedJobs[counter] == SiloUtil.trackJj) SiloUtil.trackWriter.println("Previously occupied job " +
                    occupiedJobs[counter] + " of type " + type + " was removed in zone " + zone + " based on exogenous forecast.");
            counter++;
            change = change - 1;
        }
    }
}
