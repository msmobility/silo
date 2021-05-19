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
package de.tum.bgu.msm.data.job;


import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.output.DefaultJobWriter;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.modules.JobDataProperties;
import de.tum.bgu.msm.simulator.UpdateListener;
import de.tum.bgu.msm.utils.SampleException;
import de.tum.bgu.msm.utils.Sampler;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Keeps data of dwellings and non-residential floorspace
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 22 February 2013 in Rhede
 **/
public class JobDataManagerImpl implements UpdateListener, JobDataManager {
    
    private final static Logger logger = Logger.getLogger(JobDataManagerImpl.class);

    private final GeoData geoData;
    private final Properties properties;
    private final JobFactory jobFactory;

    private final JobData jobData;
    private final TravelTimes travelTimes;
    private final CommutingTimeProbability commutingTimeProbability;

    private int highestJobIdInUse;

    private final Map<Integer, List<Job>> vacantJobsByRegion = new LinkedHashMap<>();
    private final Map<Integer, Double> zonalJobDensity;

    private final Map<Integer, Map<Integer,Map<String,Float>>> jobsByYearByZoneByIndustry = new ConcurrentHashMap<>();

    public JobDataManagerImpl(Properties properties,
                              JobFactory jobFactory, JobData jobData, GeoData geoData,
                              TravelTimes travelTimes, CommutingTimeProbability commutingTimeProbability) {
        this.geoData = geoData;
        this.properties = properties;
        this.jobFactory = jobFactory;
        this.jobData = jobData;
        this.travelTimes = travelTimes;
        this.commutingTimeProbability = commutingTimeProbability;
        this.zonalJobDensity = new HashMap<>();
    }

    @Override
    public void setup() {
        identifyHighestJobId();
        calculateEmploymentForecast();
        identifyVacantJobs(); //this step may be not needed - seem to be a duplicate of the method execution in the first "prepare year"
    }

    @Override
    public void prepareYear(int year) {
        calculateJobDensityByZone();
        identifyVacantJobs();
    }

    @Override
    public void endYear(int year) {
        if (!Properties.get().jobData.jobsIntermediatesFileName.equals("")) {
            final String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName +"/";
            String filejj = outputDirectory
                    + properties.jobData.jobsIntermediatesFileName
                    + "_"
                    + year + ".csv";
            new DefaultJobWriter(this.jobData.getJobs()).writeJobs(filejj);
        }
    }

    @Override
    public void endSimulation() {
        final String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName +"/";
        String filejj = outputDirectory
                + properties.jobData.jobsFinalFileName
                + "_"
                + properties.main.endYear + ".csv";
        new DefaultJobWriter(this.jobData.getJobs()).writeJobs(filejj);
    }

    @Override
    public Job getJobFromId(int jobId) {
        return jobData.get(jobId);
    }

    @Override
    public Collection<Job> getJobs() {
        return jobData.getJobs();
    }
    
    @Override
    public void removeJob(int id) {
        jobData.removeJob(id);
    }

    private void identifyHighestJobId() {
        highestJobIdInUse = 0;
        for (Job job: jobData.getJobs()) {
            highestJobIdInUse = Math.max(highestJobIdInUse, job.getId());
        }
    }

    @Override
    public int getNextJobId() {
        // increase highestJobIdInUse by 1 and return value
        return ++highestJobIdInUse;
    }

    @Override
    public List<Integer> getNextJobIds(int amount) {
        // increase highestJobIdInUse by 1 and return value
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            ids.add(++highestJobIdInUse);
        }
        return ids;
    }

    private void calculateEmploymentForecast() {
        if (properties.jobData.jobForecastMethod.equals(JobDataProperties.JobForecastMethod.INTERPOLATION)) {
            interpolateEmploymentForecast();
            logger.info("Forecasted jobs from employment forecast file");
        } else if (properties.jobData.jobForecastMethod.equals(JobDataProperties.JobForecastMethod.RATE)){
            calculateEmploymentForecastWithRate();
            logger.info("Forecasted jobs with growth rate");
        }

    }

    private void calculateEmploymentForecastWithRate() {
        int year = properties.main.startYear;
        Map<Integer, Map<String, Float>> jobCountBaseyear = new HashMap<>();
        jobsByYearByZoneByIndustry.put(year, jobCountBaseyear);
        //initialize maps with count = 0
        for (Zone zone : geoData.getZones().values()){
            Map<String, Float> jobsInThisZone = new HashMap<>();
            jobCountBaseyear.put(zone.getZoneId(), jobsInThisZone);
            for (String jobType : JobType.getJobTypes()){
                jobsInThisZone.put(jobType, 0.f);
            }
        }
        //count jobs in SP of base year
        for (Job job : jobData.getJobs()){
            int zoneId = job.getZoneId();
            String jobType = job.getType();
            jobCountBaseyear.get(zoneId).put(jobType, jobCountBaseyear.get(zoneId).get(jobType) + 1);
        }
        logger.info("Count of jobs in synthetic population of the base year completed");
        //forecast the following years
        year++;
        while (year <= properties.main.endYear){
            Map<Integer, Map<String, Float>> jobCountThisyear = new HashMap<>();
            jobsByYearByZoneByIndustry.put(year, jobCountThisyear);
            for (int zone : geoData.getZones().keySet()) {
                Map<String, Float> jobCountThisZone = new HashMap<>();
                for (String jobType : JobType.getJobTypes()){
                    jobCountThisZone.put(jobType, (float)(jobCountBaseyear.get(zone).get(jobType)*
                            Math.pow(1+properties.jobData.growthRateInPercentByJobType.get(jobType)/100,year - properties.main.startYear)));
                }
                jobCountThisyear.put(zone, jobCountThisZone);
            }
            year++;
        }
    }

    private void interpolateEmploymentForecast(){

        TableDataSet jobs;
        try {
            final String filename = properties.main.baseDirectory + "/" + properties.jobData.jobControlTotalsFileName;
            jobs = SiloUtil.readCSVfile(filename);
        } catch (Exception ee) {
            throw new RuntimeException(ee);
        }
        jobs.buildIndex(jobs.getColumnPosition("SMZ"));
        new JobType(properties.jobData.jobTypes);

        //read the headers
        String[] labels = jobs.getColumnLabels();
        String[] jobTypes = JobType.getJobTypes();
        List<String> years = new ArrayList<>();

        //find the years that are defined in the job forecast
        String jobTypeName = jobTypes[0];
        for (String label : labels) {
            if (label.contains(jobTypeName)) {
                String year = (label.substring(jobTypeName.length(), label.length()));
                if (!years.contains(year)) {
                    years.add(year);
                }
            }
        }
        //proof the rest of job types are in the file
        for (int i = 1; i < jobTypes.length; i++) {
            for (String year : years) {
                boolean found = false;
                for (String label : labels) {
                    if (label.equals(jobTypes[i] + year)) {
                        found = true;
                    }
                }
                if (!found) {
                    throw new RuntimeException("Not defined all job types for year " + year);
                }
            }
        }

        String[] yearsGiven = years.toArray(new String[0]);

        String dir = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/employmentForecast/";
        SiloUtil.createDirectoryIfNotExistingYet(dir);

            int previousFixedYear = Integer.parseInt(yearsGiven[0]);
            int nextFixedYear;
            int interpolatedYear = previousFixedYear;
            for (int i = 0; i < yearsGiven.length - 1; i++) {
                nextFixedYear = Integer.parseInt(yearsGiven[i + 1]);
                while (interpolatedYear <= nextFixedYear) {
                    Map<Integer, Map<String, Float>> jobsThisyear = new HashMap<>();
                    jobsByYearByZoneByIndustry.put(2000 + interpolatedYear, jobsThisyear);
                    final String forecastFileName = dir + properties.jobData.employmentForeCastFile + (2000 + interpolatedYear) + ".csv";
                    final PrintWriter pw = SiloUtil.openFileForSequentialWriting(forecastFileName, false);
                    final StringBuilder builder = new StringBuilder("zone");
                    for (String jobType : JobType.getJobTypes()) {
                        builder.append(",").append(jobType);
                    }
                    builder.append("\n");
                    for (int zone : geoData.getZones().keySet()) {
                        Map<String, Float> jobsThisZone = new HashMap<>();
                        jobsThisyear.put(zone, jobsThisZone);
                        builder.append(zone);
                        for (int jobTp = 0; jobTp < JobType.getNumberOfJobTypes(); jobTp++) {
                            final int index = jobs.getIndexedRowNumber(zone);
                            float currentValue;
                            if (interpolatedYear == previousFixedYear) {
                                //todo look at a different place if it is the base year!
                                currentValue = jobs.getValueAt(index, JobType.getJobType(jobTp) + yearsGiven[i]);
                            } else if (interpolatedYear == nextFixedYear) {
                                currentValue = jobs.getValueAt(index, JobType.getJobType(jobTp) + yearsGiven[i + 1]);
                            } else {
                                final float previousFixedValue = jobs.getValueAt(index, JobType.getJobType(jobTp) + yearsGiven[i]);
                                final float nextFixedValue = jobs.getValueAt(index, JobType.getJobType(jobTp) + yearsGiven[i + 1]);
                                currentValue = previousFixedValue + (nextFixedValue - previousFixedValue) * (interpolatedYear - previousFixedYear) /
                                        (nextFixedYear - previousFixedYear);
                            }

                            jobsThisZone.put(JobType.getJobType(jobTp), currentValue);
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
    

    @Override
    public float getJobForecast(int year, int zone, String jobType){
        return jobsByYearByZoneByIndustry.get(year).get(zone).get(jobType);
    }

    /**
     * identify vacant jobs by region (one-time task at beginning of model run only)
     */
    private void identifyVacantJobs() {
        logger.info("  Identifying vacant jobs");
        for (Job jj : jobData.getJobs()) {
            if (jj.getWorkerId() == -1) {
                int region = geoData.getZones().get(jj.getZoneId()).getRegion().getId();

                vacantJobsByRegion.putIfAbsent(region, new ArrayList<>());
                vacantJobsByRegion.get(region).add(jj);

                if (jj.getId() == SiloUtil.trackJj) {
                    SiloUtil.trackWriter.println("Added job " + jj.getId() + " to list of vacant jobs.");
                }
            }
        }
    }

    /**
     * Person quits job and the job is added to the vacantJobList
     * @param makeJobAvailableToOthers
     * @param person
     */
    @Override
    public void quitJob(boolean makeJobAvailableToOthers, Person person) {
        // <makeJobAvailableToOthers> is false if this job disappears from the job market
        if (person == null) {
            return;
        }
        final int workplace = person.getJobId();
        Job jb = jobData.get(workplace);
        if (makeJobAvailableToOthers) {
            addJobToVacancyList(jb);
        }
        jb.setWorkerID(-1);
        person.setWorkplace(-1);
        person.setOccupation(Occupation.UNEMPLOYED);
        person.setIncome((int) (person.getAnnualIncome() * 0.6 + 0.5));
        //todo: think about smarter retirement/social welfare algorithm to adjust income after employee leaves work.
    }
    
    private int getNumberOfVacantJobsByRegion(int region) {
        return vacantJobsByRegion.getOrDefault(region, Collections.EMPTY_LIST).size();
    }
    
    @Override
    public Job findVacantJob(Zone homeZone, Collection<Region> regions) {
        // select vacant job for person living in homeZone

        Sampler<Region> regionSampler = new Sampler<>(regions.size(), Region.class, SiloUtil.getRandomObject());

        if (homeZone != null) {
            // person has home location (i.e., is not inmigrating right now)
            for (Region reg : regions) {
                int numberOfVacantJobs = getNumberOfVacantJobsByRegion(reg.getId());
                if (numberOfVacantJobs > 0) {
                    int travelTime_min = (int) ((travelTimes.getTravelTimeToRegion(homeZone, reg,
                            properties.transportModel.peakHour_s, TransportMode.car) + 0.5));
                    //todo make region probability sensitve to mode choice to find a vacant job
                    final double prob = commutingTimeProbability.getCommutingTimeProbability(Math.max(1, travelTime_min), TransportMode.car) * (double) numberOfVacantJobs;
                    regionSampler.incrementalAdd(reg, prob);
                }
            }
            if (regionSampler.getCumulatedProbability() == 0) {
                // could not find job in reasonable distance. Person will have to commute far and is likely to relocate in the future
                for (Region reg : regions) {
                    if (getNumberOfVacantJobsByRegion(reg.getId()) > 0) {
                        int travelTime_min = (int) ((travelTimes.getTravelTimeToRegion(homeZone, reg,
                                properties.transportModel.peakHour_s, TransportMode.car) + 0.5));
                        final double prob = 1. / Math.max(1, travelTime_min);
                        regionSampler.incrementalAdd(reg, prob);
                    }
                }
            }
        } else {
            // person has no home location because (s)he is inmigrating right now and a dwelling has not been chosen yet
            for (Region reg : regions) {
                int numberOfJobs = getNumberOfVacantJobsByRegion(reg.getId());
                if (numberOfJobs > 0) {
                    regionSampler.incrementalAdd(reg, (double) numberOfJobs);
                }
            }
        }

        if (regionSampler.getCumulatedProbability() == 0) {
            logger.warn("No jobs remaining. Could not find new job.");
            return null;
        }
        int selectedRegion = -1;
        try {
            selectedRegion = regionSampler.sampleObject().getId();
        } catch (SampleException e) {
            e.printStackTrace();
        }

        List<Job> eligibleJobs = vacantJobsByRegion.get(selectedRegion);
        Job selectedJob = eligibleJobs.remove(SiloUtil.getRandomObject().nextInt(eligibleJobs.size()));

        if (selectedJob.getId() == SiloUtil.trackJj) {
            SiloUtil.trackWriter.println("Removed job " + selectedJob.getId() + " from list of vacant jobs.");
        }
        return selectedJob;
    }


    /**
     * add job jobId to vacancy list
     * @param job
     */
    private void addJobToVacancyList(Job job) {

        int region = geoData.getZones().get(job.getZoneId()).getRegion().getId();
        vacantJobsByRegion.putIfAbsent(region, new ArrayList<>());
        vacantJobsByRegion.get(region).add(job);

        if (job.getId() == SiloUtil.trackJj) {
            SiloUtil.trackWriter.println("Added job " + job.getId() + " to list of vacant jobs.");
        }
        }




    private void calculateJobDensityByZone() {
        final Multiset<Integer> counter = ConcurrentHashMultiset.create();
        jobData.getJobs().parallelStream().forEach(j -> counter.add(j.getZoneId()));
        geoData.getZones().forEach((id, zone) -> zonalJobDensity.put(id, (double) (counter.count(id) / zone.getArea_sqmi())));
    }


    @Override
    public double getJobDensityInZone(int zone) {
        return zonalJobDensity.get(zone);
    }

    @Override
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

    @Override
    public void addJob(Job jj) {
        this.jobData.addJob(jj);
    }

    @Override
    public JobFactory getFactory() {
        return jobFactory;
    }

    @Override
    public Map<Integer, List<Job>> getVacantJobsByRegion() {
        return vacantJobsByRegion;
    }
}
