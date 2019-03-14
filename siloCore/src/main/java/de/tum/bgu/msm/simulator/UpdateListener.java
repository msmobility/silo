package de.tum.bgu.msm.simulator;

/**
 *
 */
public interface UpdateListener {
    /**
     * //todo
     */
    void setup();

    /**
     * //TODO
     * @param year
     */
    void prepareYear(int year);

    /**
     * //TODO
     * @param year
     */
    void endYear(int year);

    /**
     * TODO
     */
    void endSimulation();
}
