package com.pb.sawdust.tabledata.transform.row;

import com.pb.sawdust.calculator.Function2;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.metadata.DataType;

/**
 * The {@code RowWiseColumnTransformation} is a row-wise data table transformation which modifies a single column via a
 * specified function.
 *
 * @param <T>
 *        The type the transformation acts on. This will often correspond to the transformed column's data type.
 *
 * @author crf
 *         Started 1/25/12 6:07 AM
 */
public class BasicRowWiseColumnTransformation<T> extends FunctionRowWiseColumnTransformation<T,DataRow> {

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
     *        The function which will transform the original column value. The initial column value and the data row being
     *        transformed are passed to this function as arguments.
     */
    protected BasicRowWiseColumnTransformation(String column, DataType dataType, Function2<T,DataRow,T> transformation) {
        super(column,dataType,transformation);
    }

    /**
     * Constructor specifying the column to transform and the function which will calculate the transformation values. The
     * data type this transformation acts on will be the same as the type of the column in the data table.
     *
     * @param column
     *        The name of the column this transformation will act on.
     *
     * @param transformation
     *        The function which will transform the original column value. The initial column value and the data row being
     *        transformed are passed to this function as arguments.
     */
    protected BasicRowWiseColumnTransformation(String column, Function2<T,DataRow,T> transformation) {
        super(column,transformation);
    }

    @Override
    protected ContextDataRow<DataRow> getContextDataRow(DataRow row, DataTable table, int rowIndex) {
        return new DegenerateContextDataRow(row);
    }
}
