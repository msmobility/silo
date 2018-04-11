package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public class DemographicsProperties {

    public final String uecFileName;
    public final int dataSheet;

    //Death
    public final int deathModelSheet;
    public final boolean logDeathCalculation;

    //Birth
    public final float marriedScaler;
    public final float singleScaler;
    public final int birthModelSheet;
    public final boolean logBirthCalculation;
    public final float localScaler;
    public final int marriageModelSheet;
    public final boolean logMarriageCalculation;
    public final int minMarryAge;
    public final float localMarriageAdjuster;
    public final float onePersonHhMarriageBias;
    public final double marryAbsAgeDiff;
    public final double marryAgeSpreadFac;
    public final float interracialMarriageShare;
    public final String autoOwnerShipUecFile;
    public final int autoOwnershipDataSheet;
    public final boolean logAutoOwnership;
    public final int autoOwnershipUecUtility;

    public DemographicsProperties(ResourceBundle bundle) {
        uecFileName = ResourceUtil.getProperty(bundle, "Demographics.UEC.FileName");
        dataSheet = ResourceUtil.getIntegerProperty(bundle, "Demographics.UEC.DataSheetNumber");

        deathModelSheet = ResourceUtil.getIntegerProperty(bundle, "Demographics.UEC.ModelSheetNumber.Death");
        logDeathCalculation = ResourceUtil.getBooleanProperty(bundle, "log.util.death");

        marriedScaler = (float) ResourceUtil.getDoubleProperty(bundle, "demographics.birth.scaler.married");
        singleScaler = (float) ResourceUtil.getDoubleProperty(bundle, "demographics.birth.scaler.single");
        birthModelSheet = ResourceUtil.getIntegerProperty(bundle, "Demographics.UEC.ModelSheetNumber.Birth");
        logBirthCalculation = ResourceUtil.getBooleanProperty(bundle, "log.util.birth");
        localScaler = (float) ResourceUtil.getDoubleProperty(bundle, "demographics.local.birth.rate.adjuster");

        marriageModelSheet = ResourceUtil.getIntegerProperty(bundle, "Demographics.UEC.ModelSheetNumber.Marriage");
        logMarriageCalculation = ResourceUtil.getBooleanProperty(bundle, "log.util.marriage");
        minMarryAge = ResourceUtil.getIntegerProperty(bundle, "demographics.min.age.for.legal.marriage");
        localMarriageAdjuster = (float) ResourceUtil.getDoubleProperty(bundle, "demographics.local.marriage.rate.adjuster");
        onePersonHhMarriageBias = (float) ResourceUtil.getDoubleProperty(bundle, "demographics.single.pers.hh.marriage.bias");
        marryAbsAgeDiff = ResourceUtil.getDoubleProperty(bundle, "demographics.age.diff.of.partners.absolute");
        marryAgeSpreadFac = ResourceUtil.getDoubleProperty(bundle, "demographics.age.diff.of.partners.spreadfc");
        interracialMarriageShare = (float) ResourceUtil.getDoubleProperty(bundle, "demographics.interracial.marriage.share");
        autoOwnerShipUecFile = ResourceUtil.getProperty(bundle, "AutoOwnership.UEC.FileName");
        autoOwnershipDataSheet = ResourceUtil.getIntegerProperty(bundle, "AutoOwnership.UEC.DataSheetNumber");
        logAutoOwnership = ResourceUtil.getBooleanProperty(bundle, "log.util.autoOwnership");
        autoOwnershipUecUtility = ResourceUtil.getIntegerProperty(bundle, "AutoOwnership.UEC.Ownership.Utility");
    }
}
