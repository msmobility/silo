package com.pb.sawdust.tensor;

import com.pb.sawdust.tensor.factory.TensorFactory;

/**
 * @author crf <br/>
 *         Started: Aug 2, 2009 1:46:10 PM
 */
@Deprecated
public class TensorFactoryProvider { //todo: replace calls to this with dependency injection pattern   
    private static TensorFactory factory = LinearTensor.getFactory();

    public static TensorFactory getFactory() {
        return factory;
    }

    public static void setFactory(TensorFactory factory) {
        TensorFactoryProvider.factory = factory;
    }
    
}
