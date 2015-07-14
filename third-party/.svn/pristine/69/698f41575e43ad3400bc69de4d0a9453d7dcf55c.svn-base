package com.pb.sawdust.tabledata.transform.row;

import com.pb.sawdust.calculator.Function2;
import com.pb.sawdust.tabledata.metadata.DataType;

/**
 * The {@code FunctionRowWiseColumnTransformation} is a contextual row-wise column transformation where the transformed
 * values are calculated via a function acting on the initial value of the column and the data row being transformed.
 *
 * @param <T>
 *        The type the transformation acts on. This will often correspond to the transformed column's data type.
 *
 * @param <C>
 *        The typ of the context.
 *
 * @author crf
 *         Started 1/25/12 6:15 AM
 */
public abstract class FunctionRowWiseColumnTransformation<T,C> extends ContextualRowWiseColumnTransformation<T,C> {
    private final Function2<T,C,T> transformation;

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
     *        The function which will transform the original column value. The initial column value and the context are
     *        passed to this function as arguments.
     */
    protected FunctionRowWiseColumnTransformation(String column, DataType dataType, Function2<T,C,T> transformation) {
        super(column,dataType);
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
     *        The function which will transform the original column value. The initial column value and the context are
     *        passed to this function as arguments.
     */
    protected FunctionRowWiseColumnTransformation(String column, Function2<T,C,T> transformation) {
        super(column);
        this.transformation = transformation;
    }

    T getColumnDataTransformation(T t, C context) {
        return transformation.apply(t,context);
    }

    @Override
    protected T getTransformedData(T t, ContextDataRow<C> contextDataRow) {
        return getColumnDataTransformation(t,contextDataRow.getContext());
    }
}
