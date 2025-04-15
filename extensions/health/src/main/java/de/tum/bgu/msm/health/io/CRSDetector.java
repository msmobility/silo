package de.tum.bgu.msm.health.io;

import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;

public class CRSDetector {

    private static final Logger logger = LogManager.getLogger(CRSDetector.class);

    public static String detectCRS(String filePath) {
        try {
            // Load the GeoJSON file
            File file = new File(filePath);
            FeatureJSON featureJSON = new FeatureJSON();

            // Read the feature collection schema
            var featureType = featureJSON.readFeatureCollectionSchema(new FileInputStream(file), true);

            // Extract CRS from the schema
            var crs = featureType.getCoordinateReferenceSystem();

            // Convert CRS to EPSG code or WKT
            return CRS.toSRS(Objects.requireNonNullElse(crs, DefaultGeographicCRS.WGS84)); // Default to WGS84
        } catch (Exception e) {
            logger.error("Failed to detect CRS from the GeoJSON file: {}", filePath, e);
            throw new RuntimeException("Failed to detect CRS from the GeoJSON file.", e);
        }
    }
}