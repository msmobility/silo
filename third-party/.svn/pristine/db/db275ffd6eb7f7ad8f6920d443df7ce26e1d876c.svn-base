package com.pb.sawdust.tensor.alias.vector.primitive;

import com.pb.sawdust.tensor.decorators.primitive.size.CharD1TensorTest;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.alias.scalar.primitive.CharScalar;
import org.junit.Test;                                                      
import static org.junit.Assert.*;

/**
 * @author crf <br/>
 *         Started: Jul 5, 2009 9:19:38 AM
 */
public abstract class CharVectorTest extends CharD1TensorTest {
    @Test
    public void testIteratorType() {
        for (Tensor<Character> t : tensor)
            assertTrue(t instanceof CharScalar);
    }
}
