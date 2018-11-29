package de.tum.bgu.msm.data.munich;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.AbstractDefaultGeoData;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.geo.RegionImpl;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;

import java.util.List;

/**
 * Interface to store zonal, county and regional data used by the SILO Model
 * @author Ana Moreno and Rolf Moeckel, Technical University of Munich
 * Created on 5 April 2017 in Munich
 **/

public class GeoDataMuc extends AbstractDefaultGeoData {

    private static final Logger logger = Logger.getLogger(GeoDataMuc.class);

    public GeoDataMuc() {
        super("Zone", "Region", "id");
    }

    @Override
    protected void readZones() {
        String fileName = Properties.get().main.baseDirectory + Properties.get().geo.zonalDataFile;
        TableDataSet zonalData = SiloUtil.readCSVfile(fileName);
        int[] zoneIds = zonalData.getColumnAsInt(zoneIdColumnName);
        int[] zoneMsa = zonalData.getColumnAsInt("msa");
        float[] zoneAreas = zonalData.getColumnAsFloat("Area");

        double[] centroidX = zonalData.getColumnAsDouble("centroidX");
        double[] centroidY = zonalData.getColumnAsDouble("centroidY");
        double[] ptDistances = zonalData.getColumnAsDouble("distanceToTransit");

        int[] areaTypes = zonalData.getColumnAsInt("BBSR_Type");

        int[] regionColumn = zonalData.getColumnAsInt("Region");

        for(int i = 0; i < zoneIds.length; i++) {
            Coord centroid = new Coord(centroidX[i], centroidY[i]);
            AreaTypes.SGType type = AreaTypes.SGType.valueOf(areaTypes[i]);
            Region region;
            int regionId = regionColumn[i];
            if (regions.containsKey(regionId)) {
                region = regions.get(regionId);
            } else {
                region = new RegionImpl(regionId);
                regions.put(region.getId(), region);
            }
            MunichZone zone = new MunichZone(zoneIds[i], zoneMsa[i], zoneAreas[i], centroid, type, ptDistances[i], region);
            region.addZone(zone);
            zones.put(zoneIds[i], zone);
        }
    }


    @Override
    public void readData() {
        super.readData();
    }
}








