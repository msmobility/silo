package com.pb.sawdust.tensor.alias.vector.id;

import com.pb.sawdust.tensor.decorators.id.primitive.size.IdCharD1TensorTest;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.alias.scalar.id.IdCharScalar;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * @author crf <br/>
 *         Started: Jul 24, 2009 5:05:20 PM
 */
public abstract class IdCharVectorTest<I> extends IdCharD1TensorTest<I> {
    @Test
    public void testIteratorType() {
        for (Tensor<Character> t : tensor)
            assertTrue(t instanceof IdCharScalar);
    }
}
