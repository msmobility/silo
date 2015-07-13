package com.pb.sawdust.calculator;

/**
 * The {@code DegenerateFunction1} provides a single argument function which simply returns its argument.
 *
 * @author crf
 *         Started 9/15/11 7:44 PM
 */
public class DegenerateFunction1<X> implements Function1<X,X> {
    @Override
    public X apply(X x) {
        return x;
    }
}
