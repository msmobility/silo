package de.tum.bgu.msm.events;

/**
 * Holds the names of available events
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 December 2009 in Santa Fe
**/

public enum EventTypes {

    // person events
    birthday,
    checkDeath,
    checkBirth,
    checkLeaveParentHh,
    checkMarriage,
    checkDivorce,
    checkSchoolUniv,
    checkDriversLicense,
    householdMove,
    inmigration,
    outMigration,
    // dwelling events
    ddChangeQual,
    ddDemolition,
    ddConstruction,
    // employment events
    findNewJob,
    quitJob
}
