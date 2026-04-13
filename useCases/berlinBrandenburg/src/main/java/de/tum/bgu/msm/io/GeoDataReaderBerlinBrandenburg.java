package de.tum.bgu.msm.io;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.AreaTypes;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.geo.RegionImpl;
import de.tum.bgu.msm.data.geo.ZoneBerlinBrandenburg;
import de.tum.bgu.msm.io.input.GeoDataReader;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.api.feature.simple.SimpleFeature;
import org.matsim.core.utils.gis.ShapeFileReader;

public class GeoDataReaderBerlinBrandenburg implements GeoDataReader {

    private static Logger logger = LogManager.getLogger(GeoDataReaderBerlinBrandenburg.class);

    private GeoData geoData;

    private final String SHAPE_IDENTIFIER = "id";
    private final String ZONE_ID_COLUMN = "id";

    public GeoDataReaderBerlinBrandenburg(GeoData geoData) {
        this.geoData = geoData;
    }

    @Override
    public void readZoneCsv(String path) {
        TableDataSet zonalData = SiloUtil.readCSVfile(path);
        int[] zoneIds = zonalData.getColumnAsInt(ZONE_ID_COLUMN);
        float[] zoneAreas = zonalData.getColumnAsFloat("Area");

        double[] ptDistances = zonalData.getColumnAsDouble("distanceToTransit");

        int[] areaTypes = zonalData.getColumnAsInt("BBSR_Type");

        int[] regionColumn = zonalData.getColumnAsInt("Region");

        for (int i = 0; i < zoneIds.length; i++) {
            AreaTypes.SGType type = AreaTypes.SGType.CORE_CITY;
            Region region;
            int regionId = regionColumn[i];
            if (geoData.getRegions().containsKey(regionId)) {
                region = geoData.getRegions().get(regionId);
            } else {
                region = new RegionImpl(regionId);
                geoData.addRegion(region);
            }
            ZoneBerlinBrandenburg zone = new ZoneBerlinBrandenburg(zoneIds[i], zoneAreas[i], type, ptDistances[i], region);
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
        int counter = 0;
        for (SimpleFeature feature : ShapeFileReader.getAllFeatures(path)) {
            int zoneId = Integer.parseInt(feature.getAttribute(SHAPE_IDENTIFIER).toString());
            ZoneBerlinBrandenburg zone = (ZoneBerlinBrandenburg) geoData.getZones().get(zoneId);
            if (zone != null) {
                zone.setZoneFeature(feature);
                final Object ags = feature.getAttribute("AGS");
                if(ags != null) {
                    zone.setAgs(Integer.parseInt(ags.toString()));
                }
            } else {
                counter++;
            }
        }
        if(counter > 0) {
            logger.warn("There were " + counter + " shapes that do not exist in silo zone system");
        }
    }
}