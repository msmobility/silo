package de.tum.bgu.msm.io;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.geo.GeoDataMstm;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.io.input.GeoDataReader;
import de.tum.bgu.msm.data.geo.County;
import de.tum.bgu.msm.data.geo.MstmRegion;
import de.tum.bgu.msm.data.geo.MstmZone;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Map;

public class GeoDataReaderMstm implements GeoDataReader {

    private final static Logger logger = Logger.getLogger(GeoDataReaderMstm.class);

    private final String ZONE_ID_COLUMN = "ZoneId";
    private final String COUNTY_COLUMN_NAME = "COUNTYFIPS";
    private final String SHAPE_IDENTIFIER = "SMZRMZ";

    private final GeoDataMstm geoData;

    public GeoDataReaderMstm(GeoDataMstm geoData) {
        this.geoData = geoData;
    }

    @Override
    public void readZoneCsv(String fileName) {
        TableDataSet zonalData = SiloUtil.readCSVfile(fileName);
        int[] zoneIds = zonalData.getColumnAsInt(ZONE_ID_COLUMN);
        int[] zoneMsa = zonalData.getColumnAsInt("msa");
        int[] puma = zonalData.getColumnAsInt("PUMA");
        int[] simplifiedPuma = zonalData.getColumnAsInt("simplifiedPUMA");
        int[] countyData = zonalData.getColumnAsInt(COUNTY_COLUMN_NAME);
        float[] zoneAreas = zonalData.getColumnAsFloat("Area");
        int[] regionData = zonalData.getColumnAsInt("Region");

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

            Zone zone = new MstmZone(zoneIds[i], zoneMsa[i], zoneAreas[i], puma[i], simplifiedPuma[i], county, region);
            region.addZone(zone);
            geoData.addZone(zone);
        }

        zonalData.buildIndex(zonalData.getColumnPosition("ZoneId"));
        for (Region region: regions.values()) {
            double schoolQuality = region.getZones().stream().mapToDouble(
                    zone -> zonalData.getIndexedValueAt(zone.getId(), "SchoolQualityIndex")).average().getAsDouble();

            ((MstmRegion) region).setSchoolQuality(schoolQuality);
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

    public void readCrimeData(String path) {
        TableDataSet tblCrimeIndex = SiloUtil.readCSVfile(path);
        for (int row = 1; row <= tblCrimeIndex.getRowCount(); row++) {
            int county = (int) tblCrimeIndex.getValueAt(row, "FIPS");
            double crimeRate = tblCrimeIndex.getValueAt(row, "CrimeIndicator");
            if(geoData.getCounties().containsKey(county)) {
                geoData.getCounties().get(county).setCrimeRate(crimeRate);
            } else {
                throw new RuntimeException("County " + county + " referred in crime data table does not exist!");
            }
        }

        for(Region region: geoData.getRegions().values()) {
            double regionalCrimeRate = 0.;
            double regionalArea = 0.;
            for(Zone zone: region.getZones()) {
                regionalCrimeRate += ((MstmZone)zone).getCounty().getCrimeRate() * zone.getArea_sqmi();
                regionalArea += zone.getArea_sqmi();
            }
            regionalCrimeRate /= regionalArea;
            ((MstmRegion) region).setCrimeRate(regionalCrimeRate);
        }
    }
}
