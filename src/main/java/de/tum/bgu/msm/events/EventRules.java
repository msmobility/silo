package de.tum.bgu.msm.events;

import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.PersonRole;
import de.tum.bgu.msm.demography.BirthModel;
import de.tum.bgu.msm.demography.MarryDivorceModel;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.properties.Properties;

import java.util.ResourceBundle;

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

   public static void setUpEventRules (Properties properties) {

       runEventAllDemography       = properties.getEventRulesProperties().isAllDemography();
       runEventBirthday            = properties.getEventRulesProperties().isBirthday();
       runEventCheckDeath          = properties.getEventRulesProperties().isDeath();
       runEventCheckBirth          = properties.getEventRulesProperties().isBirth();
       runEventCheckLeaveParentHh  = properties.getEventRulesProperties().isLeaveParentHh();
       runEventCheckMarriage       = properties.getEventRulesProperties().isMarriage();
       runEventCheckDivorce        = properties.getEventRulesProperties().isDivorce();
       runEventCheckSchoolUniv     = properties.getEventRulesProperties().isSchoolUniversity();
       runEventCheckDriversLicense = properties.getEventRulesProperties().isDriversLicense();
       runEventStartNewJob         = properties.getEventRulesProperties().isStartNewJob();
       runEventQuitJob             = properties.getEventRulesProperties().isQuitJob();
       runEventAllHhMoves          = properties.getEventRulesProperties().isAllHhMoves();
       runEventInmigration         = properties.getEventRulesProperties().isInmigration();
       runEventOutMigration        = properties.getEventRulesProperties().isOutMigration();
       runEventAllDdDevelopments   = properties.getEventRulesProperties().isAllDwellingDevelopments();
       runEventDdChangeQual        = properties.getEventRulesProperties().isDwellingChangeQuality();
       runEventDdDemolition        = properties.getEventRulesProperties().isDwellingDemolition();
       runEventDdConstruction      = properties.getEventRulesProperties().isDwellingConstruction();
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
        Household hh = Household.getHouseholdFromId(per.getHhId());
        if(hh == null) {
            System.out.println("null!");
        }
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
