package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The {@code ConstantDataProvider} is a data provider for variables whose values are constant across all observations.
 *
 * @author crf
 *         Started 4/11/12 5:26 AM
 */
public class ConstantDataProvider extends AbstractDataProvider {
    private final int dataLength;
    private final Map<String,Double> variableData;

    /**
     * Constructor specifying the data source identifier, the initial variable data, the provider length, and and tensor
     * factory used to build data results. This constructor should only be called if the data provider is equivalent to
     * another (already constructed) provider, and that the equivalence needs to be recognized through the data identifier.
     *
     * @param id
     *        The identifier for this provider.
     *
     * @param variableData
     *        Map holding (initial) variable data. The map's keys are the variable names, and the (respective) values are
     *        the variable values.
     *
     * @param dataLength
     *        The length of the data provider.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @throws IllegalArgumentException if {@code id} has not already been allocated via {@code AbstractIdData}.
     */
    public ConstantDataProvider(int id, Map<String,Double> variableData, int dataLength, TensorFactory factory) {
        super(id,factory);
        this.dataLength = dataLength;
        this.variableData = new HashMap<>(variableData);
    }

    /**
     * Constructor specifying the initial variable data, the provider length, and tensor factory used to build data results.
     *
     * @param variableData
     *        Map holding (initial) variable data. The map's keys are the variable names, and the (respective) values are
     *        the variable values.
     *
     * @param dataLength
     *        The length of the data provider.
     *
     * @param factory
     *        The tensor factory used to build data results.
     */
    public ConstantDataProvider(Map<String,Double> variableData, int dataLength, TensorFactory factory) {
        super(factory);
        this.dataLength = dataLength;
        this.variableData = new HashMap<>(variableData);
    }

    /**
     * Constructor for an empty provider specifying the data source identifier, the provider length, and and tensor
     * factory used to build data results. This constructor should only be called if the data provider is equivalent to
     * another (already constructed) provider, and that the equivalence needs to be recognized through the data identifier.
     *
     * @param id
     *        The identifier for this provider.
     *
     * @param dataLength
     *        The length of the data provider.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @throws IllegalArgumentException if {@code id} has not already been allocated via {@code AbstractIdData}.
     */
    public ConstantDataProvider(int id, int dataLength, TensorFactory factory) {
        this(id,new HashMap<String,Double>(),dataLength,factory);
    }

    /**
     * Constructor forr an empty provider specifying specifying the provider length and tensor factory used to build data
     * results.
     *
     * @param dataLength
     *        The length of the data provider.
     *
     * @param factory
     *        The tensor factory used to build data results.
     */
    public ConstantDataProvider(int dataLength, TensorFactory factory) {
        this(new HashMap<String,Double>(),dataLength,factory);
    }

    /**
     * Add a variable to this provider.
     *
     * @param variable
     *        The variable name.
     *
     * @param value
     *        The variable's value.
     *
     * @throws IllegalArgumentException if a variable named {@code variable} already exists in this provider.
     */
    public void addVariable(String variable, double value) {
        if (variableData.containsKey(variable))
            throw new IllegalArgumentException("Variable already in provider: " + variable);
        variableData.put(variable,value);
    }

    @Override
    public int getDataLength() {
        return dataLength;
    }

    @Override
    public boolean hasVariable(String variable) {
        return variableData.containsKey(variable);
    }

    @Override
    public Set<String> getVariables() {
        return variableData.keySet();
    }

    private double[] getData(String variable, int length) {
        if (!hasVariable(variable))
            throw new IllegalArgumentException("Variable not found: " + variable);
        if (length < 1)
            throw new IllegalArgumentException("Invalid data length: " + length);
        double[] data = new double[length];
        Arrays.fill(data,variableData.get(variable));
        return data;
    }

    @Override
    public double[] getVariableData(String variable) {
        return getData(variable,dataLength);
    }

    @Override
    public double[] getVariableData(String variable, int start, int end) {
        return getData(variable,end-start);
    }

    @Override
    public DataProvider getSubData(int start, int end) {
        if (end <= start)
            throw new IllegalArgumentException("Subdata must have a strictly positive range (start=" + start + ", end=" + end + ")");
        if (end > dataLength  || start < 0)
            throw new IllegalArgumentException(String.format("Subdata (start: %d, end: %d) out of bounds for provider of length %d",start,end,dataLength));
        return new ConstantDataProvider(variableData,end-start,factory);
    }
}
