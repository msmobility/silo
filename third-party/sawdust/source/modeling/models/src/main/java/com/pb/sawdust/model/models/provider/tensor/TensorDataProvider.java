package com.pb.sawdust.model.models.provider.tensor;

import com.pb.sawdust.model.models.provider.AbstractDataProvider;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tensor.decorators.primitive.DoubleTensor;
import com.pb.sawdust.tensor.decorators.primitive.size.*;
import com.pb.sawdust.tensor.factory.TensorFactory;

import static com.pb.sawdust.util.Range.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The {@code TensorDataProvider} class is a data provider implementation which provides data from a {@code DoubleTensor}.
 * Specifically, the data provider only holds one variable (named at construction time) and the data is sourced from a
 * single tensor. The location of the data in the tensor, for a given provider location, is determined through a {@link IndexProvider},
 * which provides flexibility as to how the tensor data is accessed and mapped to the provider.
 * <p>
 * This class is internally optimized to take account of the tensor's size and use the {@code getCell(...)} method appropriate
 * to that "sized" (<code>D_DoubleTensor</code>) tensor. Thus, a special extension of this class for size-specific tensor
 * subclasses (<code>Matrix</code>, <code>Vector</code>, <i>etc.</i>) should not be needed. Also, this class is intended
 * for use when <i>random-access</i> to a tensor is needed. If a tensor's values can be applied linearly (<i>i.e.</i> as
 * a vector), then the {@link VectorDataProvider} should be used.
 * <p>
 * Note that to use numeric, but non-{@code double}, tensors, the "numeric tensor" methods in {@code com.pb.tensor.TensorUtil}
 * can be employed.
 *
 * @author crf
 *         Started 4/6/12 6:03 AM
 */
public class TensorDataProvider extends AbstractDataProvider {
    private final String variableName;
    private final IndexProvider indexProvider;
    private final int dimensionality;

    private final DoubleTensor tensor;
    private final DoubleD0Tensor d0Tensor;
    private final DoubleD1Tensor d1Tensor;
    private final DoubleD2Tensor d2Tensor;
    private final DoubleD3Tensor d3Tensor;
    private final DoubleD4Tensor d4Tensor;
    private final DoubleD5Tensor d5Tensor;
    private final DoubleD6Tensor d6Tensor;
    private final DoubleD7Tensor d7Tensor;
    private final DoubleD8Tensor d8Tensor;
    private final DoubleD9Tensor d9Tensor;

    /**
     * Constructor specifying the variable name, the source tensor, the index provider, and the tensor factory.
     *
     * @param variableName
     *        The name of the variable provided by this tensor provider.
     *
     * @param tensor
     *        The tensor holding the data.
     *
     * @param indexProvider
     *        The index provider which will be used to map index requests to locations in the data tensor.
     *
     * @param factory
     *        The tensor factory used to build data results.
     */
    public TensorDataProvider(String variableName, DoubleTensor tensor, IndexProvider indexProvider, TensorFactory factory) {
        super(factory);
        this.variableName = variableName;
        this.indexProvider = indexProvider;
        this.tensor = tensor;
        dimensionality = tensor.size();
        d0Tensor = dimensionality == 0 ? (tensor instanceof DoubleD0Tensor ? (DoubleD0Tensor) tensor : new DoubleD0TensorShell(tensor)) : null;
        d1Tensor = dimensionality == 1 ? (tensor instanceof DoubleD1Tensor ? (DoubleD1Tensor) tensor : new DoubleD1TensorShell(tensor)) : null;
        d2Tensor = dimensionality == 2 ? (tensor instanceof DoubleD2Tensor ? (DoubleD2Tensor) tensor : new DoubleD2TensorShell(tensor)) : null;
        d3Tensor = dimensionality == 3 ? (tensor instanceof DoubleD3Tensor ? (DoubleD3Tensor) tensor : new DoubleD3TensorShell(tensor)) : null;
        d4Tensor = dimensionality == 4 ? (tensor instanceof DoubleD4Tensor ? (DoubleD4Tensor) tensor : new DoubleD4TensorShell(tensor)) : null;
        d5Tensor = dimensionality == 5 ? (tensor instanceof DoubleD5Tensor ? (DoubleD5Tensor) tensor : new DoubleD5TensorShell(tensor)) : null;
        d6Tensor = dimensionality == 6 ? (tensor instanceof DoubleD6Tensor ? (DoubleD6Tensor) tensor : new DoubleD6TensorShell(tensor)) : null;
        d7Tensor = dimensionality == 7 ? (tensor instanceof DoubleD7Tensor ? (DoubleD7Tensor) tensor : new DoubleD7TensorShell(tensor)) : null;
        d8Tensor = dimensionality == 8 ? (tensor instanceof DoubleD8Tensor ? (DoubleD8Tensor) tensor : new DoubleD8TensorShell(tensor)) : null;
        d9Tensor = dimensionality == 9 ? (tensor instanceof DoubleD9Tensor ? (DoubleD9Tensor) tensor : new DoubleD9TensorShell(tensor)) : null;
    }

    /**
     * Convenience constructor for a tensor data provider whose index lookup comes from variables in a data provider.
     *
     * @param variableName
     *        The name of the variable provided by this tensor provider.
     *
     * @param tensor
     *        The tensor holding the data.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @param indexSource
     *        The data provider which will be used to get locate index values.
     *
     * @param indexVariables
     *        The variables in {@code indexSource} to use for the indices in {@code tensor}. There should be one variable
     *        for each dimension in {@code tensor}, and these variables should be ordered according to the dimension they
     *        correspond to.
     */
    public TensorDataProvider(String variableName, DoubleTensor tensor, TensorFactory factory, DataProvider indexSource, String ... indexVariables) {
        this(variableName,tensor,new DataProviderIndexProvider(indexSource,Arrays.asList(indexVariables)),factory);
    }

    /**
     * Convenience constructor for a tensor data provider whose index lookup comes from variables in a data provider.
     *
     * @param variableName
     *        The name of the variable provided by this tensor provider.
     *
     * @param tensor
     *        The tensor holding the data.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @param indexSource
     *        The data provider which will be used to get locate index values.
     *
     * @param indexVariables
     *        The variables in {@code indexSource} to use for the indices in {@code tensor}. There should be one variable
     *        for each dimension in {@code tensor}, and these variables should be ordered according to the dimension they
     *        correspond to.
     */
    public TensorDataProvider(String variableName, DoubleTensor tensor, TensorFactory factory, DataProvider indexSource, List<String> indexVariables) {
        this(variableName,tensor,new DataProviderIndexProvider(indexSource,indexVariables),factory);
    }

    /**
     * Convenience constructor for a tensor data provider whose index lookup comes from variables in a data tablw.
     *
     * @param variableName
     *        The name of the variable provided by this tensor provider.
     *
     * @param tensor
     *        The tensor holding the data.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @param indexSource
     *        The data table which will be used to get locate index values.
     *
     * @param indexColumns
     *        The columns in {@code indexSource} to use for the indices in {@code tensor}. There should be one column
     *        for each dimension in {@code tensor}, and these columns should be ordered according to the dimension they
     *        correspond to.
     */
    public TensorDataProvider(String variableName, DoubleTensor tensor, TensorFactory factory, DataTable indexSource, String ... indexColumns) {
        this(variableName,tensor,new DataTableIndexProvider(indexSource,Arrays.asList(indexColumns)),factory);
    }

    /**
     * Convenience constructor for a tensor data provider whose index lookup comes from variables in a data tablw.
     *
     * @param variableName
     *        The name of the variable provided by this tensor provider.
     *
     * @param tensor
     *        The tensor holding the data.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @param indexSource
     *        The data table which will be used to get locate index values.
     *
     * @param indexColumns
     *        The columns in {@code indexSource} to use for the indices in {@code tensor}. There should be one column
     *        for each dimension in {@code tensor}, and these columns should be ordered according to the dimension they
     *        correspond to.
     */
    public TensorDataProvider(String variableName, DoubleTensor tensor, TensorFactory factory, DataTable indexSource, List<String> indexColumns) {
        this(variableName,tensor,new DataTableIndexProvider(indexSource,indexColumns),factory);
    }

    @Override
    public int getDataLength() {
        return indexProvider.getIndexLength();
    }

    @Override
    public boolean hasVariable(String variable) {
        return variableName.equals(variable);
    }

    @Override
    public Set<String> getVariables() {
        Set<String> variable = new HashSet<>();
        variable.add(variableName);
        return variable;
    }

    @Override
    public double[] getVariableData(String variable) {
        return getVariableData(variable,0,getDataLength());
    }

    @Override
    public double[] getVariableData(String variable, int start, int end) {
        if (!hasVariable(variable))
            throw new IllegalArgumentException("Variable not found: " + variable);
        int[][] indices = new int[dimensionality][];
        for (int i : range(dimensionality))
            indices[i] = indexProvider.getIndices(i,start,end);
        double[] data = new double[end-start];
        switch (dimensionality) {
            case 0 : data[0] = d0Tensor.getCell(); break;
            case 1 : for (int i = 0; i < data.length; i++)
                         data[i] = d1Tensor.getCell(indices[0][i]); break;
            case 2 : for (int i = 0; i < data.length; i++)
                         data[i] = d2Tensor.getCell(indices[0][i],indices[1][i]); break;
            case 3 : for (int i = 0; i < data.length; i++)
                         data[i] = d3Tensor.getCell(indices[0][i],indices[1][i],indices[2][i]); break;
            case 4 : for (int i = 0; i < data.length; i++)
                         data[i] = d4Tensor.getCell(indices[0][i],indices[1][i],indices[2][i],indices[3][i]); break;
            case 5 : for (int i = 0; i < data.length; i++)
                         data[i] = d5Tensor.getCell(indices[0][i],indices[1][i],indices[2][i],indices[3][i],indices[4][i]); break;
            case 6 : for (int i = 0; i < data.length; i++)
                         data[i] = d6Tensor.getCell(indices[0][i],indices[1][i],indices[2][i],indices[3][i],indices[4][i],indices[5][i]); break;
            case 7 : for (int i = 0; i < data.length; i++)
                         data[i] = d7Tensor.getCell(indices[0][i],indices[1][i],indices[2][i],indices[3][i],indices[4][i],indices[5][i],indices[6][i]); break;
            case 8 : for (int i = 0; i < data.length; i++)
                         data[i] = d8Tensor.getCell(indices[0][i],indices[1][i],indices[2][i],indices[3][i],indices[4][i],indices[5][i],indices[6][i],indices[7][i]); break;
            case 9 : for (int i = 0; i < data.length; i++)
                         data[i] = d9Tensor.getCell(indices[0][i],indices[1][i],indices[2][i],indices[3][i],indices[4][i],indices[5][i],indices[6][i],indices[7][i],indices[8][i]); break;
            default : {
                int[] indexer = new int[dimensionality];
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < indexer.length; j++)
                        indexer[j] = indices[j][i];
                    data[i] = tensor.getCell(indexer);
                }
            }
        }
        return data;
    }
}
