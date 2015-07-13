package com.pb.sawdust.tensor.decorators.id.primitive.size;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorFactoryTests;
import com.pb.sawdust.tensor.decorators.id.TensorIdFactoryTests;
import com.pb.sawdust.tensor.factory.TensorFactoryTest;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.array.*;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code IdPrimitiveSizePackageFactoryTests} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 10:17:02 AM
 */
public class TensorIdPrimitiveSizeFactoryTests {
    public static List<Class<? extends TestBase>> TEST_CLASSES = new LinkedList<Class<? extends TestBase>>();

    static {
        TEST_CLASSES.add(TensorFactoryIdBooleanD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdBooleanD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdBooleanD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdBooleanD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdBooleanD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdBooleanD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdBooleanD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdBooleanD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdBooleanD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdBooleanD9TensorTest.class);

        TEST_CLASSES.add(TensorFactoryIdCharD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdCharD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdCharD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdCharD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdCharD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdCharD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdCharD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdCharD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdCharD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdCharD9TensorTest.class);

        TEST_CLASSES.add(TensorFactoryIdByteD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdByteD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdByteD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdByteD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdByteD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdByteD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdByteD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdByteD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdByteD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdByteD9TensorTest.class);

        TEST_CLASSES.add(TensorFactoryIdShortD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdShortD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdShortD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdShortD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdShortD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdShortD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdShortD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdShortD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdShortD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdShortD9TensorTest.class);

        TEST_CLASSES.add(TensorFactoryIdIntD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdIntD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdIntD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdIntD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdIntD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdIntD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdIntD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdIntD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdIntD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdIntD9TensorTest.class);

        TEST_CLASSES.add(TensorFactoryIdLongD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdLongD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdLongD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdLongD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdLongD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdLongD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdLongD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdLongD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdLongD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdLongD9TensorTest.class);

        TEST_CLASSES.add(TensorFactoryIdFloatD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdFloatD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdFloatD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdFloatD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdFloatD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdFloatD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdFloatD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdFloatD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdFloatD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdFloatD9TensorTest.class);

        TEST_CLASSES.add(TensorFactoryIdDoubleD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdDoubleD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdDoubleD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdDoubleD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdDoubleD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdDoubleD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdDoubleD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdDoubleD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdDoubleD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIdDoubleD9TensorTest.class);
    }
    


    public static class TensorFactoryIdBooleanD0TensorTest extends IdBooleanD0TensorTest<String> {

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

        protected Tensor<Boolean> getTensor(TypeSafeArray<Boolean> data) {
            ids = getIds();
            return getIdTensor(data,ids);
        }

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(new int[] {1},random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected IdBooleanD0Tensor<String> getIdTensor(TypeSafeArray<Boolean> data, List<List<String>> ids) {
            IdBooleanD0Tensor<String> tensor = (IdBooleanD0Tensor<String>) factory.booleanTensor(ids);
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdBooleanD1TensorTest extends IdBooleanD1TensorTest<String> {

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

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected IdBooleanD1Tensor<String> getIdTensor(TypeSafeArray<Boolean> data, List<List<String>> ids) {
            IdBooleanD1Tensor<String> tensor = (IdBooleanD1Tensor<String>) factory.booleanTensor(ids, ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdBooleanD2TensorTest extends IdBooleanD2TensorTest<String> {

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

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected IdBooleanD2Tensor<String> getIdTensor(TypeSafeArray<Boolean> data, List<List<String>> ids) {
            IdBooleanD2Tensor<String> tensor = (IdBooleanD2Tensor<String>) factory.booleanTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdBooleanD3TensorTest extends IdBooleanD3TensorTest<String> {

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

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected IdBooleanD3Tensor<String> getIdTensor(TypeSafeArray<Boolean> data, List<List<String>> ids) {
            IdBooleanD3Tensor<String> tensor = (IdBooleanD3Tensor<String>) factory.booleanTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdBooleanD4TensorTest extends IdBooleanD4TensorTest<String> {

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

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected IdBooleanD4Tensor<String> getIdTensor(TypeSafeArray<Boolean> data, List<List<String>> ids) {
            IdBooleanD4Tensor<String> tensor = (IdBooleanD4Tensor<String>) factory.booleanTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdBooleanD5TensorTest extends IdBooleanD5TensorTest<String> {

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

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected IdBooleanD5Tensor<String> getIdTensor(TypeSafeArray<Boolean> data, List<List<String>> ids) {
            IdBooleanD5Tensor<String> tensor = (IdBooleanD5Tensor<String>) factory.booleanTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdBooleanD6TensorTest extends IdBooleanD6TensorTest<String> {

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

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected IdBooleanD6Tensor<String> getIdTensor(TypeSafeArray<Boolean> data, List<List<String>> ids) {
            IdBooleanD6Tensor<String> tensor = (IdBooleanD6Tensor<String>) factory.booleanTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdBooleanD7TensorTest extends IdBooleanD7TensorTest<String> {

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

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected IdBooleanD7Tensor<String> getIdTensor(TypeSafeArray<Boolean> data, List<List<String>> ids) {
            IdBooleanD7Tensor<String> tensor = (IdBooleanD7Tensor<String>) factory.booleanTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdBooleanD8TensorTest extends IdBooleanD8TensorTest<String> {

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

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected IdBooleanD8Tensor<String> getIdTensor(TypeSafeArray<Boolean> data, List<List<String>> ids) {
            IdBooleanD8Tensor<String> tensor = (IdBooleanD8Tensor<String>) factory.booleanTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }   

    public static class TensorFactoryIdBooleanD9TensorTest extends IdBooleanD9TensorTest<String> {

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

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected IdBooleanD9Tensor<String> getIdTensor(TypeSafeArray<Boolean> data, List<List<String>> ids) {
            IdBooleanD9Tensor<String> tensor = (IdBooleanD9Tensor<String>) factory.booleanTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }  

    public static class TensorFactoryIdByteD0TensorTest extends IdByteD0TensorTest<String> {

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

        protected Tensor<Byte> getTensor(TypeSafeArray<Byte> data) {
            ids = getIds();
            return getIdTensor(data,ids);
        }

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(new int[] {1},random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected IdByteD0Tensor<String> getIdTensor(TypeSafeArray<Byte> data, List<List<String>> ids) {
            IdByteD0Tensor<String> tensor = (IdByteD0Tensor<String>) factory.byteTensor(ids);
            tensor.setTensorValues(data);
            return tensor;
        }
    }
    
    public static class TensorFactoryIdByteD1TensorTest extends IdByteD1TensorTest<String> {

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

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected IdByteD1Tensor<String> getIdTensor(TypeSafeArray<Byte> data, List<List<String>> ids) {
            IdByteD1Tensor<String> tensor = (IdByteD1Tensor<String>) factory.byteTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdByteD2TensorTest extends IdByteD2TensorTest<String> {

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

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected IdByteD2Tensor<String> getIdTensor(TypeSafeArray<Byte> data, List<List<String>> ids) {
            IdByteD2Tensor<String> tensor = (IdByteD2Tensor<String>) factory.byteTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdByteD3TensorTest extends IdByteD3TensorTest<String> {

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

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected IdByteD3Tensor<String> getIdTensor(TypeSafeArray<Byte> data, List<List<String>> ids) {
            IdByteD3Tensor<String> tensor = (IdByteD3Tensor<String>) factory.byteTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdByteD4TensorTest extends IdByteD4TensorTest<String> {

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

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected IdByteD4Tensor<String> getIdTensor(TypeSafeArray<Byte> data, List<List<String>> ids) {
            IdByteD4Tensor<String> tensor = (IdByteD4Tensor<String>) factory.byteTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdByteD5TensorTest extends IdByteD5TensorTest<String> {

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

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected IdByteD5Tensor<String> getIdTensor(TypeSafeArray<Byte> data, List<List<String>> ids) {
            IdByteD5Tensor<String> tensor = (IdByteD5Tensor<String>) factory.byteTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdByteD6TensorTest extends IdByteD6TensorTest<String> {

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

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected IdByteD6Tensor<String> getIdTensor(TypeSafeArray<Byte> data, List<List<String>> ids) {
            IdByteD6Tensor<String> tensor = (IdByteD6Tensor<String>) factory.byteTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdByteD7TensorTest extends IdByteD7TensorTest<String> {

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

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected IdByteD7Tensor<String> getIdTensor(TypeSafeArray<Byte> data, List<List<String>> ids) {
            IdByteD7Tensor<String> tensor = (IdByteD7Tensor<String>) factory.byteTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdByteD8TensorTest extends IdByteD8TensorTest<String> {

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

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected IdByteD8Tensor<String> getIdTensor(TypeSafeArray<Byte> data, List<List<String>> ids) {
            IdByteD8Tensor<String> tensor = (IdByteD8Tensor<String>) factory.byteTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdByteD9TensorTest extends IdByteD9TensorTest<String> {

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

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected IdByteD9Tensor<String> getIdTensor(TypeSafeArray<Byte> data, List<List<String>> ids) {
            IdByteD9Tensor<String> tensor = (IdByteD9Tensor<String>) factory.byteTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }                   
      

    public static class TensorFactoryIdShortD0TensorTest extends IdShortD0TensorTest<String> {

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

        protected Tensor<Short> getTensor(TypeSafeArray<Short> data) {
            ids = getIds();
            return getIdTensor(data,ids);
        }

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(new int[] {1},random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected IdShortD0Tensor<String> getIdTensor(TypeSafeArray<Short> data, List<List<String>> ids) {
            IdShortD0Tensor<String> tensor = (IdShortD0Tensor<String>) factory.shortTensor(ids);
            tensor.setTensorValues(data);
            return tensor;
        }
    }
    
    public static class TensorFactoryIdShortD1TensorTest extends IdShortD1TensorTest<String> {

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

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected IdShortD1Tensor<String> getIdTensor(TypeSafeArray<Short> data, List<List<String>> ids) {
            IdShortD1Tensor<String> tensor = (IdShortD1Tensor<String>) factory.shortTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdShortD2TensorTest extends IdShortD2TensorTest<String> {

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

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected IdShortD2Tensor<String> getIdTensor(TypeSafeArray<Short> data, List<List<String>> ids) {
            IdShortD2Tensor<String> tensor = (IdShortD2Tensor<String>) factory.shortTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdShortD3TensorTest extends IdShortD3TensorTest<String> {

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

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected IdShortD3Tensor<String> getIdTensor(TypeSafeArray<Short> data, List<List<String>> ids) {
            IdShortD3Tensor<String> tensor = (IdShortD3Tensor<String>) factory.shortTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdShortD4TensorTest extends IdShortD4TensorTest<String> {

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

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected IdShortD4Tensor<String> getIdTensor(TypeSafeArray<Short> data, List<List<String>> ids) {
            IdShortD4Tensor<String> tensor = (IdShortD4Tensor<String>) factory.shortTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdShortD5TensorTest extends IdShortD5TensorTest<String> {

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

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected IdShortD5Tensor<String> getIdTensor(TypeSafeArray<Short> data, List<List<String>> ids) {
            IdShortD5Tensor<String> tensor = (IdShortD5Tensor<String>) factory.shortTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdShortD6TensorTest extends IdShortD6TensorTest<String> {

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

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected IdShortD6Tensor<String> getIdTensor(TypeSafeArray<Short> data, List<List<String>> ids) {
            IdShortD6Tensor<String> tensor = (IdShortD6Tensor<String>) factory.shortTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdShortD7TensorTest extends IdShortD7TensorTest<String> {

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

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected IdShortD7Tensor<String> getIdTensor(TypeSafeArray<Short> data, List<List<String>> ids) {
            IdShortD7Tensor<String> tensor = (IdShortD7Tensor<String>) factory.shortTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdShortD8TensorTest extends IdShortD8TensorTest<String> {

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

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected IdShortD8Tensor<String> getIdTensor(TypeSafeArray<Short> data, List<List<String>> ids) {
            IdShortD8Tensor<String> tensor = (IdShortD8Tensor<String>) factory.shortTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdShortD9TensorTest extends IdShortD9TensorTest<String> {

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

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected IdShortD9Tensor<String> getIdTensor(TypeSafeArray<Short> data, List<List<String>> ids) {
            IdShortD9Tensor<String> tensor = (IdShortD9Tensor<String>) factory.shortTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdIntD0TensorTest extends IdIntD0TensorTest<String> {

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

        protected Tensor<Integer> getTensor(TypeSafeArray<Integer> data) {
            ids = getIds();
            return getIdTensor(data,ids);
        }

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(new int[] {1},random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IdIntD0Tensor<String> getIdTensor(TypeSafeArray<Integer> data, List<List<String>> ids) {
            IdIntD0Tensor<String> tensor = (IdIntD0Tensor<String>) factory.intTensor(ids);
            tensor.setTensorValues(data);
            return tensor;
        }
    }
    
    public static class TensorFactoryIdIntD1TensorTest extends IdIntD1TensorTest<String> {

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

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IdIntD1Tensor<String> getIdTensor(TypeSafeArray<Integer> data, List<List<String>> ids) {
            IdIntD1Tensor<String> tensor = (IdIntD1Tensor<String>) factory.intTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdIntD2TensorTest extends IdIntD2TensorTest<String> {

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

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IdIntD2Tensor<String> getIdTensor(TypeSafeArray<Integer> data, List<List<String>> ids) {
            IdIntD2Tensor<String> tensor = (IdIntD2Tensor<String>) factory.intTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdIntD3TensorTest extends IdIntD3TensorTest<String> {

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

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IdIntD3Tensor<String> getIdTensor(TypeSafeArray<Integer> data, List<List<String>> ids) {
            IdIntD3Tensor<String> tensor = (IdIntD3Tensor<String>) factory.intTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdIntD4TensorTest extends IdIntD4TensorTest<String> {

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

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IdIntD4Tensor<String> getIdTensor(TypeSafeArray<Integer> data, List<List<String>> ids) {
            IdIntD4Tensor<String> tensor = (IdIntD4Tensor<String>) factory.intTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdIntD5TensorTest extends IdIntD5TensorTest<String> {

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

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IdIntD5Tensor<String> getIdTensor(TypeSafeArray<Integer> data, List<List<String>> ids) {
            IdIntD5Tensor<String> tensor = (IdIntD5Tensor<String>) factory.intTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdIntD6TensorTest extends IdIntD6TensorTest<String> {

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

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IdIntD6Tensor<String> getIdTensor(TypeSafeArray<Integer> data, List<List<String>> ids) {
            IdIntD6Tensor<String> tensor = (IdIntD6Tensor<String>) factory.intTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdIntD7TensorTest extends IdIntD7TensorTest<String> {

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

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IdIntD7Tensor<String> getIdTensor(TypeSafeArray<Integer> data, List<List<String>> ids) {
            IdIntD7Tensor<String> tensor = (IdIntD7Tensor<String>) factory.intTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdIntD8TensorTest extends IdIntD8TensorTest<String> {

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

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IdIntD8Tensor<String> getIdTensor(TypeSafeArray<Integer> data, List<List<String>> ids) {
            IdIntD8Tensor<String> tensor = (IdIntD8Tensor<String>) factory.intTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdIntD9TensorTest extends IdIntD9TensorTest<String> {

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

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IdIntD9Tensor<String> getIdTensor(TypeSafeArray<Integer> data, List<List<String>> ids) {
            IdIntD9Tensor<String> tensor = (IdIntD9Tensor<String>) factory.intTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }  

    public static class TensorFactoryIdLongD0TensorTest extends IdLongD0TensorTest<String> {

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

        protected Tensor<Long> getTensor(TypeSafeArray<Long> data) {
            ids = getIds();
            return getIdTensor(data,ids);
        }

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(new int[] {1},random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected IdLongD0Tensor<String> getIdTensor(TypeSafeArray<Long> data, List<List<String>> ids) {
            IdLongD0Tensor<String> tensor = (IdLongD0Tensor<String>) factory.longTensor(ids);
            tensor.setTensorValues(data);
            return tensor;
        }
    }
    
    public static class TensorFactoryIdLongD1TensorTest extends IdLongD1TensorTest<String> {

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

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected IdLongD1Tensor<String> getIdTensor(TypeSafeArray<Long> data, List<List<String>> ids) {
            IdLongD1Tensor<String> tensor = (IdLongD1Tensor<String>) factory.longTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdLongD2TensorTest extends IdLongD2TensorTest<String> {

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

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected IdLongD2Tensor<String> getIdTensor(TypeSafeArray<Long> data, List<List<String>> ids) {
            IdLongD2Tensor<String> tensor = (IdLongD2Tensor<String>) factory.longTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdLongD3TensorTest extends IdLongD3TensorTest<String> {

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

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected IdLongD3Tensor<String> getIdTensor(TypeSafeArray<Long> data, List<List<String>> ids) {
            IdLongD3Tensor<String> tensor = (IdLongD3Tensor<String>) factory.longTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdLongD4TensorTest extends IdLongD4TensorTest<String> {

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

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected IdLongD4Tensor<String> getIdTensor(TypeSafeArray<Long> data, List<List<String>> ids) {
            IdLongD4Tensor<String> tensor = (IdLongD4Tensor<String>) factory.longTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdLongD5TensorTest extends IdLongD5TensorTest<String> {

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

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected IdLongD5Tensor<String> getIdTensor(TypeSafeArray<Long> data, List<List<String>> ids) {
            IdLongD5Tensor<String> tensor = (IdLongD5Tensor<String>) factory.longTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdLongD6TensorTest extends IdLongD6TensorTest<String> {

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

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected IdLongD6Tensor<String> getIdTensor(TypeSafeArray<Long> data, List<List<String>> ids) {
            IdLongD6Tensor<String> tensor = (IdLongD6Tensor<String>) factory.longTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdLongD7TensorTest extends IdLongD7TensorTest<String> {

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

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected IdLongD7Tensor<String> getIdTensor(TypeSafeArray<Long> data, List<List<String>> ids) {
            IdLongD7Tensor<String> tensor = (IdLongD7Tensor<String>) factory.longTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdLongD8TensorTest extends IdLongD8TensorTest<String> {

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

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected IdLongD8Tensor<String> getIdTensor(TypeSafeArray<Long> data, List<List<String>> ids) {
            IdLongD8Tensor<String> tensor = (IdLongD8Tensor<String>) factory.longTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdLongD9TensorTest extends IdLongD9TensorTest<String> {

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

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected IdLongD9Tensor<String> getIdTensor(TypeSafeArray<Long> data, List<List<String>> ids) {
            IdLongD9Tensor<String> tensor = (IdLongD9Tensor<String>) factory.longTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }  

    public static class TensorFactoryIdFloatD0TensorTest extends IdFloatD0TensorTest<String> {

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

        protected Tensor<Float> getTensor(TypeSafeArray<Float> data) {
            ids = getIds();
            return getIdTensor(data,ids);
        }

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(new int[] {1},random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected IdFloatD0Tensor<String> getIdTensor(TypeSafeArray<Float> data, List<List<String>> ids) {
            IdFloatD0Tensor<String> tensor = (IdFloatD0Tensor<String>) factory.floatTensor(ids);
            tensor.setTensorValues(data);
            return tensor;
        }
    }        
    
    public static class TensorFactoryIdFloatD1TensorTest extends IdFloatD1TensorTest<String> {

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

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected IdFloatD1Tensor<String> getIdTensor(TypeSafeArray<Float> data, List<List<String>> ids) {
            IdFloatD1Tensor<String> tensor = (IdFloatD1Tensor<String>) factory.floatTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdFloatD2TensorTest extends IdFloatD2TensorTest<String> {

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

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected IdFloatD2Tensor<String> getIdTensor(TypeSafeArray<Float> data, List<List<String>> ids) {
            IdFloatD2Tensor<String> tensor = (IdFloatD2Tensor<String>) factory.floatTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdFloatD3TensorTest extends IdFloatD3TensorTest<String> {

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

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected IdFloatD3Tensor<String> getIdTensor(TypeSafeArray<Float> data, List<List<String>> ids) {
            IdFloatD3Tensor<String> tensor = (IdFloatD3Tensor<String>) factory.floatTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdFloatD4TensorTest extends IdFloatD4TensorTest<String> {

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

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected IdFloatD4Tensor<String> getIdTensor(TypeSafeArray<Float> data, List<List<String>> ids) {
            IdFloatD4Tensor<String> tensor = (IdFloatD4Tensor<String>) factory.floatTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdFloatD5TensorTest extends IdFloatD5TensorTest<String> {

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

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected IdFloatD5Tensor<String> getIdTensor(TypeSafeArray<Float> data, List<List<String>> ids) {
            IdFloatD5Tensor<String> tensor = (IdFloatD5Tensor<String>) factory.floatTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdFloatD6TensorTest extends IdFloatD6TensorTest<String> {

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

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected IdFloatD6Tensor<String> getIdTensor(TypeSafeArray<Float> data, List<List<String>> ids) {
            IdFloatD6Tensor<String> tensor = (IdFloatD6Tensor<String>) factory.floatTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdFloatD7TensorTest extends IdFloatD7TensorTest<String> {

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

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected IdFloatD7Tensor<String> getIdTensor(TypeSafeArray<Float> data, List<List<String>> ids) {
            IdFloatD7Tensor<String> tensor = (IdFloatD7Tensor<String>) factory.floatTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdFloatD8TensorTest extends IdFloatD8TensorTest<String> {

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

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected IdFloatD8Tensor<String> getIdTensor(TypeSafeArray<Float> data, List<List<String>> ids) {
            IdFloatD8Tensor<String> tensor = (IdFloatD8Tensor<String>) factory.floatTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdFloatD9TensorTest extends IdFloatD9TensorTest<String> {

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

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected IdFloatD9Tensor<String> getIdTensor(TypeSafeArray<Float> data, List<List<String>> ids) {
            IdFloatD9Tensor<String> tensor = (IdFloatD9Tensor<String>) factory.floatTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }  

    public static class TensorFactoryIdDoubleD0TensorTest extends IdDoubleD0TensorTest<String> {

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

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(new int[] {1},random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected IdDoubleD0Tensor<String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdDoubleD0Tensor<String> tensor = (IdDoubleD0Tensor<String>) factory.doubleTensor(ids);
            tensor.setTensorValues(data);
            return tensor;
        }
    }   
    
    public static class TensorFactoryIdDoubleD1TensorTest extends IdDoubleD1TensorTest<String> {

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

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected IdDoubleD1Tensor<String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdDoubleD1Tensor<String> tensor = (IdDoubleD1Tensor<String>) factory.doubleTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdDoubleD2TensorTest extends IdDoubleD2TensorTest<String> {

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

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected IdDoubleD2Tensor<String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdDoubleD2Tensor<String> tensor = (IdDoubleD2Tensor<String>) factory.doubleTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdDoubleD3TensorTest extends IdDoubleD3TensorTest<String> {

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

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected IdDoubleD3Tensor<String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdDoubleD3Tensor<String> tensor = (IdDoubleD3Tensor<String>) factory.doubleTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdDoubleD4TensorTest extends IdDoubleD4TensorTest<String> {

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

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected IdDoubleD4Tensor<String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdDoubleD4Tensor<String> tensor = (IdDoubleD4Tensor<String>) factory.doubleTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdDoubleD5TensorTest extends IdDoubleD5TensorTest<String> {

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

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected IdDoubleD5Tensor<String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdDoubleD5Tensor<String> tensor = (IdDoubleD5Tensor<String>) factory.doubleTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdDoubleD6TensorTest extends IdDoubleD6TensorTest<String> {

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

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected IdDoubleD6Tensor<String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdDoubleD6Tensor<String> tensor = (IdDoubleD6Tensor<String>) factory.doubleTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdDoubleD7TensorTest extends IdDoubleD7TensorTest<String> {

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

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected IdDoubleD7Tensor<String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdDoubleD7Tensor<String> tensor = (IdDoubleD7Tensor<String>) factory.doubleTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdDoubleD8TensorTest extends IdDoubleD8TensorTest<String> {

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

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected IdDoubleD8Tensor<String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdDoubleD8Tensor<String> tensor = (IdDoubleD8Tensor<String>) factory.doubleTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdDoubleD9TensorTest extends IdDoubleD9TensorTest<String> {

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

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected IdDoubleD9Tensor<String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdDoubleD9Tensor<String> tensor = (IdDoubleD9Tensor<String>) factory.doubleTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }  

    public static class TensorFactoryIdCharD0TensorTest extends IdCharD0TensorTest<String> {

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

        protected Tensor<Character> getTensor(TypeSafeArray<Character> data) {
            ids = getIds();
            return getIdTensor(data,ids);
        }

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(new int[] {1},random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected IdCharD0Tensor<String> getIdTensor(TypeSafeArray<Character> data, List<List<String>> ids) {
            IdCharD0Tensor<String> tensor = (IdCharD0Tensor<String>) factory.charTensor(ids);
            tensor.setTensorValues(data);
            return tensor;
        }
    }            
    
    public static class TensorFactoryIdCharD1TensorTest extends IdCharD1TensorTest<String> {

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

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected IdCharD1Tensor<String> getIdTensor(TypeSafeArray<Character> data, List<List<String>> ids) {
            IdCharD1Tensor<String> tensor = (IdCharD1Tensor<String>) factory.charTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdCharD2TensorTest extends IdCharD2TensorTest<String> {

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

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected IdCharD2Tensor<String> getIdTensor(TypeSafeArray<Character> data, List<List<String>> ids) {
            IdCharD2Tensor<String> tensor = (IdCharD2Tensor<String>) factory.charTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdCharD3TensorTest extends IdCharD3TensorTest<String> {

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

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected IdCharD3Tensor<String> getIdTensor(TypeSafeArray<Character> data, List<List<String>> ids) {
            IdCharD3Tensor<String> tensor = (IdCharD3Tensor<String>) factory.charTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdCharD4TensorTest extends IdCharD4TensorTest<String> {

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

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected IdCharD4Tensor<String> getIdTensor(TypeSafeArray<Character> data, List<List<String>> ids) {
            IdCharD4Tensor<String> tensor = (IdCharD4Tensor<String>) factory.charTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdCharD5TensorTest extends IdCharD5TensorTest<String> {

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

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected IdCharD5Tensor<String> getIdTensor(TypeSafeArray<Character> data, List<List<String>> ids) {
            IdCharD5Tensor<String> tensor = (IdCharD5Tensor<String>) factory.charTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdCharD6TensorTest extends IdCharD6TensorTest<String> {

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

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected IdCharD6Tensor<String> getIdTensor(TypeSafeArray<Character> data, List<List<String>> ids) {
            IdCharD6Tensor<String> tensor = (IdCharD6Tensor<String>) factory.charTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdCharD7TensorTest extends IdCharD7TensorTest<String> {

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

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected IdCharD7Tensor<String> getIdTensor(TypeSafeArray<Character> data, List<List<String>> ids) {
            IdCharD7Tensor<String> tensor = (IdCharD7Tensor<String>) factory.charTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdCharD8TensorTest extends IdCharD8TensorTest<String> {

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

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected IdCharD8Tensor<String> getIdTensor(TypeSafeArray<Character> data, List<List<String>> ids) {
            IdCharD8Tensor<String> tensor = (IdCharD8Tensor<String>) factory.charTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdCharD9TensorTest extends IdCharD9TensorTest<String> {

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

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected IdCharD9Tensor<String> getIdTensor(TypeSafeArray<Character> data, List<List<String>> ids) {
            IdCharD9Tensor<String> tensor = (IdCharD9Tensor<String>) factory.charTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }
    
}
