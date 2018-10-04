package de.tum.bgu.msm.data.school;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.properties.Properties;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SchoolFactoryImpl implements SchoolFactory {
    private final Map<String, Map<Integer,Double>> startTimeDistributionBySchoolType = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer,Double>> studyTimeDistributionBySchoolType = new ConcurrentHashMap<>();
    private int intervalInSecondsForPreferredTimes;

    @Override
    public School createSchool(int id, int type, int capacity, Coordinate coordinate, int zoneId) {
        School school = new SchoolImpl(id, type, capacity, coordinate, zoneId);
        if (Properties.get().main.implementation.equals(Implementation.MUNICH)) {
            school.setSchoolStudyingTime(SiloUtil.select(startTimeDistributionBySchoolType.get(type)) +
                            intervalInSecondsForPreferredTimes * SiloUtil.getRandomNumberAsDouble(),
                    SiloUtil.select(studyTimeDistributionBySchoolType.get(type)) +
                            intervalInSecondsForPreferredTimes * SiloUtil.getRandomNumberAsDouble());
        }
        //this.schools.put(id, school);
        return school;
    }


    public void readStudyTimeDistributions(Properties properties) {
//        String fileNameStart = properties.jobData.jobStartTimeDistributionFile;
//        String recString = "";
//        BufferedReader in = null;
//        try {
//            in = new BufferedReader(new FileReader(fileNameStart));
//            recString = in.readLine();
//            String[] header = recString.split(",");
//            while ((recString = in.readLine()) != null) {
//                String[] row = recString.split(",");
//                int time = Integer.parseInt(row[0]);
//                for (int column = 1; column < header.length; column++) {
//                    if (startTimeDistributionByJobType.containsKey(header[column])) {
//                        startTimeDistributionByJobType.get(header[column]).put(time, Double.parseDouble(row[column]));
//                    } else {
//                        Map<Integer, Double> startTimeDistribution = new HashMap<>();
//                        startTimeDistribution.put(time, Double.parseDouble(row[column]));
//                        startTimeDistributionByJobType.put(header[column], startTimeDistribution);
//                    }
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String fileNameDuration = properties.jobData.jobDurationDistributionFile;
//        try {
//            in = new BufferedReader(new FileReader(fileNameDuration));
//            recString = in.readLine();
//            String[] header = recString.split(",");
//            while ((recString = in.readLine()) != null) {
//                String[] row = recString.split(",");
//                int time = Integer.parseInt(row[0]);
//                for (int column = 1; column < header.length; column++) {
//                    if (workingTimeDistributionByJobType.containsKey(header[column])) {
//                        workingTimeDistributionByJobType.get(header[column]).put(time, Double.parseDouble(row[column]));
//                    } else {
//                        Map<Integer, Double> workingTimeDistribution = new HashMap<>();
//                        workingTimeDistribution.put(time, Double.parseDouble(row[column]));
//                        workingTimeDistributionByJobType.put(header[column], workingTimeDistribution);
//                    }
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        intervalInSecondsForPreferredTimes = Math.round(24 * 3600 / startTimeDistributionByJobType.get(JobType.getJobType(0)).size());

    }
}
