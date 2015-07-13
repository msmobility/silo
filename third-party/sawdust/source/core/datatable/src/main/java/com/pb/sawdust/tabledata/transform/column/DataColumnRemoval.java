package com.pb.sawdust.tabledata.transform.column;

import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.transform.MutatingDataTableTransformation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@code DataColumnRemoval} class is a mutating data table transformation which deletes columns from the input table.
 *
 * @author crf
 *         Started 1/24/12 9:52 AM
 */
public class DataColumnRemoval extends ColumnWiseDataTableTransformation implements MutatingDataTableTransformation {
    private final Set<String> columnsToRemove;

    /**
     * Constructor specifying the names of the columns to remove.
     *
     * @param columnsToRemove
     *        The names of the columns that this transformation will remove.
     */
    public DataColumnRemoval(Set<String> columnsToRemove) {
        this.columnsToRemove = new HashSet<>(columnsToRemove);
    }

    /**
     * Constructor specifying the names of the columns to remove.
     *
     * @param columnsToRemove
     *        The names of the columns that this transformation will remove.
     */
    public DataColumnRemoval(String ... columnsToRemove) {
        this(new HashSet<>(Arrays.asList(columnsToRemove)));
    }

    @Override
    public Set<String> getColumnsToTransform() {
        return Collections.unmodifiableSet(columnsToRemove);
    }

    @Override
    public DataTable transformColumns(Set<String> columns, DataTable table) {
        for (String column : columns)
            table.deleteColumn(column);
        return table;
    }
}
