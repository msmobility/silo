package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public class RealEstateProperties {

    private static final String DD_FILE_ASCII = "dwelling.file.ascii";
    private static final String READ_BIN_FILE = "read.binary.dd.file";
    private static final String DD_FILE_BIN = "dwellings.file.bin";
    private static final String MAX_NUM_VAC_DD = "vacant.dd.by.reg.array";
    private static final String ACRES_BY_DD = "developer.acres.per.dwelling.by.type";

    private final boolean readBinaryDwellingFile;
    private final String dwellingsFile;
    private final String dwellingTypeAcresFile;
    private final int maxStorageOfVacantDwellings;
    private final String binaryDwellingsFile;

    public RealEstateProperties(ResourceBundle bundle) {
        readBinaryDwellingFile = ResourceUtil.getBooleanProperty(bundle, READ_BIN_FILE, false);
        dwellingsFile = ResourceUtil.getProperty(bundle, DD_FILE_ASCII);
        dwellingTypeAcresFile = ResourceUtil.getProperty(bundle, ACRES_BY_DD);
        maxStorageOfVacantDwellings = ResourceUtil.getIntegerProperty(bundle, MAX_NUM_VAC_DD);
        binaryDwellingsFile = ResourceUtil.getProperty(bundle, DD_FILE_BIN);
    }

    public boolean isReadBinaryDwellingFile() {
        return readBinaryDwellingFile;
    }

    public String getDwellingsFile() {
        return dwellingsFile;
    }

    public String getDwellingTypeAcresFile() {
        return dwellingTypeAcresFile;
    }

    public int getMaxStorageOfVacantDwellings() {
        return maxStorageOfVacantDwellings;
    }

    public String getBinaryDwellingsFile() {
        return binaryDwellingsFile;
    }
}
