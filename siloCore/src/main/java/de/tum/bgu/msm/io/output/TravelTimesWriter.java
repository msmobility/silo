package de.tum.bgu.msm.io.output;

public interface TravelTimesWriter {
    /**
     *
     * @param path
     * @param name
     * @param mode
     */
    void writeTravelTimes(String path, String name, String mode);
}
