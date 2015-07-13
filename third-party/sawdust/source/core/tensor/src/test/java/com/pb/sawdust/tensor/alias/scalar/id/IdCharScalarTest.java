package com.pb.sawdust.tensor.alias.scalar.id;

import com.pb.sawdust.tensor.decorators.id.primitive.size.IdCharD0TensorTest;
import com.pb.sawdust.tensor.Tensor;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * @author crf <br/>
 *         Started: Jul 24, 2009 5:05:20 PM
 */
public abstract class IdCharScalarTest<I> extends IdCharD0TensorTest<I> {
    @Test
    public void testIteratorType() {
        for (Tensor<Character> t : tensor)
            assertTrue(t instanceof IdCharScalar);
    }
}
