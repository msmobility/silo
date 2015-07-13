package com.pb.sawdust.model.models.provider.filter;

import com.pb.sawdust.calculator.tensor.la.TransposedMatrix;
import com.pb.sawdust.model.models.provider.*;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.SliceIndex;
import com.pb.sawdust.tensor.slice.SliceUtil;
import com.pb.sawdust.util.annotations.Broken;
import com.pb.sawdust.util.array.DoubleTypeSafeArray;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * The {@code FixedFilterDataProvider} ...
 *
 * @author crf <br/>
 *         Started 3/5/11 9:25 AM
 */
@Broken(reason="copied from FilteredDataProvider, need to actually implement")
public class FixedFilterDataProvider extends AbstractDataProvider implements CalculationProvider {
    private final DataProvider provider;
    private final FixedDataFilter filter;
    private final int[] index;

    public FixedFilterDataProvider(TensorFactory factory, DataProvider provider, BooleanVector filter) {
        super(factory);
        if (filter.size(0) != provider.getDataLength())
            throw new IllegalArgumentException(String.format("Filter size (%d) and provider length (%d) must be equal.",filter.size(0),provider.getDataLength()));
        this.provider = provider;
        this.filter = new FixedDataFilter(filter);
        int[] index = new int[filter.size(0)];
        int counter = 0;
        for (int i = 0; i < index.length; i++)
            if (filter.getCell(i))
                index[counter++] = i;
        this.index = Arrays.copyOfRange(index,0,counter);
    }

    public FixedFilterDataProvider(TensorFactory factory, DataProvider provider, DataFilter filter) {
        this(factory,provider,filter.getFilter(provider));
    }

    @Override
    public int getDataLength() {
        return index.length;
    }

    @Override
    public boolean hasVariable(String variable) {
        return provider.hasVariable(variable);
    }

    @Override
    public Set<String> getVariables() {
        return provider.getVariables();
    }

    @Override
    public double[] getVariableData(String variable) {
        double[] variableData = provider.getVariableData(variable);
        double[] filteredData = new double[index.length];
        for (int i = 0; i < index.length; i++)
            filteredData[i] = variableData[index[i]];
        return filteredData;
    }

    @Override
    public double[] getVariableData(String variable, int start, int end) {
        if (end <= start)
            throw new IllegalArgumentException("Subdata must have a strictly positive range (start=" + start + ", end=" + end + ")");
        if (end > index.length  || start < 0)
            throw new IllegalArgumentException(String.format("Subdata (start: %d, end: %d) out of bounds for provider of length %d",start,end,index.length));
        return Arrays.copyOfRange(getVariableData(variable),start,end);
    }

    @Override
    public DoubleMatrix getData(List<String> variables, TensorFactory factory) {
        //this, or leave super? - not sure which is better (super will be smaller memory but maybe more copying...)
        double[][] vData = new double[variables.size()][];
        int counter = 0;
        for (String variable : variables)
            vData[counter++] = provider.getVariableData(variable);
        DoubleMatrix mat = (DoubleMatrix) factory.doubleTensor(vData[0].length,vData.length);
        TransposedMatrix.transpose(mat).setTensorValues(new DoubleTypeSafeArray(vData));
        return (DoubleMatrix) mat.getReferenceTensor(SliceIndex.getSliceIndex(mat.getIndex(),filter.getFilteredSlice(provider), SliceUtil.fullSlice(mat.size(1))));
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
        return new FixedFilterSubDataProvider(start,end);
    }

    private class FixedFilterSubDataProvider extends LazySubDataProvider implements CalculationProvider {

        private FixedFilterSubDataProvider(int start, int end) {
            super(FixedFilterDataProvider.this,FixedFilterDataProvider.this.factory,start,end);
        }

        public VariableCalculation getCalculation(String variable) {
            return FixedFilterDataProvider.this.getCalculation(variable);
        }

        public VariableCalculation getResolvedCalculation(String variable) {
            return FixedFilterDataProvider.this.getResolvedCalculation(variable);
        }

        public boolean containsCalculatedVariables(){
            return FixedFilterDataProvider.this.containsCalculatedVariables();
        }
    }
}
