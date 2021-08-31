package run;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.RegionImpl;
import de.tum.bgu.msm.data.geo.ZoneImpl;
import de.tum.bgu.msm.io.input.GeoDataReader;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Map;

public class GeoDataReaderPerth implements GeoDataReader {

    private final static Logger logger = Logger.getLogger(GeoDataReaderPerth.class);

    private final String ZONE_ID_COLUMN = "Zone";
    private final String SHAPE_IDENTIFIER = "SA1_7DIG11";

    private final DefaultGeoData geoData;

    public GeoDataReaderPerth(DefaultGeoData geoData) {
        this.geoData = geoData;
    }

    @Override
    public void readZoneCsv(String fileName) {
        TableDataSet zonalData = SiloUtil.readCSVfile(fileName);
        int[] zoneIds = zonalData.getColumnAsInt(ZONE_ID_COLUMN);
        float[] zoneAreas = zonalData.getColumnAsFloat("Area");
        int[] regionData = zonalData.getColumnAsInt("Region");

        final Map<Integer, Region> regions = geoData.getRegions();
        for(int i = 0; i < zoneIds.length; i++) {

            Region region;
            int regionId = regionData[i];
            if (regions.containsKey(regionId)) {
                region = regions.get(regionId);
            } else {
                region = new RegionImpl(regionId);
                geoData.addRegion(region);
            }

            Zone zone = new ZoneImpl(zoneIds[i], zoneAreas[i], region);
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
