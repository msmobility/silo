package de.tum.bgu.msm.io;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.AreaTypes;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.ZoneMEL;
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

import static de.tum.bgu.msm.util.MelbourneImplementationConfig.getMelbourneProperties;


public class GeoDataReaderMelbourne implements GeoDataReader {

    private static Logger logger = LogManager.getLogger(GeoDataReaderMelbourne.class);

    static java.util.Properties properties = getMelbourneProperties();

    private GeoData geoDataMEL;
    private DataContainer dataContainer;

    private final String SHAPE_IDENTIFIER = properties.getProperty("zone.id.field");
    private final String ZONE_ID_COLUMN = properties.getProperty("zone.id.field");
    private final String ZONE_URBAN_TYPE_COLUMN = properties.getProperty("zone.urban.type.field");
    private final String CATCHMENT_ID_COLUMN = properties.getProperty("zone.catchment.id.field");
    private final String SOCIOECONOMIC_DISADVANTAGE_DECILES_COLUMN = properties.getProperty("zone.socioeconomic.disadvantage.deciles.field");
    private final String POP_CENTROID_X_COLUMN = properties.getProperty("zone.pop.centroid.x.field");
    private final String POP_CENTROID_Y_COLUMN = properties.getProperty("zone.pop.centroid.y.field");

    public GeoDataReaderMelbourne(DataContainer dataContainer) {
        this.geoDataMEL = dataContainer.getGeoData();
        this.dataContainer = dataContainer;
    }

    @Override
    public void readZoneCsv(String path) {
        TableDataSet zonalData = SiloUtil.readCSVfile(path);
        int[] zoneIds = zonalData.getColumnAsInt(ZONE_ID_COLUMN);
        //float[] zoneAreas = zonalData.getColumnAsFloat("area");
        int[] areaTypes = zonalData.getColumnAsInt(ZONE_URBAN_TYPE_COLUMN);
        double[] popCentroid_x = zonalData.getColumnAsDouble(POP_CENTROID_X_COLUMN);
        double[] popCentroid_y = zonalData.getColumnAsDouble(POP_CENTROID_Y_COLUMN);
        String[] catchmentCode = zonalData.getColumnAsString(CATCHMENT_ID_COLUMN);
        int[] socioEconomicDisadvantageDeciles = zonalData.    getColumnAsInt(SOCIOECONOMIC_DISADVANTAGE_DECILES_COLUMN);

        int[] regionColumn = zonalData.getColumnAsInt(CATCHMENT_ID_COLUMN);

        for (int i = 0; i < zoneIds.length; i++) {
            AreaTypes.RType type = areaTypes[i]==1? AreaTypes.RType.URBAN: AreaTypes.RType.RURAL;
            Region region;
            int regionId = regionColumn[i];
            if (geoDataMEL.getRegions().containsKey(regionId)) {
                region = geoDataMEL.getRegions().get(regionId);
            } else {
                region = new RegionImpl(regionId);
                geoDataMEL.addRegion(region);
            }
            ZoneMEL zone = new ZoneMEL(zoneIds[i], 0, type,new Coordinate(popCentroid_x[i], popCentroid_y[i], 0.),region);
            region.addZone(zone);
            zone.setCatchmentCode(catchmentCode[i]);
            zone.setSocioEconomicDisadvantageDeciles(socioEconomicDisadvantageDeciles[i]);
            geoDataMEL.addZone(zone);
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
            Zone zone = geoDataMEL.getZones().get(zoneId);
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