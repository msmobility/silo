package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * The {@code SimpleDataProvider} class is a basic, quasi-immutable data provider. It is quasi-immutable because its
 * internal structure (data length, number and name of variables) cannot be altered after construction, but the variable
 * values may change.
 *
 * @author crf <br/>
 *         Jul 27, 2010 8:30:55 AM
 */
public class SimpleDataProvider extends MapDataProvider {

    private static int getDataLength(Map<String,double[]> data) {
        if (data.size() == 0)
            throw new IllegalArgumentException("Simple data provider must be constructed with at least one variable.");
        int length = -1;
        for (String variable : data.keySet()) {
            double[] d = data.get(variable);
            if (length == -1) {
                if ((length = d.length) == 0)
                    throw new IllegalArgumentException("Data provider cannot provide empty (zero-length) data.");
            } else if (length != d.length) {
                throw new IllegalArgumentException("Data lengths must all be equal (" + length + " + vs " + d.length + ").");
            }
        }
        return length;
    }

    /**
     * Constructor specifying the data identifier, variable data and tensor factory used to build data results. All variable
     * data arrays (the data map's values} must have equal lengths, which will be the length of the provider. The data arrays
     * are used directly to store the data internally, so changes to the arrays outside of this class will affect the data
     * values returned by the provider.
     *
     * @param dataId
     *        The data id to use for this provider.
     *
     * @param data
     *        The variable data.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @throws IllegalArgumentException if {@code data} is empty, if the lengths of all of the data arrays in {@code data}
     *                                  are not equal, if the lengths of the data arrays are zero, or if {@code dataId} has
     *                                  not already been allocated via {@code AbstractIdData}.
     */
    public SimpleDataProvider(int dataId, Map<String,double[]> data, TensorFactory factory) {
        super(dataId,factory,getDataLength(data),data);
    }

    /**
     * Constructor specifying the variable data and tensor factory used to build data results. All variable data arrays
     * (the data map's values} must have equal lengths, which will be the length of the provider. The data arrays are used
     * directly to store the data internally, so changes to the arrays outside of this class will affect the data values
     * returned by the provider.
     *
     * @param data
     *        The variable data.
     *
     * @param factory
     *        The tensor factory used to build data results.
     *
     * @throws IllegalArgumentException if {@code data} is empty, if the lengths of all of the data arrays in {@code data}
     *                                  are not equal, or if the lengths of the data arrays are zero.
     */
    public SimpleDataProvider(Map<String,double[]> data, TensorFactory factory) {
        super(factory,getDataLength(data),data);
    }

    @Override
    Map<String, double[]> getDataMap(Map<String, double[]> initialData) {
        return Collections.unmodifiableMap(new HashMap<String,double[]>(initialData));
    }
}
