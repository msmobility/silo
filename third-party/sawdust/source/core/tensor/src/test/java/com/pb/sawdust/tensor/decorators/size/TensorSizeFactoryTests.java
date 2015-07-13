package com.pb.sawdust.tensor.decorators.size;

import com.pb.sawdust.tensor.TensorFactoryTests;
import com.pb.sawdust.tensor.factory.TensorFactoryTest;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code SizePackageFactoryTests} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 6:58:40 AM
 */
public class TensorSizeFactoryTests {
    public static List<Class<? extends TestBase>> TEST_CLASSES = new LinkedList<Class<? extends TestBase>>();
    static {
        TEST_CLASSES.add(TensorFactoryD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryD9TensorTest.class);
    }
    
    public static class TensorFactoryD0TensorTest extends D0TensorTest<Double> {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected D0Tensor<Double> getTensor(TypeSafeArray<Double> data) {
            D0Tensor<Double> tensor = (D0Tensor<Double>) factory.<Double>tensor();
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

    public static class TensorFactoryD1TensorTest extends D1TensorTest<Double> {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected D1Tensor<Double> getTensor(TypeSafeArray<Double> data) {
            D1Tensor<Double> tensor = (D1Tensor<Double>) factory.<Double>tensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected TypeSafeArray<Double> getData() {
            return TensorFactoryTests.doubleObjectData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected JavaType getJavaType() {
            return JavaType.OBJECT;
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }
    }

    public static class TensorFactoryD2TensorTest extends D2TensorTest<Double> {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected D2Tensor<Double> getTensor(TypeSafeArray<Double> data) {
            D2Tensor<Double> tensor = (D2Tensor<Double>) factory.<Double>tensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected TypeSafeArray<Double> getData() {
            return TensorFactoryTests.doubleObjectData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected JavaType getJavaType() {
            return JavaType.OBJECT;
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }
    }

    public static class TensorFactoryD3TensorTest extends D3TensorTest<Double> {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected D3Tensor<Double> getTensor(TypeSafeArray<Double> data) {
            D3Tensor<Double> tensor = (D3Tensor<Double>) factory.<Double>tensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected TypeSafeArray<Double> getData() {
            return TensorFactoryTests.doubleObjectData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected JavaType getJavaType() {
            return JavaType.OBJECT;
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }
    }

    public static class TensorFactoryD4TensorTest extends D4TensorTest<Double> {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected D4Tensor<Double> getTensor(TypeSafeArray<Double> data) {
            D4Tensor<Double> tensor = (D4Tensor<Double>) factory.<Double>tensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected TypeSafeArray<Double> getData() {
            return TensorFactoryTests.doubleObjectData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected JavaType getJavaType() {
            return JavaType.OBJECT;
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }
    }

    public static class TensorFactoryD5TensorTest extends D5TensorTest<Double> {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected D5Tensor<Double> getTensor(TypeSafeArray<Double> data) {
            D5Tensor<Double> tensor = (D5Tensor<Double>) factory.<Double>tensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected TypeSafeArray<Double> getData() {
            return TensorFactoryTests.doubleObjectData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected JavaType getJavaType() {
            return JavaType.OBJECT;
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }
    }

    public static class TensorFactoryD6TensorTest extends D6TensorTest<Double> {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected D6Tensor<Double> getTensor(TypeSafeArray<Double> data) {
            D6Tensor<Double> tensor = (D6Tensor<Double>) factory.<Double>tensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected TypeSafeArray<Double> getData() {
            return TensorFactoryTests.doubleObjectData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected JavaType getJavaType() {
            return JavaType.OBJECT;
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }
    }

    public static class TensorFactoryD7TensorTest extends D7TensorTest<Double> {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected D7Tensor<Double> getTensor(TypeSafeArray<Double> data) {
            D7Tensor<Double> tensor = (D7Tensor<Double>) factory.<Double>tensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected TypeSafeArray<Double> getData() {
            return TensorFactoryTests.doubleObjectData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected JavaType getJavaType() {
            return JavaType.OBJECT;
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }
    }

    public static class TensorFactoryD8TensorTest extends D8TensorTest<Double> {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(), TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected D8Tensor<Double> getTensor(TypeSafeArray<Double> data) {
            D8Tensor<Double> tensor = (D8Tensor<Double>) factory.<Double>tensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected TypeSafeArray<Double> getData() {
            return TensorFactoryTests.doubleObjectData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected JavaType getJavaType() {
            return JavaType.OBJECT;
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }
    }

    public static class TensorFactoryD9TensorTest extends D9TensorTest<Double> {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected D9Tensor<Double> getTensor(TypeSafeArray<Double> data) {
            D9Tensor<Double> tensor = (D9Tensor<Double>) factory.<Double>tensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected TypeSafeArray<Double> getData() {
            return TensorFactoryTests.doubleObjectData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected JavaType getJavaType() {
            return JavaType.OBJECT;
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }
    }
}
