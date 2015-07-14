package com.pb.sawdust.tensor.decorators.id.primitive;

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
 * The {@code IdPrimitivePackageFactoryTests} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 8:35:27 AM
 */
public class TensorIdPrimitiveFactoryTests {
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



    public static class TensorFactoryBooleanTensorTest extends IdBooleanTensorTest<String> {
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

        protected IdBooleanTensor<String> getIdTensor(TypeSafeArray<Boolean> data, List<List<String>> ids) {
            IdBooleanTensor<String> tensor = factory.booleanTensor(ids, ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryCharTensorTest extends IdCharTensorTest<String> {
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

        protected IdCharTensor<String> getIdTensor(TypeSafeArray<Character> data, List<List<String>> ids) {
            IdCharTensor<String> tensor = factory.charTensor(ids,ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryByteTensorTest extends IdByteTensorTest<String> {
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

        protected IdByteTensor<String> getIdTensor(TypeSafeArray<Byte> data, List<List<String>> ids) {
            IdByteTensor<String> tensor = factory.byteTensor(ids,ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryShortTensorTest extends IdShortTensorTest<String> {
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

        protected IdShortTensor<String> getIdTensor(TypeSafeArray<Short> data, List<List<String>> ids) {
            IdShortTensor<String> tensor = factory.shortTensor(ids,ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryIntTensorTest extends IdIntTensorTest<String> {
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

        protected IdIntTensor<String> getIdTensor(TypeSafeArray<Integer> data, List<List<String>> ids) {
            IdIntTensor<String> tensor = factory.intTensor(ids,ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryLongTensorTest extends IdLongTensorTest<String> {
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

        protected IdLongTensor<String> getIdTensor(TypeSafeArray<Long> data, List<List<String>> ids) {
            IdLongTensor<String> tensor = factory.longTensor(ids,ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryFloatTensorTest extends IdFloatTensorTest<String> {
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

        protected IdFloatTensor<String> getIdTensor(TypeSafeArray<Float> data, List<List<String>> ids) {
            IdFloatTensor<String> tensor = factory.floatTensor(ids,ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryDoubleTensorTest extends IdDoubleTensorTest<String> {
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

        protected IdDoubleTensor<String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdDoubleTensor<String> tensor = factory.doubleTensor(ids,ArrayUtil.getDimensions(data.getArray()));
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
