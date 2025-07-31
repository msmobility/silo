package de.tum.bgu.msm.io;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.geo.RegionImpl;
import de.tum.bgu.msm.data.geo.ZoneImpl;
import de.tum.bgu.msm.io.input.GeoDataReader;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.api.feature.simple.SimpleFeature;
import org.matsim.core.utils.gis.ShapeFileReader;

public class GeoDataReaderTak implements GeoDataReader {

    private final GeoData geoData;

    private final String SHAPE_IDENTIFIER = "id";
    private final String ZONE_ID_COLUMN = "Zone";

    private final static Logger logger = LogManager.getLogger(GeoDataReaderTak.class);

    public GeoDataReaderTak(GeoData geoData) {
        this.geoData = geoData;
    }

    @Override
    public void readZoneCsv(String path) {
        TableDataSet zonalData = SiloUtil.readCSVfile(path);
        int[] zoneIds = zonalData.getColumnAsInt(ZONE_ID_COLUMN);
        float[] zoneAreas = zonalData.getColumnAsFloat("Area");

        int[] regionColumn = zonalData.getColumnAsInt("Region");

        for(int i = 0; i < zoneIds.length; i++) {
            Region region;
            int regionId = regionColumn[i];
            if (geoData.getRegions().containsKey(regionId)) {
                region = geoData.getRegions().get(regionId);
            } else {
                region = new RegionImpl(regionId);
                geoData.addRegion(region);
            }
            ZoneImpl zone = new ZoneImpl(zoneIds[i], zoneAreas[i], region);
            region.addZone(zone);
            geoData.addZone(zone);
        }
    }

    @Override
    public void readZoneShapefile(String path) {
        if (path == null) {
            logger.error("No shape file found!");
            throw new RuntimeException("No shape file found!");
        }
        for (SimpleFeature feature : ShapeFileReader.getAllFeatures(path)) {
            int zoneId = Integer.parseInt(feature.getAttribute(SHAPE_IDENTIFIER).toString());
            Zone zone = geoData.getZones().get(zoneId);
            if (zone != null) {
                zone.setZoneFeature(feature);
            } else {
                logger.warn("zoneId: " + zoneId + " does not exist in silo zone system");
            }
        }
    }
}
