package com.pb.sawdust.tensor.alias.matrix.id;

import com.pb.sawdust.tensor.TensorFactoryTests;
import com.pb.sawdust.tensor.decorators.id.TensorIdFactoryTests;
import com.pb.sawdust.tensor.factory.TensorFactoryTest;
import com.pb.sawdust.tensor.decorators.id.primitive.size.*;
import com.pb.sawdust.tensor.decorators.id.size.IdD2Tensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.*;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code MatrixIdPackageFactoryTests} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 10:42:23 AM
 */
public class TensorMatrixIdFactoryTests {
    public static List<Class<? extends TestBase>> TEST_CLASSES = new LinkedList<Class<? extends TestBase>>();
    static {
        TEST_CLASSES.add(TensorFactoryIdMatrixTest.class);
        TEST_CLASSES.add(TensorFactoryIdBooleanMatrixTest.class);
        TEST_CLASSES.add(TensorFactoryIdCharMatrixTest.class);
        TEST_CLASSES.add(TensorFactoryIdByteMatrixTest.class);
        TEST_CLASSES.add(TensorFactoryIdShortMatrixTest.class);
        TEST_CLASSES.add(TensorFactoryIdIntMatrixTest.class);
        TEST_CLASSES.add(TensorFactoryIdLongMatrixTest.class);
        TEST_CLASSES.add(TensorFactoryIdFloatMatrixTest.class);
        TEST_CLASSES.add(TensorFactoryIdDoubleMatrixTest.class);
    }

    public static class TensorFactoryIdMatrixTest extends IdMatrixTest<Double,String> {
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

        protected IdD2Tensor<Double,String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdD2Tensor<Double,String> tensor = (IdD2Tensor<Double,String>) factory.<Double,String>tensor(ids, ArrayUtil.getDimensions(data.getArray()));
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

    public static class TensorFactoryIdBooleanMatrixTest extends IdBooleanMatrixTest<String> {

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

    public static class TensorFactoryIdCharMatrixTest extends IdCharMatrixTest<String> {

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

    public static class TensorFactoryIdByteMatrixTest extends IdByteMatrixTest<String> {

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

    public static class TensorFactoryIdShortMatrixTest extends IdShortMatrixTest<String> {

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

    public static class TensorFactoryIdIntMatrixTest extends IdIntMatrixTest<String> {

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

    public static class TensorFactoryIdLongMatrixTest extends IdLongMatrixTest<String> {

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

    public static class TensorFactoryIdFloatMatrixTest extends IdFloatMatrixTest<String> {

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

    public static class TensorFactoryIdDoubleMatrixTest extends IdDoubleMatrixTest<String> {

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
}
