package com.pb.sawdust.tensor.factory;

import com.pb.sawdust.tensor.TensorFactoryTests;
import com.pb.sawdust.tensor.alias.matrix.TensorMatrixFactoryTests;
import com.pb.sawdust.tensor.alias.matrix.id.TensorMatrixIdFactoryTests;
import com.pb.sawdust.tensor.alias.matrix.primitive.TensorMatrixPrimitiveFactoryTests;
import com.pb.sawdust.tensor.alias.scalar.TensorScalarFactoryTests;
import com.pb.sawdust.tensor.alias.scalar.id.TensorScalarIdFactoryTests;
import com.pb.sawdust.tensor.alias.scalar.primitive.TensorScalarPrimitiveFactoryTests;
import com.pb.sawdust.tensor.alias.vector.TensorVectorFactoryTests;
import com.pb.sawdust.tensor.alias.vector.id.TensorVectorIdFactoryTests;
import com.pb.sawdust.tensor.alias.vector.primitive.TensorVectorPrimitiveFactoryTests;
import com.pb.sawdust.tensor.decorators.id.TensorIdFactoryTests;
import com.pb.sawdust.tensor.decorators.id.primitive.TensorIdPrimitiveFactoryTests;
import com.pb.sawdust.tensor.decorators.id.primitive.size.TensorIdPrimitiveSizeFactoryTests;
import com.pb.sawdust.tensor.decorators.id.size.TensorIdSizeFactoryTests;
import com.pb.sawdust.tensor.decorators.primitive.TensorPrimitiveFactoryTests;
import com.pb.sawdust.tensor.decorators.primitive.size.TensorPrimitiveSizeFactoryTests;
import com.pb.sawdust.tensor.decorators.size.TensorSizeFactoryTests;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code TensorFactoryTest} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 6:16:06 AM
 */
public abstract class TensorFactoryTest extends TestBase {
    public static final String TENSOR_FACTORY_KEY = "tensor factory";

    abstract protected TensorFactory getFactory();

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        addTestData(TENSOR_FACTORY_KEY,getFactory());
        List<Class<? extends TestBase>> adds = new LinkedList<Class<? extends TestBase>>();
        adds.addAll(TensorFactoryTests.TEST_CLASSES);
        adds.addAll(TensorSizeFactoryTests.TEST_CLASSES);
        adds.addAll(TensorPrimitiveFactoryTests.TEST_CLASSES);
        adds.addAll(TensorPrimitiveSizeFactoryTests.TEST_CLASSES);
        adds.addAll(TensorIdFactoryTests.TEST_CLASSES);
        adds.addAll(TensorIdSizeFactoryTests.TEST_CLASSES);
        adds.addAll(TensorIdPrimitiveFactoryTests.TEST_CLASSES);
        adds.addAll(TensorIdPrimitiveSizeFactoryTests.TEST_CLASSES);
        adds.addAll(TensorScalarFactoryTests.TEST_CLASSES);
        adds.addAll(TensorScalarPrimitiveFactoryTests.TEST_CLASSES);
        adds.addAll(TensorScalarIdFactoryTests.TEST_CLASSES);
        adds.addAll(TensorVectorFactoryTests.TEST_CLASSES);
        adds.addAll(TensorVectorPrimitiveFactoryTests.TEST_CLASSES);
        adds.addAll(TensorVectorIdFactoryTests.TEST_CLASSES);
        adds.addAll(TensorMatrixFactoryTests.TEST_CLASSES);
        adds.addAll(TensorMatrixPrimitiveFactoryTests.TEST_CLASSES);
        adds.addAll(TensorMatrixIdFactoryTests.TEST_CLASSES);
        return adds;
    }

    @Test
    public void placeholderTest() { }
}
