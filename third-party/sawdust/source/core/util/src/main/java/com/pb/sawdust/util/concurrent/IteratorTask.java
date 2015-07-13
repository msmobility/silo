package com.pb.sawdust.util.concurrent;

import com.pb.sawdust.calculator.Function1;
import com.pb.sawdust.util.ThreadTimer;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.collections.IndexedElement;
import com.pb.sawdust.util.collections.OrderEnforcingQueue;
import com.pb.sawdust.util.exceptions.RuntimeInterruptedException;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The {@code IteratorTask} class provides a {@code ForkJoinTask} which parallelizes actions on elements held in an iterator.
 * That is, given an iterator of objects of type {@code A}, and a function which takes an {@code A} to a {@code B}, this
 * class can be used to parallelize the transfer of the {@code Iterator&lt;A&gt;} to {@code Iterator&lt;B&gt;} (via the
 * transfer function) using the fork-join framework. This class is intended primarily to be a convenience for parallelizing
 * simple operations without having to wrap them in a larger framework.
 *
 * @author crf <br/>
 *         Started Aug 14, 2010 7:50:01 PM
 */
public class IteratorTask<A,B> extends ForkJoinTask<Iterator<B>> implements Iterable<B> {
    private static final long serialVersionUID = -3011927654892459366L;

    private final Function1<A,B> function;
    private final Iterator<A> iterator;
    private final boolean preserveOrdering;
    private volatile long endElementIndex = 0;
    private volatile Iterator<B> resultIterator = null;
    private volatile Boolean hasNext = null;

    /**
     * Constructor specifying the initial iterator, the transfer function, and whether the iterator's ordering should be
     * preserved. If the iterator's ordering is preserved, then the resultant iterator (over {@code B}) will have each of
     * its elements occurring in the same place as its source in the original iterator. If iteration order is not preserved,
     * then the resultant iterator will just iterate over the results as they become available; this may be preferrable
     * when immediate processing of the results is more important than maintaining some sort of ordering relationships.
     *
     * @param iterator
     *        The source iterator.
     *
     * @param function
     *        The transfer function.
     *
     * @param preserveOrdering
     *        If {@code true}, then the resultant iterator will maintain the same ordering as {@code iterator}.
     */
    public IteratorTask(Iterator<A> iterator, Function1<A,B> function, boolean preserveOrdering) {
        this.function = function;
        this.iterator = iterator;
        this.preserveOrdering = preserveOrdering;
    }

    /**
     * Constructor specifying the initial iterator and the transfer function. The resultant iterator will maintain the same
     * ordering as the source iterator.
     *
     * @param iterator
     *        The source iterator.
     *
     * @param function
     *        The transfer function.
     */
    public IteratorTask(Iterator<A> iterator, Function1<A,B> function) {
        this(iterator,function,true);
    }
    /**
     * Constructor specifying the initial iterable, the transfer function, and whether the iterable's ordering should be
     * preserved. If the iterable's ordering is preserved, then the resultant iterator (over {@code B}) will have each of
     * its elements occurring in the same place as its source in the original iterable. If iteration order is not preserved,
     * then the resultant iterator will just iterate over the results as they become available; this may be preferrable
     * when immediate processing of the results is more important than maintaining some sort of ordering relationships.
     *
     * @param iterable
     *        The source iterable.
     *
     * @param function
     *        The transfer function.
     *
     * @param preserveOrdering
     *        If {@code true}, then the resultant iterator will maintain the same ordering as {@code iterator}.
     */
    public IteratorTask(Iterable<A> iterable, Function1<A,B> function, boolean preserveOrdering) {
        this(iterable.iterator(),function,preserveOrdering);
    }

    /**
     * Constructor specifying the initial iterable and the transfer function. The resultant iterator will maintain the same
     * ordering as the source iterable.
     *
     * @param iterable
     *        The source iterator.
     *
     * @param function
     *        The transfer function.
     */
    public IteratorTask(Iterable<A> iterable, Function1<A,B> function) {
        this(iterable,function,true);
    }

    @Override
    public Iterator<B> getRawResult() {
        return resultIterator;
    }

    @Override
    protected void setRawResult(Iterator<B> resultIterator) {
        this.resultIterator =  resultIterator;
    }

    @Override
    protected boolean exec() {
        final BlockingQueue<IndexedElement<ForkJoinTask<B>>> queue = preserveOrdering ? new OrderEnforcingQueue<ForkJoinTask<B>>() : new LinkedBlockingQueue<IndexedElement<ForkJoinTask<B>>>();
        resultIterator = new IteratorTaskIterator(queue);

        hasNext = iterator.hasNext();
        while(hasNext) { //synchronize continuously to allow has next check in result iterator a chance to see
            synchronized (hasNext) {
                new IteratorSubTask(iterator.next(),endElementIndex++,queue).fork();
                hasNext = iterator.hasNext();
            }
        }
        return true;
    }

    @Override
    public Iterator<B> iterator() {
        return join();
    }

    private class IteratorTaskIterator implements Iterator<B> {
        private final BlockingQueue<IndexedElement<ForkJoinTask<B>>> queue;
        private long currentElementIndex = 0;

        public IteratorTaskIterator(BlockingQueue<IndexedElement<ForkJoinTask<B>>> queue) {
            this.queue = queue;
        }

        @Override
        public boolean hasNext() {
            if (currentElementIndex < endElementIndex)
                return true;
            //down here: we are waiting on source iterator...
            while (hasNext == null) {
                //source iterator hasn't started, give it some time to start
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeInterruptedException(e);
                }
            }
            boolean hasNextValue;
            synchronized (hasNext) {
                hasNextValue = hasNext;
            }
            if (!hasNextValue)
                return currentElementIndex < endElementIndex; //final check to make sure we have finished
            else
                return true;
        }

        @Override
        public B next() {
            if (!hasNext())
                throw new NoSuchElementException();
            try {
                currentElementIndex++;
                return queue.take().getElement().join();
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    private class IteratorSubTask extends ForkJoinTask<B> {
        private static final long serialVersionUID = 1868568545786061326L;

        private final A input;
        private final BlockingQueue<IndexedElement<ForkJoinTask<B>>> queue;
        private B returnValue = null;
        private final long index;

        public IteratorSubTask(A input, long index, BlockingQueue<IndexedElement<ForkJoinTask<B>>> queue) {
            this.input = input;
            this.queue = queue;
            this.index = index;
        }

        @Override
        public B getRawResult() {
            return returnValue;
        }

        @Override
        protected void setRawResult(B b) {
            returnValue = b;
        }

        @Override
        protected boolean exec() {
            queue.add(new IndexedElement<ForkJoinTask<B>>(this,index)); //add first so that join can be correctly administered
            returnValue = function.apply(input);
            return true;
        }
    }

    public static void main(String ... args) {
        final Random r = new Random();
//        System.out.println("Testing parallel iterator task by manually taking the 10,000th power of 100,000 ints...");
//        Function1<Integer,Integer> f = new Function1<Integer,Integer>() {
//            @Override
//            public Integer apply(Integer integer) {
//                for (int i = 0; i < 10000; i++)
//                    integer *= integer;
//                return integer;
//            }
//        };
//        int[] array = new int[100000];
//        for (int i = 0; i < array.length; i++)
//            array[i] = r.nextInt();
//        List<Integer> source = Arrays.asList(ArrayUtil.toIntegerArray(array));
//        int[] result = new int[100000];
//        IteratorTask<Integer,Integer> itTask = new IteratorTask<Integer,Integer>(source.iterator(),f,true);
//
//        ThreadTimer timer = new ThreadTimer(TimeUnit.MILLISECONDS);
//
//        timer.startTimer();
//        int counter = 0;
//        for (int i : source)
//            result[counter++] = f.apply(i);
//        System.out.println("Single threaded: " + timer.resetTimer() + " milliseconds");
//
//        ForkJoinPoolFactory.getForkJoinPool().execute(itTask);
//        counter = 0;
//        for (Integer i : itTask)
//            result[counter++] = i;
//        System.out.println("Fork join tasked: " + timer.endTimer() + " milliseconds");


        Function1<Integer,Integer> f2 = new Function1<Integer,Integer>() {
            @Override
            public Integer apply(Integer integer) {
                System.out.println("starting " + integer);
                try {
                    Thread.sleep(500 + r.nextInt(2500)); //between 0.5 and 3 seconds
                } catch (InterruptedException e) {
                    //ignore
                }
                System.out.println("ending " + integer);
                return integer;
            }
        };
        int[] a = new int[100];
        for (int i = 0; i < a.length; i++)
            a[i] = i+1;
        List<Integer> source2 = Arrays.asList(ArrayUtil.toIntegerArray(a));
        IteratorTask<Integer,Integer> itTask2 = new IteratorTask<>(source2.iterator(),f2,true);
        ForkJoinPoolFactory.getForkJoinPool().execute(itTask2);
        for (int i : itTask2) {

        }
        System.out.println("done");
    }
}
