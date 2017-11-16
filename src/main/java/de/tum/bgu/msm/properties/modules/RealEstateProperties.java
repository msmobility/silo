package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public class RealEstateProperties {

    public final boolean readBinaryDwellingFile;
    public final String dwellingsFile;
    public final String dwellingTypeAcresFile;
    public final int maxStorageOfVacantDwellings;
    public final String binaryDwellingsFile;

    public RealEstateProperties(ResourceBundle bundle) {
        readBinaryDwellingFile = ResourceUtil.getBooleanProperty(bundle, "read.binary.dd.file", false);
        dwellingsFile = ResourceUtil.getProperty(bundle, "dwelling.file.ascii");
        dwellingTypeAcresFile = ResourceUtil.getProperty(bundle, "developer.acres.per.dwelling.by.type");
        maxStorageOfVacantDwellings = ResourceUtil.getIntegerProperty(bundle, "vacant.dd.by.reg.array");
        binaryDwellingsFile = ResourceUtil.getProperty(bundle, "dwellings.file.bin");
    }
}
