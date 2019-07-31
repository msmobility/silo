package de.tum.bgu.msm.schools;

import de.tum.bgu.msm.data.Location;

/**
 * School interface
 * @author QZhang
 **/
public interface School extends Location {

    int getId();

    int getType();

    int getCapacity();

    int getOccupancy();

    void setOccupancy(int occupancy);

    void setSchoolStudyingTime(double startTimeInSeconds, double studyTimeInSeconds);

    double getStartTimeInSeconds();

    double getStudyTimeInSeconds();
}
