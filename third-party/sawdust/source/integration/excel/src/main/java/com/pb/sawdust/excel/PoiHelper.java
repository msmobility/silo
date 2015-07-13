package com.pb.sawdust.excel;

import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.util.exceptions.RuntimeIOException;
import com.pb.sawdust.util.exceptions.RuntimeWrappingException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * The {@code PoiHelper} ...
 *
 * @author crf
 *         Started 2/7/12 7:10 AM
 */
public class PoiHelper {
    public static final DataType DEFAULT_NULL_DATA_TYPE = DataType.DOUBLE;


    public static String getCellAsString(Cell cell) {
        return readCell(cell).toString();
    }

    public static DataType getCellDataType(Cell cell) {
        return getCellDataType(cell,DEFAULT_NULL_DATA_TYPE);
    }

    public static DataType getCellDataType(Cell cell, DataType nullDataType) {
        int cellType = cell.getCellType();
        return getCellDataType(cellType == Cell.CELL_TYPE_FORMULA ? cell.getCachedFormulaResultType() : cellType,nullDataType);
    }

    private static DataType getCellDataType(int cellType, DataType nullDataType) {
        switch (cellType) {
            case Cell.CELL_TYPE_BLANK : return nullDataType;
            case Cell.CELL_TYPE_BOOLEAN : return DataType.BOOLEAN;
            case Cell.CELL_TYPE_ERROR : throw new IllegalArgumentException("CELL_TYPE_ERROR does not translate into a DataType.");
            case Cell.CELL_TYPE_FORMULA : throw new IllegalStateException("Shouldn't be here.");
            case Cell.CELL_TYPE_NUMERIC : return DataType.DOUBLE;
            case Cell.CELL_TYPE_STRING : return DataType.STRING;
            default : throw new IllegalStateException("Unknown cell type: " + cellType);
        }
    }

    public static Object readCell(Cell cell, DataType type) {
        return readCell(cell,type,DEFAULT_NULL_DATA_TYPE);
    }

    public static Object readCell(Cell cell, DataType type, DataType nullDataType) {
        switch (getCellDataType(cell,nullDataType)) {
            case BOOLEAN : return type.coerce(cell.getBooleanCellValue(),DataType.BOOLEAN);
            case STRING : {
                String value = cell.getStringCellValue();
                if (value.length() == 0) { //special case for empty string
                    switch (type) {
                        case BYTE : return (byte) 0;
                        case SHORT : return (short) 0;
                        case INT : return 0;
                        case LONG : return 0L;
                        case FLOAT : return 0.0f;
                        case DOUBLE : return 0.0;
                        case BOOLEAN : return false;
                    }
                }
                return type.coerce(value,DataType.STRING);
            }
            default : return type.coerce(cell.getNumericCellValue(),DataType.DOUBLE);
        }
    }

    public static Object readCell(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK : return null;
            case Cell.CELL_TYPE_BOOLEAN : return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_ERROR : return "CELL_TYPE_ERROR";
            case Cell.CELL_TYPE_FORMULA : return "CELL_TYPE_FORMULA";
            case Cell.CELL_TYPE_NUMERIC : return cell.getNumericCellValue();
            case Cell.CELL_TYPE_STRING : return cell.getStringCellValue();
            default : throw new IllegalStateException("Unknown cell type: " + cell.getCellType());
        }
    }

    public static String getCellTypeString(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK : return "CELL_TYPE_BLANK";
            case Cell.CELL_TYPE_BOOLEAN : return "CELL_TYPE_BOOLEAN";
            case Cell.CELL_TYPE_ERROR : return "CELL_TYPE_ERROR";
            case Cell.CELL_TYPE_FORMULA : return "CELL_TYPE_FORMULA";
            case Cell.CELL_TYPE_NUMERIC : return "CELL_TYPE_NUMERIC";
            case Cell.CELL_TYPE_STRING : return "CELL_TYPE_STRING";
            default : throw new IllegalStateException("Unknown cell type: " + cell.getCellType());
        }
    }

    public static Workbook readWorkbook(File file) {
        try (InputStream inp = new FileInputStream(file)) {
            Workbook wb = WorkbookFactory.create(inp);
            wb.setMissingCellPolicy(Row.CREATE_NULL_AS_BLANK );
            return wb;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } catch (InvalidFormatException e) {
            throw new RuntimeWrappingException(e);
        }
    }

    public static Sheet readSheet(File file, int sheetIndex) {
        return readWorkbook(file).getSheetAt(sheetIndex);
    }

    public static Workbook getWorkbook(File file) {
        if (file.exists())
            return readWorkbook(file);
        if (file.toPath().toString().endsWith(".xlsx"))
            return new XSSFWorkbook();
        else if (file.toPath().toString().endsWith(".xls"))
            return new HSSFWorkbook();
        throw new IllegalArgumentException("File not found, and unknown excel type: " + file);
    }
}
