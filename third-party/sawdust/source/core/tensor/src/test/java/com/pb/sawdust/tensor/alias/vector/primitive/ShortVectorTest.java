package com.pb.sawdust.tensor.alias.vector.primitive;

import com.pb.sawdust.tensor.decorators.primitive.size.ShortD1TensorTest;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.alias.scalar.primitive.ShortScalar;
import org.junit.Test;                                                      
import static org.junit.Assert.*;

/**
 * @author crf <br/>
 *         Started: Jul 5, 2009 9:19:38 AM
 */
public abstract class ShortVectorTest extends ShortD1TensorTest {
    @Test
    public void testIteratorType() {
        for (Tensor<Short> t : tensor)
            assertTrue(t instanceof ShortScalar);
    }
}
