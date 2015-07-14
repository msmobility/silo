package com.pb.sawdust.data.census.pums.transform;

import com.pb.sawdust.data.census.pums.*;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.transform.row.ContextDataRow;
import com.pb.sawdust.tabledata.transform.row.ContextualRowWiseColumnTransformation;

/**
 * The {@code RowWisePumaColumnTransformation} is a row-wise PUMA data table transformation which transforms a single column
 * in the table.
 *
 * @param <T>
 *        The type the transformation acts on. This will often correspond to the transformed column's data type.
 *
 * @author crf
 *         Started 1/25/12 9:59 AM
 */
public abstract class PumaRowWiseColumnTransformation<T> extends ContextualRowWiseColumnTransformation<T,PumaDataGroup> implements PumaDataGroupTransformation {
    private final PumaDataDictionary<?,?> dictionary;
    private final PumaDataType transformationType;

    /**
     * Constructor specifying the name of the column in the data table which will be transformed, the type the transformation
     * acts on, and the data dictionary corresponding to the transformation.
     *
     * @param column
     *        The name of the column this transformation will act on.
     *
     * @param dataType
     *        The type the transformation acts on. If this does not correspond to the type of data held by {@code column},
     *        the the data table will probably need to have data coercion enabled.
     *
     * @param dictionary
     *        The data dictionary for this transformation.
     */
    public PumaRowWiseColumnTransformation(PumaDataField column, DataType dataType, PumaDataDictionary<?,?> dictionary) {
        super(column.getColumnName(),dataType);
        this.dictionary = dictionary;
        transformationType = PumaDataType.getFieldType(column);
    }

    /**
     * Constructor specifying the column in the data table which will be transformed and the data dictionary corresponding
     * to the transformation. The data type this transformation acts on will be the same as the type of the column in the
     * data table.
     *
     * @param column
     *        The name of the column this transformation will act on.
     *
     * @param dictionary
     *        The data dictionary for this transformation.
     */
    public PumaRowWiseColumnTransformation(PumaDataField column, PumaDataDictionary<?,?> dictionary) {
        super(column.getColumnName());
        this.dictionary = dictionary;
        transformationType = PumaDataType.getFieldType(column);
    }

    /**
     * Get the transformed cell data, given its initial value and context data row.
     *
     * @param t
     *        The initial value of the cell.
     *
     * @param row
     *        The {@code PumaDataRow} which will be transformed.
     *
     * @return the value to set the cell to.
     */
    abstract T getTransformedData(T t, PumaTables.PumaDataRow row);

    @Override
    protected T getTransformedData(T t, ContextDataRow<PumaDataGroup> row) {
        return getTransformedData(t,(PumaTables.PumaDataRow) row);
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
