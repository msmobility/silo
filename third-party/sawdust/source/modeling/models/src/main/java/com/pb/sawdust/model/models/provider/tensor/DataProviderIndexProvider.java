package com.pb.sawdust.model.models.provider.tensor;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code DataProviderIndexProvider} class is an index provider whose index values are obtained from a {@code DataProvider}.
 * The length of this provider is the same as the data length of the source data provider, and the index lookup values
 * are specified as variables in the data provider. Because a data provider returns {@code double}s instead of {@code int}s,
 * the returned index values are casts (not rounded values) from the source provider data.
 *
 * @author crf
 *         Started 4/6/12 7:36 AM
 */
public class DataProviderIndexProvider extends VariableIndexProvider<Integer> {
    private final DataProvider dataProvider;

    /**
     * Constructor specifying the source data provider, the variables to use for the index lookups, and the index ids.
     *
     * @param dataProvider
     *        The source data provider.
     *
     * @param indexVariables
     *        The index variables, in order (of the dimensions they corresponding to).
     *
     * @param ids
     *        A mapping from the dimension to use id mapping to the mapping to use. Any dimensions not present in this
     *        map will not use id mapping.
     *
     * @throws IllegalArgumentException if any dimension key in {@code ids} is less than zero or greater than or equal to
     *                                  {@code indexVariables.size()}, or if any column in {@code indexVariables} is not found
     *                                  in {@code dataProvider}.
     */
    @SuppressWarnings("unchecked") //this is an internal transfer which is correct for the way the mapping is used
    public DataProviderIndexProvider(DataProvider dataProvider, List<String> indexVariables, Map<Integer,List<Integer>> ids) {
        super(indexVariables,dummy(ids));
        this.dataProvider = dataProvider;
        for (String variable : indexVariables)
            if (!dataProvider.hasVariable(variable))
                throw new IllegalArgumentException("Variable not found in provider: " + variable);
    }

    @SuppressWarnings("unchecked") //this is internally correct
    private static Map<Integer,List<? extends Integer>> dummy(Map<?,?> map) {
        return (Map<Integer,List<? extends Integer>>) map;
    }

    /**
     * Constructor specifying the source data provider and the columns to use for the index lookups. No index id mappingx will
     * be used.
     *
     * @param dataProvider
     *        The source data provider.
     *
     * @param indexVariables
     *        The index columns, in order (of the dimensions they corresponding to).
     *
     * @throws IllegalArgumentException if any column in {@code indexVariables} is not found in {@code dataProvider}.
     */
    public DataProviderIndexProvider(DataProvider dataProvider, List<String> indexVariables) {
        this(dataProvider,indexVariables,new HashMap<Integer,List<Integer>>());
    }

    private void checkDimension(int dimension) {
        if (dimension < 0 || dimension >= getDimensionCount())
            throw new IllegalArgumentException("Index out of bounds: " + dimension);
    }

    @Override
    protected Integer getIndexId(String variable, int location) {
        return getIndex(variable,location);
    }

    @Override
    protected Integer[] getIndexIds(String variable, int start, int end) {
        double[] dindices = dataProvider.getVariableData(variable,start,end);
        Integer[] indices = new Integer[dindices.length];
        for (int i = 0; i < dindices.length; i++)
            indices[i] = (int) dindices[i];
        return indices;
    }

    @Override
    protected int getIndex(String variable, int location) {
        return (int) dataProvider.getVariableData(variable,location,location+1)[0];
    }

    @Override
    protected int[] getIndices(String variable, int start, int end) {
        double[] dindices = dataProvider.getVariableData(variable,start,end);
        int[] indices = new int[dindices.length];
        for (int i = 0; i < dindices.length; i++)
            indices[i] = (int) dindices[i];
        return indices;
    }

    @Override
    public int getIndexLength() {
        return dataProvider.getDataLength();
    }
}
