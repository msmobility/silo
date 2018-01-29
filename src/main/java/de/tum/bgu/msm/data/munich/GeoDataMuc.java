package de.tum.bgu.msm.data.munich;

import de.tum.bgu.msm.data.AbstractDefaultGeoData;
import org.matsim.api.core.v01.Coord;

/**
 * Interface to store zonal, county and regional data used by the SILO Model
 * @author Ana Moreno and Rolf Moeckel, Technical University of Munich
 * Created on 5 April 2017 in Munich
 **/

public class GeoDataMuc extends AbstractDefaultGeoData {

    private final String ptDistanceColName = "distanceToTransit";
    protected Coord[] centroids;

    public GeoDataMuc() {
        super("Zone", "Region");
    }

    @Override
    public void setInitialData () {
        super.setInitialData();
        createCentroids();
    }

    private void createCentroids() {
        centroids = new Coord[zones.length];
        for(int zone: zones) {
            Coord centroid = new Coord(zonalData.getIndexedValueAt(zone, "centroidX"),
                    zonalData.getIndexedValueAt(zone, "centroidY"));
            centroids[getZoneIndex(zone)] = centroid;
        }
    }

    public double getPTDistanceOfZone(int zone) {
        return zonalData.getIndexedValueAt(zone, ptDistanceColName);
    }

    public Coord getCentroidOfZone(int zone) {
        return centroids[getZoneIndex(zone)];
    }
}








