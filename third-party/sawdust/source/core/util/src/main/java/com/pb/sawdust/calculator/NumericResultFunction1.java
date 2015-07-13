package com.pb.sawdust.calculator;

/**
 * The {@code NumericResultFunction1} specifies a one-argument function which returns a numeric value as its result.
 *
 * @author crf
 *         Started 1/26/12 11:48 AM
 */
public interface NumericResultFunction1<T>  extends Function1<T,Number> {
    /**
     * Apply the function to get a {@code byte} result.
     *
     * @param t
     *        The function argument.
     *
     * @return the result of the function, applied to {@code t}.
     */
    byte applyByte(T t);

    /**
     * Apply the function to get a {@code short} result.
     *
     * @param t
     *        The function argument.
     *
     * @return the result of the function, applied to {@code t}.
     */
    short applyShort(T t);

    /**
     * Apply the function to get an {@code int} result.
     *
     * @param t
     *        The function argument.
     *
     * @return the result of the function, applied to {@code t}.
     */
    int applyInt(T t);

    /**
     * Apply the function to get a {@code long} result.
     *
     * @param t
     *        The function argument.
     *
     * @return the result of the function, applied to {@code t}.
     */
    long applyLong(T t);

    /**
     * Apply the function to get a {@code float} result.
     *
     * @param t
     *        The function argument.
     *
     * @return the result of the function, applied to {@code t}.
     */
    float applyFloat(T t);

    /**
     * Apply the function to get a {@code double} result.
     *
     * @param t
     *        The function argument.
     *
     * @return the result of the function, applied to {@code t}.
     */
    double applyDouble(T t);
}
