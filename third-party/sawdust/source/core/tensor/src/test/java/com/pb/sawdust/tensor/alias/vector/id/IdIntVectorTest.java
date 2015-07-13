package com.pb.sawdust.tensor.alias.vector.id;

import com.pb.sawdust.tensor.decorators.id.primitive.size.IdIntD1TensorTest;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.alias.scalar.id.IdIntScalar;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * @author crf <br/>
 *         Started: Jul 24, 2009 5:05:20 PM
 */
public abstract class IdIntVectorTest<I> extends IdIntD1TensorTest<I> {
    @Test
    public void testIteratorType() {
        for (Tensor<Integer> t : tensor)
            assertTrue(t instanceof IdIntScalar);
    }
}
