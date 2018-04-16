package de.tum.bgu.msm.data.munich;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.AbstractDefaultGeoData;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.RegionImpl;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.properties.Properties;
import org.matsim.api.core.v01.Coord;

/**
 * Interface to store zonal, county and regional data used by the SILO Model
 * @author Ana Moreno and Rolf Moeckel, Technical University of Munich
 * Created on 5 April 2017 in Munich
 **/

public class GeoDataMuc extends AbstractDefaultGeoData {

    public GeoDataMuc() {
        super("Zone", "Region");
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

        for(int i = 0; i < zoneIds.length; i++) {
            Coord centroid = new Coord(centroidX[i], centroidY[i]);
            MunichZone zone = new MunichZone(zoneIds[i], zoneMsa[i], zoneAreas[i], centroid, ptDistances[i]);
            zones.put(zoneIds[i], zone);
        }
    }

    @Override
    protected void readRegionDefinition() {
        String regFileName = Properties.get().main.baseDirectory + Properties.get().geo.regionDefinitionFile;
        TableDataSet regDef = SiloUtil.readCSVfile(regFileName);
        for (int row = 1; row <= regDef.getRowCount(); row++) {
            int taz = (int) regDef.getValueAt(row, zoneIdColumnName);
            int regionId = (int) regDef.getValueAt(row, regionColumnName);
            Zone zone = zones.get(taz);
            if (zone != null) {
                Region region;
                if (regions.containsKey(regionId)) {
                    region = regions.get(regionId);
                    region.addZone(zone);
                } else {
                    region = new RegionImpl(regionId);
                    region.addZone(zone);
                    regions.put(region.getId(), region);
                }
                zone.setRegion(region);
            } else {
                throw new RuntimeException("Zone " + taz + " of regions definition file does not exist.");
            }
        }
    }

    @Override
    public void readData() {
        super.readData();
    }
}








