package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.tabledata.metadata.DataType;

import java.lang.reflect.Array;
import com.pb.sawdust.tabledata.*;
import com.pb.sawdust.tabledata.util.TableDataFactory;
import com.pb.sawdust.tabledata.read.TableReader;
import com.pb.sawdust.util.array.ArrayUtil;

/**
 * The {@code RowDataTable} class provides a "row-based" implementation of the {@code DataTable} interface. This
 * means that this implementation is optimized for row-wise operations ({@code addRow(Object[])},
 * {@code getData(int)}, <i>etc.</i>), with the tradeoff being that other operations, in particular column-wise ones,
 * will be less efficient. The actual table data is internally held by this class as java arrays. This class includes a
 * constructor taking a {@code DataTable} instance as its sole argument, and can be used when optimized row-wise
 * operations are desired.
 *
 * @author crf <br/>
 *         Started: May 27, 2008 11:43:57 PM
 */
public class RowDataTable extends AbstractDataTable {

    private Object[][] table;
    private TableSchema schema;

    /**
     * Constructor specifying the table structure in a schema.
     *
     * @param schema
     *        The table's structure.
     */
    public RowDataTable(TableSchema schema) {
        this.schema = schema;
        table = new Object[0][getColumnCount()];
    }

    /**
     * Constructor specifying the table structure, as well as data to fill the table with. The data should be organized
     * as required by the {@link #addDataByRow(Object[][])} method - its first dimension is the row, and second dimension
     * the column entries.
     *
     * @param schema
     *        The table's structure.
     *
     * @param data
     *        The data to fill the table with.  The first dimension is rows, and the second is columns.
     */
    public RowDataTable(TableSchema schema, Object[][] data) {
        this(schema);
        addDataByRow(data);
    }

    /**
     * Constructor which copies a pre-existing data table into a {@code RowDataTable} instance.
     *
     * @param table
     *        The source data table to copy.
     */
    public RowDataTable(DataTable table) {
        this(table.getSchema().copy());
        int currentRow = 0;
        Object[][] data = new Object[table.getRowCount()][];
        for (DataRow row : table) {
            data[currentRow++] = row.getData();
        }
        addRowsWithoutTypeChecking(data); //other table did type checking, so it can be bypassed here
        setPrimaryKey(table.getPrimaryKey());
    }

    /**
     * Constructor which uses a {@code TableReader} to define the table structure and load its data.
     *
     * @param reader
     *        The table reader to create the table from.
     */
    public RowDataTable(TableReader reader) {
        this(new TableSchema(reader.getTableName(),reader.getColumnNames(),reader.getColumnTypes()),reader.getData());
    }

    protected <A> boolean addColumnToData(String columnLabel, A columnDataArray, DataType type) {
        int currentColumnCount = getColumnCount();
        //copy old data into new array
        if (currentColumnCount == 0)
            //have to initialize table in this case
            table = new Object[Array.getLength(columnDataArray)][1];
        for (int i=0; i < getRowCount(); i++) {
            Object[] newData = new Object[currentColumnCount+1];
            System.arraycopy(table[i],0,newData,0,currentColumnCount);
            newData[currentColumnCount] = Array.get(columnDataArray,i);
            table[i] = newData;
        }
        return true;
//        int counter = 0;
//        switch (type) {
//            case BOOLEAN : {
//                for (boolean data : (boolean[]) columnDataArray)
//                    table[counter++][currentColumnCount] = data;
//                return true;
//            }
//            case BYTE : {
//                for (byte data : (byte[]) columnDataArray)
//                    table[counter++][currentColumnCount] = data;
//                return true;
//            }
//            case SHORT : {
//                for (short data : (short[]) columnDataArray)
//                    table[counter++][currentColumnCount] = data;
//                return true;
//            }
//            case INT : {
//                for (int data : (int[]) columnDataArray)
//                    table[counter++][currentColumnCount] = data;
//                return true;
//            }
//            case LONG : {
//                for (long data : (long[]) columnDataArray)
//                    table[counter++][currentColumnCount] = data;
//                return true;
//            }
//            case FLOAT : {
//                for (float data : (float[]) columnDataArray)
//                    table[counter++][currentColumnCount] = data;
//                return true;
//            }
//            case DOUBLE : {
//                for (double data : (double[]) columnDataArray)
//                    table[counter++][currentColumnCount] = data;
//                return true;
//            }
//            case STRING : {
//                for (String data : (String[]) columnDataArray)
//                    table[counter++][currentColumnCount] = data;
//                return true;
//            }
//        }
//        return false;
    }

    protected boolean addRow(int nextRowIndex, Object ... rowData) {
        boundAndTypeCheckRowData(rowData);
        Object[][] newTable = new Object[table.length+1][getColumnCount()];
        System.arraycopy(table,0,newTable,0,table.length);
        newTable[newTable.length-1] = rowData;
        table = newTable;
        return true;
    }

    public boolean addDataByRow(Object[][] data) {
        for (Object[] dataRow : data)
            boundAndTypeCheckRowData(dataRow);
        addRowsWithoutTypeChecking(data);
        return true;
    }

    private void addRowsWithoutTypeChecking(Object[][] data) {
        int addedRows = data.length;
        int currentRows = table.length;
        Object[][] newTable = new Object[table.length+addedRows][getColumnCount()];
        System.arraycopy(table,0,newTable,0,currentRows);
        for (Object[] aData : data)
            newTable[currentRows++] = ArrayUtil.copyArray(aData);
//        System.arraycopy(data,0,newTable,currentRows,addedRows);
        table = newTable;
    }

    public boolean addDataByColumn(Object[] data) {
        if (data.length != getColumnCount())
            throw new TableDataException("Column data length must be equal to the number of columns in the table; found " + data.length + ", expected " + getColumnCount());
        if (data.length == 0)
            return true;
        //transfer to row data, then add
        int newRowCount = Array.getLength(data[0]);
        Object[][] rowData = new Object[newRowCount][data.length];
        for (int i = 0; i < data.length; i++) {
            Object columnData = data[i];
            if (newRowCount != Array.getLength(columnData))
                throw new IllegalArgumentException("Column data must be composed of eqaul length arrays.");
            for (int j = 0; j < newRowCount; j++)
                rowData[j][i] = Array.get(columnData,j);
        }
        addDataByRow(rowData);
        return true;
    }

    public Object getCellValue(int rowIndex, int columnIndex) {
        try {
            return table[rowIndex][columnIndex];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TableDataException("Invalid column index: " + columnIndex);
        }
    }


    public int getRowCount() {
        return table.length;
    }

    public int getColumnCount() {
        return schema.getColumnLabels().length;
    }

    public TableSchema getSchema() {
        return schema;
    }

    protected boolean setCellValueInData(int rowNumber, int columnIndex, Object value) {
        if (!isDataValid(value,getColumnDataType(columnIndex)))
            throw new TableDataException(TableDataException.INVALID_DATA_TYPE,value.getClass().getName(),getColumnDataType(columnIndex).getObjectTypeString());
        table[rowNumber][columnIndex] = value;
        return true;
    }

    public boolean setRowData(int rowNumber, Object ... values) {
        if (rowNumber < 0 || rowNumber >= getRowCount())
            throw new TableDataException(TableDataException.ROW_NUMBER_OUT_OF_BOUNDS,rowNumber);
        if (values.length != getColumnCount())
            throw new TableDataException("Row data must have correct number of entries.");
        boundAndTypeCheckRowData(values);
        table[rowNumber] = values;
        setPrimaryKeySpoiled();
        return true;
    }

    private boolean isDataValid(Object value, DataType type) {
        return value.getClass() == type.getObjectClass();
    }

    protected void deleteRowFromData(int rowNumber) {
        int newRowCount = table.length-1;
        Object[][] newTable = new Object[newRowCount][getColumnCount()];
        System.arraycopy(table,0,newTable,0,rowNumber);
        if(rowNumber != newRowCount) {
            System.arraycopy(table,rowNumber+1,newTable,rowNumber,newRowCount-rowNumber);
        }
        table = newTable;
    }

    protected void deleteColumnFromData(int columnNumber) {
        int newColumnCount = getColumnCount()-1;
        Object[][] newTable =  new Object[getRowCount()][getColumnCount()-1];
        for (int i = 0; i < getRowCount(); i++) {
            System.arraycopy(table[i],0,newTable[i],0,columnNumber);
            if(columnNumber != newColumnCount) {
                System.arraycopy(table[i],columnNumber+1,newTable[i],columnNumber,newColumnCount-columnNumber);
            }
        }
        table = newTable;
    }

    public DataRow getRow(int rowNumber) {
        try {
            return TableDataFactory.getDataRow(table[rowNumber],schema.getColumnLabels(),schema.getColumnTypes(),willCoerceData());
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Invalid row index: " + rowNumber);
        }
    }
}
