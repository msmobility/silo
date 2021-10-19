package de.tum.bgu.msm.matsim;

import de.tum.bgu.msm.container.DefaultDataContainer;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.development.Development;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.geo.RegionImpl;
import de.tum.bgu.msm.data.geo.ZoneImpl;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.vehicles.Vehicle;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Map;
import java.util.Random;

public class RegionalTravelTimesTest {

    @Test
    public void testRegionalTravelTimesWithMatsim() {

        GeoData geoData = new DefaultGeoData();
        final RegionImpl region1 = new RegionImpl(1);
        final RegionImpl region2 = new RegionImpl(2);

        geoData.addRegion(region1);
        geoData.addRegion(region2);

        final MockZone zone1 = new MockZone(1, 10, region1);
        region1.addZone(zone1);
        geoData.addZone(zone1);
        final MockZone zone2 = new MockZone(2, 10, region1);
        region1.addZone(zone2);
        geoData.addZone(zone2);
        final MockZone zone3 = new MockZone(3, 10, region1);
        region1.addZone(zone3);
        geoData.addZone(zone3);
        final MockZone zone4 = new MockZone(4, 10, region2);
        region2.addZone(zone4);
        geoData.addZone(zone4);
        final MockZone zone5 = new MockZone(5, 10, region2);
        region2.addZone(zone5);
        geoData.addZone(zone5);

        Random random = new Random(42);
        IndexedDoubleMatrix2D matrix = new IndexedDoubleMatrix2D(geoData.getZones().values(), geoData.getZones().values());
        matrix.assign(argument -> random.nextDouble() * 10);

        final Config config = ConfigUtils.createConfig();
        Properties properties = Properties.initializeProperties("./test/silo.properties");
        MatsimTravelTimesAndCosts travelTimes = new MatsimTravelTimesAndCosts(config);
        DefaultDataContainer dataContainer = new DefaultDataContainer(geoData, null,
                null, null, travelTimes, null, null, properties);
        final Network network = getNetwork();
        final MatsimData matsimData = new MatsimData(config, properties, ZoneConnectorManagerImpl.ZoneConnectorMethod.RANDOM, dataContainer, network, null);

        matsimData.update(new TravelDisutility() {
            @Override
            public double getLinkTravelDisutility(Link link, double v, Person person, Vehicle vehicle) {
                return link.getLength();
            }

            @Override
            public double getLinkMinimumTravelDisutility(Link link) {
                return link.getLength();
            }
        }, (link, v, person, vehicle) -> link.getLength() / link.getFreespeed());
        travelTimes.initialize(geoData, matsimData);
        travelTimes.update(matsimData);


        Assert.assertEquals(0.0, travelTimes.getTravelTimeFromRegion(region1, zone1, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0471, travelTimes.getTravelTimeFromRegion(region1, zone2, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0471, travelTimes.getTravelTimeFromRegion(region1, zone3, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0471, travelTimes.getTravelTimeFromRegion(region1, zone4, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0942, travelTimes.getTravelTimeFromRegion(region1, zone5, 0, TransportMode.car), 0.001);

        Assert.assertEquals(0.1414, travelTimes.getTravelTimeFromRegion(region2, zone1, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0942, travelTimes.getTravelTimeFromRegion(region2, zone2, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0471, travelTimes.getTravelTimeFromRegion(region2, zone3, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0471, travelTimes.getTravelTimeFromRegion(region2, zone4, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0471, travelTimes.getTravelTimeFromRegion(region2, zone5, 0, TransportMode.car), 0.001);

        Assert.assertEquals(0.0, travelTimes.getTravelTimeToRegion(zone1, region1, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0471, travelTimes.getTravelTimeToRegion(zone2, region1, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0471, travelTimes.getTravelTimeToRegion(zone3, region1, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0471, travelTimes.getTravelTimeToRegion(zone4, region1, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0942, travelTimes.getTravelTimeToRegion(zone5, region1, 0, TransportMode.car), 0.001);

        Assert.assertEquals(0.1414, travelTimes.getTravelTimeToRegion(zone1, region2, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0942, travelTimes.getTravelTimeToRegion(zone2, region2, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0471, travelTimes.getTravelTimeToRegion(zone3, region2, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0471, travelTimes.getTravelTimeToRegion(zone4, region2, 0, TransportMode.car), 0.001);
        Assert.assertEquals(0.0471, travelTimes.getTravelTimeToRegion(zone5, region2, 0, TransportMode.car), 0.001);
    }

    private Network getNetwork() {
        final Network network = NetworkUtils.createNetwork();
        final NetworkFactory factory = network.getFactory();

        final Node node0 = factory.createNode(Id.createNodeId(0), new Coord(0, 0));
        network.addNode(node0);
        for (int i = 1; i <= 10; i++) {
            final Node node = factory.createNode(Id.createNodeId(i), new Coord(i * 2, i * 2));
            network.addNode(node);
            final Link link = factory.createLink(Id.createLinkId(i),
                    network.getNodes().get(Id.createNodeId(i - 1)),
                    network.getNodes().get(Id.createNodeId(i)));
            final Link linkR = factory.createLink(Id.createLinkId(i + "r"),
                    network.getNodes().get(Id.createNodeId(i)),
                    network.getNodes().get(Id.createNodeId(i - 1)));
            network.addLink(link);
            network.addLink(linkR);
        }
        return network;
    }

    /**
     * Mock zone impl with coordinate based on id
     */
    private static class MockZone implements Zone {

        private ZoneImpl delegate;

        MockZone(int id, float area, Region region) {
            this.delegate = new ZoneImpl(id, area, region);
        }

        @Override
        public int getZoneId() {
            return delegate.getZoneId();
        }

        @Override
        public Region getRegion() {
            return delegate.getRegion();
        }

        @Override
        public float getArea_sqmi() {
            return delegate.getArea_sqmi();
        }

        @Override
        public SimpleFeature getZoneFeature() {
            return delegate.getZoneFeature();
        }

        @Override
        public Coordinate getRandomCoordinate(Random random) {
            return new Coordinate(getId() * 2, getId() * 2);
        }

        @Override
        public void setZoneFeature(SimpleFeature zoneFeature) {
            delegate.setZoneFeature(zoneFeature);
        }

        @Override
        public Development getDevelopment() {
            return delegate.getDevelopment();
        }

        @Override
        public void setDevelopment(Development development) {
            delegate.setDevelopment(development);
        }

        @Override
        public Map<String, Object> getAttributes() {
            return null;
        }

        @Override
        public int getId() {
            return delegate.getId();
        }

        @Override
        public String toString() {
            return delegate.toString();
        }

        @Override
        public int hashCode() {
            return delegate.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return delegate.equals(o);
        }
    }
}
