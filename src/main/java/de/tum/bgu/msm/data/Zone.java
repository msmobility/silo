package de.tum.bgu.msm.data;

public interface Zone extends Id{

    void setRegion(Region region);

    Region getRegion();

    int getMsa();

    float getArea();

}
