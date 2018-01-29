package de.tum.bgu.msm.data;

import de.tum.bgu.msm.data.munich.GeoDataMuc;

public class DefaultPTDistances implements PTDistances{

    private final GeoDataMuc geoData;

    public DefaultPTDistances(GeoDataMuc geoData) {
        this.geoData = geoData;
    }

    @Override
    public double getDistanceToNearestPTStop(int zone) {
        return geoData.getPTDistanceOfZone(zone);
    }
}
