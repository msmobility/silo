package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.*;

/**
 * The {@code ReadOnlyWrappedDataTable} class provides a data table wrapper whose contents cannot be modified. Since this
 * class only wraps the underlying data table, changes to that data table (if allowed) will be reflected through to this
 * data table instance.
 *
 * @author crf <br/>
 *         Started Nov 4, 2010 1:16:38 AM
 */
public class ReadOnlyWrappedDataTable extends FixedSizeDataTable {

    /**
     * Constructor specifying the data table to wrap.
     *
     * @param table
     *        The data table to wrap.
     */
    public ReadOnlyWrappedDataTable(DataTable table) {
        super(table);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is read-only.
     */
    @Override
    public boolean setRowData(int rowNumber, Object... values) {
        throw new UnsupportedOperationException("Table is read-only");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is read-only.
     */
    @Override
    public <K> boolean setRowDataByKey(K key, Object... values) {
        throw new UnsupportedOperationException("Table is read-only");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is read-only.
     */
    @Override
    public boolean setColumnData(int columnIndex, Object columnValues) {
        throw new UnsupportedOperationException("Table is read-only");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is read-only.
     */
    @Override
    public boolean setColumnData(String columnLabel, Object columnValues) {
        throw new UnsupportedOperationException("Table is read-only");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is read-only.
     */
    @Override
    public boolean setCellValue(int rowNumber, int columnIndex, Object value) {
        throw new UnsupportedOperationException("Table is read-only");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is read-only.
     */
    @Override
    public boolean setCellValue(int rowNumber, String columnLabel, Object value) {
        throw new UnsupportedOperationException("Table is read-only");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is read-only.
     */
    @Override
    public <K> boolean setCellValueByKey(K key, int columnIndex, Object value) {
        throw new UnsupportedOperationException("Table is read-only");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is read-only.
     */
    @Override
    public <K> boolean setCellValueByKey(K key, String columnLabel, Object value) {
        throw new UnsupportedOperationException("Table is read-only");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is read-only.
     */
    @Override
    public <K> boolean setPrimaryKey(TableKey<K> primaryKey) {
        throw new UnsupportedOperationException("Table is read-only");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is read-only.
     */
    @Override
    public boolean setPrimaryKey(String columnLabel) {
        throw new UnsupportedOperationException("Table is read-only");
    }

}
