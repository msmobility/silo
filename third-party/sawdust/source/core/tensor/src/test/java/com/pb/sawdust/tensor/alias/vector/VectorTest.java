package com.pb.sawdust.tensor.alias.vector;

import org.junit.Test;
import com.pb.sawdust.tensor.decorators.size.D1TensorTest;
import com.pb.sawdust.tensor.alias.scalar.Scalar;
import com.pb.sawdust.tensor.Tensor;
import static org.junit.Assert.*;

/**
 * @author crf <br/>
 *         Started: Jul 5, 2009 10:38:55 AM
 */
public abstract class VectorTest<T> extends D1TensorTest<T> {
    @Test
    public void testIteratorType() {
        for (Tensor<T> t : tensor)
            assertTrue(t instanceof Scalar);
    }
}
