package com.pb.sawdust.tabledata.transform.row;

import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.basic.WrappedDataRow;
import com.pb.sawdust.tabledata.metadata.DataType;

/**
 * The {@code ContextualRowWiseColumnTransformation} is a row-wise contextual data table transformation which transforms
 * a single column in the table.
 *
 * @param <T>
 *        The type the transformation acts on. This will often correspond to the transformed column's data type.
 *
 * @param <C>
 *        The typ of the context.
 *
 * @author crf
 *         Started 1/20/12 6:48 AM
 */
public abstract class ContextualRowWiseColumnTransformation<T,C> extends ContextualRowWiseTransformation<C> {
    private final String column;
    private final DataType dataType;

    /**
     * Constructor specifying the name of the column in the data table which will be transformed, and the type the transformation
     * acts on.
     *
     * @param column
     *        The name of the column this transformation will act on.
     *
     * @param dataType
     *        The type the transformation acts on. If this does not correspond to the type of data held by {@code column},
     *        the the data table will probably need to have data coercion enabled.
     */
    protected ContextualRowWiseColumnTransformation(String column, DataType dataType) {
        this.column = column;
        this.dataType = dataType;
    }

    /**
     * Constructor specifying the column in the data table which will be transformed. The data type this transformation acts
     * on will be the same as the type of the column in the data table.
     *
     * @param column
     *        The name of the column this transformation will act on.
     */
    protected ContextualRowWiseColumnTransformation(String column) {
        this(column,null);
    }

    /**
     * Get the transformed cell data, given its initial value and context data row.
     *
     * @param t
     *        The initial value of the cell.
     *
     * @param contextDataRow
     *        The context data row holding both the context and the data of the row which will be transformed.
     *
     * @return the value to set the cell to.
     */
    abstract protected T getTransformedData(T t, ContextDataRow<C> contextDataRow);

    @Override
    protected void transformRow(ContextDataRow<C> contextDataRow, DataTable table, int rowIndex) {
        table.setCellValue(rowIndex,column,getTransformedData(getColumnData(contextDataRow),contextDataRow));
    }

    /**
     * Get the data from a data row for the column this transformation acts on.
     *
     * @param row
     *        The row holding the data which will be transformed.
     *
     * @return the value from {@code row} of the column this transformation acts on.
     *
     * @throws IllegalArgumentException if {@code row} does not have a column which matches to the one this transformation
     *                                  acts on.
     */
    @SuppressWarnings("unchecked") //this will be correct, as long as user correctly specify generic parameter
    protected T getColumnData(DataRow row) {
        switch (dataType == null ? row.getColumnType(column) : dataType) {
            case BOOLEAN : return (T) (Boolean) row.getCellAsBoolean(column);
            case BYTE : return (T) (Byte) row.getCellAsByte(column);
            case SHORT : return (T) (Short) row.getCellAsShort(column);
            case INT : return (T) (Integer) row.getCellAsInt(column);
            case LONG : return (T) (Long) row.getCellAsLong(column);
            case FLOAT : return (T) (Float) row.getCellAsFloat(column);
            case DOUBLE : return (T) (Double) row.getCellAsDouble(column);
            case STRING : return (T) row.getCellAsString(column);
            default : throw new IllegalStateException("Bad data type for transformation: " + dataType);
        }
    }
}
