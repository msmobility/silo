package com.pb.sawdust.excel.tabledata.read;

import com.pb.sawdust.calculator.Function1;
import com.pb.sawdust.excel.PoiHelper;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.read.FileTableReader;
import com.pb.sawdust.util.Range;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.collections.iterators.TransformingIterator;
import com.pb.sawdust.util.exceptions.RuntimeIOException;
import com.pb.sawdust.util.exceptions.RuntimeWrappingException;
import jxl.*;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code ExcelBiff5TableReader} ...
 *
 * @author crf
 *         Started 2/7/12 9:42 AM
 */
class ExcelBiff5TableReader extends AbstractExcelTableReader {
    private final int worksheetIndex;

    private int typeDepth = 1;
    private DataType nullDataType = PoiHelper.DEFAULT_NULL_DATA_TYPE;
    private int[] dataIndices;
    private String[] columnNames;
    private DataType[] types;

    public ExcelBiff5TableReader(String tableFilePath, String worksheetName) {
        super(tableFilePath,worksheetName);
        worksheetIndex = getWorksheetIndex(worksheetName);
    }

    public ExcelBiff5TableReader(String tableFilePath, int worksheetIndex) {
        super(tableFilePath,getWorksheetName(tableFilePath,worksheetIndex));
        this.worksheetIndex = worksheetIndex;
    }

    public ExcelBiff5TableReader(String tableFilePath) {
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
        int counter = 0;
        for (String sheet : readWorkbook(tableFile).getSheetNames()) {
            if (worksheetName.equals(sheet))
                return counter;
            counter++;
        }
        throw new IllegalArgumentException("Worksheet not found: " + worksheetName);
    }

    private static Workbook readWorkbook(File file) {
        WorkbookSettings settings = new WorkbookSettings();
        settings.setCellValidationDisabled(false);
        try {
            return Workbook.getWorkbook(file,settings);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } catch (BiffException e) {
            throw new RuntimeWrappingException(e);
        }
    }

    private Sheet readSheet(File file, int sheetIndex) {
        return readWorkbook(file).getSheet(sheetIndex);
    }

    private void loadStructureData() {
        if (columnNames == null) {
            Sheet sheet = readSheet(tableFile,worksheetIndex);
            Cell[] row = sheet.getRow(0);
            List<Integer> dataIndices = new LinkedList<>();
            List<String> headers = new LinkedList<>();
            for (Cell cell : row) {
                dataIndices.add(cell.getColumn());
                headers.add(readCell(cell,getCellDataType(cell.getType(),nullDataType)).toString());
            }
            this.dataIndices = ArrayUtil.toIntArray(dataIndices);
            columnNames = headers.toArray(new String[headers.size()]);
            types = new DataType[columnNames.length];

            List<Integer> notTyped = new LinkedList<>(Arrays.asList(ArrayUtil.toIntegerArray(range(types.length).getRangeArray())));
            for (int r : range(1,sheet.getRows())) {
//            while(typeDepth > 0 && notTyped.size() > 0 && iterator.hasNext()) {
//                row = iterator.next();
                row = sheet.getRow(r);
                Iterator<Integer> notTypedIterator = notTyped.iterator();
                while (notTypedIterator.hasNext()) {
                    int i = notTypedIterator.next();
                    try {
                        if (row[this.dataIndices[i]].getType() != CellType.EMPTY) {
                            types[i] = getCellDataType(row[this.dataIndices[i]].getType(),nullDataType);
                            notTypedIterator.remove();
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        //suppress, because this means just out at end of row
                    }
                }
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
        final Sheet sheet = readSheet(tableFile,worksheetIndex);
        return new TransformingIterator<>(Range.range(1,sheet.getRows()).iterator(),new Function1<Integer,Object[]>() {
            @Override
            public Object[] apply(Integer row) {
                Object[] data = new Object[indices.length];
                for (int i : range(data.length))
                    data[i] = readCell(sheet.getCell(indices[i],row),types[i]);
                return data;
            }
        });
    }

    public static Object readCell(Cell cell, DataType type) {
        DataType ctype = getCellDataType(cell.getType(),null);
        if (ctype == null) {
            switch (type) {
                case BOOLEAN : return false;
                case BYTE : return (byte) 0;
                case SHORT : return (short) 0;
                case INT : return 0;
                case LONG : return 0L;
                case FLOAT : return 0f;
                case DOUBLE : return 0d;
                case STRING : return "";
                default : throw new IllegalStateException("Shouldn't be here");
            }
        }
        switch (ctype) {
            case BOOLEAN : return type.coerce(((BooleanCell) cell).getValue(),DataType.BOOLEAN);
            case DOUBLE : return type.coerce(((NumberCell) cell).getValue(),DataType.DOUBLE);
            case STRING : return type.coerce(((LabelCell) cell).getString(),DataType.STRING);
            default : return type.coerce(((NumberCell) cell).getValue(),DataType.DOUBLE);
        }
    }

    private static DataType getCellDataType(CellType cellType, DataType nullDataType) {
        if (cellType == CellType.BOOLEAN) {
            return DataType.BOOLEAN;
        } else if (cellType == CellType.LABEL) {
            return DataType.STRING;
        } else if (cellType == CellType.NUMBER) {
            return DataType.DOUBLE;
        } else if (cellType == CellType.NUMBER_FORMULA) {
            return DataType.DOUBLE;
        } else if (cellType == CellType.STRING_FORMULA) {
            return DataType.STRING;
        } else if (cellType == CellType.BOOLEAN_FORMULA) {
            return DataType.BOOLEAN;
        } else if (cellType == CellType.EMPTY) {
            return nullDataType;
        } else{
            throw new IllegalStateException("Invalid cell type: " + cellType);
        }
    }

    private static String getWorksheetName(String file, int worksheetIndex) {
        Workbook wb = readWorkbook(new File(file));
        if (wb.getNumberOfSheets() <= worksheetIndex)
            throw new IllegalArgumentException(String.format("Worksheet index (%d) out of bounds for excel file: %s",worksheetIndex,file));
        return wb.getSheet(worksheetIndex).getName();
    }
}
