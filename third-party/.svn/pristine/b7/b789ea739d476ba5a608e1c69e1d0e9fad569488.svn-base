package com.pb.sawdust.calculator.tensor.la;

import com.pb.sawdust.calculator.tensor.la.mm.DefaultMatrixMultiplication;
import com.pb.sawdust.calculator.tensor.la.mm.MatrixMultiplication;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.index.SliceIndex;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.slice.Slice;
import com.pb.sawdust.tensor.slice.SliceUtil;
import com.pb.sawdust.tensor.alias.matrix.Matrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.*;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.test.TestBase;
import static com.pb.sawdust.calculator.tensor.la.LinearAlgebraTestUtil.*;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.EnumSet;

/**
 * @author crf <br/>
 *         Started: Oct 15, 2009 10:17:42 AM
 */
public class LinearAlgebraMatrixDecompositionTest extends TestBase {

    private static JavaType type;
    private static TensorFactory factory = ArrayTensor.getFactory();
    private static MatrixMultiplication mm = new DefaultMatrixMultiplication(factory);
    private static MatrixDecompositions mc = new MatrixDecompositions(factory);

    public static void main(String ... args) {
        Set<JavaType> types = EnumSet.allOf(JavaType.class);
        types.remove(JavaType.BOOLEAN);
        types.remove(JavaType.CHAR);
        types.remove(JavaType.OBJECT);
        for (JavaType type : types) {
            LinearAlgebraMatrixDecompositionTest.type = type;
            System.out.println(type);
            TestBase.main();
        }
//        LinearAlgebraMatrixDecompositionTest.type = JavaType.BYTE;
//        TestBase.main();
    }

    @SuppressWarnings("unchecked") //all the unchecked matrix calls are ok - funny stuff to make the methods generic
    private void symmetricPositiveDefiniteMatrix(Matrix m) {
        boolean redo = false;
        for (int i : range(m.size(0))) {
            double sum = 0.0;
            for (int j : range(m.size(1))) {
                if (i != j)
                    sum += ((Number) m.getValue(i,j)).doubleValue();
                if (j < i)
                    m.setValue(m.getValue(j,i),i,j);
            }
            switch (type) {
                case BYTE : {
                    if (sum > Byte.MAX_VALUE) {
                        int shiftDown = Math.max((int) (sum / Byte.MAX_VALUE),2);
                        for (int j : range(m.size(1))) {
                            if (i != j)
                                m.setValue((byte) ((Byte) m.getValue(i,j) / shiftDown),i,j);
                            m.setValue(m.getValue(i,j),j,i);
                        }
                        redo = true;
//                        System.out.println(TensorUtil.toString(m));
                    } else {
                        m.setValue((byte) Math.max(sum,(byte) random.nextInt((byte) sum,Byte.MAX_VALUE+1)),i,i);
                    }
                } break;
                case SHORT : {
                    if (sum > Short.MAX_VALUE) {
                        int shiftDown = Math.max((int) (sum / Short.MAX_VALUE),2);
                        for (int j : range(m.size(1))) {
                            m.setValue((short) ((Short) m.getValue(i,j) / shiftDown),i,j);
                            m.setValue(m.getValue(i,j),j,i);
                        }
                        redo = true;
                    } else {
                        m.setValue((short) Math.max(sum,(short) random.nextInt((short) sum,Short.MAX_VALUE+1)),i,i);
                    }
                } break;
                case INT : {
                    if (sum > Integer.MAX_VALUE) {
                        int shiftDown = Math.max((int) (sum / Integer.MAX_VALUE),2);
                        for (int j : range(m.size(1))) {
                            m.setValue(((Integer) m.getValue(i,j) / shiftDown),i,j);
                            m.setValue(m.getValue(i,j),j,i);
                        }
                        redo = true;
                    } else {
                        m.setValue((int) Math.max(sum,random.nextInt((int) sum,Integer.MAX_VALUE)+1),i,i);
                    }
                } break;
                case LONG :  m.setValue((long) sum + random.nextInt(100),i,i); break;
                case FLOAT : m.setValue((float) sum + random.nextFloat()*5,i,i); break;
                case DOUBLE : m.setValue(sum + random.nextDouble()*5,i,i); break;
            }
        }
        if (redo)
            symmetricPositiveDefiniteMatrix(m);
    }

    @Test
    @SuppressWarnings("unchecked") //all the unchecked matrix calls are ok - funny stuff to make the methods generic
    public void testCholesky() {
        int size = random.nextInt(5,100);
        Matrix m = getRandomizedMatrix(size,size,type,factory,true);
        symmetricPositiveDefiniteMatrix(m);
        DoubleMatrix o = null;
        switch (type) {
            case BYTE : o = mc.choleskyDecomposition((ByteMatrix) m); break;
            case SHORT : o = mc.choleskyDecomposition((ShortMatrix) m); break;
            case INT : o = mc.choleskyDecomposition((IntMatrix) m); break;
            case LONG : o = mc.choleskyDecomposition((LongMatrix) m); break;
            case FLOAT : o = mc.choleskyDecomposition((FloatMatrix) m); break;
            case DOUBLE : o = mc.choleskyDecomposition((DoubleMatrix) m); break;
        }
        assertMatrixEquals((DoubleMatrix) TensorUtil.asDoubleTensor((Matrix<? extends Number>) m),mm.multiply(o,o,false,true));
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //all the unchecked matrix calls are ok - funny stuff to make the methods generic
    public void testCholeskyNotSymmetric() {
        int size = random.nextInt(5,100);
        Matrix m = getRandomizedMatrix(size,size,type,factory,true);
        symmetricPositiveDefiniteMatrix(m);
        switch (type) {
            case BYTE : m.setValue((byte) ((Byte) m.getValue(0,1) - 1),0,1); mc.choleskyDecomposition((ByteMatrix) m); break;
            case SHORT : m.setValue((short) ((Short) m.getValue(0,1) - 1),0,1); mc.choleskyDecomposition((ShortMatrix) m); break;
            case INT : m.setValue(((Integer) m.getValue(0,1) - 1),0,1); mc.choleskyDecomposition((IntMatrix) m); break;
            case LONG : m.setValue(((Long) m.getValue(0,1) - 1),0,1); mc.choleskyDecomposition((LongMatrix) m); break;
            case FLOAT : m.setValue(((Float) m.getValue(0,1) - 1),0,1); mc.choleskyDecomposition((FloatMatrix) m); break;
            case DOUBLE : m.setValue(((Double) m.getValue(0,1) - 1),0,1); mc.choleskyDecomposition((DoubleMatrix) m); break;
        }
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //all the unchecked matrix calls are ok - funny stuff to make the methods generic
    public void testCholeskyNotPositiveDefinite() {
        int size = random.nextInt(5,100);
        Matrix m = getRandomizedMatrix(size,size,type,factory,true);
        symmetricPositiveDefiniteMatrix(m);
        switch (type) {
            case BYTE : m.setValue((byte) -1,0,0); mc.choleskyDecomposition((ByteMatrix) m); break;
            case SHORT : m.setValue((short) -1,0,0); mc.choleskyDecomposition((ShortMatrix) m); break;
            case INT : m.setValue(-1,0,0); mc.choleskyDecomposition((IntMatrix) m); break;
            case LONG : m.setValue((long) -1,0,0); mc.choleskyDecomposition((LongMatrix) m); break;
            case FLOAT : m.setValue((float) -0.1,0,0); mc.choleskyDecomposition((FloatMatrix) m); break;
            case DOUBLE : m.setValue(-0.1,0,0); mc.choleskyDecomposition((DoubleMatrix) m); break;
        }
    }

    @Test
    @SuppressWarnings("unchecked") //all the unchecked matrix calls are ok - funny stuff to make the methods generic
    public void testLU() {
        int size = random.nextInt(5,30);
        Matrix m = getRandomizedMatrix(size,size,type,factory);
        MatrixDecompositions.LUDecomposition lu = null;
        switch (type) {
            case BYTE : lu = mc.luDecomposition((ByteMatrix) m); break;
            case SHORT : lu = mc.luDecomposition((ShortMatrix) m); break;
            case INT : lu = mc.luDecomposition((IntMatrix) m); break;
            case LONG : lu = mc.luDecomposition((LongMatrix) m); break;
            case FLOAT : lu = mc.luDecomposition((FloatMatrix) m); break;
            case DOUBLE : lu = mc.luDecomposition((DoubleMatrix) m); break;
        }
        assertMatrixEquals((DoubleMatrix) TensorUtil.asDoubleTensor((Matrix<? extends Number>) m),mm.multiply(lu.getP(),mm.multiply(lu.getL(),lu.getU()),true,false));
    }

    @Test
    @SuppressWarnings("unchecked") //all the unchecked matrix calls are ok - funny stuff to make the methods generic
    public void testLUNotSingular() {
        int size = random.nextInt(5,30);
        Matrix m = getRandomizedMatrix(size,size,type,factory);
        MatrixDecompositions.LUDecomposition lu = null;
        switch (type) {
            case BYTE : lu = mc.luDecomposition((ByteMatrix) m); break;
            case SHORT : lu = mc.luDecomposition((ShortMatrix) m); break;
            case INT : lu = mc.luDecomposition((IntMatrix) m); break;
            case LONG : lu = mc.luDecomposition((LongMatrix) m); break;
            case FLOAT : lu = mc.luDecomposition((FloatMatrix) m); break;
            case DOUBLE : lu = mc.luDecomposition((DoubleMatrix) m); break;
        }
        assertFalse(lu.isSingular());
    }

    @Test
    @SuppressWarnings("unchecked") //all the unchecked matrix calls are ok - funny stuff to make the methods generic
    public void testLUSingular() {
        int size = random.nextInt(5,30);
        Matrix m = getRandomizedMatrix(size,size,type,factory);
        int r = random.nextInt(1,size);
        for (int i : range(m.size(1)))
            m.setValue(m.getValue(0,i),r,i); //copy from first row to r row
        MatrixDecompositions.LUDecomposition lu = null;
        switch (type) {
            case BYTE : lu = mc.luDecomposition((ByteMatrix) m); break;
            case SHORT : lu = mc.luDecomposition((ShortMatrix) m); break;
            case INT : lu = mc.luDecomposition((IntMatrix) m); break;
            case LONG : lu = mc.luDecomposition((LongMatrix) m); break;
            case FLOAT : lu = mc.luDecomposition((FloatMatrix) m); break;
            case DOUBLE : lu = mc.luDecomposition((DoubleMatrix) m); break;
        }
        if (!lu.isSingular())
            System.out.println(TensorUtil.toString(lu.getL()));
        assertTrue(lu.isSingular());
    }

    @Test
    @SuppressWarnings("unchecked") //all the unchecked matrix calls are ok - funny stuff to make the methods generic
    public void testQR() {
        int rows = random.nextInt(10,30);
        int columns = random.nextInt(5,rows);
        Matrix m = getRandomizedMatrix(rows,columns,type,factory);
        MatrixDecompositions.QRDecomposition qr = null;
        switch (type) {
            case BYTE : qr = mc.qrDecomposition((ByteMatrix) m,true); break;
            case SHORT : qr = mc.qrDecomposition((ShortMatrix) m,true); break;
            case INT : qr = mc.qrDecomposition((IntMatrix) m,true); break;
            case LONG : qr = mc.qrDecomposition((LongMatrix) m,true); break;
            case FLOAT : qr = mc.qrDecomposition((FloatMatrix) m,true); break;
            case DOUBLE : qr = mc.qrDecomposition((DoubleMatrix) m,true); break;
        }
        assertMatrixEquals((DoubleMatrix) TensorUtil.asDoubleTensor((Matrix<? extends Number>) m),mm.multiply(mm.multiply(qr.getQ(),qr.getR()),qr.getP(),false,true));
    }

    @Test
    @SuppressWarnings("unchecked") //all the unchecked matrix calls are ok - funny stuff to make the methods generic
    public void testQRSquare() {
        int size = random.nextInt(5,30);
        Matrix m = getRandomizedMatrix(size,size,type,factory);
        MatrixDecompositions.QRDecomposition qr = null;
        switch (type) {
            case BYTE : qr = mc.qrDecomposition((ByteMatrix) m,true); break;
            case SHORT : qr = mc.qrDecomposition((ShortMatrix) m,true); break;
            case INT : qr = mc.qrDecomposition((IntMatrix) m,true); break;
            case LONG : qr = mc.qrDecomposition((LongMatrix) m,true); break;
            case FLOAT : qr = mc.qrDecomposition((FloatMatrix) m,true); break;
            case DOUBLE : qr = mc.qrDecomposition((DoubleMatrix) m,true); break;
        }
        assertMatrixEquals((DoubleMatrix) TensorUtil.asDoubleTensor((Matrix<? extends Number>) m),mm.multiply(mm.multiply(qr.getQ(),qr.getR()),qr.getP(),false,true));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testQRRowsSmallerThanColumns() {
        int columns = random.nextInt(10,30);
        int rows = random.nextInt(5,columns);
        Matrix m = getRandomizedMatrix(rows,columns,type,factory);
        switch (type) {
            case BYTE : mc.qrDecomposition((ByteMatrix) m,true); break;
            case SHORT : mc.qrDecomposition((ShortMatrix) m,true); break;
            case INT : mc.qrDecomposition((IntMatrix) m,true); break;
            case LONG : mc.qrDecomposition((LongMatrix) m,true); break;
            case FLOAT : mc.qrDecomposition((FloatMatrix) m,true); break;
            case DOUBLE : mc.qrDecomposition((DoubleMatrix) m,true); break;
        }
    }

    @Test
    @SuppressWarnings("unchecked") //all the unchecked matrix calls are ok - funny stuff to make the methods generic
    public void testQRNoPivot() {
        int rows = random.nextInt(10,30);
        int columns = random.nextInt(5,rows);
        Matrix m = getRandomizedMatrix(rows,columns,type,factory);
        MatrixDecompositions.QRDecomposition qr = null;
        switch (type) {
            case BYTE : qr = mc.qrDecomposition((ByteMatrix) m,false); break;
            case SHORT : qr = mc.qrDecomposition((ShortMatrix) m,false); break;
            case INT : qr = mc.qrDecomposition((IntMatrix) m,false); break;
            case LONG : qr = mc.qrDecomposition((LongMatrix) m,false); break;
            case FLOAT : qr = mc.qrDecomposition((FloatMatrix) m,false); break;
            case DOUBLE : qr = mc.qrDecomposition((DoubleMatrix) m,false); break;
        }
        assertMatrixEquals((DoubleMatrix) TensorUtil.asDoubleTensor((Matrix<? extends Number>) m),mm.multiply(mm.multiply(qr.getQ(),qr.getR()),qr.getP(),false,true));
    }

    @Test
    @SuppressWarnings("unchecked") //all the unchecked matrix calls are ok - funny stuff to make the methods generic
    public void testQRSquareNoPivot() {
        int size = random.nextInt(5,30);
        Matrix m = getRandomizedMatrix(size,size,type,factory);
        MatrixDecompositions.QRDecomposition qr = null;
        switch (type) {
            case BYTE : qr = mc.qrDecomposition((ByteMatrix) m,false); break;
            case SHORT : qr = mc.qrDecomposition((ShortMatrix) m,false); break;
            case INT : qr = mc.qrDecomposition((IntMatrix) m,false); break;
            case LONG : qr = mc.qrDecomposition((LongMatrix) m,false); break;
            case FLOAT : qr = mc.qrDecomposition((FloatMatrix) m,false); break;
            case DOUBLE : qr = mc.qrDecomposition((DoubleMatrix) m,false); break;
        }
        assertMatrixEquals((DoubleMatrix) TensorUtil.asDoubleTensor((Matrix<? extends Number>) m),mm.multiply(mm.multiply(qr.getQ(),qr.getR()),qr.getP(),false,true));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testQRRowsSmallerThanColumnsNoPivot() {
        int columns = random.nextInt(10,30);
        int rows = random.nextInt(5,columns);
        Matrix m = getRandomizedMatrix(rows,columns,type,factory);
        switch (type) {
            case BYTE : mc.qrDecomposition((ByteMatrix) m,false); break;
            case SHORT : mc.qrDecomposition((ShortMatrix) m,false); break;
            case INT : mc.qrDecomposition((IntMatrix) m,false); break;
            case LONG : mc.qrDecomposition((LongMatrix) m,false); break;
            case FLOAT : mc.qrDecomposition((FloatMatrix) m,false); break;
            case DOUBLE : mc.qrDecomposition((DoubleMatrix) m,false); break;
        }
    }

    @Test
    @SuppressWarnings("unchecked") //all the unchecked matrix calls are ok - funny stuff to make the methods generic
    public void testDeterminant() {
        //int size = random.nextInt(5,7);
        int size = random.nextInt(3,4);
        Matrix m = getRandomizedMatrix(size,size,type,factory);
        double det = 0.0;
        switch (type) {
            case BYTE : det = mc.determinant((ByteMatrix) m); break;
            case SHORT : det = mc.determinant((ShortMatrix) m); break;
            case INT : det = mc.determinant((IntMatrix) m); break;
            case LONG : det = mc.determinant((LongMatrix) m); break;
            case FLOAT : det = mc.determinant((FloatMatrix) m); break;
            case DOUBLE : det = mc.determinant((DoubleMatrix) m); break;
        }
        try {
            assertAlmostEquals(determinantByHand(m),det);
        } catch (AssertionError e) {
            System.out.println(TensorUtil.toString(m));MatrixDecompositions.LUDecomposition lu = null;
            switch (type) {
                case BYTE : lu = mc.luDecomposition((ByteMatrix) m); break;
                case SHORT : lu = mc.luDecomposition((ShortMatrix) m); break;
                case INT : lu = mc.luDecomposition((IntMatrix) m); break;
                case LONG : lu = mc.luDecomposition((LongMatrix) m); break;
                case FLOAT : lu = mc.luDecomposition((FloatMatrix) m); break;
                case DOUBLE : lu = mc.luDecomposition((DoubleMatrix) m); break;
            }
            System.out.println(TensorUtil.toString(lu.getL()));
            throw e;
        }
    }

    @SuppressWarnings("unchecked") //matrix double cast is awkward, but necessary
    private double determinantByHand(Matrix<? extends Number> m) {
        if (m.size(0) == 2)
            return m.getValue(0,0).doubleValue() * m.getValue(1,1).doubleValue() - m.getValue(1,0).doubleValue() * m.getValue(0,1).doubleValue();
        double det = 0.0;
        for (int i : range(m.size(0))) {
            Slice spanr = SliceUtil.range(1,m.size(1));
            Slice spanc;
            if (i == 0)
                spanc = SliceUtil.range(i+1,m.size(1));
            else if (i == m.size(1) - 1)
                spanc = SliceUtil.range(0,i);
            else
                spanc = SliceUtil.compositeSlice(SliceUtil.range(0,i),SliceUtil.range(i+1,m.size(0)));
//            System.out.println(Arrays.toString(spanc.getSliceIndices()));
            Index<?> index = SliceIndex.getDefaultIdSliceIndex(m.getIndex(),spanr,spanc);
            det += m.getValue(0,i).doubleValue() * ((i % 2) == 1 ? -1.0 : 1.0) * determinantByHand((Matrix<? extends Number>) (Matrix)  m.getReferenceTensor(index));
        }
        return det;
    }

    @Test
    @SuppressWarnings("unchecked") //all the unchecked matrix calls are ok - funny stuff to make the methods generic
    public void testInverse() {
        int size = random.nextInt(5,30);
        Matrix m = getRandomizedMatrix(size,size,type,factory,true);
        DoubleMatrix inverse = null;
        switch (type) {
            case BYTE : inverse = mc.inverse((ByteMatrix) m,mm); break;
            case SHORT : inverse = mc.inverse((ShortMatrix) m,mm); break;
            case INT : inverse = mc.inverse((IntMatrix) m,mm); break;
            case LONG : inverse = mc.inverse((LongMatrix) m,mm); break;
            case FLOAT : inverse = mc.inverse((FloatMatrix) m,mm); break;
            case DOUBLE : inverse = mc.inverse((DoubleMatrix) m,mm); break;
        }
        assertMatrixEquals((DoubleMatrix) TensorUtil.asDoubleTensor(TensorUtil.identityTensor(2,size)),mm.multiply((DoubleMatrix) TensorUtil.asDoubleTensor((Matrix<? extends Number>) m),inverse));
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked") //all the unchecked matrix calls are ok - funny stuff to make the methods generic
    public void testInverseSingular() {
        int size = random.nextInt(5,30);
        Matrix m = getRandomizedMatrix(size,size,type,factory,true);
        int r = random.nextInt(1,size);
        for (int i : range(m.size(1)))
            m.setValue(m.getValue(0,i),r,i); //copy from first row to r row

        switch (type) {
            case BYTE : mc.inverse((ByteMatrix) m,mm); break;
            case SHORT : mc.inverse((ShortMatrix) m,mm); break;
            case INT : mc.inverse((IntMatrix) m,mm); break;
            case LONG : mc.inverse((LongMatrix) m,mm); break;
            case FLOAT : mc.inverse((FloatMatrix) m,mm); break;
            case DOUBLE : mc.inverse((DoubleMatrix) m,mm); break;
        }
    }


}
