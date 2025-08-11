package de.tum.bgu.msm.util;

import de.tum.bgu.msm.common.datafile.TableDataSet;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CsvWriter {
    public static void writeTableDataSetToCSV(TableDataSet table, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write column labels
            String[] colLabels = table.getColumnLabels();
            writer.println(String.join(",", colLabels));

            // Write data rows
            int rowCount = table.getRowCount();
            int colCount = table.getColumnCount();
            for (int row = 1; row <= rowCount; row++) {
                StringBuilder sb = new StringBuilder();
                for (int col = 1; col <= colCount; col++) {
                    sb.append(table.getValueAt(row, col));
                    if (col < colCount) {
                        sb.append(",");
                    }
                }
                writer.println(sb.toString());
            }
        }
    }
}
