package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public class RealEstateProperties {

    public final boolean readBinaryDwellingFile;
    public final String dwellingsFile;
    public final String dwellingTypeAcresFile;
    public final int maxStorageOfVacantDwellings;
    public final String binaryDwellingsFile;
    public final String uecFile;
    public final int dataSheet;
    public final int modelSheet;
    public final double[] structuralVacancy;
    public final boolean constructionOverwriteDwelling;
    public final boolean traceOverwriteDwellings;
    public final String overWriteDwellingsTraceFile;
    public final String constructionOverwriteDwellingFile;
    public final boolean makeSOmeNewDdAffordable;
    public final float affordableDwellingsShare;
    public final float levelOfAffordability;
    public final float constructionLogModelBeta;
    public final float constructionLogModelInflator;

    public RealEstateProperties(ResourceBundle bundle) {
        readBinaryDwellingFile = ResourceUtil.getBooleanProperty(bundle, "read.binary.dd.file", false);
        dwellingsFile = ResourceUtil.getProperty(bundle, "dwelling.file.ascii");
        dwellingTypeAcresFile = ResourceUtil.getProperty(bundle, "developer.acres.per.dwelling.by.type");
        maxStorageOfVacantDwellings = ResourceUtil.getIntegerProperty(bundle, "vacant.dd.by.reg.array");
        binaryDwellingsFile = ResourceUtil.getProperty(bundle, "dwellings.file.bin");
        uecFile = ResourceUtil.getProperty(bundle, "RealEstate.UEC.FileName");
        dataSheet = ResourceUtil.getIntegerProperty(bundle, "RealEstate.UEC.DataSheetNumber");
        modelSheet = ResourceUtil.getIntegerProperty(bundle, "RealEstate.UEC.ModelSheetNumber.Pricing");
        structuralVacancy = ResourceUtil.getDoubleArray(bundle, "vacancy.rate.by.type");
        constructionOverwriteDwelling = ResourceUtil.getBooleanProperty(bundle, "construct.dwelling.use.overwrite");
        traceOverwriteDwellings = ResourceUtil.getBooleanProperty(bundle, "trace.use.of.overwrite.dwellings");
        overWriteDwellingsTraceFile = ResourceUtil.getProperty(bundle,"trace.file.for.overwrite.dwellings");
        constructionOverwriteDwellingFile = ResourceUtil.getProperty(bundle, "construct.dwelling.overwrite");
        makeSOmeNewDdAffordable = ResourceUtil.getBooleanProperty(bundle, "make.new.dwellings.partly.affordable", false);
        affordableDwellingsShare = (float) ResourceUtil.getDoubleProperty(bundle, "share.of.affordable.dwellings");
        levelOfAffordability = (float) ResourceUtil.getDoubleProperty(bundle, "level.of.affordability.setting");
        constructionLogModelBeta = (float) ResourceUtil.getDoubleProperty(bundle, "construct.dwelling.mn.log.model.beta");
        constructionLogModelInflator = (float) ResourceUtil.getDoubleProperty(bundle, "construct.dwelling.mn.log.model.inflator");
    }
}
