package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.tabledata.metadata.DataType;

import java.lang.reflect.Array;
import java.util.Arrays;

import com.pb.sawdust.tabledata.*;
import com.pb.sawdust.tabledata.util.TableDataFactory;
import com.pb.sawdust.tabledata.read.TableReader;

/**
 * The {@code ColumnDataTable} class provides a "column-based" implementation of the {@code DataTable} interface. This
 * means that this implementation is optimized for column-wise operations ({@code addColumn(String, Object, com.pb.sawdust.tabledata.metadata.DataType)},
 * {@code getBooleanColumn(int)}, <i>etc.</i>), with the tradeoff being that other operations, in particular row-wise
 * ones, will be less efficient. The actual table data is internally held by this class as java arrays. This class
 * includes a constructor taking a {@code DataTable} instance as its sole argument, and should generally be used when
 * optimized column-wise operations are desired.
 *
 * @author crf <br/>
 *         Started: May 28, 2008 11:36:38 PM
 *
 * @see com.pb.sawdust.tabledata.basic.RowDataTable
 */
public class ColumnDataTable extends AbstractDataTable {

    private Object[] table; //table is an array of arrays, the arrays being the correct PRIMITIVE type
    private TableSchema schema;

    /**
     * Constructor specifying the table structure in a schema.
     *
     * @param schema
     *        The table's structure.
     */
    public ColumnDataTable(TableSchema schema) {
        this.schema = schema;
        table = new Object[schema.getColumnLabels().length];
        DataType[] columnTypes = schema.getColumnTypes();
        for (int i = 0; i < table.length; i++)
            table[i] = getEmptyColumn(columnTypes[i]);
    }

    /**
     * Constructor specifying the table structure, as well as data to fill the table with. The data should be organized
     * as required by the {@link #addDataByRow(Object[][])} method - its first dimension is the row, and second
     * dimension the column entries.
     *
     * @param schema
     *        The table's structure.
     *
     * @param data
     *        The data to fill the table with.  The first dimension is rows, and the second is columns.
     */
    public ColumnDataTable(TableSchema schema, Object[][] data) {
        this(schema);
        addDataByRow(data);
    }

    /**
     * Constructor which copies a pre-existing data table into a {@code ColumnDataTable} instance.
     *
     * @param table
     *        The source data table to copy.
     */
    public ColumnDataTable(DataTable table) {
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
    public ColumnDataTable(TableReader reader) {
        this(new TableSchema(reader.getTableName(),reader.getColumnNames(),reader.getColumnTypes()),reader.getData());
    }

    private Object getEmptyColumn(DataType type) {
        return type.getPrimitiveArray(0);
//        switch(type) {
//            case BOOLEAN : return (A) new boolean[0];
//            case BYTE : return (A) new byte[0];
//            case SHORT : return (A) new short[0];
//            case INT : return (A) new int[0];
//            case LONG : return (A) new long[0];
//            case FLOAT : return (A) new float[0];
//            case DOUBLE : return (A) new double[0];
//            case STRING : return (A) new String[0];
//            default : return null;
//        }
    }

    public Object getCellValue(int rowIndex, int columnIndex) {
        try {
            return Array.get(table[columnIndex],rowIndex);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TableDataException("Invalid column index: " + columnIndex);
        }
    }
    
    protected <A> boolean addColumnToData(String columnLabel, A columnDataArray, DataType type) {
        int oldColumnCount = getColumnCount();
        Object[] newTable = new Object[getColumnCount()+1];
        System.arraycopy(table,0,newTable,0,oldColumnCount);
        newTable[oldColumnCount] = columnDataArray;
        table = newTable;
        return true;
    }

    protected boolean addRow(int nextRowIndex, Object ... rowData) {
        Object[] newTable = new Object[getColumnCount()];
        int rowCount = getRowCount();
        int counter = 0;
        DataType[] columnTypes = schema.getColumnTypes();
        for (Object data : rowData) {                     
            try {
                Object column = columnTypes[counter].getPrimitiveArray(rowCount+1);
                System.arraycopy(table[counter], 0, column, 0, rowCount);
                Array.set(column,rowCount,data);
                newTable[counter++] = column;
//                switch (columnTypes[counter]) {
//                    case BOOLEAN : {
//                        boolean[] column = new boolean[rowCount+1];
//                        System.arraycopy(table[counter],0,column,0,rowCount);
//                        column[rowCount] = (Boolean) data;
//                        newTable[counter++] = column;
//                        break;
//                    }
//                    case BYTE : {
//                        byte[] column = new byte[rowCount+1];
//                        System.arraycopy(table[counter],0,column,0,rowCount);
//                        column[rowCount] = (Byte) data;
//                        newTable[counter++] = column;
//                        break;
//                    }
//                    case SHORT : {
//                        short[] column = new short[rowCount+1];
//                        System.arraycopy(table[counter],0,column,0,rowCount);
//                        column[rowCount] = (Short) data;
//                        newTable[counter++] = column;
//                        break;
//                    }
//                    case INT : {
//                        int[] column = new int[rowCount+1];
//                        System.arraycopy(table[counter],0,column,0,rowCount);
//                        column[rowCount] = (Integer) data;
//                        newTable[counter++] = column;
//                        break;
//                    }
//                    case LONG : {
//                        long[] column = new long[rowCount+1];
//                        System.arraycopy(table[counter],0,column,0,rowCount);
//                        column[rowCount] = (Long) data;
//                        newTable[counter++] = column;
//                        break;
//                    }
//                    case FLOAT : {
//                        float[] column = new float[rowCount+1];
//                        System.arraycopy(table[counter],0,column,0,rowCount);
//                        column[rowCount] = (Float) data;
//                        newTable[counter++] = column;
//                        break;
//                    }
//                    case DOUBLE : {
//                        double[] column = new double[rowCount+1];
//                        System.arraycopy(table[counter],0,column,0,rowCount);
//                        column[rowCount] = (Double) data;
//                        newTable[counter++] = column;
//                        break;
//                    }
//                    case STRING : {
//                        String[] column = new String[rowCount+1];
//                        System.arraycopy(table[counter],0,column,0,rowCount);
//                        column[rowCount] = (String) data;
//                        newTable[counter++] = column;
//                        break;
//                    }
//                }
            } catch (ArrayStoreException e) {
                throw new TableDataException("Invalid data type for column " + getColumnLabels()[counter] + " (caused by: " + e.getMessage() + ")");
            }
        }
        table = newTable;
        return true;
    }

    public boolean addDataByColumn(Object[] data) {
        if (data.length != getColumnCount())
            throw new TableDataException("Column data length must be equal to the number of columns in the table; found " + data.length + ", expected " + getColumnCount());
        //check data types and lengths, and end with an array of primitive arrays
        int newRowCount = 0;
        for (int i = 0; i < data.length; i++) {
            if (!data[i].getClass().isArray())
                throw new IllegalArgumentException("Column data must be composed of arrays.");
            if (i == 0)
                newRowCount = Array.getLength(data[i]);
            else if (newRowCount != Array.getLength(data[i]))
                throw new IllegalArgumentException("Column data must be composed of eqaul length arrays.");
            DataType columnType = getColumnDataType(i);
            data[i] = getPrimitiveColumn(data[i],columnType);
            if (!checkPrimitiveArrayType(data[i],columnType))
                throw new TableDataException(TableDataException.INVALID_DATA_TYPE,data[i].getClass().getComponentType(),columnType.getPrimitiveTypeString());
        }
//        //now copy data
//        Object[] newTable = new Object[getColumnCount()];
//        int oldRowCount = getRowCount();
//        for (int i = 0; i < data.length; i++) {
//            newTable[i] = getColumnDataType(i).getPrimitiveArray(oldRowCount+newRowCount);
//            System.arraycopy(table[i],0,newTable[i],0,oldRowCount);
//            System.arraycopy(data[i],0,newTable[i],oldRowCount,newRowCount);
//        }
//        table = newTable;
        addColumnsWithoutTypeChecking(newRowCount,data);
        return true;
    }

    private void addColumnsWithoutTypeChecking(int newRowCount, Object[] data) {
        Object[] newTable = new Object[getColumnCount()];
        int oldRowCount = getRowCount();
        for (int i = 0; i < data.length; i++) {
            newTable[i] = getColumnDataType(i).getPrimitiveArray(oldRowCount+newRowCount);
            System.arraycopy(table[i],0,newTable[i],0,oldRowCount);
            System.arraycopy(data[i],0,newTable[i],oldRowCount,newRowCount);
        }
        table = newTable;
     }

    /**
     * {@inheritDoc}
     *
     * If data coersion is enabled then this data will be coerced <i>in place</i>.  To change this behavior, override
     * the {@link #coerceRowData(Object...)} method.
     *
     * @throws IllegalArgumentException if data coersion is enabled and any of the values in {@code data} cannot be coerced.
     */
    public boolean addDataByRow(Object[][] data) {
        if (data.length == 0)
            return true;
        Object[] columnData = new Object[getColumnCount()];
        int newRowCount = data.length;
        int columnCount = getColumnCount();
        if (willCoerceData())
            for (int i = 0; i < newRowCount; i++)
                data[i] = coerceRowData(data[i]);
        for (int i = 0; i < columnCount; i++) {
            Object primitiveArray = getColumnDataType(i).getPrimitiveArray(newRowCount);
            for (int j = 0; j < newRowCount; j++) {
                if (i == 0)
                    boundAndTypeCheckRowData(data[j]);
                Array.set(primitiveArray,j,data[j][i]);
            }
            columnData[i] = primitiveArray;
        }
        addColumnsWithoutTypeChecking(newRowCount,columnData);
        return true;
    }

    private void addRowsWithoutTypeChecking(Object[][] data) {
        if (data.length == 0)
            return;
        Object[] columnData = new Object[getColumnCount()];
        int newRowCount = data.length;
        int columnCount = getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            Object primitiveArray = getColumnDataType(i).getPrimitiveArray(newRowCount);
            for (int j = 0; j < newRowCount; j++) {
                Array.set(primitiveArray,j,data[j][i]);
            }
            columnData[i] = primitiveArray;
        }
        addColumnsWithoutTypeChecking(newRowCount,columnData);
    }

    public int getRowCount() {
        return getColumnCount() > 0 ? Array.getLength(table[0]) : 0;
    }

    public int getColumnCount() {
        return table.length;
    }

    public TableSchema getSchema() {
        return schema;
    }

    protected boolean setCellValueInData(int rowNumber, int columnIndex, Object value) {
        try {
            Array.set(table[columnIndex],rowNumber,value);
        } catch (IllegalArgumentException e) {
            throw new TableDataException(TableDataException.INVALID_DATA_TYPE,value.getClass().getName(),getColumnDataType(columnIndex).getObjectTypeString());
        }
        return true;
    }

    protected void deleteRowFromData(int rowNumber) {
        Object[] newTable = new Object[getColumnCount()];
        int newRowCount = getRowCount()-1;
        int counter = 0;
        DataType[] columnTypes = schema.getColumnTypes();
        for (Object column : table) {
            Object newColumn = columnTypes[counter].getPrimitiveArray(newRowCount);
            System.arraycopy(column,0,newColumn,0,rowNumber);
            if(rowNumber != newRowCount) {
                System.arraycopy(column,rowNumber+1,newColumn,rowNumber,newRowCount-rowNumber);
            }
            newTable[counter++] = newColumn;
        }
        table = newTable;
    }

    protected void deleteColumnFromData(int columnNumber) {
        int newColumnCount = getColumnCount()-1;
        Object[] newTable = new Object[newColumnCount];
        System.arraycopy(table,0,newTable,0,columnNumber);
        if(columnNumber != newColumnCount) {
            System.arraycopy(table,columnNumber+1,newTable,columnNumber,newColumnCount-columnNumber);
        }
        table = newTable;
    }

    public DataRow getRow(int rowNumber) {
        Object[] rowData = new Object[getColumnCount()];
        int counter = 0;
        try {
            for (Object column : table)
                rowData[counter++] = Array.get(column,rowNumber);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Invalid row index: " + rowNumber);
        }
        return TableDataFactory.getDataRow(rowData,schema.getColumnLabels(),schema.getColumnTypes(),willCoerceData());
    }
}
