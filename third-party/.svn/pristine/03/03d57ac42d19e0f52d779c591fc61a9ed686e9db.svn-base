package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.calculator.Function1;
import com.pb.sawdust.calculator.tensor.la.TransposedMatrix;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.model.models.trace.ConstantTrace;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.util.array.DoubleTypeSafeArray;
import com.pb.sawdust.util.collections.iterators.IterableIterator;
import com.pb.sawdust.util.concurrent.IteratorTask;
import com.pb.sawdust.util.exceptions.RuntimeInterruptedException;
import com.pb.sawdust.util.exceptions.RuntimeWrappingException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * The {@code AbstractDataProvider} class is a skeletal implementation of {@code DataProvider}.  It extends the
 * {@code AbstractIdData} class to fulfill its data identification requirements, and uses the {@code LazySubDataProvider}
 * class for creating subdata. The documentation for those classes should be referenced for implementation details. Of
 * particular note, the {@link #getVariableData(String, int, int)} method should be implemented to get the actual variable
 * sub-data, and should not call another {@code DataProvider} method to ge the data.
 *
 * @author crf <br/>
 *         Started Jul 27, 2010 2:31:31 PM
 */
public abstract class AbstractDataProvider extends AbstractIdData implements DataProvider {
    /**
     * The tensor factory that is used to build data results for this provider.
     */
    protected final TensorFactory factory;

    /**
     * Constructor specifying the tensor factory used to build data results.
     *
     * @param factory
     *        The tensor factory used to build data results.
     */
    public AbstractDataProvider(TensorFactory factory) {
        super();
        this.factory = factory;
    }

    /**
     * Constructor specifying the data source identifier and tensor factory used to build data results. This method should
     * only be called if the data provider is equivalent to another (already constructed) provider, and that the equivalence
     * needs to be recognized through the data identifier.
     *
     * @param id
     *        The identifier for this provider.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @throws IllegalArgumentException if {@code id} has not already been allocated via {@code AbstractIdData}.
     */
    public AbstractDataProvider(int id, TensorFactory factory) {
        super(id);
        this.factory = factory;
    }

    @Override
    public DataProvider getSubData(int start, int end) {
        return new LazySubDataProvider(this,factory,start,end);
    }

    @Override
    public int getAbsoluteStartIndex() {
        return 0;
    }

    @Override
    public DoubleMatrix getData(List<String> variables) {
        return getData(variables,factory);
    }

    @Override
    public DoubleMatrix getData(List<String> variables, TensorFactory factory) {
        final double[][] vData = new double[variables.size()][];
        int counter = 0;
//        for (String variable : variables)
//            vData[counter++] = getVariableData(variable);

        Function1<String,double[]> variableDataRetrievalFunction = new Function1<String,double[]>() {
                    @Override
                    public double[] apply(String variable) {
                        return getVariableData(variable);
                    }
                };
        IteratorTask<String,double[]> variableRetrievalTask = new IteratorTask<>(variables.iterator(),variableDataRetrievalFunction);
        for (double[] data : new IterableIterator<>(new ForkJoinPool().invoke(variableRetrievalTask))) {
            final double[] d = data; //having issues with visibility, so make container explicit and final
            vData[counter++] = d;
        }
        DoubleMatrix mat = (DoubleMatrix) factory.doubleTensor(vData[0].length,vData.length);
        TransposedMatrix.transpose(mat).setTensorValues(new DoubleTypeSafeArray(vData));
        return mat;
    }

    @Override
    public CalculationTrace getVariableTrace(String variable, int observation) {
        if (observation < 0 || observation >= getDataLength())
            throw new IllegalArgumentException(String.format("Observation out of bounds for provider of length %d: %d",getDataLength(),observation));
        if (this instanceof CalculationProvider)
            return ((CalculationProvider) this).getCalculation(variable).getVariableTrace(this,observation);
        else
            return new ConstantTrace(variable,getVariableData(variable)[observation]);
    }
}
