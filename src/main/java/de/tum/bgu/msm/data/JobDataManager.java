/*
 * Copyright  2005 PB Consult Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package de.tum.bgu.msm.data;

import java.io.*;
import java.util.*;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.events.IssueCounter;
import org.apache.log4j.Logger;


/**
 * Keeps data of dwellings and non-residential floorspace
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 22 February 2013 in Rhede
 **/

public class JobDataManager {
    static Logger logger = Logger.getLogger(JobDataManager.class);

    protected static final String PROPERTIES_JJ_FILE_BIN       = "job.file.bin";
    protected static final String PROPERTIES_JJ_FILE_ASCII     = "job.file.ascii";
    protected static final String PROPERTIES_READ_BIN_FILE     = "read.binary.jj.file";
    protected static final String PROPERTIES_MAX_NUM_VAC_JOB   = "vacant.job.by.reg.array";
    public static final String PROPERTIES_EMPLOYMENT_FORECAST  = "interpol.empl.forecast";
    public static final String PROPERTIES_JOB_CONTROL_TOTAL    = "job.control.total";
    protected static final String PROPERTIES_JOB_CONTROL_YEARS = "job.control.total.years";
    private ResourceBundle rb;
    private geoDataI geoData;

    private static int highestJobIdInUse;
    private static int[][] vacantJobsByRegion;
    private static int[] vacantJobsByRegionPos;
    private static int numberOfStoredVacantJobs;
    private static float[] zonalJobDensity;


    public JobDataManager(ResourceBundle rb, geoDataI geoData) {
        // constructor
        this.rb = rb;
        this.geoData = geoData;
        numberOfStoredVacantJobs = ResourceUtil.getIntegerProperty(rb, PROPERTIES_MAX_NUM_VAC_JOB);
    }


    public void readJobs (boolean readSmallSynPop, int sizeSmallSynPop) {
        // read population
        new JobType(rb);
        boolean readBin = ResourceUtil.getBooleanProperty(rb, PROPERTIES_READ_BIN_FILE, false);
        if (readBin) {
            readBinaryJobDataObjects();
        } else {
            readJobData(readSmallSynPop, sizeSmallSynPop);
        }
        setHighestJobId();
    }


    private void readJobData(boolean readSmallSynPop, int sizeSmallSynPop) {
        logger.info("Reading job micro data from ascii file");

        int year = SiloUtil.getStartYear();
        String fileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_JJ_FILE_ASCII);
        if (readSmallSynPop) fileName += "_" + sizeSmallSynPop;
        fileName += "_" + year + ".csv";

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posZone = SiloUtil.findPositionInArray("zone",header);
            int posWorker = SiloUtil.findPositionInArray("personId",header);
            int posType = SiloUtil.findPositionInArray("type",header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id      = Integer.parseInt(lineElements[posId]);
                int zone    = Integer.parseInt(lineElements[posZone]);
                int worker  = Integer.parseInt(lineElements[posWorker]);
                String type = lineElements[posType].replace("\"", "");
                new Job(id, zone, worker, type);
                if (id == SiloUtil.trackJj) {
                    SiloUtil.trackWriter.println("Read job with following attributes from " + fileName);
                    Job.getJobFromId(id).logAttributes(SiloUtil.trackWriter);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop job file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " jobs.");
    }


    public static void writeBinaryJobDataObjects(ResourceBundle appRb) {
        // Store job object data in binary file

        String fileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(appRb, PROPERTIES_JJ_FILE_BIN);
        logger.info("  Writing job data to binary file.");
        Object[] data = Job.getJobs().toArray(new Job[Job.getJobCount()]);
        try {
            File fl = new File(fileName);
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fl));
            out.writeObject(data);
            out.close();
        } catch (Exception e) {
            logger.error("Error saving to binary file " + fileName + ". Object not saved.\n" + e);
        }
    }


    private void readBinaryJobDataObjects() {
        // read jobs from binary file
        String fileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_JJ_FILE_BIN);
        logger.info("Reading job data from binary file.");
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(fileName)));
            Object[] data = (Object[]) in.readObject();
            Job.saveJobs((Job[]) data[0]);
        } catch (Exception e) {
            logger.error ("Error reading from binary file " + fileName + ". Object not read.\n" + e);
        }
        logger.info("Finished reading " + Job.getJobCount() + " jobs.");
    }


    public void setHighestJobId () {
        // identify highest job ID in use
        highestJobIdInUse = 0;
        for (Job jj: Job.getJobArray()) highestJobIdInUse = Math.max(highestJobIdInUse, jj.getId());
    }


    public static int getNextJobId () {
        // increase highestJobIdInUse by 1 and return value
        highestJobIdInUse++;
        return highestJobIdInUse;
    }

    public static List<Integer> getNextJobIds (int amount) {
        // increase highestJobIdInUse by 1 and return value
        List<Integer> ids = new ArrayList<>();
        for(int i = 0; i < amount; i++) {
            ids.add(++highestJobIdInUse);
        }
        return ids;
    }


    public void updateEmploymentForecast() {
        // create yearly employment forecast files

    	// TODO Would it be better to make this adjustable rather than hardcoded? dz, apr/16
        String[] yearsGiven;
        if (rb.containsKey(PROPERTIES_JOB_CONTROL_YEARS)) {
            int[] yearsInt = ResourceUtil.getIntegerArray(rb, PROPERTIES_JOB_CONTROL_YEARS);
            yearsGiven = new String[yearsInt.length];
            for (int i = 0; i < yearsInt.length; i++) yearsGiven[i] = String.valueOf(yearsInt[i]);
        } else {
            yearsGiven = new String[]{"00", "07", "10", "30", "40"};  // Warning: if years are changed, also change interpolation loop below under "// interpolate employment data"
        }
        int highestYear = SiloUtil.getHighestVal(yearsGiven);
        int smallestYear = SiloUtil.getLowestVal(yearsGiven);

        logger.info("  Interpolating employment forecast for all years from " + (2000 + smallestYear) + " to " +
                (2000 + highestYear));
        TableDataSet jobs ;
        try {
      	  final String filename = SiloUtil.baseDirectory + "/" + ResourceUtil.getProperty(rb, PROPERTIES_JOB_CONTROL_TOTAL);
		jobs = SiloUtil.readCSVfile(filename);
        } catch (Exception ee) {
      	  throw new RuntimeException(ee) ;
        }
        new JobType(rb);

        // jobInventory by [industry][year][tazIndex]
        float[][][] jobInventory = new float[JobType.getNumberOfJobTypes()][highestYear+1][geoData.getZones().length];

        // read employment data
        for (int row = 1; row <= jobs.getRowCount(); row++) {
            int taz = (int) jobs.getValueAt(row, "SMZ");
            for (int jobTp = 0; jobTp < JobType.getNumberOfJobTypes(); jobTp++) {
                for (String year: yearsGiven) {
                     jobInventory[jobTp][Integer.parseInt(year)][geoData.getZoneIndex(taz)] = jobs.getValueAt(row, JobType.getJobType(jobTp) + year);
                }
            }

            // interpolate employment data between available years
            for (int interval = 1; interval < yearsGiven.length; interval++) {
                for (int year = Integer.parseInt(yearsGiven[interval-1]) + 1;
                     year < Integer.parseInt(yearsGiven[interval]); year++) {
                    int prevYear = Integer.parseInt(yearsGiven[interval-1]);
                    int nextYear = Integer.parseInt(yearsGiven[interval]);
                    for (int jobTp = 0; jobTp < JobType.getNumberOfJobTypes(); jobTp++) {
                        float prevInt = jobInventory[jobTp][Integer.parseInt(yearsGiven[interval-1])][geoData.getZoneIndex(taz)];
                        float currInt = jobInventory[jobTp][Integer.parseInt(yearsGiven[interval])][geoData.getZoneIndex(taz)];
                        jobInventory[jobTp][year][geoData.getZoneIndex(taz)] = prevInt + (currInt - prevInt) * (year - prevYear) /
                                (nextYear - prevYear);
                    }
                }
            }
        }

        String dir = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName + "/employmentForecast/";
        SiloUtil.createDirectoryIfNotExistingYet(dir);
        for (int yr = Integer.parseInt(yearsGiven[0]); yr <= highestYear; yr++) {
            String forecastFileName;
            forecastFileName = dir + rb.getString(PROPERTIES_EMPLOYMENT_FORECAST) + (2000 + yr) + ".csv";
            PrintWriter pw = SiloUtil.openFileForSequentialWriting(forecastFileName, false);
            pw.print("zone");
            for (String ind: JobType.getJobTypes()) pw.print("," + ind);
            pw.println();
            for (int zone: geoData.getZones()) {
                pw.print(zone);
                for (int jobTp = 0; jobTp < JobType.getNumberOfJobTypes(); jobTp++) pw.print("," + jobInventory[jobTp][yr][geoData.getZoneIndex(zone)]);
                pw.println();
            }
            pw.close();
        }
    }


    public void identifyVacantJobs() {
        // identify vacant jobs by region (one-time task at beginning of model run only)

        int highestRegionID = SiloUtil.getHighestVal(geoData.getRegionList());
        vacantJobsByRegion = new int[highestRegionID + 1][numberOfStoredVacantJobs + 1];
        vacantJobsByRegion = SiloUtil.setArrayToValue(vacantJobsByRegion, 0);
        vacantJobsByRegionPos = new int[highestRegionID + 1];
        vacantJobsByRegionPos = SiloUtil.setArrayToValue(vacantJobsByRegionPos, 0);

        logger.info("  Identifying vacant jobs");
        for (Job jj : Job.getJobArray()) {
        	if (jj == null) continue;   // should not happen, but model has crashed without this statement.
            if (jj.getWorkerId() == -1) {
                int jobId = jj.getId();
                int region = geoData.getRegionOfZone(jj.getZone());
                if (vacantJobsByRegionPos[region] < numberOfStoredVacantJobs) {
                    vacantJobsByRegion[region][vacantJobsByRegionPos[region]] = jobId;
                    vacantJobsByRegionPos[region]++;
                } else {
                    IssueCounter.countExcessOfVacantJobs(region);
                }
                if (jobId == SiloUtil.trackJj)
                    SiloUtil.trackWriter.println("Added job " + jobId + " to list of vacant jobs.");
            }
        }
    }


    public static int getNumberOfVacantJobsByRegion (int region) {
        return vacantJobsByRegionPos[region];
    }


//    public static void removeJobFromVacancyList(int jobId, int region, boolean logError) {
//        // remove job jobId in zone from vacancy list
//
//        boolean notFound = true;
//
//        if (vacantJobsByRegionPos[region] == 0) {
//            if (logError) logger.error("No vacant jobs in region " + region + " stored. Could not remove job " + jobId + ".");
//            return;
//        }
//        for (int pos = 0; pos < vacantJobsByRegionPos[region]; pos++) {
//            if (vacantJobsByRegion[region][pos] == jobId) {
//                vacantJobsByRegion[region][pos] = vacantJobsByRegion[region][vacantJobsByRegionPos[region] - 1];
//                vacantJobsByRegion[region][vacantJobsByRegionPos[region] - 1] = 0;
//                vacantJobsByRegionPos[region] -= 1;
//                if (jobId == SiloUtil.trackJj)
//                    SiloUtil.trackWriter.println("Removed job " + jobId + " from list of vacant jobs.");
//                notFound = false;
//                break;
//            }
//        }
//
//        if (notFound && logError) logger.warn("Could not find job " + jobId + " in list of vacant jobs. See method " +
//                "<removeJobFromVacancyList>.");
//    }


    public static int findVacantJob (int homeZone, int[] regions) {
        // select vacant job for person living in homeZone

        double[] regionProbability = new double[SiloUtil.getHighestVal(regions) + 1];

        if (homeZone > 0) {
            // person has home location (i.e., is not inmigrating right now)
            for (int reg: regions) {
                if (vacantJobsByRegionPos[reg] > 0) {
                    int distance = (int) (Accessibility.getMinDistanceFromZoneToRegion(homeZone, reg) + 0.5);
                    regionProbability[reg] = Accessibility.getWorkTLFD(distance) * (double) getNumberOfVacantJobsByRegion(reg);
                }
            }
            if (SiloUtil.getSum(regionProbability) == 0) {
                // could not find job in reasonable distance. Person will have to commute far and is likely to relocate in the future
                for (int reg: regions) {
                    if (vacantJobsByRegionPos[reg] > 0) {
                        int distance = (int) (Accessibility.getMinDistanceFromZoneToRegion(homeZone, reg) + 0.5);
                        regionProbability[reg] = 1f / distance;
                    }
                }
            }
        } else {
            // person has no home location because (s)he is inmigrating right now and a dwelling has not been chosen yet
            for (int reg: regions) {
                if (vacantJobsByRegionPos[reg] > 0) {
                    regionProbability[reg] = getNumberOfVacantJobsByRegion(reg);
                }
            }
        }

        if (SiloUtil.getSum(regionProbability) == 0) {
            logger.warn("No jobs remaining. Could not find new job.");
            return -1;
        }
        int selectedRegion = SiloUtil.select(regionProbability);
        float[] jobProbability = new float[getNumberOfVacantJobsByRegion(selectedRegion)];
        jobProbability = SiloUtil.setArrayToValue(jobProbability, 1);
        int selectedJob = SiloUtil.select(jobProbability);

        int jobId = vacantJobsByRegion[selectedRegion][selectedJob];
        vacantJobsByRegion[selectedRegion][selectedJob] = vacantJobsByRegion[selectedRegion][vacantJobsByRegionPos[selectedRegion] - 1];
        vacantJobsByRegion[selectedRegion][vacantJobsByRegionPos[selectedRegion] - 1] = 0;
        vacantJobsByRegionPos[selectedRegion] -= 1;
        if (jobId == SiloUtil.trackJj) SiloUtil.trackWriter.println("Removed job " + jobId + " from list of vacant jobs.");
        return jobId;
    }


    public void addJobToVacancyList(int zone, int jobId) {
        // add job jobId to vacancy list

        int region = geoData.getRegionOfZone(zone);
        vacantJobsByRegion[region][vacantJobsByRegionPos[region]] = jobId;
        if (vacantJobsByRegionPos[region] < numberOfStoredVacantJobs) vacantJobsByRegionPos[region]++;
        if (vacantJobsByRegionPos[region] >= numberOfStoredVacantJobs) IssueCounter.countExcessOfVacantJobs(region);
        if (jobId == SiloUtil.trackJj) SiloUtil.trackWriter.println("Added job " + jobId + " to list of vacant jobs.");
    }


    public void summarizeJobs(int[] regionList) {
        // summarize jobs for summary file

        String txt = "jobByRegion";
        for (String empType: JobType.getJobTypes()) txt += "," + empType;
        summarizeData.resultFile(txt + ",total");

        int[][] jobsByTypeAndRegion = new int[JobType.getNumberOfJobTypes()][SiloUtil.getHighestVal(regionList) + 1];
        for (Job job: Job.getJobArray()) {
            jobsByTypeAndRegion[JobType.getOrdinal(job.getType())][geoData.getRegionOfZone(job.getZone())]++;
        }

        for (int region: regionList) {
            StringBuilder line = new StringBuilder(String.valueOf(region));
            int regionSum = 0;
            for (String empType: JobType.getJobTypes()) {
                line.append(",").append(jobsByTypeAndRegion[JobType.getOrdinal(empType)][region]);
                regionSum += jobsByTypeAndRegion[JobType.getOrdinal(empType)][region];
            }
            summarizeData.resultFile(line + "," + regionSum);
        }
    }


    public void calculateJobDensityByZone() {
        zonalJobDensity = new float[geoData.getZones().length];
        for (Job jj: Job.getJobArray()) zonalJobDensity[geoData.getZoneIndex(jj.getZone())]++;
        for (int zone: geoData.getZones())
            zonalJobDensity[geoData.getZoneIndex(zone)] /= geoData.getSizeOfZoneInAcres(zone);
    }


    public float getJobDensityInZone(int zone) {
        return zonalJobDensity[geoData.getZoneIndex(zone)];
    }

    public int getJobDensityCategoryOfZone(int zone) {
        // return job density category 1 to 10 of zone

        float[] densityCategories = {0.f, 0.143f, 0.437f, 0.865f, 1.324f, 1.8778f, 2.664f, 3.99105f, 6.f, 12.7f};
        for (int i = 0; i < densityCategories.length; i++) {
            if (zonalJobDensity[geoData.getZoneIndex(zone)] < densityCategories[i]) return i;
        }
        return densityCategories.length;
    }
}
