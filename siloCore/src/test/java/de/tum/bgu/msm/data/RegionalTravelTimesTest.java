package de.tum.bgu.msm.data;

import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.geo.RegionImpl;
import de.tum.bgu.msm.data.geo.ZoneImpl;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import org.junit.Assert;
import org.junit.Test;
import org.matsim.api.core.v01.TransportMode;

import java.util.Random;

public class RegionalTravelTimesTest {

    @Test
    public void testRegionalTravelTimesWithSkim() {

        GeoData geoData = new DefaultGeoData();
        final RegionImpl region1 = new RegionImpl(1);
        final RegionImpl region2 = new RegionImpl(2);

        geoData.addRegion(region1);
        geoData.addRegion(region2);

        final ZoneImpl zone1 = new ZoneImpl(1, 10, region1);
        region1.addZone(zone1);
        geoData.addZone(zone1);
        final ZoneImpl zone2 = new ZoneImpl(2, 10, region1);
        region1.addZone(zone2);
        geoData.addZone(zone2);
        final ZoneImpl zone3 = new ZoneImpl(3, 10, region1);
        region1.addZone(zone3);
        geoData.addZone(zone3);
        final ZoneImpl zone4 = new ZoneImpl(4, 10, region2);
        region2.addZone(zone4);
        geoData.addZone(zone4);
        final ZoneImpl zone5 = new ZoneImpl(5, 10, region2);
        region2.addZone(zone5);
        geoData.addZone(zone5);

        Random random = new Random(42);
        IndexedDoubleMatrix2D matrix = new IndexedDoubleMatrix2D(geoData.getZones().values(), geoData.getZones().values());
        matrix.assign(argument -> random.nextDouble() * 10);
        SkimTravelTimes skimTravelTimes = new SkimTravelTimes();
        skimTravelTimes.updateSkimMatrix(matrix, TransportMode.car);
        skimTravelTimes.updateSkimMatrix(matrix, TransportMode.pt);
        skimTravelTimes.updateRegionalTravelTimes(geoData.getRegions().values(), geoData.getZones().values());

        Assert.assertEquals(0.3141, skimTravelTimes.getTravelTimeFromRegion(region1, zone1, 0, TransportMode.car), 0.001);
        Assert.assertEquals(1.7221, skimTravelTimes.getTravelTimeFromRegion(region1, zone2, 0, TransportMode.car), 0.001);
        Assert.assertEquals(5.8002, skimTravelTimes.getTravelTimeFromRegion(region1, zone3, 0, TransportMode.car), 0.001);
        Assert.assertEquals(2.0976, skimTravelTimes.getTravelTimeFromRegion(region1, zone4, 0, TransportMode.car), 0.001);
        Assert.assertEquals(5.9434, skimTravelTimes.getTravelTimeFromRegion(region1, zone5, 0, TransportMode.car), 0.001);

        Assert.assertEquals(6.6554, skimTravelTimes.getTravelTimeFromRegion(region2, zone1, 0, TransportMode.car), 0.001);
        Assert.assertEquals(2.7707, skimTravelTimes.getTravelTimeFromRegion(region2, zone2, 0, TransportMode.car), 0.001);
        Assert.assertEquals(2.7574, skimTravelTimes.getTravelTimeFromRegion(region2, zone3, 0, TransportMode.car), 0.001);
        Assert.assertEquals(3.6878, skimTravelTimes.getTravelTimeFromRegion(region2, zone4, 0, TransportMode.car), 0.001);
        Assert.assertEquals(7.2756, skimTravelTimes.getTravelTimeFromRegion(region2, zone5, 0, TransportMode.car), 0.001);

        Assert.assertEquals(0.3141, skimTravelTimes.getTravelTimeToRegion(zone1, region1, 0, TransportMode.car), 0.001);
        Assert.assertEquals(1.7221, skimTravelTimes.getTravelTimeToRegion(zone2, region1, 0, TransportMode.car), 0.001);
        Assert.assertEquals(1.7737, skimTravelTimes.getTravelTimeToRegion(zone3, region1, 0, TransportMode.car), 0.001);
        Assert.assertEquals(2.7574, skimTravelTimes.getTravelTimeToRegion(zone4, region1, 0, TransportMode.car), 0.001);
        Assert.assertEquals(2.7707, skimTravelTimes.getTravelTimeToRegion(zone5, region1, 0, TransportMode.car), 0.001);

        Assert.assertEquals(5.7104, skimTravelTimes.getTravelTimeToRegion(zone1, region2, 0, TransportMode.car), 0.001);
        Assert.assertEquals(2.0976, skimTravelTimes.getTravelTimeToRegion(zone2, region2, 0, TransportMode.car), 0.001);
        Assert.assertEquals(4.3649, skimTravelTimes.getTravelTimeToRegion(zone3, region2, 0, TransportMode.car), 0.001);
        Assert.assertEquals(3.6878, skimTravelTimes.getTravelTimeToRegion(zone4, region2, 0, TransportMode.car), 0.001);
        Assert.assertEquals(6.8322, skimTravelTimes.getTravelTimeToRegion(zone5, region2, 0, TransportMode.car), 0.001);
    }
}
