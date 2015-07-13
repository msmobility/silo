package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.AbstractDataRow;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.util.array.ArrayUtil;

/**
 * The {@code BasicDataRow} class provides a simple implementation of the {@code DataRow} interface.
 *
 * @author crf <br/>
 *         Started: May 21, 2008 4:51:35 PM
 */
public class BasicDataRow extends AbstractDataRow {
    private Object[] rowData;
    private String[] columnLabels;
    private DataType[] columnTypes;

    /**
     * Constructor specifying the row's data, column labels, and column types.
     *
     * @param rowData
     *        The row's data.
     *
     * @param columnLabels
     *        The row's column labels, ordered as they are in the row's data.
     *
     * @param columnTypes
     *        The row's column types, ordered as they are in the row's data.
     *
     * @param coercionOn
     *        Indicator for whether this data row will coerce data or not.
     */
    public BasicDataRow(Object[] rowData, String[] columnLabels, DataType[] columnTypes, boolean coercionOn) {
        super(coercionOn);
        if (rowData.length != columnLabels.length || rowData.length != columnTypes.length)
            throw new IllegalArgumentException("Row data, labels, and type must be of same length in a data row.");
        this.rowData = rowData;
        this.columnLabels = columnLabels;
        this.columnTypes = columnTypes;
    }

    /**
     * Constructor used to create a separate copy of an existing data row.
     *
     * @param row
     *        The row to copy.
     */
    public BasicDataRow(DataRow row) {
        //only copy row data, column labels and types should be immutable
        this(ArrayUtil.copyArray(row.getData()),row.getColumnLabels(),row.getColumnTypes(),row.willCoerceData());
    }

    public String[] getColumnLabels() {
        return columnLabels;
    }

    public DataType[] getColumnTypes() {
        return columnTypes;
    }

    public Object[] getData() {
        return rowData;
    }
}
