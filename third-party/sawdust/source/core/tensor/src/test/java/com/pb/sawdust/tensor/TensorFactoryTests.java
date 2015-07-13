package com.pb.sawdust.tensor;

import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.factory.TensorFactoryTest;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.RandomDeluxe;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.*;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.LinkedList;
import java.util.List;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code TensorPackageFactoryTests} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 8:23:53 AM
 */
public class TensorFactoryTests {
    public static List<Class<? extends TestBase>> TEST_CLASSES = new LinkedList<Class<? extends TestBase>>();
    static {
        TEST_CLASSES.add(TensorFactoryTensorTest.class);
    }
    
    public static int[] randomDimensions(RandomDeluxe random) {
        return randomDimensions(random,random.nextInt(10,12)); //make sure we're above 9
//        return randomDimensions(random,random.nextInt(2,4)); //for quick tests
    }

    public static int[] randomDimensions(RandomDeluxe random, int dimensionCount) {
        int[] dimensions = new int[dimensionCount];
        for (int i : range(dimensions.length))
            dimensions[i] = random.nextInt(1,dimensionCount > 9 ? 4 : 6); //limit size for deeply dimensioned tensors
        return dimensions;
    }

    public static BooleanTypeSafeArray booleanData(RandomDeluxe random) {
        return booleanData(randomDimensions(random),random);
    }

    public static BooleanTypeSafeArray booleanData(int[] dimensions, RandomDeluxe random) {
        BooleanTypeSafeArray array = TypeSafeArrayFactory.booleanTypeSafeArray(dimensions);
        for (int[] i : IterableAbacus.getIterableAbacus(dimensions))
            array.set(randomBoolean(random),i);
        return array;
    }

    public static boolean randomBoolean(RandomDeluxe random) {
        return random.nextBoolean();
    }

    public static CharTypeSafeArray charData(RandomDeluxe random) {
        return charData(randomDimensions(random),random);
    }

    public static CharTypeSafeArray charData(int[] dimensions, RandomDeluxe random) {
        CharTypeSafeArray array = TypeSafeArrayFactory.charTypeSafeArray(dimensions);
        for (int[] i : IterableAbacus.getIterableAbacus(dimensions))
            array.set(randomChar(random),i);
        return array;
    }

    public static char randomChar(RandomDeluxe random) {
        return random.nextAsciiChar();
    }

    public static ByteTypeSafeArray byteData(RandomDeluxe random) {
        return byteData(randomDimensions(random),random);
    }

    public static ByteTypeSafeArray byteData(int[] dimensions, RandomDeluxe random) {
        ByteTypeSafeArray array = TypeSafeArrayFactory.byteTypeSafeArray(dimensions);
        for (int[] i : IterableAbacus.getIterableAbacus(dimensions))
            array.set(randomByte(random),i);
        return array;
    }

    public static byte randomByte(RandomDeluxe random) {
        return random.nextByte();
    }

    public static ShortTypeSafeArray shortData(RandomDeluxe random) {
        return shortData(randomDimensions(random),random);
    }

    public static ShortTypeSafeArray shortData(int[] dimensions, RandomDeluxe random) {
        ShortTypeSafeArray array = TypeSafeArrayFactory.shortTypeSafeArray(dimensions);
        for (int[] i : IterableAbacus.getIterableAbacus(dimensions))
            array.set(randomShort(random),i);
        return array;
    }

    public static short randomShort(RandomDeluxe random) {
        return random.nextShort();
    }

    public static IntTypeSafeArray intData(RandomDeluxe random) {
        return intData(randomDimensions(random),random);
    }

    public static IntTypeSafeArray intData(int[] dimensions, RandomDeluxe random) {
        IntTypeSafeArray array = TypeSafeArrayFactory.intTypeSafeArray(dimensions);
        for (int[] i : IterableAbacus.getIterableAbacus(dimensions))
            array.set(randomInt(random),i);
        return array;
    }

    public static int randomInt(RandomDeluxe random) {
        return random.nextInt();
    }

    public static LongTypeSafeArray longData(RandomDeluxe random) {
        return longData(randomDimensions(random),random);
    }

    public static LongTypeSafeArray longData(int[] dimensions, RandomDeluxe random) {
        LongTypeSafeArray array = TypeSafeArrayFactory.longTypeSafeArray(dimensions);
        for (int[] i : IterableAbacus.getIterableAbacus(dimensions))
            array.set(randomLong(random),i);
        return array;
    }

    public static Long randomLong(RandomDeluxe random) {
        return random.nextLong();
    }

    public static FloatTypeSafeArray floatData(RandomDeluxe random) {
        return floatData(randomDimensions(random),random);
    }

    public static FloatTypeSafeArray floatData(int[] dimensions, RandomDeluxe random) {
        FloatTypeSafeArray array = TypeSafeArrayFactory.floatTypeSafeArray(dimensions);
        for (int[] i : IterableAbacus.getIterableAbacus(dimensions))
            array.set(randomFloat(random),i);
        return array;
    }

    public static float randomFloat(RandomDeluxe random) {
        return random.nextFloat();
    }

    public static DoubleTypeSafeArray doubleData(RandomDeluxe random) {
        return doubleData(randomDimensions(random),random);
    }

    public static DoubleTypeSafeArray doubleData(int[] dimensions, RandomDeluxe random) {
        DoubleTypeSafeArray array = TypeSafeArrayFactory.doubleTypeSafeArray(dimensions);
        for (int[] i : IterableAbacus.getIterableAbacus(dimensions))
            array.setValue(randomDouble(random),i);
        return array;
    }

    public static Double randomDouble(RandomDeluxe random) {
        return random.nextDouble();
    }

    public static TypeSafeArray<Double> doubleObjectData(RandomDeluxe random) {
        return doubleObjectData(randomDimensions(random),random);
    }

    public static TypeSafeArray<Double> doubleObjectData(int[] dimensions, RandomDeluxe random) {
        TypeSafeArray<Double> array = TypeSafeArrayFactory.typeSafeArray(Double.class,dimensions);
        for (int[] i : IterableAbacus.getIterableAbacus(dimensions))
            array.setValue(randomDouble(random),i);
        return array;
    }

    public static class TensorFactoryTensorTest extends TensorTest<Double> {
        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(), TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected Tensor<Double> getTensor(TypeSafeArray<Double> data) {
            Tensor<Double> tensor = factory.tensor(ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected TypeSafeArray<Double> getData() {
            return doubleObjectData(random);
        }

        protected JavaType getJavaType() {
            return JavaType.OBJECT;
        }

        protected Double getRandomElement() {
            return randomDouble(random);
        }
    }
}
