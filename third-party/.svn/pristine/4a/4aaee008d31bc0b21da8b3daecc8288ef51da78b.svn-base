package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.*;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.metadata.TableSchema;

import java.util.Iterator;

/**
 * The {@code WrappedDataTable} class provides a {@code DataTable} implementation which wraps another data table. All methods
 * just (by default) pass through to the wrapped data table. This class is useful as a starting class for indirect {@code DataTable}
 * extensions which need to only modify select behavior from the source class.
 *
 * @author crf <br/>
 *         Started Nov 4, 2010 12:55:06 AM
 */
public class WrappedDataTable implements DataTable {
    /**
     * The wrapped data table.
     */
    protected final DataTable wrappedTable;

    /**
     * Constructor specifying the data table to wrap.
     *
     * @param wrappedTable
     *        The data table this instance will wrap.
     */
    public WrappedDataTable(DataTable wrappedTable) {
        this.wrappedTable = wrappedTable;
    }

    @Override
    public String getLabel() {
        return wrappedTable.getLabel();
    }

    @Override
    public int getRowCount() {
        return wrappedTable.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return wrappedTable.getColumnCount();
    }

    @Override
    public String[] getColumnLabels() {
        return wrappedTable.getColumnLabels();
    }

    @Override
    public boolean hasColumn(String columnLabel) {
        return wrappedTable.hasColumn(columnLabel);
    }

    @Override
    public int getColumnNumber(String columnLabel) {
        return wrappedTable.getColumnNumber(columnLabel);
    }

    @Override
    public String getColumnLabel(int columnNumber) {
        return wrappedTable.getColumnLabel(columnNumber);
    }

    @Override
    public DataType[] getColumnTypes() {
        return wrappedTable.getColumnTypes();
    }

    @Override
    public DataType getColumnDataType(String columnLabel) {
        return wrappedTable.getColumnDataType(columnLabel);
    }

    @Override
    public DataType getColumnDataType(int columnNumber) {
        return wrappedTable.getColumnDataType(columnNumber);
    }

    @Override
    public TableSchema getSchema() {
        return wrappedTable.getSchema();
    }

    @Override
    public boolean addRow(Object ... rowData) {
        return wrappedTable.addRow(rowData);
    }

    @Override
    public boolean addRow(DataRow row) {
        return wrappedTable.addRow(row);
    }

    @Override
    public boolean addColumn(String columnLabel, Object columnData, DataType type) {
        return wrappedTable.addColumn(columnLabel,columnData,type);
    }

    @Override
    public boolean addColumn(DataColumn column) {
        return wrappedTable.addColumn(column);
    }

    @Override
    public boolean addDataByColumn(Object[] data) {
        return wrappedTable.addDataByColumn(data);
    }

    @Override
    public boolean addDataByRow(Object[][] data) {
        return wrappedTable.addDataByRow(data);
    }

    @Override
    public boolean setRowData(int rowNumber, Object... values) {
        return wrappedTable.setRowData(rowNumber,values);
    }

    @Override
    public <K> boolean setRowDataByKey(K key, Object... values) {
        return wrappedTable.setRowDataByKey(key,values);
    }

    @Override
    public boolean setColumnData(int columnIndex, Object columnValues) {
        return wrappedTable.setColumnData(columnIndex,columnValues);
    }

    @Override
    public boolean setColumnData(String columnLabel, Object columnValues) {
        return wrappedTable.setColumnData(columnLabel,columnValues);
    }

    @Override
    public boolean setCellValue(int rowNumber, int columnIndex, Object value) {
        return wrappedTable.setCellValue(rowNumber,columnIndex,value);
    }

    @Override
    public boolean setCellValue(int rowNumber, String columnLabel, Object value) {
        return wrappedTable.setCellValue(rowNumber,columnLabel,value);
    }

    @Override
    public <K> boolean setCellValueByKey(K key, int columnIndex, Object value) {
        return wrappedTable.setCellValueByKey(key,columnIndex,value);
    }

    @Override
    public <K> boolean setCellValueByKey(K key, String columnLabel, Object value) {
        return wrappedTable.setCellValueByKey(key,columnLabel,value);
    }

    @Override
    public DataRow deleteRow(int rowNumber) {
        return wrappedTable.deleteRow(rowNumber);
    }

    @Override
    public <K> DataRow deleteRowByKey(K key) {
        return wrappedTable.deleteRowByKey(key);
    }

    @Override
    public <T> DataColumn<T> deleteColumn(int columnNumber) {
        return wrappedTable.deleteColumn(columnNumber);
    }

    @Override
    public <T> DataColumn<T> deleteColumn(String columnLabel) {
        return wrappedTable.deleteColumn(columnLabel);
    }

    @Override
    public <K> boolean setPrimaryKey(TableKey<K> primaryKey) {
        return wrappedTable.setPrimaryKey(primaryKey);
    }

    @Override
    public boolean setPrimaryKey(String columnLabel) {
        return wrappedTable.setPrimaryKey(columnLabel);
    }

    @Override
    public <K> TableKey<K> getPrimaryKey() {
        return wrappedTable.getPrimaryKey();
    }

    @Override
    public Object getCellValue(int rowIndex, int columnIndex) {
        return wrappedTable.getCellValue(rowIndex,columnIndex);
    }

    @Override
    public Object getCellValue(int rowIndex, String columnLabel) {
        return wrappedTable.getCellValue(rowIndex,columnLabel);
    }

    @Override
    public <K> Object getCellValueByKey(K key, int columnIndex) {
        return wrappedTable.getCellValueByKey(key,columnIndex);
    }

    @Override
    public <K> Object getCellValueByKey(K key, String columnLabel) {
        return wrappedTable.getCellValueByKey(key,columnLabel);
    }

    @Override
    public DataRow getRow(int rowIndex) {
        return wrappedTable.getRow(rowIndex);
    }

    @Override
    public <K> DataRow getRowByKey(K key) {
        return wrappedTable.getRowByKey(key);
    }

    @Override
    @SuppressWarnings({"unchecked", "varargs"})
    public <K> DataTable getIndexedRows(TableIndex<K> index, K... indexValues) {
        return wrappedTable.getIndexedRows(index,indexValues);
    }

    @Override
    public BooleanDataColumn getBooleanColumn(int columnIndex) {
        return wrappedTable.getBooleanColumn(columnIndex);
    }

    @Override
    public BooleanDataColumn getBooleanColumn(String columnLabel) {
        return wrappedTable.getBooleanColumn(columnLabel);
    }

    @Override
    public ByteDataColumn getByteColumn(int columnIndex) {
        return wrappedTable.getByteColumn(columnIndex);
    }

    @Override
    public ByteDataColumn getByteColumn(String columnLabel) {
        return wrappedTable.getByteColumn(columnLabel);
    }

    @Override
    public ShortDataColumn getShortColumn(int columnIndex) {
        return wrappedTable.getShortColumn(columnIndex);
    }

    @Override
    public ShortDataColumn getShortColumn(String columnLabel) {
        return wrappedTable.getShortColumn(columnLabel);
    }

    @Override
    public IntegerDataColumn getIntColumn(int columnIndex) {
        return wrappedTable.getIntColumn(columnIndex);
    }

    @Override
    public IntegerDataColumn getIntColumn(String columnLabel) {
        return wrappedTable.getIntColumn(columnLabel);
    }

    @Override
    public LongDataColumn getLongColumn(int columnIndex) {
        return wrappedTable.getLongColumn(columnIndex);
    }

    @Override
    public LongDataColumn getLongColumn(String columnLabel) {
        return wrappedTable.getLongColumn(columnLabel);
    }

    @Override
    public FloatDataColumn getFloatColumn(int columnIndex) {
        return wrappedTable.getFloatColumn(columnIndex);
    }

    @Override
    public FloatDataColumn getFloatColumn(String columnLabel) {
        return wrappedTable.getFloatColumn(columnLabel);
    }

    @Override
    public DoubleDataColumn getDoubleColumn(int columnIndex) {
        return wrappedTable.getDoubleColumn(columnIndex);
    }

    @Override
    public DoubleDataColumn getDoubleColumn(String columnLabel) {
        return wrappedTable.getDoubleColumn(columnLabel);
    }

    @Override
    public StringDataColumn getStringColumn(int columnIndex) {
        return wrappedTable.getStringColumn(columnIndex);
    }

    @Override
    public StringDataColumn getStringColumn(String columnLabel) {
        return wrappedTable.getStringColumn(columnLabel);
    }

    @Override
    public <T> DataColumn<T> getColumn(int columnIndex) {
        return wrappedTable.getColumn(columnIndex);
    }

    @Override
    public <T> DataColumn<T> getColumn(String columnLabel) {
        return wrappedTable.getColumn(columnLabel);
    }

    @Override
    public void setDataCoersion(boolean coersionOn) {
        wrappedTable.setDataCoersion(coersionOn);
    }

    @Override
    public boolean willCoerceData() {
        return wrappedTable.willCoerceData();
    }

    @Override
    public DataTable getTablePartition(int start, int end) {
        return wrappedTable.getTablePartition(start,end);
    }

    @Override
    public Iterator<DataRow> iterator() {
        return wrappedTable.iterator();
    }
}
