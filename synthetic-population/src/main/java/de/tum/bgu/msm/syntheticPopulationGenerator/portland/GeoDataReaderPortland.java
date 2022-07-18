package de.tum.bgu.msm.syntheticPopulationGenerator.portland;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.County;
import de.tum.bgu.msm.data.geo.GeoDataMstm;
import de.tum.bgu.msm.data.geo.MstmRegion;
import de.tum.bgu.msm.data.geo.MstmZone;
import de.tum.bgu.msm.io.GeoDataReaderMstm;
import de.tum.bgu.msm.io.input.GeoDataReader;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Map;

public class GeoDataReaderPortland implements GeoDataReader {
    private final static Logger logger = Logger.getLogger(GeoDataReaderMstm.class);

    private final String ZONE_ID_COLUMN = "id";
    private final String COUNTY_COLUMN_NAME = "county_id";
    private final String SHAPE_IDENTIFIER = "FINALTAZ";

    private final GeoDataMstm geoData;

    public GeoDataReaderPortland(GeoDataMstm geoData) {
        this.geoData = geoData;
    }

    @Override
    public void readZoneCsv(String fileName) {
        TableDataSet zonalData = SiloUtil.readCSVfile(fileName);
        int[] zoneIds = zonalData.getColumnAsInt(ZONE_ID_COLUMN);
        //int[] zoneMsa = zonalData.getColumnAsInt("msa");
        int[] puma = zonalData.getColumnAsInt("puma");
        //int[] simplifiedPuma = zonalData.getColumnAsInt("simplifiedPUMA");
        int[] countyData = zonalData.getColumnAsInt(COUNTY_COLUMN_NAME);
        float[] zoneAreas = zonalData.getColumnAsFloat("area_acres");
        int[] regionData = zonalData.getColumnAsInt("region_id");

        final Map<Integer, County> counties = geoData.getCounties();
        final Map<Integer, Region> regions = geoData.getRegions();
        for(int i = 0; i < zoneIds.length; i++) {
            County county;
            if(counties.containsKey(countyData[i])) {
                county = counties.get(countyData[i]);
            } else {
                county = new County(countyData[i]);
                geoData.addCounty(county);
            }

            Region region;
            int regionId = regionData[i];
            if (regions.containsKey(regionId)) {
                region = regions.get(regionId);
            } else {
                region = new MstmRegion(regionId);
                geoData.addRegion(region);
            }

            Zone zone = new MstmZone(zoneIds[i], -1, zoneAreas[i], puma[i], -1, county, region);
            region.addZone(zone);
            geoData.addZone(zone);
        }

        zonalData.buildIndex(zonalData.getColumnPosition("Id"));
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
