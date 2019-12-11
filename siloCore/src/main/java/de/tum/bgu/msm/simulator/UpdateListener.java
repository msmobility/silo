package de.tum.bgu.msm.simulator;

/**
 *
 */
public interface UpdateListener {
    /**
     */
    void setup();

    /**
     * @param year
     */
    void prepareYear(int year);

    /**
     * @param year
     */
    void endYear(int year);

    /**
     */
    void endSimulation();

}
