package de.tum.bgu.msm.utils;

import com.google.common.collect.Lists;
import de.tum.bgu.msm.data.development.Development;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.output.OmxTravelTimesWriter;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.TransportMode;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

public class OmxSkimTest {

    private Zone mockZone;

    @Before
    public void init() {
        SiloUtil.loadHdf5Lib();
        mockZone = new Zone() {

            @Override
            public Region getRegion() {
                return null;
            }

            @Override
            public float getArea_sqmi() {
                return 0;
            }

            @Override
            public void setZoneFeature(SimpleFeature zoneFeature) {

            }

            @Override
            public SimpleFeature getZoneFeature() {
                return null;
            }

            @Override
            public Coordinate getRandomCoordinate(Random random) {
                return null;
            }

            @Override
            public Development getDevelopment() {
                return null;
            }

            @Override
            public void setDevelopment(Development development) {

            }

            @Override
            public Map<String, Object> getAttributes() {
                return null;
            }

            @Override
            public int getId() {
                return 10;
            }

            @Override
            public int getZoneId() {
                return 10;
            }
        };
    }

    @Test
    public void writeAndReadTravelTimeMatrixTest() {
        new OmxTravelTimesWriter(new TravelTimes() {

            @Override
            public double getTravelTime(Location origin, Location destination, double timeOfDay_s, String mode) {
                return 99;
            }

            @Override
            public double getTravelTimeFromRegion(Region region, Zone zone, double v, String s) {
                return 0;
            }

            @Override
            public double getTravelTimeToRegion(Zone zone, Region region, double v, String s) {
                return 0;
            }


            @Override
            public IndexedDoubleMatrix2D getPeakSkim(String s) {
                final IndexedDoubleMatrix2D matrix = new IndexedDoubleMatrix2D(Lists.newArrayList(mockZone), Lists.newArrayList(mockZone));
                matrix.setIndexed(mockZone.getId(), mockZone.getZoneId(), getTravelTime(mockZone, mockZone,0, TransportMode.car));
                return matrix;
            }

            @Override
            public TravelTimes duplicate() {
                return null;
            }
        }, Collections.singletonList(mockZone)).writeTravelTimes("test/testskim.omx", "test", TransportMode.car);


        SkimTravelTimes travelTimes = new SkimTravelTimes();
        travelTimes.readSkim(TransportMode.car, "test/testskim.omx", "test", 1.0);
        Assert.assertEquals(99, travelTimes.getTravelTime(mockZone, mockZone, 0, TransportMode.car), 0.);
        new File("./test/testskim.omx").delete();
    }
}
