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

    }
}
