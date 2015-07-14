package com.pb.sawdust.calculator.tensor;

import com.pb.sawdust.calculator.NumericFunction1;
import com.pb.sawdust.calculator.NumericFunction2;
import com.pb.sawdust.calculator.NumericFunctionN;
import com.pb.sawdust.calculator.NumericValuesProvider;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.decorators.primitive.DoubleTensor;
import com.pb.sawdust.tensor.decorators.primitive.FloatTensor;
import com.pb.sawdust.tensor.decorators.primitive.IntTensor;
import com.pb.sawdust.tensor.decorators.primitive.LongTensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.abacus.Abacus;
import com.pb.sawdust.util.abacus.LockShiftingAbacus;
import com.pb.sawdust.util.concurrent.DnCRecursiveAction;
import com.pb.sawdust.util.concurrent.ForkJoinPoolFactory;
import com.pb.sawdust.util.concurrent.LongCountDownLatch;
import com.pb.sawdust.util.exceptions.RuntimeInterruptedException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

/**
 * The {@code DefaultElementByElementTensorCalculation} provides a default cell-wise tensor calculation (including mutating
 * calculations) implementation.  If the number of individual calculations in a given tensor calculation (counted as the
 * number of cells in the output (result) tensor) is greater than a specified threshold, then the calculations will be
 * performed in parallel, using the default number of threads/parallelism as specified by {@link com.pb.sawdust.util.concurrent.ForkJoinPoolFactory#getForkJoinPool()}.
 *
 * @author crf <br/>
 *         Started Nov 19, 2010 5:16:44 PM
 */
public class DefaultCellWiseTensorCalculation extends AbstractCellWiseTensorCalculation {
    /**
     * The default minimum calculation count for parallel mode to be enabled.
     */
    public static final int DEFAULT_MIN_PARALLEL_LENGTH = 10000;
    
    private final ForkJoinPool pool;
    private final int minParallelLength;
    private final int maxSurplusQueuedTaskCount = 3; //todo: tune this?

    /**
     * Constructor specifying the tensor factory used to create result tensors, as well as the minimum number of calculations
     * required for parallel mode to be enabled. For the purposes of this class, the number of cells in an output (result)
     * tensor is used as the number of calculations when determining whether parallel mode is enabled or not.
     *
     * @param factory
     *        The tensor factory used to construct result tensors.
     *
     * @param minParallelLength
     *        The minimum number of calculations required for parallel mode to be enabled.
     */
    public DefaultCellWiseTensorCalculation(TensorFactory factory, int minParallelLength) {
        super(factory);
        pool = ForkJoinPoolFactory.getForkJoinPool();
        this.minParallelLength = minParallelLength;
    }

    /**
     * Constructor specifying the tensor factory used to create result tensors.  The default value for the minimumn number
     * of calculations for parallel mode ({@link #DEFAULT_MIN_PARALLEL_LENGTH}) will be used. For the purposes of this class,
     * the number of cells in an output (result) tensor is used as the number of calculations when determining whether parallel
     * mode is enabled or not.
     *
     * @param factory
     *        The tensor factory used to construct result tensors.
     */
    public DefaultCellWiseTensorCalculation(TensorFactory factory) {
        this(factory,DEFAULT_MIN_PARALLEL_LENGTH);
    }
    
    protected DoubleTensor calculate(DoubleTensor t, DoubleTensor result, NumericFunction1 function) {
        //special for scalar
        if (t.size() == 0)
            result.setCell(function.apply(t.getCell()));
        else
            pool.invoke(new DoubleFunction1CalculateAction(t,result,function));
        return result;
    } 
    
    protected FloatTensor calculate(FloatTensor t, FloatTensor result, NumericFunction1 function) {
        //special for scalar
        if (t.size() == 0)
            result.setCell(function.apply(t.getCell()));
        else
            pool.invoke(new FloatFunction1CalculateAction(t,result,function));
        return result;
    }                   
    
    protected LongTensor calculate(LongTensor t, LongTensor result, NumericFunction1 function) {
        //special for scalar
        if (t.size() == 0)
            result.setCell(function.apply(t.getCell()));
        else
            pool.invoke(new LongFunction1CalculateAction(t,result,function));
        return result;
    }    
    
    protected IntTensor calculate(IntTensor t, IntTensor result, NumericFunction1 function) {
        //special for scalar
        if (t.size() == 0)
            result.setCell(function.apply(t.getCell()));
        else
            pool.invoke(new IntFunction1CalculateAction(t,result,function));
        return result;
    }

//    @Override
//    protected LongTensor calculate(LongTensor t1, LongTensor t2, LongTensor result, NumericFunctions.NumericFunction2 function) {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    protected DoubleTensor calculate(DoubleTensor t1, DoubleTensor t2, DoubleTensor result, NumericFunctions.NumericFunction2 function) {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    protected LongTensor calculate(LongTensor t1, LongTensor t2, LongTensor result, NumericFunctions.NumericFunction2 function, int[] matchingDimensions, int[] freeDimensions) {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    protected DoubleTensor calculate(DoubleTensor t1, DoubleTensor t2, DoubleTensor result, NumericFunctions.NumericFunction2 function, int[] matchingDimensions, int[] freeDimensions) {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    protected LongTensor calculate(NumericFunctions.NumericFunctionN function, LongTensor[] parameters, LongTensor result) {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//    
//    @Override
//    protected DoubleTensor calculate(NumericFunctions.NumericFunctionN function, DoubleTensor[] parameters, DoubleTensor result) {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }

    
    //function1

    private class DoubleFunction1CalculateAction extends DnCRecursiveAction {
        private static final long serialVersionUID = -8723727574676741797L;

        private final DoubleTensor t;
        private final DoubleTensor result;
        private final NumericFunction1 function;
        private final Abacus a;

        public DoubleFunction1CalculateAction(DoubleTensor t, DoubleTensor result, NumericFunction1 function, Abacus a, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t =t ;
            this.result = result;
            this.function = function;
            this.a = a;
        }

        private DoubleFunction1CalculateAction(DoubleTensor t, DoubleTensor result, NumericFunction1 function) {
            super(0, TensorUtil.getElementCount(t));
            this.t = t;
            this.result = result;
            this.function = function;
            a = new Abacus(t.getDimensions());
        }

        @Override
         protected void computeAction(long start, long length) {
            Abacus ab = a.freshClone();
            ab.setAbacusAtPosition(start);
            long pos = 0;
            while(pos < length) {
                int[] index = ab.next();
                result.setCell(function.apply(t.getCell(index)),index);
                pos++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new DoubleFunction1CalculateAction(t,result,function,a,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }

    }

    private class FloatFunction1CalculateAction extends DnCRecursiveAction {
        static final long serialVersionUID = 1822816105617105082L;

        private final FloatTensor t;
        private final FloatTensor result;
        private final NumericFunction1 function;
        private final Abacus a;

        public FloatFunction1CalculateAction(FloatTensor t, FloatTensor result, NumericFunction1 function, Abacus a, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t =t ;
            this.result = result;
            this.function = function;
            this.a = a;
        }

        public FloatFunction1CalculateAction(FloatTensor t, FloatTensor result, NumericFunction1 function) {
            super(0,TensorUtil.getElementCount(t));
            this.t = t;
            this.result = result;
            this.function = function;
            a = new Abacus(t.getDimensions());
        }

        @Override
         protected void computeAction(long start, long length) {
            Abacus ab = a.freshClone();
            ab.setAbacusAtPosition(start);
            long pos = 0;
            while(pos < length) {
                int[] index = ab.next();
                result.setCell(function.apply(t.getCell(index)),index);
                pos++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new FloatFunction1CalculateAction(t,result,function,a,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }

    } 

    private class LongFunction1CalculateAction extends DnCRecursiveAction {
        private static final long serialVersionUID = 3937734191725338576L;

        private final LongTensor t;
        private final LongTensor result;
        private final NumericFunction1 function;
        private final Abacus a;

        public LongFunction1CalculateAction(LongTensor t, LongTensor result, NumericFunction1 function, Abacus a, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t =t ;
            this.result = result;
            this.function = function;
            this.a = a;
        }

        public LongFunction1CalculateAction(LongTensor t, LongTensor result, NumericFunction1 function) {
            super(0,TensorUtil.getElementCount(t));
            this.t = t;
            this.result = result;
            this.function = function;
            a = new Abacus(t.getDimensions());
        }

        @Override
         protected void computeAction(long start, long length) {
            Abacus ab = a.freshClone();
            ab.setAbacusAtPosition(start);
            long pos = 0;
            while(pos < length) {
                int[] index = ab.next();
                result.setCell(function.apply(t.getCell(index)),index);
                pos++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new LongFunction1CalculateAction(t,result,function,a,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }

    }   

    private class IntFunction1CalculateAction extends DnCRecursiveAction {
        private static final long serialVersionUID = -6844277540922060239L;

        private final IntTensor t;
        private final IntTensor result;
        private final NumericFunction1 function;
        private final Abacus a;

        public IntFunction1CalculateAction(IntTensor t, IntTensor result, NumericFunction1 function, Abacus a, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t =t ;
            this.result = result;
            this.function = function;
            this.a = a;
        }

        public IntFunction1CalculateAction(IntTensor t, IntTensor result, NumericFunction1 function) {
            super(0,TensorUtil.getElementCount(t));
            this.t = t;
            this.result = result;
            this.function = function;
            a = new Abacus(t.getDimensions());
        }

        @Override
         protected void computeAction(long start, long length) {
            Abacus ab = a.freshClone();
            ab.setAbacusAtPosition(start);
            long pos = 0;
            while(pos < length) {
                int[] index = ab.next();
                result.setCell(function.apply(t.getCell(index)),index);
                pos++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new IntFunction1CalculateAction(t,result,function,a,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }

    }

    //function 2   
    @Override
    protected DoubleTensor calculate(DoubleTensor t1, DoubleTensor t2, DoubleTensor result, NumericFunction2 function, int[]  fixedDimensions, int[] freeDimensions) {
        //fixed dimensions are the dimensions, in order, of the bigger tensor which match those of the smaller one
        //  so fixedDimension[i] is the dimension in t1 corresponding to t2's dimension i
        //free dimensions are the dims from the bigger tensor not in the fixed dim set
        //  assume they've already been checked
        //  assume t1 is the bigger tensor - can "reverse" function to do this if necessary

        //special for scalar
        if (t1.size() == 0) {
            result.setCell(function.apply(t1.getCell(),t2.getCell()));
            return result;
        }

        int[] bigDim = t1.getDimensions();
        long cycleCount = 1;
        long oneCycle = 1;
        for (int i : fixedDimensions)
            oneCycle *= bigDim[i];
        for (int i : freeDimensions)
            cycleCount *= bigDim[i];
        Abacus a = new LockShiftingAbacus(bigDim,freeDimensions);
        //Semaphore block = new Semaphore(maxParallelCycleCalcs);  //only allows a certain number of elements to be calculated concurrently
        LongCountDownLatch latch = new LongCountDownLatch(cycleCount); //counter to ensure we've finished calculating before returning
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool();

        try {
            List<ForkJoinTask<?>> tasks = new LinkedList<ForkJoinTask<?>>();
            for (long i = 0; i < cycleCount; i++)
                tasks.add(pool.submit(new DoubleFunction2CalculateAction(t1,t2,result,function,a,i,fixedDimensions,oneCycle,latch)));
            latch.await();
            for (ForkJoinTask<?> task : tasks)
                if (task.getException() != null)
                    throw new RuntimeException(task.getException());
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        }
        return result;
    }
       
    @Override
    protected DoubleTensor calculate(DoubleTensor t1, DoubleTensor t2, DoubleTensor result, NumericFunction2 function) {
        int[] dim = t1.getDimensions();
//        if (!Arrays.equals(dim,t2.getDimensions()))
//            throw new IllegalArgumentException("Element by element tensors must be identically sized: " + Arrays.toString(dim) + " vs. " + Arrays.toString(t2.getDimensions()));

        //special for scalar
        if (t1.size() == 0) {
            result.setCell(function.apply(t1.getCell(),t2.getCell()));
            return result;
        }

        Abacus a = new Abacus(dim);
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool();
        pool.invoke(new DoubleFunction2CalculateMatchAction(t1,t2,result,function,a));
        return result;
     }


    private class DoubleFunction2CalculateAction extends DnCRecursiveAction {
        static final long serialVersionUID = -4110974711616067563L;

        private final Abacus a;
        private final DoubleTensor t1;
        private final DoubleTensor t2;
        private final DoubleTensor ot;
        private final NumericFunction2 function;
        private final long cycle;
        private final int[] fixedDimensions;
        private final LongCountDownLatch latch;

        private DoubleFunction2CalculateAction(DoubleTensor t1, DoubleTensor t2, DoubleTensor ot, NumericFunction2 function, Abacus a, long cycle, int[] fixedDimensions, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
            this.cycle = cycle;
            this.fixedDimensions = fixedDimensions;
            latch = null;
        }

        private DoubleFunction2CalculateAction(DoubleTensor t1, DoubleTensor t2, DoubleTensor ot, NumericFunction2 function, Abacus a, long cyclePoint, int[] fixedDimensions, long length, LongCountDownLatch latch) {
            super(0,length);
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
            cycle = length*cyclePoint;
            this.fixedDimensions = fixedDimensions;
            this.latch = latch;
        }

        @Override
        protected void computeAction(long start, long length) {
            Abacus subA = a.freshClone();
            subA.setAbacusAtPosition(cycle+start);
            int[] subPosition = new int[fixedDimensions.length];
            int len = subPosition.length;
            long counter = 0;
            while (counter < length) {
                int[] position = subA.next();
                for (int d = 0; d < len; d++)
                    subPosition[d] = position[fixedDimensions[d]];
                ot.setCell(function.apply(t1.getCell(position), t2.getCell(subPosition)), position);
                counter++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new DoubleFunction2CalculateAction(t1,t2,ot,function,a,cycle,fixedDimensions,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }

        protected void compute() {
            super.compute();
            if (latch != null)
                latch.countDown();
        }
    }

    private class DoubleFunction2CalculateMatchAction extends DnCRecursiveAction {
        private static final long serialVersionUID = 9029363814764116664L;

        private final Abacus a;
        private final DoubleTensor t1;
        private final DoubleTensor t2;
        private final DoubleTensor ot;
        private final NumericFunction2 function;

        private DoubleFunction2CalculateMatchAction(DoubleTensor t1, DoubleTensor t2, DoubleTensor ot, NumericFunction2 function, Abacus a, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

        private DoubleFunction2CalculateMatchAction(DoubleTensor t1, DoubleTensor t2, DoubleTensor ot, NumericFunction2 function, Abacus a) {
            super(0,a.getStateCount());
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

        @Override
        protected void computeAction(long start, long length) {
            Abacus ab = a.freshClone();
            ab.setAbacusAtPosition(start);
            long counter = 0;
            while (counter < length) {
                 int[] ind = ab.next();
                 ot.setCell(function.apply(t1.getCell(ind),t2.getCell(ind)),ind);
                counter++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new DoubleFunction2CalculateMatchAction(t1,t2,ot,function,a,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }
    }
       
    @Override
    protected FloatTensor calculate(FloatTensor t1, FloatTensor t2, FloatTensor result, NumericFunction2 function, int[]  fixedDimensions, int[] freeDimensions) {
        //fixed dimensions are the dimensions, in order, of the bigger tensor which match those of the smaller one
        //  so fixedDimension[i] is the dimension in t1 corresponding to t2's dimension i
        //free dimensions are the dims from the bigger tensor not in the fixed dim set
        //  assume they've already been checked
        //  assume t1 is the bigger tensor - can "reverse" function to do this if necessary

        //special for scalar
        if (t1.size() == 0) {
            result.setCell(function.apply(t1.getCell(),t2.getCell()));
            return result;
        }

        int[] bigDim = t1.getDimensions();
        long cycleCount = 1;
        long oneCycle = 1;
        for (int i : fixedDimensions)
            oneCycle *= bigDim[i];
        for (int i : freeDimensions)
            cycleCount *= bigDim[i];
        Abacus a = new LockShiftingAbacus(bigDim,freeDimensions);

        LongCountDownLatch latch = new LongCountDownLatch(cycleCount); //counter to ensure we've finished calculating before returning
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool();

        try {
            List<ForkJoinTask<?>> tasks = new LinkedList<ForkJoinTask<?>>();
            for (long i = 0; i < cycleCount; i++)
                tasks.add(pool.submit(new FloatFunction2CalculateAction(t1,t2,result,function,a,i,fixedDimensions,oneCycle,latch)));
            latch.await();
            for (ForkJoinTask<?> task : tasks)
                if (task.getException() != null)
                    throw new RuntimeException(task.getException());
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        }
        return result;
    }
       
    @Override
    protected FloatTensor calculate(FloatTensor t1, FloatTensor t2, FloatTensor result, NumericFunction2 function) {
        int[] dim = t1.getDimensions();
//        if (!Arrays.equals(dim,t2.getDimensions()))
//            throw new IllegalArgumentException("Element by element tensors must be identically sized: " + Arrays.toString(dim) + " vs. " + Arrays.toString(t2.getDimensions()));

        //special for scalar
        if (t1.size() == 0) {
            result.setCell(function.apply(t1.getCell(),t2.getCell()));
            return result;
        }

        Abacus a = new Abacus(dim);
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool();
        pool.invoke(new FloatFunction2CalculateMatchAction(t1,t2,result,function,a));
        return result;
     }


    private class FloatFunction2CalculateAction extends DnCRecursiveAction {
        static final long serialVersionUID = 7676364092525960383L;

        private final Abacus a;
        private final FloatTensor t1;
        private final FloatTensor t2;
        private final FloatTensor ot;
        private final NumericFunction2 function;
        private final long cycle;
        private final int[] fixedDimensions;
        private final LongCountDownLatch latch;

        private FloatFunction2CalculateAction(FloatTensor t1, FloatTensor t2, FloatTensor ot, NumericFunction2 function, Abacus a, long cycle, int[] fixedDimensions, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
            this.cycle = cycle;
            this.fixedDimensions = fixedDimensions;
            latch = null;
        }

        private FloatFunction2CalculateAction(FloatTensor t1, FloatTensor t2, FloatTensor ot, NumericFunction2 function, Abacus a, long cyclePoint, int[] fixedDimensions, long length, LongCountDownLatch latch) {
            super(0,length);
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
            cycle = length*cyclePoint;
            this.fixedDimensions = fixedDimensions;
            this.latch = latch;
        }

        @Override
        protected void computeAction(long start, long length) {
            Abacus subA = a.freshClone();
            subA.setAbacusAtPosition(cycle+start);
            int[] subPosition = new int[fixedDimensions.length];
            int len = subPosition.length;
            long counter = 0;
            while (counter < length) {
                int[] position = subA.next();
                for (int d = 0; d < len; d++)
                    subPosition[d] = position[fixedDimensions[d]];
                ot.setCell(function.apply(t1.getCell(position),t2.getCell(subPosition)),position);
                counter++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new FloatFunction2CalculateAction(t1,t2,ot,function,a,cycle,fixedDimensions,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }

        protected void compute() {
            super.compute();
            if (latch != null) 
                latch.countDown();
        }
    }

    private class FloatFunction2CalculateMatchAction extends DnCRecursiveAction {
        static final long serialVersionUID = -7145975486353435515L;

        private final Abacus a;
        private final FloatTensor t1;
        private final FloatTensor t2;
        private final FloatTensor ot;
        private final NumericFunction2 function;

        private FloatFunction2CalculateMatchAction(FloatTensor t1, FloatTensor t2, FloatTensor ot, NumericFunction2 function, Abacus a, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

        private FloatFunction2CalculateMatchAction(FloatTensor t1, FloatTensor t2, FloatTensor ot, NumericFunction2 function, Abacus a) {
            super(0,a.getStateCount());
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

        @Override
        protected void computeAction(long start, long length) {
            Abacus subA = a.freshClone();
            subA.setAbacusAtPosition(start);
            long counter = 0;
            while (counter < length) {
                 int[] ind = subA.next();
                 ot.setCell(function.apply(t1.getCell(ind),t2.getCell(ind)),ind);
                counter++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new FloatFunction2CalculateMatchAction(t1,t2,ot,function,a,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }
    }
       
    @Override
    protected LongTensor calculate(LongTensor t1, LongTensor t2, LongTensor result, NumericFunction2 function, int[]  fixedDimensions, int[] freeDimensions) {
        //fixed dimensions are the dimensions, in order, of the bigger tensor which match those of the smaller one
        //  so fixedDimension[i] is the dimension in t1 corresponding to t2's dimension i
        //free dimensions are the dims from the bigger tensor not in the fixed dim set
        //  assume they've already been checked
        //  assume t1 is the bigger tensor - can "reverse" function to do this if necessary

        //special for scalar
        if (t1.size() == 0) {
            result.setCell(function.apply(t1.getCell(),t2.getCell()));
            return result;
        }

        int[] bigDim = t1.getDimensions();
        long cycleCount = 1;
        long oneCycle = 1;
        for (int i : fixedDimensions)
            oneCycle *= bigDim[i];
        for (int i : freeDimensions)
            cycleCount *= bigDim[i];
        Abacus a = new LockShiftingAbacus(bigDim,freeDimensions);

        LongCountDownLatch latch = new LongCountDownLatch(cycleCount); //counter to ensure we've finished calculating before returning
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool();

        try {
            List<ForkJoinTask<?>> tasks = new LinkedList<ForkJoinTask<?>>();
            for (long i = 0; i < cycleCount; i++)
                tasks.add(pool.submit(new LongFunction2CalculateAction(t1,t2,result,function,a,i,fixedDimensions,oneCycle,latch)));
            latch.await();
            for (ForkJoinTask<?> task : tasks)
                if (task.getException() != null)
                    throw new RuntimeException(task.getException());
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        }
        return result;
    }
       
    @Override
    protected LongTensor calculate(LongTensor t1, LongTensor t2, LongTensor result, NumericFunction2 function) {
        int[] dim = t1.getDimensions();
//        if (!Arrays.equals(dim,t2.getDimensions()))
//            throw new IllegalArgumentException("Element by element tensors must be identically sized: " + Arrays.toString(dim) + " vs. " + Arrays.toString(t2.getDimensions()));

        //special for scalar
        if (t1.size() == 0) {
            result.setCell(function.apply(t1.getCell(),t2.getCell()));
            return result;
        }

        Abacus a = new Abacus(dim);
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool();
        pool.invoke(new LongFunction2CalculateMatchAction(t1,t2,result,function,a));
        return result;
    }


    private class LongFunction2CalculateAction extends DnCRecursiveAction {
        private static final long serialVersionUID = 8123428349053748041L;

        private final Abacus a;
        private final LongTensor t1;
        private final LongTensor t2;
        private final LongTensor ot;
        private final NumericFunction2 function;
        private final long cycle;
        private final int[] fixedDimensions;
        private final LongCountDownLatch latch;

        private LongFunction2CalculateAction(LongTensor t1, LongTensor t2, LongTensor ot, NumericFunction2 function, Abacus a, long cycle, int[] fixedDimensions, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
            this.cycle = cycle;
            this.fixedDimensions = fixedDimensions;
            latch = null;
        }

        private LongFunction2CalculateAction(LongTensor t1, LongTensor t2, LongTensor ot, NumericFunction2 function, Abacus a, long cyclePoint, int[] fixedDimensions, long length, LongCountDownLatch latch) {
            super(0,length);
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
            cycle = length*cyclePoint;
            this.fixedDimensions = fixedDimensions;
            this.latch = latch;
        }

        @Override
        protected void computeAction(long start, long length) {
            Abacus subA = a.freshClone();
            subA.setAbacusAtPosition(cycle+start);
            int[] subPosition = new int[fixedDimensions.length];
            int len = subPosition.length;
            long counter = 0;
            while (counter < length) {
                int[] position = subA.next();
                for (int d = 0; d < len; d++)
                    subPosition[d] = position[fixedDimensions[d]];
                ot.setCell(function.apply(t1.getCell(position),t2.getCell(subPosition)),position);
                counter++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new LongFunction2CalculateAction(t1,t2,ot,function,a,cycle,fixedDimensions,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }

        protected void compute() {
            super.compute();
            if (latch != null)
                latch.countDown();
        }
    }

    private class LongFunction2CalculateMatchAction extends DnCRecursiveAction {
        private static final long serialVersionUID = 1490655017517100873L;

        private final Abacus a;
        private final LongTensor t1;
        private final LongTensor t2;
        private final LongTensor ot;
        private final NumericFunction2 function;

        private LongFunction2CalculateMatchAction(LongTensor t1, LongTensor t2, LongTensor ot, NumericFunction2 function, Abacus a, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

        private LongFunction2CalculateMatchAction(LongTensor t1, LongTensor t2, LongTensor ot, NumericFunction2 function, Abacus a) {
            super(0,a.getStateCount());
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

        @Override
        protected void computeAction(long start, long length) {
            Abacus subA = a.freshClone();
            subA.setAbacusAtPosition(start);
            long counter = 0;
            while (counter < length) {
                 int[] ind = subA.next();
                 ot.setCell(function.apply(t1.getCell(ind),t2.getCell(ind)),ind);
                counter++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new LongFunction2CalculateMatchAction(t1,t2,ot,function,a,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }
    }
     
    @Override
    protected IntTensor calculate(IntTensor t1, IntTensor t2, IntTensor result, NumericFunction2 function, int[]  fixedDimensions, int[] freeDimensions) {
        //fixed dimensions are the dimensions, in order, of the bigger tensor which match those of the smaller one
        //  so fixedDimension[i] is the dimension in t1 corresponding to t2's dimension i
        //free dimensions are the dims from the bigger tensor not in the fixed dim set
        //  assume they've already been checked
        //  assume t1 is the bigger tensor - can "reverse" function to do this if necessary

        //special for scalar
        if (t1.size() == 0) {
            result.setCell(function.apply(t1.getCell(),t2.getCell()));
            return result;
        }

        int[] bigDim = t1.getDimensions();
        long cycleCount = 1;
        long oneCycle = 1;
        for (int i : fixedDimensions)
            oneCycle *= bigDim[i];
        for (int i : freeDimensions)
            cycleCount *= bigDim[i];
        Abacus a = new LockShiftingAbacus(bigDim,freeDimensions);

        LongCountDownLatch latch = new LongCountDownLatch(cycleCount); //counter to ensure we've finished calculating before returning
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool();

        try {
            List<ForkJoinTask<?>> tasks = new LinkedList<ForkJoinTask<?>>();
            for (long i = 0; i < cycleCount; i++)
                tasks.add(pool.submit(new IntFunction2CalculateAction(t1,t2,result,function,a,i,fixedDimensions,oneCycle,latch)));
            latch.await();
            for (ForkJoinTask<?> task : tasks)
                if (task.getException() != null)
                    throw new RuntimeException(task.getException());
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        }
        return result;
    }
       
    @Override
    protected IntTensor calculate(IntTensor t1, IntTensor t2, IntTensor result, NumericFunction2 function) {
        int[] dim = t1.getDimensions();
//        if (!Arrays.equals(dim,t2.getDimensions()))
//            throw new IllegalArgumentException("Element by element tensors must be identically sized: " + Arrays.toString(dim) + " vs. " + Arrays.toString(t2.getDimensions()));

        //special for scalar
        if (t1.size() == 0) {
            result.setCell(function.apply(t1.getCell(),t2.getCell()));
            return result;
        }

        Abacus a = new Abacus(dim);
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool();
        pool.invoke(new IntFunction2CalculateMatchAction(t1,t2,result,function,a));
        return result;
    }


    private class IntFunction2CalculateAction extends DnCRecursiveAction {
        private static final long serialVersionUID = -520011983779887890L;

        private final Abacus a;
        private final IntTensor t1;
        private final IntTensor t2;
        private final IntTensor ot;
        private final NumericFunction2 function;
        private final long cycle;
        private final int[] fixedDimensions;
        private final LongCountDownLatch latch;

        private IntFunction2CalculateAction(IntTensor t1, IntTensor t2, IntTensor ot, NumericFunction2 function, Abacus a, long cycle, int[] fixedDimensions, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
            this.cycle = cycle;
            this.fixedDimensions = fixedDimensions;
            latch = null;
        }

        private IntFunction2CalculateAction(IntTensor t1, IntTensor t2, IntTensor ot, NumericFunction2 function, Abacus a, long cyclePoint, int[] fixedDimensions, long length, LongCountDownLatch latch) {
            super(0,length);
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
            cycle = length*cyclePoint;
            this.fixedDimensions = fixedDimensions;
            this.latch = latch;
        }

        @Override
        protected void computeAction(long start, long length) {
            Abacus subA = a.freshClone();
            subA.setAbacusAtPosition(cycle+start);
            int[] subPosition = new int[fixedDimensions.length];
            int len = subPosition.length;
            long counter = 0;
            while (counter < length) {
                int[] position = subA.next();
                for (int d = 0; d < len; d++)
                    subPosition[d] = position[fixedDimensions[d]];
                ot.setCell(function.apply(t1.getCell(position),t2.getCell(subPosition)),position);
                counter++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new IntFunction2CalculateAction(t1,t2,ot,function,a,cycle,fixedDimensions,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }

        protected void compute() {
            super.compute();
            if (latch != null)
                latch.countDown();
        }
    }

    private class IntFunction2CalculateMatchAction extends DnCRecursiveAction {
        private static final long serialVersionUID = 3853697055475621705L;

        private final Abacus a;
        private final IntTensor t1;
        private final IntTensor t2;
        private final IntTensor ot;
        private final NumericFunction2 function;

        private IntFunction2CalculateMatchAction(IntTensor t1, IntTensor t2, IntTensor ot, NumericFunction2 function, Abacus a, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

        private IntFunction2CalculateMatchAction(IntTensor t1, IntTensor t2, IntTensor ot, NumericFunction2 function, Abacus a) {
            super(0,a.getStateCount());
            this.t1 = t1;
            this.t2 = t2;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

        @Override
        protected void computeAction(long start, long length) {
            Abacus subA = a.freshClone();
            subA.setAbacusAtPosition(start);
            long counter = 0;
            while (counter < length) {
                 int[] ind = subA.next();
                 ot.setCell(function.apply(t1.getCell(ind),t2.getCell(ind)),ind);
                counter++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new IntFunction2CalculateMatchAction(t1,t2,ot,function,a,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }
    }
    
    protected DoubleTensor calculate(NumericFunctionN function, DoubleTensor[] parameters, DoubleTensor result) {
        int[] dim = result.getDimensions();
        if (dim.length == 0) {
            //special for scalar
            double[] values = new double[parameters.length];
            for (int i = 0; i < values.length; i++)
                values[i] = parameters[i].getCell();
            result.setCell(function.applyDouble(values));
            return result;
        }
        Abacus ab = new Abacus(dim);
        DoubleFunctionNCalculateMatchAction action = new DoubleFunctionNCalculateMatchAction(parameters,result,function,ab);
        ForkJoinPoolFactory.getForkJoinPool().invoke(action);
        return result;
    }
    
    private class DoubleFunctionNCalculateMatchAction extends DnCRecursiveAction {
        static final long serialVersionUID = 8964441411499371206L;

        private final Abacus a;
        private final DoubleTensor[] t;
        private final DoubleTensor ot;
        private final NumericFunctionN function;

        private DoubleFunctionNCalculateMatchAction(DoubleTensor[] t, DoubleTensor ot, NumericFunctionN function, Abacus a, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t = t;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

        private DoubleFunctionNCalculateMatchAction(DoubleTensor[] t, DoubleTensor ot, NumericFunctionN function, Abacus a) {
            super(0,a.getStateCount());
            this.t = t;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

//        @Override
//        protected void computeAction(long start, long length) {
//            int len = t.length;
//            double[] vals = new double[len];
//            Abacus subA = a.freshClone();
//            subA.setAbacusAtPosition(start);
//            long counter = 0;
//            while (counter < length) {
//                int[] ind = subA.next();
//                for (int i = 0; i < len; i++)
//                    vals[i] = t[i].getCell(ind);
//                ot.setCell(function.applyDouble(vals),ind);
//                counter++;
//            }
//        }

        @Override
        protected void computeAction(long start, long length) {
            DoubleTensorMatchValuesProvider p = new DoubleTensorMatchValuesProvider(t);
            Abacus subA = a.freshClone();
            subA.setAbacusAtPosition(start);
            long counter = 0;
            while (counter < length) {
                int[] ind = subA.next();
                p.ind = ind;
                ot.setCell(function.applyDouble(p),ind);
                counter++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new DoubleFunctionNCalculateMatchAction(t,ot,function,a,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }
    }

    private class DoubleTensorMatchValuesProvider extends NumericValuesProvider.DoubleValuesProvider {
        private final DoubleTensor[] tensors;
        int[] ind;

        DoubleTensorMatchValuesProvider(DoubleTensor[] tensors) {
            this.tensors = tensors;
        }

        @Override
        public int getLength() {
            return tensors.length;
        }

        @Override
        public double getValue(int i) {
            return tensors[i].getCell(ind);
        }
    }
        
    @Override
    protected FloatTensor calculate(NumericFunctionN function, FloatTensor[] parameters, FloatTensor result) {
        int[] dim =result.getDimensions();
        if (dim.length == 0) {
            //special for scalar
            float[] values = new float[parameters.length];
            for (int i = 0; i < values.length; i++)
                values[i] = parameters[i].getCell();
            result.setCell(function.applyFloat(values));
            return result;
        }
        Abacus ab = new Abacus(dim);
        FloatFunctionNCalculateMatchAction action = new FloatFunctionNCalculateMatchAction(parameters,result,function,ab);
        ForkJoinPoolFactory.getForkJoinPool().invoke(action);
        return result;
    }
    
    private class FloatFunctionNCalculateMatchAction extends DnCRecursiveAction {
        static final long serialVersionUID = 4613545211571244409L;

        private final Abacus a;
        private final FloatTensor[] t;
        private final FloatTensor ot;
        private final NumericFunctionN function;

        private FloatFunctionNCalculateMatchAction(FloatTensor[] t, FloatTensor ot, NumericFunctionN function, Abacus a, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t = t;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

        private FloatFunctionNCalculateMatchAction(FloatTensor[] t, FloatTensor ot, NumericFunctionN function, Abacus a) {
            super(0,a.getStateCount());
            this.t = t;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

        @Override
        protected void computeAction(long start, long length) {
            int len = t.length;
            float[] vals = new float[len];
            Abacus subA = a.freshClone();
            subA.setAbacusAtPosition(start);
            long counter = 0;
            while (counter < length) {
                int[] ind = subA.next();
                for (int i = 0; i < len; i++)
                    vals[i] = t[i].getCell(ind);
                ot.setCell(function.applyFloat(vals),ind);
                counter++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new FloatFunctionNCalculateMatchAction(t,ot,function,a,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }
    }
      
    @Override
    protected LongTensor calculate(NumericFunctionN function, LongTensor[] parameters, LongTensor result) {
        int[] dim =result.getDimensions();
        if (dim.length == 0) {
            //special for scalar
            long[] values = new long[parameters.length];
            for (int i = 0; i < values.length; i++)
                values[i] = parameters[i].getCell();
            result.setCell(function.applyLong(values));
            return result;
        }
        Abacus ab = new Abacus(dim);
        LongFunctionNCalculateMatchAction action = new LongFunctionNCalculateMatchAction(parameters,result,function,ab);
        ForkJoinPoolFactory.getForkJoinPool().invoke(action);
        return result;
    }
    
    private class LongFunctionNCalculateMatchAction extends DnCRecursiveAction {
        private static final long serialVersionUID = 664691882913091574L;

        private final Abacus a;
        private final LongTensor[] t;
        private final LongTensor ot;
        private final NumericFunctionN function;

        private LongFunctionNCalculateMatchAction(LongTensor[] t, LongTensor ot, NumericFunctionN function, Abacus a, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t = t;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

        private LongFunctionNCalculateMatchAction(LongTensor[] t, LongTensor ot, NumericFunctionN function, Abacus a) {
            super(0,a.getStateCount());
            this.t = t;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

        @Override
        protected void computeAction(long start, long length) {
            int len = t.length;
            long[] vals = new long[len];
            Abacus subA = a.freshClone();
            subA.setAbacusAtPosition(start);
            long counter = 0;
            while (counter < length) {
                int[] ind = subA.next();
                for (int i = 0; i < len; i++)
                    vals[i] = t[i].getCell(ind);
                ot.setCell(function.applyLong(vals),ind);
                counter++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new LongFunctionNCalculateMatchAction(t,ot,function,a,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }
    }
     
    @Override
    protected IntTensor calculate(NumericFunctionN function, IntTensor[] parameters, IntTensor result) {
        int[] dim =result.getDimensions();
        if (dim.length == 0) {
            //special for scalar
            int[] values = new int[parameters.length];
            for (int i = 0; i < values.length; i++)
                values[i] = parameters[i].getCell();
            result.setCell(function.applyInt(values));
            return result;
        }
        Abacus ab = new Abacus(dim);
        IntFunctionNCalculateMatchAction action = new IntFunctionNCalculateMatchAction(parameters,result,function,ab);
        ForkJoinPoolFactory.getForkJoinPool().invoke(action);
        return result;
    }
    
    private class IntFunctionNCalculateMatchAction extends DnCRecursiveAction {
        private static final long serialVersionUID = -4893179184935550461L;

        private final Abacus a;
        private final IntTensor[] t;
        private final IntTensor ot;
        private final NumericFunctionN function;

        private IntFunctionNCalculateMatchAction(IntTensor[] t, IntTensor ot, NumericFunctionN function, Abacus a, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.t = t;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

        private IntFunctionNCalculateMatchAction(IntTensor[] t, IntTensor ot, NumericFunctionN function, Abacus a) {
            super(0,a.getStateCount());
            this.t = t;
            this.ot = ot;
            this.a = a;
            this.function = function;
        }

        @Override
        protected void computeAction(long start, long length) {
            int len = t.length;
            int[] vals = new int[len];
            Abacus subA = a.freshClone();
            subA.setAbacusAtPosition(start);
            long counter = 0;
            while (counter < length) {
                int[] ind = subA.next();
                for (int i = 0; i < len; i++)
                    vals[i] = t[i].getCell(ind);
                ot.setCell(function.applyInt(vals),ind);
                counter++;
            }
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new IntFunctionNCalculateMatchAction(t,ot,function,a,start,length,next);
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > minParallelLength && getSurplusQueuedTaskCount() <= maxSurplusQueuedTaskCount;
        }
    }
    
}
