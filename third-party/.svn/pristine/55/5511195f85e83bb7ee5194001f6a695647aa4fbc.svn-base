package com.pb.sawdust.model.models.provider.filter;

import com.pb.sawdust.calculator.tensor.la.TransposedMatrix;
import com.pb.sawdust.model.models.provider.*;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.SliceIndex;
import com.pb.sawdust.tensor.slice.Slice;
import com.pb.sawdust.tensor.slice.SliceUtil;
import com.pb.sawdust.util.array.DoubleTypeSafeArray;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * The {@code FilteredDataProvider} ...
 *
 * @author crf <br/>
 *         Started 3/3/11 6:58 AM
 */
public class FilteredDataProvider extends AbstractDataProvider implements CalculationProvider {
    private final DataProvider provider;
    private final DataFilter filter;


    public FilteredDataProvider(TensorFactory factory, DataProvider provider, DataFilter filter) {
        super(factory);
        this.provider = provider;
        this.filter = filter;
    }

    private int getDataLength(DataFilter filter) {
        return filter.getFilteredSlice(provider).getSize();
    }

    @Override
    public int getDataLength() {
        return getDataLength(filter);
    }

    @Override
    public boolean hasVariable(String variable) {
        return provider.hasVariable(variable);
    }

    @Override
    public Set<String> getVariables() {
        return provider.getVariables();
    }

    private double[] getVariableData(String variable, DataFilter filter) {
        double[] variableData = provider.getVariableData(variable);
        int[] indices = filter.getFilteredSlice(provider).getSliceIndices();
        double[] filteredData = new double[indices.length];
        for (int i = 0; i < filteredData.length; i++)
            filteredData[i] = variableData[indices[i]];
        return filteredData;
    }

    @Override
    public double[] getVariableData(String variable) {
        return getVariableData(variable,filter);
    }

    @Override
    public double[] getVariableData(String variable, int start, int end) {
        double[] variableData = getVariableData(variable);
        if (end <= start)
            throw new IllegalArgumentException("Subdata must have a strictly positive range (start=" + start + ", end=" + end + ")");
        if (end > variableData.length  || start < 0)
            throw new IllegalArgumentException(String.format("Subdata (start: %d, end: %d) out of bounds for provider of length %d",start,end,variableData.length));
        return Arrays.copyOfRange(variableData,start,end);
    }

    private DoubleMatrix getData(List<String> variables, TensorFactory factory, DataFilter filter) {
        double[][] vData = new double[variables.size()][];
        int counter = 0;
        for (String variable : variables)
            vData[counter++] = provider.getVariableData(variable);
        DoubleMatrix mat = (DoubleMatrix) factory.doubleTensor(vData[0].length,vData.length);
        TransposedMatrix.transpose(mat).setTensorValues(new DoubleTypeSafeArray(vData));
        return (DoubleMatrix) mat.getReferenceTensor(SliceIndex.getSliceIndex(mat.getIndex(),filter.getFilteredSlice(provider),SliceUtil.fullSlice(mat.size(1))));
    }

    @Override
    public DoubleMatrix getData(List<String> variables, TensorFactory factory) {
        return getData(variables,factory,filter);
    }

    @Override
    public VariableCalculation getCalculation(String variable) {
        return (provider instanceof CalculationProvider) ? ((CalculationProvider) provider).getCalculation(variable) : new VariableCalculation(variable);
    }

    @Override
    public VariableCalculation getResolvedCalculation(String variable) {
        return (provider instanceof CalculationProvider) ? ((CalculationProvider) provider).getResolvedCalculation(variable) : new VariableCalculation(variable);
    }

    @Override
    public boolean containsCalculatedVariables() {
        return (provider instanceof CalculationProvider) && ((CalculationProvider) provider).containsCalculatedVariables();
    }

    @Override
    public DataProvider getSubData(int start, int end) {
        return new FilteredSubDataProvider(start,end); //todo: note that the subdata provider is quite inefficient, and shouldn't be used in general
    }

    private class FilteredSubDataProvider extends LazySubDataProvider implements CalculationProvider {
        private final SubDataFilter filter;

        private FilteredSubDataProvider(int start, int end) {
            super(FilteredDataProvider.this,FilteredDataProvider.this.factory,start,end);
            filter = new SubDataFilter(start,end);
        }

        @Override
        public int getDataLength() {
            return FilteredDataProvider.this.getDataLength(filter);
        }

        @Override
        public double[] getVariableData(String variable) {
            return FilteredDataProvider.this.getVariableData(variable,filter);
        }

        @Override
        public DoubleMatrix getData(List<String> variables, TensorFactory factory) {
            return FilteredDataProvider.this.getData(variables,factory,filter);
        }

        public VariableCalculation getCalculation(String variable) {
            return FilteredDataProvider.this.getCalculation(variable);
        }

        public VariableCalculation getResolvedCalculation(String variable) {
            return FilteredDataProvider.this.getResolvedCalculation(variable);
        }

        public boolean containsCalculatedVariables(){
            return FilteredDataProvider.this.containsCalculatedVariables();
        }
    }

    private class SubDataFilter extends AbstractDataFilter {
        private final Slice slice;

        public SubDataFilter(int start, int end) {
            slice = SliceUtil.range(start,end);
        }

        @Override
        public BooleanVector getFilter(DataProvider provider) {
            BooleanVector v = FilteredDataProvider.this.filter.getFilter(FilteredDataProvider.this.provider);
            return (BooleanVector) v.getReferenceTensor(SliceIndex.getSliceIndex(v.getIndex(),slice));
        }
    }
}
