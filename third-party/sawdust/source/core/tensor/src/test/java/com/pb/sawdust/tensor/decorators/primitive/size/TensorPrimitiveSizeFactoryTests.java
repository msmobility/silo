package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.TensorFactoryTests;
import com.pb.sawdust.tensor.factory.TensorFactoryTest;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.array.*;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code PrimitiveSizePackageFactoryTests} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 7:46:25 AM
 */
public class TensorPrimitiveSizeFactoryTests {
    public static List<Class<? extends TestBase>> TEST_CLASSES = new LinkedList<Class<? extends TestBase>>();
    static {
        TEST_CLASSES.add(TensorFactoryBooleanD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryBooleanD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryBooleanD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryBooleanD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryBooleanD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryBooleanD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryBooleanD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryBooleanD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryBooleanD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryBooleanD9TensorTest.class);
        
        TEST_CLASSES.add(TensorFactoryCharD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryCharD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryCharD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryCharD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryCharD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryCharD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryCharD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryCharD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryCharD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryCharD9TensorTest.class);
        
        TEST_CLASSES.add(TensorFactoryByteD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryByteD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryByteD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryByteD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryByteD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryByteD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryByteD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryByteD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryByteD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryByteD9TensorTest.class);
        
        TEST_CLASSES.add(TensorFactoryShortD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryShortD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryShortD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryShortD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryShortD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryShortD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryShortD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryShortD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryShortD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryShortD9TensorTest.class);
        
        TEST_CLASSES.add(TensorFactoryIntD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIntD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIntD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIntD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIntD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIntD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIntD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIntD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIntD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryIntD9TensorTest.class);
        
        TEST_CLASSES.add(TensorFactoryLongD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryLongD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryLongD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryLongD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryLongD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryLongD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryLongD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryLongD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryLongD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryLongD9TensorTest.class);
        
        TEST_CLASSES.add(TensorFactoryFloatD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryFloatD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryFloatD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryFloatD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryFloatD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryFloatD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryFloatD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryFloatD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryFloatD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryFloatD9TensorTest.class);
        
        TEST_CLASSES.add(TensorFactoryDoubleD0TensorTest.class);
        TEST_CLASSES.add(TensorFactoryDoubleD1TensorTest.class);
        TEST_CLASSES.add(TensorFactoryDoubleD2TensorTest.class);
        TEST_CLASSES.add(TensorFactoryDoubleD3TensorTest.class);
        TEST_CLASSES.add(TensorFactoryDoubleD4TensorTest.class);
        TEST_CLASSES.add(TensorFactoryDoubleD5TensorTest.class);
        TEST_CLASSES.add(TensorFactoryDoubleD6TensorTest.class);
        TEST_CLASSES.add(TensorFactoryDoubleD7TensorTest.class);
        TEST_CLASSES.add(TensorFactoryDoubleD8TensorTest.class);
        TEST_CLASSES.add(TensorFactoryDoubleD9TensorTest.class);
    }
    


    public static class TensorFactoryBooleanD0TensorTest extends BooleanD0TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(new int[] {1},random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected BooleanD0Tensor getTensor(TypeSafeArray<Boolean> data) {
            BooleanD0Tensor tensor = (BooleanD0Tensor) factory.booleanTensor();
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryBooleanD1TensorTest extends BooleanD1TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected BooleanD1Tensor getTensor(TypeSafeArray<Boolean> data) {
            BooleanD1Tensor tensor = (BooleanD1Tensor) factory.booleanTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryBooleanD2TensorTest extends BooleanD2TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected BooleanD2Tensor getTensor(TypeSafeArray<Boolean> data) {
            BooleanD2Tensor tensor = (BooleanD2Tensor) factory.booleanTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryBooleanD3TensorTest extends BooleanD3TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected BooleanD3Tensor getTensor(TypeSafeArray<Boolean> data) {
            BooleanD3Tensor tensor = (BooleanD3Tensor) factory.booleanTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryBooleanD4TensorTest extends BooleanD4TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected BooleanD4Tensor getTensor(TypeSafeArray<Boolean> data) {
            BooleanD4Tensor tensor = (BooleanD4Tensor) factory.booleanTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryBooleanD5TensorTest extends BooleanD5TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected BooleanD5Tensor getTensor(TypeSafeArray<Boolean> data) {
            BooleanD5Tensor tensor = (BooleanD5Tensor) factory.booleanTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryBooleanD6TensorTest extends BooleanD6TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected BooleanD6Tensor getTensor(TypeSafeArray<Boolean> data) {
            BooleanD6Tensor tensor = (BooleanD6Tensor) factory.booleanTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryBooleanD7TensorTest extends BooleanD7TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected BooleanD7Tensor getTensor(TypeSafeArray<Boolean> data) {
            BooleanD7Tensor tensor = (BooleanD7Tensor) factory.booleanTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryBooleanD8TensorTest extends BooleanD8TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected BooleanD8Tensor getTensor(TypeSafeArray<Boolean> data) {
            BooleanD8Tensor tensor = (BooleanD8Tensor) factory.booleanTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }   

    public static class TensorFactoryBooleanD9TensorTest extends BooleanD9TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected BooleanD9Tensor getTensor(TypeSafeArray<Boolean> data) {
            BooleanD9Tensor tensor = (BooleanD9Tensor) factory.booleanTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }  

    public static class TensorFactoryByteD0TensorTest extends ByteD0TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(new int[] {1},random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected ByteD0Tensor getTensor(TypeSafeArray<Byte> data) {
            ByteD0Tensor tensor = (ByteD0Tensor) factory.byteTensor();
            tensor.setTensorValues(data);
            return tensor;
        }
    }
    
    public static class TensorFactoryByteD1TensorTest extends ByteD1TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected ByteD1Tensor getTensor(TypeSafeArray<Byte> data) {
            ByteD1Tensor tensor = (ByteD1Tensor) factory.byteTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryByteD2TensorTest extends ByteD2TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected ByteD2Tensor getTensor(TypeSafeArray<Byte> data) {
            ByteD2Tensor tensor = (ByteD2Tensor) factory.byteTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryByteD3TensorTest extends ByteD3TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected ByteD3Tensor getTensor(TypeSafeArray<Byte> data) {
            ByteD3Tensor tensor = (ByteD3Tensor) factory.byteTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryByteD4TensorTest extends ByteD4TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected ByteD4Tensor getTensor(TypeSafeArray<Byte> data) {
            ByteD4Tensor tensor = (ByteD4Tensor) factory.byteTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryByteD5TensorTest extends ByteD5TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected ByteD5Tensor getTensor(TypeSafeArray<Byte> data) {
            ByteD5Tensor tensor = (ByteD5Tensor) factory.byteTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryByteD6TensorTest extends ByteD6TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected ByteD6Tensor getTensor(TypeSafeArray<Byte> data) {
            ByteD6Tensor tensor = (ByteD6Tensor) factory.byteTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryByteD7TensorTest extends ByteD7TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected ByteD7Tensor getTensor(TypeSafeArray<Byte> data) {
            ByteD7Tensor tensor = (ByteD7Tensor) factory.byteTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryByteD8TensorTest extends ByteD8TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected ByteD8Tensor getTensor(TypeSafeArray<Byte> data) {
            ByteD8Tensor tensor = (ByteD8Tensor) factory.byteTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryByteD9TensorTest extends ByteD9TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected ByteD9Tensor getTensor(TypeSafeArray<Byte> data) {
            ByteD9Tensor tensor = (ByteD9Tensor) factory.byteTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }                   
      

    public static class TensorFactoryShortD0TensorTest extends ShortD0TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(new int[] {1},random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected ShortD0Tensor getTensor(TypeSafeArray<Short> data) {
            ShortD0Tensor tensor = (ShortD0Tensor) factory.shortTensor();
            tensor.setTensorValues(data);
            return tensor;
        }
    }
    
    public static class TensorFactoryShortD1TensorTest extends ShortD1TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected ShortD1Tensor getTensor(TypeSafeArray<Short> data) {
            ShortD1Tensor tensor = (ShortD1Tensor) factory.shortTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryShortD2TensorTest extends ShortD2TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected ShortD2Tensor getTensor(TypeSafeArray<Short> data) {
            ShortD2Tensor tensor = (ShortD2Tensor) factory.shortTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryShortD3TensorTest extends ShortD3TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected ShortD3Tensor getTensor(TypeSafeArray<Short> data) {
            ShortD3Tensor tensor = (ShortD3Tensor) factory.shortTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryShortD4TensorTest extends ShortD4TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected ShortD4Tensor getTensor(TypeSafeArray<Short> data) {
            ShortD4Tensor tensor = (ShortD4Tensor) factory.shortTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryShortD5TensorTest extends ShortD5TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected ShortD5Tensor getTensor(TypeSafeArray<Short> data) {
            ShortD5Tensor tensor = (ShortD5Tensor) factory.shortTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryShortD6TensorTest extends ShortD6TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected ShortD6Tensor getTensor(TypeSafeArray<Short> data) {
            ShortD6Tensor tensor = (ShortD6Tensor) factory.shortTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryShortD7TensorTest extends ShortD7TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected ShortD7Tensor getTensor(TypeSafeArray<Short> data) {
            ShortD7Tensor tensor = (ShortD7Tensor) factory.shortTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryShortD8TensorTest extends ShortD8TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected ShortD8Tensor getTensor(TypeSafeArray<Short> data) {
            ShortD8Tensor tensor = (ShortD8Tensor) factory.shortTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryShortD9TensorTest extends ShortD9TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected ShortD9Tensor getTensor(TypeSafeArray<Short> data) {
            ShortD9Tensor tensor = (ShortD9Tensor) factory.shortTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIntD0TensorTest extends IntD0TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(new int[] {1},random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IntD0Tensor getTensor(TypeSafeArray<Integer> data) {
            IntD0Tensor tensor = (IntD0Tensor) factory.intTensor();
            tensor.setTensorValues(data);
            return tensor;
        }
    }
    
    public static class TensorFactoryIntD1TensorTest extends IntD1TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IntD1Tensor getTensor(TypeSafeArray<Integer> data) {
            IntD1Tensor tensor = (IntD1Tensor) factory.intTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIntD2TensorTest extends IntD2TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IntD2Tensor getTensor(TypeSafeArray<Integer> data) {
            IntD2Tensor tensor = (IntD2Tensor) factory.intTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIntD3TensorTest extends IntD3TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IntD3Tensor getTensor(TypeSafeArray<Integer> data) {
            IntD3Tensor tensor = (IntD3Tensor) factory.intTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIntD4TensorTest extends IntD4TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IntD4Tensor getTensor(TypeSafeArray<Integer> data) {
            IntD4Tensor tensor = (IntD4Tensor) factory.intTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIntD5TensorTest extends IntD5TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(), TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IntD5Tensor getTensor(TypeSafeArray<Integer> data) {
            IntD5Tensor tensor = (IntD5Tensor) factory.intTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIntD6TensorTest extends IntD6TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IntD6Tensor getTensor(TypeSafeArray<Integer> data) {
            IntD6Tensor tensor = (IntD6Tensor) factory.intTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIntD7TensorTest extends IntD7TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IntD7Tensor getTensor(TypeSafeArray<Integer> data) {
            IntD7Tensor tensor = (IntD7Tensor) factory.intTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIntD8TensorTest extends IntD8TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IntD8Tensor getTensor(TypeSafeArray<Integer> data) {
            IntD8Tensor tensor = (IntD8Tensor) factory.intTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIntD9TensorTest extends IntD9TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IntD9Tensor getTensor(TypeSafeArray<Integer> data) {
            IntD9Tensor tensor = (IntD9Tensor) factory.intTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }  

    public static class TensorFactoryLongD0TensorTest extends LongD0TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(new int[] {1},random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected LongD0Tensor getTensor(TypeSafeArray<Long> data) {
            LongD0Tensor tensor = (LongD0Tensor) factory.longTensor();
            tensor.setTensorValues(data);
            return tensor;
        }
    }
    
    public static class TensorFactoryLongD1TensorTest extends LongD1TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected LongD1Tensor getTensor(TypeSafeArray<Long> data) {
            LongD1Tensor tensor = (LongD1Tensor) factory.longTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryLongD2TensorTest extends LongD2TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected LongD2Tensor getTensor(TypeSafeArray<Long> data) {
            LongD2Tensor tensor = (LongD2Tensor) factory.longTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryLongD3TensorTest extends LongD3TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected LongD3Tensor getTensor(TypeSafeArray<Long> data) {
            LongD3Tensor tensor = (LongD3Tensor) factory.longTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryLongD4TensorTest extends LongD4TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected LongD4Tensor getTensor(TypeSafeArray<Long> data) {
            LongD4Tensor tensor = (LongD4Tensor) factory.longTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryLongD5TensorTest extends LongD5TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected LongD5Tensor getTensor(TypeSafeArray<Long> data) {
            LongD5Tensor tensor = (LongD5Tensor) factory.longTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryLongD6TensorTest extends LongD6TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected LongD6Tensor getTensor(TypeSafeArray<Long> data) {
            LongD6Tensor tensor = (LongD6Tensor) factory.longTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryLongD7TensorTest extends LongD7TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected LongD7Tensor getTensor(TypeSafeArray<Long> data) {
            LongD7Tensor tensor = (LongD7Tensor) factory.longTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryLongD8TensorTest extends LongD8TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected LongD8Tensor getTensor(TypeSafeArray<Long> data) {
            LongD8Tensor tensor = (LongD8Tensor) factory.longTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryLongD9TensorTest extends LongD9TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected LongD9Tensor getTensor(TypeSafeArray<Long> data) {
            LongD9Tensor tensor = (LongD9Tensor) factory.longTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }  

    public static class TensorFactoryFloatD0TensorTest extends FloatD0TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(new int[] {1},random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected FloatD0Tensor getTensor(TypeSafeArray<Float> data) {
            FloatD0Tensor tensor = (FloatD0Tensor) factory.floatTensor();
            tensor.setTensorValues(data);
            return tensor;
        }
    }        
    
    public static class TensorFactoryFloatD1TensorTest extends FloatD1TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected FloatD1Tensor getTensor(TypeSafeArray<Float> data) {
            FloatD1Tensor tensor = (FloatD1Tensor) factory.floatTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryFloatD2TensorTest extends FloatD2TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected FloatD2Tensor getTensor(TypeSafeArray<Float> data) {
            FloatD2Tensor tensor = (FloatD2Tensor) factory.floatTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryFloatD3TensorTest extends FloatD3TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected FloatD3Tensor getTensor(TypeSafeArray<Float> data) {
            FloatD3Tensor tensor = (FloatD3Tensor) factory.floatTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryFloatD4TensorTest extends FloatD4TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected FloatD4Tensor getTensor(TypeSafeArray<Float> data) {
            FloatD4Tensor tensor = (FloatD4Tensor) factory.floatTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryFloatD5TensorTest extends FloatD5TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected FloatD5Tensor getTensor(TypeSafeArray<Float> data) {
            FloatD5Tensor tensor = (FloatD5Tensor) factory.floatTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryFloatD6TensorTest extends FloatD6TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected FloatD6Tensor getTensor(TypeSafeArray<Float> data) {
            FloatD6Tensor tensor = (FloatD6Tensor) factory.floatTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryFloatD7TensorTest extends FloatD7TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected FloatD7Tensor getTensor(TypeSafeArray<Float> data) {
            FloatD7Tensor tensor = (FloatD7Tensor) factory.floatTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryFloatD8TensorTest extends FloatD8TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected FloatD8Tensor getTensor(TypeSafeArray<Float> data) {
            FloatD8Tensor tensor = (FloatD8Tensor) factory.floatTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryFloatD9TensorTest extends FloatD9TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected FloatD9Tensor getTensor(TypeSafeArray<Float> data) {
            FloatD9Tensor tensor = (FloatD9Tensor) factory.floatTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }  

    public static class TensorFactoryDoubleD0TensorTest extends DoubleD0TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(new int[] {1},random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected DoubleD0Tensor getTensor(TypeSafeArray<Double> data) {
            DoubleD0Tensor tensor = (DoubleD0Tensor) factory.doubleTensor();
            tensor.setTensorValues(data);
            return tensor;
        }
    }   
    
    public static class TensorFactoryDoubleD1TensorTest extends DoubleD1TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected DoubleD1Tensor getTensor(TypeSafeArray<Double> data) {
            DoubleD1Tensor tensor = (DoubleD1Tensor) factory.doubleTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryDoubleD2TensorTest extends DoubleD2TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected DoubleD2Tensor getTensor(TypeSafeArray<Double> data) {
            DoubleD2Tensor tensor = (DoubleD2Tensor) factory.doubleTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryDoubleD3TensorTest extends DoubleD3TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected DoubleD3Tensor getTensor(TypeSafeArray<Double> data) {
            DoubleD3Tensor tensor = (DoubleD3Tensor) factory.doubleTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryDoubleD4TensorTest extends DoubleD4TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected DoubleD4Tensor getTensor(TypeSafeArray<Double> data) {
            DoubleD4Tensor tensor = (DoubleD4Tensor) factory.doubleTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryDoubleD5TensorTest extends DoubleD5TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected DoubleD5Tensor getTensor(TypeSafeArray<Double> data) {
            DoubleD5Tensor tensor = (DoubleD5Tensor) factory.doubleTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryDoubleD6TensorTest extends DoubleD6TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected DoubleD6Tensor getTensor(TypeSafeArray<Double> data) {
            DoubleD6Tensor tensor = (DoubleD6Tensor) factory.doubleTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryDoubleD7TensorTest extends DoubleD7TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected DoubleD7Tensor getTensor(TypeSafeArray<Double> data) {
            DoubleD7Tensor tensor = (DoubleD7Tensor) factory.doubleTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryDoubleD8TensorTest extends DoubleD8TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected DoubleD8Tensor getTensor(TypeSafeArray<Double> data) {
            DoubleD8Tensor tensor = (DoubleD8Tensor) factory.doubleTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryDoubleD9TensorTest extends DoubleD9TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected DoubleD9Tensor getTensor(TypeSafeArray<Double> data) {
            DoubleD9Tensor tensor = (DoubleD9Tensor) factory.doubleTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }  

    public static class TensorFactoryCharD0TensorTest extends CharD0TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(new int[] {1},random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected CharD0Tensor getTensor(TypeSafeArray<Character> data) {
            CharD0Tensor tensor = (CharD0Tensor) factory.charTensor();
            tensor.setTensorValues(data);
            return tensor;
        }
    }            
    
    public static class TensorFactoryCharD1TensorTest extends CharD1TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected CharD1Tensor getTensor(TypeSafeArray<Character> data) {
            CharD1Tensor tensor = (CharD1Tensor) factory.charTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryCharD2TensorTest extends CharD2TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected CharD2Tensor getTensor(TypeSafeArray<Character> data) {
            CharD2Tensor tensor = (CharD2Tensor) factory.charTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryCharD3TensorTest extends CharD3TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,3),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected CharD3Tensor getTensor(TypeSafeArray<Character> data) {
            CharD3Tensor tensor = (CharD3Tensor) factory.charTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryCharD4TensorTest extends CharD4TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,4),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected CharD4Tensor getTensor(TypeSafeArray<Character> data) {
            CharD4Tensor tensor = (CharD4Tensor) factory.charTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryCharD5TensorTest extends CharD5TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,5),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected CharD5Tensor getTensor(TypeSafeArray<Character> data) {
            CharD5Tensor tensor = (CharD5Tensor) factory.charTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryCharD6TensorTest extends CharD6TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,6),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected CharD6Tensor getTensor(TypeSafeArray<Character> data) {
            CharD6Tensor tensor = (CharD6Tensor) factory.charTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryCharD7TensorTest extends CharD7TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,7),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected CharD7Tensor getTensor(TypeSafeArray<Character> data) {
            CharD7Tensor tensor = (CharD7Tensor) factory.charTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryCharD8TensorTest extends CharD8TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,8),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected CharD8Tensor getTensor(TypeSafeArray<Character> data) {
            CharD8Tensor tensor = (CharD8Tensor) factory.charTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryCharD9TensorTest extends CharD9TensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,9),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected CharD9Tensor getTensor(TypeSafeArray<Character> data) {
            CharD9Tensor tensor = (CharD9Tensor) factory.charTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }    
}
