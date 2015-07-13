package com.pb.sawdust.calculator.tensor.la;

import com.pb.sawdust.tensor.AbstractTensor;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.matrix.Matrix;
import com.pb.sawdust.tensor.alias.matrix.id.IdBooleanMatrix;
import com.pb.sawdust.tensor.alias.matrix.id.IdDoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.*;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdBooleanD2TensorShell;
import com.pb.sawdust.tensor.decorators.id.primitive.size.IdDoubleD2TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.decorators.primitive.size.*;
import com.pb.sawdust.tensor.decorators.size.D2TensorShell;
import com.pb.sawdust.tensor.index.BaseIndex;
import com.pb.sawdust.tensor.index.IdlessIndex;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.StandardIndex;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.RandomDeluxe;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code MatrixTranspose} class provides convenience methods for getting (reference) matrix transposes.
 *
 * @author crf <br/>
 *         Started Apr 7, 2010 9:00:07 PM
 */
public class TransposedMatrix {
    
    private TransposedMatrix() {} //private constructor, no need to instantiate this factory

    private static <I> List<List<I>> switchIds(List<List<I>> ids) {
        List<List<I>> newIds = new LinkedList<List<I>>();
        newIds.add(ids.get(1));
        newIds.add(ids.get(0));
        return newIds;
    }

    @SuppressWarnings("unchecked") //we are internally doing the correct thing here
    private static <I> Index<I> getTransposeIndex(Index<I> index) {
        if (index instanceof IdlessIndex) {
            return (Index<I>) new StandardIndex(index.size(1),index.size(0));
        } else {
            return new BaseIndex<I>(switchIds(index.getIndexIds()));
        }
    }
      
    private static class MatrixTranspose<T> extends D2TensorShell<T> {
        private final Matrix<T> matrix;

        MatrixTranspose(final Matrix<T> matrix) {
            super(new AbstractTensor<T>(getTransposeIndex(matrix.getIndex())) {
                //ignore methods, overridden in surrounding class
                public T getValue(int... indices) { return null; }
                public void setValue(T value, int... indices) { }
                public JavaType getType() {
                    return matrix.getType();
                }
            });
            this.matrix = matrix;
        }

        @Override
        public T getValue(int d0index, int d1index) {
            return matrix.getValue(d1index,d0index);
        }

        @Override
        public void setValue(T value, int d0index, int d1index) {
            matrix.setValue(value,d1index,d0index);
        }
    }
    
    private static class BooleanMatrixTranspose extends BooleanD2TensorShell {
        private final BooleanMatrix matrix;

        BooleanMatrixTranspose(BooleanMatrix matrix) {
            super(new AbstractBooleanTensor(getTransposeIndex(matrix.getIndex())) {
                //ignore methods, overridden in surrounding class
                public boolean getCell(int... indices) { return true; }
                public void setCell(boolean value, int... indices) { }
            });
            this.matrix = matrix;
        }

        @Override
        public boolean getCell(int d0index, int d1index) {
            return matrix.getCell(d1index,d0index);
        }

        @Override
        public void setCell(boolean value, int d0index, int d1index) {
            matrix.setCell(value,d1index,d0index);
        }
    }

    private static class IdBooleanMatrixTranspose<I> extends IdBooleanD2TensorShell<I> {
            IdBooleanMatrixTranspose(BooleanMatrix matrix) {
                super(new BooleanMatrixTranspose(matrix));
            }
    }
    
    private static class CharMatrixTranspose extends CharD2TensorShell {
        private final CharMatrix matrix;

        CharMatrixTranspose(CharMatrix matrix) {
            super(new AbstractCharTensor(getTransposeIndex(matrix.getIndex())) {
                //ignore methods, overridden in surrounding class
                public char getCell(int... indices) { return 0; }
                public void setCell(char value, int... indices) { }
            });
            this.matrix = matrix;
        }

        @Override
        public char getCell(int d0index, int d1index) {
            return matrix.getCell(d1index,d0index);
        }

        @Override
        public void setCell(char value, int d0index, int d1index) {
            matrix.setCell(value,d1index,d0index);
        }
    }
    
    private static class ByteMatrixTranspose extends ByteD2TensorShell {
        private final ByteMatrix matrix;

        ByteMatrixTranspose(ByteMatrix matrix) {
            super(new AbstractByteTensor(getTransposeIndex(matrix.getIndex())) {
                //ignore methods, overridden in surrounding class
                public byte getCell(int... indices) { return 0; }
                public void setCell(byte value, int... indices) { }
            });
            this.matrix = matrix;
        }

        @Override
        public byte getCell(int d0index, int d1index) {
            return matrix.getCell(d1index,d0index);
        }

        @Override
        public void setCell(byte value, int d0index, int d1index) {
            matrix.setCell(value,d1index,d0index);
        }
    }
    
    private static class ShortMatrixTranspose extends ShortD2TensorShell {
        private final ShortMatrix matrix;

        ShortMatrixTranspose(ShortMatrix matrix) {
            super(new AbstractShortTensor(getTransposeIndex(matrix.getIndex())) {
                //ignore methods, overridden in surrounding class
                public short getCell(int... indices) { return 0; }
                public void setCell(short value, int... indices) { }
            });
            this.matrix = matrix;
        }

        @Override
        public short getCell(int d0index, int d1index) {
            return matrix.getCell(d1index,d0index);
        }

        @Override
        public void setCell(short value, int d0index, int d1index) {
            matrix.setCell(value,d1index,d0index);
        }
    }
    
    private static class IntMatrixTranspose extends IntD2TensorShell {
        private final IntMatrix matrix;

        IntMatrixTranspose(IntMatrix matrix) {
            super(new AbstractIntTensor(getTransposeIndex(matrix.getIndex())) {
                //ignore methods, overridden in surrounding class
                public int getCell(int... indices) { return 0; }
                public void setCell(int value, int... indices) { }
            });
            this.matrix = matrix;
        }

        @Override
        public int getCell(int d0index, int d1index) {
            return matrix.getCell(d1index,d0index);
        }

        @Override
        public void setCell(int value, int d0index, int d1index) {
            matrix.setCell(value,d1index,d0index);
        }
    }
    
    private static class LongMatrixTranspose extends LongD2TensorShell {
        private final LongMatrix matrix;

        LongMatrixTranspose(LongMatrix matrix) {
            super(new AbstractLongTensor(getTransposeIndex(matrix.getIndex())) {
                //ignore methods, overridden in surrounding class
                public long getCell(int... indices) { return 0; }
                public void setCell(long value, int... indices) { }
            });
            this.matrix = matrix;
        }

        @Override
        public long getCell(int d0index, int d1index) {
            return matrix.getCell(d1index,d0index);
        }

        @Override
        public void setCell(long value, int d0index, int d1index) {
            matrix.setCell(value,d1index,d0index);
        }
    }
    
    private static class FloatMatrixTranspose extends FloatD2TensorShell {
        private final FloatMatrix matrix;

        FloatMatrixTranspose(FloatMatrix matrix) {
            super(new AbstractFloatTensor(getTransposeIndex(matrix.getIndex())) {
                //ignore methods, overridden in surrounding class
                public float getCell(int... indices) { return 0; }
                public void setCell(float value, int... indices) { }
            });
            this.matrix = matrix;
        }

        @Override
        public float getCell(int d0index, int d1index) {
            return matrix.getCell(d1index,d0index);
        }

        @Override
        public void setCell(float value, int d0index, int d1index) {
            matrix.setCell(value,d1index,d0index);
        }
    }
    
    private static class DoubleMatrixTranspose extends DoubleD2TensorShell {
        private final DoubleMatrix matrix;

        DoubleMatrixTranspose(DoubleMatrix matrix) {
            super(new AbstractDoubleTensor(getTransposeIndex(matrix.getIndex())) {
                //ignore methods, overridden in surrounding class
                public double getCell(int... indices) { return 0; }
                public void setCell(double value, int... indices) { }
            });
            this.matrix = matrix;
        }

        @Override
        public double getCell(int d0index, int d1index) {
            return matrix.getCell(d1index,d0index);
        }

        @Override
        public void setCell(double value, int d0index, int d1index) {
            matrix.setCell(value,d1index,d0index);
        }
    }

    private static class IdDoubleMatrixTranspose<I> extends IdDoubleD2TensorShell<I> { //todo: test this out, along with others...
            IdDoubleMatrixTranspose(DoubleMatrix matrix) {
                super(new DoubleMatrixTranspose(matrix));
            }
    }
    
    
    /****************public methods****************************/
    /**
     * Get the transpose of a matrix. The returned matrix will be a reference to the source matrix.
     * 
     * @param m
     *        The input matrix.
     * 
     * @return the transpose of {@code m}.
     */
    public static ByteMatrix transpose(ByteMatrix m) {
        return new TransposedMatrix.ByteMatrixTranspose(m);
    }

    /**
     * Get the transpose of a matrix. The returned matrix will be a reference to the source matrix.
     * 
     * @param m
     *        The input matrix.
     * 
     * @return the transpose of {@code m}.
     */
    public static ShortMatrix transpose(ShortMatrix m) {
        return new TransposedMatrix.ShortMatrixTranspose(m);
    }

    /**
     * Get the transpose of a matrix. The returned matrix will be a reference to the source matrix.
     * 
     * @param m
     *        The input matrix.
     * 
     * @return the transpose of {@code m}.
     */
    public static IntMatrix transpose(IntMatrix m) {
        return new TransposedMatrix.IntMatrixTranspose(m);
    }

    /**
     * Get the transpose of a matrix. The returned matrix will be a reference to the source matrix.
     * 
     * @param m
     *        The input matrix.
     * 
     * @return the transpose of {@code m}.
     */
    public static LongMatrix transpose(LongMatrix m) {
        return new TransposedMatrix.LongMatrixTranspose(m);
    }

    /**
     * Get the transpose of a matrix. The returned matrix will be a reference to the source matrix.
     * 
     * @param m
     *        The input matrix.
     * 
     * @return the transpose of {@code m}.
     */
    public static FloatMatrix transpose(FloatMatrix m) {
        return new TransposedMatrix.FloatMatrixTranspose(m);
    }

    /**
     * Get the transpose of a matrix. The returned matrix will be a reference to the source matrix.
     * 
     * @param m
     *        The input matrix.
     * 
     * @return the transpose of {@code m}.
     */
    public static DoubleMatrix transpose(DoubleMatrix m) {
        return new TransposedMatrix.DoubleMatrixTranspose(m);
    }

    public static <I> IdDoubleMatrix<I> transpose(IdDoubleMatrix<I> m) {
        return new TransposedMatrix.IdDoubleMatrixTranspose<>(m);
    }

    /**
     * Get the transpose of a matrix. The returned matrix will be a reference to the source matrix.
     * 
     * @param m
     *        The input matrix.
     * 
     * @return the transpose of {@code m}.
     */
    public static CharMatrix transpose(CharMatrix m) {
        return new TransposedMatrix.CharMatrixTranspose(m);
    }

    /**
     * Get the transpose of a matrix. The returned matrix will be a reference to the source matrix.
     * 
     * @param m
     *        The input matrix.
     * 
     * @return the transpose of {@code m}.
     */
    public static BooleanMatrix transpose(BooleanMatrix m) {
        return new TransposedMatrix.BooleanMatrixTranspose(m);
    }
    public static <I> IdBooleanMatrix<I> transpose(IdBooleanMatrix<I> m) {
        return new TransposedMatrix.IdBooleanMatrixTranspose<>(m);
    }

    /**
     * Get the transpose of a matrix. The returned matrix will be a reference to the source matrix.
     *
     * @param m
     *        The input matrix.
     *
     * @return the transpose of {@code m}.
     */
    public static <T> Matrix<T> transpose(Matrix<T> m) {
        return new TransposedMatrix.MatrixTranspose<T>(m);
    }
    

    public static void main(String ... args) {
        DoubleMatrix m = (DoubleMatrix) ArrayTensor.getFactory().doubleTensor(5,7);
        final RandomDeluxe random = new RandomDeluxe();
        TensorUtil.fill(m, new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int... indices) {
                return random.nextInt(1, 15);
            }
        });
        System.out.println(TensorUtil.toString(m));
        System.out.println(TensorUtil.toString(new DoubleMatrixTranspose(m)));
    }
}
