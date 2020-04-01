package de.tum.bgu.msm.io.output;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

public class YearByYearCsvModelTracker {

    private final Path rootDirectory;
    private final String baseFileName;
    private final String header;

    private GZIPOutputStream currentWriter;

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
            if (currentWriter != null) {
                currentWriter.flush();
                currentWriter.close();
            }

            OutputStream fos = Files.newOutputStream(rootDirectory.resolve(baseFileName + currentYear + ".csv.gz"));
            currentWriter = new GZIPOutputStream(fos);

            currentWriter.write(header.getBytes());
            currentWriter.write("\n".getBytes());
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
            currentWriter.write(record.getBytes());
            currentWriter.write("\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
