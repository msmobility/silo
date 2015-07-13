package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.*;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.tabledata.util.TableDataFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code RestructuredDataTable} is a data table which provides a view into another by rearranging, removing, renaming,
 * and/or changing the data types of its columns. Columns are rearranged/removed only in the view, not in the base data table.
 * If column data types are changed, then this is done internally through data coercion. This data table is a limited view:
 * most changes to the underlying base data table will be reflected through this class, but changes to the underlying data
 * table's column structure (specifically: deleting columns included in this view) can put this data table in an inconsistent
 * state, leading to unpredictable behavior.
 *
 * @author crf
 *         Started 4/13/12 6:22 AM
 */
public class RestructuredDataTable extends ReadOnlyWrappedDataTable {
    private final InternalRestructuredDataTable restructuredTable;

    /**
     * Constructor specifying the base data table, the label for the new table, and the new columns to be included in the
     * restructured data table (in the desired order).
     *
     * @param table
     *        The base data table.
     *
     * @param tableLabel
     *        The name for the new table.
     *
     * @param columns
     *        A mapping from the new column names to the original ones (in {@code table}), in the desired order. That is,
     *        the column ordering will be the ordering returned by <code>columns.getKeySet()</code>.
     *
     * @param columnTypes
     *        A mapping from the new column names to the new data types. If a column in {@code columns.keySet()} is not
     *        present in this mapping, then the source column's data type will be used.
     *
     * @throws IllegalArgumentException if any column in {@code columns} is not included in {@code table}, or if any column
     *                                  in {@code columns} is repeated.
     */
    public RestructuredDataTable(DataTable table, String tableLabel, Map<String,String> columns, Map<String,DataType> columnTypes) {
        super(new InternalRestructuredDataTable(table,tableLabel,columns,columnTypes));
        restructuredTable = (InternalRestructuredDataTable) wrappedTable;
    }

    /**
     * Constructor specifying the base data table, the label for the new table, and the new columns to be included in the
     * restructured data table (in the desired order).
     *
     * @param table
     *        The base data table.
     *
     * @param tableLabel
     *        The name for the new table.
     *
     * @param columns
     *        A mapping from the new column names to the original ones (in {@code table}), in the desired order. That is,
     *        the column ordering will be the ordering returned by <code>columns.getKeySet()</code>.
     *
     * @throws IllegalArgumentException if any column in {@code columns} is not included in {@code table}, or if any column
     *                                  in {@code columns} is repeated.
     */
    public RestructuredDataTable(DataTable table, String tableLabel, Map<String,String> columns) {
        super(new InternalRestructuredDataTable(table,tableLabel,columns));
        restructuredTable = (InternalRestructuredDataTable) wrappedTable;
    }

    /**
     * Constructor specifying the base data table and the map specifying the new columns to be included in the restructured
     * data table (in the desired order). The base table's name will be used for the new one's label.
     *
     * @param table
     *        The base data table.
     *
     * @param columns
     *        A mapping from the new column names to the original ones (in {@code table}), in the desired order. That is, the column ordering
     *        will be the ordering returned by <code>columns.getKeySet()</code>.
     *
     * @throws IllegalArgumentException if any column in {@code columns} is not included in {@code table}, or if any column
     *                                  in {@code columns} is repeated.
     */
    public RestructuredDataTable(DataTable table, Map<String,String> columns) {
        this(table,table.getLabel(),columns);
    }

    /**
     * Constructor specifying the base data table, the label for the new table, and the columns to be included in the restructured
     * data table (in the desired order).
     *
     * @param table
     *        The base data table.
     *
     * @param tableLabel
     *        The name for the new table.
     *
     * @param columns
     *        The columns from the base table to include, in the desired order.
     *
     * @throws IllegalArgumentException if any column in {@code columns} is not included in {@code table}, or if any column
     *                                  in {@code columns} is repeated.
     */
    public RestructuredDataTable(DataTable table, String tableLabel, List<String> columns) {
        super(new InternalRestructuredDataTable(table,tableLabel,columns));
        restructuredTable = (InternalRestructuredDataTable) wrappedTable;
    }

    /**
     * Constructor specifying the base data table and the columns to be included in the restructured data table (in the
     * desired order). The base table's name will be used for the new one's label.
     *
     * @param table
     *        The base data table.
     *
     * @param columns
     *        The columns from the base table to include, in the desired order.
     *
     * @throws IllegalArgumentException if any column in {@code columns} is not included in {@code table}, or if any column
     *                                  in {@code columns} is repeated.
     */
    public RestructuredDataTable(DataTable table, List<String> columns) {
        this(table,table.getLabel(),columns);
    }

    //use this class internally to get benefits of a full fledged data table
    // then use ReadOnlyWrappedDataTable to close off write methods
    private static class InternalRestructuredDataTable extends AbstractDataTable {
        private final DataTable baseTable;
        private final TableSchema restructuredSchema;
        private final int[] columnIndexMapping;

        private static Map<String,String> getColumnMap(List<String> columns) {
            Map<String,String> columnMap = new LinkedHashMap<>();
            for (String column : columns)
                columnMap.put(column,column);
            return columnMap;
        }

        private InternalRestructuredDataTable(DataTable baseTable, String tableLabel, List<String> columns) {
            this(baseTable,tableLabel,getColumnMap(columns));
        }

        private InternalRestructuredDataTable(DataTable baseTable, String tableLabel, Map<String,String> columns) {
            this(baseTable,tableLabel,columns,null);
        }

        private InternalRestructuredDataTable(DataTable baseTable, String tableLabel, Map<String,String> columns, Map<String,DataType> columnTypes) {
            this.baseTable = baseTable;
            restructuredSchema = new TableSchema(tableLabel);
            columnIndexMapping = new int[columns.size()];
            boolean needCoercion = false;
            int counter = 0;
            for (String newColumnName : columns.keySet()) {
                String column = columns.get(newColumnName);
                if (!baseTable.hasColumn(column))
                    throw new IllegalArgumentException("Column not found in source data table: " + column);
                DataType columnType = baseTable.getColumnDataType(column);
                if (columnTypes != null && columnTypes.containsKey(newColumnName)) {
                    DataType newColumnType = columnTypes.get(newColumnName);
                    needCoercion |= columnType != newColumnType;
                    columnType = newColumnType;
                }
                restructuredSchema.addColumn(newColumnName,columnType);
                columnIndexMapping[counter++] = baseTable.getColumnNumber(column);
            }
            setDataCoersion(needCoercion || baseTable.willCoerceData());
        }

        @Override
        protected <A> boolean addColumnToData(String columnLabel, A columnDataArray, DataType type) {
            return false; //inaccessible
        }

        @Override
        protected boolean addRow(int nextRowIndex, Object... rowData) {
            return false;  //inaccessible
        }

        @Override
        protected void deleteColumnFromData(int columnNumber) {
            //inaccessible
        }

        @Override
        protected void deleteRowFromData(int rowNumber) {
            //inaccessible
        }

        @Override
        protected boolean setCellValueInData(int rowNumber, int columnIndex, Object value) {
            return false; //inaccessible
        }

        @Override
        public int getRowCount() {
            return baseTable.getRowCount();
        }

        @Override
        public int getColumnCount() {
            return restructuredSchema.getColumnLabels().length;
        }

        @Override
        public TableSchema getSchema() {
            return restructuredSchema;
        }

        @Override
        public DataRow getRow(int rowIndex) {
            Object[] rowData = new Object[getColumnCount()];
            DataRow sourceRowData = baseTable.getRow(rowIndex);
            for (int i = 0; i < rowData.length; i++)
                rowData[i] = sourceRowData.getCell(columnIndexMapping[i]);
            return TableDataFactory.getDataRow(rowData,restructuredSchema.getColumnLabels(),restructuredSchema.getColumnTypes(),willCoerceData());
        }
    }

    private int getWrappedColumnIndex(int columnIndex) {
        return restructuredTable.columnIndexMapping[columnIndex];
    }

    @Override
    public Object getCellValue(int rowIndex, int columnIndex) {
        return wrappedTable.getCellValue(rowIndex,getWrappedColumnIndex(columnIndex));
    }

    @Override
    public <K> Object getCellValueByKey(K key, int columnIndex) {
        return wrappedTable.getCellValueByKey(key,getWrappedColumnIndex(columnIndex));
    }

    @Override
    public BooleanDataColumn getBooleanColumn(int columnIndex) {
        return wrappedTable.getBooleanColumn(getWrappedColumnIndex(columnIndex));
    }

    @Override
    public ByteDataColumn getByteColumn(int columnIndex) {
        return wrappedTable.getByteColumn(getWrappedColumnIndex(columnIndex));
    }

    @Override
    public ShortDataColumn getShortColumn(int columnIndex) {
        return wrappedTable.getShortColumn(getWrappedColumnIndex(columnIndex));
    }

    @Override
    public IntegerDataColumn getIntColumn(int columnIndex) {
        return wrappedTable.getIntColumn(getWrappedColumnIndex(columnIndex));
    }

    @Override
    public LongDataColumn getLongColumn(int columnIndex) {
        return wrappedTable.getLongColumn(getWrappedColumnIndex(columnIndex));
    }

    @Override
    public FloatDataColumn getFloatColumn(int columnIndex) {
        return wrappedTable.getFloatColumn(getWrappedColumnIndex(columnIndex));
    }

    @Override
    public DoubleDataColumn getDoubleColumn(int columnIndex) {
        return wrappedTable.getDoubleColumn(getWrappedColumnIndex(columnIndex));
    }

    @Override
    public StringDataColumn getStringColumn(int columnIndex) {
        return wrappedTable.getStringColumn(getWrappedColumnIndex(columnIndex));
    }

    @Override
    public <T> DataColumn<T> getColumn(int columnIndex) {
        return wrappedTable.getColumn(getWrappedColumnIndex(columnIndex));
    }
}
