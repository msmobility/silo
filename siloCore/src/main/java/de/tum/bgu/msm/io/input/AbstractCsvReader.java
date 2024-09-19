package de.tum.bgu.msm.io.input;

import de.tum.bgu.msm.data.DataSet;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractCsvReader extends AbstractInputReader{

    private static final Logger logger = Logger.getLogger(AbstractCsvReader.class);

    private BufferedReader reader;

    private int numberOfRecords = 0;

    protected AbstractCsvReader(DataSet dataSet) {
        super(dataSet);
    }

    protected abstract void processHeader(String[] header);

    protected abstract void processRecord(String[] record);

    public void read(Path filePath, String delimiter) {
        initializeReader(filePath, delimiter);
        try {
            String record;
            while ((record = reader.readLine()) != null) {
                numberOfRecords++;
                processRecord(record.split(delimiter));
            }
        } catch (IOException e) {
            logger.error("Error parsing record number " + numberOfRecords + ": " + e.getMessage(), e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info(this.getClass().getSimpleName() + ": Read " + numberOfRecords + " records.");
    }

    private void initializeReader(Path filePath, String delimiter) {
        try {

            reader = Files.newBufferedReader(filePath,  StandardCharsets.ISO_8859_1);
            processHeader(reader.readLine().split(delimiter));
        } catch (IOException e) {
            logger.error("Error initializing csv reader: " + e.getMessage(), e);
        }
    }
}
