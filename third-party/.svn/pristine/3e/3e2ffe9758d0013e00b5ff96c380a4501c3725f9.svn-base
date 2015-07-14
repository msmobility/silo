package com.pb.sawdust.model.integration.blas;

import com.pb.sawdust.calculator.tensor.la.mm.AbstractMatrixMultiplication;
import com.pb.sawdust.calculator.tensor.la.mm.MatrixMultiplication;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.FloatMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.IntMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.LongMatrix;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.RandomDeluxe;
import com.pb.sawdust.util.ThreadTimer;

import java.util.concurrent.TimeUnit;

/**
 * The {@code JBlasMatrixMultiplication} provides a {@code MatrixMultiplication} implementation which uses native BLAS
 * linear algebra libraries to perform its calculations. It is noted that currently the Windows 64-bit native library
 * is not working (and, when it is fixed, may be less performant than the 32-bit variant).
 *
 * @see com.pb.sawdust.model.integration.blas.JBlas
 *
 * @author crf <br/>
 *         Started Dec 13, 2010 8:29:21 AM
 */
public class JBlasMatrixMultiplication extends AbstractMatrixMultiplication {

    /**
     * Constructor specifying the tensor factory used to create output matrices.
     *
     * @param factory
     *        The tensor factory.
     */
    public JBlasMatrixMultiplication(TensorFactory factory) {
        super(factory);
    }

    protected void multiply(IntMatrix m1, IntMatrix m2, IntMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
        JBlas.gemm(m1, m2, output, true, transpose1, transpose2, 1, 1, m1Dim, m2Dim);
    }

    protected void multiply(LongMatrix m1, LongMatrix m2, LongMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
        JBlas.gemm(m1, m2, output, true, transpose1, transpose2, 1L, 1L, m1Dim, m2Dim);
    }

    protected void multiply(FloatMatrix m1, FloatMatrix m2, FloatMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
        JBlas.gemm(m1, m2, output, true, transpose1, transpose2, 1.0f, 1.0f, m1Dim, m2Dim);
    }

    protected void multiply(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
        JBlas.gemm(m1, m2, output, true, transpose1, transpose2, 1.0, 1.0, m1Dim, m2Dim);
    }

    public static void main(String ... args) {
        //System.setProperty("java.library.path",System.getProperty("java.library.path") + );

        ThreadTimer tt = new ThreadTimer(TimeUnit.SECONDS);
        System.out.println(System.getProperty("java.library.path"));

        //this next line turns on JBlas debugging
        //Logger.getLogger().setLevel(Logger.DEBUG);


        TensorFactory factory = ArrayTensor.getFactory();
        MatrixMultiplication mm = new JBlasMatrixMultiplication(factory);
//        JBlasMatrixMultiplication mm = new JBlasMatrixMultiplication(factory);
//        DefaultMatrixMultiplication mm = new DefaultMatrixMultiplication(factory);


        final RandomDeluxe r = new RandomDeluxe();
        DoubleMatrix m = (DoubleMatrix) factory.doubleTensor(2000,2000);
        TensorUtil.fill(m,new TensorUtil.DoubleTensorValueFunction() {
            public double getValue(int ... index) {
                return r.nextInt(1,6);
            }
        });

        DoubleMatrix m2 = (DoubleMatrix) factory.doubleTensor(2000,2000);
        TensorUtil.fill(m2,new TensorUtil.DoubleTensorValueFunction() {
            public double getValue(int ... index) {
                return r.nextInt(0,6);
            }
        });

//        System.out.println(TensorUtil.toString(m));
//        System.out.println(TensorUtil.toString(m2));

        tt.startTimer();
        mm.multiply(m,m2,false,false);
        System.out.println(tt.endTimer());

//        LinearAlgebra la = LinearAlgebra.getMatrixCalculations(factory);
//        System.out.println(TensorUtil.toString(mm.multiply(m,m2,false,false)));
//        System.out.println(TensorUtil.toString(la.multiply(m,m2)));
    }
}
