package de.tum.bgu.msm.data;

import java.util.Set;

public interface Zone {

    void setRegion(Region region);

    int getId();

    Region getRegion();

    int getMsa();

    float getArea();

    Set<Job> getJobs();

    boolean addJob(Job job);

    boolean removeJob(Job job);

    Set<Dwelling> getDwellings();

    boolean addDwelling(Dwelling dwelling);

    boolean removeDwelling(Dwelling dwelling);

    int getPopulation();

    double getZonalJobDensity();

    int getNumberOfJobsForType(String jt);
}
