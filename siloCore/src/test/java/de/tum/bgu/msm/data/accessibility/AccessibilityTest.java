package de.tum.bgu.msm.data.accessibility;

import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.geo.RegionImpl;
import de.tum.bgu.msm.data.geo.ZoneImpl;
import de.tum.bgu.msm.data.job.JobData;
import de.tum.bgu.msm.data.job.JobDataImpl;
import de.tum.bgu.msm.data.job.JobFactory;
import de.tum.bgu.msm.data.job.JobFactoryImpl;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import org.junit.Assert;
import org.junit.Test;
import org.matsim.api.core.v01.TransportMode;

import java.util.Random;

public class AccessibilityTest {

    public static final Properties PROPERTIES = Properties.initializeProperties("./test/silo.properties");

    @Test
    public void testAccessibilitiesWithDummyTravelTimes() {


        GeoData geoData = new DefaultGeoData();
        final RegionImpl region1 = new RegionImpl(1);
        final RegionImpl region2 = new RegionImpl(2);
        final RegionImpl region3 = new RegionImpl(3);

        geoData.addRegion(region1);
        geoData.addRegion(region2);
        geoData.addRegion(region3);

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
        final ZoneImpl zone6 = new ZoneImpl(6, 10, region3);
        region3.addZone(zone6);
        geoData.addZone(zone6);
        final ZoneImpl zone7 = new ZoneImpl(7, 10, region3);
        region3.addZone(zone7);
        geoData.addZone(zone7);
        final ZoneImpl zone8 = new ZoneImpl(8, 10, region3);
        region3.addZone(zone8);
        geoData.addZone(zone8);

        Random random = new Random(42);
        final TravelTimes travelTimes = new TravelTimes() {
            @Override
            public double getTravelTime(Location location, Location location1, double v, String s) {
                return random.nextDouble() * 10;
            }

            @Override
            public double getTravelTimeFromRegion(Region region, Zone zone, double v, String s) {
                return random.nextDouble() * 10;
            }

            @Override
            public double getTravelTimeToRegion(Zone zone, Region region, double v, String s) {
                return random.nextDouble() * 10;
            }

            @Override
            public IndexedDoubleMatrix2D getPeakSkim(String s) {
                IndexedDoubleMatrix2D matrix = new IndexedDoubleMatrix2D(geoData.getZones().values(), geoData.getZones().values());
                matrix.assign(argument -> random.nextDouble() * 10);
                return matrix;
            }

            @Override
            public TravelTimes duplicate() {
                return null;
            }
        };
        final DwellingData dwellingData = new DwellingDataImpl();
        final JobData jobData = new JobDataImpl();

        DwellingFactory factory = new DwellingFactoryImpl();
        dwellingData.addDwelling(factory.createDwelling(1, 1, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));
        dwellingData.addDwelling(factory.createDwelling(2, 1, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));
        dwellingData.addDwelling(factory.createDwelling(3, 2, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));
        dwellingData.addDwelling(factory.createDwelling(4, 5, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));
        dwellingData.addDwelling(factory.createDwelling(5, 7, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));
        dwellingData.addDwelling(factory.createDwelling(6, 7, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));
        dwellingData.addDwelling(factory.createDwelling(7, 8, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));
        dwellingData.addDwelling(factory.createDwelling(8, 8, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));

        JobFactory jobFactory = new JobFactoryImpl();
        jobData.addJob(jobFactory.createJob(1, 1, null, -1, null));
        jobData.addJob(jobFactory.createJob(2, 2, null, -1, null));
        jobData.addJob(jobFactory.createJob(3, 3, null, -1, null));
        jobData.addJob(jobFactory.createJob(4, 4, null, -1, null));
        jobData.addJob(jobFactory.createJob(5, 5, null, -1, null));
        jobData.addJob(jobFactory.createJob(6, 6, null, -1, null));
        jobData.addJob(jobFactory.createJob(7, 7, null, -1, null));
        jobData.addJob(jobFactory.createJob(8, 8, null, -1, null));
        jobData.addJob(jobFactory.createJob(9, 1, null, -1, null));
        jobData.addJob(jobFactory.createJob(10, 4, null, -1, null));
        jobData.addJob(jobFactory.createJob(11, 7, null, -1, null));


        AccessibilityImpl accessibility = new AccessibilityImpl(geoData, travelTimes, PROPERTIES,
                dwellingData, jobData);

        accessibility.setup();
        accessibility.calculateHansenAccessibilities(2010);

        Assert.assertEquals(46.0049, accessibility.getAutoAccessibilityForZone(zone1), 0.001);
        Assert.assertEquals(65.1538, accessibility.getAutoAccessibilityForZone(zone2), 0.001);
        Assert.assertEquals(100.000, accessibility.getAutoAccessibilityForZone(zone3), 0.001);
        Assert.assertEquals(63.8752, accessibility.getAutoAccessibilityForZone(zone4), 0.001);
        Assert.assertEquals(86.2602, accessibility.getAutoAccessibilityForZone(zone5), 0.001);
        Assert.assertEquals(57.4189, accessibility.getAutoAccessibilityForZone(zone6), 0.001);
        Assert.assertEquals(57.4118, accessibility.getAutoAccessibilityForZone(zone7), 0.001);
        Assert.assertEquals(72.7557, accessibility.getAutoAccessibilityForZone(zone8), 0.001);


        Assert.assertEquals(52.3879, accessibility.getRegionalAccessibility(region1), 0.001);
        Assert.assertEquals(86.2602, accessibility.getRegionalAccessibility(region2), 0.001);
        Assert.assertEquals(65.0838, accessibility.getRegionalAccessibility(region3), 0.001);

    }


    @Test
    public void testAccessibilitiesWithSkimTravelTimes() {

        GeoData geoData = new DefaultGeoData();
        final RegionImpl region1 = new RegionImpl(1);
        final RegionImpl region2 = new RegionImpl(2);
        final RegionImpl region3 = new RegionImpl(3);

        geoData.addRegion(region1);
        geoData.addRegion(region2);
        geoData.addRegion(region3);

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
        final ZoneImpl zone6 = new ZoneImpl(6, 10, region3);
        region3.addZone(zone6);
        geoData.addZone(zone6);
        final ZoneImpl zone7 = new ZoneImpl(7, 10, region3);
        region3.addZone(zone7);
        geoData.addZone(zone7);
        final ZoneImpl zone8 = new ZoneImpl(8, 10, region3);
        region3.addZone(zone8);
        geoData.addZone(zone8);

        Random random = new Random(42);
        IndexedDoubleMatrix2D matrix = new IndexedDoubleMatrix2D(geoData.getZones().values(), geoData.getZones().values());
        matrix.assign(argument -> random.nextDouble() * 10);
        SkimTravelTimes skimTravelTimes = new SkimTravelTimes();
        skimTravelTimes.updateSkimMatrix(matrix, TransportMode.car);
        skimTravelTimes.updateSkimMatrix(matrix, TransportMode.pt);
        skimTravelTimes.updateRegionalTravelTimes(geoData.getRegions().values(), geoData.getZones().values());

        final DwellingData dwellingData = new DwellingDataImpl();
        final JobData jobData = new JobDataImpl();

        DwellingFactory factory = new DwellingFactoryImpl();
        dwellingData.addDwelling(factory.createDwelling(1, 1, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));
        dwellingData.addDwelling(factory.createDwelling(2, 1, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));
        dwellingData.addDwelling(factory.createDwelling(3, 2, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));
        dwellingData.addDwelling(factory.createDwelling(4, 5, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));
        dwellingData.addDwelling(factory.createDwelling(5, 7, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));
        dwellingData.addDwelling(factory.createDwelling(6, 7, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));
        dwellingData.addDwelling(factory.createDwelling(7, 8, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));
        dwellingData.addDwelling(factory.createDwelling(8, 8, null, -1, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, 2, 2, 1000, 1985));

        JobFactory jobFactory = new JobFactoryImpl();
        jobData.addJob(jobFactory.createJob(1, 1, null, -1, null));
        jobData.addJob(jobFactory.createJob(2, 2, null, -1, null));
        jobData.addJob(jobFactory.createJob(3, 3, null, -1, null));
        jobData.addJob(jobFactory.createJob(4, 4, null, -1, null));
        jobData.addJob(jobFactory.createJob(5, 5, null, -1, null));
        jobData.addJob(jobFactory.createJob(6, 6, null, -1, null));
        jobData.addJob(jobFactory.createJob(7, 7, null, -1, null));
        jobData.addJob(jobFactory.createJob(8, 8, null, -1, null));
        jobData.addJob(jobFactory.createJob(9, 1, null, -1, null));
        jobData.addJob(jobFactory.createJob(10, 4, null, -1, null));
        jobData.addJob(jobFactory.createJob(11, 7, null, -1, null));

        AccessibilityImpl accessibility = new AccessibilityImpl(geoData, skimTravelTimes, PROPERTIES,
                dwellingData, jobData);

        accessibility.setup();
        accessibility.calculateHansenAccessibilities(2010);

        Assert.assertEquals(46.0049, accessibility.getAutoAccessibilityForZone(zone1), 0.001);
        Assert.assertEquals(65.1538, accessibility.getAutoAccessibilityForZone(zone2), 0.001);
        Assert.assertEquals(100.000, accessibility.getAutoAccessibilityForZone(zone3), 0.001);
        Assert.assertEquals(63.8752, accessibility.getAutoAccessibilityForZone(zone4), 0.001);
        Assert.assertEquals(86.2602, accessibility.getAutoAccessibilityForZone(zone5), 0.001);
        Assert.assertEquals(57.4189, accessibility.getAutoAccessibilityForZone(zone6), 0.001);
        Assert.assertEquals(57.4118, accessibility.getAutoAccessibilityForZone(zone7), 0.001);
        Assert.assertEquals(72.7557, accessibility.getAutoAccessibilityForZone(zone8), 0.001);


        Assert.assertEquals(52.3879, accessibility.getRegionalAccessibility(region1), 0.001);
        Assert.assertEquals(86.2602, accessibility.getRegionalAccessibility(region2), 0.001);
        Assert.assertEquals(65.0838, accessibility.getRegionalAccessibility(region3), 0.001);
    }
}
