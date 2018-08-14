package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.ResourceBundle;

public class DemographicsProperties {


    //Marriage
    public final float marriedScaler;
    public final float singleScaler;
    public final float localScaler;
    public final int minMarryAge;
    public final float localMarriageAdjuster;
    public final float onePersonHhMarriageBias;
    public final double marryAbsAgeDiff;
    public final double marryAgeSpreadFac;
    public final float interracialMarriageShare;

    //Auto ownership
    public final String autoOwnerShipUecFile;
    public final int autoOwnershipDataSheet;
    public final boolean logAutoOwnership;
    public final int autoOwnershipUecUtility;

    public DemographicsProperties(ResourceBundle bundle) {
        PropertiesUtil.newPropertySubmodule("Demographic properties");
        marriedScaler = (float) PropertiesUtil.getDoubleProperty(bundle, "demographics.birth.scaler.married", 2.243);
        singleScaler = (float) PropertiesUtil.getDoubleProperty(bundle, "demographics.birth.scaler.single", 0.1);
        localScaler = (float) PropertiesUtil.getDoubleProperty(bundle, "demographics.local.birth.rate.adjuster", 0.87);
        minMarryAge = PropertiesUtil.getIntProperty(bundle, "demographics.min.age.for.legal.marriage", 18);
        localMarriageAdjuster = (float) PropertiesUtil.getDoubleProperty(bundle, "demographics.local.marriage.rate.adjuster", 1.1);
        onePersonHhMarriageBias = (float) PropertiesUtil.getDoubleProperty(bundle, "demographics.single.pers.hh.marriage.bias", 2);
        marryAbsAgeDiff = PropertiesUtil.getDoubleProperty(bundle, "demographics.age.diff.of.partners.absolute", 2.3);
        marryAgeSpreadFac = PropertiesUtil.getDoubleProperty(bundle, "demographics.age.diff.of.partners.spreadfc", 0.5);
        interracialMarriageShare = (float) PropertiesUtil.getDoubleProperty(bundle, "demographics.interracial.marriage.share", 0.02);

        //will be removed soon
        PropertiesUtil.newPropertySubmodule("Auto ownership UEC properties - to be removed");
        autoOwnerShipUecFile = PropertiesUtil.getStringProperty(bundle, "AutoOwnership.UEC.FileName");
        autoOwnershipDataSheet = PropertiesUtil.getIntProperty(bundle, "AutoOwnership.UEC.DataSheetNumber");
        logAutoOwnership = PropertiesUtil.getBooleanProperty(bundle, "log.util.autoOwnership");
        autoOwnershipUecUtility = PropertiesUtil.getIntProperty(bundle, "AutoOwnership.UEC.Ownership.Utility");
    }
}
