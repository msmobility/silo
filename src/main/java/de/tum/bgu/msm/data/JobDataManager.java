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

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.jobTypes.maryland.MarylandJobType;
import de.tum.bgu.msm.data.jobTypes.munich.MunichJobType;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Keeps data of dwellings and non-residential floorspace
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 22 February 2013 in Rhede
 **/

public class JobDataManager {
    static Logger logger = Logger.getLogger(JobDataManager.class);

    private final GeoData geoData;
    private final Map<Integer, Job> jobs = new ConcurrentHashMap<>();

    private int highestJobIdInUse;
    private int[][] vacantJobsByRegion;
    private int[] vacantJobsByRegionPos;
    private int numberOfStoredVacantJobs;
    private final Map<Integer, Double> zonalJobDensity;


    public JobDataManager(SiloDataContainer dataContainer) {
        this.geoData = dataContainer.getGeoData();
        this.zonalJobDensity = new HashMap<>();
    }

    public Job createJob(int id, int zone, int workerId, String type) {
        Job job = new Job(id, zone, workerId, type);
        this.jobs.put(id, job);
        return job;
    }

    public Job getJobFromId(int jobId) {
        return jobs.get(jobId);
    }

    public void saveJobs (Job[] jjs) {
        for (Job jj: jjs) jobs.put(jj.getId(), jj);
    }

    public int getJobCount() {
        return jobs.size();
    }

    public Collection<Job> getJobs() {
        return jobs.values();
    }

    public Set<Integer> getJobMapIDs () {
        return jobs.keySet();
    }

    public void removeJob(int id) {
        jobs.remove(id);
    }

    public void fillMitoZoneEmployees(Map<Integer, MitoZone> zones) {

        for (Job jj : jobs.values()) {
            final MitoZone zone = zones.get(jj.getZone());
            final String type = jj.getType().toUpperCase();
            try {
                de.tum.bgu.msm.data.jobTypes.JobType mitoJobType = null;
                switch (Properties.get().main.implementation) {
                    case MARYLAND:
                        mitoJobType = MarylandJobType.valueOf(type);
                        break;
                    case MUNICH:
                        mitoJobType = MunichJobType.valueOf(type);
                        break;
                    default:
                        logger.error("Implementation " + Properties.get().main.implementation + " is not yet supported by MITO", new IllegalArgumentException());
                }
                zone.addEmployeeForType(mitoJobType);
            } catch (IllegalArgumentException e) {
                logger.warn("Job type " + type + " not defined for MITO implementation: " + Properties.get().main.implementation);
            }
        }
    }


    public void readJobs(Properties properties) {
        new JobType(properties.jobData.jobTypes);
        boolean readBin = properties.jobData.readBinaryJobFile;
        if (readBin) {
            readBinaryJobDataObjects();
        } else {
            readJobData(properties);
        }
        setHighestJobId();
    }


    private void readJobData(Properties properties) {
        logger.info("Reading job micro data from ascii file");

        int year = Properties.get().main.startYear;
        String fileName = properties.main.baseDirectory + properties.jobData.jobsFileName;
        fileName += "_" + year + ".csv";

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posZone = SiloUtil.findPositionInArray("zone", header);
            int posWorker = SiloUtil.findPositionInArray("personId", header);
            int posType = SiloUtil.findPositionInArray("type", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                int zone = Integer.parseInt(lineElements[posZone]);
                int worker = Integer.parseInt(lineElements[posWorker]);
                String type = lineElements[posType].replace("\"", "");
                createJob(id, zone, worker, type);
                if (id == SiloUtil.trackJj) {
                    SiloUtil.trackWriter.println("Read job with following attributes from " + fileName);
                    SiloUtil.trackWriter.println(jobs.get(id).toString());
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop job file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " jobs.");
    }


    public void writeBinaryJobDataObjects() {
        // Store job object data in binary file

        String fileName = Properties.get().main.baseDirectory + Properties.get().householdData.binaryJobFile;
        logger.info("  Writing job data to binary file.");
        Object[] data = jobs.values().toArray(new Job[]{});
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
        String fileName = Properties.get().main.baseDirectory + Properties.get().jobData.binaryJobsFileName;
        logger.info("Reading job data from binary file.");
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(fileName)));
            Object[] data = (Object[]) in.readObject();
            saveJobs((Job[]) data[0]);
        } catch (Exception e) {
            logger.error("Error reading from binary file " + fileName + ". Object not read.\n" + e);
        }
        logger.info("Finished reading " + jobs.size() + " jobs.");
    }


    public void setHighestJobId() {
        // identify highest job ID in use
        highestJobIdInUse = 0;
        for (int id : jobs.keySet()) {
            highestJobIdInUse = Math.max(highestJobIdInUse, id);
        }
    }


    public int getNextJobId() {
        // increase highestJobIdInUse by 1 and return value
        return ++highestJobIdInUse;
    }

    public List<Integer> getNextJobIds(int amount) {
        // increase highestJobIdInUse by 1 and return value
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            ids.add(++highestJobIdInUse);
        }
        return ids;
    }


    public void calculateEmploymentForecast() {

        // TODO Would it be better to make this adjustable rather than hardcoded? dz, apr/16
        String[] yearsGiven;
        if (Properties.get().jobData.hasControlYears) {
            int[] yearsInt = Properties.get().jobData.controlYears;
            yearsGiven = new String[yearsInt.length];
            for (int i = 0; i < yearsInt.length; i++) yearsGiven[i] = String.valueOf(yearsInt[i]);
        } else {
            yearsGiven = new String[]{"00", "07", "10", "30", "40"};  // Warning: if years are changed, also change interpolation loop below under "// interpolate employment data"
        }
        int highestYear = SiloUtil.getHighestVal(yearsGiven);
        int smallestYear = SiloUtil.getLowestVal(yearsGiven);

        logger.info("  Interpolating employment forecast for all years from " + (2000 + smallestYear) + " to " +
                (2000 + highestYear));
        TableDataSet jobs;
        try {
            final String filename = Properties.get().main.baseDirectory + "/" + Properties.get().jobData.jobControlTotalsFileName;
            jobs = SiloUtil.readCSVfile(filename);
        } catch (Exception ee) {
            throw new RuntimeException(ee);
        }
        jobs.buildIndex(jobs.getColumnPosition("SMZ"));
        new JobType(Properties.get().jobData.jobTypes);

        String dir = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName + "/employmentForecast/";
        SiloUtil.createDirectoryIfNotExistingYet(dir);

        int previousFixedYear = Integer.parseInt(yearsGiven[0]);
        int nextFixedYear;
        int interpolatedYear = previousFixedYear;
        for (int i = 0; i < yearsGiven.length - 1; i++) {
            nextFixedYear = Integer.parseInt(yearsGiven[i + 1]);
            while (interpolatedYear <= nextFixedYear) {
                final String forecastFileName = dir + Properties.get().jobData.employmentForeCastFile + (2000 + interpolatedYear) + ".csv";
                final PrintWriter pw = SiloUtil.openFileForSequentialWriting(forecastFileName, false);
                final StringBuilder builder = new StringBuilder("zone");
                for (String jobType : JobType.getJobTypes()) {
                    builder.append(",").append(jobType);
                }
                builder.append("\n");
                for (int zone : geoData.getZones().keySet()) {
                    builder.append(zone);
                    for (int jobTp = 0; jobTp < JobType.getNumberOfJobTypes(); jobTp++) {
                        final int index = jobs.getIndexedRowNumber(zone);
                        float currentValue;
                        if (interpolatedYear == previousFixedYear) {
                            currentValue = jobs.getValueAt(index, JobType.getJobType(jobTp) + yearsGiven[i]);
                        } else if (interpolatedYear == nextFixedYear) {
                            currentValue = jobs.getValueAt(index, JobType.getJobType(jobTp) + yearsGiven[i + 1]);
                        } else {
                            final float previousFixedValue = jobs.getValueAt(index, JobType.getJobType(jobTp) + yearsGiven[i]);
                            final float nextFixedValue = jobs.getValueAt(index, JobType.getJobType(jobTp) + yearsGiven[i + 1]);
                            currentValue = previousFixedValue + (nextFixedValue - previousFixedValue) * (interpolatedYear - previousFixedYear) /
                                    (nextFixedYear - previousFixedYear);
                        }
                        builder.append(",").append(currentValue);
                    }
                    builder.append("\n");
                }
                pw.print(builder.toString());
                pw.close();
                interpolatedYear++;
            }
            previousFixedYear = nextFixedYear;
        }
    }


    public void identifyVacantJobs() {
        // identify vacant jobs by region (one-time task at beginning of model run only)
        numberOfStoredVacantJobs = Properties.get().jobData.maxStorageOfvacantJobs;
        int highestRegionID = geoData.getRegions().keySet().stream().mapToInt(Integer::intValue).max().getAsInt();
        vacantJobsByRegion = new int[highestRegionID + 1][numberOfStoredVacantJobs + 1];
        vacantJobsByRegion = SiloUtil.setArrayToValue(vacantJobsByRegion, 0);
        vacantJobsByRegionPos = new int[highestRegionID + 1];
        vacantJobsByRegionPos = SiloUtil.setArrayToValue(vacantJobsByRegionPos, 0);

        logger.info("  Identifying vacant jobs");
        for (Job jj : jobs.values()) {
            if (jj.getWorkerId() == -1) {
                int jobId = jj.getId();

                int region = geoData.getZones().get(jj.getZone()).getRegion().getId();
                if (vacantJobsByRegionPos[region] < numberOfStoredVacantJobs) {
                    vacantJobsByRegion[region][vacantJobsByRegionPos[region]] = jobId;
                    vacantJobsByRegionPos[region]++;
                } else {
                    IssueCounter.countExcessOfVacantJobs(region);
                }
                if (jobId == SiloUtil.trackJj) {
                    SiloUtil.trackWriter.println("Added job " + jobId + " to list of vacant jobs.");
                }
            }
        }
    }

    public void quitJob (boolean makeJobAvailableToOthers, Person person) {
        // Person quits job and the job is added to the vacantJobList
        // <makeJobAvailableToOthers> is false if this job disappears from the job market
        if(person == null) {
            return;
        }
        final int workplace = person.getWorkplace();
        Job jb = jobs.get(workplace);
        if (makeJobAvailableToOthers) {
            addJobToVacancyList(jb.getZone(), workplace);
        }
        jb.setWorkerID(-1);
        person.setWorkplace(-1);
        person.setOccupation(2);
        person.setIncome((int) (person.getIncome() * 0.6 + 0.5));
        //todo: think about smarter retirement/social welfare algorithm to adjust income after employee leaves work.
    }


    public int getNumberOfVacantJobsByRegion(int region) {
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


    public int findVacantJob(int homeZone, Collection<Integer> regionIds, Accessibility accessibility) {
        // select vacant job for person living in homeZone

        double[] regionProbability = new double[SiloUtil.getHighestVal(regionIds.stream().mapToInt(Integer::intValue).toArray()) + 1];

        if (homeZone > 0) {
            // person has home location (i.e., is not inmigrating right now)
            for (int reg : regionIds) {
                if (vacantJobsByRegionPos[reg] > 0) {
                    int distance = (int) (accessibility.getMinTravelTimeFromZoneToRegion(homeZone, reg) + 0.5);
                    regionProbability[reg] = accessibility.getCommutingTimeProbability(distance) * (double) getNumberOfVacantJobsByRegion(reg);
                }
            }
            if (SiloUtil.getSum(regionProbability) == 0) {
                // could not find job in reasonable distance. Person will have to commute far and is likely to relocate in the future
                for (int reg : regionIds) {
                    if (vacantJobsByRegionPos[reg] > 0) {
                        int distance = (int) (accessibility.getMinTravelTimeFromZoneToRegion(homeZone, reg) + 0.5);
                        regionProbability[reg] = 1f / distance;
                    }
                }
            }
        } else {
            // person has no home location because (s)he is inmigrating right now and a dwelling has not been chosen yet
            for (int reg : regionIds) {
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
        if (getNumberOfVacantJobsByRegion(selectedRegion) == 0) {
            logger.warn("Selected region "+ selectedRegion + " but could not find any jobs there.");
            return -1;
        }
        float[] jobProbability = new float[getNumberOfVacantJobsByRegion(selectedRegion)];
        jobProbability = SiloUtil.setArrayToValue(jobProbability, 1);
        int selectedJob = SiloUtil.select(jobProbability);

        int jobId = vacantJobsByRegion[selectedRegion][selectedJob];
        vacantJobsByRegion[selectedRegion][selectedJob] = vacantJobsByRegion[selectedRegion][vacantJobsByRegionPos[selectedRegion] - 1];
        vacantJobsByRegion[selectedRegion][vacantJobsByRegionPos[selectedRegion] - 1] = 0;
        vacantJobsByRegionPos[selectedRegion] -= 1;
        if (jobId == SiloUtil.trackJj)
            SiloUtil.trackWriter.println("Removed job " + jobId + " from list of vacant jobs.");
        return jobId;
    }


    public void addJobToVacancyList(int zone, int jobId) {
        // add job jobId to vacancy list

        int region = geoData.getZones().get(zone).getRegion().getId();
        vacantJobsByRegion[region][vacantJobsByRegionPos[region]] = jobId;
        if (vacantJobsByRegionPos[region] < numberOfStoredVacantJobs) {
            vacantJobsByRegionPos[region]++;
        }
        if (vacantJobsByRegionPos[region] >= numberOfStoredVacantJobs) {
            IssueCounter.countExcessOfVacantJobs(region);
        }
        if (jobId == SiloUtil.trackJj) {
            SiloUtil.trackWriter.println("Added job " + jobId + " to list of vacant jobs.");
        }
    }


    public void summarizeJobs(Map<Integer, Region> regions) {
        // summarize jobs for summary file

        String txt = "jobByRegion";
        for (String empType : JobType.getJobTypes()) txt += "," + empType;
        SummarizeData.resultFile(txt + ",total");

        final int highestId = regions.keySet().stream().mapToInt(Integer::intValue).max().getAsInt();
        int[][] jobsByTypeAndRegion = new int[JobType.getNumberOfJobTypes()][highestId + 1];
        for (Job job : jobs.values()) {
            jobsByTypeAndRegion[JobType.getOrdinal(job.getType())][geoData.getZones().get(job.getZone()).getRegion().getId()]++;
        }

        for (int region : regions.keySet()) {
            StringBuilder line = new StringBuilder(String.valueOf(region));
            int regionSum = 0;
            for (String empType : JobType.getJobTypes()) {
                line.append(",").append(jobsByTypeAndRegion[JobType.getOrdinal(empType)][region]);
                regionSum += jobsByTypeAndRegion[JobType.getOrdinal(empType)][region];
            }
            SummarizeData.resultFile(line + "," + regionSum);
        }
    }


    public void calculateJobDensityByZone() {
        Multiset<Integer> counter = ConcurrentHashMultiset.create();
        jobs.values().parallelStream().forEach(j -> counter.add(j.getZone()));
        geoData.getZones().forEach((id, zone) -> zonalJobDensity.put(id, (double) (counter.count(id) / zone.getArea())));
    }


    public double getJobDensityInZone(int zone) {
        return zonalJobDensity.get(zone);
    }

    public int getJobDensityCategoryOfZone(int zone) {
        // return job density category 1 to 10 of zone
        //TODO: magic numbers
        float[] densityCategories = {0.f, 0.143f, 0.437f, 0.865f, 1.324f, 1.8778f, 2.664f, 3.99105f, 6.f, 12.7f};
        for (int i = 0; i < densityCategories.length; i++) {
            if (zonalJobDensity.get(zone) < densityCategories[i]) {
                return i;
            }
        }
        return densityCategories.length;
    }
}
