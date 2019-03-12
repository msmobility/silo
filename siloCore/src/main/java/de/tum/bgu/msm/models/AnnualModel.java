package de.tum.bgu.msm.models;

public interface AnnualModel {

    /**
     *
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
