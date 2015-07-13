package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.AbstractTableIndex;
import com.pb.sawdust.tabledata.DataTable;

/**
 * The {@code SimpleTableIndex} provides a table index built on a single column from the data table.
 *
 * @author crf
 *         Started 1/19/12 2:43 PM
 */
public class SimpleTableIndex<I> extends AbstractTableIndex<I,I> {

    /**
     * Constructor specifying the data table and the column to be used for this index. This does not build the index;
     * the user must call {@code buildIndex()} before using the index.
     *
     * @param table
     *        The data table used to build this index.
     *
     * @param indexColumnLabel
     *        The column label from {@code table} used for the index.
     *
     * @throws com.pb.sawdust.tabledata.TableDataException if {@code indexColumnLabel} is not contained in {@code table}.
     */
    public SimpleTableIndex(DataTable table, String indexColumnLabel) {
        super(table,indexColumnLabel);
    }

    @Override
    @SafeVarargs
    protected final I buildIndexKey(I ... indexValues) {
        return indexValues[0];
    }
}
