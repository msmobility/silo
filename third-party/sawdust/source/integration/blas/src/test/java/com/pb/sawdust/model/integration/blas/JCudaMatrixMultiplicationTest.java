package com.pb.sawdust.model.integration.blas;

import com.pb.sawdust.calculator.tensor.la.mm.AbstractMatrixMultiplication;
import com.pb.sawdust.calculator.tensor.la.mm.AbstractMatrixMultiplicationTest;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.test.TestBase;

/**
 * The {@code JCudaMatrixMultiplicationTest} ...
 *
 * @author crf <br/>
 *         Started Dec 13, 2010 8:52:05 AM
 */
public class JCudaMatrixMultiplicationTest extends AbstractMatrixMultiplicationTest {

    public static void main(String ... args) {
        if (JCuda.isJCublasAvailable())
            TestBase.main();
        else
            System.out.println(" - (J)Cuda not available/installed; " + JCudaMatrixMultiplicationTest.class.getCanonicalName() + " tests will be skipped.");
    }

    @Override
    protected AbstractMatrixMultiplication getAbstractMatrixMultiplication(TensorFactory factory) {
        return new JCudaMatrixMultiplication(factory);
    }
}
