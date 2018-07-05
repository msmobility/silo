package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

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
        PropertiesUtil.printOutModuleTitle("CBLCM properties");
        createCblcmFiles = PropertiesUtil.getBooleanProperty(bundle, "create.cblcm.files", false);
        baseYear = PropertiesUtil.getStringProperty(bundle, "cblcm.base.year", "INSERT_DEFAULT_VALUE");
        baseFile = PropertiesUtil.getStringProperty(bundle, "cblcm.base.file", "INSERT_DEFAULT_VALUE");
        years = PropertiesUtil.getIntPropertyArray(bundle, "cblcm.years", new int[]{2000,2010,2020,2030,2040});
        populationFile = PropertiesUtil.getStringProperty(bundle,"cblcm.population.file.name", "cblcmPopulation");
        employmentFile = PropertiesUtil.getStringProperty(bundle, "cblcm.employment.file.name", "cblcmEmployment");
        dwellingsFile = PropertiesUtil.getStringProperty(bundle, "cblcm.dwellings.file.name", "cblcmDwellings");
        accessibilityFile = PropertiesUtil.getStringProperty(bundle, "cblcm.accessibilities.file.name", "cblcmAccessibilities");
        countyOrderFile = PropertiesUtil.getStringProperty(bundle, "cblcm.county.order.list", "INSERT_DEFAULT_VALUE");
        countyPopulationFile = PropertiesUtil.getStringProperty(bundle, "cblcm.county.population.file.name", "cblcm_HDemand.txt");
        countyEmployMentFile = PropertiesUtil.getStringProperty(bundle, "cblcm.county.employment.file.name" , "cblcm_CDemand.txt");
    }

    public double multiplierPrefix(String column) {
        return PropertiesUtil.getDoubleProperty(bundle, "cblcm.multiplier" + "." + column);
    }
}
