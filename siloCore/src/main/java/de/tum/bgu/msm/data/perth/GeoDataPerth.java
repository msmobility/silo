//package de.tum.bgu.msm.data.perth;
//
//import com.pb.common.datafile.TableDataSet;
//import de.tum.bgu.msm.data.geo.DefaultGeoData;
//import de.tum.bgu.msm.data.Region;
//import de.tum.bgu.msm.data.geo.RegionImpl;
//import de.tum.bgu.msm.data.geo.ZoneImpl;
//import de.tum.bgu.msm.properties.Properties;
//import de.tum.bgu.msm.utils.SiloUtil;
//import org.apache.log4j.Logger;
//
///**
// * Interface to store zonal, county and regional data used by the SILO Model
// * @author Nico Kuehnel and Rolf Moeckel, Technical University of Munich
// * Created on 8 March 2019 in Munich
// **/
//
//public class GeoDataPerth extends DefaultGeoData {
//
//    private static final Logger logger = Logger.getLogger(GeoDataPerth.class);
//
//    public GeoDataPerth() {
//        super("Zone", "Region", "id");
//    }
//
//    @Override
//    protected void readZones() {
//        String fileName = Properties.get().main.baseDirectory + Properties.get().geo.zonalDataFile;
//        TableDataSet zonalData = SiloUtil.readCSVfile(fileName);
//        int[] zoneIds = zonalData.getColumnAsInt(zoneIdColumnName);
//        int[] zoneMsa = zonalData.getColumnAsInt("msa");
//        float[] zoneAreas = zonalData.getColumnAsFloat("Area");
//
//        int[] regionColumn = zonalData.getColumnAsInt("Region");
//
//        for(int i = 0; i < zoneIds.length; i++) {
//
//            Region region;
//            int regionId = regionColumn[i];
//            if (regions.containsKey(regionId)) {
//                region = regions.get(regionId);
//            } else {
//                region = new RegionImpl(regionId);
//                regions.put(region.getId(), region);
//            }
//            ZoneImpl zone = new ZoneImpl(zoneIds[i], zoneMsa[i], zoneAreas[i], region);
//            region.addZone(zone);
//            zones.put(zoneIds[i], zone);
//        }
//    }
//
//
//    @Override
//    public void readData() {
//        super.readData();
//    }
//}
//
//
//
//
//
//
//
//
