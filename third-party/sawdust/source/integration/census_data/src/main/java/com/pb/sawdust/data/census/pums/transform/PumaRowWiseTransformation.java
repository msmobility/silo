package com.pb.sawdust.data.census.pums.transform;

import com.pb.sawdust.data.census.pums.PumaDataDictionary;
import com.pb.sawdust.data.census.pums.PumaDataGroup;
import com.pb.sawdust.data.census.pums.PumaDataType;
import com.pb.sawdust.data.census.pums.PumaTables;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.transform.row.ContextDataRow;
import com.pb.sawdust.tabledata.transform.row.ContextualRowWiseTransformation;

/**
 * The {@code RowWisePumaTransformation} is a row-wise mutating PUMA data table transformation which provides the row's
 * corresponding {@code PumaDataGroup} as a context for the transformation.
 *
 * @author crf
 *         Started 1/25/12 9:50 AM
 */
public abstract class PumaRowWiseTransformation extends ContextualRowWiseTransformation<PumaDataGroup> implements PumaDataGroupTransformation {
    private final PumaDataDictionary<?,?> dictionary;
    private final PumaDataType transformationType;

    /**
     * Constructor specifying the PUMA data dictionary corresponding to this transformation, and whether this is a household
     * or person transformation.
     *
     * @param dictionary
     *        The data dictionary for this transformation.
     *
     * @param transformationType
     *        The type of PUMA data this transformation acts on.
     */
    public PumaRowWiseTransformation(PumaDataDictionary<?,?> dictionary, PumaDataType transformationType) {
        this.dictionary = dictionary;
        this.transformationType = transformationType;
    }

    /**
     * Transform a given row of data in the table. The actual transformation should be performed on the <i>table</i> (at
     * the specified row index), and not on the data row, which may be unmodifiable or separated view of the data. On the
     * other hand, access of the row's data (and contextual data group) for use in the transformation should occur through
     * the data row parameter.
     *
     * @param row
     *        The {@code ContextDataRow} representation of the row to transform. This object will hold the context which
     *        may be used in the transformation.
     *
     * @param table
     *        The table holding the row to transform.
     *
     * @param rowIndex
     *        The index (in {@code table} of the row to transform.
     */
    abstract protected void transformRow(PumaTables.PumaDataRow row, DataTable table, int rowIndex);

    @Override
    protected void transformRow(ContextDataRow<PumaDataGroup> row, DataTable table, int rowIndex) {
        transformRow((PumaTables.PumaDataRow) row,table,rowIndex);
    }

    @Override
    public PumaTables.PumaDataRow getContextDataRow(DataRow row, DataTable table, int rowIndex) {
        return (PumaTables.PumaDataRow) row;
    }

    @Override
    public PumaDataDictionary<?,?> getDataDictionary() {
        return dictionary;
    }

    @Override
    public PumaDataType getTransformationType() {
        return transformationType;
    }
}
