package de.tum.bgu.msm.events;

/**
 * Holds the names of available events
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 December 2009 in Santa Fe
**/

public enum EventTypes {

    // person events
    BIRTHDAY,
    CHECK_DEATH,
    CHECK_BIRTH,
    CHECK_LEAVE_PARENT_HH,
    CHECK_MARRIAGE,
    CHECK_DIVORCE,
    CHECK_SCHOOL_UNIV,
    CHECK_DRIVERS_LICENSE,
    HOUSEHOLD_MOVE,
    INMIGRATION,
    OUT_MIGRATION,
    // dwelling events
    DD_CHANGE_QUAL,
    DD_DEMOLITION,
    DD_CONSTRUCTION,
    // employment events
    FIND_NEW_JOB,
    QUIT_JOB
}
