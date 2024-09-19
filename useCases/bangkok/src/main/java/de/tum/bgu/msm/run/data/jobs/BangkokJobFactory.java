package de.tum.bgu.msm.run.data.jobs;

import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobFactory;
import de.tum.bgu.msm.data.job.JobImpl;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BangkokJobFactory implements JobFactory {

    private final Map<String, Map<Integer,Double>> startTimeDistributionByJobType = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer,Double>> workingTimeDistributionByJobType = new ConcurrentHashMap<>();
    private int intervalInSecondsForPreferredTimes;

    @Override
    public Job createJob(int id, int zoneId, Coordinate coordinate, int workerId, String type) {
        final BangkokJob bangkokJob = new BangkokJob(id, zoneId, coordinate, workerId, type);
        bangkokJob.setJobWorkingTime((int) (SiloUtil.select(startTimeDistributionByJobType.get(bangkokJob.getType())) +
                        intervalInSecondsForPreferredTimes * SiloUtil.getRandomNumberAsDouble()),
                (int) (SiloUtil.select(workingTimeDistributionByJobType.get(bangkokJob.getType())) +
                        intervalInSecondsForPreferredTimes * SiloUtil.getRandomNumberAsDouble()));
        return bangkokJob;
    }


    public void readWorkingTimeDistributions(Properties properties) {
        String fileNameStart = properties.main.baseDirectory + properties.jobData.jobStartTimeDistributionFile;
        String recString = "";
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(fileNameStart));
            recString = in.readLine();
            String[] header = recString.split(",");
            while ((recString = in.readLine()) != null) {
                String[] row = recString.split(",");
                int time = Integer.parseInt(row[0]);
                for (int column = 1; column < header.length; column++) {
                    if (startTimeDistributionByJobType.containsKey(header[column])) {
                        startTimeDistributionByJobType.get(header[column]).put(time, Double.parseDouble(row[column]));
                    } else {
                        Map<Integer, Double> startTimeDistribution = new HashMap<>();
                        startTimeDistribution.put(time, Double.parseDouble(row[column]));
                        startTimeDistributionByJobType.put(header[column], startTimeDistribution);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileNameDuration = properties.main.baseDirectory + properties.jobData.jobDurationDistributionFile;
        try {
            in = new BufferedReader(new FileReader(fileNameDuration));
            recString = in.readLine();
            String[] header = recString.split(",");
            while ((recString = in.readLine()) != null) {
                String[] row = recString.split(",");
                int time = Integer.parseInt(row[0]);
                for (int column = 1; column < header.length; column++) {
                    if (workingTimeDistributionByJobType.containsKey(header[column])) {
                        workingTimeDistributionByJobType.get(header[column]).put(time, Double.parseDouble(row[column]));
                    } else {
                        Map<Integer, Double> workingTimeDistribution = new HashMap<>();
                        workingTimeDistribution.put(time, Double.parseDouble(row[column]));
                        workingTimeDistributionByJobType.put(header[column], workingTimeDistribution);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        intervalInSecondsForPreferredTimes = Math.round(24 * 3600 / startTimeDistributionByJobType.get(JobType.getJobType(0)).size());

    }

}
