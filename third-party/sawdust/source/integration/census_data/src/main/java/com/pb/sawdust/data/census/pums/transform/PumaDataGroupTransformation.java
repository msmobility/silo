package com.pb.sawdust.data.census.pums.transform;

import com.pb.sawdust.data.census.pums.PumaTables;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;

/**
 * The {@code PumaDataGroupTransformation} is a row-wise PUMA data table transformation which provides a data row's corresponding
 * (containing) {@code PumaDataGroup} as a context for the transformation. That this transformation is row-wise is not
 * enforced, but is implied by the reference to using a {@code PumaDataGroup} as a context. This interface is meant to be
 * a complement to {@link com.pb.sawdust.tabledata.transform.row.ContextualRowWiseTransformation}, refining its {@code getContextDataRow}
 * method.
 *
 * @author crf
 *         Started 1/25/12 8:20 AM
 */
public interface PumaDataGroupTransformation extends PumaDataTableTransformation {

    /**
     * Get the {@code PumaDataRow} for the specified row. This method is meant to be used in conjunction with the one
     * specified in {@link com.pb.sawdust.tabledata.transform.row.ContextualRowWiseTransformation}, allowing its return
     * type to be refined without extending the class.
     *
     * @param row
     *        The row which will form the basis of the PUMA data row.
     *
     * @param table
     *        The table that {@code row} came from.
     *
     * @param rowIndex
     *        The index of {@code row} in {@code table}.
     *
     * @return the context data row holding both the context and {@code row}'s information.
     */
    PumaTables.PumaDataRow getContextDataRow(DataRow row, DataTable table, int rowIndex);
}
