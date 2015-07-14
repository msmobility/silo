package com.pb.sawdust.tensor.alias.matrix.id;

import com.pb.sawdust.tensor.decorators.id.size.IdD2TensorTest;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.alias.vector.id.IdVector;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * @author crf <br/>
 *         Started: Jul 24, 2009 4:25:26 PM
 */
public abstract class IdMatrixTest<T,I> extends IdD2TensorTest<T,I> {
    @Test
    public void testIteratorType() {
        for (Tensor<T> t : tensor)
            assertTrue(t instanceof IdVector);
    }
}
