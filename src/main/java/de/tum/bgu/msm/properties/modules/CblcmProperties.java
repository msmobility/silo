package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public class CblcmProperties {

    private final ResourceBundle bundle;

    public final boolean createCblcmFiles;
    public final String baseYear;
    public final String baseFile;

    public CblcmProperties(ResourceBundle bundle) {
        this.bundle = bundle;
        createCblcmFiles = ResourceUtil.getBooleanProperty(bundle, "create.cblcm.files", false);
        baseYear = ResourceUtil.getProperty(bundle, "cblcm.base.year");
        baseFile = ResourceUtil.getProperty(bundle, "cblcm.base.file");
    }

    public double multiplierPrefix(String column) {
        return ResourceUtil.getDoubleProperty(bundle, "cblcm.multiplier" + "." + column);
    }
}
