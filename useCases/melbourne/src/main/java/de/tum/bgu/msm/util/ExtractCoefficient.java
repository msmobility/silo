package de.tum.bgu.msm.util;

import de.tum.bgu.msm.data.Purpose;
import de.tum.bgu.msm.resources.Resources;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtractCoefficient {
    private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(ExtractCoefficient.class);

    // Cache: Purpose -> Map<Row, Map<Column, Value>>
    private static final Map<Purpose, Map<String, Map<String, Double>>> coefficientCache = new ConcurrentHashMap<>();

    public static Double extractCoefficient(Purpose purpose, String targetColumn, String targetRow) {
        if (purpose == null || targetColumn == null || targetRow == null) {
            logger.error("Invalid input: purpose, targetColumn, or targetRow is null.");
            return 0.0;
        }

        // Check cache first
        Map<String, Map<String, Double>> table = coefficientCache.get(purpose);
        if (table == null) {
            // Not cached: load and parse CSV
            Path csvFilePath;
            if (Resources.instance == null) {
                String mc_coefficients_path = MelbourneImplementationConfig.getMitoBaseProperties().getProperty(
                        "MC_COEFFICIENTS",
                        "input/mito/modeChoice/mc_coefficients"
                );
                if (mc_coefficients_path == null || mc_coefficients_path.isEmpty()) {
                    logger.error("Could not load MITO resources, nor 'MC_COEFFICIENTS' from project properties.");
                }
                csvFilePath = Path.of(String. format("%s_%s.csv",
                        mc_coefficients_path,
                        purpose.toString().toLowerCase()));
            } else {
                csvFilePath = Resources.instance.getModeChoiceCoefficients(purpose);
            }

            if (csvFilePath == null) {
                logger.error("CSV file path is null for the given purpose.");
                return 0.0;
            }
            table = new ConcurrentHashMap<>();
            try (CSVParser parser = new CSVParser(
                    new FileReader(String.valueOf(csvFilePath)),
                    CSVFormat.Builder.create()
                            .setHeader()
                            .setSkipHeaderRecord(true)
                            .build()
            )) {
                for (CSVRecord record : parser) {
                    String rowKey = record.get(0);
                    if (rowKey == null) continue;
                    Map<String, Double> row = new ConcurrentHashMap<>();
                    for (String col : parser.getHeaderNames()) {
                        String value = record.get(targetColumn);
                        if (value != null) {
                            try {
                                row.put(col, Double.valueOf(value));
                            } catch (NumberFormatException e) {
                                logger.warn("Invalid number format for value '{}': {}", value, e.getMessage());
                            }
                        }
                    }
                    table.put(rowKey, row);
                }
                coefficientCache.put(purpose, table);
            } catch (IOException e) {
                logger.error("Error reading CSV file: {}", e.getMessage());
                return 0.0;
            }
        }
        // Lookup value in cache
        Map<String, Double> row = table.get(targetRow);
        if (row != null) {
            Double value = row.get(targetColumn);
            if (value != null) {
                return value;
            } else {
                logger.error("Value for targetColumn '{}' is null.", targetColumn);
            }
        }
        return 0.0; // Return 0 if no match is found
    }
}
