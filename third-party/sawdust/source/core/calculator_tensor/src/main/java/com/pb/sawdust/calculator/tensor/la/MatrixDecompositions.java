package com.pb.sawdust.calculator.tensor.la;

import com.pb.sawdust.calculator.tensor.CellWiseTensorCalculation;
import com.pb.sawdust.calculator.tensor.DefaultCellWiseTensorCalculation;
import com.pb.sawdust.calculator.tensor.la.mm.MatrixMultiplication;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.slice.Slice;
import com.pb.sawdust.tensor.slice.SliceUtil;
import com.pb.sawdust.tensor.decorators.primitive.size.DoubleD2TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.size.IntD2TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.AbstractIntTensor;
import com.pb.sawdust.tensor.index.PermutationIndex;
import com.pb.sawdust.tensor.index.SliceIndex;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.alias.matrix.primitive.*;
import com.pb.sawdust.tensor.alias.matrix.Matrix;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import static com.pb.sawdust.util.Range.range;
import com.pb.sawdust.util.concurrent.DnCRecursiveTask;
import com.pb.sawdust.util.concurrent.ForkJoinPoolFactory;
import com.pb.sawdust.util.concurrent.DnCRecursiveAction;
import com.pb.sawdust.util.Range;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.calculator.NumericFunctions;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The {@code MarixDecompositions} class holds methods and classes dealing with matrix decompositions.
 *
 * @author crf <br/>
 *         Started: July 20, 2011 2:07:57 PM
 */
public class MatrixDecompositions {
    private static final int CHOLESKY_FJP_PARALLELISM =  Runtime.getRuntime().availableProcessors();
    private static int CHOLESKY_PARALLEL_ACTION_MINIMUM_SIZE = 250;
    private static final String CHOLESKY_NAME = "cholesky";

    private static final int QR_FJP_PARALLELISM =  Runtime.getRuntime().availableProcessors();
    private static int QR_PARALLEL_ACTION_MINIMUM_SIZE = 5;

    private static final int LU_FJP_PARALLELISM =  Runtime.getRuntime().availableProcessors()+2; //todo: why 2?
    private static int LU_PARALLEL_ACTION_MINIMUM_SIZE = 100;
    private static final String LU_NAME = "lu/determinant";

    private final TensorFactory factory;


    public MatrixDecompositions(TensorFactory factory) {
        this.factory = factory;
    }

    private void checkSquare(Matrix<?> matrix, String operation) {
        if (matrix.size(0) != matrix.size(1))
            throw new IllegalArgumentException("Matrix must be square for operation " + operation);
    }

    private static IntMatrix getPermutationMatrix(final PermutationIndex index, final boolean d0Permutation) {
        int size = d0Permutation ? index.size(0) : index.size(1);
        return new IntD2TensorShell(new AbstractIntTensor(new int[] {size,size}) {
                public int getCell(int... indices) {return 0;/*ignored*/}
                public void setCell(int value, int... indices) {/*ignored*/}
            }){
                public int getCell(int d0Index, int d1Index) {
                    if (d0Permutation)
                        return index.getIndex(0,d0Index) == d1Index ? 1 : 0;
                    else
                        return index.getIndex(1,d1Index) == d0Index ? 1 : 0;
                }

                public void setCell(int value, int d0Index, int d1Index) {
                    throw new UnsupportedOperationException("Tensor is unmodifiable");
                }
        };
    }

    /////////////////////cholesky///////////////////////
    // matrix = C*C(T) where C(T) is transpose of c
    private DoubleMatrix cholesky(DoubleMatrix matrix) {
        checkSquare(matrix,CHOLESKY_NAME);
        int size = matrix.size(0);
        final boolean parallel = size > CHOLESKY_PARALLEL_ACTION_MINIMUM_SIZE;
        ForkJoinPool pool = null;
        if (parallel)
            pool = ForkJoinPoolFactory.getForkJoinPool(CHOLESKY_FJP_PARALLELISM);

        for (int i : range(size)) {
            double d = 0.0;
            for (int j : range(i+1,size))
                if (matrix.getCell(i,j) != matrix.getCell(j,i))
                    throw new IllegalArgumentException("Matrix input to Cholesky decomposition must be symmetric");
                else
                    matrix.setCell(0.0,i,j);
            for (int j : range(i)) {
                double s = 0.0;
                if (parallel)
                    s = pool.invoke(new CholeskyTask(matrix,i,j));
                else
                    for (int k : range(j))
                        s += matrix.getCell(j,k)*matrix.getCell(i,k);
                s = (matrix.getCell(i,j) - s)/matrix.getCell(j,j);
                matrix.setCell(s,i,j);
                d += s*s;
            }
            if ((d = matrix.getCell(i,i) - d) < 0.0)
                throw new IllegalArgumentException("Matrix input to Cholesky decomposition must be positive definite.");
            matrix.setCell(Math.sqrt(d),i,i);
        }
        return matrix;
    }

    private class CholeskyTask extends DnCRecursiveTask<Double> {
        private static final long serialVersionUID = 5866264991223778557L;

        private final DoubleMatrix matrix;
        private final int row1;
        private final int row2;

        private CholeskyTask(DoubleMatrix matrix, int row1, int row2, long start, long length, DnCRecursiveTask<Double> next) {
            super(start,length,next);
            this.matrix =matrix;
            this.row1 = row1;
            this.row2 = row2;
        }

        private CholeskyTask(DoubleMatrix matrix, int row1, int row2) {
            super(0,row2);
            this.matrix =matrix;
            this.row1 = row1;
            this.row2 = row2;
        }

        @Override
        protected Double computeTask(long start, long length) {
            int end = (int) (start+length);
            double result = 0.0d;
            for (int i = (int) start; i < end; i++)
                result += matrix.getCell(row2,i)*matrix.getCell(row1,i);
            return result;
        }

        @Override
        protected DnCRecursiveTask<Double> getNextTask(long start, long length, DnCRecursiveTask<Double> next) {
            return new CholeskyTask(matrix,row1,row2,start,length,next);
        }

        @Override
        protected boolean continueDividing(long length) {
            return length > CHOLESKY_PARALLEL_ACTION_MINIMUM_SIZE && getSurplusQueuedTaskCount() <= 3;
        }

        @Override
        protected Double joinResults(Double result1, Double result2) {
            return result1 + result2;
        }
    }

    /////////////////QR////////////////////////////////
    // matrix = Q*R
    public class QRDecompositionImpl implements QRDecomposition {
        private final DoubleMatrix qr;
        private final DoubleVector rDiag;
        private final DoubleMatrix r;
        private DoubleMatrix q;
        private final IntMatrix p;
        private final boolean fullRank;
        private final AtomicBoolean qComputed;

        private QRDecompositionImpl(DoubleMatrix qr, DoubleVector rDiag, boolean fullRank, boolean pivoted) {
            this.qr = qr;
            this.rDiag = rDiag;
            this.fullRank = fullRank;
            Slice rSlice = SliceUtil.slice(Range.range(qr.size(1)).getRangeArray());
            r = (DoubleMatrix) TensorUtil.unmodifiableTensor(
                    new RMatrix((DoubleMatrix) qr.getReferenceTensor(
                            SliceIndex.getDefaultIdSliceIndex(qr.getIndex(),rSlice,rSlice))));
            q = (DoubleMatrix) factory.doubleTensor(qr.getDimensions());
            qComputed = new AtomicBoolean(false);
            if (pivoted)
                p = getPermutationMatrix((PermutationIndex) qr.getIndex(),false);
            else
                p = (IntMatrix) TensorUtil.identityTensor(2,qr.size(1));
        }

        private void computeQ() {
            int m = q.size(0);
            int n = q.size(1);
            for (int i = n-1; i >= 0; i--) {
                q.setCell(1.0,i,i);
                for (int j = i; j < n; j++) {
                    double d = qr.getCell(i,i);
                    if (d != 0.0) {
                        double s = 0.0;
                        for (int k = i; k < m; k++)
                            s += qr.getCell(k,i)*q.getCell(k,j);
                        s = -s/d;
                        for (int k = i; k < m; k++)
                            q.setCell(q.getCell(k,j)+s*qr.getCell(k,i),k,j);
                    }
                }
            }
            q = (DoubleMatrix) TensorUtil.unmodifiableTensor(q);
        }

        private class RMatrix extends DoubleD2TensorShell {
            private RMatrix(DoubleMatrix rMatrix) {
                super(rMatrix);
            }

            public double getCell(int index0, int index1) {
                if (index0 < index1)
                    return qr.getCell(index0,index1);
                else if (index0 == index1)
                    return rDiag.getCell(index0);
                TensorImplUtil.checkIndexLengths(getDimensions(),index0,index1);
                return 0.0;
            }
        }

        public DoubleMatrix getR() {
            return r;
        }

        public DoubleMatrix getQ() {
            synchronized (qComputed) {
                if (qComputed.compareAndSet(false,true))
                    computeQ();
            }
            return q;
        }

        public IntMatrix getP() {
            return p;
        }

        public boolean isFullRank() {
            return fullRank;
        }
    }

    private QRDecomposition qr(DoubleMatrix matrix, boolean pivot) {
        //note: m >= n is a requirement
        int m = matrix.size(0);
        int n = matrix.size(1);
        if (m < n)
            throw new IllegalArgumentException("QR decomposition requires that row count >= column count; row count=" + m + ", column count=" + n);
        final boolean parallel = Math.sqrt(m*n) > QR_PARALLEL_ACTION_MINIMUM_SIZE; //todo: this is intuitive, not sure if I am correct
        ForkJoinPool pool = null;
        if (parallel)
            pool = ForkJoinPoolFactory.getForkJoinPool(QR_FJP_PARALLELISM);

        DoubleVector rDiag = (DoubleVector) factory.doubleTensor(n);
        PermutationIndex index = null;
        if (pivot) {
            index = new PermutationIndex(matrix.getDimensions());
            matrix = (DoubleMatrix) matrix.getReferenceTensor(index);
        }

        boolean fullRank = true;
        for (int i = 0; i < n; i++) {
            double norm = matrix.getCell(i,i);
            double shift = Math.signum(norm);
            if (parallel)
                norm = Math.hypot(norm,pool.invoke(new QRTask(matrix,i,i+1,m)));
            else
                for (int j = i+1; j < m; j++)
                    norm = Math.hypot(norm,matrix.getCell(j,i));

            if (pivot) {
                //check all norms and pivot if necessary
                int pivotColumn = i;
                for (int j = i; j < n; j++) {
                    double norm2 = matrix.getCell(i,j);
                    double shift2 = Math.signum(norm);
                    if (parallel)
                        norm2 = Math.hypot(norm2,pool.invoke(new QRTask(matrix,j,i+1,m)));
                    else
                        for (int k = i+1; k < m; k++)
                            norm2 = Math.hypot(norm2,matrix.getCell(k,j));
                    if (norm2 > norm) {
                        pivotColumn = j;
                        norm = norm2;
                        shift = shift2;
                    }
                }
                if (pivotColumn != i)
                    index.permute(1,i,pivotColumn);
            }

            if (norm != 0.0) {
                norm = shift < 0 ? -norm : norm;
                if (parallel)
                    pool.invoke(new QRAction(matrix,norm,i,m));
                else
                    for (int j = i; j < m; j++)
                        matrix.setCell(matrix.getCell(j,i)/norm,j,i);
                matrix.setCell(matrix.getCell(i,i)+1.0,i,i);

                for (int j = i+1; j < n; j++) {
                    double s = 0.0;
                    if (parallel)
                        s = pool.invoke(new QRTask(matrix,i,j,i,m));
                    else
                        for (int k = i; k < m; k++)
                            s += matrix.getCell(k,i)*matrix.getCell(k,j);
                    s /= matrix.getCell(i,i);
                    if (parallel)
                        pool.invoke(new QRAction(matrix,s,i,j,m));
                    else
                        for (int k = i; k < m; k++)
                            matrix.setCell(matrix.getCell(k,j) - s*matrix.getCell(k,i),k,j);
                }
            } else {
                fullRank = false;
            }
            rDiag.setCell(-norm,i);
        }
        return new QRDecompositionImpl(matrix,rDiag,fullRank,pivot);
    }

    private class QRAction extends DnCRecursiveAction {
        private static final long serialVersionUID = 3691649274595012789L;

        private final int mode;
        private final DoubleMatrix matrix;
        private final double factor;
        private final int column1;
        private final int column2;

        private QRAction(DoubleMatrix matrix, int mode, double factor, int column1, int column2, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.matrix = matrix;
            this.mode = mode;
            this.factor = factor;
            this.column1 = column1;
            this.column2 = column2;
        }

        private QRAction(DoubleMatrix matrix, int mode, double factor, int column1, int column2, long d0Size) {
            super(column1,d0Size-column1);
            this.matrix = matrix;
            this.mode = mode;
            this.factor = factor;
            this.column1 = column1;
            this.column2 = column2;
        }

        private QRAction(DoubleMatrix matrix, double factor, int column1, int d0Size) {
            this(matrix,1,factor,column1,-1,d0Size);
        }

        private QRAction(DoubleMatrix matrix, double factor, int column1, int column2, int d0Size) {
            this(matrix,2,factor,column1,column2,d0Size);
        }

        @Override
        protected void computeAction(long start, long length) {
            int end = (int) (start + length);
            switch (mode) {
                case 1 : {
                    for (int i = (int) start; i < end; i++)
                        matrix.setCell(matrix.getCell(i,column1)/factor,i,column1);
                    break;
                }
                case 2 : {
                    for (int i = (int) start; i < end; i++)
                        matrix.setCell(matrix.getCell(i,column2) - factor*matrix.getCell(i,column1),i,column2);
                    break;
                }
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new QRAction(matrix,mode,factor,column1,column2,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > QR_PARALLEL_ACTION_MINIMUM_SIZE && getSurplusQueuedTaskCount() <= 3;
        }
    }

    private class QRTask extends DnCRecursiveTask<Double> {
        private static final long serialVersionUID = 1008966201771131655L;

        private final int mode;
        private final DoubleMatrix matrix;
        private final int column1;
        private final int column2;

        private QRTask(DoubleMatrix matrix, int mode, int column1, int column2, long start, long length, DnCRecursiveTask<Double> next) {
            super(start,length,next);
            this.mode = mode;
            this.matrix = matrix;
            this.column1 = column1;
            this.column2 = column2;
        }

        private QRTask(DoubleMatrix matrix, int mode, int column1, int column2, int start, int length) {
            super((long) start, (long) length);
            this.mode = mode;
            this.matrix = matrix;
            this.column1 = column1;
            this.column2 = column2;
        }

        private QRTask(DoubleMatrix matrix, int column1, int start, int d0Size) {
            this(matrix,1, column1,-1,start,d0Size-start);
        }

        private QRTask(DoubleMatrix matrix, int column1, int column2, int start, int d0Size) {
            this(matrix,2, column1,column2,start,d0Size-start);
        }

        @Override
        protected Double computeTask(long start, long length) {
            int s = (int) start;
            int end = (int) (start+length);
            switch (mode) {
                case 1 : {
                    if (length == 0)
                        return 0.0;
                    if (length == 1)
                        return matrix.getCell(s,column1);
                    double norm = Math.hypot(matrix.getCell(s,column1),matrix.getCell(s+1,column1));
                    for (int i = s+2; i < end; i++)
                        norm = Math.hypot(norm,matrix.getCell(i,column1));
                    return norm;
                }
                case 2 : {
                    double sum = 0.0;
                    for (int i = s; i < end; i++)
                        sum += matrix.getCell(i,column1)*matrix.getCell(i,column2);
                    return sum;
                }
                default : throw new IllegalStateException("Shouldn't be here.");
            }
        }

        @Override
        protected DnCRecursiveTask<Double> getNextTask(long start, long length, DnCRecursiveTask<Double> next) {
            return new QRTask(matrix,mode, column1,column2,start,length,next);
        }

        @Override
        protected boolean continueDividing(long length) {
            return length > QR_PARALLEL_ACTION_MINIMUM_SIZE && getSurplusQueuedTaskCount() <= 3;
        }

        @Override
        protected Double joinResults(Double result1, Double result2) {
            switch (mode) {
                case 1 : return Math.hypot(result1,result2);
                case 2 : return result1 + result2;
                default : throw new IllegalStateException("Shouldn't be here.");
            }
        }
    }



    /////////////////LU/////////////////////////////////
    //if a diagonal element is less than this value, it is treated as zero, which will cause the matrix to be called singular
    // this may not work very well with matrices with really small values, or (perhaps) ill-conditioned matrices
    // keep an eye out to see what happens
    private static final double SINGULAR_EPSILON_DOUBLE = 1.0e-10;
    private static final double SINGULAR_EPSILON_INTEGER = 1.0e-5;

    // P*matrix = L*U :: matrix = P(T)*L*U where P(T) is transpose of P
    /**
     * The {@code LUDecomposition} class provides access to the results of an LU matrix cecomposition. An LU matrix
     * decomposition decomposes an <code>N x N</code> matrix <code>A</code> into two <code>N x N</code> matrices,
     * <code>L</code> (lower triangular) and <code>U</code> (upper triangular) which, along with a permuation matrix
     * <code>P</code> (<code>N x N</code>) form the following relationship: <code>PA = LU</code>.  Note: the inverse of
     * <code>P</code> is equal to its transpose.
     */
    public class LUDecompositionImpl implements LUDecomposition {
        private final DoubleMatrix matrix;
        private final DoubleMatrix lMatrix;
        private final DoubleMatrix uMatrix;
        private final IntMatrix pMatrix;
        private final boolean isSingular;

        private LUDecompositionImpl(DoubleMatrix matrix, double determinant) {
            this.matrix = matrix;
            lMatrix = (DoubleMatrix) TensorUtil.unmodifiableTensor(new LMatrix());
            uMatrix = (DoubleMatrix) TensorUtil.unmodifiableTensor(new UMatrix());
            pMatrix = getPermutationMatrix((PermutationIndex) matrix.getIndex(),true);
            isSingular = determinant == 0.0d;
        }

        /**
         * Ge the <code>L</code> matrix for this decomposition. The returned matrix will be unmodifiable.
         *
         * @return this decomposition's <code>L</code> matrix.
         */
        public DoubleMatrix getL() {
            return lMatrix;
        }

        /**
         * Ge the <code>U</code> matrix for this decomposition. The returned matrix will be unmodifiable.
         *
         * @return this decomposition's <code>U</code> matrix.
         */
        public DoubleMatrix getU() {
            return uMatrix;
        }

        /**
         * Ge the permutation (<code>P</code>) matrix for this decomposition. The returned matrix will be unmodifiable.
         *
         * @return this decomposition's permutation matrix.
         */
        public IntMatrix getP() {
            return pMatrix;
        }

        /**
         * Tells whether the input matrix is singular or not.
         *
         * @return {@code true} if the input matrix is singular, {@code false} otherwise.
         */
        public boolean isSingular() {
            return isSingular;
        }

//        int[] getRowPermutation() {
//            return ((PermutationIndex) matrix.getIndex()).getPermutation(0);
//        }

        private class LMatrix extends DoubleD2TensorShell {
            private LMatrix() {
                super(matrix);
            }

            public double getCell(int index0, int index1) {
                if (index0 < index1) {
                    TensorImplUtil.checkIndexLengths(getDimensions(),index0,index1);
                    return 0.0d;
                } else {
                    return matrix.getCell(index0,index1);
                }
            }
        }

        private class UMatrix extends DoubleD2TensorShell {
            private UMatrix() {
                super(matrix);
            }

            public double getCell(int index0, int index1) {
                if (index0 > index1) {
                    TensorImplUtil.checkIndexLengths(getDimensions(),index0,index1);
                    return 0.0d;
                } else if(index0 == index1) {
                    return 1.0d;
                } else {
                    return matrix.getCell(index0,index1);
                }
            }
        }
    }

    private double luDeterminant(DoubleMatrix matrix, final double singularEpsilon) { //decomposes in place - needs a double matrix with a permutation index
        checkSquare(matrix,LU_NAME);
        int size = matrix.size(0);
        final boolean parallel = size > LU_PARALLEL_ACTION_MINIMUM_SIZE;
        ForkJoinPool pool = null;
        if (parallel)
            pool = ForkJoinPoolFactory.getForkJoinPool(LU_FJP_PARALLELISM);
        PermutationIndex index = (PermutationIndex) matrix.getIndex();
        boolean parity = false;
        double det = 1.0f;
        for (int i : range(size)) {
            parity ^= pivot(matrix,index,i);
            double diag = matrix.getCell(i,i);
            //det *= diag;
            det *= Math.abs(diag) < singularEpsilon ? 0.0 : diag;
            Range r = range(i+1,size);
            if (parallel)
                pool.invoke(new DeterminantAction(0,matrix,i,diag,-1,size));
            else
                for (int j : r)
                    matrix.setCell(matrix.getCell(i,j)/diag,i,j);
            double mult;
            for (int j : r)
                if ((mult = matrix.getCell(j,i)) != 0.0) {
                    if (parallel)
                        pool.invoke(new DeterminantAction(1,matrix,i,mult,j,size));
                    else
                        for (int k : r)
                            matrix.setCell(matrix.getCell(j,k)-mult*matrix.getCell(i,k),j,k);
                }
        }
        return Double.isNaN(det) ? 0.0 : parity ? -1*det : det; //NaNs in diagonal indicate singularity and invalid LU decomp
    }

    private boolean pivot(DoubleMatrix matrix, PermutationIndex index, int point) {
        double v = matrix.getCell(point,point);
        int p = point;
        for (int i : range(point+1,matrix.size(0))) {
            double ov = Math.abs(matrix.getCell(i,point));
            if (ov > v) {
                v = ov;
                p = i;
            }
        }
        if (p > point) {
            index.permute(0,point,p);
            return true;
        }
        return false;
    }

    private class DeterminantAction extends DnCRecursiveAction {
        private static final long serialVersionUID = 568920239742841119L;

        private final int code;
        private final int row;
        private final double data;
        private final DoubleMatrix matrix;
        private final int loopPoint;

        private DeterminantAction(int code, DoubleMatrix matrix, int row, double data, int loopPoint, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.code = code;
            this.row = row;
            this.data = data;
            this.loopPoint = loopPoint;
            this.matrix = matrix;
        }

        private DeterminantAction(int code, DoubleMatrix matrix, int row, double data, int loopPoint, int size) {
            super(row+1,size-row-1);
            this.code = code;
            this.row = row;
            this.data = data;
            this.loopPoint = loopPoint;
            this.matrix = matrix;
        }

        @Override
        protected void computeAction(long start, long length) {
            int end = (int) (start+length);
            switch (code) {
                case 0 : { //rescale by diagonal
                    for (int i = (int) start; i < end; i++)
                        matrix.setCell(matrix.getCell(row,i)/data,row,i); //data is diagonal
                    return;
                }
                case 1 : { //update loop
                    for (int i = (int) start; i < end; i++)
                        matrix.setCell(matrix.getCell(loopPoint,i)-data*matrix.getCell(row,i),loopPoint,i); //data is first multiplication factor
                }
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new DeterminantAction(code,matrix,row,data,loopPoint,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > LU_PARALLEL_ACTION_MINIMUM_SIZE && getSurplusQueuedTaskCount() <= 3;
        }
    }

    ////////////////matrix inverse////////////////////////
    DoubleMatrix inverse(DoubleMatrix m, MatrixMultiplication mm, JavaType type) {
        LUDecompositionImpl lu = (LUDecompositionImpl) luDecomposition(m,type);
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(factory);
        if (lu.isSingular())
            throw new IllegalArgumentException("Input matrix is singular, inverse cannot be formed.");
        m = lu.matrix;
        int sizeM1 = m.size(0)-1;
        m.setCell(1/m.getCell(sizeM1,sizeM1),sizeM1,sizeM1);
        for (int i = sizeM1-1; i >=0; i--) {
            double diag = m.getCell(i,i);
            Slice span = SliceUtil.span(i+1,sizeM1);
            Index<?> index = SliceIndex.getDefaultIdSliceIndex(m.getIndex(),span,span);
            DoubleMatrix x = (DoubleMatrix) m.getReferenceTensor(index);

            Index<?> uIndex = SliceIndex.getDefaultIdSliceIndex(m.getIndex(),SliceUtil.slice(i),span);
            DoubleMatrix uTemp = (DoubleMatrix) m.getReferenceTensor(uIndex);
            DoubleMatrix temp = (DoubleMatrix) TensorUtil.copyOf(uTemp,factory);
            DoubleMatrix temp2 = (DoubleMatrix) cwtc.calculate(mm.multiply(uTemp,x), NumericFunctions.NEGATE);
            TensorUtil.copyTo(temp2,uTemp);

            Index<?> lIndex = SliceIndex.getDefaultIdSliceIndex(m.getIndex(),span,SliceUtil.slice(i));
            DoubleMatrix lTemp = (DoubleMatrix) m.getReferenceTensor(lIndex);
            temp2 = (DoubleMatrix) cwtc.calculate(mm.multiply(x,lTemp),-1.0/diag,NumericFunctions.MULTIPLY);
            TensorUtil.copyTo(temp2,lTemp);

            m.setCell(1/diag - mm.multiply(temp,lTemp).getCell(0,0),i,i);
        }
        return mm.multiply(m,lu.getP());
    }


    ///////////////access methods//////////////////
    /**
     * Get the Cholesky decomposition for a matrix. The returned matrix, <code>L</code>, has the following relationship
     * with the input matrix:
     * <pre>
     *     <code>matrix = L*transpose(L)</code>
     * </pre>
     * The input matrix must be symmetric and positive definite.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @return the Cholesky decomposition of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix} is not symmetric or is not positive definite.
     */
    public DoubleMatrix choleskyDecomposition(ByteMatrix matrix) {
        DoubleMatrix m = (DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory);
        cholesky(m);
        return m;
    }

    /**
     * Get the Cholesky decomposition for a matrix. The returned matrix, <code>L</code>, has the following relationship
     * with the input matrix:
     * <pre>
     *     <code>matrix = L*transpose(L)</code>
     * </pre>
     * The input matrix must be symmetric and positive definite.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @return the Cholesky decomposition of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix} is not symmetric or is not positive definite.
     */
    public DoubleMatrix choleskyDecomposition(ShortMatrix matrix) {
        DoubleMatrix m = (DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory);
        cholesky(m);
        return m;
    }

    /**
     * Get the Cholesky decomposition for a matrix. The returned matrix, <code>L</code>, has the following relationship
     * with the input matrix:
     * <pre>
     *     <code>matrix = L*transpose(L)</code>
     * </pre>
     * The input matrix must be symmetric and positive definite.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @return the Cholesky decomposition of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix} is not symmetric or is not positive definite.
     */
    public DoubleMatrix choleskyDecomposition(IntMatrix matrix) {
        DoubleMatrix m = (DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory);
        cholesky(m);
        return m;
    }

    /**
     * Get the Cholesky decomposition for a matrix. The returned matrix, <code>L</code>, has the following relationship
     * with the input matrix:
     * <pre>
     *     <code>matrix = L*transpose(L)</code>
     * </pre>
     * The input matrix must be symmetric and positive definite.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @return the Cholesky decomposition of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix} is not symmetric or is not positive definite.
     */
    public DoubleMatrix choleskyDecomposition(LongMatrix matrix) {
        DoubleMatrix m = (DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory);
        cholesky(m);
        return m;
    }

    /**
     * Get the Cholesky decomposition for a matrix. The returned matrix, <code>L</code>, has the following relationship
     * with the input matrix:
     * <pre>
     *     <code>matrix = L*transpose(L)</code>
     * </pre>
     * The input matrix must be symmetric and positive definite.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @return the Cholesky decomposition of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix} is not symmetric or is not positive definite.
     */
    public DoubleMatrix choleskyDecomposition(FloatMatrix matrix) {
        DoubleMatrix m = (DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory);
        cholesky(m);
        return m;
    }

    /**
     * Get the Cholesky decomposition for a matrix. The returned matrix, <code>L</code>, has the following relationship
     * with the input matrix:
     * <pre>
     *     <code>matrix = L*transpose(L)</code>
     * </pre>
     * The input matrix must be symmetric and positive definite.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @return the Cholesky decomposition of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix} is not symmetric or is not positive definite.
     */
    public DoubleMatrix choleskyDecomposition(DoubleMatrix matrix) {
        DoubleMatrix m = (DoubleMatrix) TensorUtil.copyOf(matrix,factory);
        cholesky(m);
        return m;
    }

//    /**
//     * Get the Cholesky decomposition for a matrix, placing the result in the input matrix. When the method completes,
//     * the resulting matrix, <code>L</code>, has the following relationship with the original matrix:
//     * <pre>
//     *     <code>matrix = L*transpose(L)</code>
//     * </pre>
//     * The input matrix must be symmetric and positive definite.
//     *
//     * @param matrix
//     *        The matrix to decompose and store the result in.
//     *
//     * @throws IllegalArgumentException if {@code matrix} is not symmetric or is not positive definite.
//     */
//    public void choleskyDecompositionInPlace(DoubleMatrix matrix) {
//        cholesky(matrix);
//    }

    /**
     * Get the LU decomposition for a matrix. Details of the LU decomposition can be found in the documentation for
     * the returned object {@link LUDecomposition}.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @return the LU decomposition of {@code matrix}.
     */
    public LUDecomposition luDecomposition(ByteMatrix matrix) {
        return luDecompositionInPlace((DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory),matrix.getType());
    }

    /**
     * Get the LU decomposition for a matrix. Details of the LU decomposition can be found in the documentation for
     * the returned object {@link LUDecomposition}.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @return the LU decomposition of {@code matrix}.
     */
    public LUDecomposition luDecomposition(ShortMatrix matrix) {
        return luDecompositionInPlace((DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory),matrix.getType());
    }

    /**
     * Get the LU decomposition for a matrix. Details of the LU decomposition can be found in the documentation for
     * the returned object {@link LUDecomposition}.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @return the LU decomposition of {@code matrix}.
     */
    public LUDecomposition luDecomposition(IntMatrix matrix) {
        return luDecompositionInPlace((DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory),matrix.getType());
    }

    /**
     * Get the LU decomposition for a matrix. Details of the LU decomposition can be found in the documentation for
     * the returned object {@link LUDecomposition}.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @return the LU decomposition of {@code matrix}.
     */
    public LUDecomposition luDecomposition(LongMatrix matrix) {
        return luDecompositionInPlace((DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory),matrix.getType());
    }

    /**
     * Get the LU decomposition for a matrix. Details of the LU decomposition can be found in the documentation for
     * the returned object {@link LUDecomposition}.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @return the LU decomposition of {@code matrix}.
     */
    public LUDecomposition luDecomposition(FloatMatrix matrix) {
        return luDecompositionInPlace((DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory),matrix.getType());
    }

    /**
     * Get the LU decomposition for a matrix. Details of the LU decomposition can be found in the documentation for
     * the returned object {@link LUDecomposition}
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @return the LU decomposition of {@code matrix}.
     */
    public LUDecomposition luDecomposition(DoubleMatrix matrix) {
        return luDecompositionInPlace((DoubleMatrix) TensorUtil.copyOf(matrix,factory),matrix.getType());
    }

    private LUDecomposition luDecomposition(DoubleMatrix matrix, JavaType type) {
        return luDecompositionInPlace((DoubleMatrix) TensorUtil.copyOf(matrix,factory),type);
    }

    private double getSingularEpsilon(JavaType type) {
        switch (type) {
            case BYTE :
            case SHORT :
            case INT :
            case LONG : return SINGULAR_EPSILON_INTEGER;
            case FLOAT :
            case DOUBLE : return SINGULAR_EPSILON_DOUBLE;
            default : throw new IllegalStateException("Should not be here.");
        }
    }

    private DoubleMatrix getPermutationMatrix(DoubleMatrix matrix) {
        return (DoubleMatrix) matrix.getReferenceTensor(new PermutationIndex(matrix.getDimensions()));
    }

    private LUDecomposition luDecompositionInPlace(DoubleMatrix matrix, JavaType type) {
        matrix = getPermutationMatrix(matrix);
        double det = luDeterminant(matrix,getSingularEpsilon(type));
        return new LUDecompositionImpl(matrix,det);
    }

    /**
     * Get the QR decomposition for a matrix. Details of the QR decomposition can be found in the documentation for
     * the returned object {@link QRDecomposition}. The {@code pivot} parameter specifies whether row pivoting should be
     * used during the decomposition; allowing row pivoting can improve the numerical stability of the method. If pivoting
     * is turned off, the the permutation matrix in the returned result will be the identity matrix.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @param pivot
     *        If {@code true}, then row pivoting will be used when decomposing the matrix.
     *
     * @return the QR decomposition of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix.size(0) < matrix.size(1)}.
     */
    public QRDecomposition qrDecomposition(ByteMatrix matrix, boolean pivot) {
        return qrDecompositionInPlace((DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory),pivot);
    }

    /**
     * Get the QR decomposition for a matrix. Details of the QR decomposition can be found in the documentation for
     * the returned object {@link QRDecomposition}. The {@code pivot} parameter specifies whether row pivoting should be
     * used during the decomposition; allowing row pivoting can improve the numerical stability of the method. If pivoting
     * is turned off, the the permutation matrix in the returned result will be the identity matrix.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @param pivot
     *        If {@code true}, then row pivoting will be used when decomposing the matrix.
     *
     * @return the QR decomposition of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix.size(0) < matrix.size(1)}.
     */
    public QRDecomposition qrDecomposition(ShortMatrix matrix, boolean pivot) {
        return qrDecompositionInPlace((DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory),pivot);
    }

    /**
     * Get the QR decomposition for a matrix. Details of the QR decomposition can be found in the documentation for
     * the returned object {@link QRDecomposition}. The {@code pivot} parameter specifies whether row pivoting should be
     * used during the decomposition; allowing row pivoting can improve the numerical stability of the method. If pivoting
     * is turned off, the the permutation matrix in the returned result will be the identity matrix.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @param pivot
     *        If {@code true}, then row pivoting will be used when decomposing the matrix.
     *
     * @return the QR decomposition of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix.size(0) < matrix.size(1)}.
     */
    public QRDecomposition qrDecomposition(IntMatrix matrix, boolean pivot) {
        return qrDecompositionInPlace((DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory),pivot);
    }

    /**
     * Get the QR decomposition for a matrix. Details of the QR decomposition can be found in the documentation for
     * the returned object {@link QRDecomposition}. The {@code pivot} parameter specifies whether row pivoting should be
     * used during the decomposition; allowing row pivoting can improve the numerical stability of the method. If pivoting
     * is turned off, the the permutation matrix in the returned result will be the identity matrix.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @param pivot
     *        If {@code true}, then row pivoting will be used when decomposing the matrix.
     *
     * @return the QR decomposition of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix.size(0) < matrix.size(1)}.
     */
    public QRDecomposition qrDecomposition(LongMatrix matrix, boolean pivot) {
        return qrDecompositionInPlace((DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory),pivot);
    }

    /**
     * Get the QR decomposition for a matrix. Details of the QR decomposition can be found in the documentation for
     * the returned object {@link QRDecomposition}. The {@code pivot} parameter specifies whether row pivoting should be
     * used during the decomposition; allowing row pivoting can improve the numerical stability of the method. If pivoting
     * is turned off, the the permutation matrix in the returned result will be the identity matrix.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @param pivot
     *        If {@code true}, then row pivoting will be used when decomposing the matrix.
     *
     * @return the QR decomposition of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix.size(0) < matrix.size(1)}.
     */
    public QRDecomposition qrDecomposition(FloatMatrix matrix, boolean pivot) {
        return qrDecompositionInPlace((DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory),pivot);
    }

    /**
     * Get the QR decomposition for a matrix. Details of the QR decomposition can be found in the documentation for
     * the returned object {@link QRDecomposition}. The {@code pivot} parameter specifies whether row pivoting should be
     * used during the decomposition; allowing row pivoting can improve the numerical stability of the method. If pivoting
     * is turned off, the the permutation matrix in the returned result will be the identity matrix.
     *
     * @param matrix
     *        The matrix to decompose.
     *
     * @param pivot
     *        If {@code true}, then row pivoting will be used when decomposing the matrix.
     *
     * @return the QR decomposition of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix.size(0) < matrix.size(1)}.
     */
    public QRDecomposition qrDecomposition(DoubleMatrix matrix, boolean pivot) {
        return qrDecompositionInPlace((DoubleMatrix) TensorUtil.copyOf(matrix,factory),pivot);
    }

    private QRDecomposition qrDecompositionInPlace(DoubleMatrix matrix, boolean pivot) {
        matrix = (DoubleMatrix) matrix.getReferenceTensor(new PermutationIndex(matrix.getDimensions()));
        return qr(matrix,pivot);
    }

    /**
     * Get the determinant for a matrix.
     *
     * @param matrix
     *        The matrix for which the determinant will be calculated.
     *
     * @return the determinant of {@code matrix}.
     */
    public long determinant(ByteMatrix matrix) {
        return Math.round(luDeterminant(getPermutationMatrix((DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory)),getSingularEpsilon(matrix.getType())));
    }

    /**
     * Get the determinant for a matrix.
     *
     * @param matrix
     *        The matrix for which the determinant will be calculated.
     *
     * @return the determinant of {@code matrix}.
     */
    public long determinant(ShortMatrix matrix) {
        return Math.round(luDeterminant(getPermutationMatrix((DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory)),getSingularEpsilon(matrix.getType())));
    }

    /**
     * Get the determinant for a matrix.
     *
     * @param matrix
     *        The matrix for which the determinant will be calculated.
     *
     * @return the determinant of {@code matrix}.
     */
    public long determinant(IntMatrix matrix) {
        return Math.round(luDeterminant(getPermutationMatrix((DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory)),getSingularEpsilon(matrix.getType())));
    }

    /**
     * Get the determinant for a matrix.
     *
     * @param matrix
     *        The matrix for which the determinant will be calculated.
     *
     * @return the determinant of {@code matrix}.
     */
    public long determinant(LongMatrix matrix) {
        return Math.round(luDeterminant(getPermutationMatrix((DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory)),getSingularEpsilon(matrix.getType())));
    }

    /**
     * Get the determinant for a matrix.
     *
     * @param matrix
     *        The matrix for which the determinant will be calculated.
     *
     * @return the determinant of {@code matrix}.
     */
    public double determinant(FloatMatrix matrix) {
        return luDeterminant(getPermutationMatrix((DoubleMatrix) TensorUtil.copyOfAsDouble(matrix,factory)),getSingularEpsilon(matrix.getType()));
    }

    /**
     * Get the determinant for a matrix.
     *
     * @param matrix
     *        The matrix for which the determinant will be calculated.
     *
     * @return the determinant of {@code matrix}.
     */
    public double determinant(DoubleMatrix matrix) {
        return luDeterminant(getPermutationMatrix((DoubleMatrix) TensorUtil.copyOf(matrix,factory)),getSingularEpsilon(matrix.getType()));
    }

    /**
     * Get the inverse of a matrix. The inverse, {@code N}, has the following relationship with the input matrix:
     * <pre>
     *     <code>matrix*N = I</code>
     * </pre>
     * where <code>I</code> is the identity matrix. If the input matrix is singular, then an inverse does not exist and
     * an exception will be thrown.
     *
     * @param matrix
     *        The matrix for which the inverse will be calculated.
     *
     * @param mm
     *        The matrix multiplication implementation which will be used to calculate inverse.
     *
     * @return the inverse of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix} is singular.
     */
    public DoubleMatrix inverse(ByteMatrix matrix, MatrixMultiplication mm) {
        return inverse((DoubleMatrix) TensorUtil.asDoubleTensor(matrix),mm,matrix.getType());
    }

    /**
     * Get the inverse of a matrix. The inverse, {@code N}, has the following relationship with the input matrix:
     * <pre>
     *     <code>matrix*N = I</code>
     * </pre>
     * where <code>I</code> is the identity matrix. If the input matrix is singular, then an inverse does not exist and
     * an exception will be thrown.
     *
     * @param matrix
     *        The matrix for which the inverse will be calculated.
     *
     * @param mm
     *        The matrix multiplication implementation which will be used to calculate inverse.
     *
     * @return the inverse of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix} is singular.
     */
    public DoubleMatrix inverse(ShortMatrix matrix, MatrixMultiplication mm) {
        return inverse((DoubleMatrix) TensorUtil.asDoubleTensor(matrix),mm,matrix.getType());
    }

    /**
     * Get the inverse of a matrix. The inverse, {@code N}, has the following relationship with the input matrix:
     * <pre>
     *     <code>matrix*N = I</code>
     * </pre>
     * where <code>I</code> is the identity matrix. If the input matrix is singular, then an inverse does not exist and
     * an exception will be thrown.
     *
     * @param matrix
     *        The matrix for which the inverse will be calculated.
     *
     * @param mm
     *        The matrix multiplication implementation which will be used to calculate inverse.
     *
     * @return the inverse of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix} is singular.
     */
    public DoubleMatrix inverse(IntMatrix matrix, MatrixMultiplication mm) {
        return inverse((DoubleMatrix) TensorUtil.asDoubleTensor(matrix),mm,matrix.getType());
    }

    /**
     * Get the inverse of a matrix. The inverse, {@code N}, has the following relationship with the input matrix:
     * <pre>
     *     <code>matrix*N = I</code>
     * </pre>
     * where <code>I</code> is the identity matrix. If the input matrix is singular, then an inverse does not exist and
     * an exception will be thrown.
     *
     * @param matrix
     *        The matrix for which the inverse will be calculated.
     *
     * @param mm
     *        The matrix multiplication implementation which will be used to calculate inverse.
     *
     * @return the inverse of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix} is singular.
     */
    public DoubleMatrix inverse(LongMatrix matrix, MatrixMultiplication mm) {
        return inverse((DoubleMatrix) TensorUtil.asDoubleTensor(matrix),mm,matrix.getType());
    }

    /**
     * Get the inverse of a matrix. The inverse, {@code N}, has the following relationship with the input matrix:
     * <pre>
     *     <code>matrix*N = I</code>
     * </pre>
     * where <code>I</code> is the identity matrix. If the input matrix is singular, then an inverse does not exist and
     * an exception will be thrown.
     *
     * @param matrix
     *        The matrix for which the inverse will be calculated.
     *
     * @param mm
     *        The matrix multiplication implementation which will be used to calculate inverse.
     *
     * @return the inverse of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix} is singular.
     */
    public DoubleMatrix inverse(FloatMatrix matrix, MatrixMultiplication mm) {
        return inverse((DoubleMatrix) TensorUtil.asDoubleTensor(matrix),mm,matrix.getType());
    }

    /**
     * Get the inverse of a matrix. The inverse, {@code N}, has the following relationship with the input matrix:
     * <pre>
     *     <code>matrix*N = I</code>
     * </pre>
     * where <code>I</code> is the identity matrix. If the input matrix is singular, then an inverse does not exist and
     * an exception will be thrown.
     *
     * @param matrix
     *        The matrix for which the inverse will be calculated.
     *
     * @param mm
     *        The matrix multiplication implementation which will be used to calculate inverse.
     *
     * @return the inverse of {@code matrix}.
     *
     * @throws IllegalArgumentException if {@code matrix} is singular.
     */
    public DoubleMatrix inverse(DoubleMatrix matrix, MatrixMultiplication mm) {
        return inverse((DoubleMatrix) TensorUtil.asDoubleTensor(matrix),mm,matrix.getType());
    }

    /**
     * A {@code LUDecomposition} class provides access to the results of an LU matrix cecomposition. An LU matrix
     * decomposition decomposes an <code>N x N</code> matrix <code>A</code> into two <code>N x N</code> matrices,
     * <code>L</code> (lower triangular) and <code>U</code> (upper triangular) which, along with a permuation matrix
     * <code>P</code> (<code>N x N</code>) form the following relationship: <code>PA = LU</code>.  Note: the inverse of
     * <code>P</code> is equal to its transpose.
     */
    public interface LUDecomposition {
        /**
         * Ge the <code>L</code> matrix for this decomposition. The returned matrix will be unmodifiable.
         *
         * @return this decomposition's <code>L</code> matrix.
         */
        DoubleMatrix getL();

        /**
         * Ge the <code>U</code> matrix for this decomposition. The returned matrix will be unmodifiable.
         *
         * @return this decomposition's <code>U</code> matrix.
         */
        DoubleMatrix getU();

        /**
         * Ge the permutation (<code>P</code>) matrix for this decomposition. The returned matrix will be unmodifiable.
         *
         * @return this decomposition's permutation matrix.
         */
        IntMatrix getP();

        /**
         * Tells whether the input matrix is singular or not.
         *
         * @return {@code true} if the input matrix is singular, {@code false} otherwise.
         */
        boolean isSingular();
    }

    /**
     * A {@code QRDecomposition} class provides access to the results of a QR matrix cecomposition. A QR matrix
     * decomposition decomposes an <code>M x N</code> matrix (where <code>M >= N</code>) into two matrices, <code>Q</code>
     * (<code>M x N</code>) and <code>R</code> (<code>N x N</code> and upper triangular) which, along with a permuation
     * matrix <code>P</code>  (<code>M x M</code>) form the following relationship: <code>AP = QR</code>.  Note: the
     * inverse of <code>P</code> is equal to its transpose.
     */
    public interface QRDecomposition {
        /**
         * Ge the <code>R</code> matrix for this decomposition. The returned matrix will be unmodifiable.
         *
         * @return this decomposition's <code>R</code> matrix.
         */
        DoubleMatrix getR();

        /**
         * Ge the <code>Q</code> matrix for this decomposition. The returned matrix will be unmodifiable.
         *
         * @return this decomposition's <code>Q</code> matrix.
         */
        DoubleMatrix getQ();

        /**
         * Ge the permutation (<code>P</code>) matrix for this decomposition. The returned matrix will be unmodifiable.
         *
         * @return this decomposition's permutation matrix.
         */
        IntMatrix getP();

        /**
         * Tells whether the source matrix was full rank or not.
         *
         * @return {@code true} if the source matrix was full rank, {@code false} if not.
         */
        boolean isFullRank();
    }


//    static void main(String ... args) {
////        TensorFactory f = ArrayTensor.getFactory();
////        MatrixDecompositions md = new MatrixDecompositions(f);
////        PermutationIndex i = new PermutationIndex(4,5);
////        i.permute(0,1,2);
////        System.out.println(TensorUtil.toString(md.getPermutationMatrix(i,true)));
//
//
//
//        final RandomDeluxe r = new RandomDeluxe();
//        TensorFactory f = ArrayTensor.getFactory();
//        //DoubleMatrix t = (DoubleMatrix) f.doubleTensor(10,3);
//        DoubleMatrix t = (DoubleMatrix) f.doubleTensor(4,4);
//        TensorUtil.fill(t,new TensorUtil.DoubleTensorValueFunction() {
//            public double getValue(int[] indices) {
//                return r.nextInt(10);
//            }
//        });
//        MatrixDecompositions md = new MatrixDecompositions(f);
//        LinearAlgebra mc = LinearAlgebra.getMatrixCalculations(f);
//
//
//
//        ///lu
//        LUDecomposition lud = md.luDecomposition(t);
//        System.out.println(TensorUtil.toString(t));
//        System.out.println(TensorUtil.toString(lud.getL()));
//        System.out.println(TensorUtil.toString(lud.getU()));
//        System.out.println(TensorUtil.toString(lud.getP()));
//        //System.out.println(TensorUtil.toString(mc.multiplyT((DoubleMatrix) TensorUtil.asDoubleTensor(lud.getP()),mc.multiply(lud.getL(),lud.getU()))));
//        System.out.println(TensorUtil.toString(mc.multiply(lud.getL(),lud.getU())));


        ////// cholesky
//        for (int i = 0; i < t.size(0); i++) {
//            double sum = 0.0;
//            for (int j = 0; j < t.size(1); j++) {
//                if (i != j)
//                    sum += t.getCell(i,j);
//                if (i < j)
//                    t.setCell(t.getCell(i,j),j,i);
//            }
//            t.setCell(sum+r.nextDouble()*5,i,i);
//        }
//
//        DoubleMatrix c = md.choleskyDecomposition(t);
//        System.out.println(TensorUtil.toString(t));
//        System.out.println(TensorUtil.toString(mc.multiplyT2(c,c)));

        //// qr

//        System.out.println(TensorUtil.toString(t));
//        QRDecomposition qrd = md.qr(t);
//        System.out.println("R");
//        System.out.println(TensorUtil.toString(qrd.getR()));
//        System.out.println("Q");
//        System.out.println(TensorUtil.toString(qrd.getQ()));
//
//        System.out.println(TensorUtil.toString(mc.multiply(qrd.getQ(),qrd.getR())));
//        System.out.println(TensorUtil.toString(mc.multiplyT(qrd.getQ(),qrd.getQ())));

        ///qr with pivoting
//        System.out.println(TensorUtil.toString(t));
//        QRDecomposition qrd2 = md.qr(t,true);
//
//        System.out.println(TensorUtil.toString(mc.multiply(qrd2.getQ(),qrd2.getR())));
////        System.out.println(TensorUtil.toString(new MatrixMultiplication(f).multiply(qrd2.getQ(),qrd2.getR())));
//        System.out.println(TensorUtil.toString(mc.multiplyT2(mc.multiply(qrd2.getQ(),qrd2.getR()),(DoubleMatrix) TensorUtil.copyOfAsDouble(qrd2.getP(),f))));
//    }

}
