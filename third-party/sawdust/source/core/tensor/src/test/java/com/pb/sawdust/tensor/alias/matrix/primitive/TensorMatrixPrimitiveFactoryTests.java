package com.pb.sawdust.tensor.alias.matrix.primitive;

import com.pb.sawdust.tensor.TensorFactoryTests;
import com.pb.sawdust.tensor.factory.TensorFactoryTest;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.array.*;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code MatrixPrimitivePackageFactoryTests} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 10:32:21 AM
 */
public class TensorMatrixPrimitiveFactoryTests {
    public static List<Class<? extends TestBase>> TEST_CLASSES = new LinkedList<Class<? extends TestBase>>();
    static {
        TEST_CLASSES.add(TensorFactoryBooleanMatrixTest.class);
        TEST_CLASSES.add(TensorFactoryCharMatrixTest.class);
        TEST_CLASSES.add(TensorFactoryByteMatrixTest.class);
        TEST_CLASSES.add(TensorFactoryShortMatrixTest.class);
        TEST_CLASSES.add(TensorFactoryIntMatrixTest.class);
        TEST_CLASSES.add(TensorFactoryLongMatrixTest.class);
        TEST_CLASSES.add(TensorFactoryFloatMatrixTest.class);
        TEST_CLASSES.add(TensorFactoryDoubleMatrixTest.class);
    }


    public static class TensorFactoryBooleanMatrixTest extends BooleanMatrixTest {

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

        protected BooleanMatrix getTensor(TypeSafeArray<Boolean> data) {
            BooleanMatrix tensor = (BooleanMatrix) factory.booleanTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryCharMatrixTest extends CharMatrixTest {

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

        protected CharMatrix getTensor(TypeSafeArray<Character> data) {
            CharMatrix tensor = (CharMatrix) factory.charTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryByteMatrixTest extends ByteMatrixTest {

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

        protected ByteMatrix getTensor(TypeSafeArray<Byte> data) {
            ByteMatrix tensor = (ByteMatrix) factory.byteTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryShortMatrixTest extends ShortMatrixTest {

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

        protected ShortMatrix getTensor(TypeSafeArray<Short> data) {
            ShortMatrix tensor = (ShortMatrix) factory.shortTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIntMatrixTest extends IntMatrixTest {

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
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IntMatrix getTensor(TypeSafeArray<Integer> data) {
            IntMatrix tensor = (IntMatrix) factory.intTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryLongMatrixTest extends LongMatrixTest {

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

        protected LongMatrix getTensor(TypeSafeArray<Long> data) {
            LongMatrix tensor = (LongMatrix) factory.longTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryFloatMatrixTest extends FloatMatrixTest {

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

        protected FloatMatrix getTensor(TypeSafeArray<Float> data) {
            FloatMatrix tensor = (FloatMatrix) factory.floatTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryDoubleMatrixTest extends DoubleMatrixTest {

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
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,2),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected DoubleMatrix getTensor(TypeSafeArray<Double> data) {
            DoubleMatrix tensor = (DoubleMatrix) factory.doubleTensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }
}
