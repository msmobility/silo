package com.pb.sawdust.tensor.decorators.id.size;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorFactoryTests;
import com.pb.sawdust.tensor.decorators.id.TensorIdFactoryTests;
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
 * The {@code IdSizePackageFactoryTests} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 8:21:37 AM
 */
public class TensorIdSizeFactoryTests {
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



    public static class TensorFactoryD0TensorTest extends IdD0TensorTest<Double,String> {
        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected Tensor<Double> getTensor(TypeSafeArray<Double> data) {
            ids = getIds();
            return getIdTensor(data,ids);
        }

        protected IdD0Tensor<Double,String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdD0Tensor<Double,String> tensor = (IdD0Tensor<Double,String>) factory.<Double,String>tensor(ids);
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

    public static class TensorFactoryD1TensorTest extends IdD1TensorTest<Double,String> {
        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected IdD1Tensor<Double,String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdD1Tensor<Double,String> tensor = (IdD1Tensor<Double,String>) factory.<Double,String>tensor(ids, ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryD2TensorTest extends IdD2TensorTest<Double,String> {
        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(), TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected IdD2Tensor<Double,String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdD2Tensor<Double,String> tensor = (IdD2Tensor<Double,String>) factory.<Double,String>tensor(ids,ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryD3TensorTest extends IdD3TensorTest<Double,String> {
        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected IdD3Tensor<Double,String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdD3Tensor<Double,String> tensor = (IdD3Tensor<Double,String>) factory.<Double,String>tensor(ids,ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryD4TensorTest extends IdD4TensorTest<Double,String> {
        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected IdD4Tensor<Double,String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdD4Tensor<Double,String> tensor = (IdD4Tensor<Double,String>) factory.<Double,String>tensor(ids,ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryD5TensorTest extends IdD5TensorTest<Double,String> {
        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected IdD5Tensor<Double,String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdD5Tensor<Double,String> tensor = (IdD5Tensor<Double,String>) factory.<Double,String>tensor(ids,ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryD6TensorTest extends IdD6TensorTest<Double,String> {
        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected IdD6Tensor<Double,String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdD6Tensor<Double,String> tensor = (IdD6Tensor<Double,String>) factory.<Double,String>tensor(ids,ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryD7TensorTest extends IdD7TensorTest<Double,String> {
        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected IdD7Tensor<Double,String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdD7Tensor<Double,String> tensor = (IdD7Tensor<Double,String>) factory.<Double,String>tensor(ids,ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryD8TensorTest extends IdD8TensorTest<Double,String> {
        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected IdD8Tensor<Double,String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdD8Tensor<Double,String> tensor = (IdD8Tensor<Double,String>) factory.<Double,String>tensor(ids,ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryD9TensorTest extends IdD9TensorTest<Double,String> {
        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(), TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected IdD9Tensor<Double,String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdD9Tensor<Double,String> tensor = (IdD9Tensor<Double,String>) factory.<Double,String>tensor(ids,ArrayUtil.getDimensions(data.getArray()));
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
