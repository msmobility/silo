package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.Implementation;

import java.util.ResourceBundle;

public class GeoProperties {

    public final String zonalDataFile;
    public final String regionDefinitionFile;
    public final String zonalSchoolQualityFile;
    public final String countyCrimeFile;
    public final String landUseAreaFile;
    public final int[] developableLandUseTypes;
    public final String developmentRestrictionsFile;
    public final boolean useCapacityForDwellings;
    public final String transitAccessTime;
    public final String zonalAttributesFile;
    public final String capacityFile;

    public GeoProperties(ResourceBundle bundle, Implementation implementation) {
        zonalDataFile = ResourceUtil.getProperty(bundle, "zonal.data.file");
        regionDefinitionFile = ResourceUtil.getProperty(bundle, "region.definition.file");
        zonalSchoolQualityFile = ResourceUtil.getProperty(bundle, "school.quality.index");
        countyCrimeFile = ResourceUtil.getProperty(bundle, "crime.index");
        landUseAreaFile = ResourceUtil.getProperty(bundle, "land.use.area.by.taz");
        developableLandUseTypes = ResourceUtil.getIntegerArray(bundle, "developable.lu.category");
        developmentRestrictionsFile = ResourceUtil.getProperty(bundle, "development.restrictions");
        useCapacityForDwellings = ResourceUtil.getBooleanProperty(bundle, "use.growth.capacity.data", false);
        capacityFile = ResourceUtil.getProperty(bundle, "growth.capacity.file");
        if(implementation == Implementation.MUNICH) {
            zonalAttributesFile = ResourceUtil.getProperty(bundle, "raster.cells.definition");
        } else {
            zonalAttributesFile = null;
        }
        transitAccessTime = ResourceUtil.getProperty(bundle, "transit.access.time");
    }
}


