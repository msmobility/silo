package de.tum.bgu.msm.health.test;

import de.tum.bgu.msm.health.io.CRSDetector;
import org.junit.Before;
import org.junit.Test;


import java.net.URL;

import static org.junit.Assert.assertEquals;

public class CRSDetectorTest {

    private String resourcePathString;

    @Before
    public void setUp() {
        resourcePathString = getClass().getResource("CRSDetectorTest.class").getPath().replace("target/classes","src/main/java").replace("CRSDetectorTest.class", "");
        if (resourcePathString == null) {
            throw new RuntimeException("Base resource URL not found.");
        }
    }

    @Test
    public void testDetectEPSG27700_ValidGeoJSON() {
        String validGeoJSONPath = resourcePathString+"resources/test_epsg_27700.geojson";
        String expectedCRS = "EPSG:27700";
        String detectedCRS = CRSDetector.detectCRS(validGeoJSONPath);
        System.out.println("Expected CRS: " + expectedCRS + "\nDetected CRS: " + detectedCRS);
        assertEquals("The detected CRS should match the expected CRS.", expectedCRS, detectedCRS);
    }

    @Test
    public void testDetectEPSG28355_ValidGeoJSON() {
        String validGeoJSONPath = resourcePathString+"resources/test_epsg_28355.geojson";
        String expectedCRS = "EPSG:28355";
        String detectedCRS = CRSDetector.detectCRS(validGeoJSONPath);
        System.out.println("Expected CRS: " + expectedCRS + "\nDetected CRS: " + detectedCRS);
        assertEquals("The detected CRS should match the expected CRS.", expectedCRS, detectedCRS);
    }

    @Test(expected = RuntimeException.class)
    public void testDetectCRS_InvalidFilePath() {
        String invalidFilePath = resourcePathString+"resources/non_existent_file.json";
        CRSDetector.detectCRS(invalidFilePath);
    }

    @Test
    public void testDetectCRS_NoCRSInGeoJSON() {
        String noCRSFilePath = resourcePathString+"resources/test_no_crs.geojson";
        String expectedCRS = "CRS:84";
        String detectedCRS = CRSDetector.detectCRS(noCRSFilePath);
        System.out.println("Expected CRS: " + expectedCRS + "\nDetected CRS: " + detectedCRS);
        assertEquals("The detected CRS should fall back to CRS 84 (equivalent to WGS84, but with Long-Lat instead of Lat-Long coordinates.", expectedCRS, detectedCRS);
    }
}