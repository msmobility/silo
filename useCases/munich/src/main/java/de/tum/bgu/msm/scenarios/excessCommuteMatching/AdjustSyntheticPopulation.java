package de.tum.bgu.msm.scenarios.excessCommuteMatching;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.io.JobWriterMuc;
import de.tum.bgu.msm.io.PersonWriterMuc;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.utils.SiloUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class AdjustSyntheticPopulation {

    public static void main(String[] args) {
        String path = "C:\\Users\\Nico\\tum\\fabilut\\gitproject\\muc/siloMuc.properties";
        String matchBaseDirectory = "C:\\Users\\Nico\\tum\\msm-papers\\data\\thePerfectMatch";

        Properties properties = SiloUtil.siloInitialization(path);
        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
        DataBuilder.read(properties, dataContainer);

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

        new PersonWriterMuc(dataContainer.getHouseholdDataManager()).writePersons(matchBaseDirectory +"/ppMatched.csv");
        new JobWriterMuc(dataContainer.getJobDataManager()).writeJobs(matchBaseDirectory + "/jjMatched.csv");

    }

    private static List<Match> readMatches(String base, String sector) {
        List<Match> matches = new ArrayList<>();
        File file = new File(base +"/finalMatches"+ sector+".csv");
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
}
