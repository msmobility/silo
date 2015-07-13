package com.pb.sawdust.tensor;

import com.pb.sawdust.tensor.factory.ConcurrentTensorFactory;
import com.pb.sawdust.tensor.factory.ConcurrentTensorFactoryTest;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.factory.TensorFactoryTest;
import com.pb.sawdust.util.test.TestBase;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code SparseTensorTest} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 11:42:39 AM
 */
public class SparseTensorTest extends TensorFactoryTest {

    public static void main(String ... args) {
        TestBase.main();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Class<? extends TestBase>> adds = new LinkedList<Class<? extends TestBase>>();
        adds.addAll(super.getAdditionalTestClasses());
        adds.add(ConcurrentSparseTensorTest.class);
        return adds;
    }

    protected TensorFactory getFactory() {
        return SparseTensor.getFactory();
    }

    public static class ConcurrentSparseTensorTest extends ConcurrentTensorFactoryTest {

        @Override
        protected ConcurrentTensorFactory getFactory() {
        return SparseTensor.getFactory();
        }
    }
}
