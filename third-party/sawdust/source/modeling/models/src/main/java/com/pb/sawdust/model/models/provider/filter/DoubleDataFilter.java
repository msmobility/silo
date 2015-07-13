package com.pb.sawdust.model.models.provider.filter;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.factory.TensorFactory;

/**
 * The {@code DoubleDataFilter} ...
 *
 * @author crf <br/>
 *         Started 3/3/11 10:39 PM
 */
public abstract class DoubleDataFilter extends CachedDataFilter {
    private final TensorFactory factory;

    public DoubleDataFilter(TensorFactory factory) {
        this.factory = factory;
    }

    protected abstract double[] getDoubleFilter(DataProvider provider);

    @Override
    protected BooleanVector getFilterUncached(DataProvider provider) {
        return getFilter(factory,getDoubleFilter(provider));
    }

    public static BooleanVector getFilter(TensorFactory factory, final double[] inputs) {
        BooleanVector filter = factory.booleanVector(inputs.length);
        TensorUtil.fill(filter,new TensorUtil.BooleanTensorValueFunction() {
            @Override
            public boolean getValue(int ... indices) {
                return inputs[indices[0]] != 0.0;
            }
        });
        return filter;
    }
}
