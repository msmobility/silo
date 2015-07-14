package com.pb.sawdust.tensor.alias.vector.id;

import com.pb.sawdust.tensor.decorators.id.primitive.size.IdDoubleD1TensorTest;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.alias.scalar.id.IdDoubleScalar;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * @author crf <br/>
 *         Started: Jul 24, 2009 5:05:20 PM
 */
public abstract class IdDoubleVectorTest<I> extends IdDoubleD1TensorTest<I> {
    @Test
    public void testIteratorType() {
        for (Tensor<Double> t : tensor)
            assertTrue(t instanceof IdDoubleScalar);
    }
}
