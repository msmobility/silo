package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.AbstractDataTable;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.tabledata.util.TableDataFactory;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.*;

/**
 * The {@code CoercedDataTable} class is a data table which wraps another data table, but modifying the data types of the
 * underlying table's columns.
 *
 * @author crf
 *         Started 1/11/12 6:41 AM
 */
public class CoercedDataTable extends AbstractDataTable {
    private final DataTable coercedTable;
    private final TableSchema schema;
    private final Map<Integer,DataType[]> coercionMapping; //column -> [original type,this table's type]

    /**
     * Constructor specifying the table to wrap, and the columns whose data types will be coerced.
     *
     * @param table
     *        The table to wrap.
     *
     * @param columnTypeMapping
     *        A mapping from column index to the new data type. An entry need only be present for those columns which are
     *        being coerced.
     *
     * @throws IllegalArgumentException if any of the indices in {@code columnTypeMapping.keySet()} are out of bounds for
     *                                  {@code table}.
     * @throws IllegalStateException if {@code table} does not have data coercion enabled.
     */
    public CoercedDataTable(DataTable table, Map<Integer,DataType> columnTypeMapping) {
        if (!table.willCoerceData())
            throw new IllegalStateException("Input table to coerced data table must have data coercion enabled.");
        coercedTable = table;
        coercionMapping = new HashMap<>();
        for (int i : columnTypeMapping.keySet()) {
            if (i < 0 || i >= table.getColumnCount())
                throw new IllegalArgumentException(String.format("Coersion column index out of bounds for table of size %d: %d",table.getColumnCount(),i));
            if (!columnTypeMapping.get(i).equals(coercedTable.getColumnDataType(i)))
                coercionMapping.put(i,new DataType[] {coercedTable.getColumnDataType(i),columnTypeMapping.get(i)});
        }
        DataType[] types = ArrayUtil.copyArray(coercedTable.getColumnTypes());
        for (int i : coercionMapping.keySet())
            types[i] = coercionMapping.get(i)[1];
        schema = new TableSchema(table.getLabel(),table.getColumnLabels(),types);
        setDataCoersion(true);
    }

    private Object coerceWrite(int index, Object value) {
        DataType[] typeMap = coercionMapping.get(index);
        return typeMap[0].coerce(value,typeMap[1]);
    }

    private Object coerceRead(int index, Object value) {
        DataType[] typeMap = coercionMapping.get(index);
        return typeMap[1].coerce(value,typeMap[0]);
    }

    private Object[] coerce(Object[] values, boolean read) {
        Object[] coercedData = ArrayUtil.copyArray(values);
        for (int i : coercionMapping.keySet())
            coercedData[i] = read ? coerceRead(i,coercedData[i]) : coerceWrite(i,coercedData[i]);
        return coercedData;
    }

    @Override
    protected <A> boolean addColumnToData(String columnLabel, A columnDataArray, DataType type) {
        return coercedTable.addColumn(columnLabel,columnDataArray,type);
    }

    @Override
    protected boolean addRow(int nextRowIndex, Object... rowData) {
        return coercedTable.addRow(coerce(rowData,false));
    }

    @Override
    protected void deleteColumnFromData(int columnNumber) {
        coercedTable.deleteColumn(columnNumber);
        Set<Integer> columnsToDecrement = new TreeSet<>();
        if (coercionMapping.containsKey(columnNumber))
            coercionMapping.remove(columnNumber);
        for (int i : coercionMapping.keySet())
            if (i > columnNumber)
                columnsToDecrement.add(i);
        for (int i : columnsToDecrement)
            coercionMapping.put(i-1,coercionMapping.remove(i));
    }

    @Override
    protected void deleteRowFromData(int rowNumber) {
        coercedTable.deleteRow(rowNumber);
    }

    @Override
    protected boolean setCellValueInData(int rowNumber, int columnIndex, Object value) {
        if (coercionMapping.containsKey(columnIndex))
            value = coerceWrite(columnIndex,value);
        return coercedTable.setCellValue(rowNumber,columnIndex,value);
    }

    @Override
    public int getRowCount() {
        return coercedTable.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return coercedTable.getColumnCount();
    }

    @Override
    public TableSchema getSchema() {
        return schema;
    }

    @Override
    public DataRow getRow(int rowIndex) {
        return TableDataFactory.getDataRow(coerce(coercedTable.getRow(rowIndex).getData(),true),schema.getColumnLabels(),schema.getColumnTypes(),willCoerceData());
    }


}
