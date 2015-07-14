package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.DataColumn;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.metadata.DataType;

/**
 * The {@code FixedSizeDataTable} class a data table wrapper which cannot have rows or columns added or removed. Other than
 * this restriction, the data table is still mutable. Since this class only wraps the underlying data table, changes to
 * that data table (including the additional/removal of rows or columns, if allowed) will be reflected through to this
 * data table instance.
 *
 * @author crf <br/>
 *         Started Nov 4, 2010 1:19:25 AM
 */
public class FixedSizeDataTable extends WrappedDataTable {

    /**
     * Constructor specifying the data table to wrap.
     *
     * @param table
     *        The data table to wrap.
     */
    public FixedSizeDataTable(DataTable table) {
        super(table);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is fixed-size.
     */
    @Override
    public boolean addRow(Object ... rowData) {
        throw new UnsupportedOperationException("Table is fixed-size");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is fixed-size.
     */
    @Override
    public boolean addRow(DataRow row) {
        throw new UnsupportedOperationException("Table is fixed-size");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is fixed-size.
     */
    @Override
    public boolean addColumn(String columnLabel, Object columnData, DataType type) {
        throw new UnsupportedOperationException("Table is fixed-size");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is fixed-size.
     */
    @Override
    public boolean addColumn(DataColumn column) {
        throw new UnsupportedOperationException("Table is fixed-size");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is fixed-size.
     */
    @Override
    public boolean addDataByColumn(Object[] data) {
        throw new UnsupportedOperationException("Table is fixed-size");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is fixed-size.
     */
    @Override
    public boolean addDataByRow(Object[][] data) {
        throw new UnsupportedOperationException("Table is fixed-size");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is fixed-size.
     */
    @Override
    public DataRow deleteRow(int rowNumber) {
        throw new UnsupportedOperationException("Table is fixed-size");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is fixed-size.
     */
    @Override
    public <K> DataRow deleteRowByKey(K key) {
        throw new UnsupportedOperationException("Table is fixed-size");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is fixed-size.
     */
    @Override
    public <T> DataColumn<T> deleteColumn(int columnNumber) {
        throw new UnsupportedOperationException("Table is fixed-size");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always, since this data table is fixed-size.
     */
    @Override
    public <T> DataColumn<T> deleteColumn(String columnLabel) {
        throw new UnsupportedOperationException("Table is fixed-size");
    }
}
