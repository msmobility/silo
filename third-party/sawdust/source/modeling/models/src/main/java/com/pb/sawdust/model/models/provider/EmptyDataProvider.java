package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code EmptyDataProvider} is a data provider which holds no data. It can be useful in cases where a placeholder
 * data provider is required.
 *
 * @author crf
 *         Started 4/9/12 11:54 AM
 */
public class EmptyDataProvider extends MapDataProvider {

    /**
     * Constructor specifying the data source identifier, the provider length, and tensor factory used to build data results.
     * This constructor should only be called if the data provider is equivalent to another (already constructed) provider,
     * and that the equivalence needs to be recognized through the data identifier.
     *
     * @param dataId
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
    public EmptyDataProvider(int dataId, int dataLength, TensorFactory factory) {
        super(dataId,factory,dataLength,new HashMap<String,double[]>());
    }

    /**
     * Constructor specifying the data source identifier and the tensor factory used to build data results.
     *
     * @param dataLength
     *        The length of the data provider.
     *
     * @param factory
     *        The tensor factory used to build data results.
     */
    public EmptyDataProvider(int dataLength, TensorFactory factory) {
        super(factory,dataLength,new HashMap<String,double[]>());
    }

    @Override
    Map<String,double[]> getDataMap(Map<String,double[]> initialData) {
        return new HashMap<>(initialData);
    }
}
