package de.tum.bgu.msm.health.io;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class CRSDetector {

    private static final Logger logger = LogManager.getLogger(CRSDetector.class);

    public static String detectCRS(String filePath) {
        try {
            // Load the shapefile
            File file = new File(filePath);
            ShapefileDataStore shapefileDataStore = new ShapefileDataStore(file.toURI().toURL());
            shapefileDataStore.setCharset(StandardCharsets.UTF_8);

            // Extract the CRS from the shapefile schema
            var crs = shapefileDataStore.getSchema().getCoordinateReferenceSystem();

            // Dispose of the data store
            shapefileDataStore.dispose();

            // Convert CRS to EPSG code or WKT
            // Fallback to a default CRS if none is found
            // Default to WGS84
            return CRS.toSRS(Objects.requireNonNullElse(crs, DefaultGeographicCRS.WGS84)); // Returns EPSG code (e.g., "EPSG:28355")
        } catch (Exception e) {
            logger.error("Failed to detect CRS from the source data: {}", filePath, e);
            throw new RuntimeException("Failed to detect CRS from the source data.", e);
        }
    }
}