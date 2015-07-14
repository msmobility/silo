package com.pb.sawdust.model.integration.blas;

import com.pb.sawdust.calculator.tensor.la.mm.AbstractMatrixMultiplication;
import com.pb.sawdust.calculator.tensor.la.mm.AbstractMatrixMultiplicationTest;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.test.TestBase;

/**
 * The {@code JBlasMatrixMultiplicationTest} ...
 *
 * @author crf <br/>
 *         Started 1/11/11 10:45 AM
 */
public class JBlasMatrixMultiplicationTest extends AbstractMatrixMultiplicationTest {

    public static void main(String ... args) {
        if (JBlas.isJBlasAvailable())
            TestBase.main();
        else
            System.out.println(" - JBlas not available; " + JBlasMatrixMultiplicationTest.class.getCanonicalName() + " tests will be skipped.");
    }

    @Override
    protected AbstractMatrixMultiplication getAbstractMatrixMultiplication(TensorFactory factory) {
        return new JBlasMatrixMultiplication(factory);
    }
}
