package com.pb.sawdust.data.census.pums.transform;

import com.pb.sawdust.data.census.pums.PumaDataDictionary;
import com.pb.sawdust.data.census.pums.PumaDataType;
import com.pb.sawdust.data.census.pums.PumaTables;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.transform.row.RowWiseDataTableTransformation;

/**
 * The {@code RowWisePumaDataTableTransformation} is a PUMA row-wise data table transformation which wraps another row-wise
 * data table transformation so that it can be applied to PUMA data tables via the {@code PumaDataGroupTransformation}
 * interface.
 *
 * @author crf
 *         Started 1/25/12 8:35 AM
 */
public class PumaRowWiseDataTableTransformation extends RowWiseDataTableTransformation implements PumaDataGroupTransformation {
    private final RowWiseDataTableTransformation transformation;
    private final PumaDataDictionary<?,?> dictionary;
    private final PumaDataType transformationType;

    /**
     * Constructor specifying the transformation, the PUMA data dictionary corresponding to this transformation, and whether
     * this is a household or person transformation.
     *
     * @param transformation
     *        The transformation that this instance will wrap.
     *
     * @param dictionary
     *        The data dictionary for this transformation.
     *
     * @param transformationType
     *        The type of PUMA data this transformation acts on.
     */
    public PumaRowWiseDataTableTransformation(RowWiseDataTableTransformation transformation, PumaDataDictionary<?,?> dictionary, PumaDataType transformationType) {
        this.transformation = transformation;
        this.dictionary = dictionary;
        this.transformationType = transformationType;
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

    @Override
    public void transformRow(DataRow row, DataTable table, int rowIndex) {
        transformation.transformRow(row,table,rowIndex);
    }
}
