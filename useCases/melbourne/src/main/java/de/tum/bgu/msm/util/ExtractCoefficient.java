package de.tum.bgu.msm.util;

import de.tum.bgu.msm.data.Purpose;
import de.tum.bgu.msm.resources.Resources;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class ExtractCoefficient {
    private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(ExtractCoefficient.class);

    public static Double extractCoefficient(Purpose purpose, String targetColumn, String targetRow) {
        if (purpose == null || targetColumn == null || targetRow == null) {
            logger.error("Invalid input: purpose, targetColumn, or targetRow is null.");
            return 0.0;
        }

        Path csvFilePath = Resources.instance.getModeChoiceCoefficients(purpose);
        if (csvFilePath == null) {
            logger.error("CSV file path is null for the given purpose.");
            return 0.0;
        }

        try (CSVParser parser = new CSVParser(
                new FileReader(String.valueOf(csvFilePath)),
                CSVFormat.Builder.create()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build()
        )) {
            for (CSVRecord record : parser) {
                if (record.get(0) != null && record.get(0).equalsIgnoreCase(targetRow)) {
                    String value = record.get(targetColumn);
                    if (value != null) {
                        try {
                            return Double.valueOf(value); // Safely parse the value
                        } catch (NumberFormatException e) {
                            logger.error("Error parsing value to Double: {}", e.getMessage());
                        }
                    } else {
                        logger.error("Value for targetColumn '{}' is null.", targetColumn);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error reading CSV file: {}", e.getMessage());
        }

        return 0.0; // Return null if no match is found
    }
}
