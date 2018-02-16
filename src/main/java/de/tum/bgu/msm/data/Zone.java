package de.tum.bgu.msm.data;

public interface Zone extends Id{

    void setRegion(Region region);

    Region getRegion();

    /**
     * @return the zone's metropolitan statistical area
     */
    int getMsa();

    /**
     * @return the area of the zone in acres
     */
    float getArea();

}
