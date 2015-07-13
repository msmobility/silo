package com.pb.sawdust.tensor.alias.matrix.primitive;

import com.pb.sawdust.tensor.decorators.primitive.size.LongD2TensorTest;
import com.pb.sawdust.tensor.alias.vector.primitive.LongVector;
import com.pb.sawdust.tensor.Tensor;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author crf <br/>
 *         Started: Jul 5, 2009 9:19:38 AM
 */
public abstract class LongMatrixTest extends LongD2TensorTest {    
    @Test
    public void testIteratorType() {
        for (Tensor<Long> t : tensor)
            assertTrue(t instanceof LongVector);
    }
}
