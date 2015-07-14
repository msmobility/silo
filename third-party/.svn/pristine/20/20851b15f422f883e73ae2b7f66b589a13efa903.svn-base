package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.AbstractDataTable;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.tabledata.util.TableDataFactory;
import static com.pb.sawdust.util.Range.*;

/**
 * The {@code ColumnAppendableDataTable} is a data table which wraps a base data table and allows columns to be appended
 * (and removed). The base data table is of fixed size, so none of the columns held by it may be removed, and rows may not
 * be added nor deleted from this data table. However, new columns may be added to (and subsequently removed from) this data
 * table as desired. This class is useful for providing multiple (partially) editable views on a core (effectively immutable)
 * data table.
 * <p>
 * This data table does not enforce a consistent connection to the base data table. That is, if the base data table's underlying
 * structure is modified (columns/rows added and/or deleted), then that will likely put this table in an inconsistent state,
 * leading to unpredictable behavior. Modifications to the base data table's contents (values) are reflected through with
 * no concerns.
 *
 * @author crf
 *         Started 4/13/12 6:50 AM
 */
public class ColumnAppendableDataTable extends AbstractDataTable {
    private final DataTable baseDataTable;
    private final DataTable appendableDataTable;
    private final TableSchema mergedSchema;
    private final int baseDataTableColumnCount;

    /**
     * Constructor specifying the base data table and label for the new table.
     *
     * @param baseDataTable
     *        The base data table.
     *
     * @param tableLabel
     *        The name for the new table.
     */
    public ColumnAppendableDataTable(DataTable baseDataTable, String tableLabel) {
        this.baseDataTable = baseDataTable;

        Object[][] appendableData = new Object[baseDataTable.getRowCount()][1];
        for (int i : range(appendableData.length))
            appendableData[i][0] = (byte) 0;

        TableSchema appendableTableSchema = new TableSchema("_appendable_table_");
        appendableTableSchema.addColumn("___dummy___",DataType.BYTE);
        appendableDataTable = new ColumnDataTable(appendableTableSchema,appendableData);

        mergedSchema = new TableSchema(tableLabel,baseDataTable.getColumnLabels(),baseDataTable.getColumnTypes());
        baseDataTableColumnCount = baseDataTable.getColumnCount();

        setDataCoersion(baseDataTable.willCoerceData());
    }

    /**
     * Constructor specifying the base data table. The base table's name will be used for the new one's label.
     *
     * @param baseDataTable
     *        The base data table.
     */
    public ColumnAppendableDataTable(DataTable baseDataTable) {
        this(baseDataTable,baseDataTable.getLabel());
    }

    @Override
    protected <A> boolean addColumnToData(String columnLabel, A columnDataArray, DataType type) {
        return appendableDataTable.addColumn(columnLabel,columnDataArray,type);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException since rows cannot be added/deleted from this data table.
     */
    @Override
    protected boolean addRow(int nextRowIndex, Object ... rowData) {
        throw new UnsupportedOperationException("Cannot add rows to column-appendable data table");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException if the column number points to a column in the base table.
     */
    @Override
    protected void deleteColumnFromData(int columnNumber) {
        if (columnNumber < baseDataTableColumnCount)
            throw new UnsupportedOperationException("Cannot delete base table column in column-appendable data table");
        appendableDataTable.deleteColumn(columnNumber-baseDataTableColumnCount+1);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException since rows cannot be added/deleted from this data table.
     */
    @Override
    protected void deleteRowFromData(int rowNumber) {
        throw new UnsupportedOperationException("Cannot delete rows from column-appendable data table");
    }

    @Override
    protected boolean setCellValueInData(int rowNumber, int columnIndex, Object value) {
        return (columnIndex < baseDataTableColumnCount) ? baseDataTable.setCellValue(rowNumber,columnIndex,value) : appendableDataTable.setCellValue(rowNumber,columnIndex-baseDataTableColumnCount+1,value);
    }

    @Override
    public int getRowCount() {
        return baseDataTable.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return mergedSchema.getColumnLabels().length;
    }

    @Override
    public TableSchema getSchema() {
        return mergedSchema;
    }

    @Override
    public DataRow getRow(int rowIndex) {
        Object[] rowData = new Object[getColumnCount()];
        System.arraycopy(baseDataTable.getRow(rowIndex).getData(),0,rowData,0,baseDataTableColumnCount);
        System.arraycopy(appendableDataTable.getRow(rowIndex).getData(),1,rowData,baseDataTableColumnCount,appendableDataTable.getColumnCount()-1);
        return TableDataFactory.getDataRow(rowData,mergedSchema.getColumnLabels(),mergedSchema.getColumnTypes(),willCoerceData());
    }
}
