package com.pb.sawdust.tensor.alias.scalar.id;

import com.pb.sawdust.tensor.decorators.id.size.IdD0TensorTest;
import com.pb.sawdust.tensor.Tensor;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * @author crf <br/>
 *         Started: Jul 24, 2009 4:25:26 PM
 */
public abstract class IdScalarTest<T,I> extends IdD0TensorTest<T,I> {
    @Test
    public void testIteratorType() {
        for (Tensor<T> t : tensor)
            assertTrue(t instanceof IdScalar);
    }
}
