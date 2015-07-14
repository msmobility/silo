package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.tabledata.metadata.DataType;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import com.pb.sawdust.tabledata.*;
import com.pb.sawdust.tabledata.read.TableReader;

/**
 * The {@code ListDataTable} class provides an implementation of the {@code DataTable} interface backed by a {@code List}.
 * This implementation is optimized for row-wise, read-only operations, especially iterating over data rows. It does not
 * allow setting data values in the table, nor does it allow columns to be added or deleted. All other column-wise
 * operations will be inefficient, especially compared to {@link ColumnDataTable}.
 *
 * @author crf <br/>
 *         Started: May 21, 2008 10:11:09 AM
 */
public class ListDataTable extends AbstractDataTable {

    private List<DataRow> table;
    private TableSchema schema;

    /**
     * Constructor specifying the table structure in a schema.
     *
     * @param schema
     *        The table's structure.
     */
    public ListDataTable(TableSchema schema) {
        this.schema = schema;
        table = new LinkedList<DataRow>();
    }

    /**
     * Constructor specifying the table structure, as well as data to fill the table with. The data should be organized
     * as required by the {@link #addDataByRow(Object[][])} method - its first dimension is the row, and second
     * dimension the column entries.
     * <p>
     * This constructor will attempt to coerce the data into the correct type. To avoid this behavior,
     *
     * @param schema
     *        The table's structure.
     *
     * @param data
     *        The data to fill the table with.  The first dimension is rows, and the second is columns.
     */
    public ListDataTable(TableSchema schema, Object[][] data) {
        this(schema);
        addDataByRow(data);
    }

    /**
     * Constructor which copies a pre-existing data table into a {@code ListDataTable} instance.
     *
     * @param table
     *        The source data table to copy.
     */
    public ListDataTable(DataTable table) {
        this(table.getSchema().copy());
        for (DataRow row : table) {
            addRow(row);
        }
        setPrimaryKey(table.getPrimaryKey());
    }

    /**
     * Constructor which uses a {@code TableReader} to define the table structure and load its data.
     *
     * @param reader
     *        The table reader to create the table from.
     */
    public ListDataTable(TableReader reader) {
        this(new TableSchema(reader.getTableName(),reader.getColumnNames(),reader.getColumnTypes()),reader.getData());
    }

    /**
     * Adding a column is not supported by this class. Will throw an {@code UnsupportedOperationException}.
     *
     * @param columnLabel
     *        The column to add.
     *
     * @param type
     *        The type of the column to add.
     *
     * @return nothing, as an excpetion will be thrown.
     *
     * @throws UnsupportedOperationException since this method is not implemented.
     */
    public boolean addColumn(String columnLabel, Object columnDataArray, DataType type) {
        throw new UnsupportedOperationException("addColumn not supported by ListDataTable.");
    }

    public boolean setRowData(int rowNumber, Object ... values) {
        throw new UnsupportedOperationException("setRowData not supported by ListDataTable.");
    }

    public <K> boolean setRowDataByKey(K key, Object ... values) {
        throw new UnsupportedOperationException("setRowDataByKey not supported by ListDataTable.");
    }

    public boolean setColumnData(int columnIndex, Object columnValues) {
        throw new UnsupportedOperationException("setColumnData not supported by ListDataTable.");
    }

    public boolean setColumnData(String columnLabel, Object columnValues) {
        throw new UnsupportedOperationException("setColumnData not supported by ListDataTable.");
    }

    public boolean setCellValue(int rowNumber, int columnIndex, Object value) {
        throw new UnsupportedOperationException("setCellValue not not supported by ListDataTable");
    }

    public boolean setCellValue(int rowNumber, String columnLabel, Object value) {
        throw new UnsupportedOperationException("setCellValue not supported by ListDataTable.");
    }

    public <K> boolean setCellValueByKey(K key, int columnIndex, Object value) {
        throw new UnsupportedOperationException(" not supported by ListDataTable.");
    }

    public <K> boolean setCellValueByKey(K key, String columnLabel, Object value) {
        throw new UnsupportedOperationException("setCellValueByKey not supported by ListDataTable.");
    }

    protected boolean setCellValueInData(int rowNumber, int columnIndex, Object value) {
        throw new UnsupportedOperationException("setCellValueInData not supported by ListDataTable.");
    }

    protected void deleteRowFromData(int rowNumber) {
        table.remove(rowNumber);
    }

    protected void deleteColumnFromData(int columnNumber) {
        throw new UnsupportedOperationException("deleteColumnFromData not supported by ListDataTable.");
    }

    public <T> DataColumn<T> deleteColumn(int columnNumber) {
        throw new UnsupportedOperationException("deleteColumn not supported by ListDataTable.");
    }

    public <T> DataColumn<T> deleteColumn(String columnlabel) {
        throw new UnsupportedOperationException("deleteColumn not supported by ListDataTable.");
    }

    /**
     * Adding a column is not supported by this class. Will throw an {@code UnsupportedOperationException}.
     *
     * @param columnLabel
     *        The column to add.
     *
     * @param columnDataArray
     *        An array of the new column's data.
     *
     * @param type
     *        The type of the column to add.
     *
     * @return nothing, as an excpetion will be thrown.
     *
     * @throws UnsupportedOperationException since this method is not implemented.
     */
    protected <A> boolean addColumnToData(String columnLabel, A columnDataArray, DataType type) {
        throw new UnsupportedOperationException("addColumnToData not supported by ListDataTable.");
    }

    public boolean addRow(DataRow row) {
        if (table.add(row)) {
            setPrimaryKeySpoiled();
            return true;
        }
        return false;
    }

    protected boolean addRow(int nextRowIndex, final Object... rowData) {
        return table.add(new BasicDataRow(rowData,schema.getColumnLabels(),schema.getColumnTypes(),willCoerceData()));
    }

    public int getRowCount() {
        return table.size();
    }

    public int getColumnCount() {
        return schema.getColumnLabels().length;
    }

    public TableSchema getSchema() {
        return schema;
    }

    public DataRow getRow(int rowNumber) {
        try {
            return table.get(rowNumber);
        } catch (IndexOutOfBoundsException e) {
            throw new TableDataException(TableDataException.ROW_NUMBER_OUT_OF_BOUNDS,rowNumber);
        }
    }

    public Iterator<DataRow> iterator() {
        return table.iterator();
    }

    public DataTable getTablePartition(final int start, final int end) {
        return new DataTablePartition(this,start,end,willCoerceData()) {
            public Iterator<DataRow> iterator() {
                final Iterator<DataRow> iterator = ListDataTable.this.iterator();
                int counter = 0;
                while (counter++ < start)
                    iterator.next();
                return new Iterator<DataRow>() {
                    int counter = start;

                    public boolean hasNext() {
                        return counter < end;
                    }

                    public DataRow next() {
                        counter++;
                        return iterator.next();
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}
