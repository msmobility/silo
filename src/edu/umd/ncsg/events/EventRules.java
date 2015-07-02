package edu.umd.ncsg.events;

import edu.umd.ncsg.data.Person;
import edu.umd.ncsg.data.Household;
import edu.umd.ncsg.data.PersonRole;
import edu.umd.ncsg.data.Dwelling;
import edu.umd.ncsg.demography.BirthModel;
import edu.umd.ncsg.demography.MarryDivorceModel;
import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

/**
 * Conditions that must be fulfilled for each event to be executed
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 January 2010 in Rhede
 **/

public class EventRules {

    protected static final String PROPERTIES_event_allDemography      = "event.all.demography";
    protected static final String PROPERTIES_event_birthday           = "event.birthday";
    protected static final String PROPERTIES_event_checkDeath         = "event.checkDeath";
    protected static final String PROPERTIES_event_checkBirth         = "event.checkBirth";
    protected static final String PROPERTIES_event_checkLeaveParentHh = "event.checkLeaveParentHh";
    protected static final String PROPERTIES_event_checkMarriage      = "event.checkMarriage";
    protected static final String PROPERTIES_event_checkDivorce       = "event.checkDivorce";

    protected static final String PROPERTIES_event_startNewJob        = "event.startJob";
    protected static final String PROPERTIES_event_quitJob            = "event.quitJob";

    protected static final String PROPERTIES_event_allHhMoves         = "event.all.hhMoves";
    protected static final String PROPERTIES_event_inmigration        = "event.inmigration";
    protected static final String PROPERTIES_event_outMigration       = "event.outMigration";

    protected static final String PROPERTIES_event_allDdDevelopments  = "event.all.developers";
    protected static final String PROPERTIES_event_ddChangeQual       = "event.ddChangeQual";
    protected static final String PROPERTIES_event_ddDemolition       = "event.ddDemolition";
    protected static final String PROPERTIES_event_ddConstruction     = "event.ddConstruction";

    private static boolean runEventAllDemography;
    private static boolean runEventBirthday;
    private static boolean runEventCheckDeath;
    private static boolean runEventCheckBirth;
    private static boolean runEventCheckLeaveParentHh;
    private static boolean runEventCheckMarriage;
    private static boolean runEventCheckDivorce;
    private static boolean runEventStartNewJob;
    private static boolean runEventQuitJob;
    private static boolean runEventAllHhMoves;
    private static boolean runEventInmigration;
    private static boolean runEventOutMigration;
    private static boolean runEventAllDdDevelopments;
    private static boolean runEventDdChangeQual;
    private static boolean runEventDdDemolition;
    private static boolean runEventDdConstruction;

   public static void setUpEventRules (ResourceBundle rb) {

       runEventAllDemography      = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_allDemography);
       runEventBirthday           = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_birthday);
       runEventCheckDeath         = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_checkDeath);
       runEventCheckBirth         = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_checkBirth);
       runEventCheckLeaveParentHh = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_checkLeaveParentHh);
       runEventCheckMarriage      = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_checkMarriage);
       runEventCheckDivorce       = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_checkDivorce);
       runEventStartNewJob        = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_startNewJob);
       runEventQuitJob            = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_quitJob);
       runEventAllHhMoves         = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_allHhMoves);
       runEventInmigration        = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_inmigration);
       runEventOutMigration       = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_outMigration);
       runEventAllDdDevelopments  = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_allDdDevelopments);
       runEventDdChangeQual       = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_ddChangeQual);
       runEventDdDemolition       = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_ddDemolition);
       runEventDdConstruction     = ResourceUtil.getBooleanProperty(rb, PROPERTIES_event_ddConstruction);
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
