package de.tum.bgu.msm.events;

import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.PersonRole;
import de.tum.bgu.msm.demography.BirthModel;
import de.tum.bgu.msm.demography.MarryDivorceModel;
import de.tum.bgu.msm.properties.Properties;

/**
 * Conditions that must be fulfilled for each event to be executed
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 January 2010 in Rhede
 **/

public class EventRules {

    private static boolean runEventAllDemography;
    private static boolean runEventBirthday;
    private static boolean runEventCheckDeath;
    private static boolean runEventCheckBirth;
    private static boolean runEventCheckLeaveParentHh;
    private static boolean runEventCheckMarriage;
    private static boolean runEventCheckDivorce;
    private static boolean runEventCheckSchoolUniv;
    private static boolean runEventCheckDriversLicense;
    private static boolean runEventStartNewJob;
    private static boolean runEventQuitJob;
    private static boolean runEventAllHhMoves;
    private static boolean runEventInmigration;
    private static boolean runEventOutMigration;
    private static boolean runEventAllDdDevelopments;
    private static boolean runEventDdChangeQual;
    private static boolean runEventDdDemolition;
    private static boolean runEventDdConstruction;

   public static void setUpEventRules () {
       runEventAllDemography       = Properties.get().eventRules.allDemography;
       runEventBirthday            = Properties.get().eventRules.birthday;
       runEventCheckDeath          = Properties.get().eventRules.death;
       runEventCheckBirth          = Properties.get().eventRules.birth;
       runEventCheckLeaveParentHh  = Properties.get().eventRules.leaveParentHh;
       runEventCheckMarriage       = Properties.get().eventRules.marriage;
       runEventCheckDivorce        = Properties.get().eventRules.divorce;
       runEventCheckSchoolUniv     = Properties.get().eventRules.schoolUniversity;
       runEventCheckDriversLicense = Properties.get().eventRules.driversLicense;
       runEventStartNewJob         = Properties.get().eventRules.startNewJob;
       runEventQuitJob             = Properties.get().eventRules.quitJob;
       runEventAllHhMoves          = Properties.get().eventRules.allHhMoves;
       runEventInmigration         = Properties.get().eventRules.inmigration;
       runEventOutMigration        = Properties.get().eventRules.outMigration;
       runEventAllDdDevelopments   = Properties.get().eventRules.allDwellingDevelopments;
       runEventDdChangeQual        = Properties.get().eventRules.dwellingChangeQuality;
       runEventDdDemolition        = Properties.get().eventRules.dwellingDemolition;
       runEventDdConstruction      = Properties.get().eventRules.dwellingConstruction;
    }

    // Conditions for person events
    public static boolean ruleBirthday (Person per) {
        return per != null && runEventBirthday && runEventAllDemography;
    }

    public static boolean ruleDeath (Person per) {
        return per != null && runEventCheckDeath && runEventAllDemography;
    }

    public static boolean ruleGiveBirth (Person per) {
        return (per != null && per.getGender() == 2 && BirthModel.personCanGiveBirth(per.getType())) &&
                runEventCheckBirth && runEventAllDemography;
    }

    public static boolean ruleLeaveParHousehold (Person per) {
        if (per == null) return false;
        Household hh = per.getHh();
        return (hh.getHhSize() >= 2 && per.getRole() == PersonRole.child) &&
                runEventCheckLeaveParentHh && runEventAllDemography;
    }

    public static boolean ruleGetMarried (Person per) {
        if (per == null) return false;
        PersonRole role = per.getRole();
        return (role == PersonRole.single || role == PersonRole.child) && per.getAge() >= MarryDivorceModel.getMinMarryAge() &&
                runEventCheckMarriage && runEventAllDemography;
    }

    public static boolean ruleGetDivorced (Person per) {
        return (per != null && per.getRole() == PersonRole.married) && runEventCheckDivorce && runEventAllDemography;
    }

    public static boolean ruleChangeSchoolUniversity (Person per) {
        return (per != null) && runEventCheckSchoolUniv && runEventAllDemography;
    }

    public static boolean ruleChangeDriversLicense (Person per) {
        return (per != null) && runEventCheckDriversLicense && runEventAllDemography;
    }

    // Conditions for change-of-job events
    public static boolean ruleStartNewJob() {
        return runEventStartNewJob && runEventAllDemography;
    }

    public static boolean ruleQuitJob() {
        return runEventQuitJob && runEventAllDemography;
    }

    // Conditions for household events
    public static boolean ruleHouseholdMove (Household hh) {
        return (hh != null) && runEventAllHhMoves;
    }

    public static boolean ruleOutmigrate (Household hh) {
        return (hh != null) && runEventOutMigration && runEventAllHhMoves;
    }

    public static boolean ruleOutmigrate () {
        return runEventOutMigration && runEventAllHhMoves;
    }

    public static boolean ruleInmigrate() {
        return runEventInmigration && runEventAllHhMoves;
    }

    // Conditions for dwelling events
    public static boolean ruleChangeDwellingQuality (Dwelling dd) {
        return (dd != null) && runEventDdChangeQual && runEventAllDdDevelopments;
    }

    public static boolean ruleDemolishDwelling (Dwelling dd) {
        return (dd != null) && runEventDdDemolition && runEventAllDdDevelopments;
    }

    public static boolean ruleBuildDwelling () {
        return runEventDdConstruction && runEventAllDdDevelopments;
    }

    public static boolean runMarriages () {
        return runEventCheckMarriage;
    }
}
