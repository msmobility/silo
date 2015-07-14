package com.pb.sawdust.tensor.alias.vector.primitive;

import com.pb.sawdust.tensor.decorators.primitive.size.FloatD1TensorTest;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.alias.scalar.primitive.FloatScalar;
import org.junit.Test;                                                      
import static org.junit.Assert.*;

/**
 * @author crf <br/>
 *         Started: Jul 5, 2009 9:19:38 AM
 */
public abstract class FloatVectorTest extends FloatD1TensorTest {
    @Test
    public void testIteratorType() {
        for (Tensor<Float> t : tensor)
            assertTrue(t instanceof FloatScalar);
    }
}
