package com.pb.sawdust.calculator.tensor.la;

import com.pb.sawdust.tensor.alias.matrix.Matrix;
import com.pb.sawdust.tensor.alias.vector.Vector;
import com.pb.sawdust.tensor.alias.scalar.Scalar;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.MathUtil;
import com.pb.sawdust.util.RandomDeluxe;
import com.pb.sawdust.util.test.TestBase;
import static com.pb.sawdust.util.Range.range;

import java.util.Arrays;

import static org.junit.Assert.fail;

/**
 * @author crf <br/>
 *         Started: Oct 15, 2009 10:30:01 AM
 */
class LinearAlgebraTestUtil {
    static RandomDeluxe random = new RandomDeluxe();
    private static final TestBase tb = new TestBase() {};

    static void assertMatrixEquals(Matrix expected, Matrix result) {
        if (!Arrays.equals(expected.getDimensions(),result.getDimensions()))
            fail("Matrix dimensions don't match: expected " + Arrays.toString(expected.getDimensions()) + ", found " + Arrays.toString(result.getDimensions()));
        for (int i : range(expected.size(0)))
            for (int j : range(expected.size(1)))
                if (!MathUtil.almostEquals(expected.getValue(i,j),result.getValue(i,j),0.001, MathUtil.DEFAULT_EQUALS_ZERO_EPSILON))
                    fail("Matrix elements don't match at cell (" + i + "," + j + "): expected " + expected.getValue(i,j) + ", found " + result.getValue(i,j));
    }

    static Matrix<?> getMatrix(int d1, int d2, JavaType type, TensorFactory factory) {
        switch (type) {
            case BYTE : return (Matrix) factory.byteTensor(d1,d2);
            case SHORT : return (Matrix) factory.shortTensor(d1,d2);
            case INT : return (Matrix) factory.intTensor(d1,d2);
            case LONG : return (Matrix) factory.longTensor(d1,d2);
            case FLOAT : return (Matrix) factory.floatTensor(d1,d2);
            case DOUBLE : return (Matrix) factory.doubleTensor(d1,d2);
            default : throw new IllegalStateException("Shouldn't be here");
        }
    }

    static Vector<?> getVector(int d1, JavaType type, TensorFactory factory) {
        switch (type) {
            case BYTE : return (Vector) factory.byteTensor(d1);
            case SHORT : return (Vector) factory.shortTensor(d1);
            case INT : return (Vector) factory.intTensor(d1);
            case LONG : return (Vector) factory.longTensor(d1);
            case FLOAT : return (Vector) factory.floatTensor(d1);
            case DOUBLE : return (Vector) factory.doubleTensor(d1);
            default : throw new IllegalStateException("Shouldn't be here");
        }
    }

    static Scalar<?> getScalar(JavaType type, TensorFactory factory) {
        switch (type) {
            case BYTE : return (Scalar) factory.byteTensor();
            case SHORT : return (Scalar) factory.shortTensor();
            case INT : return (Scalar) factory.intTensor();
            case LONG : return (Scalar) factory.longTensor();
            case FLOAT : return (Scalar) factory.floatTensor();
            case DOUBLE : return (Scalar) factory.doubleTensor();
            default : throw new IllegalStateException("Shouldn't be here");
        }
    }

    static Matrix<?> getRandomizedMatrix(int d1, int d2, JavaType type, TensorFactory factory, boolean positive) {
        Matrix m = getMatrix(d1,d2,type,factory);
        fillRandom(m,type,positive);
        return m;
    }

    static Matrix<?> getRandomizedMatrix(int d1, int d2, JavaType type, TensorFactory factory) {
        return getRandomizedMatrix(d1, d2,type,factory,false);
    }

    static Vector<?> getRandomizedVector(int d1, JavaType type, TensorFactory factory, boolean positive) {
        Vector m = getVector(d1,type,factory);
        fillRandom(m,type,positive);
        return m;
    }

    static Vector<?> getRandomizedVector(int d1, JavaType type, TensorFactory factory) {
        return getRandomizedVector(d1,type,factory,false);
    }

    static Scalar<?> getRandomizedScalar(JavaType type, TensorFactory factory, boolean positive) {
        Scalar s = getScalar(type,factory);
        fillRandom(s,type,positive);
        return s;
    }

    static Scalar<?> getRandomizedScalar(JavaType type, TensorFactory factory) {
        return getRandomizedScalar(type,factory,false);
    }

    static void fillRandom(Tensor<?> tensor, JavaType type) {
         fillRandom(tensor,type,false);
    }

    static void fillRandom(Tensor<?> tensor, JavaType type, final boolean positive) {
        switch (type) {
            case BYTE : {
                TensorUtil.fill((ByteTensor) tensor, new TensorUtil.ByteTensorValueFunction() {
                    public byte getValue(int ... indices) {
                        return (byte) (random.nextByte() - (positive ? 0 : random.nextByte()));
                    }
                });
            } break;
            case SHORT : {
                TensorUtil.fill((ShortTensor) tensor, new TensorUtil.ShortTensorValueFunction() {
                    public short getValue(int ... indices) {
                        return (short) (random.nextShort() - (positive ? 0 : random.nextShort()));
                    }
                });
            } break;
            case INT : {
                TensorUtil.fill((IntTensor) tensor, new TensorUtil.IntTensorValueFunction() {
                    public int getValue(int ... indices) {
//                        return random.nextInt(positive ? 0 : Integer.MIN_VALUE,Integer.MAX_VALUE);
                        return random.nextInt(positive ? 0 : -100,100);
                    }
                });
            } break;
            case LONG : {
                TensorUtil.fill((LongTensor) tensor, new TensorUtil.LongTensorValueFunction() {
                    public long getValue(int ... indices) {
                        //return positive ? Math.abs(random.nextLong()) : random.nextLong();
//                        return random.nextInt(positive ? 0 : Integer.MIN_VALUE,Integer.MAX_VALUE);
                        return random.nextInt(positive ? 0 : -100,100);
                    }
                });
            } break;
            case FLOAT : {
                TensorUtil.fill((FloatTensor) tensor, new TensorUtil.FloatTensorValueFunction() {
                    public float getValue(int ... indices) {
                        return random.nextFloat() * ((positive || random.nextBoolean()) ? 1.0f : -1.0f);
                    }
                });
            } break;
            case DOUBLE : {
                TensorUtil.fill((DoubleTensor) tensor, new TensorUtil.DoubleTensorValueFunction() {
                    public double getValue(int ... indices) {
                        return random.nextDouble() * ((positive || random.nextBoolean()) ? 1.0 : -1.0);
                    }
                });
            } break;
            default : throw new IllegalStateException("Shouldn't be here");
        }
    }
}