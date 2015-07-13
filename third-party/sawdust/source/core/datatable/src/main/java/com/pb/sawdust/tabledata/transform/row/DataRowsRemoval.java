package com.pb.sawdust.tabledata.transform.row;

import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.transform.MutatingDataTableTransformation;
import com.pb.sawdust.util.Filter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@code DataRowsRemoval} class is a mutating data table transformation which removes rows from the table. Note that
 * this <i>is not</i> a row-wise transformation, but rather is applied atomically; this is because the removal of rows
 * inherently affects row indices (and iterators) and thus row-wise independence of transformations cannot be guaranteed.
 * <p>
 * This class uses a filter to determine if a row should be removed or not. It is important to note that if a data row
 * passes the filter (that is, the filter returns {@code true} for the data row), then it will be removed; only those data
 * rows blocked by the filter are retained in the data table. The filter works on {@code ContextDataRow}s, allowing this
 * class to be mixed in with interfaces representing contextual transformations.
 *
 * @author crf
 *         Started 1/30/12 7:16 AM
 */
public abstract class DataRowsRemoval<C> implements MutatingDataTableTransformation {
    private final Filter<ContextDataRow<C>> rowFilter;
    private final ConcurrentMap<DataTable,Set<Integer>> rowsToDelete = new ConcurrentHashMap<>();
    private final ConcurrentMap<DataTable,AtomicInteger> tableSize = new ConcurrentHashMap<>(); //so multiple tables can use this transformation

    /**
     * Constructor specifying the row filter used to determine which rows to remove from the table. If the row filter returns
     * {@code true} for a certain data row, then that row will be removed from the table.
     *
     * @param rowFilter
     *        The filter used to determine which rows to remove.
     */
    public DataRowsRemoval(Filter<ContextDataRow<C>> rowFilter) { //if true, the delete row
        this.rowFilter = rowFilter;
    }

    /**
     * Get the context data row for a given row in the table.
     *
     * @param row
     *        The data row.
     *
     * @param table
     *        The source data table.
     *
     * @param rowIndex
     *        The index of the row in {@code table}.
     *
     * @return the appropriate context data row for {@code row}.
     */
    protected abstract ContextDataRow<C> getContextDataRow(DataRow row, DataTable table, int rowIndex);

    /**
     * Do something with a row which will be removed by this transformation. This method could be used to collect the number
     * of rows that were deleted, or some attributes about them. By default, this it does nothing.
     *
     * @param contextDataRow
     *        The row which will be removed.
     *
     * @param table
     *        The table from which the row wil be removed.
     *
     * @param rowIndex
     *        The index of the row in {@code table}.
     */
    protected void processRowToBeRemoved(ContextDataRow<C> contextDataRow, DataTable table, int rowIndex) {

    }

    public DataTable transform(DataTable table) {
        return new DataRowsRemovalTransformation().transform(table);
    }

    //this allows the determination of what rows to delete (which is a row-wise independent operation) to proceed concurrently, if that is ever implemented
    private class DataRowsRemovalTransformation extends ContextualRowWiseTransformation<C> {
        @Override
        protected void transformRow(ContextDataRow<C> contextDataRow, DataTable table, int rowIndex) {
            Set<Integer> rowsToDeleteSet = DataRowsRemoval.this.rowsToDelete.get(table);
            if (rowsToDeleteSet == null) {
                rowsToDeleteSet = Collections.synchronizedSet(new TreeSet<>(Collections.<Integer>reverseOrder()));
                tableSize.putIfAbsent(table,new AtomicInteger(table.getRowCount()));
                DataRowsRemoval.this.rowsToDelete.putIfAbsent(table,rowsToDeleteSet);
            }
            if (rowFilter.filter(contextDataRow)) {
                rowsToDeleteSet.add(rowIndex);
                processRowToBeRemoved(contextDataRow,table,rowIndex);
            }
            if (tableSize.get(table).decrementAndGet() == 0) {
                //done, so delete rows and clear out data
                for (int i : rowsToDelete.remove(table))
                    table.deleteRow(i);
                tableSize.remove(table);
            }
        }

        @Override
        protected ContextDataRow<C> getContextDataRow(DataRow row, DataTable table, int rowIndex) {
            return DataRowsRemoval.this.getContextDataRow(row,table,rowIndex);
        }
    }
}
