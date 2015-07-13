package com.pb.sawdust.calculator.tensor.la.mm;

import com.pb.sawdust.calculator.tensor.TensorTestUtil;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.alias.matrix.primitive.*;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code MatrixMatrixMultiplicationTest} ...
 *
 * @author crf <br/>
 *         Started Dec 18, 2010 12:52:08 AM
 */
public abstract class MatrixMatrixMultiplicationTest extends TestBase {

    public static enum MultiplyType {
        VECTOR_VECTOR,
        MATRIX_VECTOR,
        VECTOR_MATRIX,
        MATRIX_MATRIX
    }
    public static final String MULTIPLY_TYPE_KEY = "MultiplyType";

    protected MatrixMatrixMultiplication mm;
    protected TensorFactory factory;
    protected TensorTestUtil ttUtil;
    protected MultiplyType type;

    abstract protected MatrixMatrixMultiplication getMatrixMatrixMultiplication(TensorFactory factory);

    protected TensorFactory getFactory() {
        return ArrayTensor.getFactory();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> contexts = new LinkedList<Map<String,Object>>();
        for (MultiplyType t : MultiplyType.values())
            contexts.add(buildContext(MULTIPLY_TYPE_KEY,t));
        addClassRunContext(this.getClass(),contexts);
        return super.getAdditionalTestClasses();
    }

    @Before
    public void beforeTest() {
        factory = getFactory();
        mm = getMatrixMatrixMultiplication(factory);
        ttUtil  = new TensorTestUtil(factory,random);
        ttUtil.setPrintFailedMatrices(true);
        try {
            type = (MultiplyType) getTestData(MULTIPLY_TYPE_KEY);
        } catch (NullPointerException e) {
            type = MultiplyType.MATRIX_MATRIX;
        }
    }

    private int[][] getDims(boolean transpose1, boolean transpose2) {
        int[] d1;
        int[] d2;
        int maxSize = 5; //if too big, then overflows sometimes happen - not deterministic
        int match = random.nextInt(1,maxSize);
        switch (type) {
            case VECTOR_VECTOR : {
                d1 = new int[] {transpose1 ? match : 1,transpose1 ? 1 : match};
                d2 = new int[] {transpose2 ? 1 : match,transpose2 ? match : 1};
                break;
            }
            case MATRIX_VECTOR : {
                int d1s = random.nextInt(1,maxSize);
                d1 = new int[] {transpose1 ? match : d1s,transpose1 ? d1s : match};
                d2 = new int[] {transpose2 ? 1 : match,transpose2 ? match : 1};
                break;
            }
            case VECTOR_MATRIX : {
                int d2s = random.nextInt(1,maxSize);
                d1 = new int[] {transpose1 ? match : 1,transpose1 ? 1 : match};
                d2 = new int[] {transpose2 ? d2s : match,transpose2 ? match : d2s};
                break;
            }
            case MATRIX_MATRIX : {
                int d1s = random.nextInt(1,maxSize);
                int d2s = random.nextInt(1,maxSize);
                d1 = new int[] {transpose1 ? match : d1s,transpose1 ? d1s : match};
                d2 = new int[] {transpose2 ? d2s : match,transpose2 ? match : d2s};
                break;
            }
            default : throw new IllegalStateException("Shouldn't be here.");
        }
        return new int[][] {d1,d2};
    }

    protected void testByteByte(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        ByteMatrix t1 = (ByteMatrix) ttUtil.getRandomTensor(JavaType.BYTE,dims[0]);
        ByteMatrix t2 = (ByteMatrix) ttUtil.getRandomTensor(JavaType.BYTE,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        ByteMatrix check = factory.byteMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                byte result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = (byte) (result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j));
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testByteShort(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        ByteMatrix t1 = (ByteMatrix) ttUtil.getRandomTensor(JavaType.BYTE,dims[0]);
        ShortMatrix t2 = (ShortMatrix) ttUtil.getRandomTensor(JavaType.SHORT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        ShortMatrix check = factory.shortMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                short result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = (short) (result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j));
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testShortByte(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        ShortMatrix t1 = (ShortMatrix) ttUtil.getRandomTensor(JavaType.SHORT,dims[0]);
        ByteMatrix t2 = (ByteMatrix) ttUtil.getRandomTensor(JavaType.BYTE,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        ShortMatrix check = factory.shortMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                short result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = (short) (result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j));
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testByteInt(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        ByteMatrix t1 = (ByteMatrix) ttUtil.getRandomTensor(JavaType.BYTE,dims[0]);
        IntMatrix t2 = (IntMatrix) ttUtil.getRandomTensor(JavaType.INT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        IntMatrix check = factory.intMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                int result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testIntByte(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        IntMatrix t1 = (IntMatrix) ttUtil.getRandomTensor(JavaType.INT,dims[0]);
        ByteMatrix t2 = (ByteMatrix) ttUtil.getRandomTensor(JavaType.BYTE,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        IntMatrix check = factory.intMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                int result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testByteLong(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        ByteMatrix t1 = (ByteMatrix) ttUtil.getRandomTensor(JavaType.BYTE,dims[0]);
        LongMatrix t2 = (LongMatrix) ttUtil.getRandomTensor(JavaType.LONG,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        LongMatrix check = factory.longMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                long result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testLongByte(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        LongMatrix t1 = (LongMatrix) ttUtil.getRandomTensor(JavaType.LONG,dims[0]);
        ByteMatrix t2 = (ByteMatrix) ttUtil.getRandomTensor(JavaType.BYTE,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        LongMatrix check = factory.longMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                long result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testByteFloat(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        ByteMatrix t1 = (ByteMatrix) ttUtil.getRandomTensor(JavaType.BYTE,dims[0]);
        FloatMatrix t2 = (FloatMatrix) ttUtil.getRandomTensor(JavaType.FLOAT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        FloatMatrix check = factory.floatMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                float result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testFloatByte(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        FloatMatrix t1 = (FloatMatrix) ttUtil.getRandomTensor(JavaType.FLOAT,dims[0]);
        ByteMatrix t2 = (ByteMatrix) ttUtil.getRandomTensor(JavaType.BYTE,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        FloatMatrix check = factory.floatMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                float result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testByteDouble(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        ByteMatrix t1 = (ByteMatrix) ttUtil.getRandomTensor(JavaType.BYTE,dims[0]);
        DoubleMatrix t2 = (DoubleMatrix) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        DoubleMatrix check = factory.doubleMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                double result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testDoubleByte(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        DoubleMatrix t1 = (DoubleMatrix) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[0]);
        ByteMatrix t2 = (ByteMatrix) ttUtil.getRandomTensor(JavaType.BYTE,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        DoubleMatrix check = factory.doubleMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                double result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testShortShort(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        ShortMatrix t1 = (ShortMatrix) ttUtil.getRandomTensor(JavaType.SHORT,dims[0]);
        ShortMatrix t2 = (ShortMatrix) ttUtil.getRandomTensor(JavaType.SHORT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        ShortMatrix check = factory.shortMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                short result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = (short) (result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j));
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testShortInt(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        ShortMatrix t1 = (ShortMatrix) ttUtil.getRandomTensor(JavaType.SHORT,dims[0]);
        IntMatrix t2 = (IntMatrix) ttUtil.getRandomTensor(JavaType.INT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        IntMatrix check = factory.intMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                int result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testIntShort(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        IntMatrix t1 = (IntMatrix) ttUtil.getRandomTensor(JavaType.INT,dims[0]);
        ShortMatrix t2 = (ShortMatrix) ttUtil.getRandomTensor(JavaType.SHORT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        IntMatrix check = factory.intMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                int result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testShortLong(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        ShortMatrix t1 = (ShortMatrix) ttUtil.getRandomTensor(JavaType.SHORT,dims[0]);
        LongMatrix t2 = (LongMatrix) ttUtil.getRandomTensor(JavaType.LONG,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        LongMatrix check = factory.longMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                long result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testLongShort(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        LongMatrix t1 = (LongMatrix) ttUtil.getRandomTensor(JavaType.LONG,dims[0]);
        ShortMatrix t2 = (ShortMatrix) ttUtil.getRandomTensor(JavaType.SHORT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        LongMatrix check = factory.longMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                long result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testShortFloat(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        ShortMatrix t1 = (ShortMatrix) ttUtil.getRandomTensor(JavaType.SHORT,dims[0]);
        FloatMatrix t2 = (FloatMatrix) ttUtil.getRandomTensor(JavaType.FLOAT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        FloatMatrix check = factory.floatMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                float result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testFloatShort(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        FloatMatrix t1 = (FloatMatrix) ttUtil.getRandomTensor(JavaType.FLOAT,dims[0]);
        ShortMatrix t2 = (ShortMatrix) ttUtil.getRandomTensor(JavaType.SHORT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        FloatMatrix check = factory.floatMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                float result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testShortDouble(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        ShortMatrix t1 = (ShortMatrix) ttUtil.getRandomTensor(JavaType.SHORT,dims[0]);
        DoubleMatrix t2 = (DoubleMatrix) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        DoubleMatrix check = factory.doubleMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                double result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testDoubleShort(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        DoubleMatrix t1 = (DoubleMatrix) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[0]);
        ShortMatrix t2 = (ShortMatrix) ttUtil.getRandomTensor(JavaType.SHORT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        DoubleMatrix check = factory.doubleMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                double result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testIntInt(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        IntMatrix t1 = (IntMatrix) ttUtil.getRandomTensor(JavaType.INT,dims[0]);
        IntMatrix t2 = (IntMatrix) ttUtil.getRandomTensor(JavaType.INT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        IntMatrix check = factory.intMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                int result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testIntLong(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        IntMatrix t1 = (IntMatrix) ttUtil.getRandomTensor(JavaType.INT,dims[0]);
        LongMatrix t2 = (LongMatrix) ttUtil.getRandomTensor(JavaType.LONG,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        LongMatrix check = factory.longMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                long result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testLongInt(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        LongMatrix t1 = (LongMatrix) ttUtil.getRandomTensor(JavaType.LONG,dims[0]);
        IntMatrix t2 = (IntMatrix) ttUtil.getRandomTensor(JavaType.INT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        LongMatrix check = factory.longMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                long result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testIntFloat(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        IntMatrix t1 = (IntMatrix) ttUtil.getRandomTensor(JavaType.INT,dims[0]);
        FloatMatrix t2 = (FloatMatrix) ttUtil.getRandomTensor(JavaType.FLOAT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        FloatMatrix check = factory.floatMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                float result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testFloatInt(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        FloatMatrix t1 = (FloatMatrix) ttUtil.getRandomTensor(JavaType.FLOAT,dims[0]);
        IntMatrix t2 = (IntMatrix) ttUtil.getRandomTensor(JavaType.INT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        FloatMatrix check = factory.floatMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                float result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testIntDouble(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        IntMatrix t1 = (IntMatrix) ttUtil.getRandomTensor(JavaType.INT,dims[0]);
        DoubleMatrix t2 = (DoubleMatrix) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        DoubleMatrix check = factory.doubleMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                double result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testDoubleInt(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        DoubleMatrix t1 = (DoubleMatrix) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[0]);
        IntMatrix t2 = (IntMatrix) ttUtil.getRandomTensor(JavaType.INT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        DoubleMatrix check = factory.doubleMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                double result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testLongLong(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        LongMatrix t1 = (LongMatrix) ttUtil.getRandomTensor(JavaType.LONG,dims[0]);
        LongMatrix t2 = (LongMatrix) ttUtil.getRandomTensor(JavaType.LONG,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        LongMatrix check = factory.longMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                long result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testLongFloat(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        LongMatrix t1 = (LongMatrix) ttUtil.getRandomTensor(JavaType.LONG,dims[0]);
        FloatMatrix t2 = (FloatMatrix) ttUtil.getRandomTensor(JavaType.FLOAT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        FloatMatrix check = factory.floatMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                float result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testFloatLong(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        FloatMatrix t1 = (FloatMatrix) ttUtil.getRandomTensor(JavaType.FLOAT,dims[0]);
        LongMatrix t2 = (LongMatrix) ttUtil.getRandomTensor(JavaType.LONG,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        FloatMatrix check = factory.floatMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                float result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testLongDouble(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        LongMatrix t1 = (LongMatrix) ttUtil.getRandomTensor(JavaType.LONG,dims[0]);
        DoubleMatrix t2 = (DoubleMatrix) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        DoubleMatrix check = factory.doubleMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                double result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testDoubleLong(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        DoubleMatrix t1 = (DoubleMatrix) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[0]);
        LongMatrix t2 = (LongMatrix) ttUtil.getRandomTensor(JavaType.LONG,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        DoubleMatrix check = factory.doubleMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                double result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testFloatFloat(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        FloatMatrix t1 = (FloatMatrix) ttUtil.getRandomTensor(JavaType.FLOAT,dims[0]);
        FloatMatrix t2 = (FloatMatrix) ttUtil.getRandomTensor(JavaType.FLOAT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        FloatMatrix check = factory.floatMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                float result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testFloatDouble(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        FloatMatrix t1 = (FloatMatrix) ttUtil.getRandomTensor(JavaType.FLOAT,dims[0]);
        DoubleMatrix t2 = (DoubleMatrix) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        DoubleMatrix check = factory.doubleMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                double result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testDoubleFloat(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        DoubleMatrix t1 = (DoubleMatrix) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[0]);
        FloatMatrix t2 = (FloatMatrix) ttUtil.getRandomTensor(JavaType.FLOAT,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        DoubleMatrix check = factory.doubleMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                double result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    protected void testDoubleDouble(boolean tr1, boolean tr2, boolean misaligned, boolean defaultArgs) {
        int[][] dims = getDims(tr1,tr2);
        if (misaligned)
            dims[1][tr2 ? 1 : 0]++;
        DoubleMatrix t1 = (DoubleMatrix) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[0]);
        DoubleMatrix t2 = (DoubleMatrix) ttUtil.getRandomTensor(JavaType.DOUBLE,dims[1]);
        int[] output = {tr1 ? dims[0][1] : dims[0][0],tr2 ? dims[1][0] : dims[1][1]};
        DoubleMatrix check = factory.doubleMatrix(output[0],output[1]);
        for (int i : range(output[0])) {
            for (int j : range(output[1])) {
                double result = 0;
                for (int k : range(tr1 ? dims[0][0] : dims[0][1])) {
                    result = result + t1.getCell(tr1 ? k : i,tr1 ? i : k)*t2.getCell(tr2 ? j : k,tr2 ? k : j);
                }
                check.setCell(result,i,j);
            }
        }
        if (!defaultArgs)
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2,tr1,tr2));
        else
            ttUtil.assertTensorEquals(check,mm.multiply(t1,t2));
    }

    @Test
    public void testByteByte() {
        testByteByte(false,false,false,false);
    }

    @Test
    public void testByteByteT1() {
        testByteByte(true,false,false,false);
    }

    @Test
    public void testByteByteT2() {
        testByteByte(false,true,false,false);
    }

    @Test
    public void testByteByteT1T2() {
        testByteByte(true,true,false,false);
    }

    @Test
    public void testByteShort() {
        testByteShort(false,false,false,false);
    }

    @Test
    public void testByteShortT1() {
        testByteShort(true,false,false,false);
    }

    @Test
    public void testByteShortT2() {
        testByteShort(false,true,false,false);
    }

    @Test
    public void testByteShortT1T2() {
        testByteShort(true,true,false,false);
    }

    @Test
    public void testShortByte() {
        testShortByte(false,false,false,false);
    }

    @Test
    public void testShortByteT1() {
        testShortByte(true,false,false,false);
    }

    @Test
    public void testShortByteT2() {
        testShortByte(false,true,false,false);
    }

    @Test
    public void testShortByteT1T2() {
        testShortByte(true,true,false,false);
    }

    @Test
    public void testByteInt() {
        testByteInt(false,false,false,false);
    }

    @Test
    public void testByteIntT1() {
        testByteInt(true,false,false,false);
    }

    @Test
    public void testByteIntT2() {
        testByteInt(false,true,false,false);
    }

    @Test
    public void testByteIntT1T2() {
        testByteInt(true,true,false,false);
    }

    @Test
    public void testIntByte() {
        testIntByte(false,false,false,false);
    }

    @Test
    public void testIntByteT1() {
        testIntByte(true,false,false,false);
    }

    @Test
    public void testIntByteT2() {
        testIntByte(false,true,false,false);
    }

    @Test
    public void testIntByteT1T2() {
        testIntByte(true,true,false,false);
    }

    @Test
    public void testByteLong() {
        testByteLong(false,false,false,false);
    }

    @Test
    public void testByteLongT1() {
        testByteLong(true,false,false,false);
    }

    @Test
    public void testByteLongT2() {
        testByteLong(false,true,false,false);
    }

    @Test
    public void testByteLongT1T2() {
        testByteLong(true,true,false,false);
    }

    @Test
    public void testLongByte() {
        testLongByte(false,false,false,false);
    }

    @Test
    public void testLongByteT1() {
        testLongByte(true,false,false,false);
    }

    @Test
    public void testLongByteT2() {
        testLongByte(false,true,false,false);
    }

    @Test
    public void testLongByteT1T2() {
        testLongByte(true,true,false,false);
    }

    @Test
    public void testByteFloat() {
        testByteFloat(false,false,false,false);
    }

    @Test
    public void testByteFloatT1() {
        testByteFloat(true,false,false,false);
    }

    @Test
    public void testByteFloatT2() {
        testByteFloat(false,true,false,false);
    }

    @Test
    public void testByteFloatT1T2() {
        testByteFloat(true,true,false,false);
    }

    @Test
    public void testFloatByte() {
        testFloatByte(false,false,false,false);
    }

    @Test
    public void testFloatByteT1() {
        testFloatByte(true,false,false,false);
    }

    @Test
    public void testFloatByteT2() {
        testFloatByte(false,true,false,false);
    }

    @Test
    public void testFloatByteT1T2() {
        testFloatByte(true,true,false,false);
    }

    @Test
    public void testByteDouble() {
        testByteDouble(false,false,false,false);
    }

    @Test
    public void testByteDoubleT1() {
        testByteDouble(true,false,false,false);
    }

    @Test
    public void testByteDoubleT2() {
        testByteDouble(false,true,false,false);
    }

    @Test
    public void testByteDoubleT1T2() {
        testByteDouble(true,true,false,false);
    }

    @Test
    public void testDoubleByte() {
        testDoubleByte(false,false,false,false);
    }

    @Test
    public void testDoubleByteT1() {
        testDoubleByte(true,false,false,false);
    }

    @Test
    public void testDoubleByteT2() {
        testDoubleByte(false,true,false,false);
    }

    @Test
    public void testDoubleByteT1T2() {
        testDoubleByte(true,true,false,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteByteMisaligned() {
        testByteByte(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteByteT1Misaligned() {
        testByteByte(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteByteT2Misaligned() {
        testByteByte(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteByteT1T2Misaligned() {
        testByteByte(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteShortMisaligned() {
        testByteShort(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteShortT1Misaligned() {
        testByteShort(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteShortT2Misaligned() {
        testByteShort(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteShortT1T2Misaligned() {
        testByteShort(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortByteMisaligned() {
        testShortByte(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortByteT1Misaligned() {
        testShortByte(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortByteT2Misaligned() {
        testShortByte(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortByteT1T2Misaligned() {
        testShortByte(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteIntMisaligned() {
        testByteInt(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteIntT1Misaligned() {
        testByteInt(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteIntT2Misaligned() {
        testByteInt(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteIntT1T2Misaligned() {
        testByteInt(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntByteMisaligned() {
        testIntByte(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntByteT1Misaligned() {
        testIntByte(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntByteT2Misaligned() {
        testIntByte(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntByteT1T2Misaligned() {
        testIntByte(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteLongMisaligned() {
        testByteLong(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteLongT1Misaligned() {
        testByteLong(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteLongT2Misaligned() {
        testByteLong(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteLongT1T2Misaligned() {
        testByteLong(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongByteMisaligned() {
        testLongByte(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongByteT1Misaligned() {
        testLongByte(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongByteT2Misaligned() {
        testLongByte(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongByteT1T2Misaligned() {
        testLongByte(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteFloatMisaligned() {
        testByteFloat(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteFloatT1Misaligned() {
        testByteFloat(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteFloatT2Misaligned() {
        testByteFloat(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteFloatT1T2Misaligned() {
        testByteFloat(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatByteMisaligned() {
        testFloatByte(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatByteT1Misaligned() {
        testFloatByte(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatByteT2Misaligned() {
        testFloatByte(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatByteT1T2Misaligned() {
        testFloatByte(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteDoubleMisaligned() {
        testByteDouble(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteDoubleT1Misaligned() {
        testByteDouble(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteDoubleT2Misaligned() {
        testByteDouble(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteDoubleT1T2Misaligned() {
        testByteDouble(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleByteMisaligned() {
        testDoubleByte(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleByteT1Misaligned() {
        testDoubleByte(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleByteT2Misaligned() {
        testDoubleByte(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleByteT1T2Misaligned() {
        testDoubleByte(true,true,true,false);
    }

    @Test
    public void testShortShort() {
        testShortShort(false,false,false,false);
    }

    @Test
    public void testShortShortT1() {
        testShortShort(true,false,false,false);
    }

    @Test
    public void testShortShortT2() {
        testShortShort(false,true,false,false);
    }

    @Test
    public void testShortShortT1T2() {
        testShortShort(true,true,false,false);
    }

    @Test
    public void testShortInt() {
        testShortInt(false,false,false,false);
    }

    @Test
    public void testShortIntT1() {
        testShortInt(true,false,false,false);
    }

    @Test
    public void testShortIntT2() {
        testShortInt(false,true,false,false);
    }

    @Test
    public void testShortIntT1T2() {
        testShortInt(true,true,false,false);
    }

    @Test
    public void testIntShort() {
        testIntShort(false,false,false,false);
    }

    @Test
    public void testIntShortT1() {
        testIntShort(true,false,false,false);
    }

    @Test
    public void testIntShortT2() {
        testIntShort(false,true,false,false);
    }

    @Test
    public void testIntShortT1T2() {
        testIntShort(true,true,false,false);
    }

    @Test
    public void testShortLong() {
        testShortLong(false,false,false,false);
    }

    @Test
    public void testShortLongT1() {
        testShortLong(true,false,false,false);
    }

    @Test
    public void testShortLongT2() {
        testShortLong(false,true,false,false);
    }

    @Test
    public void testShortLongT1T2() {
        testShortLong(true,true,false,false);
    }

    @Test
    public void testLongShort() {
        testLongShort(false,false,false,false);
    }

    @Test
    public void testLongShortT1() {
        testLongShort(true,false,false,false);
    }

    @Test
    public void testLongShortT2() {
        testLongShort(false,true,false,false);
    }

    @Test
    public void testLongShortT1T2() {
        testLongShort(true,true,false,false);
    }

    @Test
    public void testShortFloat() {
        testShortFloat(false,false,false,false);
    }

    @Test
    public void testShortFloatT1() {
        testShortFloat(true,false,false,false);
    }

    @Test
    public void testShortFloatT2() {
        testShortFloat(false,true,false,false);
    }

    @Test
    public void testShortFloatT1T2() {
        testShortFloat(true,true,false,false);
    }

    @Test
    public void testFloatShort() {
        testFloatShort(false,false,false,false);
    }

    @Test
    public void testFloatShortT1() {
        testFloatShort(true,false,false,false);
    }

    @Test
    public void testFloatShortT2() {
        testFloatShort(false,true,false,false);
    }

    @Test
    public void testFloatShortT1T2() {
        testFloatShort(true,true,false,false);
    }

    @Test
    public void testShortDouble() {
        testShortDouble(false,false,false,false);
    }

    @Test
    public void testShortDoubleT1() {
        testShortDouble(true,false,false,false);
    }

    @Test
    public void testShortDoubleT2() {
        testShortDouble(false,true,false,false);
    }

    @Test
    public void testShortDoubleT1T2() {
        testShortDouble(true,true,false,false);
    }

    @Test
    public void testDoubleShort() {
        testDoubleShort(false,false,false,false);
    }

    @Test
    public void testDoubleShortT1() {
        testDoubleShort(true,false,false,false);
    }

    @Test
    public void testDoubleShortT2() {
        testDoubleShort(false,true,false,false);
    }

    @Test
    public void testDoubleShortT1T2() {
        testDoubleShort(true,true,false,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortShortMisaligned() {
        testShortShort(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortShortT1Misaligned() {
        testShortShort(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortShortT2Misaligned() {
        testShortShort(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortShortT1T2Misaligned() {
        testShortShort(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortIntMisaligned() {
        testShortInt(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortIntT1Misaligned() {
        testShortInt(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortIntT2Misaligned() {
        testShortInt(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortIntT1T2Misaligned() {
        testShortInt(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntShortMisaligned() {
        testIntShort(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntShortT1Misaligned() {
        testIntShort(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntShortT2Misaligned() {
        testIntShort(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntShortT1T2Misaligned() {
        testIntShort(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortLongMisaligned() {
        testShortLong(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortLongT1Misaligned() {
        testShortLong(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortLongT2Misaligned() {
        testShortLong(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortLongT1T2Misaligned() {
        testShortLong(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongShortMisaligned() {
        testLongShort(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongShortT1Misaligned() {
        testLongShort(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongShortT2Misaligned() {
        testLongShort(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongShortT1T2Misaligned() {
        testLongShort(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortFloatMisaligned() {
        testShortFloat(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortFloatT1Misaligned() {
        testShortFloat(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortFloatT2Misaligned() {
        testShortFloat(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortFloatT1T2Misaligned() {
        testShortFloat(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatShortMisaligned() {
        testFloatShort(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatShortT1Misaligned() {
        testFloatShort(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatShortT2Misaligned() {
        testFloatShort(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatShortT1T2Misaligned() {
        testFloatShort(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortDoubleMisaligned() {
        testShortDouble(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortDoubleT1Misaligned() {
        testShortDouble(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortDoubleT2Misaligned() {
        testShortDouble(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortDoubleT1T2Misaligned() {
        testShortDouble(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleShortMisaligned() {
        testDoubleShort(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleShortT1Misaligned() {
        testDoubleShort(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleShortT2Misaligned() {
        testDoubleShort(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleShortT1T2Misaligned() {
        testDoubleShort(true,true,true,false);
    }

    @Test
    public void testIntInt() {
        testIntInt(false,false,false,false);
    }

    @Test
    public void testIntIntT1() {
        testIntInt(true,false,false,false);
    }

    @Test
    public void testIntIntT2() {
        testIntInt(false,true,false,false);
    }

    @Test
    public void testIntIntT1T2() {
        testIntInt(true,true,false,false);
    }

    @Test
    public void testIntLong() {
        testIntLong(false,false,false,false);
    }

    @Test
    public void testIntLongT1() {
        testIntLong(true,false,false,false);
    }

    @Test
    public void testIntLongT2() {
        testIntLong(false,true,false,false);
    }

    @Test
    public void testIntLongT1T2() {
        testIntLong(true,true,false,false);
    }

    @Test
    public void testLongInt() {
        testLongInt(false,false,false,false);
    }

    @Test
    public void testLongIntT1() {
        testLongInt(true,false,false,false);
    }

    @Test
    public void testLongIntT2() {
        testLongInt(false,true,false,false);
    }

    @Test
    public void testLongIntT1T2() {
        testLongInt(true,true,false,false);
    }

    @Test
    public void testIntFloat() {
        testIntFloat(false,false,false,false);
    }

    @Test
    public void testIntFloatT1() {
        testIntFloat(true,false,false,false);
    }

    @Test
    public void testIntFloatT2() {
        testIntFloat(false,true,false,false);
    }

    @Test
    public void testIntFloatT1T2() {
        testIntFloat(true,true,false,false);
    }

    @Test
    public void testFloatInt() {
        testFloatInt(false,false,false,false);
    }

    @Test
    public void testFloatIntT1() {
        testFloatInt(true,false,false,false);
    }

    @Test
    public void testFloatIntT2() {
        testFloatInt(false,true,false,false);
    }

    @Test
    public void testFloatIntT1T2() {
        testFloatInt(true,true,false,false);
    }

    @Test
    public void testIntDouble() {
        testIntDouble(false,false,false,false);
    }

    @Test
    public void testIntDoubleT1() {
        testIntDouble(true,false,false,false);
    }

    @Test
    public void testIntDoubleT2() {
        testIntDouble(false,true,false,false);
    }

    @Test
    public void testIntDoubleT1T2() {
        testIntDouble(true,true,false,false);
    }

    @Test
    public void testDoubleInt() {
        testDoubleInt(false,false,false,false);
    }

    @Test
    public void testDoubleIntT1() {
        testDoubleInt(true,false,false,false);
    }

    @Test
    public void testDoubleIntT2() {
        testDoubleInt(false,true,false,false);
    }

    @Test
    public void testDoubleIntT1T2() {
        testDoubleInt(true,true,false,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntIntMisaligned() {
        testIntInt(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntIntT1Misaligned() {
        testIntInt(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntIntT2Misaligned() {
        testIntInt(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntIntT1T2Misaligned() {
        testIntInt(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntLongMisaligned() {
        testIntLong(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntLongT1Misaligned() {
        testIntLong(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntLongT2Misaligned() {
        testIntLong(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntLongT1T2Misaligned() {
        testIntLong(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongIntMisaligned() {
        testLongInt(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongIntT1Misaligned() {
        testLongInt(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongIntT2Misaligned() {
        testLongInt(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongIntT1T2Misaligned() {
        testLongInt(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntFloatMisaligned() {
        testIntFloat(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntFloatT1Misaligned() {
        testIntFloat(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntFloatT2Misaligned() {
        testIntFloat(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntFloatT1T2Misaligned() {
        testIntFloat(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatIntMisaligned() {
        testFloatInt(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatIntT1Misaligned() {
        testFloatInt(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatIntT2Misaligned() {
        testFloatInt(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatIntT1T2Misaligned() {
        testFloatInt(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntDoubleMisaligned() {
        testIntDouble(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntDoubleT1Misaligned() {
        testIntDouble(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntDoubleT2Misaligned() {
        testIntDouble(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntDoubleT1T2Misaligned() {
        testIntDouble(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleIntMisaligned() {
        testDoubleInt(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleIntT1Misaligned() {
        testDoubleInt(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleIntT2Misaligned() {
        testDoubleInt(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleIntT1T2Misaligned() {
        testDoubleInt(true,true,true,false);
    }

    @Test
    public void testLongLong() {
        testLongLong(false,false,false,false);
    }

    @Test
    public void testLongLongT1() {
        testLongLong(true,false,false,false);
    }

    @Test
    public void testLongLongT2() {
        testLongLong(false,true,false,false);
    }

    @Test
    public void testLongLongT1T2() {
        testLongLong(true,true,false,false);
    }

    @Test
    public void testLongFloat() {
        testLongFloat(false,false,false,false);
    }

    @Test
    public void testLongFloatT1() {
        testLongFloat(true,false,false,false);
    }

    @Test
    public void testLongFloatT2() {
        testLongFloat(false,true,false,false);
    }

    @Test
    public void testLongFloatT1T2() {
        testLongFloat(true,true,false,false);
    }

    @Test
    public void testFloatLong() {
        testFloatLong(false,false,false,false);
    }

    @Test
    public void testFloatLongT1() {
        testFloatLong(true,false,false,false);
    }

    @Test
    public void testFloatLongT2() {
        testFloatLong(false,true,false,false);
    }

    @Test
    public void testFloatLongT1T2() {
        testFloatLong(true,true,false,false);
    }

    @Test
    public void testLongDouble() {
        testLongDouble(false,false,false,false);
    }

    @Test
    public void testLongDoubleT1() {
        testLongDouble(true,false,false,false);
    }

    @Test
    public void testLongDoubleT2() {
        testLongDouble(false,true,false,false);
    }

    @Test
    public void testLongDoubleT1T2() {
        testLongDouble(true,true,false,false);
    }

    @Test
    public void testDoubleLong() {
        testDoubleLong(false,false,false,false);
    }

    @Test
    public void testDoubleLongT1() {
        testDoubleLong(true,false,false,false);
    }

    @Test
    public void testDoubleLongT2() {
        testDoubleLong(false,true,false,false);
    }

    @Test
    public void testDoubleLongT1T2() {
        testDoubleLong(true,true,false,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongLongMisaligned() {
        testLongLong(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongLongT1Misaligned() {
        testLongLong(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongLongT2Misaligned() {
        testLongLong(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongLongT1T2Misaligned() {
        testLongLong(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongFloatMisaligned() {
        testLongFloat(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongFloatT1Misaligned() {
        testLongFloat(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongFloatT2Misaligned() {
        testLongFloat(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongFloatT1T2Misaligned() {
        testLongFloat(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatLongMisaligned() {
        testFloatLong(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatLongT1Misaligned() {
        testFloatLong(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatLongT2Misaligned() {
        testFloatLong(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatLongT1T2Misaligned() {
        testFloatLong(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongDoubleMisaligned() {
        testLongDouble(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongDoubleT1Misaligned() {
        testLongDouble(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongDoubleT2Misaligned() {
        testLongDouble(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongDoubleT1T2Misaligned() {
        testLongDouble(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleLongMisaligned() {
        testDoubleLong(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleLongT1Misaligned() {
        testDoubleLong(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleLongT2Misaligned() {
        testDoubleLong(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleLongT1T2Misaligned() {
        testDoubleLong(true,true,true,false);
    }

    @Test
    public void testFloatFloat() {
        testFloatFloat(false,false,false,false);
    }

    @Test
    public void testFloatFloatT1() {
        testFloatFloat(true,false,false,false);
    }

    @Test
    public void testFloatFloatT2() {
        testFloatFloat(false,true,false,false);
    }

    @Test
    public void testFloatFloatT1T2() {
        testFloatFloat(true,true,false,false);
    }

    @Test
    public void testFloatDouble() {
        testFloatDouble(false,false,false,false);
    }

    @Test
    public void testFloatDoubleT1() {
        testFloatDouble(true,false,false,false);
    }

    @Test
    public void testFloatDoubleT2() {
        testFloatDouble(false,true,false,false);
    }

    @Test
    public void testFloatDoubleT1T2() {
        testFloatDouble(true,true,false,false);
    }

    @Test
    public void testDoubleFloat() {
        testDoubleFloat(false,false,false,false);
    }

    @Test
    public void testDoubleFloatT1() {
        testDoubleFloat(true,false,false,false);
    }

    @Test
    public void testDoubleFloatT2() {
        testDoubleFloat(false,true,false,false);
    }

    @Test
    public void testDoubleFloatT1T2() {
        testDoubleFloat(true,true,false,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatFloatMisaligned() {
        testFloatFloat(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatFloatT1Misaligned() {
        testFloatFloat(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatFloatT2Misaligned() {
        testFloatFloat(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatFloatT1T2Misaligned() {
        testFloatFloat(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatDoubleMisaligned() {
        testFloatDouble(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatDoubleT1Misaligned() {
        testFloatDouble(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatDoubleT2Misaligned() {
        testFloatDouble(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatDoubleT1T2Misaligned() {
        testFloatDouble(true,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleFloatMisaligned() {
        testDoubleFloat(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleFloatT1Misaligned() {
        testDoubleFloat(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleFloatT2Misaligned() {
        testDoubleFloat(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleFloatT1T2Misaligned() {
        testDoubleFloat(true,true,true,false);
    }

    @Test
    public void testDoubleDouble() {
        testDoubleDouble(false,false,false,false);
    }

    @Test
    public void testDoubleDoubleT1() {
        testDoubleDouble(true,false,false,false);
    }

    @Test
    public void testDoubleDoubleT2() {
        testDoubleDouble(false,true,false,false);
    }

    @Test
    public void testDoubleDoubleT1T2() {
        testDoubleDouble(true,true,false,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleDoubleMisaligned() {
        testDoubleDouble(false,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleDoubleT1Misaligned() {
        testDoubleDouble(true,false,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleDoubleT2Misaligned() {
        testDoubleDouble(false,true,true,false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleDoubleT1T2Misaligned() {
        testDoubleDouble(true,true,true,false);
    }

    @Test
    public void testByteByteDefault() {
        testByteByte(false,false,false,true);
    }

    @Test
    public void testByteShortDefault() {
        testByteShort(false,false,false,true);
    }

    @Test
    public void testShortByteDefault() {
        testShortByte(false,false,false,true);
    }

    @Test
    public void testByteIntDefault() {
        testByteInt(false,false,false,true);
    }

    @Test
    public void testIntByteDefault() {
        testIntByte(false,false,false,true);
    }

    @Test
    public void testByteLongDefault() {
        testByteLong(false,false,false,true);
    }

    @Test
    public void testLongByteDefault() {
        testLongByte(false,false,false,true);
    }

    @Test
    public void testByteFloatDefault() {
        testByteFloat(false,false,false,true);
    }

    @Test
    public void testFloatByteDefault() {
        testFloatByte(false,false,false,true);
    }

    @Test
    public void testByteDoubleDefault() {
        testByteDouble(false,false,false,true);
    }

    @Test
    public void testDoubleByteDefault() {
        testDoubleByte(false,false,false,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteByteMisalignedDefault() {
        testByteByte(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteShortMisalignedDefault() {
        testByteShort(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortByteMisalignedDefault() {
        testShortByte(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteIntMisalignedDefault() {
        testByteInt(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntByteMisalignedDefault() {
        testIntByte(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteLongMisalignedDefault() {
        testByteLong(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongByteMisalignedDefault() {
        testLongByte(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteFloatMisalignedDefault() {
        testByteFloat(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatByteMisalignedDefault() {
        testFloatByte(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteDoubleMisalignedDefault() {
        testByteDouble(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleByteMisalignedDefault() {
        testDoubleByte(false,false,true,true);
    }

    @Test
    public void testShortShortDefault() {
        testShortShort(false,false,false,true);
    }

    @Test
    public void testShortIntDefault() {
        testShortInt(false,false,false,true);
    }

    @Test
    public void testIntShortDefault() {
        testIntShort(false,false,false,true);
    }

    @Test
    public void testShortLongDefault() {
        testShortLong(false,false,false,true);
    }

    @Test
    public void testLongShortDefault() {
        testLongShort(false,false,false,true);
    }

    @Test
    public void testShortFloatDefault() {
        testShortFloat(false,false,false,true);
    }

    @Test
    public void testFloatShortDefault() {
        testFloatShort(false,false,false,true);
    }

    @Test
    public void testShortDoubleDefault() {
        testShortDouble(false,false,false,true);
    }

    @Test
    public void testDoubleShortDefault() {
        testDoubleShort(false,false,false,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortShortMisalignedDefault() {
        testShortShort(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortIntMisalignedDefault() {
        testShortInt(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntShortMisalignedDefault() {
        testIntShort(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortLongMisalignedDefault() {
        testShortLong(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongShortMisalignedDefault() {
        testLongShort(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortFloatMisalignedDefault() {
        testShortFloat(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatShortMisalignedDefault() {
        testFloatShort(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortDoubleMisalignedDefault() {
        testShortDouble(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleShortMisalignedDefault() {
        testDoubleShort(false,false,true,true);
    }

    @Test
    public void testIntIntDefault() {
        testIntInt(false,false,false,true);
    }

    @Test
    public void testIntLongDefault() {
        testIntLong(false,false,false,true);
    }

    @Test
    public void testLongIntDefault() {
        testLongInt(false,false,false,true);
    }

    @Test
    public void testIntFloatDefault() {
        testIntFloat(false,false,false,true);
    }

    @Test
    public void testFloatIntDefault() {
        testFloatInt(false,false,false,true);
    }

    @Test
    public void testIntDoubleDefault() {
        testIntDouble(false,false,false,true);
    }

    @Test
    public void testDoubleIntDefault() {
        testDoubleInt(false,false,false,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntIntMisalignedDefault() {
        testIntInt(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntLongMisalignedDefault() {
        testIntLong(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongIntMisalignedDefault() {
        testLongInt(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntFloatMisalignedDefault() {
        testIntFloat(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatIntMisalignedDefault() {
        testFloatInt(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntDoubleMisalignedDefault() {
        testIntDouble(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleIntMisalignedDefault() {
        testDoubleInt(false,false,true,true);
    }

    @Test
    public void testLongLongDefault() {
        testLongLong(false,false,false,true);
    }

    @Test
    public void testLongFloatDefault() {
        testLongFloat(false,false,false,true);
    }

    @Test
    public void testFloatLongDefault() {
        testFloatLong(false,false,false,true);
    }

    @Test
    public void testLongDoubleDefault() {
        testLongDouble(false,false,false,true);
    }

    @Test
    public void testDoubleLongDefault() {
        testDoubleLong(false,false,false,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongLongMisalignedDefault() {
        testLongLong(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongFloatMisalignedDefault() {
        testLongFloat(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatLongMisalignedDefault() {
        testFloatLong(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongDoubleMisalignedDefault() {
        testLongDouble(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleLongMisalignedDefault() {
        testDoubleLong(false,false,true,true);
    }

    @Test
    public void testFloatFloatDefault() {
        testFloatFloat(false,false,false,true);
    }

    @Test
    public void testFloatDoubleDefault() {
        testFloatDouble(false,false,false,true);
    }

    @Test
    public void testDoubleFloatDefault() {
        testDoubleFloat(false,false,false,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatFloatMisalignedDefault() {
        testFloatFloat(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatDoubleMisalignedDefault() {
        testFloatDouble(false,false,true,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleFloatMisalignedDefault() {
        testDoubleFloat(false,false,true,true);
    }

    @Test
    public void testDoubleDoubleDefault() {
        testDoubleDouble(false,false,false,true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleDoubleMisalignedDefault() {
        testDoubleDouble(false,false,true,true);
    }
}
