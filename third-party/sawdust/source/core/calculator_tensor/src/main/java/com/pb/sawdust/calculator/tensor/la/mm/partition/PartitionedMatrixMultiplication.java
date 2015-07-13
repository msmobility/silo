package com.pb.sawdust.calculator.tensor.la.mm.partition;

import com.pb.sawdust.calculator.tensor.la.mm.AbstractMatrixMultiplication;
import com.pb.sawdust.calculator.tensor.la.mm.MatrixMultiplication;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.FloatMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.IntMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.LongMatrix;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.SliceIndex;
import com.pb.sawdust.tensor.slice.Slice;
import com.pb.sawdust.tensor.slice.SliceUtil;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.ThreadTimer;
import com.pb.sawdust.util.exceptions.RuntimeInterruptedException;

import java.util.*;
import java.util.concurrent.*;

/**
 * The {@code PartitionedMatrixMultiplication} ...
 *
 * @author crf <br/>
 *         Started 1/9/11 4:48 PM
 */
public class PartitionedMatrixMultiplication extends AbstractMatrixMultiplication {
    private final List<MatrixMultiplicationResource> mm;
    private final MatrixMultiplicationResource mainMM;

    public PartitionedMatrixMultiplication(TensorFactory factory, List<? extends MatrixMultiplicationResource> mm) {
        super(factory);
        this.mm = new LinkedList<MatrixMultiplicationResource>();
        for (MatrixMultiplicationResource pmm : mm)
            if (pmm.isAvailable())
                this.mm.add(pmm);
        mainMM = mm.get(0);
    }      

    private int getCellCount(int[] m1Dim, int[] m2Dim) {
        return m1Dim[0]*m1Dim[1]+m1Dim[0]*m2Dim[1]+m2Dim[0]*m2Dim[1];
    }
    
    protected void multiply(final DoubleMatrix m1, final DoubleMatrix m2, final DoubleMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
        if (mainMM.getMaxCellCount(JavaType.DOUBLE) > getCellCount(m1Dim,m2Dim)) {
            AbstractMatrixMultiplication.multiply(mainMM.getMatrixMultiplication(), m1, m2, output, transpose1, transpose2, m1Dim, m2Dim);
        } else {
            //partition
            final CountDownLatch latch = new CountDownLatch(mm.size());
            final BlockingQueue<Integer> currentPoint = new LinkedBlockingDeque<Integer>();
            currentPoint.add(0);
            for (MatrixMultiplicationResource pmm : mm) {
                final MatrixMultiplicationResource thisPmm = pmm;
                final PartitionRule thisRule = pmm.getPartitionRule(JavaType.DOUBLE, m1Dim, m2Dim, transpose1, transpose2);
                final Slice slice;
                final Slice outputSlice;
                if (thisRule.partitionFirstIndex()) {
                    slice = SliceUtil.fullSlice(transpose1 ? m1.size(0) : m1.size(1));
                    outputSlice = SliceUtil.fullSlice(output.size(1));
                } else {
                    slice = SliceUtil.fullSlice(transpose2 ? m2.size(1) : m2.size(0));
                    outputSlice = SliceUtil.fullSlice(output.size(0));
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int start;
                        while (true) { //keep getting tasks - this loop will break when we're done
                            try {
                                start = currentPoint.take();
                            } catch (InterruptedException e) {
                                throw new RuntimeInterruptedException(e);
                            }
                            int partitionLength = thisRule.nextPartitionLength(start);
                            if (partitionLength == -1) {
                                latch.countDown();
                                return; //all done
                            }
                            int nextStart = start + partitionLength;
                            System.out.println("  [" + start + "," + nextStart + ")");
                            currentPoint.add(nextStart); //put next start back asap so we don't hold anybody else up

                            DoubleMatrix mn1;
                            DoubleMatrix mn2;
                            DoubleMatrix mno;
                            Slice rangeSlice = SliceUtil.range(start,nextStart);
                            if (thisRule.partitionFirstIndex()) {
                                mn1 = (DoubleMatrix) m1.getReferenceTensor(transpose1 ?
                                        SliceIndex.getSliceIndex(m1.getIndex(),slice,rangeSlice) :
                                        SliceIndex.getSliceIndex(m1.getIndex(),rangeSlice,slice));
                                mn2 = m2;
                                mno = (DoubleMatrix) output.getReferenceTensor(SliceIndex.getSliceIndex(output.getIndex(),rangeSlice,outputSlice));
                            } else {
                                mn1 = m1;
                                mn2 = (DoubleMatrix) m2.getReferenceTensor(transpose2 ? 
                                        SliceIndex.getSliceIndex(m2.getIndex(),rangeSlice,slice) :
                                        SliceIndex.getSliceIndex(m2.getIndex(),slice,rangeSlice));
                                mno = (DoubleMatrix) output.getReferenceTensor(SliceIndex.getSliceIndex(output.getIndex(),outputSlice,rangeSlice));
                            }
                            AbstractMatrixMultiplication.multiply(thisPmm.getMatrixMultiplication(), mn1, mn2, mno, transpose1, transpose2, mn1.getDimensions(), mn2.getDimensions());
                        }
                    }
                }).start();
            }
            try {
                latch.await(); //wait till everyone is finished
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            }
        }
    }
    
    protected void multiply(final FloatMatrix m1, final FloatMatrix m2, final FloatMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
        if (mainMM.getMaxCellCount(JavaType.FLOAT) > getCellCount(m1Dim,m2Dim)) {
            AbstractMatrixMultiplication.multiply(mainMM.getMatrixMultiplication(),m1,m2,output,transpose1,transpose2,m1Dim,m2Dim);
        } else {
            //partition
            final CountDownLatch latch = new CountDownLatch(mm.size());
            final BlockingQueue<Integer> currentPoint = new LinkedBlockingDeque<Integer>();
            currentPoint.add(0);
            for (MatrixMultiplicationResource pmm : mm) {
                final MatrixMultiplicationResource thisPmm = pmm;
                final PartitionRule thisRule = pmm.getPartitionRule(JavaType.FLOAT, m1Dim, m2Dim, transpose1, transpose2);
                final Slice slice;
                final Slice outputSlice;
                if (thisRule.partitionFirstIndex()) {
                    slice = SliceUtil.fullSlice(transpose1 ? m1.size(0) : m1.size(1));
                    outputSlice = SliceUtil.fullSlice(output.size(1));
                } else {
                    slice = SliceUtil.fullSlice(transpose2 ? m2.size(1) : m2.size(0));
                    outputSlice = SliceUtil.fullSlice(output.size(0));
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int start;
                        while (true) { //keep getting tasks - this loop will break when we're done
                            try {
                                start = currentPoint.take();
                            } catch (InterruptedException e) {
                                throw new RuntimeInterruptedException(e);
                            }
                            int partitionLength = thisRule.nextPartitionLength(start);
                            if (partitionLength == -1) {
                                latch.countDown();
                                return; //all done
                            }
                            int nextStart = start + partitionLength;
                            System.out.println("  [" + start + "," + nextStart + ")");
                            currentPoint.add(nextStart); //put next start back asap so we don't hold anybody else up

                            FloatMatrix mn1;
                            FloatMatrix mn2;
                            FloatMatrix mno;
                            Slice rangeSlice = SliceUtil.range(start,nextStart);
                            if (thisRule.partitionFirstIndex()) {
                                mn1 = (FloatMatrix) m1.getReferenceTensor(transpose1 ?
                                        SliceIndex.getSliceIndex(m1.getIndex(),slice,rangeSlice) :
                                        SliceIndex.getSliceIndex(m1.getIndex(),rangeSlice,slice));
                                mn2 = m2;
                                mno = (FloatMatrix) output.getReferenceTensor(SliceIndex.getSliceIndex(output.getIndex(),rangeSlice,outputSlice));
                            } else {
                                mn1 = m1;
                                mn2 = (FloatMatrix) m2.getReferenceTensor(transpose2 ? 
                                        SliceIndex.getSliceIndex(m2.getIndex(),rangeSlice,slice) :
                                        SliceIndex.getSliceIndex(m2.getIndex(),slice,rangeSlice));
                                mno = (FloatMatrix) output.getReferenceTensor(SliceIndex.getSliceIndex(output.getIndex(),outputSlice,rangeSlice));
                            }
                            AbstractMatrixMultiplication.multiply(thisPmm.getMatrixMultiplication(),mn1, mn2, mno, transpose1, transpose2, mn1.getDimensions(), mn2.getDimensions());
                        }
                    }
                }).start();
            }
            try {
                latch.await(); //wait till everyone is finished
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            }
        }
    }
    
    protected void multiply(final LongMatrix m1, final LongMatrix m2, final LongMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
        if (mainMM.getMaxCellCount(JavaType.LONG) > getCellCount(m1Dim,m2Dim)) {
            AbstractMatrixMultiplication.multiply(mainMM.getMatrixMultiplication(),m1,m2,output,transpose1,transpose2,m1Dim,m2Dim);
        } else {
            //partition
            final CountDownLatch latch = new CountDownLatch(mm.size());
            final BlockingQueue<Integer> currentPoint = new LinkedBlockingDeque<Integer>();
            currentPoint.add(0);
            for (MatrixMultiplicationResource pmm : mm) {
                final MatrixMultiplicationResource thisPmm = pmm;
                final PartitionRule thisRule = pmm.getPartitionRule(JavaType.LONG, m1Dim, m2Dim, transpose1, transpose2);
                final Slice slice;
                final Slice outputSlice;
                if (thisRule.partitionFirstIndex()) {
                    slice = SliceUtil.fullSlice(transpose1 ? m1.size(0) : m1.size(1));
                    outputSlice = SliceUtil.fullSlice(output.size(1));
                } else {
                    slice = SliceUtil.fullSlice(transpose2 ? m2.size(1) : m2.size(0));
                    outputSlice = SliceUtil.fullSlice(output.size(0));
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int start;
                        while (true) { //keep getting tasks - this loop will break when we're done
                            try {
                                start = currentPoint.take();
                            } catch (InterruptedException e) {
                                throw new RuntimeInterruptedException(e);
                            }
                            int partitionLength = thisRule.nextPartitionLength(start);
                            if (partitionLength == -1) {
                                latch.countDown();
                                return; //all done
                            }
                            int nextStart = start + partitionLength;
                            System.out.println("  [" + start + "," + nextStart + ")");
                            currentPoint.add(nextStart); //put next start back asap so we don't hold anybody else up

                            LongMatrix mn1;
                            LongMatrix mn2;
                            LongMatrix mno;
                            Slice rangeSlice = SliceUtil.range(start,nextStart);
                            if (thisRule.partitionFirstIndex()) {
                                mn1 = (LongMatrix) m1.getReferenceTensor(transpose1 ?
                                        SliceIndex.getSliceIndex(m1.getIndex(),slice,rangeSlice) :
                                        SliceIndex.getSliceIndex(m1.getIndex(),rangeSlice,slice));
                                mn2 = m2;
                                mno = (LongMatrix) output.getReferenceTensor(SliceIndex.getSliceIndex(output.getIndex(),rangeSlice,outputSlice));
                            } else {
                                mn1 = m1;
                                mn2 = (LongMatrix) m2.getReferenceTensor(transpose2 ? 
                                        SliceIndex.getSliceIndex(m2.getIndex(),rangeSlice,slice) :
                                        SliceIndex.getSliceIndex(m2.getIndex(),slice,rangeSlice));
                                mno = (LongMatrix) output.getReferenceTensor(SliceIndex.getSliceIndex(output.getIndex(),outputSlice,rangeSlice));
                            }
                            AbstractMatrixMultiplication.multiply(thisPmm.getMatrixMultiplication(),mn1, mn2, mno, transpose1, transpose2, mn1.getDimensions(), mn2.getDimensions());
                        }
                    }
                }).start();
            }
            try {
                latch.await(); //wait till everyone is finished
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            }
        }
    }
    
    protected void multiply(final IntMatrix m1, final IntMatrix m2, final IntMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
        if (mainMM.getMaxCellCount(JavaType.INT) > getCellCount(m1Dim,m2Dim)) {
            AbstractMatrixMultiplication.multiply(mainMM.getMatrixMultiplication(),m1,m2,output,transpose1,transpose2,m1Dim,m2Dim);
        } else {
            //partition
            final CountDownLatch latch = new CountDownLatch(mm.size());
            final BlockingQueue<Integer> currentPoint = new LinkedBlockingDeque<Integer>();
            currentPoint.add(0);
            for (MatrixMultiplicationResource pmm : mm) {
                final MatrixMultiplicationResource thisPmm = pmm;
                final PartitionRule thisRule = pmm.getPartitionRule(JavaType.INT, m1Dim, m2Dim, transpose1, transpose2);
                final Slice slice;
                final Slice outputSlice;
                if (thisRule.partitionFirstIndex()) {
                    slice = SliceUtil.fullSlice(transpose1 ? m1.size(0) : m1.size(1));
                    outputSlice = SliceUtil.fullSlice(output.size(1));
                } else {
                    slice = SliceUtil.fullSlice(transpose2 ? m2.size(1) : m2.size(0));
                    outputSlice = SliceUtil.fullSlice(output.size(0));
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int start;
                        while (true) { //keep getting tasks - this loop will break when we're done
                            try {
                                start = currentPoint.take();
                            } catch (InterruptedException e) {
                                throw new RuntimeInterruptedException(e);
                            }
                            int partitionLength = thisRule.nextPartitionLength(start);
                            if (partitionLength == -1) {
                                latch.countDown();
                                return; //all done
                            }
                            int nextStart = start + partitionLength;
                            System.out.println("  [" + start + "," + nextStart + ")");
                            currentPoint.add(nextStart); //put next start back asap so we don't hold anybody else up

                            IntMatrix mn1;
                            IntMatrix mn2;
                            IntMatrix mno;
                            Slice rangeSlice = SliceUtil.range(start,nextStart);
                            if (thisRule.partitionFirstIndex()) {
                                mn1 = (IntMatrix) m1.getReferenceTensor(transpose1 ?
                                        SliceIndex.getSliceIndex(m1.getIndex(),slice,rangeSlice) :
                                        SliceIndex.getSliceIndex(m1.getIndex(),rangeSlice,slice));
                                mn2 = m2;
                                mno = (IntMatrix) output.getReferenceTensor(SliceIndex.getSliceIndex(output.getIndex(),rangeSlice,outputSlice));
                            } else {
                                mn1 = m1;
                                mn2 = (IntMatrix) m2.getReferenceTensor(transpose2 ? 
                                        SliceIndex.getSliceIndex(m2.getIndex(),rangeSlice,slice) :
                                        SliceIndex.getSliceIndex(m2.getIndex(),slice,rangeSlice));
                                mno = (IntMatrix) output.getReferenceTensor(SliceIndex.getSliceIndex(output.getIndex(),outputSlice,rangeSlice));
                            }
                            AbstractMatrixMultiplication.multiply(thisPmm.getMatrixMultiplication(),mn1, mn2, mno, transpose1, transpose2, mn1.getDimensions(), mn2.getDimensions());
                        }
                    }
                }).start();
            }
            try {
                latch.await(); //wait till everyone is finished
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            }
        }
    }
}
