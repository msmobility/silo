package com.pb.sawdust.tensor.alias.scalar;

import com.pb.sawdust.tensor.decorators.size.D0TensorTest;
import com.pb.sawdust.tensor.Tensor;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * @author crf <br/>
 *         Started: Jul 5, 2009 2:10:19 PM
 */
public abstract class ScalarTest<T> extends D0TensorTest<T> {
    @Test
    public void testIteratorType() {
        for (Tensor<T> t : tensor)
            assertTrue(t instanceof Scalar);
    }
}
