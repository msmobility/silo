package de.tum.bgu.msm.data;


import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractDefaultGeoData implements GeoData {

    private static final Logger logger = Logger.getLogger(AbstractDefaultGeoData.class);

    protected final String zoneIdColumnName;
    protected final String regionColumnName;
    private final String shapeIdentifier;

    protected final Map<Integer, Zone> zones = new LinkedHashMap<>();
    protected final Map<Integer, Region> regions = new LinkedHashMap<>();
    public Map<Integer, SimpleFeature> zoneFeatureMap = new LinkedHashMap<>();

    public AbstractDefaultGeoData(String zoneIdColumnName, String regionColumnName, String shapeIdentifier) {
        this.zoneIdColumnName = zoneIdColumnName;
        this.regionColumnName = regionColumnName;
        this.shapeIdentifier = shapeIdentifier;
    }

    @Override
    public void readData() {
        readZones();
        readShapes();
        readDevelopmentData();
    }

    private void readShapes() {
        String zoneShapeFile = Properties.get().geo.zoneShapeFile;
        if (zoneShapeFile == null) {
            logger.error("No shape file found!");
            throw new RuntimeException("No shape file found!");
        }
        for (SimpleFeature feature : ShapeFileReader.getAllFeatures(Properties.get().main.baseDirectory + zoneShapeFile)) {
            int zoneId = Integer.parseInt(feature.getAttribute(shapeIdentifier).toString());
            Zone zone = zones.get(zoneId);
            if (zone != null) {
                zone.setZoneFeature(feature);
                zoneFeatureMap.put(zoneId, feature);
            } else {
                logger.warn("zoneId: " + zoneId + " does not exist in silo zone system");
            }
        }
    }

    @Override
    public Map<Integer, Zone> getZones() {
        return Collections.unmodifiableMap(zones);
    }


    @Override
    public Map<Integer, Region> getRegions() {
        return Collections.unmodifiableMap(regions);
    }

    public Map<Integer, SimpleFeature> getZoneFeatureMap() {
        return this.zoneFeatureMap;
    }


    protected abstract void readZones();

    private void readDevelopmentData() {
        String baseDirectory = Properties.get().main.baseDirectory;

        TableDataSet developmentTable = SiloUtil.readCSVfile(baseDirectory + Properties.get().geo.landUseAndDevelopmentFile);

        int[] zoneIdData = developmentTable.getColumnAsInt("Zone");
        EnumMap<DwellingType, int[]> constraintData = new EnumMap<>(DwellingType.class);
        for (DwellingType dwellingType : DwellingType.values()) {
            constraintData.put(dwellingType, developmentTable.getColumnAsInt(dwellingType.name()));
        }
        int[] dwellingCapacityData = developmentTable.getColumnAsInt("DevCapacity");
        double[] landUseData = developmentTable.getColumnAsDouble("DevLandUse");

        for (int i = 0; i < zoneIdData.length; i++) {

            EnumMap<DwellingType, Boolean> constraints = new EnumMap<>(DwellingType.class);
            for (DwellingType dwellingType : DwellingType.values()) {
                constraints.put(dwellingType, constraintData.get(dwellingType)[i] == 1);
            }

            Development development = new DevelopmentImpl(landUseData[i], dwellingCapacityData[i], constraints, Properties.get().geo.useCapacityForDwellings);
            zones.get(zoneIdData[i]).setDevelopment(development);
        }

    }

}