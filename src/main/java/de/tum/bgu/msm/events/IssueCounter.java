package de.tum.bgu.msm.events;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.GeoData;
import org.apache.log4j.Logger;

/**
 * Keep track of cases that are undesirable
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 3 December 2012 in Santa Fe
 **/

public class IssueCounter {

    static Logger logger = Logger.getLogger(IssueCounter.class);
    private static int lackOfDwellingFailedMarriage;
    private static int lackOfDwellingFailedLeavingChild;
    private static int lackOfDwellingFailedDivorce;
    private static int lackOfDwellingFailedInmigration;
    private static int forcedOutmigrationByDemolition;
    private static int[] exceededVacantDwellingStorage;
    private static int[] exceededVacantJobStorage;
    private static int missingJob;
    private static boolean foundIssues = false;


    public static void setUpCounter() {
        // set counter to 0
        lackOfDwellingFailedMarriage = 0;
        lackOfDwellingFailedLeavingChild = 0;
        lackOfDwellingFailedDivorce = 0;
        lackOfDwellingFailedInmigration = 0;
        forcedOutmigrationByDemolition = 0;
        missingJob = 0;
    }


    public static void regionSpecificCounters(GeoData geoData) {
        exceededVacantDwellingStorage = new int[SiloUtil.getHighestVal(geoData.getRegionIdsArray()) + 1];
        exceededVacantJobStorage = new int[SiloUtil.getHighestVal(geoData.getRegionIdsArray()) + 1];
        SiloUtil.setArrayToValue(exceededVacantDwellingStorage, 0);
        SiloUtil.setArrayToValue(exceededVacantJobStorage, 0);
    }


    public static void countLackOfDwellingFailedMarriage () {
        lackOfDwellingFailedMarriage++;
        foundIssues = true;
    }

    public static void countLackOfDwellingFailedLeavingChild() {
        lackOfDwellingFailedLeavingChild++;
        foundIssues = true;
    }

    public static void countLackOfDwellingFailedDivorce() {
        lackOfDwellingFailedDivorce++;
        foundIssues = true;
    }

    public static void countLackOfDwellingFailedInmigration() {
        lackOfDwellingFailedInmigration++;
        foundIssues = true;
    }

    public static void countMissingJob() {
        missingJob++;
        foundIssues = true;
    }


    public static void countLackOfDwellingForcedOutmigration() {
        forcedOutmigrationByDemolition++;
        foundIssues = true;
    }


    public static void countExcessOfVacantDwellings(int region) {
        exceededVacantDwellingStorage[region]++;
        foundIssues = true;
    }


    public static void countExcessOfVacantJobs(int region) {
        exceededVacantJobStorage[region]++;
        foundIssues = true;
    }


    public static boolean didFindIssues() {
        return foundIssues;
    }

    public static void logIssues (GeoData geoData) {
        // log found issues
        if (lackOfDwellingFailedDivorce > 0) logger.warn("  Encountered " + lackOfDwellingFailedDivorce + " cases where " +
                "couple wanted to get divorced but could not find vacant dwelling.");
        if (lackOfDwellingFailedInmigration > 0) logger.warn("  Encountered " + lackOfDwellingFailedInmigration + " cases " +
                "where household wanted to inmigrate but could not find vacant dwelling.");
        if (lackOfDwellingFailedLeavingChild > 0) logger.warn("  Encountered " + lackOfDwellingFailedLeavingChild + " cases " +
                "where child wanted to leave parental household but could not find vacant dwelling.");
        if (lackOfDwellingFailedMarriage > 0) logger.warn("  Encountered " + lackOfDwellingFailedMarriage + " cases " +
                "where a couple wanted to marry (cohabitate) but could not find vacant dwelling.");
        if (forcedOutmigrationByDemolition > 0) logger.warn("  Encountered " + forcedOutmigrationByDemolition + " cases " +
                "where a household had to outmigrate because its dwelling was demolished and no other vacant dwelling could be found.");
        if (SiloUtil.getSum(exceededVacantDwellingStorage) > 0) {
            logger.warn("  Could not store all vacant dwellings in vacDwellingsByRegion[][] for regions:");
            for (int region: geoData.getRegions().keySet()) {
                if (exceededVacantDwellingStorage[region] > 0) {
                    logger.warn("  - Region " + region + ": " + exceededVacantDwellingStorage[region] + " vacant dwellings");
                }
            }
        }
        if (SiloUtil.getSum(exceededVacantJobStorage) > 0) {
            logger.warn("  Could not store all vacant jobs in vacantJobsByRegion[][] for regions:");
            for (int region: geoData.getRegions().keySet()) {
                if (exceededVacantJobStorage[region] > 0) {
                    logger.warn("  - Region " + region + ": " + exceededVacantJobStorage[region] + " vacant jobs");
                }
            }
        }
        if (missingJob > 0) {
            logger.warn("  Encountered " + missingJob + " cases where a person should have started a " +
                    "new job to keep constant labor participation rates but could not find a job.");
        }
    }
}