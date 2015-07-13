package com.pb.sawdust.util.concurrent;

import com.pb.sawdust.calculator.Function1;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code DnCFunctionalTask} provides an easy-to-use implementation of the {@code DnCRecursiveTask} which transforms
 * an array of inputs into a list of outputs. In other words, it uses a specified function to convert inputs to outputs
 * in a (divide-and-conquer) concurrent fashion. It is assumed that the transformation function is thread-safe; that is,
 * that its calculations are either independent (from one input to another) or synchronized appropriately to allow for safe
 * concurrent processing.
 *
 * @param <F>
 *        The input ("from") type.
 *
 * @param <T>
 *        The output ("to") type.
 *
 * @author crf
 *         Started 5/24/12 5:11 AM
 */
public class DnCFunctionalTask<F,T> extends DnCRecursiveTask<List<T>> {
    private static final long serialVersionUID = 2511709840077408069L;
    /**
     * The default minimum calculation length.
     */
    public static final int DEFAULT_MIN_CALCULATION_LENGTH = 10000;

    private final Function1<F,T> function;
    private final F[] source;
    private final int minCalculationLength;


    /**
     * Constructor for sub-tasks.
     *
     * @param start
     *        The starting point of the task.
     *
     * @param length
     *        The length of the task.
     *
     * @param next
     *        The next action.
     *
     * @param source
     *        The array of inputs.
     *
     * @param function
     *        The transformation function.
     *
     * @param minCalculationLength
     *        The minimum calculation length. If a divide-and-conquer segment is smaller than this amount, then its contents
     *        will be processed (instead of further divided), regardless of the number of idle (available) processing threads.
     */
    protected DnCFunctionalTask(long start, long length, DnCRecursiveTask<List<T>> next, F[] source, Function1<F,T> function, int minCalculationLength) {
        super(start,length,next);
        this.source = source;
        this.function = function;
        this.minCalculationLength = minCalculationLength;
    }

    /**
     * Constructor specifying the source input array, the transformation function, and the minimum calculation length.
     *
     * @param source
     *        The array of inputs.
     *
     * @param function
     *        The transformation function.
     *
     * @param minCalculationLength
     *        The minimum calculation length. If a divide-and-conquer segment is smaller than this amount, then its contents
     *        will be processed (instead of further divided), regardless of the number of idle (available) processing threads.
     */
    public DnCFunctionalTask(F[] source, Function1<F,T> function, int minCalculationLength) {
        super(0,source.length);
        this.source = source;
        this.function = function;
        this.minCalculationLength = minCalculationLength;
    }

    /**
     * Constructor specifying the source input array and the transformation function. The {@link #DEFAULT_MIN_CALCULATION_LENGTH}
     * will be used for the minimum calculation length.
     *
     * @param source
     *        The array of inputs.
     *
     * @param function
     *        The transformation function.
     */
    public DnCFunctionalTask(F[] source, Function1<F,T> function) {
        this(source,function,DEFAULT_MIN_CALCULATION_LENGTH);
    }

    /**
     * Constructor specifying the source input list, the transformation function, and the minimum calculation length. This
     * constructor will (internally) transform the inputs into an array; thus, if efficiency is a concern, the
     * {@link #DnCFunctionalTask(Object[],com.pb.sawdust.calculator.Function1,int)} constructor should be
     * preferred to this one.
     *
     * @param source
     *        The list of inputs.
     *
     * @param function
     *        The transformation function.
     *
     * @param minCalculationLength
     *        The minimum calculation length. If a divide-and-conquer segment is smaller than this amount, then its contents
     *        will be processed (instead of further divided), regardless of the number of idle (available) processing threads.
     */
    @SuppressWarnings("unchecked") //ok because the Object array will only be used internally
    public DnCFunctionalTask(Collection<F> source, Function1<F,T> function, int minCalculationLength) {
        this(source.toArray((F[]) new Object[source.size()]),function,minCalculationLength);
    }

    /**
     * Constructor specifying the source input list and the transformation function. The {@link #DEFAULT_MIN_CALCULATION_LENGTH}
     * will be used for the minimum calculation length. This constructor will (internally) transform the inputs into an array;
     * thus, if efficiency is a concern, the {@link #DnCFunctionalTask(Object[],com.pb.sawdust.calculator.Function1)} constructor
     * should be preferred to this one.
     *
     * @param source
     *        The list of inputs.
     *
     * @param function
     *        The transformation function.
     */
    public DnCFunctionalTask(Collection<F> source, Function1<F,T> function) {
        this(source,function,DEFAULT_MIN_CALCULATION_LENGTH);
    }

    @Override
    protected List<T> computeTask(long start, long length) {
        int end = (int) (start + length);
        List<T> result = new LinkedList<>();
        for (int i = (int) start; i < end; i++)
            result.add(function.apply(source[i]));
        return result;
    }

    @Override
    protected DnCRecursiveTask<List<T>> getNextTask(long start, long length, DnCRecursiveTask<List<T>> next) {
        return new DnCFunctionalTask<F,T>(start,length,next,source,function,minCalculationLength);
    }

    @Override
    protected boolean continueDividing(long length) {
        return length > 1000 && getSurplusQueuedTaskCount() < 3;
    }

    @Override
    protected List<T> joinResults(List<T> result1, List<T> result2) {
        //for some reason some nulls were leaking in occasionally, so I'll just be proactive against them
        if (result1 == null)
            return result2;
        if (result2 != null)
            result1.addAll(result2);
        return result1;
    }
}
