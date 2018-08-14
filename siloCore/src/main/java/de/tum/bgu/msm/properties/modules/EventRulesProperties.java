package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.ResourceBundle;

public class EventRulesProperties {

    public final boolean allDemography;
    public final boolean birthday;
    public final boolean death;
    public final boolean birth;
    public final boolean leaveParentHh;
    public final boolean marriage;
    public final boolean divorce;
    public final boolean schoolUniversity;
    public final boolean driversLicense;
    public final boolean startNewJob;
    public final boolean quitJob;
    public final boolean allHhMoves;
    public final boolean inmigration;
    public final boolean outMigration;
    public final boolean allDwellingDevelopments;
    public final boolean dwellingChangeQuality;
    public final boolean dwellingDemolition;
    public final boolean dwellingConstruction;

    public EventRulesProperties(ResourceBundle bundle) {
        PropertiesUtil.newPropertySubmodule("Event rule properties");
        allDemography = PropertiesUtil.getBooleanProperty(bundle, "event.all.demography", true);
        birthday = PropertiesUtil.getBooleanProperty(bundle, "event.birthday", true);
        death = PropertiesUtil.getBooleanProperty(bundle, "event.checkDeath", true);
        birth = PropertiesUtil.getBooleanProperty(bundle, "event.checkBirth", true);
        leaveParentHh = PropertiesUtil.getBooleanProperty(bundle, "event.checkLeaveParentHh", true);
        marriage = PropertiesUtil.getBooleanProperty(bundle, "event.checkMarriage", true);
        divorce = PropertiesUtil.getBooleanProperty(bundle, "event.checkDivorce", true);
        schoolUniversity = PropertiesUtil.getBooleanProperty(bundle, "event.checkSchoolUniv", true);
        driversLicense = PropertiesUtil.getBooleanProperty(bundle, "event.checkDriversLicense", true);
        startNewJob = PropertiesUtil.getBooleanProperty(bundle, "event.startJob", true);
        quitJob = PropertiesUtil.getBooleanProperty(bundle, "event.quitJob", true);
        allHhMoves = PropertiesUtil.getBooleanProperty(bundle, "event.all.hhMoves", true);
        inmigration = PropertiesUtil.getBooleanProperty(bundle, "event.inmigration", true);
        outMigration = PropertiesUtil.getBooleanProperty(bundle, "event.outMigration", true);
        allDwellingDevelopments = PropertiesUtil.getBooleanProperty(bundle, "event.all.developers", true);
        dwellingChangeQuality = PropertiesUtil.getBooleanProperty(bundle, "event.ddChangeQual", true);
        dwellingDemolition = PropertiesUtil.getBooleanProperty(bundle, "event.ddDemolition", true);
        dwellingConstruction = PropertiesUtil.getBooleanProperty(bundle, "event.ddConstruction", true);
    }
}
