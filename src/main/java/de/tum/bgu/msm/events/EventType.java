package de.tum.bgu.msm.events;

/**
 * Holds the names of available events
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 December 2009 in Santa Fe
**/

public enum EventType {

    // person events
    BIRTHDAY,
    DEATH,
    BIRTH,
    LEAVE_PARENTAL_HOUSEHOLD,
    MARRIAGE,
    DIVORCE,
    EDUCATION_UPDATE,
    DRIVERS_LICENSE_UPDATE,

    //household events
    HOUSEHOLD_MOVE,
    INMIGRATION,
    OUT_MIGRATION,

    // dwelling events
    DWELLING_RENOVATION,
    DWELLING_DEMOLITION,
    DWELLING_CONSTRUCTION,

    // employment events
    FIND_NEW_JOB,
    QUIT_JOB
}
