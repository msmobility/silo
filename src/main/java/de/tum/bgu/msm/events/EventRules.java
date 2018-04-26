package de.tum.bgu.msm.events;

import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.PersonRole;
import de.tum.bgu.msm.models.demography.BirthModel;
import de.tum.bgu.msm.properties.Properties;

/**
 * Conditions that must be fulfilled for each event to be executed
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 January 2010 in Rhede
 **/

public class EventRules {

    private EventRules(){}

    // Conditions for person events
    public static boolean ruleBirthday (Person per) {
        return per != null && Properties.get().eventRules.birthday && Properties.get().eventRules.allDemography;
    }

    public static boolean ruleDeath (Person per) {
        return per != null && Properties.get().eventRules.death && Properties.get().eventRules.allDemography;
    }

    public static boolean ruleGiveBirth (Person per) {
        return (per != null && per.getGender() == 2 && BirthModel.personCanGiveBirth(per.getAge())) &&
                Properties.get().eventRules.birth && Properties.get().eventRules.allDemography;
    }

    public static boolean ruleLeaveParHousehold (Person per) {
        if (per == null) return false;
        Household hh = per.getHh();
        return (hh.getHhSize() >= 2 && per.getRole() == PersonRole.CHILD) &&
                Properties.get().eventRules.leaveParentHh && Properties.get().eventRules.allDemography;
    }

    public static boolean ruleGetMarried (Person per) {
        if (per == null) return false;
        PersonRole role = per.getRole();
        return (role == PersonRole.SINGLE || role == PersonRole.CHILD)
                && per.getAge() >= Properties.get().demographics.minMarryAge
                && Properties.get().eventRules.marriage
                && Properties.get().eventRules.allDemography
                && per.getAge() < 100;
    }

    public static boolean ruleGetDivorced (Person per) {
        return (per != null && per.getRole() == PersonRole.MARRIED) && Properties.get().eventRules.divorce && Properties.get().eventRules.allDemography;
    }

    public static boolean ruleChangeSchoolUniversity (Person per) {
        return (per != null) && Properties.get().eventRules.schoolUniversity && Properties.get().eventRules.allDemography;
    }

    public static boolean ruleChangeDriversLicense (Person per) {
        return (per != null) && Properties.get().eventRules.driversLicense && Properties.get().eventRules.allDemography;
    }

    // Conditions for change-of-job events
    public static boolean ruleStartNewJob() {
        return Properties.get().eventRules.startNewJob && Properties.get().eventRules.allDemography;
    }

    public static boolean ruleQuitJob() {
        return Properties.get().eventRules.quitJob && Properties.get().eventRules.allDemography;
    }

    // Conditions for household events
    public static boolean ruleHouseholdMove (Household hh) {
        return (hh != null) &&  Properties.get().eventRules.allHhMoves;
    }

    public static boolean ruleOutmigrate (Household hh) {
        return (hh != null) && Properties.get().eventRules.outMigration &&  Properties.get().eventRules.allHhMoves;
    }

    public static boolean ruleOutmigrate () {
        return Properties.get().eventRules.outMigration &&  Properties.get().eventRules.allHhMoves;
    }

    public static boolean ruleInmigrate() {
        return Properties.get().eventRules.inmigration &&  Properties.get().eventRules.allHhMoves;
    }

    // Conditions for dwelling events
    public static boolean ruleChangeDwellingQuality (Dwelling dd) {
        return (dd != null) && Properties.get().eventRules.dwellingChangeQuality && Properties.get().eventRules.allDwellingDevelopments;
    }

    public static boolean ruleDemolishDwelling (Dwelling dd) {
        return (dd != null) && Properties.get().eventRules.dwellingDemolition && Properties.get().eventRules.allDwellingDevelopments;
    }

    public static boolean ruleBuildDwelling () {
        return Properties.get().eventRules.dwellingConstruction && Properties.get().eventRules.allDwellingDevelopments;
    }

    public static boolean runMarriages () {
        return Properties.get().eventRules.marriage ;
    }
}
