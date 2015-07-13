package com.pb.sawdust.tensor.decorators.primitive;

import com.pb.sawdust.tensor.TensorFactoryTests;
import com.pb.sawdust.tensor.factory.TensorFactoryTest;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.array.*;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code PrimitivePackageFactoryTests} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 7:37:39 AM
 */
public class TensorPrimitiveFactoryTests {
    public static List<Class<? extends TestBase>> TEST_CLASSES = new LinkedList<Class<? extends TestBase>>();
    static {
        TEST_CLASSES.add(TensorFactoryBooleanTensorTest.class);
        TEST_CLASSES.add(TensorFactoryCharTensorTest.class);
        TEST_CLASSES.add(TensorFactoryByteTensorTest.class);
        TEST_CLASSES.add(TensorFactoryShortTensorTest.class);
        TEST_CLASSES.add(TensorFactoryIntTensorTest.class);
        TEST_CLASSES.add(TensorFactoryLongTensorTest.class);
        TEST_CLASSES.add(TensorFactoryFloatTensorTest.class);
        TEST_CLASSES.add(TensorFactoryDoubleTensorTest.class);
    }
    
    public static class TensorFactoryBooleanTensorTest extends BooleanTensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected BooleanTensor getTensor(TypeSafeArray<Boolean> data) {
            BooleanTensor tensor = factory.booleanTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }
    }

    public static class TensorFactoryCharTensorTest extends CharTensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected CharTensor getTensor(TypeSafeArray<Character> data) {
            CharTensor tensor = factory.charTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }
    }

    public static class TensorFactoryByteTensorTest extends ByteTensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ByteTensor getTensor(TypeSafeArray<Byte> data) {
            ByteTensor tensor = factory.byteTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }
    }

    public static class TensorFactoryShortTensorTest extends ShortTensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected ShortTensor getTensor(TypeSafeArray<Short> data) {
            ShortTensor tensor = factory.shortTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }
    }

    public static class TensorFactoryIntTensorTest extends IntTensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected IntTensor getTensor(TypeSafeArray<Integer> data) {
            IntTensor tensor = factory.intTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }
    }

    public static class TensorFactoryLongTensorTest extends LongTensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(), TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected LongTensor getTensor(TypeSafeArray<Long> data) {
            LongTensor tensor = factory.longTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }
    }

    public static class TensorFactoryFloatTensorTest extends FloatTensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected FloatTensor getTensor(TypeSafeArray<Float> data) {
            FloatTensor tensor = factory.floatTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }
    }

    public static class TensorFactoryDoubleTensorTest extends DoubleTensorTest {
        protected TensorFactory factory;
        
        public static void main(String ... args) {
            TestBase.main();
        }
        
        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(), TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected DoubleTensor getTensor(TypeSafeArray<Double> data) {
            DoubleTensor tensor = factory.doubleTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }
    }
    
}
