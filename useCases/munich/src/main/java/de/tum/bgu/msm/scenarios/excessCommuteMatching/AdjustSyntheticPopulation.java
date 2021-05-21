package de.tum.bgu.msm.scenarios.excessCommuteMatching;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.io.HouseholdWriterMucDisability;
import de.tum.bgu.msm.io.JobWriterMuc;
import de.tum.bgu.msm.io.PersonWriterMuc;
import de.tum.bgu.msm.io.output.HouseholdWriter;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.utils.SiloUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class AdjustSyntheticPopulation {
    private final static double SCALE_FACTOR = 0.05;
    private final static long RANDOM_SEED = 1;
    private final static String path = "F:\\models\\muc/siloMuc.properties";
    private final static String matchBaseDirectory = "F:\\models\\msm-papers\\data\\thePerfectMatch";


    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(path);
        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
        DataBuilder.read(properties, dataContainer);

        scale(SCALE_FACTOR, dataContainer, RANDOM_SEED);
        new PersonWriterMuc(dataContainer.getHouseholdDataManager()).writePersons(matchBaseDirectory +"/ppOrigin_"+ SCALE_FACTOR + "_" + RANDOM_SEED + ".csv");
        new JobWriterMuc(dataContainer.getJobDataManager()).writeJobs(matchBaseDirectory + "/jjOrigin_" + SCALE_FACTOR + "_" + RANDOM_SEED + ".csv");
        new HouseholdWriterMucDisability(dataContainer.getHouseholdDataManager(),dataContainer.getRealEstateDataManager()).writeHouseholds(matchBaseDirectory + "/hhOrigin_" + SCALE_FACTOR + "_" + RANDOM_SEED + ".csv");


        List<Match> totalMatches = new ArrayList<>();
        for(String sector: JobType.getJobTypes()) {
            final List<Match> matches = readMatches(matchBaseDirectory, sector);
            totalMatches.addAll(matches);
        }

        for(Match match: totalMatches) {
            final Person personFromId = dataContainer.getHouseholdDataManager().getPersonFromId(match.workerId);
            final Job jobFromId = dataContainer.getJobDataManager().getJobFromId(match.jobId);
            personFromId.setWorkplace(jobFromId.getId());
            jobFromId.setWorkerID(personFromId.getId());
        }

        new PersonWriterMuc(dataContainer.getHouseholdDataManager()).writePersons(matchBaseDirectory +"/ppMatched_" + SCALE_FACTOR + "_" + RANDOM_SEED + ".csv");
        new JobWriterMuc(dataContainer.getJobDataManager()).writeJobs(matchBaseDirectory + "/jjMatched_" + SCALE_FACTOR + "_" + RANDOM_SEED + ".csv");
        new HouseholdWriterMucDisability(dataContainer.getHouseholdDataManager(),dataContainer.getRealEstateDataManager()).writeHouseholds(matchBaseDirectory + "/hhMatched_" + SCALE_FACTOR + "_" + RANDOM_SEED + ".csv");

    }

    private static List<Match> readMatches(String base, String sector) {
        List<Match> matches = new ArrayList<>();
        File file = new File(base +"/finalMatches"+ sector+"_" + SCALE_FACTOR + "_" + RANDOM_SEED + "_oneStep.csv");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            final String header = reader.readLine();
            String record = reader.readLine();
            while (record!= null) {
                final String[] split = record.split(",");
                int workerId = Integer.parseInt(split[0]);
                int jobId = Integer.parseInt(split[1]);
                matches.add(new Match(workerId, jobId));
                record = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matches;
    }

    private static class Match {
        private final int workerId;
        private final int jobId;

        public Match(int workerId, int jobId) {
            this.workerId = workerId;
            this.jobId = jobId;
        }
    }

    public static void scale(double scaleFactor, DataContainerWithSchools dataContainer, long seed) {
        Random random = new Random(seed);
        //scale households
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        Collection<Household> households = householdDataManager.getHouseholds();

        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        Collection<Job> jobs = jobDataManager.getJobs();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();

        for (Household hh : households) {
            if (random.nextDouble() > scaleFactor) {
                int dwellingId = hh.getDwellingId();
                for (Person person : hh.getPersons().values()) {
                    person.setHousehold(null);
                    householdDataManager.removePersonFromHousehold(person);
                    householdDataManager.removePerson(person.getId());
                    Job job = jobDataManager.getJobFromId(person.getJobId());
                    if (job != null){
                        jobDataManager.removeJob(job.getId());
                    }
                }
                householdDataManager.removeHousehold(hh.getId());
                realEstateDataManager.removeDwellingFromVacancyList(dwellingId);
                realEstateDataManager.removeDwelling(dwellingId);

            }
        }

        for (Job jj : jobs) {
            if (jj.getWorkerId() == -1) {
                if (random.nextDouble() > scaleFactor) {
                    jobDataManager.removeJob(jj.getId());
                }
            }
        }

    }

}
