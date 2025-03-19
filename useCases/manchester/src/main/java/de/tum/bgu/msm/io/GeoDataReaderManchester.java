package de.tum.bgu.msm.io;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.AreaTypes;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.ZoneMCR;
import de.tum.bgu.msm.health.data.ActivityLocation;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.io.input.GeoDataReader;


import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.geo.RegionImpl;

import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.api.feature.simple.SimpleFeature;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.core.utils.gis.ShapeFileReader;

public class GeoDataReaderManchester implements GeoDataReader {

    private static Logger logger = LogManager.getLogger(GeoDataReaderManchester.class);

    private GeoData geoDataMcr;
    private DataContainer dataContainer;

    private final String SHAPE_IDENTIFIER = "id";
    private final String ZONE_ID_COLUMN = "oaID";

    public GeoDataReaderManchester(DataContainer dataContainer) {
        this.geoDataMcr = dataContainer.getGeoData();
        this.dataContainer = dataContainer;
    }

    @Override
    public void readZoneCsv(String path) {
        TableDataSet zonalData = SiloUtil.readCSVfile(path);
        int[] zoneIds = zonalData.getColumnAsInt(ZONE_ID_COLUMN);
        //float[] zoneAreas = zonalData.getColumnAsFloat("area");
        int[] areaTypes = zonalData.getColumnAsInt("urbanType");
        double[] popCentroid_x = zonalData.getColumnAsDouble("popCentroid_x");
        double[] popCentroid_y = zonalData.getColumnAsDouble("popCentroid_y");
        String[] lsoaCode = zonalData.getColumnAsString("lsoa21cd");
        int[] imd10 = zonalData.getColumnAsInt("imd10");

        //TODO: check where region is used. then to define it should be msoa, lad or losa
        int[] regionColumn = zonalData.getColumnAsInt("msoaID");

        for (int i = 0; i < zoneIds.length; i++) {
            AreaTypes.RType type = areaTypes[i]==1? AreaTypes.RType.URBAN: AreaTypes.RType.RURAL;
            Region region;
            int regionId = regionColumn[i];
            if (geoDataMcr.getRegions().containsKey(regionId)) {
                region = geoDataMcr.getRegions().get(regionId);
            } else {
                region = new RegionImpl(regionId);
                geoDataMcr.addRegion(region);
            }
            ZoneMCR zone = new ZoneMCR(zoneIds[i], 0, type,new Coordinate(popCentroid_x[i], popCentroid_y[i], 0.),region);
            region.addZone(zone);
            zone.setLsoaCode(lsoaCode[i]);
            zone.setImd10(imd10[i]);
            geoDataMcr.addZone(zone);
            ActivityLocation activityLocation = new ActivityLocation(("zone"+zone.getZoneId()),zone.getPopCentroidCoord());
            ((DataContainerHealth) dataContainer).getActivityLocations().put(("zone"+zone.getZoneId()),activityLocation);
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
            Zone zone = geoDataMcr.getZones().get(zoneId);
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