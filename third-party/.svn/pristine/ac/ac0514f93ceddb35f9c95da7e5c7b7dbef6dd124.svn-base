package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * The {@code DataTableDataProvider} class is a data provider backed by a {@code DataTable} where variable is represented
 * by a column. If data coersion is not enabled in the data table, then attempting to get data from the provider may cause
 * an exception to be thrown if the variable is held a non-{@code DataType.DOUBLE} column.  If the backing data table
 * is mutable, then the provider will be as well. The backing data table should not be empty (zero-length data providers
 * are not allowed), so constructing or using this provider when the backing table is empty will cause an exception to
 * be thrown.
 *
 * @author crf <br/>
 *         Started Jul 27, 2010 2:10:23 PM
 */
public class DataTableDataProvider extends AbstractDataProvider {
    private final DataTable table;

    /**
     * Constructor specifing the data identifier, backing data table and the tensor factory used to build data results.
     *
     * @param dataId
     *        The data id to use for this provider.
     *
     * @param table
     *        The data table the provider will get its data from.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @throws IllegalArgumentException if {@code table} is empty (contains zero rows of data), or if {@code dataId} has 
     *                                  not already been allocated via {@code AbstractIdData}.
     */
    public DataTableDataProvider(int dataId, DataTable table, TensorFactory factory) {
        super(dataId,factory);
        if (table.getRowCount() == 0)
            throw new IllegalArgumentException("DataTableDataProvider data table must not be empty.");
        this.table = table;
    }

    /**
     * Constructor specifing the backing data table and the tensor factory used to build data results.
     *
     * @param table
     *        The data table the provider will get its data from.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @throws IllegalArgumentException if {@code table} is empty (contains zero rows of data).
     */
    public DataTableDataProvider(DataTable table, TensorFactory factory) {
        super(factory);
        if (table.getRowCount() == 0)
            throw new IllegalArgumentException("DataTableDataProvider data table must not be empty.");
        this.table = table;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException if {@code table} is empty (contains zero rows of data).
     */
    @Override
    public int getDataLength() {
        int length = table.getRowCount();
        if (length == 0)
            throw new IllegalStateException("DataTableDataProvider data table must not be empty.");
        return length;
    }

    @Override
    public double[] getVariableData(String variable) {
        if (!hasVariable(variable)) {
            throw new IllegalArgumentException("Variable not found: " + variable);
        }
        getDataLength(); //to check for empty data
        return table.getDoubleColumn(variable).getPrimitiveColumn();
    }

    @Override
    public double[] getVariableData(String variable, int start, int end) {
        if (!hasVariable(variable))
            throw new IllegalArgumentException("Variable not found: " + variable);
        getDataLength(); //to check for empty data
        return table.getTablePartition(start,end).getDoubleColumn(variable).getPrimitiveColumn();
    }

    @Override
    public boolean hasVariable(String variable) {
        return table.hasColumn(variable);
    }

    @Override
    public Set<String> getVariables() {
        return new HashSet<String>(Arrays.asList(table.getColumnLabels()));
    }
}
