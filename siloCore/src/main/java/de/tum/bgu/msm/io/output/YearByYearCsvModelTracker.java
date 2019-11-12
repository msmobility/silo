package de.tum.bgu.msm.io.output;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class YearByYearCsvModelTracker {

    private final Path rootDirectory;
    private final String baseFileName;
    private final String header;

    private BufferedWriter currentWriter;

    public YearByYearCsvModelTracker(Path rootDirectory, String baseFileName, String header) {
        this.rootDirectory = rootDirectory;
        this.baseFileName = baseFileName;
        this.header = header;
        try {
            Files.createDirectories(rootDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void newYear(int currentYear) {
        try {
            if(currentWriter != null) {
                currentWriter.flush();
                currentWriter.close();
            }
            currentWriter = Files.newBufferedWriter(rootDirectory.resolve(baseFileName+currentYear+".csv"));
            currentWriter.write(header);
            currentWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void end() {
        try {
            if (currentWriter != null) {
                currentWriter.flush();
                currentWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void trackRecord(String record) {
        try {
            currentWriter.write(record);
            currentWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
