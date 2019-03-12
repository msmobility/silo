package de.tum.bgu.msm.simulator;

/**
 *
 */
public interface AnnualUpdate {
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
    void finishYear(int year);
}
