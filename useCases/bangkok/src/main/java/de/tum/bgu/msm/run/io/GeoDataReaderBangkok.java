package de.tum.bgu.msm.run.io;

import de.tum.bgu.msm.data.geo.ZoneImpl;
import de.tum.bgu.msm.io.input.GeoDataReader;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.geo.RegionImpl;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

public class GeoDataReaderBangkok implements GeoDataReader {

    private static Logger logger = Logger.getLogger(GeoDataReaderBangkok.class);

    private GeoData geoDataMuc;

    private final String SHAPE_IDENTIFIER = "ZONE";
    private final String ZONE_ID_COLUMN = "Zone";

    public GeoDataReaderBangkok(GeoData geoDataMuc) {
        this.geoDataMuc = geoDataMuc;
    }

    @Override
    public void readZoneCsv(String path) {
        TableDataSet zonalData = SiloUtil.readCSVfile(path);
        int[] zoneIds = zonalData.getColumnAsInt(ZONE_ID_COLUMN);
        float[] zoneAreas = zonalData.getColumnAsFloat("Area");

        //int[] areaTypes = zonalData.getColumnAsInt("BBSR_Type");

        int[] regionColumn = zonalData.getColumnAsInt("Region");

        for (int i = 0; i < zoneIds.length; i++) {
            //AreaTypes.SGType type = AreaTypes.SGType.valueOf(areaTypes[i]);
            Region region;
            int regionId = regionColumn[i];
            if (geoDataMuc.getRegions().containsKey(regionId)) {
                region = geoDataMuc.getRegions().get(regionId);
            } else {
                region = new RegionImpl(regionId);
                geoDataMuc.addRegion(region);
            }
            ZoneImpl zone = new ZoneImpl(zoneIds[i], zoneAreas[i], region);
            region.addZone(zone);
            geoDataMuc.addZone(zone);
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
            Zone zone = geoDataMuc.getZones().get(zoneId);
            if (zone != null) {
                zone.setZoneFeature(feature);
            } else {
                counter++;
            }
        }
        if(counter > 0) {
            logger.warn("There were " + counter + " shapes that do not exist in silo zone system");
        }
    }
}