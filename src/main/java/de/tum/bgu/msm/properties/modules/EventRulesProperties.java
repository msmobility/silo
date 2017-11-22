package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

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
        allDemography = ResourceUtil.getBooleanProperty(bundle, "event.all.demography");
        birthday = ResourceUtil.getBooleanProperty(bundle, "event.birthday");
        death = ResourceUtil.getBooleanProperty(bundle, "event.checkDeath");
        birth = ResourceUtil.getBooleanProperty(bundle, "event.checkBirth");
        leaveParentHh = ResourceUtil.getBooleanProperty(bundle, "event.checkLeaveParentHh");
        marriage = ResourceUtil.getBooleanProperty(bundle, "event.checkMarriage");
        divorce = ResourceUtil.getBooleanProperty(bundle, "event.checkDivorce");
        schoolUniversity = ResourceUtil.getBooleanProperty(bundle, "event.checkSchoolUniv", false);
        driversLicense = ResourceUtil.getBooleanProperty(bundle, "event.checkDriversLicense", false);
        startNewJob = ResourceUtil.getBooleanProperty(bundle, "event.startJob");
        quitJob = ResourceUtil.getBooleanProperty(bundle, "event.quitJob");
        allHhMoves = ResourceUtil.getBooleanProperty(bundle, "event.all.hhMoves");
        inmigration = ResourceUtil.getBooleanProperty(bundle, "event.inmigration");
        outMigration = ResourceUtil.getBooleanProperty(bundle, "event.outMigration");
        allDwellingDevelopments = ResourceUtil.getBooleanProperty(bundle, "event.all.developers");
        dwellingChangeQuality = ResourceUtil.getBooleanProperty(bundle, "event.ddChangeQual");
        dwellingDemolition = ResourceUtil.getBooleanProperty(bundle, "event.ddDemolition");
        dwellingConstruction = ResourceUtil.getBooleanProperty(bundle, "event.ddConstruction");
    }
}
