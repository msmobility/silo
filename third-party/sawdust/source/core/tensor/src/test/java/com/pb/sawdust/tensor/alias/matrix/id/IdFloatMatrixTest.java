package com.pb.sawdust.tensor.alias.matrix.id;

import com.pb.sawdust.tensor.decorators.id.primitive.size.IdFloatD2TensorTest;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.alias.vector.id.IdFloatVector;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * @author crf <br/>
 *         Started: Jul 24, 2009 5:05:20 PM
 */
public abstract class IdFloatMatrixTest<I> extends IdFloatD2TensorTest<I> {
    @Test
    public void testIteratorType() {
        for (Tensor<Float> t : tensor)
            assertTrue(t instanceof IdFloatVector);
    }
}
