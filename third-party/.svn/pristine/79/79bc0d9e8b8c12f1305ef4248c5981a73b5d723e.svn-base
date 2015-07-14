package com.pb.sawdust.tensor.alias.vector.primitive;

import com.pb.sawdust.tensor.TensorFactoryTests;
import com.pb.sawdust.tensor.factory.TensorFactoryTest;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.array.*;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code VectorPrimitivePackageFactoryTests} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 10:32:21 AM
 */
public class TensorVectorPrimitiveFactoryTests {
    public static List<Class<? extends TestBase>> TEST_CLASSES = new LinkedList<Class<? extends TestBase>>();
    static {
        TEST_CLASSES.add(TensorFactoryBooleanVectorTest.class);
        TEST_CLASSES.add(TensorFactoryCharVectorTest.class);
        TEST_CLASSES.add(TensorFactoryByteVectorTest.class);
        TEST_CLASSES.add(TensorFactoryShortVectorTest.class);
        TEST_CLASSES.add(TensorFactoryIntVectorTest.class);
        TEST_CLASSES.add(TensorFactoryLongVectorTest.class);
        TEST_CLASSES.add(TensorFactoryFloatVectorTest.class);
        TEST_CLASSES.add(TensorFactoryDoubleVectorTest.class);
    }


    public static class TensorFactoryBooleanVectorTest extends BooleanVectorTest {

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

        protected BooleanVector getTensor(TypeSafeArray<Boolean> data) {
            BooleanVector tensor = (BooleanVector) factory.booleanTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryCharVectorTest extends CharVectorTest {

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

        protected CharVector getTensor(TypeSafeArray<Character> data) {
            CharVector tensor = (CharVector) factory.charTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryByteVectorTest extends ByteVectorTest {

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

        protected ByteVector getTensor(TypeSafeArray<Byte> data) {
            ByteVector tensor = (ByteVector) factory.byteTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryShortVectorTest extends ShortVectorTest {

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

        protected ShortVector getTensor(TypeSafeArray<Short> data) {
            ShortVector tensor = (ShortVector) factory.shortTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIntVectorTest extends IntVectorTest {

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

        protected IntVector getTensor(TypeSafeArray<Integer> data) {
            IntVector tensor = (IntVector) factory.intTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryLongVectorTest extends LongVectorTest {

        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(), TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected LongVector getTensor(TypeSafeArray<Long> data) {
            LongVector tensor = (LongVector) factory.longTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryFloatVectorTest extends FloatVectorTest {

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

        protected FloatVector getTensor(TypeSafeArray<Float> data) {
            FloatVector tensor = (FloatVector) factory.floatTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryDoubleVectorTest extends DoubleVectorTest {

        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(), TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected DoubleVector getTensor(TypeSafeArray<Double> data) {
            DoubleVector tensor = (DoubleVector) factory.doubleTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }
}
