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
    public School createSchool(int id, int type, int capacity, int occupancy, Coordinate coordinate, int zoneId) {
        School school = new SchoolImpl(id, type, capacity, occupancy, coordinate, zoneId);

        //TODO: same as job? element and secondary school have same schoolTime, university has a distribution?
        if (Properties.get().main.implementation.equals(Implementation.MUNICH)) {
            //school.setSchoolStudyingTime();
        }

        return school;
    }


}
