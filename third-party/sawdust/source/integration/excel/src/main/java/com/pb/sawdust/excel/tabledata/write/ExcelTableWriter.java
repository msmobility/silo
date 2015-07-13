package com.pb.sawdust.excel.tabledata.write;

import com.pb.sawdust.excel.PoiHelper;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.write.FileTableWriter;
import com.pb.sawdust.tabledata.write.TableWriter;
import static com.pb.sawdust.util.Range.*;

import com.pb.sawdust.util.Range;
import com.pb.sawdust.util.exceptions.RuntimeIOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The {@code ExcelTableWriter} ...
 *
 * @author crf
 *         Started 2/7/12 3:02 PM
 */
public class ExcelTableWriter extends FileTableWriter {

    public ExcelTableWriter(File file) {
        super(file);
    }

    public ExcelTableWriter(String tableFilePath) {
        super(tableFilePath);
    }

    @Override
    public void writeTable(DataTable table) {
        Sheet sheet = getWorksheet(table.getLabel());
        //write header
        Row row = sheet.createRow(0);
        int counter = 0;
        for (String column : table.getColumnLabels())
            row.createCell(counter++).setCellValue(column);
        //now write data
        DataType[] types = table.getColumnTypes();
        Range range = new Range(types.length);
        counter = 1;
        for (DataRow r : table) {
            row = sheet.createRow(counter++);
            for (int c : range)
                setCellValue(row.createCell(c),types[c],r,c);
        }
        //write out worksheet
        try (FileOutputStream fileOut = new FileOutputStream(tableFile)) {
            sheet.getWorkbook().write(fileOut);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private void setCellValue(Cell cell, DataType type, DataRow row, int index) {
        switch (type) {
            case BOOLEAN : cell.setCellValue(row.getCellAsBoolean(index)); break;
            case BYTE : cell.setCellValue(row.getCellAsByte(index)); break;
            case SHORT : cell.setCellValue(row.getCellAsShort(index)); break;
            case INT : cell.setCellValue(row.getCellAsInt(index)); break;
            case LONG : cell.setCellValue(row.getCellAsLong(index)); break;
            case FLOAT : cell.setCellValue(row.getCellAsFloat(index)); break;
            case DOUBLE : cell.setCellValue(row.getCellAsDouble(index)); break;
            case STRING : cell.setCellValue(row.getCellAsString(index)); break;
            default : throw new IllegalStateException("Shouldn't be here");
        }
    }

    private Sheet getWorksheet(String tableName) { //will be empty when we get it
        Workbook wb = PoiHelper.getWorkbook(tableFile);
        boolean finished = false;
        for (int i : range(wb.getNumberOfSheets())) {
            if (tableName.equals(wb.getSheetName(i))) {
                wb.removeSheetAt(i);
                wb.createSheet(tableName);
                wb.setSheetOrder(tableName,i);
                finished = true;
                break;
            }
        }
        if (!finished)
            wb.createSheet(tableName);
        return wb.getSheet(tableName);
    }
}
