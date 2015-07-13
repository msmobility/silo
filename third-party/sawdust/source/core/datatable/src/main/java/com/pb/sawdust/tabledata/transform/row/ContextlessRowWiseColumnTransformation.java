package com.pb.sawdust.tabledata.transform.row;

import com.pb.sawdust.calculator.Function1;
import com.pb.sawdust.calculator.Function2;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.basic.WrappedDataRow;
import com.pb.sawdust.tabledata.metadata.DataType;

/**
 * The {@code ContextlessRowWiseColumnTransformation} is a row-wise column data table transformation whose transformation
 * function is based purely on the initial value of the column.
 *
 * @param <T>
 *        The type the transformation acts on. This will often correspond to the transformed column's data type.
 *
 * @author crf
 *         Started 1/25/12 6:19 AM
 */
public class ContextlessRowWiseColumnTransformation<T> extends FunctionRowWiseColumnTransformation<T,Void> {
    private final Function1<T,T> transformation;

    /**
     * Constructor specifying the column to transform, the data type the transformation acts on, and the function which will
     * calculate the transformation values.
     *
     * @param column
     *        The name of the column this transformation will act on.
     *
     * @param dataType
     *        The type the transformation acts on. If this does not correspond to the type of data held by {@code column},
     *        the the data table will probably need to have data coercion enabled.
     *
     * @param transformation
     *        The function which will transform the original column value.
     */
    public ContextlessRowWiseColumnTransformation(String column, DataType dataType, Function1<T,T> transformation) {
        super(column,dataType,getTwoArgFunction(transformation));
        this.transformation = transformation;
    }

    /**
     * Constructor specifying the column to transform and the function which will calculate the transformation values. The
     * data type this transformation acts on will be the same as the type of the column in the data table.
     *
     * @param column
     *        The name of the column this transformation will act on.
     *
     * @param transformation
     *        The function which will transform the original column value.
     */
    public ContextlessRowWiseColumnTransformation(String column, Function1<T,T> transformation) {
        this(column,null,transformation);
    }

    private static <T> Function2<T,Void,T> getTwoArgFunction(final Function1<T,T> transformation) {
        return new Function2<T,Void,T>() {
            @Override
            public T apply(T t, Void aVoid) {
                return transformation.apply(t);
            }
        };
    }

    /**
     * Get the transformed column value, given the initial value. This method, by default, just applies the function passed
     * to this instance's constructor to the specified input value. This method should generally not be overridden, unless
     * a compelling case is made for it (such as large efficiency gains).
     *
     * @param t
     *        The input value.
     *
     * @param context
     *        Not used. This argument must always be {@code null}.
     *
     * @return the transformed value.
     */
    protected T getColumnDataTransformation(T t, Void context) {
        return transformation.apply(t);
    }

    @Override
    protected ContextDataRow<Void> getContextDataRow(DataRow row, DataTable table, int rowIndex) {
        return new VoidContextDataRow(row);
    }

    private class VoidContextDataRow extends WrappedDataRow implements ContextDataRow<Void> {

        public VoidContextDataRow(DataRow row) {
            super(row);
        }

        @Override
        public Void getContext() {
            return null;
        }
    }
}
