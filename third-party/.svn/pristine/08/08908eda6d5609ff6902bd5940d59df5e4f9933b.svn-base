package com.pb.sawdust.util.concurrent;

import com.pb.sawdust.calculator.Function1;

import java.util.Iterator;

/**
 * The {@code IteratorAction} class provides an {@code IteratorTask} for transfer functions which do not return values.
 * The resultant iterator is used to ensure that all tasks have been completed; other than that, it is not used (it holds
 * no values/results).
 *
 * @author crf <br/>
 *         Started Aug 20, 2010 12:53:41 PM
 */
public class IteratorAction<A> extends IteratorTask<A,Void> {
    private static final long serialVersionUID = 2513569434983801137L;

    /**
     * Constructor specifying the initial iterator and the function.
     *
     * @param iterator
     *        The source iterator.
     *
     * @param function
     *        The function.
     */
    public IteratorAction(Iterator<A> iterator, Function1<A,Void> function) {
        super(iterator,function,false);
    }

    /**
     * Constructor specifying the initial iterable and the function.
     *
     * @param iterable
     *        The source iterable.
     *
     * @param function
     *        The function.
     */
    public IteratorAction(Iterable<A> iterable, Function1<A,Void> function) {
        super(iterable,function,false);
    }

    /**
     * Convenience method to wait for all of the tasks submitted by this task (to a {@code ForkJoinPool}) to complete.
     */
    public void waitForCompletion() {
        //loop to wait for finish
        for (Void v : this) {
            //do nothing - the iterator blocks on the results
        }
    }
}
