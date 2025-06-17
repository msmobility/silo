package de.tum.bgu.msm.util;

import com.opencsv.exceptions.CsvValidationException;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import java.io.*;
import java.util.*;
import com.opencsv.CSVReader;

public class RobustCSVReader extends TableDataSet {

    private final List<String[]> rows = new ArrayList<>();
    private String[] headers;

    public RobustCSVReader(String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            headers = reader.readNext();
            String[] line;
            while ((line = reader.readNext()) != null) {
                rows.add(line);
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Error reading CSV: " + fileName, e);
        }
    }

    public String[] getColumnAsString(String column) {
        int idx = getColumnIndex(column);
        String[] result = new String[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            result[i] = idx >= 0 && idx < rows.get(i).length ? rows.get(i)[idx] : null;
        }
        return result;
    }

    public int[] getColumnAsInt(String column) {
        String[] strCol = getColumnAsString(column);
        int[] result = new int[strCol.length];
        for (int i = 0; i < strCol.length; i++) {
            try {
                result[i] = (strCol[i] == null || strCol[i].isEmpty() || strCol[i].equalsIgnoreCase("NA")) ? Integer.MIN_VALUE : Integer.parseInt(strCol[i]);
            } catch (NumberFormatException e) {
                result[i] = Integer.MIN_VALUE;
            }
        }
        return result;
    }

    public double[] getColumnAsDouble(String column) {
        String[] strCol = getColumnAsString(column);
        double[] result = new double[strCol.length];
        for (int i = 0; i < strCol.length; i++) {
            try {
                result[i] = (strCol[i] == null || strCol[i].isEmpty() || strCol[i].equalsIgnoreCase("NA")) ? Double.NaN : Double.parseDouble(strCol[i]);
            } catch (NumberFormatException e) {
                result[i] = Double.NaN;
            }
        }
        return result;
    }

    private int getColumnIndex(String column) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase(column)) {
                return i;
            }
        }
        return -1;
    }
}