package com.pb.sawdust.calculator.tensor.la.mm;

import com.pb.sawdust.calculator.tensor.TensorTestUtil;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.alias.vector.primitive.*;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code VectorVectorMultiplicationTest} ...
 *
 * @author crf <br/>
 *         Started Dec 17, 2010 2:51:06 PM
 */
public abstract class VectorVectorMultiplicationTest extends TestBase {

    protected VectorVectorMultiplication mm;
    protected TensorFactory factory;
    protected TensorTestUtil ttUtil;

    abstract protected VectorVectorMultiplication getVectorVectorMultiplication(TensorFactory factory);

    protected TensorFactory getFactory() {
        return ArrayTensor.getFactory();
    }

    @Before
    public void beforeTest() {
        factory = getFactory();
        mm = getVectorVectorMultiplication(factory);
        ttUtil  = new TensorTestUtil(factory,random);
        ttUtil.setPrintFailedMatrices(true);
    }

    private int[][] getDims() {
        int maxSize = 5; //if too big, then overflows sometimes happen - not deterministic
        int match = random.nextInt(1,maxSize);                                           
        int[] d1 = new int[] {match};
        int[] d2 = new int[] {match};
        return new int[][] {d1,d2};
    }

    protected void testByteByte(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        ByteVector t1 = (ByteVector) ttUtil.getRandomTensor(JavaType.BYTE,dims[0]);
        ByteVector t2 = (ByteVector) ttUtil.getRandomTensor(JavaType.BYTE,dims[1]);
        byte check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedByteScalar(check),mm.multiply(t1,t2));
    }

    protected void testByteShort(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        ByteVector t1 = (ByteVector) ttUtil.getRandomTensor(JavaType.BYTE,dims[0]);
        ShortVector t2 = (ShortVector) ttUtil.getRandomTensor(JavaType.SHORT,dims[1]);
        short check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedShortScalar(check),mm.multiply(t1,t2));
    }

    protected void testShortByte(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        ShortVector t1 = (ShortVector) ttUtil.getRandomTensor(JavaType.SHORT,dims[0]);
        ByteVector t2 = (ByteVector) ttUtil.getRandomTensor(JavaType.BYTE,dims[1]);
        short check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedShortScalar(check),mm.multiply(t1,t2));
    }

    protected void testByteInt(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        ByteVector t1 = (ByteVector) ttUtil.getRandomTensor(JavaType.BYTE,dims[0]);
        IntVector t2 = (IntVector) ttUtil.getRandomTensor(JavaType.INT,dims[1]);
        int check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedIntScalar(check),mm.multiply(t1,t2));
    }

    protected void testIntByte(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        IntVector t1 = (IntVector) ttUtil.getRandomTensor(JavaType.INT,dims[0]);
        ByteVector t2 = (ByteVector) ttUtil.getRandomTensor(JavaType.BYTE,dims[1]);
        int check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedIntScalar(check),mm.multiply(t1,t2));
    }

    protected void testByteLong(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        ByteVector t1 = (ByteVector) ttUtil.getRandomTensor(JavaType.BYTE,dims[0]);
        LongVector t2 = (LongVector) ttUtil.getRandomTensor(JavaType.LONG,dims[1]);
        long check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedLongScalar(check),mm.multiply(t1,t2));
    }

    protected void testLongByte(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        LongVector t1 = (LongVector) ttUtil.getRandomTensor(JavaType.LONG,dims[0]);
        ByteVector t2 = (ByteVector) ttUtil.getRandomTensor(JavaType.BYTE,dims[1]);
        long check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedLongScalar(check),mm.multiply(t1,t2));
    }

    protected void testByteFloat(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        ByteVector t1 = (ByteVector) ttUtil.getRandomTensor(JavaType.BYTE,dims[0]);
        FloatVector t2 = (FloatVector) ttUtil.getRandomTensor(JavaType.FLOAT,dims[1]);
        float check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedFloatScalar(check),mm.multiply(t1,t2));
    }

    protected void testFloatByte(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        FloatVector t1 = (FloatVector) ttUtil.getRandomTensor(JavaType.FLOAT,dims[0]);
        ByteVector t2 = (ByteVector) ttUtil.getRandomTensor(JavaType.BYTE,dims[1]);
        float check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedFloatScalar(check),mm.multiply(t1,t2));
    }

    protected void testByteDouble(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        ByteVector t1 = (ByteVector) ttUtil.getRandomTensor(JavaType.BYTE,dims[0]);
        DoubleVector t2 = (DoubleVector) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[1]);
        double check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedDoubleScalar(check),mm.multiply(t1,t2));
    }

    protected void testDoubleByte(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        DoubleVector t1 = (DoubleVector) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[0]);
        ByteVector t2 = (ByteVector) ttUtil.getRandomTensor(JavaType.BYTE,dims[1]);
        double check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedDoubleScalar(check),mm.multiply(t1,t2));
    }

    protected void testShortShort(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        ShortVector t1 = (ShortVector) ttUtil.getRandomTensor(JavaType.SHORT,dims[0]);
        ShortVector t2 = (ShortVector) ttUtil.getRandomTensor(JavaType.SHORT,dims[1]);
        short check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedShortScalar(check),mm.multiply(t1,t2));
    }

    protected void testShortInt(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        ShortVector t1 = (ShortVector) ttUtil.getRandomTensor(JavaType.SHORT,dims[0]);
        IntVector t2 = (IntVector) ttUtil.getRandomTensor(JavaType.INT,dims[1]);
        int check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedIntScalar(check),mm.multiply(t1,t2));
    }

    protected void testIntShort(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        IntVector t1 = (IntVector) ttUtil.getRandomTensor(JavaType.INT,dims[0]);
        ShortVector t2 = (ShortVector) ttUtil.getRandomTensor(JavaType.SHORT,dims[1]);
        int check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedIntScalar(check),mm.multiply(t1,t2));
    }

    protected void testShortLong(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        ShortVector t1 = (ShortVector) ttUtil.getRandomTensor(JavaType.SHORT,dims[0]);
        LongVector t2 = (LongVector) ttUtil.getRandomTensor(JavaType.LONG,dims[1]);
        long check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedLongScalar(check),mm.multiply(t1,t2));
    }

    protected void testLongShort(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        LongVector t1 = (LongVector) ttUtil.getRandomTensor(JavaType.LONG,dims[0]);
        ShortVector t2 = (ShortVector) ttUtil.getRandomTensor(JavaType.SHORT,dims[1]);
        long check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedLongScalar(check),mm.multiply(t1,t2));
    }

    protected void testShortFloat(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        ShortVector t1 = (ShortVector) ttUtil.getRandomTensor(JavaType.SHORT,dims[0]);
        FloatVector t2 = (FloatVector) ttUtil.getRandomTensor(JavaType.FLOAT,dims[1]);
        float check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedFloatScalar(check),mm.multiply(t1,t2));
    }

    protected void testFloatShort(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        FloatVector t1 = (FloatVector) ttUtil.getRandomTensor(JavaType.FLOAT,dims[0]);
        ShortVector t2 = (ShortVector) ttUtil.getRandomTensor(JavaType.SHORT,dims[1]);
        float check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedFloatScalar(check),mm.multiply(t1,t2));
    }

    protected void testShortDouble(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        ShortVector t1 = (ShortVector) ttUtil.getRandomTensor(JavaType.SHORT,dims[0]);
        DoubleVector t2 = (DoubleVector) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[1]);
        double check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedDoubleScalar(check),mm.multiply(t1,t2));
    }

    protected void testDoubleShort(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        DoubleVector t1 = (DoubleVector) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[0]);
        ShortVector t2 = (ShortVector) ttUtil.getRandomTensor(JavaType.SHORT,dims[1]);
        double check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedDoubleScalar(check),mm.multiply(t1,t2));
    }

    protected void testIntInt(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        IntVector t1 = (IntVector) ttUtil.getRandomTensor(JavaType.INT,dims[0]);
        IntVector t2 = (IntVector) ttUtil.getRandomTensor(JavaType.INT,dims[1]);
        int check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedIntScalar(check),mm.multiply(t1,t2));
    }

    protected void testIntLong(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        IntVector t1 = (IntVector) ttUtil.getRandomTensor(JavaType.INT,dims[0]);
        LongVector t2 = (LongVector) ttUtil.getRandomTensor(JavaType.LONG,dims[1]);
        long check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedLongScalar(check),mm.multiply(t1,t2));
    }

    protected void testLongInt(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        LongVector t1 = (LongVector) ttUtil.getRandomTensor(JavaType.LONG,dims[0]);
        IntVector t2 = (IntVector) ttUtil.getRandomTensor(JavaType.INT,dims[1]);
        long check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedLongScalar(check),mm.multiply(t1,t2));
    }

    protected void testIntFloat(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        IntVector t1 = (IntVector) ttUtil.getRandomTensor(JavaType.INT,dims[0]);
        FloatVector t2 = (FloatVector) ttUtil.getRandomTensor(JavaType.FLOAT,dims[1]);
        float check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedFloatScalar(check),mm.multiply(t1,t2));
    }

    protected void testFloatInt(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        FloatVector t1 = (FloatVector) ttUtil.getRandomTensor(JavaType.FLOAT,dims[0]);
        IntVector t2 = (IntVector) ttUtil.getRandomTensor(JavaType.INT,dims[1]);
        float check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedFloatScalar(check),mm.multiply(t1,t2));
    }

    protected void testIntDouble(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        IntVector t1 = (IntVector) ttUtil.getRandomTensor(JavaType.INT,dims[0]);
        DoubleVector t2 = (DoubleVector) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[1]);
        double check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedDoubleScalar(check),mm.multiply(t1,t2));
    }

    protected void testDoubleInt(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        DoubleVector t1 = (DoubleVector) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[0]);
        IntVector t2 = (IntVector) ttUtil.getRandomTensor(JavaType.INT,dims[1]);
        double check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedDoubleScalar(check),mm.multiply(t1,t2));
    }

    protected void testLongLong(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        LongVector t1 = (LongVector) ttUtil.getRandomTensor(JavaType.LONG,dims[0]);
        LongVector t2 = (LongVector) ttUtil.getRandomTensor(JavaType.LONG,dims[1]);
        long check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedLongScalar(check),mm.multiply(t1,t2));
    }

    protected void testLongFloat(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        LongVector t1 = (LongVector) ttUtil.getRandomTensor(JavaType.LONG,dims[0]);
        FloatVector t2 = (FloatVector) ttUtil.getRandomTensor(JavaType.FLOAT,dims[1]);
        float check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedFloatScalar(check),mm.multiply(t1,t2));
    }

    protected void testFloatLong(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        FloatVector t1 = (FloatVector) ttUtil.getRandomTensor(JavaType.FLOAT,dims[0]);
        LongVector t2 = (LongVector) ttUtil.getRandomTensor(JavaType.LONG,dims[1]);
        float check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedFloatScalar(check),mm.multiply(t1,t2));
    }

    protected void testLongDouble(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        LongVector t1 = (LongVector) ttUtil.getRandomTensor(JavaType.LONG,dims[0]);
        DoubleVector t2 = (DoubleVector) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[1]);
        double check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedDoubleScalar(check),mm.multiply(t1,t2));
    }

    protected void testDoubleLong(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        DoubleVector t1 = (DoubleVector) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[0]);
        LongVector t2 = (LongVector) ttUtil.getRandomTensor(JavaType.LONG,dims[1]);
        double check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedDoubleScalar(check),mm.multiply(t1,t2));
    }

    protected void testFloatFloat(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        FloatVector t1 = (FloatVector) ttUtil.getRandomTensor(JavaType.FLOAT,dims[0]);
        FloatVector t2 = (FloatVector) ttUtil.getRandomTensor(JavaType.FLOAT,dims[1]);
        float check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedFloatScalar(check),mm.multiply(t1,t2));
    }

    protected void testFloatDouble(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        FloatVector t1 = (FloatVector) ttUtil.getRandomTensor(JavaType.FLOAT,dims[0]);
        DoubleVector t2 = (DoubleVector) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[1]);
        double check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedDoubleScalar(check),mm.multiply(t1,t2));
    }

    protected void testDoubleFloat(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        DoubleVector t1 = (DoubleVector) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[0]);
        FloatVector t2 = (FloatVector) ttUtil.getRandomTensor(JavaType.FLOAT,dims[1]);
        double check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedDoubleScalar(check),mm.multiply(t1,t2));
    }

    protected void testDoubleDouble(boolean misaligned) {
        int[][] dims = getDims();
        if (misaligned)
            dims[1][0]++;
        DoubleVector t1 = (DoubleVector) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[0]);
        DoubleVector t2 = (DoubleVector) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[1]);
        double check = 0;
        for (int i : range(dims[0][0]))
            check += t1.getCell(i)*t2.getCell(i);
        ttUtil.assertTensorEquals(factory.initializedDoubleScalar(check),mm.multiply(t1,t2));
    }

    @Test
    public void testByteByte() {
        testByteByte(false);
    }

    @Test
    public void testByteShort() {
        testByteShort(false);
    }

    @Test
    public void testShortByte() {
        testShortByte(false);
    }

    @Test
    public void testByteInt() {
        testByteInt(false);
    }

    @Test
    public void testIntByte() {
        testIntByte(false);
    }

    @Test
    public void testByteLong() {
        testByteLong(false);
    }

    @Test
    public void testLongByte() {
        testLongByte(false);
    }

    @Test
    public void testByteFloat() {
        testByteFloat(false);
    }
    
    @Test
    public void testFloatByte() {
        testFloatByte(false);
    }

    @Test
    public void testByteDouble() {
        testByteDouble(false);
    }

    @Test
    public void testDoubleByte() {
        testDoubleByte(false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteByteMisaligned() {
        testByteByte(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteShortMisaligned() {
        testByteShort(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortByteMisaligned() {
        testShortByte(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteIntMisaligned() {
        testByteInt(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntByteMisaligned() {
        testIntByte(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteLongMisaligned() {
        testByteLong(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongByteMisaligned() {
        testLongByte(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteFloatMisaligned() {
        testByteFloat(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatByteMisaligned() {
        testFloatByte(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteDoubleMisaligned() {
        testByteDouble(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleByteMisaligned() {
        testDoubleByte(true);
    }

    @Test
    public void testShortShort() {
        testShortShort(false);
    }

    @Test
    public void testShortInt() {
        testShortInt(false);
    }

    @Test
    public void testIntShort() {
        testIntShort(false);
    }

    @Test
    public void testShortLong() {
        testShortLong(false);
    }

    @Test
    public void testLongShort() {
        testLongShort(false);
    }

    @Test
    public void testShortFloat() {
        testShortFloat(false);
    }

    @Test
    public void testFloatShort() {
        testFloatShort(false);
    }

    @Test
    public void testShortDouble() {
        testShortDouble(false);
    }

    @Test
    public void testDoubleShort() {
        testDoubleShort(false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortShortMisaligned() {
        testShortShort(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortIntMisaligned() {
        testShortInt(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntShortMisaligned() {
        testIntShort(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortLongMisaligned() {
        testShortLong(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongShortMisaligned() {
        testLongShort(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortFloatMisaligned() {
        testShortFloat(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatShortMisaligned() {
        testFloatShort(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortDoubleMisaligned() {
        testShortDouble(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleShortMisaligned() {
        testDoubleShort(true);
    }

    @Test
    public void testIntInt() {
        testIntInt(false);
    }

    @Test
    public void testIntLong() {
        testIntLong(false);
    }


    @Test
    public void testLongInt() {
        testLongInt(false);
    }

    @Test
    public void testIntFloat() {
        testIntFloat(false);
    }

    @Test
    public void testFloatInt() {
        testFloatInt(false);
    }

    @Test
    public void testIntDouble() {
        testIntDouble(false);
    }

    @Test
    public void testDoubleInt() {
        testDoubleInt(false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntIntMisaligned() {
        testIntInt(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntLongMisaligned() {
        testIntLong(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongIntMisaligned() {
        testLongInt(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntFloatMisaligned() {
        testIntFloat(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatIntMisaligned() {
        testFloatInt(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntDoubleMisaligned() {
        testIntDouble(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleIntMisaligned() {
        testDoubleInt(true);
    }

    @Test
    public void testLongLong() {
        testLongLong(false);
    }

    @Test
    public void testLongFloat() {
        testLongFloat(false);
    }

    @Test
    public void testFloatLong() {
        testFloatLong(false);
    }

    @Test
    public void testLongDouble() {
        testLongDouble(false);
    }

    @Test
    public void testDoubleLong() {
        testDoubleLong(false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongLongMisaligned() {
        testLongLong(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongFloatMisaligned() {
        testLongFloat(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatLongMisaligned() {
        testFloatLong(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongDoubleMisaligned() {
        testLongDouble(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleLongMisaligned() {
        testDoubleLong(true);
    }

    @Test
    public void testFloatFloat() {
        testFloatFloat(false);
    }

    @Test
    public void testFloatDouble() {
        testFloatDouble(false);
    }

    @Test
    public void testDoubleFloat() {
        testDoubleFloat(false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatFloatMisaligned() {
        testFloatFloat(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatDoubleMisaligned() {
        testFloatDouble(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleFloatMisaligned() {
        testDoubleFloat(true);
    }

    @Test
    public void testDoubleDouble() {
        testDoubleDouble(false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleDoubleMisaligned() {
        testDoubleDouble(true);
    }

}
