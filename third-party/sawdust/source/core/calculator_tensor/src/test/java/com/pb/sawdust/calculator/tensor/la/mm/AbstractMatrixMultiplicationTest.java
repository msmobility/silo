package com.pb.sawdust.calculator.tensor.la.mm;

import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code AbstractMatrixMultiplicationTest} ...
 *
 * @author crf <br/>
 *         Started Dec 13, 2010 8:49:39 AM
 */
public abstract class AbstractMatrixMultiplicationTest extends TestBase {
    protected AbstractMatrixMultiplication abstractMm;
    protected TensorFactory factory;

    protected abstract AbstractMatrixMultiplication getAbstractMatrixMultiplication(TensorFactory factory);

    protected TensorFactory getFactory() {
        return ArrayTensor.getFactory();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Class<? extends TestBase>> additionalTestClasses = new LinkedList<Class<? extends TestBase>>();
        additionalTestClasses.addAll(super.getAdditionalTestClasses());
        additionalTestClasses.add(getVectorVectorMultiplicationTestClass());
        additionalTestClasses.add(getMatrixVectorMultiplicationTestClass());
        additionalTestClasses.add(getMatrixMatrixMultiplicationTestClass());
        return additionalTestClasses;
    }

    protected Class<? extends VectorVectorMultiplicationTest> getVectorVectorMultiplicationTestClass() {
        return AbstractMatrixMultiplicationTestVV.class;
    }
    protected Class<? extends MatrixVectorMultiplicationTest> getMatrixVectorMultiplicationTestClass() {
        return AbstractMatrixMultiplicationTestMV.class;
    }

    protected  Class<? extends MatrixMatrixMultiplicationTest> getMatrixMatrixMultiplicationTestClass() {
        return AbstractMatrixMultiplicationTestMM.class;
    }

    @Before
    public void beforeTest() {
        factory = getFactory();
        abstractMm = getAbstractMatrixMultiplication(factory);
    }

    public static class AbstractMatrixMultiplicationTestVV extends VectorVectorMultiplicationTest {
        protected AbstractMatrixMultiplicationTest mmt;

        @Before
        public void beforeTest() {
            mmt = (AbstractMatrixMultiplicationTest) getCallingContextInstance();
            super.beforeTest();
        }

        @Override
        protected TensorFactory getFactory() {
            return mmt.getFactory();
        }

        @Override
        protected VectorVectorMultiplication getVectorVectorMultiplication(TensorFactory factory) {
            return mmt.getAbstractMatrixMultiplication(factory);
        }
    }

    public static class AbstractMatrixMultiplicationTestMV extends MatrixVectorMultiplicationTest {
        protected AbstractMatrixMultiplicationTest mmt;

        @Before
        public void beforeTest() {
            mmt = (AbstractMatrixMultiplicationTest) getCallingContextInstance();
            super.beforeTest();
        }

        @Override
        protected TensorFactory getFactory() {
            return mmt.getFactory();
        }

        @Override
        protected MatrixVectorMultiplication getMatrixVectorMultiplication(TensorFactory factory) {
            return mmt.getAbstractMatrixMultiplication(factory);
        }
    }

    public static class AbstractMatrixMultiplicationTestMM extends MatrixMatrixMultiplicationTest {
        protected AbstractMatrixMultiplicationTest mmt;

        @Before
        public void beforeTest() {
            mmt = (AbstractMatrixMultiplicationTest) getCallingContextInstance();
            super.beforeTest();
        }

        @Override
        protected TensorFactory getFactory() {
            return mmt.getFactory();
        }

        @Override
        protected MatrixMatrixMultiplication getMatrixMatrixMultiplication(TensorFactory factory) {
            return mmt.getAbstractMatrixMultiplication(factory);
        }
    }

    @Test
    public void testStub() {
        //todo: test methods in AbstractMM
    }
}
