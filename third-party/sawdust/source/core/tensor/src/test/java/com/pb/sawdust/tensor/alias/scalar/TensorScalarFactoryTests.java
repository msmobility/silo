package com.pb.sawdust.tensor.alias.scalar;

import com.pb.sawdust.tensor.TensorFactoryTests;
import com.pb.sawdust.tensor.factory.TensorFactoryTest;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code ScalarPackageFactoryTests} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 10:29:59 AM
 */
public class TensorScalarFactoryTests {
    public static List<Class<? extends TestBase>> TEST_CLASSES = new LinkedList<Class<? extends TestBase>>();
    static {
        TEST_CLASSES.add(TensorFactoryScalarTest.class);
    }
    
    public static class TensorFactoryScalarTest extends ScalarTest<Double> {
        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(), TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        @Override
        protected Scalar<Double> getTensor(TypeSafeArray<Double> data) {
           Scalar<Double> tensor = (Scalar<Double>) factory.<Double>tensor();
           tensor.setTensorValues(data);
           return tensor;
        }

        protected TypeSafeArray<Double> getData() {
            return TensorFactoryTests.doubleObjectData(new int[] {1},random);
        }

        protected JavaType getJavaType() {
            return JavaType.OBJECT;
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }
     }
}
