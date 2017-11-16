package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public class CblcmProperties {

    private final ResourceBundle bundle;

    public final boolean createCblcmFiles;
    public final String baseYear;
    public final String baseFile;
    public final int[] years;
    public final String populationFile;
    public final String employmentFile;
    public final String dwellingsFile;
    public final String accessibilityFile;
    public final String countyOrderFile;
    public final String countyPopulationFile;
    public final String countyEmployMentFile;

    public CblcmProperties(ResourceBundle bundle) {
        this.bundle = bundle;
        createCblcmFiles = ResourceUtil.getBooleanProperty(bundle, "create.cblcm.files", false);
        baseYear = ResourceUtil.getProperty(bundle, "cblcm.base.year");
        baseFile = ResourceUtil.getProperty(bundle, "cblcm.base.file");
        years = ResourceUtil.getIntegerArray(bundle, "cblcm.years");
        populationFile = ResourceUtil.getProperty(bundle,"cblcm.population.file.name");
        employmentFile = ResourceUtil.getProperty(bundle, "cblcm.employment.file.name");
        dwellingsFile = ResourceUtil.getProperty(bundle, "cblcm.dwellings.file.name");
        accessibilityFile = ResourceUtil.getProperty(bundle, "cblcm.accessibilities.file.name");
        countyOrderFile = ResourceUtil.getProperty(bundle, "cblcm.county.order.list");
        countyPopulationFile = ResourceUtil.getProperty(bundle, "cblcm.county.population.file.name");
        countyEmployMentFile = ResourceUtil.getProperty(bundle, "cblcm.county.employment.file.name");
    }

    public double multiplierPrefix(String column) {
        return ResourceUtil.getDoubleProperty(bundle, "cblcm.multiplier" + "." + column);
    }
}
