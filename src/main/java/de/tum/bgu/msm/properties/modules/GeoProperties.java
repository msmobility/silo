package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public class GeoProperties {

    private static final String ZONAL_DATA_FILE = "zonal.data.file";
    private static final String ZONAL_SCHOOL_QUALITY_INDEX = "school.quality.index";
    private static final String COUNTY_CRIME_INDEX = "crime.index";
    private static final String REGION_DEF_FILE = "region.definition.file";
    private static final String LAND_USE_AREA = "land.use.area.by.taz";
    private static final String DEVELOPABLE = "developable.lu.category";
    private static final String DEVELOPM_RESTR = "development.restrictions";
    private static final String USE_CAPACITY = "use.growth.capacity.data";
    private static final String CAPACITY_FILE = "growth.capacity.file";

    private final String zonalDataFile;
    private final String regionDefinitionFile;
    private final String zonalSchoolQualityFile;
    private final String countyCrimeFile;
    private final String landUseAreaFile;
    private final int[] developableLandUseTypes;
    private final String developmentRestrictionsFile;
    private final boolean useCapacityForDwellings;
    private final String capacityFile;

    public GeoProperties(ResourceBundle bundle) {
        zonalDataFile = ResourceUtil.getProperty(bundle, ZONAL_DATA_FILE);
        regionDefinitionFile = ResourceUtil.getProperty(bundle, REGION_DEF_FILE);
        zonalSchoolQualityFile = ResourceUtil.getProperty(bundle, ZONAL_SCHOOL_QUALITY_INDEX);
        countyCrimeFile = ResourceUtil.getProperty(bundle, COUNTY_CRIME_INDEX);
        landUseAreaFile = ResourceUtil.getProperty(bundle, LAND_USE_AREA);
        developableLandUseTypes = ResourceUtil.getIntegerArray(bundle, DEVELOPABLE);
        developmentRestrictionsFile = ResourceUtil.getProperty(bundle, DEVELOPM_RESTR);
        useCapacityForDwellings = ResourceUtil.getBooleanProperty(bundle, USE_CAPACITY, false);
        capacityFile = ResourceUtil.getProperty(bundle, CAPACITY_FILE);

    }

    public String getZonalDataFile() {
        return zonalDataFile;
    }

    public String getRegionDefinitionFile() {
        return regionDefinitionFile;
    }

    public String getZonalSchoolQualityFile() {
        return zonalSchoolQualityFile;
    }

    public String getCountyCrimeFile() {
        return countyCrimeFile;
    }

    public String getLandUseAreaFile() {
        return landUseAreaFile;
    }

    public int[] getDevelopableLandUseTypes() {
        return developableLandUseTypes;
    }

    public String getDevelopmentRestrictionsFile() {
        return developmentRestrictionsFile;
    }

    public boolean isUseCapacityForDwellings() {
        return useCapacityForDwellings;
    }

    public String getCapacityFile() {
        return capacityFile;
    }
}


