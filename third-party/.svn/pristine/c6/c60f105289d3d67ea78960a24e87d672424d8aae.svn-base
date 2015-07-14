package com.pb.sawdust.calculator.tensor.la.mm;

import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.FloatMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.IntMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.LongMatrix;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.concurrent.DnCRecursivePrimitiveAction;
import com.pb.sawdust.util.concurrent.ForkJoinPoolFactory;
import com.pb.sawdust.util.concurrent.LongCountDownLatch;
import com.pb.sawdust.util.exceptions.RuntimeInterruptedException;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Semaphore;

/**
 * The {@code DefaultMatrixMultiplication} provides a purely Java {@code MatrixMultiplication} implementation. Though it
 * is multi-threaded, it is still not as performant as native libraries (such as BLAS or CUDA), and so, in performance
 * critical situations, it is recommended that other {@code MatrixMultiplication} implementations be used instead.
 *
 * @author crf <br/>
 *         Started Dec 13, 2010 8:23:48 AM
 */
public class DefaultMatrixMultiplication extends AbstractMatrixMultiplication {
    private static final int MIN_PARALLEL_LENGTH = 5000;
    private static final int FJP_PARALLELISM = Runtime.getRuntime().availableProcessors()+2; //todo: see if 1 above processor count is good
    private static final int MAX_PARALLEL_ELEMENT_CALCS = 200000;


    /**
     * Constructor specifying the tensor factory used to create output matrices.
     *
     * @param factory
     *        The tensor factory.
     */
    public DefaultMatrixMultiplication(TensorFactory factory) {
        super(factory);
    }

    protected void multiply(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool(FJP_PARALLELISM);
        final int length = m1Dim[1];
        int rows = m1Dim[0];
        int cols = m2Dim[1];
        Semaphore block = new Semaphore(MAX_PARALLEL_ELEMENT_CALCS);  //only allows a certain number of elements to be calculated concurrently
        LongCountDownLatch latch = new LongCountDownLatch(((long) rows)*((long) cols)); //counter to ensure we've finished calculating before returning
        try {
            if (transpose1) {
                if (transpose2) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new DoubleMatrixMultiplicationElementActionT12(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                } else {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new DoubleMatrixMultiplicationElementActionT1(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                }
            } else {
                if (transpose2) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new DoubleMatrixMultiplicationElementActionT2(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                } else {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new DoubleMatrixMultiplicationElementAction(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                }
            }
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        }
    }

    protected void multiply(LongMatrix m1, LongMatrix m2, LongMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool(FJP_PARALLELISM);
        final int length = m1Dim[1];
        int rows = m1Dim[0];
        int cols = m2Dim[1];
        Semaphore block = new Semaphore(MAX_PARALLEL_ELEMENT_CALCS);  //only allows a certain number of elements to be calculated concurrently
        LongCountDownLatch latch = new LongCountDownLatch(((long) rows)*((long) cols)); //counter to ensure we've finished calculating before returning
        try {
            if (transpose1) {
                if (transpose2) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new LongMatrixMultiplicationElementActionT12(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                } else {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new LongMatrixMultiplicationElementActionT1(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                }
            } else {
                if (transpose2) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new LongMatrixMultiplicationElementActionT2(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                } else {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new LongMatrixMultiplicationElementAction(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                }
            }
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        }
    }

    protected void multiply(FloatMatrix m1, FloatMatrix m2, FloatMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool(FJP_PARALLELISM);
        final int length = m1Dim[1];
        int rows = m1Dim[0];
        int cols = m2Dim[1];
        Semaphore block = new Semaphore(MAX_PARALLEL_ELEMENT_CALCS);  //only allows a certain number of elements to be calculated concurrently
        LongCountDownLatch latch = new LongCountDownLatch(((long) rows)*((long) cols)); //counter to ensure we've finished calculating before returning
        try {
            if (transpose1) {
                if (transpose2) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new FloatMatrixMultiplicationElementActionT12(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                } else {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new FloatMatrixMultiplicationElementActionT1(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                }
            } else {
                if (transpose2) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new FloatMatrixMultiplicationElementActionT2(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                } else {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new FloatMatrixMultiplicationElementAction(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                }
            }
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        }
    }

    protected void multiply(IntMatrix m1, IntMatrix m2, IntMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool(FJP_PARALLELISM);
        final int length = m1Dim[1];
        int rows = m1Dim[0];
        int cols = m2Dim[1];
        Semaphore block = new Semaphore(MAX_PARALLEL_ELEMENT_CALCS);  //only allows a certain number of elements to be calculated concurrently
        LongCountDownLatch latch = new LongCountDownLatch(((long) rows)*((long) cols)); //counter to ensure we've finished calculating before returning
        try {
            if (transpose1) {
                if (transpose2) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new IntMatrixMultiplicationElementActionT12(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                } else {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new IntMatrixMultiplicationElementActionT1(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                }
            } else {
                if (transpose2) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new IntMatrixMultiplicationElementActionT2(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                } else {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            block.acquire();
                            pool.execute(new IntMatrixMultiplicationElementAction(m1,m2,output,i,j,length,block,latch));
                        }
                    }
                }
            }
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        }
    }

    private abstract class AbstractDoubleMatrixMultplicationElementAction extends DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction {
        final DoubleMatrix m1;
        final DoubleMatrix m2;
        final DoubleMatrix om;
        final int row;
        final int col;
        private final Semaphore block;
        private final LongCountDownLatch latch;

        private AbstractDoubleMatrixMultplicationElementAction(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction next) {
            super(start,length,next);
            this.m1 = m1;
            this.m2 = m2;
            this.om = om;
            this.row = row;
            this.col = col;
            block = null;
            latch = null;
        }

        private AbstractDoubleMatrixMultplicationElementAction(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(0,length);
            this.m1 = m1;
            this.m2 = m2;
            this.om = om;
            this.row = row;
            this.col = col;
            this.block = block;
            this.latch = latch;
        }

        abstract double getElementSubCalculation(int index);

        @Override
        protected void computeAction(long start, long length) {
            int end = (int) (start+length);
            double sum = 0.0;
            for (int i = (int) start; i < end; i++)
                sum += getElementSubCalculation(i);
            setResult(sum);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            //return newLength > MIN_PARALLEL_LENGTH && getSurplusQueuedTaskCount() <= 3;
            return newLength > MIN_PARALLEL_LENGTH;
        }

        protected void compute() {
            super.compute();
            if (block != null) {
                block.release();
                om.setCell(getResultImmediately(),row,col);
                latch.countDown();
            }
        }
    }

    private class DoubleMatrixMultiplicationElementAction extends AbstractDoubleMatrixMultplicationElementAction {
        private static final long serialVersionUID = 2298223451789837429L;

        private DoubleMatrixMultiplicationElementAction(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private DoubleMatrixMultiplicationElementAction(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction next) {
            return new DoubleMatrixMultiplicationElementAction(m1,m2,om,row,col,start,length,next);
        }

        double getElementSubCalculation(int index) {
            return m1.getCell(row,index)*m2.getCell(index,col);
        }
    }

    private class DoubleMatrixMultiplicationElementActionT1 extends AbstractDoubleMatrixMultplicationElementAction {
        private static final long serialVersionUID = 3865516670985387739L;

        private DoubleMatrixMultiplicationElementActionT1(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private DoubleMatrixMultiplicationElementActionT1(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction next) {
            return new DoubleMatrixMultiplicationElementActionT1(m1,m2,om,row,col,start,length,next);
        }

        double getElementSubCalculation(int index) {
            return m1.getCell(index,row)*m2.getCell(index,col);
        }
    }

    private class DoubleMatrixMultiplicationElementActionT2 extends AbstractDoubleMatrixMultplicationElementAction {
        private static final long serialVersionUID = 1924602984346762857L;

        private DoubleMatrixMultiplicationElementActionT2(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private DoubleMatrixMultiplicationElementActionT2(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction next) {
            return new DoubleMatrixMultiplicationElementActionT2(m1,m2,om,row,col,start,length,next);
        }

        double getElementSubCalculation(int index) {
            return m1.getCell(row,index)*m2.getCell(col,index);
        }
    }

    private class DoubleMatrixMultiplicationElementActionT12 extends AbstractDoubleMatrixMultplicationElementAction {
        private static final long serialVersionUID = 336896789837368153L;

        private DoubleMatrixMultiplicationElementActionT12(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private DoubleMatrixMultiplicationElementActionT12(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction next) {
            return new DoubleMatrixMultiplicationElementActionT12(m1,m2,om,row,col,start,length,next);
        }

        double getElementSubCalculation(int index) {
            return m1.getCell(index,row)*m2.getCell(col,index);
        }
    }

    private abstract class AbstractLongMatrixMultplicationElementAction extends DnCRecursivePrimitiveAction.DnCRecursiveLongAction {
        final LongMatrix m1;
        final LongMatrix m2;
        final LongMatrix om;
        final int row;
        final int col;
        private final Semaphore block;
        private final LongCountDownLatch latch;

        private AbstractLongMatrixMultplicationElementAction(LongMatrix m1, LongMatrix m2, LongMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveLongAction next) {
            super(start,length,next);
            this.m1 = m1;
            this.m2 = m2;
            this.om = om;
            this.row = row;
            this.col = col;
            block = null;
            latch = null;
        }

        private AbstractLongMatrixMultplicationElementAction(LongMatrix m1, LongMatrix m2, LongMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(0,length);
            this.m1 = m1;
            this.m2 = m2;
            this.om = om;
            this.row = row;
            this.col = col;
            this.block = block;
            this.latch = latch;
        }

        abstract long getElementSubCalculation(int index);

        @Override
        protected void computeAction(long start, long length) {
            int end = (int) (start+length);
            long sum = 0L;
            for (int i = (int) start; i < end; i++)
                sum += getElementSubCalculation(i);
            setResult(sum);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > MIN_PARALLEL_LENGTH && getSurplusQueuedTaskCount() <= 3;
        }

        protected void compute() {
            super.compute();
            if (block != null) {
                block.release();
                om.setCell(getResultImmediately(),row,col);
                latch.countDown();
            }
        }
    }

    private class LongMatrixMultiplicationElementAction extends AbstractLongMatrixMultplicationElementAction {
        private static final long serialVersionUID = 2635589161193721849L;

        private LongMatrixMultiplicationElementAction(LongMatrix m1, LongMatrix m2, LongMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveLongAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private LongMatrixMultiplicationElementAction(LongMatrix m1, LongMatrix m2, LongMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveLongAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveLongAction next) {
            return new LongMatrixMultiplicationElementAction(m1,m2,om,row,col,start,length,next);
        }

        long getElementSubCalculation(int index) {
            return m1.getCell(row,index)*m2.getCell(index,col);
        }
    }

    private class LongMatrixMultiplicationElementActionT1 extends AbstractLongMatrixMultplicationElementAction {
        private static final long serialVersionUID = 5332409034575028264L;

        private LongMatrixMultiplicationElementActionT1(LongMatrix m1, LongMatrix m2, LongMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveLongAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private LongMatrixMultiplicationElementActionT1(LongMatrix m1, LongMatrix m2, LongMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveLongAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveLongAction next) {
            return new LongMatrixMultiplicationElementActionT1(m1,m2,om,row,col,start,length,next);
        }

        long getElementSubCalculation(int index) {
            return m1.getCell(index,row)*m2.getCell(index,col);
        }
    }

    private class LongMatrixMultiplicationElementActionT2 extends AbstractLongMatrixMultplicationElementAction {
        private static final long serialVersionUID = -5512712264807385731L;

        private LongMatrixMultiplicationElementActionT2(LongMatrix m1, LongMatrix m2, LongMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveLongAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private LongMatrixMultiplicationElementActionT2(LongMatrix m1, LongMatrix m2, LongMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveLongAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveLongAction next) {
            return new LongMatrixMultiplicationElementActionT2(m1,m2,om,row,col,start,length,next);
        }

        long getElementSubCalculation(int index) {
            return m1.getCell(row,index)*m2.getCell(col,index);
        }
    }

    private class LongMatrixMultiplicationElementActionT12 extends AbstractLongMatrixMultplicationElementAction {
        private static final long serialVersionUID = -8889854890151624371L;

        private LongMatrixMultiplicationElementActionT12(LongMatrix m1, LongMatrix m2, LongMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveLongAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private LongMatrixMultiplicationElementActionT12(LongMatrix m1, LongMatrix m2, LongMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveLongAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveLongAction next) {
            return new LongMatrixMultiplicationElementActionT12(m1,m2,om,row,col,start,length,next);
        }

        long getElementSubCalculation(int index) {
            return m1.getCell(index,row)*m2.getCell(col,index);
        }
    }

    private abstract class AbstractFloatMatrixMultplicationElementAction extends DnCRecursivePrimitiveAction.DnCRecursiveFloatAction {
        final FloatMatrix m1;
        final FloatMatrix m2;
        final FloatMatrix om;
        final int row;
        final int col;
        private final Semaphore block;
        private final LongCountDownLatch latch;

        private AbstractFloatMatrixMultplicationElementAction(FloatMatrix m1, FloatMatrix m2, FloatMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveFloatAction next) {
            super(start,length,next);
            this.m1 = m1;
            this.m2 = m2;
            this.om = om;
            this.row = row;
            this.col = col;
            block = null;
            latch = null;
        }

        private AbstractFloatMatrixMultplicationElementAction(FloatMatrix m1, FloatMatrix m2, FloatMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(0,length);
            this.m1 = m1;
            this.m2 = m2;
            this.om = om;
            this.row = row;
            this.col = col;
            this.block = block;
            this.latch = latch;
        }

        abstract float getElementSubCalculation(int index);

        @Override
        protected void computeAction(long start, long length) {
            int end = (int) (start+length);
            float sum = 0.0f;
            for (int i = (int) start; i < end; i++)
                sum += getElementSubCalculation(i);
            setResult(sum);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            //return newLength > MIN_PARALLEL_LENGTH && getSurplusQueuedTaskCount() <= 3;
            return newLength > MIN_PARALLEL_LENGTH;
        }

        protected void compute() {
            super.compute();
            if (block != null) {
                block.release();
                om.setCell(getResultImmediately(),row,col);
                latch.countDown();
            }
        }
    }

    private class FloatMatrixMultiplicationElementAction extends AbstractFloatMatrixMultplicationElementAction {
        static final long serialVersionUID = -8594934984435485448L;

        private FloatMatrixMultiplicationElementAction(FloatMatrix m1, FloatMatrix m2, FloatMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveFloatAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private FloatMatrixMultiplicationElementAction(FloatMatrix m1, FloatMatrix m2, FloatMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveFloatAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveFloatAction next) {
            return new FloatMatrixMultiplicationElementAction(m1,m2,om,row,col,start,length,next);
        }

        float getElementSubCalculation(int index) {
            return m1.getCell(row,index)*m2.getCell(index,col);
        }
    }

    private class FloatMatrixMultiplicationElementActionT1 extends AbstractFloatMatrixMultplicationElementAction {
        static final long serialVersionUID = 1976469876075799408L;

        private FloatMatrixMultiplicationElementActionT1(FloatMatrix m1, FloatMatrix m2, FloatMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveFloatAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private FloatMatrixMultiplicationElementActionT1(FloatMatrix m1, FloatMatrix m2, FloatMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveFloatAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveFloatAction next) {
            return new FloatMatrixMultiplicationElementActionT1(m1,m2,om,row,col,start,length,next);
        }

        float getElementSubCalculation(int index) {
            return m1.getCell(index,row)*m2.getCell(index,col);
        }
    }

    private class FloatMatrixMultiplicationElementActionT2 extends AbstractFloatMatrixMultplicationElementAction {
        private static final long serialVersionUID = 4626133665614293672L;

        private FloatMatrixMultiplicationElementActionT2(FloatMatrix m1, FloatMatrix m2, FloatMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveFloatAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private FloatMatrixMultiplicationElementActionT2(FloatMatrix m1, FloatMatrix m2, FloatMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveFloatAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveFloatAction next) {
            return new FloatMatrixMultiplicationElementActionT2(m1,m2,om,row,col,start,length,next);
        }

        float getElementSubCalculation(int index) {
            return m1.getCell(row,index)*m2.getCell(col,index);
        }
    }

    private class FloatMatrixMultiplicationElementActionT12 extends AbstractFloatMatrixMultplicationElementAction {
        private static final long serialVersionUID = 7278439528238054657L;

        private FloatMatrixMultiplicationElementActionT12(FloatMatrix m1, FloatMatrix m2, FloatMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveFloatAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private FloatMatrixMultiplicationElementActionT12(FloatMatrix m1, FloatMatrix m2, FloatMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveFloatAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveFloatAction next) {
            return new FloatMatrixMultiplicationElementActionT12(m1,m2,om,row,col,start,length,next);
        }

        float getElementSubCalculation(int index) {
            return m1.getCell(index,row)*m2.getCell(col,index);
        }
    }

    private abstract class AbstractIntMatrixMultplicationElementAction extends DnCRecursivePrimitiveAction.DnCRecursiveIntAction {
        final IntMatrix m1;
        final IntMatrix m2;
        final IntMatrix om;
        final int row;
        final int col;
        private final Semaphore block;
        private final LongCountDownLatch latch;

        private AbstractIntMatrixMultplicationElementAction(IntMatrix m1, IntMatrix m2, IntMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveIntAction next) {
            super(start,length,next);
            this.m1 = m1;
            this.m2 = m2;
            this.om = om;
            this.row = row;
            this.col = col;
            block = null;
            latch = null;
        }

        private AbstractIntMatrixMultplicationElementAction(IntMatrix m1, IntMatrix m2, IntMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(0,length);
            this.m1 = m1;
            this.m2 = m2;
            this.om = om;
            this.row = row;
            this.col = col;
            this.block = block;
            this.latch = latch;
        }

        abstract int getElementSubCalculation(int index);

        @Override
        protected void computeAction(long start, long length) {
            int end = (int) (start+length);
            int sum = 0;
            for (int i = (int) start; i < end; i++)
                sum += getElementSubCalculation(i);
            setResult(sum);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            //return newLength > MIN_PARALLEL_LENGTH && getSurplusQueuedTaskCount() <= 3;
            return newLength > MIN_PARALLEL_LENGTH;
        }

        protected void compute() {
            super.compute();
            if (block != null) {
                block.release();
                om.setCell(getResultImmediately(),row,col);
                latch.countDown();
            }
        }
    }

    private class IntMatrixMultiplicationElementAction extends AbstractIntMatrixMultplicationElementAction {
        private static final long serialVersionUID = 1402561157736201933L;

        private IntMatrixMultiplicationElementAction(IntMatrix m1, IntMatrix m2, IntMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveIntAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private IntMatrixMultiplicationElementAction(IntMatrix m1, IntMatrix m2, IntMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveIntAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveIntAction next) {
            return new IntMatrixMultiplicationElementAction(m1,m2,om,row,col,start,length,next);
        }

        int getElementSubCalculation(int index) {
            return m1.getCell(row,index)*m2.getCell(index,col);
        }
    }

    private class IntMatrixMultiplicationElementActionT1 extends AbstractIntMatrixMultplicationElementAction {
        private static final long serialVersionUID = 6631843329937333870L;

        private IntMatrixMultiplicationElementActionT1(IntMatrix m1, IntMatrix m2, IntMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveIntAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private IntMatrixMultiplicationElementActionT1(IntMatrix m1, IntMatrix m2, IntMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveIntAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveIntAction next) {
            return new IntMatrixMultiplicationElementActionT1(m1,m2,om,row,col,start,length,next);
        }

        int getElementSubCalculation(int index) {
            return m1.getCell(index,row)*m2.getCell(index,col);
        }
    }

    private class IntMatrixMultiplicationElementActionT2 extends AbstractIntMatrixMultplicationElementAction {
        private static final long serialVersionUID = 7641005785345600186L;

        private IntMatrixMultiplicationElementActionT2(IntMatrix m1, IntMatrix m2, IntMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveIntAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private IntMatrixMultiplicationElementActionT2(IntMatrix m1, IntMatrix m2, IntMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveIntAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveIntAction next) {
            return new IntMatrixMultiplicationElementActionT2(m1,m2,om,row,col,start,length,next);
        }

        int getElementSubCalculation(int index) {
            return m1.getCell(row,index)*m2.getCell(col,index);
        }
    }

    private class IntMatrixMultiplicationElementActionT12 extends AbstractIntMatrixMultplicationElementAction {
        private static final long serialVersionUID = 86124851464375189L;

        private IntMatrixMultiplicationElementActionT12(IntMatrix m1, IntMatrix m2, IntMatrix om,
                                                int row, int col, long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveIntAction next) {
            super(m1,m2,om,row,col,start,length,next);
        }

        private IntMatrixMultiplicationElementActionT12(IntMatrix m1, IntMatrix m2, IntMatrix om,
                                                int row, int col, int length, Semaphore block, LongCountDownLatch latch) {
            super(m1,m2,om,row,col,length,block,latch);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveIntAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveIntAction next) {
            return new IntMatrixMultiplicationElementActionT12(m1,m2,om,row,col,start,length,next);
        }

        int getElementSubCalculation(int index) {
            return m1.getCell(index,row)*m2.getCell(col,index);
        }
    }
}
