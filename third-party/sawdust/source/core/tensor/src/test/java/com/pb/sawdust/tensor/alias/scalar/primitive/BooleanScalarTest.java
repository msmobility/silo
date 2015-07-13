package com.pb.sawdust.tensor.alias.scalar.primitive;

import com.pb.sawdust.tensor.decorators.primitive.size.BooleanD0TensorTest;
import com.pb.sawdust.tensor.Tensor;
import org.junit.Test;                                                      
import static org.junit.Assert.*;

/**
 * @author crf <br/>
 *         Started: Jul 5, 2009 9:19:38 AM
 */
public abstract class BooleanScalarTest extends BooleanD0TensorTest {
    @Test
    public void testIteratorType() {
        for (Tensor<Boolean> t : tensor)
            assertTrue(t instanceof BooleanScalar);
    }
}
