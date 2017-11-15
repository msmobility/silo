package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public class CblcmPropertiesModule {

    private final ResourceBundle bundle;

    private static final String CBLCM_FILES = "create.cblcm.files";
    private static final String BASE_YEAR= "cblcm.base.year";
    private static final String BASE_FILE = "cblcm.base.file";
    private static final String MULTIPLIER_PREFIX  = "cblcm.multiplier";
    private static final String MAND_ZONES_FILE	= "cblcm.mandatory.zonal.base.file";

    private final boolean createCblcmFiles;
    private final String baseYear;
    private final String baseFile;

    public CblcmPropertiesModule(ResourceBundle bundle) {
        this.bundle = bundle;
        createCblcmFiles = ResourceUtil.getBooleanProperty(bundle, CBLCM_FILES, false);
        baseYear = ResourceUtil.getProperty(bundle, BASE_YEAR);
        baseFile = ResourceUtil.getProperty(bundle, BASE_FILE);

    }

    public boolean isCreateCblcmFiles() {
        return createCblcmFiles;
    }

    public String getBaseYear() {
        return baseYear;
    }

    public String getBaseFile() {
        return baseFile;
    }

    public double getMultiplierPrefix(String column) {
        return ResourceUtil.getDoubleProperty(bundle, MULTIPLIER_PREFIX+"."+column);
    }
}
