package com.pb.sawdust.calculator;

/**
 * The {@code Function1} class represents a one-argument function.
 *
 * @param <X>
 *        The type of the function's argument.
 *
 * @param <Y>
 *        The type that the function returns.
 *
 * @author crf <br/>
 *         Started: Jun 12, 2009 11:50:06 AM
 */
public interface Function1<X,Y> {

    /**
     * Apply the function.
     *
     * @param x
     *        The argument to the function.
     *
     * @return the result of applying the function to {@code x}.
     */
    Y apply(X x);
}
