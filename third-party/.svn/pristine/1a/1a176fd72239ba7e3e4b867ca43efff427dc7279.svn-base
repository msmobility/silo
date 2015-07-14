package com.pb.sawdust.tabledata.transform.row;

import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.transform.MutatingDataTableTransformation;

/**
 * The {@code RowWiseDataTableTransformation} class is used to construct row-wise mutating data table transformations. Row-wise
 * means that the transformation will be applied one row at a time, and, generally, implies that the transformation for
 * each row is independent of the others (which, if true, means the process can easily be parallelized).
 *
 * @author crf
 *         Started 1/19/12 5:28 PM
 */
public abstract class RowWiseDataTableTransformation implements MutatingDataTableTransformation {
    /**
     * Transform a given row of data in the table. The actual transformation should be performed on the <i>table</i> (at
     * the specified row index), and not on the data row, which may be unmodifiable or separated view of the data. On the
     * other hand, access of the row's data for use the transformation should occur through the data row parameter.
     *
     * @param row
     *        The {@code DataRow} representation of the row to transform.
     *
     * @param table
     *        The table holding the row to transform.
     *
     * @param rowIndex
     *        The index (in {@code table} of the row to transform.
     */
    public abstract void transformRow(DataRow row, DataTable table, int rowIndex);

    @Override
    public DataTable transform(DataTable table) {
        int counter = 0;
        for (DataRow row : table)
            transformRow(row,table,counter++);
        return table;
    }
}
