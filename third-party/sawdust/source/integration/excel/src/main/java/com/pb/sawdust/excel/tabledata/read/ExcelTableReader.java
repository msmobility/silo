package com.pb.sawdust.excel.tabledata.read;

import com.pb.sawdust.calculator.Function1;
import com.pb.sawdust.excel.PoiHelper;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.read.FileTableReader;
import static com.pb.sawdust.util.Range.*;

import com.pb.sawdust.tabledata.read.TableReader;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.collections.iterators.TransformingIterator;
import com.pb.sawdust.util.exceptions.RuntimeIOException;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * The {@code ExcelTableReader} ...
 *
 * @author crf
 *         Started 2/7/12 5:50 AM
 */
public class ExcelTableReader extends AbstractExcelTableReader {
    public static AbstractExcelTableReader excelTableReader(String tableFilePath, String worksheetName) {
        try {
            return new ExcelTableReader(tableFilePath,worksheetName);
        } catch (OldExcelFormatException e) {
            return new ExcelBiff5TableReader(tableFilePath,worksheetName);
        }
    }

    public static AbstractExcelTableReader excelTableReader(String tableFilePath, int worksheetIndex) {
        try {
            return new ExcelTableReader(tableFilePath,worksheetIndex);
        } catch (OldExcelFormatException e) {
            return new ExcelBiff5TableReader(tableFilePath,worksheetIndex);
        }
    }

    public static AbstractExcelTableReader excelTableReader(String tableFilePath) {
        try {
            return new ExcelTableReader(tableFilePath);
        } catch (OldExcelFormatException e) {
            return new ExcelBiff5TableReader(tableFilePath);
        }
    }

    private final int worksheetIndex;

    private int typeDepth = 1;
    private DataType nullDataType = PoiHelper.DEFAULT_NULL_DATA_TYPE;
    private int[] dataIndices;
    private String[] columnNames;
    private DataType[] types;

    private ExcelTableReader(String tableFilePath, String worksheetName) {
        super(tableFilePath,worksheetName);
        worksheetIndex = getWorksheetIndex(worksheetName);
    }

    private ExcelTableReader(String tableFilePath, int worksheetIndex) {
        super(tableFilePath,getWorksheetName(tableFilePath,worksheetIndex));
        this.worksheetIndex = worksheetIndex;
    }

    private ExcelTableReader(String tableFilePath) {
        this(tableFilePath,0);
    }

    public void setTypeDepth(int typeDepth) {
        if (typeDepth < 1)
            throw new IllegalArgumentException("Type depth must be greater than 0: " + typeDepth);
        this.typeDepth = typeDepth;
    }

    public void setNullDataType(DataType nullDataType) {
        this.nullDataType = nullDataType;
    }

    private  int getWorksheetIndex(String worksheetName) {
        return PoiHelper.readWorkbook(tableFile).getSheetIndex(worksheetName);
    }

    private void loadStructureData() {
        if (columnNames == null) {
            Sheet sheet = PoiHelper.readSheet(tableFile,worksheetIndex);
            Iterator<Row> iterator = sheet.iterator();
            Row row = iterator.next();
            List<Integer> dataIndices = new LinkedList<>();
            List<String> headers = new LinkedList<>();
            for (Cell cell : row) {
                dataIndices.add(cell.getColumnIndex());
                headers.add(PoiHelper.getCellAsString(cell));
            }
            this.dataIndices = ArrayUtil.toIntArray(dataIndices);
            columnNames = headers.toArray(new String[headers.size()]);
            types = new DataType[columnNames.length];

            List<Integer> notTyped = new LinkedList<>(Arrays.asList(ArrayUtil.toIntegerArray(range(types.length).getRangeArray())));
            int typeDepth = this.typeDepth;
            while((typeDepth > 0 || notTyped.size() > 0) && iterator.hasNext()) {
                row = iterator.next();
                Iterator<Integer> notTypedIterator = notTyped.iterator();
                if (typeDepth <= 0) {
                    while (notTypedIterator.hasNext()) {
                        int i = notTypedIterator.next();
                        if (row.getCell(this.dataIndices[i]).getCellType() != Cell.CELL_TYPE_BLANK) {
                            types[i] = DataType.getInclusiveDataType(types[i],PoiHelper.getCellDataType(row.getCell(this.dataIndices[i]),nullDataType));
                            notTypedIterator.remove();
                        }
                    }
                } else {
                    for (int i : range(types.length)) {
                        if (row.getCell(this.dataIndices[i]).getCellType() != Cell.CELL_TYPE_BLANK) {
                            types[i] = DataType.getInclusiveDataType(types[i],PoiHelper.getCellDataType(row.getCell(this.dataIndices[i]),nullDataType));
                            notTyped.remove((Object) (Integer) i);
                        }
                    }
                }
                typeDepth--;
            }

            for (int i : notTyped)
                types[i] = nullDataType;
        }
    }


    @Override
    protected String[] getAllColumnNames() {
        loadStructureData();
        return columnNames;
    }

    @Override
    protected DataType[] getAllColumnTypes() {
        loadStructureData();
        return types;
    }

    @Override
    protected Iterator<Object[]> getDataIterator() {
        final int[] indices = dataIndices;
        Iterator<Row> it = PoiHelper.readSheet(tableFile,worksheetIndex).iterator();
        it.next(); //skip header
        return new TransformingIterator<>(it,new Function1<Row,Object[]>() {
            @Override
            public Object[] apply(Row row) {
                Object[] data = new Object[indices.length];
                for (int i : range(data.length))
                    data[i] = PoiHelper.readCell(row.getCell(indices[i]),types[i],types[i]);
                return data;
            }
        });
    }

    private static String getWorksheetName(String file, int worksheetIndex) {
        Workbook wb = PoiHelper.readWorkbook(new File(file));
        if (wb.getNumberOfSheets() <= worksheetIndex)
            throw new IllegalArgumentException(String.format("Worksheet index (%d) out of bounds for excel file: %s",worksheetIndex,file));
        return wb.getSheetName(worksheetIndex);
    }


}
