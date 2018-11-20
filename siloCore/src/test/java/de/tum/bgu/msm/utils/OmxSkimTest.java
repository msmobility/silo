package de.tum.bgu.msm.utils;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.data.Development;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.OmxTravelTimesWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.matsim.api.core.v01.TransportMode;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.util.Collections;

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
            public int getMsa() {
                return 0;
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
            public Coordinate getRandomCoordinate() {
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
            public double getTravelTime(int origin, int destination, double timeOfDay_s, String mode) {
                return 99;
            }

            @Override
            public double getTravelTime(Location origin, Location destination, double timeOfDay_s, String mode) {
                return 99;
            }

            @Override
            public double getTravelTimeToRegion(Location origin, Region destination, double timeOfDay_s, String mode) {
                return 99;
            }
        }, Collections.singletonList(mockZone)).writeTravelTimes("test/testskim.omx", "test", TransportMode.car);


        SkimTravelTimes travelTimes = new SkimTravelTimes();
        travelTimes.readSkim(TransportMode.car, "test/testskim.omx", "test", 1.0);
        Assert.assertEquals(99, travelTimes.getTravelTime(mockZone, mockZone, 0, TransportMode.car), 0.);
        new File("./test/testskim.omx").delete();
    }
}
