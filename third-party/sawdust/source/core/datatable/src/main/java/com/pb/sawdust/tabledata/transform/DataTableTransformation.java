package com.pb.sawdust.tabledata.transform;

import com.pb.sawdust.tabledata.DataTable;

import java.util.Set;

/**
 * The {@code DataTableTransformation} interface provides a structure for transforming data tables. The specification allows
 * for both mutating transformations (which modify the input table) and wrapping transformations (which provide a view to
 * the input table, without modifying it).
 *
 * @author crf
 *         Started 1/19/12 3:23 PM
 */
public interface DataTableTransformation {

    /**
     * Transform a data table.
     *
     * @param table
     *        The table to transform.
     *
     * @return the transformed table.
     */
    DataTable transform(DataTable table);
}
