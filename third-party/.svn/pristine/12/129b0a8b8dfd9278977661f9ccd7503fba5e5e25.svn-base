package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.TableKey;
import com.pb.sawdust.tabledata.basic.BasicTableKey;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.*;

/**
 * The {@code DataTableReferenceDataProvider} class is a data provider which allows variable values from one data table
 * to be used via a key-column reference to a base data table. That is, the source data table can be joined to the data
 * provider via a reference link between a column in the data table and a variable in the provider. (If the base data provider
 * is viewed abstractly as a data table then this class builds a simple join relationship between it and the source data
 * table.)
 * <p>
 * A "missing value" may be specified, which is a special number which, when found in the base provider's key variable,
 * indicates that the base provider observation doesn't map to the source data table, and in that case the "mapped" variable
 * value will just be the missing value. All values in the base provider which are not this missing value must have a corresponding
 * value in the source data table, or an exception will be thrown. By default, a missing value is not specified.
 *
 * @author crf
 *         Started 5/23/12 3:09 PM
 */
public class DataTableReferenceDataProvider extends AbstractDataProvider {
    private final DataTable table;
    private final TableKey<Number> key;
    private final DataProvider baseProvider;
    private final String providerKeyVariable;
    private final Number missingValue;

    @SuppressWarnings("unchecked")  //switching to a <Number> key is ok here, handled internally
    private <N extends Number> DataTableReferenceDataProvider(TensorFactory factory, DataTable table, TableKey<N> key, DataProvider baseProvider, String providerKeyVariable, N missingValue) {
        super(factory);
        this.table = table;
        this.key = (TableKey<Number>) key;
        this.baseProvider = baseProvider;
        this.providerKeyVariable = providerKeyVariable;
        this.missingValue = missingValue == null ? null : (Number) key.getKeyColumnType().coerce(missingValue);
    }

    /**
     * Constructor specifying the tensor factory used to build data results, the source data table and its key column,
     * the base data provider and its key variable. No missing value functionality will be available through the provider.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @param table
     *        The source data table.
     *
     * @param keyColumn
     *        The table key column for {@code table} which will be used to lookup the appropriate value for a given observation
     *        in the base data provider.
     *
     * @param baseProvider
     *        The base data provider.
     *
     * @param providerKeyVariable
     *        The variable in {@code baseProvider} whose values will be matched through {@code key} to lookup the variable
     *        values in {@code table}.
     *
     * @param missingValue
     *        The value to use to represent as a missing value in {@code baseProvider}'s variable {@code providerKeyVariable}.
     *
     * @throws IllegalArgumentException if {@code providerKeyVariable} is not a variable in {@code baseProvider}, if {@code keyColumn}
     *                                  is not a column in {@code table}, or if any of the column names in {@code table}
     *                                  are already used for variables in {@code baseProvider}.
     */
    public DataTableReferenceDataProvider(TensorFactory factory, DataTable table, String keyColumn, DataProvider baseProvider, String providerKeyVariable, Number missingValue) {
        this(factory,table,new BasicTableKey<Number>(table,keyColumn),baseProvider,providerKeyVariable,missingValue);
    }

    /**
     * Constructor specifying the tensor factory used to build data results, the source data table and its key column,
     * the base data provider and its key variable. No missing value functionality will be available through the provider.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @param table
     *        The source data table.
     *
     * @param keyColumn
     *        The table key column for {@code table} which will be used to lookup the appropriate value for a given observation
     *        in the base data provider.
     *
     * @param baseProvider
     *        The base data provider.
     *
     * @param providerKeyVariable
     *        The variable in {@code baseProvider} whose values will be matched through {@code key} to lookup the variable
     *        values in {@code table}.
     *
     * @throws IllegalArgumentException if {@code providerKeyVariable} is not a variable in {@code baseProvider}, if {@code keyColumn}
     *                                  is not a column in {@code table}, or if any of the column names in {@code table}
     *                                  are already used for variables in {@code baseProvider}.
     */
    public DataTableReferenceDataProvider(TensorFactory factory, DataTable table, String keyColumn, DataProvider baseProvider, String providerKeyVariable) {
        this(factory,table,new BasicTableKey<Number>(table,keyColumn),baseProvider,providerKeyVariable,null);
    }

    @SuppressWarnings("unchecked")  //switching to a <Number> key is ok here, handled internally
    private <N extends Number> DataTableReferenceDataProvider(int id,TensorFactory factory, DataTable table, TableKey<N> key, DataProvider baseProvider, String providerKeyVariable, N missingValue) {
        super(id,factory);
        this.table = table;
        this.key = (TableKey<Number>) key;
        this.baseProvider = baseProvider;
        this.providerKeyVariable = providerKeyVariable;
        this.missingValue = missingValue == null ? null : (Number) key.getKeyColumnType().coerce(missingValue);
    }

    /**
     * Constructor specifying the data source identifier, the tensor factory used to build data results, the source data
     * table and its key column, the base data provider and its key variable. No missing value functionality will be available
     * through the provider. This constructor should only be called if the data provider is equivalent to another (already
     * constructed) provider, and that the equivalence needs to be recognized through the data identifier.
     *
     * @param id
     *        The identifier for this provider.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @param table
     *        The source data table.
     *
     * @param keyColumn
     *        The table key column for {@code table} which will be used to lookup the appropriate value for a given observation
     *        in the base data provider.
     *
     * @param baseProvider
     *        The base data provider.
     *
     * @param providerKeyVariable
     *        The variable in {@code baseProvider} whose values will be matched through {@code key} to lookup the variable
     *        values in {@code table}.
     *
     * @param missingValue
     *        The value to use to represent as a missing value in {@code baseProvider}'s variable {@code providerKeyVariable}.
     *
     * @throws IllegalArgumentException if {@code providerKeyVariable} is not a variable in {@code baseProvider}, if {@code keyColumn}
     *                                  is not a column in {@code table}, or if any of the column names in {@code table}
     *                                  are already used for variables in {@code baseProvider}.
     */
    public DataTableReferenceDataProvider(int id, TensorFactory factory, DataTable table, String keyColumn, DataProvider baseProvider, String providerKeyVariable, Number missingValue) {
        this(id,factory,table,new BasicTableKey<Number>(table,keyColumn),baseProvider,providerKeyVariable,missingValue);
    }

    /**
     * Constructor specifying the data source identifier, the tensor factory used to build data results, the source data
     * table and its key column, the base data provider and its key variable. No missing value functionality will be available
     * through the provider. This constructor should only be called if the data provider is equivalent to another (already
     * constructed) provider, and that the equivalence needs to be recognized through the data identifier.
     *
     * @param id
     *        The identifier for this provider.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @param table
     *        The source data table.
     *
     * @param keyColumn
     *        The table key column for {@code table} which will be used to lookup the appropriate value for a given observation
     *        in the base data provider.
     *
     * @param baseProvider
     *        The base data provider.
     *
     * @param providerKeyVariable
     *        The variable in {@code baseProvider} whose values will be matched through {@code key} to lookup the variable
     *        values in {@code table}.
     *
     * @throws IllegalArgumentException if {@code providerKeyVariable} is not a variable in {@code baseProvider}, if {@code keyColumn}
     *                                  is not a column in {@code table}, or if any of the column names in {@code table}
     *                                  are already used for variables in {@code baseProvider}.
     */
    public DataTableReferenceDataProvider(int id, TensorFactory factory, DataTable table, String keyColumn, DataProvider baseProvider, String providerKeyVariable) {
        this(id,factory,table,new BasicTableKey<Number>(table,keyColumn),baseProvider,providerKeyVariable,null);
    }

    @Override
    public int getDataLength() {
        return baseProvider.getDataLength();
    }

    @Override
    public boolean hasVariable(String variable) {
        return table.hasColumn(variable);
    }

    @Override
    public Set<String> getVariables() {
        return new HashSet<>(Arrays.asList(table.getColumnLabels()));
    }

    @Override
    public double[] getVariableData(String variable) {
        if (!hasVariable(variable))
            throw new IllegalArgumentException("Variable not found: " + variable);
        double[] data = new double[getDataLength()];
        int counter = 0;
        for (double d : baseProvider.getVariableData(providerKeyVariable)) {
            Number keyValue = (Number) key.getKeyColumnType().coerce(d);
            data[counter++] = keyValue.equals(missingValue) ? missingValue.doubleValue() : table.getRow(key.getRowNumber(keyValue)).getCellAsDouble(variable);
        }
        return data;
    }

    @Override
    public double[] getVariableData(String variable, int start, int end) {
        if (!hasVariable(variable))
            throw new IllegalArgumentException("Variable not found: " + variable);
        double[] data = new double[getDataLength()];
        if (end <= start || start < 0 || end >= data.length)
            throw new IllegalArgumentException(String.format("Data partition out of bounds: (%d,%d)",start,end));
        int counter = 0;
        for (double d : baseProvider.getSubData(start,end).getVariableData(providerKeyVariable)) {
            Number keyValue = (Number) key.getKeyColumnType().coerce(d);
            data[counter++] = keyValue.equals(missingValue) ? missingValue.doubleValue() : table.getRow(key.getRowNumber(keyValue)).getCellAsDouble(variable);
        }
        return data;
    }

    /**
     * Get a data provider in which all of the variables held by the source table in this provider have been renamed. The
     * renaming function will give every variable a new name of the form <code>[originalVariableName]@[tableName]</code>, where
     * <code>[tableName]</code> is the label (name) for the source table used by this provider.
     *
     * @return a data provider wrapping this one which uses transformed variables specific to this provider's source table name.
     */
    public DataProvider getStandardVariableRenamedProvider() {
        return VariableRenamingDataProvider.getStandardVariableRenamedProvider(this,table.getLabel());
    }
}
